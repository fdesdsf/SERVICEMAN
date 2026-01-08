package co.ke.serviceman.service;

import co.ke.serviceman.dao.ServiceRecordDAO;
import co.ke.serviceman.model.CustomerStatus;
import co.ke.serviceman.model.ServiceRecord;
import co.ke.serviceman.dto.ServiceRecordDTO;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service layer for the ServiceRecord entity.
 * Handles business logic and transaction management for ServiceRecord operations.
 */
@Stateless
public class ServiceRecordService {

    private static final Logger logger = Logger.getLogger(ServiceRecordService.class.getName());

    @Inject
    private ServiceRecordDAO recordDAO;

    /**
     * Finds a ServiceRecord entity by its ID.
     * @param id The ID of the entity to find.
     * @return The found ServiceRecord entity.
     */
    public ServiceRecord findById(Long id) {
        try {
            logger.log(Level.INFO, "Finding service record by ID: {0}", id);
            ServiceRecord record = recordDAO.findById(id);
            if (record == null) {
                logger.log(Level.WARNING, "Service record not found with ID: {0}", id);
                throw new RuntimeException("Service record not found with ID: " + id);
            }
            return record;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service record by ID: " + id, e);
            throw new RuntimeException("Failed to retrieve service record with ID: " + id, e);
        }
    }

    // DTO methods for API responses
    public ServiceRecordDTO findByIdDTO(Long id) {
        try {
            logger.log(Level.INFO, "Finding service record by ID as DTO: {0}", id);
            ServiceRecord record = recordDAO.findById(id);
            if (record == null) {
                logger.log(Level.WARNING, "Service record not found with ID: {0}", id);
                throw new RuntimeException("Service record not found with ID: " + id);
            }
            return new ServiceRecordDTO(record);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service record by ID as DTO: " + id, e);
            throw new RuntimeException("Failed to retrieve service record with ID: " + id, e);
        }
    }

