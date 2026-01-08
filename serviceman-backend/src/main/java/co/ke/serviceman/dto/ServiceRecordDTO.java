package co.ke.serviceman.dto;

import co.ke.serviceman.model.ServiceRecord;
import java.sql.Timestamp;

public class ServiceRecordDTO {
    private Long id;
    private Timestamp startTime;
    private Timestamp completionTime;
    private Long timeSpent;
    private String servedBy;
    private String notes;
    private ServiceRecord.Role arrivedFrom;
    private CustomerDTO customer;

    // Default constructor
    public ServiceRecordDTO() {
    }

    // Constructor from Entity
    public ServiceRecordDTO(ServiceRecord serviceRecord) {
        this.id = serviceRecord.getId();
        this.startTime = serviceRecord.getStartTime();
        this.completionTime = serviceRecord.getCompletionTime();
        this.timeSpent = serviceRecord.getTimeSpent();
        this.servedBy = serviceRecord.getServedBy();
        this.notes = serviceRecord.getNotes();
        this.arrivedFrom = serviceRecord.getArrivedFrom();
        this.customer = serviceRecord.getCustomer() != null ? new CustomerDTO(serviceRecord.getCustomer()) : null;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getCompletionTime() { return completionTime; }
    public void setCompletionTime(Timestamp completionTime) { this.completionTime = completionTime; }

    public Long getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Long timeSpent) { this.timeSpent = timeSpent; }

    public String getServedBy() { return servedBy; }
    public void setServedBy(String servedBy) { this.servedBy = servedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public ServiceRecord.Role getArrivedFrom() { return arrivedFrom; }
    public void setArrivedFrom(ServiceRecord.Role arrivedFrom) { this.arrivedFrom = arrivedFrom; }

    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }
}