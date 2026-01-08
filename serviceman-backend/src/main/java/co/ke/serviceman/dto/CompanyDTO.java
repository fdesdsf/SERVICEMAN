package co.ke.serviceman.dto;

import co.ke.serviceman.model.Company;
import java.sql.Timestamp;

public class CompanyDTO {
    private Long id;
    private String name;
    private Timestamp createdAt;

    // Default constructor
    public CompanyDTO() {
    }

    // Constructor from Entity
    public CompanyDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.createdAt = company.getCreatedAt();
    }

    // Getters and setters
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}