/**
 * Contrôleur REST pour gérer les opérations CRUD sur les catégories.
 * 
 * Ce contrôleur fournit des endpoint pour :
 * - Récupérer toutes les catégories
 * - Récupérer une catégorie par son identifiant
 * - Créer une nouvelle catégorie
 * - Mettre à jour une catégorie existante
 * - Supprimer une catégorie
 * - Récupérer les objets associés à une catégorie
 * 
 * Les services CategoryService et ObjectService sont injectés pour gérer la logique métier.
 * 
 * @author VotreNom
 */
package org.xchange.java.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xchange.java.model.Category;
import org.xchange.java.service.CategoryService;
import org.xchange.java.service.ObjectService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ObjectService objectService;

    @Autowired
    public CategoryController(CategoryService categoryService, ObjectService objectService) {
        this.categoryService = categoryService;
        this.objectService = objectService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updatedCategory = categoryService.updateCategory(id, category);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}/objects")
    public ResponseEntity<List<Object>> getObjectsByCategory(@PathVariable Long categoryId) {
        List<Object> objects = objectService.getObjectsByCategory(categoryId);

        if (objects.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(objects);
    }

}