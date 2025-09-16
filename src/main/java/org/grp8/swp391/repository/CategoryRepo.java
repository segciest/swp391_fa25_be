package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long> {
    Category findByCategoryName(String categoryName);
    Category save(Category category);
    Category deleteByCategoryId(Long categoryId);
    Category create(Category category);
    Category updateByCategoryId(Category category, String id);
    Category findByCategoryId(Long categoryId);git add .

}