    public List<ServiceRecordDTO> findAllDTO() {
        try {
            logger.info("Finding all service records as DTOs");
            List<ServiceRecord> records = recordDAO.findAll();
            return records.stream()
                    .map(ServiceRecordDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all service records as DTOs", e);
            throw new RuntimeException("Failed to retrieve service records: " + e.getMessage(), e);
        }
    }

    public List<ServiceRecordDTO> findByCustomerIdDTO(Long customerId) {
        try {
            logger.log(Level.INFO, "Finding service records by customer ID as DTOs: {0}", customerId);
            List<ServiceRecord> records = recordDAO.findByCustomerId(customerId);
            return records.stream()
                    .map(ServiceRecordDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service records by customer ID as DTOs: " + customerId, e);
            throw new RuntimeException("Failed to retrieve service records by customer ID: " + e.getMessage(), e);
        }
    }

    public List<ServiceRecordDTO> findByStatusDTO(CustomerStatus status) {
        try {
            logger.log(Level.INFO, "Finding service records by status as DTOs: {0}", status);
            List<ServiceRecord> records = recordDAO.findByStatus(status);
            return records.stream()
                    .map(ServiceRecordDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service records by status as DTOs: " + status, e);
            throw new RuntimeException("Failed to retrieve service records by status: " + e.getMessage(), e);
        }
    }

    // ✅ UPDATED: Admin Dashboard methods with company filtering

    public List<ServiceRecordDTO> findByDateDTO(String dateStr) {
        try {
            logger.info("Finding service records for date: " + dateStr);
            List<ServiceRecord> records = recordDAO.findByDate(dateStr);
            return records.stream()
                    .map(ServiceRecordDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service records for date: " + dateStr, e);
            throw new RuntimeException("Failed to retrieve service records for date: " + dateStr, e);
        }
    }

    // ✅ ADDED: Company-filtered method for date
    public List<ServiceRecordDTO> findByDateAndCompanyDTO(String dateStr, Long companyId) {
        try {
            logger.log(Level.INFO, "Finding service records for date: {0} and company: {1}", 
                      new Object[]{dateStr, companyId});
            List<ServiceRecord> records = recordDAO.findByDateAndCompany(dateStr, companyId);
            return records.stream()
                    .map(ServiceRecordDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service records for date and company: " + dateStr, e);
            throw new RuntimeException("Failed to retrieve service records for date and company: " + dateStr, e);
        }
    }

    public List<Object[]> getDailyTrendsDTO(int days) {
        try {
            logger.info("Getting daily trends for last " + days + " days");
            return recordDAO.getDailyCustomerCount(days);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting daily trends", e);
            throw new RuntimeException("Failed to retrieve daily trends: " + e.getMessage(), e);
        }
    }

    // ✅ ADDED: Company-filtered method for daily trends
    public List<Object[]> getDailyTrendsByCompanyDTO(int days, Long companyId) {
        try {
            logger.log(Level.INFO, "Getting daily trends for last {0} days and company: {1}", 
                      new Object[]{days, companyId});
            return recordDAO.getDailyCustomerCountByCompany(days, companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting daily trends by company", e);
            throw new RuntimeException("Failed to retrieve daily trends by company: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getStatsByRoleDTO(String dateStr) {
        try {
            logger.info("Getting statistics by role for date: " + dateStr);
            return recordDAO.getStatsByRole(dateStr);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting statistics by role", e);
            throw new RuntimeException("Failed to retrieve statistics by role: " + e.getMessage(), e);
        }
    }

    // ✅ ADDED: Company-filtered method for stats by role
    public List<Object[]> getStatsByRoleAndCompanyDTO(String dateStr, Long companyId) {
        try {
            logger.log(Level.INFO, "Getting statistics by role for date: {0} and company: {1}", 
                      new Object[]{dateStr, companyId});
            return recordDAO.getStatsByRoleAndCompany(dateStr, companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting statistics by role and company", e);
            throw new RuntimeException("Failed to retrieve statistics by role and company: " + e.getMessage(), e);
        }
    }

    /**
     * Finds all ServiceRecord entities.
     * @return A list of all ServiceRecord entities.
     */
    public List<ServiceRecord> findAll() {
        try {
            logger.info("Finding all service records");
            return recordDAO.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all service records", e);
            throw new RuntimeException("Failed to retrieve service records: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a list of ServiceRecords by the ID of its associated Customer.
     * @param customerId The ID of the customer.
     * @return A list of ServiceRecords for the given customer.
     */
    public List<ServiceRecord> findByCustomerId(Long customerId) {
        try {
            logger.log(Level.INFO, "Finding service records by customer ID: {0}", customerId);
            return recordDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service records by customer ID: " + customerId, e);
            throw new RuntimeException("Failed to retrieve service records by customer ID: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a list of ServiceRecords by their status.
     * @param status The status of the service record.
     * @return A list of ServiceRecords with the given status.
     */
    public List<ServiceRecord> findByStatus(CustomerStatus status) {
        try {
            logger.log(Level.INFO, "Finding service records by status: {0}", status);
            return recordDAO.findByStatus(status);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding service records by status: " + status, e);
            throw new RuntimeException("Failed to retrieve service records by status: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new ServiceRecord entity.
     * @param record The entity to save.
     * @return The persisted entity.
     */
    @Transactional
    public ServiceRecord create(ServiceRecord record) {
        try {
            logger.log(Level.INFO, "Creating new service record for customer ID: {0}", record.getCustomer().getId());
            return recordDAO.save(record);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating service record", e);
            throw new RuntimeException("Failed to create service record: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing ServiceRecord entity.
     * @param record The entity to update.
     * @return The updated entity.
     */
    @Transactional
    public ServiceRecord update(ServiceRecord record) {
        try {
            logger.log(Level.INFO, "Updating service record with ID: {0}", record.getId());
            ServiceRecord existingRecord = recordDAO.findById(record.getId());
            if (existingRecord == null) {
                throw new RuntimeException("Service record not found with ID: " + record.getId());
            }
            return recordDAO.update(record);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating service record with ID: " + record.getId(), e);
            throw new RuntimeException("Failed to update service record: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a ServiceRecord entity by its ID.
     * @param id The ID of the entity to delete.
     * @return True if the entity was deleted, false otherwise.
     */
    @Transactional
    public boolean delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting service record with ID: {0}", id);
            boolean deleted = recordDAO.delete(id);
            if (!deleted) {
                logger.log(Level.WARNING, "Service record not found for deletion with ID: {0}", id);
            }
            return deleted;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting service record with ID: " + id, e);
            throw new RuntimeException("Failed to delete service record: " + id, e);
        }
    }
}