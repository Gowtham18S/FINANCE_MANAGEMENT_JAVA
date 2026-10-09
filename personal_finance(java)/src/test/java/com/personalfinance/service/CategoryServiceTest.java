package com.personalfinance.service;

import com.personalfinance.dao.CategoryDAO;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryDAO categoryDAO;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryDAO);
    }

    @Test
    @DisplayName("Should successfully add a valid category")
    void testAddValidCategory() throws SQLException {
        when(categoryDAO.findByNameAndType("Consulting", CategoryType.INCOME))
                .thenReturn(Optional.empty());
        when(categoryDAO.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category c = invocation.getArgument(0);
                    c.setId(10L);
                    return c;
                });

        Category result = categoryService.addCategory("Consulting", CategoryType.INCOME, "Consulting work");
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Consulting", result.getName());
        assertEquals(CategoryType.INCOME, result.getType());
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate category name for same type")
    void testAddDuplicateCategoryName() throws SQLException {
        Category existing = new Category(1L, "Salary", CategoryType.INCOME, "Job salary");
        when(categoryDAO.findByNameAndType("Salary", CategoryType.INCOME))
                .thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                categoryService.addCategory("Salary", CategoryType.INCOME, "Duplicate test"));

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Should throw exception when adding category with empty name")
    void testAddCategoryWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () ->
                categoryService.addCategory("  ", CategoryType.EXPENSE, "Empty name"));
    }

    @Test
    @DisplayName("Should prevent deleting category if in use")
    void testDeleteCategoryInUse() throws SQLException {
        Category cat = new Category(5L, "Food", CategoryType.EXPENSE, "Food expenses");
        when(categoryDAO.findById(5L)).thenReturn(Optional.of(cat));
        when(categoryDAO.isCategoryInUse(5L)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                categoryService.deleteCategory(5L));

        assertTrue(ex.getMessage().contains("referenced by existing income, expense, or budget records"));
        verify(categoryDAO, never()).delete(5L);
    }
}
