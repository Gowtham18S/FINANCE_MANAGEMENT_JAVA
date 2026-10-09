package com.personalfinance.model;

import java.util.Objects;

/**
 * Model class representing a financial category.
 */
public class Category {
    private Long id;
    private String name;
    private CategoryType type;
    private String description;

    public Category() {}

    public Category(String name, CategoryType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Category(Long id, String name, CategoryType type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) &&
                Objects.equals(name, category.name) &&
                type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }

    @Override
    public String toString() {
        return String.format("ID: %-5d | Name: %-20s | Type: %-8s | Description: %s",
                id, name, type, (description != null ? description : "N/A"));
    }
}
