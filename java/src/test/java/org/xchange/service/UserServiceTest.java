package org.xchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.xchange.java.model.User;
import org.xchange.java.repository.UserRepository;
import org.xchange.java.service.UserService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsers() {
        User u1 = new User();
        User u2 = new User();
        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUserById_Found() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("password");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.createUser(user);

        assertNotNull(savedUser);
        assertEquals("newUser", savedUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser() {
        User updatedUserMock = new User();
        updatedUserMock.setId(10L);
        updatedUserMock.setUsername("UpdatedUser");

        when(userRepository.save(any(User.class))).thenReturn(updatedUserMock);

        User userToUpdate = new User();
        userToUpdate.setUsername("OldUsername");
        userToUpdate.setPassword("OldPassword");

        User result = userService.updateUser(10L, userToUpdate);

        assertEquals(10L, result.getId());
        assertEquals("UpdatedUser", result.getUsername());
        // Vérifie qu'on a bien fait setId(10L) avant de save
        verify(userRepository, times(1)).save(argThat(u -> u.getId() == 10L));
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(20L);
        verify(userRepository, times(1)).deleteById(20L);
    }

    @Test
    public void testGetUserByEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    public void testGetUserByEmailAndPassword() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("mypassword");
        when(userRepository.findByEmailAndPassword("test@example.com", "mypassword"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmailAndPassword("test@example.com", "mypassword");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("mypassword", result.get().getPassword());
        verify(userRepository, times(1)).findByEmailAndPassword("test@example.com", "mypassword");
    }

    @Test
    public void testLogin_Success() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("secret");
        when(userRepository.findByUsername("john")).thenReturn(user);

        User loggedIn = userService.login("john", "secret");

        assertNotNull(loggedIn);
        assertEquals("john", loggedIn.getUsername());
        assertEquals(user, userService.getCurrentUser());  // check currentUser
        verify(userRepository, times(1)).findByUsername("john");
    }

    @Test
    public void testLogin_Failure_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        User loggedIn = userService.login("unknown", "pass");

        assertNull(loggedIn);
        assertNull(userService.getCurrentUser());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    public void testGetCurrentUser() {
        // Par défaut, c'est null
        assertNull(userService.getCurrentUser());

        // On simule un login
        User user = new User();
        user.setUsername("someone");
        user.setPassword("pass");
        when(userRepository.findByUsername("someone")).thenReturn(user);

        User loggedIn = userService.login("someone", "pass");
        assertNotNull(loggedIn);
        assertEquals("someone", userService.getCurrentUser().getUsername());
    }
}
