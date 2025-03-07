package org.xchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.xchange.java.model.Exchange;
import org.xchange.java.model.User;
import org.xchange.java.repository.ExchangeRepository;
import org.xchange.java.repository.UserRepository;
import org.xchange.java.service.ExchangeService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExchangeServiceTest {

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExchangeService exchangeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllExchanges() {
        Exchange exchange1 = new Exchange();
        Exchange exchange2 = new Exchange();
        when(exchangeRepository.findAll()).thenReturn(Arrays.asList(exchange1, exchange2));

        List<Exchange> result = exchangeService.getAllExchanges();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(exchangeRepository, times(1)).findAll();
    }

    @Test
    public void testGetExchangeById_Found() {
        Exchange exchange = new Exchange();
        exchange.setId(1L);
        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(exchange));

        Optional<Exchange> result = exchangeService.getExchangeById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(exchangeRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetExchangeById_NotFound() {
        when(exchangeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Exchange> result = exchangeService.getExchangeById(999L);

        assertFalse(result.isPresent());
        verify(exchangeRepository, times(1)).findById(999L);
    }

    @Test
    public void testCreateExchange_Success() {
        // Mocks
        User user = new User();
        user.setId(100L);
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));

        Exchange savedExchange = new Exchange();
        savedExchange.setId(10L);
        when(exchangeRepository.save(any(Exchange.class))).thenReturn(savedExchange);

        // Appel
        Exchange result = exchangeService.createExchange(11L, 22L, "PENDING", 100L);

        // Vérifications
        assertNotNull(result);
        assertEquals(10L, result.getId());  // la valeur mockée
        verify(userRepository, times(1)).findById(100L);
        verify(exchangeRepository, times(1)).save(any(Exchange.class));
    }

    @Test
    public void testCreateExchange_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                exchangeService.createExchange(11L, 22L, "PENDING", 999L)
        );
        verify(userRepository, times(1)).findById(999L);
        verify(exchangeRepository, never()).save(any(Exchange.class));
    }

    @Test
    public void testUpdateExchange_Success() {
        // Exchange existant
        Exchange existingExchange = new Exchange();
        existingExchange.setId(1L);
        existingExchange.setStatus("OLD_STATUS");
        when(exchangeRepository.findById(1L)).thenReturn(Optional.of(existingExchange));

        Exchange updatedExchangeMock = new Exchange();
        updatedExchangeMock.setId(1L);
        updatedExchangeMock.setStatus("NEW_STATUS");
        when(exchangeRepository.save(any(Exchange.class))).thenReturn(updatedExchangeMock);

        // Appel
        Exchange result = exchangeService.updateExchange(1L, "NEW_STATUS");

        assertNotNull(result);
        assertEquals("NEW_STATUS", result.getStatus());
        verify(exchangeRepository, times(1)).findById(1L);
        verify(exchangeRepository, times(1)).save(any(Exchange.class));
    }

    @Test
    public void testUpdateExchange_NotFound() {
        when(exchangeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                exchangeService.updateExchange(999L, "ANY_STATUS")
        );
        verify(exchangeRepository, times(1)).findById(999L);
        verify(exchangeRepository, never()).save(any(Exchange.class));
    }

    @Test
    public void testDeleteExchange() {
        exchangeService.deleteExchange(5L);
        verify(exchangeRepository, times(1)).deleteById(5L);
    }
}
