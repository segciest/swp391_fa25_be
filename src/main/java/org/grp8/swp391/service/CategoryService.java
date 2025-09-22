package org.grp8.swp391.service;

import jakarta.persistence.EntityNotFoundException;
import org.grp8.swp391.entity.Category;
import org.grp8.swp391.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepo categoryRepo;

    public Category create(Category category) {
        return categoryRepo.save(category);
    }

    public Category update(Long id,Category category) {
        Category check = categoryRepo.findByCategoryId(id);
        if(check == null) {
            throw new EntityNotFoundException("Category not found");
        }
        return categoryRepo.save(category);

    }

    public Category findByCategoryId(Long categoryId) {
        return categoryRepo.findByCategoryId(categoryId);
    }

    public Category findByCategoryName(String categoryName) {
        return categoryRepo.findByCategoryName(categoryName);
    }

    public void deleteByCategoryId(Long categoryId) {
        categoryRepo.deleteByCategoryId(categoryId);
    }


}
