package org.xchange.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xchange.java.model.Exchange;
import org.xchange.java.model.ExchangeObject;
import org.xchange.java.model.User;
import org.xchange.java.repository.ExchangeRepository;
import org.xchange.java.repository.ObjectRepository;
import org.xchange.java.service.ExchangeService;
import org.xchange.java.repository.UserRepository;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer les échanges.
 * 
 * Ce contrôleur fournit des end point
 * pour effectuer des opérations CRUD sur les échanges,
 * ainsi que pour accepter ou refuser des demandes d'échange.
 * 
 * Points de terminaison disponibles :
 * - GET /api/exchanges : Récupère tous les échanges.
 * - POST /api/exchanges/{exchangeId}/accept : Accepte une demande d'échange.
 * - POST /api/exchanges/{exchangeId}/reject : Refuse une demande d'échange.
 * - GET /api/exchanges/received : Récupère les demandes d'échange reçues par un
 * utilisateur.
 * - GET /api/exchanges/{id} : Récupère un échange par son identifiant.
 * - GET /api/exchanges/user/{userId} : Récupère les échanges d'un utilisateur
 * par son identifiant.
 * - POST /api/exchanges : Crée un nouvel échange.
 * - PUT /api/exchanges/{id} : Met à jour un échange existant.
 * - DELETE /api/exchanges/{id} : Supprime un échange par son identifiant.
 * 
 * Les services et référentiels utilisés par ce contrôleur sont injectés via
 * l'annotation @Autowired.
 * 
 * @see ExchangeService
 * @see ObjectRepository
 * @see ExchangeRepository
 * @see UserRepository
 */
@RestController
@RequestMapping("/api/exchanges")
public class ExchangeController {

    /**
     * Service pour gérer les échanges.
     */
    @Autowired
    private ExchangeService exchangeService;

    /**
     * Référentiel pour gérer les objets.
     */
    @Autowired
    private ObjectRepository objectRepository;

    /**
     * Référentiel pour gérer les échanges.
     */
    @Autowired
    private ExchangeRepository exchangeRepository;

    /**
     * Référentiel pour gérer les utilisateurs.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Récupère tous les échanges.
     * 
     * @return la liste de tous les échanges.
     */
    @GetMapping
    public List<Exchange> getAllExchanges() {
        return exchangeService.getAllExchanges();
    }

    /**
     * Accepte une demande d'échange.
     * 
     * @param exchangeId l'identifiant de l'échange à accepter.
     * @return une réponse indiquant le succès ou l'échec de l'opération.
     */
    @PostMapping("/{exchangeId}/accept")
    public ResponseEntity<?> acceptExchange(@PathVariable Long exchangeId) {
        try {
            exchangeService.acceptExchange(exchangeId);
            return ResponseEntity.ok("Exchange accepted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Refuser une demande d'échange
    @PostMapping("/{exchangeId}/reject")
    public ResponseEntity<?> rejectExchange(@PathVariable Long exchangeId) {
        try {
            exchangeService.rejectExchange(exchangeId);
            return ResponseEntity.ok("Exchange rejected successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/received")
    public ResponseEntity<List<Exchange>> getReceivedExchangeRequests(@RequestParam Long userId) {
        List<Exchange> exchanges = exchangeRepository.findReceivedExchangeRequests(userId);
        if (exchanges.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(exchanges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exchange> getExchangeById(@PathVariable Long id) {
        Optional<Exchange> exchange = exchangeRepository.findById(id);
        return exchange.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Exchange>> getExchangesByUserId(@PathVariable Long userId) {
        List<Exchange> exchanges = exchangeRepository.findByUserId(userId);
        if (exchanges.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exchanges);
    }

    @PostMapping
    public Exchange createExchange(@RequestBody Exchange exchange) {
        if (exchange.getProposedObject() == null || exchange.getProposedObject().getId() == null) {
            throw new IllegalArgumentException("Proposed object ID must not be null");
        }
        if (exchange.getRequestedObject() == null || exchange.getRequestedObject().getId() == null) {
            throw new IllegalArgumentException("Requested object ID must not be null");
        }

        ExchangeObject proposedObject = objectRepository.findById(exchange.getProposedObject().getId())
                .orElseThrow(() -> new RuntimeException("Proposed object not found"));
        ExchangeObject requestedObject = objectRepository.findById(exchange.getRequestedObject().getId())
                .orElseThrow(() -> new RuntimeException("Requested object not found"));

        exchange.setProposedObject(proposedObject);
        exchange.setRequestedObject(requestedObject);
        return exchangeRepository.save(exchange);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exchange> updateExchange(@PathVariable Long id, @RequestBody Exchange exchange) {
        Exchange updatedExchange = exchangeService.updateExchange(id, exchange.getStatus());
        return ResponseEntity.ok(updatedExchange);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchange(@PathVariable Long id) {
        exchangeService.deleteExchange(id);
        return ResponseEntity.noContent().build();
    }
}
