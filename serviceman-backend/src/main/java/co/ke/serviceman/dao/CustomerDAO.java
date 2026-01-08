package co.ke.serviceman.dao;

import co.ke.serviceman.model.Customer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class CustomerDAO {

    private static final Logger logger = Logger.getLogger(CustomerDAO.class.getName());

    @PersistenceContext(unitName = "servicemanPU")
    private EntityManager em;

    public Customer findById(Long id) {
        try {
            logger.log(Level.FINE, "Finding Customer by ID: {0}", id);
            return em.find(Customer.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding Customer by ID: " + id, e);
            throw new RuntimeException("Database error while finding entity by ID", e);
        }
    }

    public List<Customer> findAll() {
        try {
            logger.log(Level.FINE, "Finding all Customers");
            return em.createQuery("SELECT c FROM Customer c JOIN FETCH c.company", Customer.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all Customers", e);
            throw new RuntimeException("Database error while finding all entities", e);
        }
    }

    public Customer save(Customer entity) {
        try {
            logger.log(Level.INFO, "Saving Customer");
            em.persist(entity);
            logger.log(Level.INFO, "Successfully saved Customer");
            return entity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving Customer", e);
            throw new RuntimeException("Database error while saving entity", e);
        }
    }

    public Customer update(Customer entity) {
        try {
            logger.log(Level.INFO, "Updating Customer with ID: {0}", entity.getId());
            Customer mergedEntity = em.merge(entity);
            logger.log(Level.INFO, "Successfully updated Customer");
            return mergedEntity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating Customer", e);
            throw new RuntimeException("Database error while updating entity", e);
        }
    }

    /**
     * Deletes a customer by ID.
     * @param id The ID of the customer to delete.
     * @return true if the customer was found and deleted, false otherwise.
     */
    public boolean delete(Long id) {
        try {
            logger.log(Level.INFO, "Attempting to delete Customer with ID: {0}", id);
            Customer entity = em.find(Customer.class, id);
            if (entity != null) {
                em.remove(entity);
                logger.log(Level.INFO, "Successfully deleted Customer with ID: {0}", id);
                return true;
            } else {
                logger.log(Level.WARNING, "Customer not found for deletion with ID: {0}", id);
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting Customer with ID: " + id, e);
            throw new RuntimeException("Database error while deleting entity", e);
        }
    }
    
    /**
     * Finds a single customer by their national ID.
     * @param nationalId The national ID of the customer.
     * @return The found Customer or null if no customer is found.
     */
    public Customer findByNationalId(String nationalId) {
        try {
            logger.log(Level.FINE, "Finding Customer by national ID: {0}", nationalId);
            return em.createQuery("SELECT c FROM Customer c WHERE c.nationalId = :nationalId", Customer.class)
                     .setParameter("nationalId", nationalId)
                     .getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.FINE, "No Customer found with national ID: {0}", nationalId);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding Customer by national ID: " + nationalId, e);
            throw new RuntimeException("Database error while finding customer by national ID", e);
        }
    }

    /**
     * Finds a list of customers by their status and next service point.
     * This is used to retrieve the queue of customers waiting for gate clearance.
     *
     * @param status The status of the customer (e.g., FORWARDED).
     * @param nextServicePoint The next service point for the customer (e.g., GATE_ATTENDANT).
     * @return A list of customers matching the criteria.
     */
    public List<Customer> findByStatusAndNextServicePoint(Customer.Status status, Customer.ServicePoint nextServicePoint) {
        try {
            logger.log(Level.FINE, "Finding customers with status {0} and next service point {1}", new Object[]{status, nextServicePoint});
            return em.createQuery(
                            "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.status = :status AND c.nextServicePoint = :nextServicePoint", Customer.class)
                     .setParameter("status", status)
                     .setParameter("nextServicePoint", nextServicePoint)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customers by status and next service point", e);
            throw new RuntimeException("Database error while finding customers by status and next service point", e);
        }
    }

    // --- Original methods ---
    public Customer findByPhone(String phone) {
        try {
            logger.log(Level.FINE, "Finding Customer by phone number: {0}", phone);
            return em.createQuery("SELECT c FROM Customer c WHERE c.phone = :phone", Customer.class)
                     .setParameter("phone", phone)
                     .getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.FINE, "No Customer found with phone number: {0}", phone);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding Customer by phone number: " + phone, e);
            throw new RuntimeException("Database error while finding customer by phone", e);
        }
    }

    public List<Customer> findByStatusAndCurrentRole(Customer.Status status, Customer.ServicePoint role) {
        return em.createQuery(
            "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.status = :status AND c.currentRole = :role", Customer.class)
            .setParameter("status", status)
            .setParameter("role", role)
            .getResultList();
    }

    // ✅ UPDATED: Company filtering methods

    public List<Customer> findByCompanyId(Long companyId) {
        try {
            logger.log(Level.FINE, "Finding customers by company ID: {0}", companyId);
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.company.id = :companyId ORDER BY c.createdAt DESC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customers by company ID: " + companyId, e);
            throw new RuntimeException("Database error while finding customers by company ID", e);
        }
    }

    // Add this method to your CustomerDAO
public List<Customer> findServedCustomersByCompany(Long companyId) {
    try {
        logger.log(Level.FINE, "Finding served customers by company ID: {0}", companyId);
        String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.company.id = :companyId AND c.status = 'SERVED' AND c.totalTimeInSystem IS NOT NULL ORDER BY c.createdAt DESC";
        return em.createQuery(jpql, Customer.class)
                 .setParameter("companyId", companyId)
                 .getResultList();
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error finding served customers by company ID: " + companyId, e);
        throw new RuntimeException("Database error while finding served customers by company ID", e);
    }
}

    public List<Customer> findClearanceQueueByCompany(Long companyId) {
        try {
            logger.log(Level.FINE, "Finding clearance queue by company ID: {0}", companyId);
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.company.id = :companyId AND c.nextServicePoint IS NULL ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clearance queue by company ID: " + companyId, e);
            throw new RuntimeException("Database error while finding clearance queue by company ID", e);
        }
    }

    public List<Customer> findByNextServicePointAndCompany(String nextServicePoint, Long companyId) {
        try {
            logger.log(Level.FINE, "Finding customers by nextServicePoint: {0} and companyId: {1}", 
                       new Object[]{nextServicePoint, companyId});
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = :nextServicePoint AND c.company.id = :companyId ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("nextServicePoint", Customer.ServicePoint.valueOf(nextServicePoint))
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customers by nextServicePoint and companyId", e);
            throw new RuntimeException("Database error while finding customers by nextServicePoint and companyId", e);
        }
    }

    public List<Customer> findByCurrentRoleAndCompany(String currentRole, Long companyId) {
        try {
            logger.log(Level.FINE, "Finding customers by currentRole: {0} and companyId: {1}", 
                       new Object[]{currentRole, companyId});
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.currentRole = :currentRole AND c.company.id = :companyId ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("currentRole", Customer.ServicePoint.valueOf(currentRole))
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customers by currentRole and companyId", e);
            throw new RuntimeException("Database error while finding customers by currentRole and companyId", e);
        }
    }

    // ✅ ADDED: Service point specific methods with company filtering

    public List<Customer> findCustomersForReceptionistByCompany(Long companyId) {
        try {
            logger.log(Level.FINE, "Finding receptionist queue by company ID: {0}", companyId);
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'RECEPTIONIST' AND c.company.id = :companyId ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding receptionist queue by company ID: " + companyId, e);
            throw new RuntimeException("Database error while finding receptionist queue by company ID", e);
        }
    }

    public List<Customer> findCustomersForSalesOrderDeskByCompany(Long companyId) {
        try {
            logger.log(Level.FINE, "Finding sales order desk queue by company ID: {0}", companyId);
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'SALES_ORDER_DESK' AND c.company.id = :companyId ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding sales order desk queue by company ID: " + companyId, e);
            throw new RuntimeException("Database error while finding sales order desk queue by company ID", e);
        }
    }

    public List<Customer> findCustomersForInvoicingDeskByCompany(Long companyId) {
        try {
            logger.log(Level.FINE, "Finding invoicing desk queue by company ID: {0}", companyId);
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'INVOICING_DESK' AND c.company.id = :companyId ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding invoicing desk queue by company ID: " + companyId, e);
            throw new RuntimeException("Database error while finding invoicing desk queue by company ID", e);
        }
    }

    public List<Customer> findCustomersForStoreClerkByCompany(Long companyId) {
        try {
            logger.log(Level.FINE, "Finding store clerk queue by company ID: {0}", companyId);
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'STORE_CLERK' AND c.company.id = :companyId ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class)
                     .setParameter("companyId", companyId)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding store clerk queue by company ID: " + companyId, e);
            throw new RuntimeException("Database error while finding store clerk queue by company ID", e);
        }
    }

    // ✅ ADDED: Clearance queue (without company filter for backward compatibility)
    public List<Customer> findClearanceQueue() {
        try {
            logger.log(Level.FINE, "Finding all clearance queue");
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint IS NULL ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clearance queue", e);
            throw new RuntimeException("Database error while finding clearance queue", e);
        }
    }

    // ✅ ADDED: Service point queues (without company filter for backward compatibility)
    public List<Customer> findCustomersForReceptionist() {
        try {
            logger.log(Level.FINE, "Finding all receptionist queue");
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'RECEPTIONIST' ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding receptionist queue", e);
            throw new RuntimeException("Database error while finding receptionist queue", e);
        }
    }

    public List<Customer> findCustomersForSalesOrderDesk() {
        try {
            logger.log(Level.FINE, "Finding all sales order desk queue");
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'SALES_ORDER_DESK' ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding sales order desk queue", e);
            throw new RuntimeException("Database error while finding sales order desk queue", e);
        }
    }

    public List<Customer> findCustomersForInvoicingDesk() {
        try {
            logger.log(Level.FINE, "Finding all invoicing desk queue");
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'INVOICING_DESK' ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding invoicing desk queue", e);
            throw new RuntimeException("Database error while finding invoicing desk queue", e);
        }
    }

    public List<Customer> findCustomersForStoreClerk() {
        try {
            logger.log(Level.FINE, "Finding all store clerk queue");
            String jpql = "SELECT c FROM Customer c JOIN FETCH c.company WHERE c.nextServicePoint = 'STORE_CLERK' ORDER BY c.createdAt ASC";
            return em.createQuery(jpql, Customer.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding store clerk queue", e);
            throw new RuntimeException("Database error while finding store clerk queue", e);
        }
    }
}