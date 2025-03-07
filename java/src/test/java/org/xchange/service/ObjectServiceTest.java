package org.xchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.xchange.java.model.ExchangeObject;
import org.xchange.java.repository.ObjectRepository;
import org.xchange.java.service.ObjectService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ObjectServiceTest {

    @Mock
    private ObjectRepository objectRepository;

    @InjectMocks
    private ObjectService objectService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllObjects() {
        ExchangeObject o1 = new ExchangeObject();
        ExchangeObject o2 = new ExchangeObject();
        when(objectRepository.findAll()).thenReturn(Arrays.asList(o1, o2));

        List<ExchangeObject> result = objectService.getAllObjects();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(objectRepository, times(1)).findAll();
    }

    @Test
    public void testGetObjectById_Found() {
        ExchangeObject o = new ExchangeObject();
        o.setId(123L);
        when(objectRepository.findById(123L)).thenReturn(Optional.of(o));

        Optional<ExchangeObject> result = objectService.getObjectById(123L);

        assertTrue(result.isPresent());
        assertEquals(123L, result.get().getId());
        verify(objectRepository, times(1)).findById(123L);
    }

    @Test
    public void testGetObjectById_NotFound() {
        when(objectRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<ExchangeObject> result = objectService.getObjectById(999L);

        assertFalse(result.isPresent());
        verify(objectRepository, times(1)).findById(999L);
    }

    @Test
    public void testCreateObject() {
        ExchangeObject obj = new ExchangeObject();
        obj.setName("MyObject");
        when(objectRepository.save(any(ExchangeObject.class))).thenReturn(obj);

        ExchangeObject created = objectService.createObject(obj);

        assertNotNull(created);
        assertEquals("MyObject", created.getName());
        verify(objectRepository, times(1)).save(obj);
    }

    @Test
    public void testUpdateObject() {
        ExchangeObject objToUpdate = new ExchangeObject();
        objToUpdate.setName("OldName");

        ExchangeObject updatedMock = new ExchangeObject();
        updatedMock.setId(10L);
        updatedMock.setName("NewName");

        when(objectRepository.save(any(ExchangeObject.class))).thenReturn(updatedMock);

        ExchangeObject result = objectService.updateObject(10L, objToUpdate);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("NewName", result.getName());
        // Vérifie qu'on lui a bien injecté l'id
        verify(objectRepository, times(1)).save(argThat(o -> o.getId() == 10L));
    }

    @Test
    public void testDeleteObject() {
        objectService.deleteObject(5L);
        verify(objectRepository, times(1)).deleteById(5L);
    }

    @Test
    public void testGetObjectsByUser() {
        ExchangeObject obj1 = new ExchangeObject();
        obj1.setId(101L);
        when(objectRepository.findByUserId(10L)).thenReturn(Arrays.asList(obj1));

        List<ExchangeObject> result = objectService.getObjectsByUser(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getId());
        verify(objectRepository, times(1)).findByUserId(10L);
    }

    @Test
    public void testGetObjectsByCategory() {
        ExchangeObject obj1 = new ExchangeObject();
        ExchangeObject obj2 = new ExchangeObject();
        when(objectRepository.findByCategoryId(20L)).thenReturn(Arrays.asList(obj1, obj2));

        List<Object> result = objectService.getObjectsByCategory(20L);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(objectRepository, times(1)).findByCategoryId(20L);
    }
}
