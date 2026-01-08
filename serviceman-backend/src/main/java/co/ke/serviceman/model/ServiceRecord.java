package co.ke.serviceman.model;

import javax.persistence.*;
import java.sql.Timestamp; // Changed from java.time.LocalDateTime

@Entity
@Table(name = "service_records")
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Username of the staff who served (served_by)
    @Column(name = "served_by")
    private String servedBy;

    // Role from which the customer arrived (arrived_from)
    @Enumerated(EnumType.STRING)
    @Column(name = "arrived_from")
    private Role arrivedFrom;

    // Service notes
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Start time of service
    @Column(name = "start_time")
    private Timestamp startTime; // Data type changed to Timestamp

    // Completion time of service
    @Column(name = "completion_time")
    private Timestamp completionTime; // Data type changed to Timestamp

    // Time spent in seconds
    @Column(name = "time_spent")
    private Long timeSpent;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getServedBy() { return servedBy; }
    public void setServedBy(String servedBy) { this.servedBy = servedBy; }

    public Role getArrivedFrom() { return arrivedFrom; }
    public void setArrivedFrom(Role arrivedFrom) { this.arrivedFrom = arrivedFrom; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getStartTime() { return startTime; } // Return type changed to Timestamp
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; } // Parameter type changed to Timestamp

    public Timestamp getCompletionTime() { return completionTime; } // Return type changed to Timestamp
    public void setCompletionTime(Timestamp completionTime) { this.completionTime = completionTime; } // Parameter type changed to Timestamp

    public Long getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Long timeSpent) { this.timeSpent = timeSpent; }

    // Enum for roles
    public enum Role {
        GATE_ATTENDANT,
        RECEPTIONIST,
        SALES_ORDER_DESK,
        INVOICING_DESK,
        STORE_CLERK
    }
}