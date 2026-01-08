package co.ke.serviceman.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "module_config")
public class ModuleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "gate_attendant_enabled", nullable = false)
    private boolean gateAttendantEnabled; // REMOVED = true

    @Column(name = "receptionist_enabled", nullable = false)
    private boolean receptionistEnabled; // REMOVED = true

    @Column(name = "sales_order_desk_enabled", nullable = false)
    private boolean salesOrderDeskEnabled; // REMOVED = true

    @Column(name = "invoicing_desk_enabled", nullable = false)
    private boolean invoicingDeskEnabled; // REMOVED = true

    @Column(name = "store_clerk_enabled", nullable = false)
    private boolean storeClerkEnabled; // REMOVED = true

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    // Required by JPA
    public ModuleConfig() {
    }

    // Constructor for convenience - UPDATE THIS TOO!
    public ModuleConfig(Company company, boolean gateAttendantEnabled, boolean receptionistEnabled,
                        boolean salesOrderDeskEnabled, boolean invoicingDeskEnabled, boolean storeClerkEnabled) {
        this.company = company;
        this.gateAttendantEnabled = gateAttendantEnabled;
        this.receptionistEnabled = receptionistEnabled;
        this.salesOrderDeskEnabled = salesOrderDeskEnabled;
        this.invoicingDeskEnabled = invoicingDeskEnabled;
        this.storeClerkEnabled = storeClerkEnabled;
    }

  // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isGateAttendantEnabled() {
        return gateAttendantEnabled;
    }

    public void setGateAttendantEnabled(boolean gateAttendantEnabled) {
        this.gateAttendantEnabled = gateAttendantEnabled;
    }

    public boolean isReceptionistEnabled() {
        return receptionistEnabled;
    }

    public void setReceptionistEnabled(boolean receptionistEnabled) {
        this.receptionistEnabled = receptionistEnabled;
    }

    public boolean isSalesOrderDeskEnabled() {
        return salesOrderDeskEnabled;
    }

    public void setSalesOrderDeskEnabled(boolean salesOrderDeskEnabled) {
        this.salesOrderDeskEnabled = salesOrderDeskEnabled;
    }

    public boolean isInvoicingDeskEnabled() {
        return invoicingDeskEnabled;
    }

    public void setInvoicingDeskEnabled(boolean invoicingDeskEnabled) {
        this.invoicingDeskEnabled = invoicingDeskEnabled;
    }

    public boolean isStoreClerkEnabled() {
        return storeClerkEnabled;
    }

    public void setStoreClerkEnabled(boolean storeClerkEnabled) {
        this.storeClerkEnabled = storeClerkEnabled;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleConfig that = (ModuleConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}