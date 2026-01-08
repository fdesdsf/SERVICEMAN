package co.ke.serviceman.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

@Entity
@Table(name = "customers")
public class Customer {

    public enum Status {
        IN_QUEUE,       // Waiting in queue
        IN_SERVICE,     // Currently being processed
        FORWARDED,      // Completed service, forwarded to next point
        SERVED;         // Completed all services, exited system
    }

    public enum ServicePoint {
        GATE_ATTENDANT, RECEPTIONIST, SALES_ORDER_DESK, INVOICING_DESK, STORE_CLERK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "national_id", nullable = false)
    private String nationalId;

    @Column(nullable = false)
    private String name;

    @Column(name = "customer_type", nullable = false)
    private String customerType;

    @Column(name = "service_requested", nullable = false)
    private String serviceRequested;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.IN_QUEUE;

    @Enumerated(EnumType.STRING)
    @Column(name = "`current_role`")
    private ServicePoint currentRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "`next_service_point`")
    private ServicePoint nextServicePoint;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    // ✅ ADDED: Field to store total time in system (in seconds)
    @Column(name = "total_time_in_system")
    private Long totalTimeInSystem;

    // ✅ ADDED: Field to track when customer exited the system
    @Column(name = "exit_time")
    private Timestamp exitTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServiceRecord> serviceRecords = new ArrayList<>();

    // Getters and Setters
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

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public ServicePoint getCurrentRole() { return currentRole; }
    public void setCurrentRole(ServicePoint currentRole) { this.currentRole = currentRole; }

    public ServicePoint getNextServicePoint() { return nextServicePoint; }
    public void setNextServicePoint(ServicePoint nextServicePoint) { this.nextServicePoint = nextServicePoint; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // ✅ ADDED: Getter and setter for totalTimeInSystem
    public Long getTotalTimeInSystem() { return totalTimeInSystem; }
    public void setTotalTimeInSystem(Long totalTimeInSystem) { this.totalTimeInSystem = totalTimeInSystem; }

    // ✅ ADDED: Getter and setter for exitTime
    public Timestamp getExitTime() { return exitTime; }
    public void setExitTime(Timestamp exitTime) { this.exitTime = exitTime; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public List<ServiceRecord> getServiceRecords() { return serviceRecords; }
    public void setServiceRecords(List<ServiceRecord> serviceRecords) { this.serviceRecords = serviceRecords; }

    // Helper method to add a service record and link it to this customer
    public void addServiceRecord(ServiceRecord record) {
        serviceRecords.add(record);
        record.setCustomer(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}