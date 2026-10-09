package com.personalfinance.service;

import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling business logic for category management.
 */
public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public Category addCategory(String name, CategoryType type, String description) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Category type must be specified (INCOME or EXPENSE).");
        }

        String cleanName = name.trim();
        Optional<Category> existing = categoryDAO.findByNameAndType(cleanName, type);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Category '" + cleanName + "' already exists for type " + type + ".");
        }

        Category category = new Category(cleanName, type, description != null ? description.trim() : "");
        return categoryDAO.save(category);
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    public List<Category> getCategoriesByType(CategoryType type) throws SQLException {
        if (type == null) {
            throw new IllegalArgumentException("Category type cannot be null.");
        }
        return categoryDAO.findByType(type);
    }

    public Category getCategoryById(long id) throws SQLException {
        return categoryDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
    }

    public boolean updateCategory(long id, String newName, CategoryType newType, String newDescription) throws SQLException {
        Category existing = getCategoryById(id);

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty.");
        }
        if (newType == null) {
            throw new IllegalArgumentException("Category type cannot be null.");
        }

        String cleanName = newName.trim();
        // Check duplicate name if name or type changed
        if (!existing.getName().equalsIgnoreCase(cleanName) || existing.getType() != newType) {
            Optional<Category> duplicate = categoryDAO.findByNameAndType(cleanName, newType);
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("Category '" + cleanName + "' already exists for type " + newType + ".");
            }
        }

        existing.setName(cleanName);
        existing.setType(newType);
        existing.setDescription(newDescription != null ? newDescription.trim() : "");

        return categoryDAO.update(existing);
    }

    public boolean deleteCategory(long id) throws SQLException {
        Category existing = getCategoryById(id);
        if (categoryDAO.isCategoryInUse(id)) {
            throw new IllegalStateException("Cannot delete category '" + existing.getName() + "' (ID: " + id + ") because it is referenced by existing income, expense, or budget records.");
        }
        return categoryDAO.delete(id);
    }
}
