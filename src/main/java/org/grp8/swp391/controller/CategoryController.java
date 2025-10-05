package org.grp8.swp391.controller;


import jakarta.persistence.EntityNotFoundException;
import org.grp8.swp391.entity.Category;
import org.grp8.swp391.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/name")
    public ResponseEntity<?> findByCategoryName(@RequestParam String name) {
        Category category = categoryService.findByCategoryName(name);
        if(category == null) {
            throw new EntityNotFoundException("Category not found");
        }
        return ResponseEntity.ok().body(category);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findByCategoryId(@PathVariable Long id) {
        Category category = categoryService.findByCategoryId(id);
        if(category == null) {
            throw new EntityNotFoundException("Category not found");
        }
        return ResponseEntity.ok().body(category);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(Category category) {
        return ResponseEntity.ok().body(categoryService.create(category));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,Category category) {
        Category check = categoryService.findByCategoryId(id);
        if(check == null) {
            throw new EntityNotFoundException("Category not found");

        }
        categoryService.update(id, category);
        return ResponseEntity.ok().body(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        Category category = categoryService.findByCategoryId(id);
        if(category == null) {
            throw new EntityNotFoundException("Category not found");

        }
        categoryService.deleteByCategoryId(id);


    }

}
