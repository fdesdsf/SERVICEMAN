package co.ke.serviceman.dto;

import co.ke.serviceman.model.Customer;
import java.sql.Timestamp;

public class CustomerDTO {
    private Long id;
    private String nationalId;
    private String name;
    private String customerType;
    private String serviceRequested;
    private Customer.Status status;
    private Customer.ServicePoint currentRole;
    private Customer.ServicePoint nextServicePoint;
    private Timestamp createdAt;
    private Long totalTimeInSystem; // ✅ ADDED: Total time in system (seconds)
    private Timestamp exitTime;     // ✅ ADDED: When customer exited the system
    private CompanyDTO company; // Use CompanyDTO instead of Company entity

    // Default constructor
    public CustomerDTO() {
    }

    // Constructor from Entity
    public CustomerDTO(Customer customer) {
        this.id = customer.getId();
        this.nationalId = customer.getNationalId();
        this.name = customer.getName();
        this.customerType = customer.getCustomerType();
        this.serviceRequested = customer.getServiceRequested();
        this.status = customer.getStatus();
        this.currentRole = customer.getCurrentRole();
        this.nextServicePoint = customer.getNextServicePoint();
        this.createdAt = customer.getCreatedAt();
        this.totalTimeInSystem = customer.getTotalTimeInSystem(); // ✅ ADDED
        this.exitTime = customer.getExitTime();                   // ✅ ADDED
        this.company = customer.getCompany() != null ? new CompanyDTO(customer.getCompany()) : null;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }

    public String getServiceRequested() { return serviceRequested; }
    public void setServiceRequested(String serviceRequested) { this.serviceRequested = serviceRequested; }

    public Customer.Status getStatus() { return status; }
    public void setStatus(Customer.Status status) { this.status = status; }

    public Customer.ServicePoint getCurrentRole() { return currentRole; }
    public void setCurrentRole(Customer.ServicePoint currentRole) { this.currentRole = currentRole; }

    public Customer.ServicePoint getNextServicePoint() { return nextServicePoint; }
    public void setNextServicePoint(Customer.ServicePoint nextServicePoint) { this.nextServicePoint = nextServicePoint; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // ✅ ADDED: Getter and setter for totalTimeInSystem
    public Long getTotalTimeInSystem() { return totalTimeInSystem; }
    public void setTotalTimeInSystem(Long totalTimeInSystem) { this.totalTimeInSystem = totalTimeInSystem; }

    // ✅ ADDED: Getter and setter for exitTime
    public Timestamp getExitTime() { return exitTime; }
    public void setExitTime(Timestamp exitTime) { this.exitTime = exitTime; }

    public CompanyDTO getCompany() { return company; }
    public void setCompany(CompanyDTO company) { this.company = company; }
}