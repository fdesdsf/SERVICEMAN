package co.ke.serviceman.dao;

import co.ke.serviceman.model.ServiceRecord;
import co.ke.serviceman.model.CustomerStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ServiceRecordDAO {

    private static final Logger logger = Logger.getLogger(ServiceRecordDAO.class.getName());

    // Inject the EntityManager directly into this class
    @PersistenceContext(unitName = "servicemanPU")
    private EntityManager em;

    // --- Core CRUD methods ---

    public ServiceRecord findById(Long id) {
        try {
            logger.log(Level.FINE, "Finding ServiceRecord by ID: {0}", id);
            return em.find(ServiceRecord.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding ServiceRecord by ID: " + id, e);
            throw new RuntimeException("Database error while finding entity by ID", e);
        }
    }

    public List<ServiceRecord> findAll() {
        try {
            logger.log(Level.FINE, "Finding all ServiceRecords");
            return em.createQuery("SELECT s FROM ServiceRecord s JOIN FETCH s.customer", ServiceRecord.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all ServiceRecords", e);
            throw new RuntimeException("Database error while finding all entities", e);
        }
    }

    public ServiceRecord save(ServiceRecord entity) {
        try {
            logger.log(Level.INFO, "Saving ServiceRecord for customer ID: {0}", entity.getCustomer().getId());
            em.persist(entity);
            logger.log(Level.INFO, "Successfully saved ServiceRecord with ID: {0", entity.getId());
            return entity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving ServiceRecord", e);
            throw new RuntimeException("Database error while saving entity", e);
        }
    }

    public ServiceRecord update(ServiceRecord entity) {
        try {
            logger.log(Level.INFO, "Updating ServiceRecord with ID: {0}", entity.getId());
            ServiceRecord mergedEntity = em.merge(entity);
            logger.log(Level.INFO, "Successfully updated ServiceRecord with ID: {0}", mergedEntity.getId());
            return mergedEntity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating ServiceRecord", e);
            throw new RuntimeException("Database error while updating entity", e);
        }
    }

    public boolean delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting ServiceRecord with ID: {0}", id);
            ServiceRecord entity = em.find(ServiceRecord.class, id);
            if (entity != null) {
                em.remove(entity);
                logger.log(Level.INFO, "Successfully deleted ServiceRecord with ID: {0}", id);
                return true;
            } else {
                logger.log(Level.WARNING, "ServiceRecord not found for deletion with ID: {0}", id);
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting ServiceRecord with ID: " + id, e);
            throw new RuntimeException("Database error while deleting entity", e);
        }
    }

    // --- Specific Query methods ---

    public List<ServiceRecord> findByCustomerId(Long customerId) {
        try {
            logger.log(Level.FINE, "Finding ServiceRecords by Customer ID: {0}", customerId);
            return em.createQuery("SELECT s FROM ServiceRecord s JOIN FETCH s.customer WHERE s.customer.id = :customerId", ServiceRecord.class)
                     .setParameter("customerId", customerId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding ServiceRecords by Customer ID: " + customerId, e);
            throw new RuntimeException("Database error while finding service records by customer", e);
        }
    }

    public List<ServiceRecord> findByStatus(CustomerStatus status) {
        try {
            logger.log(Level.FINE, "Finding ServiceRecords by status: {0}", status);
            return em.createQuery("SELECT s FROM ServiceRecord s JOIN FETCH s.customer WHERE s.status = :status", ServiceRecord.class)
                     .setParameter("status", status)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding ServiceRecords by status: " + status, e);
            throw new RuntimeException("Database error while finding service records by status", e);
        }
    }

    // In ServiceRecordDAO.java - Add this method with the others
    public ServiceRecord findIncompleteByCustomerAndRole(Long customerId, ServiceRecord.Role role) {
        try {
            logger.log(Level.FINE, "Finding incomplete ServiceRecord for Customer ID: {0} and Role: {1}", new Object[]{customerId, role});
            
            // This query finds the most recent INCOMPLETE record for a customer at a specific role
            String jql = "SELECT s FROM ServiceRecord s " +
                         "WHERE s.customer.id = :customerId " +
                         "AND s.arrivedFrom = :role " +
                         "AND s.completionTime IS NULL " + // This is the key filter
                         "ORDER BY s.startTime DESC";
            
            List<ServiceRecord> results = em.createQuery(jql, ServiceRecord.class)
                    .setParameter("customerId", customerId)
                    .setParameter("role", role)
                    .setMaxResults(1) // Get only the most recent one
                    .getResultList();
            
            return results.isEmpty() ? null : results.get(0);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding incomplete ServiceRecord for Customer ID: " + customerId + " and Role: " + role, e);
            throw new RuntimeException("Database error while finding incomplete service record", e);
        }
    }

    // --- NEW: Admin Dashboard methods ---

    public List<ServiceRecord> findByDate(String dateStr) {
        try {
            logger.log(Level.FINE, "Finding ServiceRecords by start date: {0}", dateStr);
            Date date = Date.valueOf(dateStr);

            return em.createQuery(
                "SELECT s FROM ServiceRecord s JOIN FETCH s.customer " +
                "WHERE DATE(s.startTime) = :date " +
                "ORDER BY s.startTime DESC",
                ServiceRecord.class
            )
            .setParameter("date", date)
            .getResultList();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding ServiceRecords by start date: " + dateStr, e);
            throw new RuntimeException("Database error while finding service records by start date", e);
        }
    }

    // ✅ ADDED: Company-filtered method for date
    public List<ServiceRecord> findByDateAndCompany(String dateStr, Long companyId) {
        try {
            logger.log(Level.FINE, "Finding ServiceRecords by start date: {0} and company: {1}", 
                      new Object[]{dateStr, companyId});
            Date date = Date.valueOf(dateStr);

            return em.createQuery(
                "SELECT s FROM ServiceRecord s JOIN FETCH s.customer c JOIN FETCH c.company " +
                "WHERE DATE(s.startTime) = :date " +
                "AND c.company.id = :companyId " +
                "ORDER BY s.startTime DESC",
                ServiceRecord.class
            )
            .setParameter("date", date)
            .setParameter("companyId", companyId)
            .getResultList();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding ServiceRecords by start date and company: " + dateStr, e);
            throw new RuntimeException("Database error while finding service records by start date and company", e);
        }
    }

    public List<Object[]> getDailyCustomerCount(int days) {
        try {
            logger.log(Level.FINE, "Getting daily customer count for last {0} days", days);
            String query = "SELECT DATE(s.completionTime) as serviceDate, COUNT(s) as customerCount " +
                         "FROM ServiceRecord s " +
                         "WHERE s.completionTime IS NOT NULL " +
                         "AND s.completionTime >= CURRENT_DATE - :days " +
                         "GROUP BY DATE(s.completionTime) " +
                         "ORDER BY serviceDate";
            
            return em.createQuery(query, Object[].class)
                     .setParameter("days", days)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting daily customer count", e);
            throw new RuntimeException("Database error while getting daily customer count", e);
        }
    }

    // ✅ ADDED: Company-filtered method for daily trends
    public List<Object[]> getDailyCustomerCountByCompany(int days, Long companyId) {
        try {
            logger.log(Level.FINE, "Getting daily customer count for last {0} days and company: {1}", 
                      new Object[]{days, companyId});
            
            LocalDate startDate = LocalDate.now().minusDays(days);
            
            String query = "SELECT DATE(s.completionTime) as serviceDate, COUNT(s) as customerCount " +
                         "FROM ServiceRecord s " +
                         "JOIN s.customer c " +
                         "JOIN c.company comp " +
                         "WHERE s.completionTime IS NOT NULL " +
                         "AND comp.id = :companyId " +
                         "AND s.completionTime >= :startDate " +
                         "GROUP BY DATE(s.completionTime) " +
                         "ORDER BY serviceDate";
            
            return em.createQuery(query, Object[].class)
                     .setParameter("companyId", companyId)
                     .setParameter("startDate", java.sql.Date.valueOf(startDate))
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting daily customer count by company", e);
            throw new RuntimeException("Database error while getting daily customer count by company", e);
        }
    }

    public List<Object[]> getStatsByRole(String dateStr) {
        try {
            logger.log(Level.FINE, "Getting statistics by role for date: {0}", dateStr);
            Date date = Date.valueOf(dateStr);
            String query = "SELECT s.arrivedFrom, COUNT(s), COALESCE(SUM(s.timeSpent), 0) " +
                         "FROM ServiceRecord s " +
                         "WHERE DATE(s.completionTime) = :date " +
                         "GROUP BY s.arrivedFrom " +
                         "ORDER BY s.arrivedFrom";
            
            return em.createQuery(query, Object[].class)
                     .setParameter("date", date)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting statistics by role for date: " + dateStr, e);
            throw new RuntimeException("Database error while getting statistics by role", e);
        }
    }

    // ✅ ADDED: Company-filtered method for stats by role
    public List<Object[]> getStatsByRoleAndCompany(String dateStr, Long companyId) {
        try {
            logger.log(Level.FINE, "Getting statistics by role for date: {0} and company: {1}", 
                      new Object[]{dateStr, companyId});
            Date date = Date.valueOf(dateStr);
            
            String query = "SELECT s.arrivedFrom, COUNT(s), COALESCE(SUM(s.timeSpent), 0) " +
                         "FROM ServiceRecord s " +
                         "JOIN s.customer c " +
                         "JOIN c.company comp " +
                         "WHERE DATE(s.completionTime) = :date " +
                         "AND comp.id = :companyId " +
                         "GROUP BY s.arrivedFrom " +
                         "ORDER BY s.arrivedFrom";
            
            return em.createQuery(query, Object[].class)
                     .setParameter("date", date)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting statistics by role and company for date: " + dateStr, e);
            throw new RuntimeException("Database error while getting statistics by role and company", e);
        }
    }

    // Alternative method if the above doesn't work with your database
    public List<Object[]> getStatsByRoleAlternative(String dateStr) {
        try {
            logger.log(Level.FINE, "Getting statistics by role (alternative) for date: {0}", dateStr);
            Date date = Date.valueOf(dateStr);
            String query = "SELECT s.arrivedFrom, COUNT(s), SUM(s.timeSpent) " +
                         "FROM ServiceRecord s " +
                         "WHERE FUNCTION('DATE', s.completionTime) = :date " +
                         "GROUP BY s.arrivedFrom " +
                         "ORDER BY s.arrivedFrom";
            
            return em.createQuery(query, Object[].class)
                     .setParameter("date", date)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting statistics by role (alternative) for date: " + dateStr, e);
            throw new RuntimeException("Database error while getting statistics by role", e);
        }
    }

    // ✅ ADDED: Alternative company-filtered method for stats by role
    public List<Object[]> getStatsByRoleAndCompanyAlternative(String dateStr, Long companyId) {
        try {
            logger.log(Level.FINE, "Getting statistics by role (alternative) for date: {0} and company: {1}", 
                      new Object[]{dateStr, companyId});
            Date date = Date.valueOf(dateStr);
            
            String query = "SELECT s.arrivedFrom, COUNT(s), SUM(s.timeSpent) " +
                         "FROM ServiceRecord s " +
                         "JOIN s.customer c " +
                         "JOIN c.company comp " +
                         "WHERE FUNCTION('DATE', s.completionTime) = :date " +
                         "AND comp.id = :companyId " +
                         "GROUP BY s.arrivedFrom " +
                         "ORDER BY s.arrivedFrom";
            
            return em.createQuery(query, Object[].class)
                     .setParameter("date", date)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting statistics by role and company (alternative) for date: " + dateStr, e);
            throw new RuntimeException("Database error while getting statistics by role and company", e);
        }
    }
}