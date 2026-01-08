package co.ke.serviceman.service;

import co.ke.serviceman.dao.CustomerDAO;
import co.ke.serviceman.dao.ServiceRecordDAO;
import co.ke.serviceman.dto.CustomerDTO;
import co.ke.serviceman.model.Customer;
import co.ke.serviceman.model.ServiceRecord;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class CustomerService {

    private static final Logger logger = Logger.getLogger(CustomerService.class.getName());

    @Inject
    private CustomerDAO customerDAO;

    @Inject
    private ServiceRecordDAO serviceRecordDAO;

    // ========== DTO METHODS FOR API RESPONSES ==========

    public List<CustomerDTO> findAllDTO() {
        try {
            logger.info("Finding all customers as DTOs");
            List<Customer> customers = customerDAO.findAll();
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all customers as DTOs", e);
            throw new RuntimeException("Failed to retrieve customers: " + e.getMessage(), e);
        }
    }

    public CustomerDTO findByIdDTO(Long id) {
        try {
            logger.log(Level.INFO, "Finding customer by ID as DTO: {0}", id);
            Customer customer = customerDAO.findById(id);
            if (customer == null) {
                logger.log(Level.WARNING, "Customer not found with ID: {0}", id);
                throw new RuntimeException("Customer not found with ID: " + id);
            }
            return new CustomerDTO(customer);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customer by ID as DTO: " + id, e);
            throw new RuntimeException("Failed to retrieve customer with ID: " + id, e);
        }
    }

    public CustomerDTO findByNationalIdDTO(String nationalId) {
        try {
            logger.log(Level.INFO, "Finding customer by national ID as DTO: {0}", nationalId);
            Customer customer = customerDAO.findByNationalId(nationalId);
            return customer != null ? new CustomerDTO(customer) : null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customer by national ID as DTO: " + nationalId, e);
            throw new RuntimeException("Failed to retrieve customer by national ID: " + e.getMessage(), e);
        }
    }

    public List<Customer> getCustomersForClearance() {
    try {
        logger.info("Retrieving customers ready for gate clearance");
        return customerDAO.findByStatusAndNextServicePoint(
            Customer.Status.FORWARDED, 
            Customer.ServicePoint.GATE_ATTENDANT
        );
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error retrieving customers for clearance", e);
        throw new RuntimeException("Failed to retrieve customers for clearance: " + e.getMessage(), e);
    }
}

public List<CustomerDTO> getCustomersForClearanceDTO() {
    try {
        logger.info("Retrieving customers ready for gate clearance as DTOs");
        List<Customer> customers = customerDAO.findByStatusAndNextServicePoint(
            Customer.Status.FORWARDED, 
            Customer.ServicePoint.GATE_ATTENDANT
        );
        return customers.stream()
                .map(CustomerDTO::new)
                .collect(Collectors.toList());
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error retrieving customers for clearance as DTOs", e);
        throw new RuntimeException("Failed to retrieve customers for clearance: " + e.getMessage(), e);
    }
}

    public List<CustomerDTO> getCustomersForReceptionistDTO() {
        try {
            logger.info("Retrieving customers waiting for receptionist as DTOs");
            List<Customer> customers = customerDAO.findCustomersForReceptionist();
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving customers for receptionist as DTOs", e);
            throw new RuntimeException("Failed to retrieve customers for receptionist: " + e.getMessage(), e);
        }
    }

    public List<CustomerDTO> getCustomersForSalesOrderDeskDTO() {
        try {
            logger.info("Retrieving customers waiting for sales order desk as DTOs");
            List<Customer> customers = customerDAO.findCustomersForSalesOrderDesk();
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving customers for sales order desk as DTOs", e);
            throw new RuntimeException("Failed to retrieve customers for sales order desk: " + e.getMessage(), e);
        }
    }

    public List<CustomerDTO> getCustomersForInvoicingDeskDTO() {
        try {
            logger.info("Retrieving customers waiting for invoicing desk as DTOs");
            List<Customer> customers = customerDAO.findCustomersForInvoicingDesk();
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving customers for invoicing desk as DTOs", e);
            throw new RuntimeException("Failed to retrieve customers for invoicing desk: " + e.getMessage(), e);
        }
    }

    public List<CustomerDTO> getCustomersForStoreClerkDTO() {
        try {
            logger.info("Retrieving customers waiting for store clerk as DTOs");
            List<Customer> customers = customerDAO.findCustomersForStoreClerk();
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving customers for store clerk as DTOs", e);
            throw new RuntimeException("Failed to retrieve customers for store clerk: " + e.getMessage(), e);
        }
    }

    // ✅ ADDED: Company-filtered DTO methods

    public List<CustomerDTO> findByCompanyIdDTO(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding customers by company ID as DTOs: {0}", companyId);
            List<Customer> customers = customerDAO.findByCompanyId(companyId);
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customers by company ID as DTOs: " + companyId, e);
            throw new RuntimeException("Failed to retrieve customers for company: " + e.getMessage(), e);
        }
    }

    public List<CustomerDTO> getCustomersForClearanceByCompanyDTO(Long companyId) {
    try {
        logger.log(Level.INFO, "Finding clearance queue by company ID as DTOs: {0}", companyId);
        // Get all FORWARDED + GATE_ATTENDANT customers
        List<Customer> customers = customerDAO.findByStatusAndNextServicePoint(
            Customer.Status.FORWARDED, 
            Customer.ServicePoint.GATE_ATTENDANT
        );
        // Filter by company in service layer
        List<Customer> companyCustomers = customers.stream()
                .filter(c -> c.getCompany().getId().equals(companyId))
                .collect(Collectors.toList());
        return companyCustomers.stream()
                .map(CustomerDTO::new)
                .collect(Collectors.toList());
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error finding clearance queue by company ID as DTOs: " + companyId, e);
        throw new RuntimeException("Failed to retrieve clearance queue for company: " + e.getMessage(), e);
    }
}

    public List<CustomerDTO> getCustomersForReceptionistByCompanyDTO(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding receptionist queue by company ID as DTOs: {0}", companyId);
            List<Customer> customers = customerDAO.findCustomersForReceptionistByCompany(companyId);
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding receptionist queue by company ID as DTOs: " + companyId, e);
            throw new RuntimeException("Failed to retrieve receptionist queue for company: " + e.getMessage(), e);
        }
    }

    

    public List<CustomerDTO> getCustomersForSalesOrderDeskByCompanyDTO(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding sales order desk queue by company ID as DTOs: {0}", companyId);
            List<Customer> customers = customerDAO.findCustomersForSalesOrderDeskByCompany(companyId);
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding sales order desk queue by company ID as DTOs: " + companyId, e);
            throw new RuntimeException("Failed to retrieve sales order desk queue for company: " + e.getMessage(), e);
        }
    }

    public List<CustomerDTO> getCustomersForInvoicingDeskByCompanyDTO(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding invoicing desk queue by company ID as DTOs: {0}", companyId);
            List<Customer> customers = customerDAO.findCustomersForInvoicingDeskByCompany(companyId);
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding invoicing desk queue by company ID as DTOs: " + companyId, e);
            throw new RuntimeException("Failed to retrieve invoicing desk queue for company: " + e.getMessage(), e);
        }
    }

    public List<CustomerDTO> getCustomersForStoreClerkByCompanyDTO(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding store clerk queue by company ID as DTOs: {0}", companyId);
            List<Customer> customers = customerDAO.findCustomersForStoreClerkByCompany(companyId);
            return customers.stream()
                    .map(CustomerDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding store clerk queue by company ID as DTOs: " + companyId, e);
            throw new RuntimeException("Failed to retrieve store clerk queue for company: " + e.getMessage(), e);
        }
    }

    // ========== ENTITY METHODS FOR INTERNAL OPERATIONS ==========

    public List<Customer> findAll() {
        try {
            logger.info("Finding all customers");
            return customerDAO.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all customers", e);
            throw new RuntimeException("Failed to retrieve customers: " + e.getMessage(), e);
        }
    }

    public Customer findById(Long id) {
        try {
            logger.log(Level.INFO, "Finding customer by ID: {0}", id);
            Customer customer = customerDAO.findById(id);
            if (customer == null) {
                logger.log(Level.WARNING, "Customer not found with ID: {0}", id);
                throw new RuntimeException("Customer not found with ID: " + id);
            }
            return customer;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customer by ID: " + id, e);
            throw new RuntimeException("Failed to retrieve customer with ID: " + id, e);
        }
    }

    public Customer findByNationalId(String nationalId) {
        try {
            logger.log(Level.INFO, "Finding customer by national ID: {0}", nationalId);
            return customerDAO.findByNationalId(nationalId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customer by national ID: " + nationalId, e);
            throw new RuntimeException("Failed to retrieve customer by national ID: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Customer create(Customer customer) {
        try {
            logger.log(Level.INFO, "Creating new customer: {0}", customer.getName());
            
            // Basic validation
            if (customer.getNationalId() == null || customer.getNationalId().trim().isEmpty() ||
                customer.getName() == null || customer.getName().trim().isEmpty()) {
                throw new RuntimeException("National ID and name are required");
            }
            
            // Check if a customer with the same National ID already exists
            Customer existingCustomer = customerDAO.findByNationalId(customer.getNationalId());
            if (existingCustomer != null) {
                throw new RuntimeException("Customer with National ID " + customer.getNationalId() + " already exists.");
            }
            
            // Set next service point based on current role
            if (customer.getCurrentRole() != null && customer.getNextServicePoint() == null) {
                customer.setNextServicePoint(determineNextServicePoint(customer.getCurrentRole(), customer.getServiceRequested()));
            }
            
            customerDAO.save(customer);
            logger.log(Level.INFO, "Customer created successfully with ID: {0}", customer.getId());
            
            // Create initial service record for new customers
            if (customer.getCurrentRole() != null) {
                createServiceRecord(customer, null, customer.getCurrentRole(), "Initial customer creation");
            }
            
            return customer;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating customer: " + customer.getName(), e);
            throw new RuntimeException("Failed to create customer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Customer update(Customer customer) {
        try {
            logger.log(Level.INFO, "Updating customer with ID: {0}", customer.getId());
            
            // Check if customer exists
            Customer existingCustomer = customerDAO.findById(customer.getId());
            if (existingCustomer == null) {
                throw new RuntimeException("Customer not found with ID: " + customer.getId());
            }
            
            // Track service point changes BEFORE updating
            boolean servicePointChanged = existingCustomer.getCurrentRole() != null && 
                                         customer.getCurrentRole() != null &&
                                         !existingCustomer.getCurrentRole().equals(customer.getCurrentRole());
            
            Customer.ServicePoint previousServicePoint = existingCustomer.getCurrentRole();
            
            // Check for National ID uniqueness if it's being changed
            if (!existingCustomer.getNationalId().equals(customer.getNationalId())) {
                Customer customerWithNewId = customerDAO.findByNationalId(customer.getNationalId());
                if (customerWithNewId != null && !customerWithNewId.getId().equals(customer.getId())) {
                    throw new RuntimeException("A different customer already has the National ID: " + customer.getNationalId());
                }
            }
            
            // Update next service point if current role changed
            if (servicePointChanged) {
                customer.setNextServicePoint(determineNextServicePoint(customer.getCurrentRole(), customer.getServiceRequested()));
            }
            
            customerDAO.update(customer);
            logger.log(Level.INFO, "Customer updated successfully with ID: {0}", customer.getId());
            
            // Create service record if service point changed (with arrivedFrom as previous service point)
            if (servicePointChanged && previousServicePoint != null) {
                createServiceRecord(customer, previousServicePoint, customer.getCurrentRole(), 
                                     "Service point changed from " + previousServicePoint + " to " + customer.getCurrentRole());
            }
            
            return customer;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating customer with ID: " + customer.getId(), e);
            throw new RuntimeException("Failed to update customer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public boolean delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting customer with ID: {0}", id);
            return customerDAO.delete(id);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting customer with ID: " + id, e);
            throw new RuntimeException("Failed to delete customer: " + id, e);
        }
    }

    // ✅ ADDED: Company-filtered entity methods

    public List<Customer> findByCompanyId(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding customers by company ID: {0}", companyId);
            return customerDAO.findByCompanyId(companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customers by company ID: " + companyId, e);
            throw new RuntimeException("Failed to retrieve customers for company", e);
        }
    }

    public List<Customer> findClearanceQueueByCompany(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding clearance queue by company ID: {0}", companyId);
            return customerDAO.findClearanceQueueByCompany(companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clearance queue by company ID: " + companyId, e);
            throw new RuntimeException("Failed to retrieve clearance queue for company", e);
        }
    }

    public List<Customer> findCustomersForReceptionistByCompany(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding receptionist queue by company ID: {0}", companyId);
            return customerDAO.findCustomersForReceptionistByCompany(companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding receptionist queue by company ID: " + companyId, e);
            throw new RuntimeException("Failed to retrieve receptionist queue for company", e);
        }
    }

    public List<Customer> findCustomersForSalesOrderDeskByCompany(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding sales order desk queue by company ID: {0}", companyId);
            return customerDAO.findCustomersForSalesOrderDeskByCompany(companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding sales order desk queue by company ID: " + companyId, e);
            throw new RuntimeException("Failed to retrieve sales order desk queue for company", e);
        }
    }

    public List<Customer> findCustomersForInvoicingDeskByCompany(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding invoicing desk queue by company ID: {0}", companyId);
            return customerDAO.findCustomersForInvoicingDeskByCompany(companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding invoicing desk queue by company ID: " + companyId, e);
            throw new RuntimeException("Failed to retrieve invoicing desk queue for company", e);
        }
    }

    public List<Customer> findCustomersForStoreClerkByCompany(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding store clerk queue by company ID: {0}", companyId);
            return customerDAO.findCustomersForStoreClerkByCompany(companyId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding store clerk queue by company ID: " + companyId, e);
            throw new RuntimeException("Failed to retrieve store clerk queue for company", e);
        }
    }

    // Add this method to your CustomerService
public List<CustomerDTO> findServedCustomersByCompanyDTO(Long companyId) {
    try {
        logger.log(Level.INFO, "Finding served customers by company ID: {0}", companyId);
        List<Customer> customers = customerDAO.findServedCustomersByCompany(companyId);
        return customers.stream()
                .map(CustomerDTO::new)
                .collect(Collectors.toList());
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error finding served customers by company ID: " + companyId, e);
        throw new RuntimeException("Failed to retrieve served customers for company", e);
    }
}

    /**
     * Clears a customer from the system, updating their status to SERVED and logging a final service record.
     *
     * @param id The ID of the customer to clear.
     * @return The updated Customer object.
     */
    @Transactional
    public Customer clearCustomer(Long id) {
        try {
            logger.log(Level.INFO, "Clearing customer with ID: {0}", id);
            Customer customer = customerDAO.findById(id);

            if (customer == null) {
                throw new RuntimeException("Customer not found with ID: " + id);
            }

            // Get the final completion time
            Timestamp finalCompletionTime = new Timestamp(new Date().getTime());

            // Get the customer's creation time from the database record
            Timestamp creationTime = customer.getCreatedAt();
            
            // Calculate total time spent in milliseconds
            long totalTimeInMilliseconds = finalCompletionTime.getTime() - creationTime.getTime();
            
            // Convert milliseconds to a more readable format (e.g., seconds)
            long totalTimeInSeconds = totalTimeInMilliseconds / 1000;

            // ✅ STORE THE TOTAL TIME ON THE CUSTOMER ENTITY
            customer.setTotalTimeInSystem(totalTimeInSeconds);
            
            // Update the status to SERVED to indicate they have exited
            customer.setStatus(Customer.Status.SERVED);
            customer.setNextServicePoint(null); // No next service point once cleared
            
            // ✅ DON'T CREATE A SERVICE RECORD FOR CLEARANCE - just log it
            logger.log(Level.INFO, "Customer {0} cleared from facility by {1}. Total time in system: {2} seconds.", 
                      new Object[]{customer.getId(), getCurrentUsername(), totalTimeInSeconds});
            
            customerDAO.update(customer);
            
            return customer;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error clearing customer with ID: " + id, e);
            throw new RuntimeException("Failed to clear customer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Customer forwardCustomer(Long id, Customer.ServicePoint currentServicePoint, Customer.ServicePoint nextServicePoint) {
        try {
            logger.log(Level.INFO, "Forwarding customer ID: {0} from {1} to {2}", new Object[]{id, currentServicePoint, nextServicePoint});

            Customer customer = customerDAO.findById(id);

            if (customer == null) {
                throw new RuntimeException("Customer not found with ID: " + id);
            }

            // Update customer details
            customer.setCurrentRole(currentServicePoint);
            customer.setStatus(Customer.Status.FORWARDED);
            customer.setNextServicePoint(nextServicePoint);

            customerDAO.update(customer);

            // Create service record for the NEXT service point
            createServiceRecord(customer, currentServicePoint, nextServicePoint, "Customer forwarded from " + currentServicePoint + " to " + nextServicePoint);

            logger.log(Level.INFO, "Customer ID: {0} forwarded from {1} to {2}", new Object[]{id, currentServicePoint, nextServicePoint});

            return customer;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error forwarding customer ID: " + id, e);
            throw new RuntimeException("Failed to forward customer: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void startService(Long customerId, Customer.ServicePoint servicePoint) {
        try {
            logger.log(Level.INFO, "Starting service for customer ID: {0} at {1}", 
                      new Object[]{customerId, servicePoint});
            
            Customer customer = customerDAO.findById(customerId);
            if (customer == null) {
                throw new RuntimeException("Customer not found with ID: " + customerId);
            }
            
            // Update customer status to indicate they're being processed
            customer.setStatus(Customer.Status.IN_SERVICE);
            customerDAO.update(customer);
            
            // Find and update the service record with start time
            ServiceRecord activeRecord = findActiveServiceRecord(customerId, servicePoint);
            if (activeRecord != null && activeRecord.getStartTime() == null) {
                activeRecord.setStartTime(new Timestamp(new Date().getTime()));
                serviceRecordDAO.update(activeRecord);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting service for customer ID: " + customerId, e);
            throw new RuntimeException("Failed to start service: " + e.getMessage(), e);
        }
    }

    /**
     * Completes a service for a customer at a specific service point
     * This sets the completion_time and calculates actual time_spent
     */
    @Transactional
    public void completeService(Long customerId, Customer.ServicePoint servicePoint, Long timeSpentSeconds) {
        try {
            logger.log(Level.INFO, "Completing service for customer ID: {0} at {1}", new Object[]{customerId, servicePoint});
            
            // 1. Convert the ServicePoint to a ServiceRecord.Role to search for
            ServiceRecord.Role targetRole = convertToServiceRecordRole(servicePoint);
            
            if (targetRole == null) {
                throw new RuntimeException("Cannot complete service: Invalid service point " + servicePoint);
            }

            // 2. Find the INCOMPLETE record for this customer at this specific service point
            ServiceRecord recordToComplete = serviceRecordDAO.findIncompleteByCustomerAndRole(customerId, targetRole);

            if (recordToComplete != null) {
                // 3. Update ONLY the correct record
                recordToComplete.setTimeSpent(timeSpentSeconds);
                recordToComplete.setCompletionTime(new Timestamp(new Date().getTime())); // Mark it complete
                serviceRecordDAO.update(recordToComplete);

                logger.log(Level.INFO, "Service completed for customer {0} at {1}. Time spent: {2} seconds", 
                          new Object[]{customerId, servicePoint, timeSpentSeconds});
            } else {
                // 4. Fallback: Create a completed record if none was found (should not happen in normal flow)
                logger.log(Level.WARNING, "No active service record found to complete for customer {0} at {1}. Creating fallback record.", 
                          new Object[]{customerId, servicePoint});
                
                Customer customer = customerDAO.findById(customerId);
                if (customer != null) {
                    ServiceRecord fallbackRecord = new ServiceRecord();
                    fallbackRecord.setCustomer(customer);
                    fallbackRecord.setArrivedFrom(targetRole);
                    fallbackRecord.setServedBy(getCurrentUsername());
                    fallbackRecord.setStartTime(new Timestamp(new Date().getTime() - (timeSpentSeconds * 1000))); // Estimate start time
                    fallbackRecord.setCompletionTime(new Timestamp(new Date().getTime()));
                    fallbackRecord.setTimeSpent(timeSpentSeconds);
                    fallbackRecord.setNotes("Fallback: Service completed but no active record was found.");
                    
                    serviceRecordDAO.save(fallbackRecord);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error completing service for customer " + customerId + " at " + servicePoint, e);
            throw new RuntimeException("Failed to complete service: " + e.getMessage(), e);
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Helper method to create service records with proper enum conversion
     * NOTE: completion_time is NOT set here - it should be set when service is actually completed
     */
    private void createServiceRecord(Customer customer, Customer.ServicePoint arrivedFrom, 
                                Customer.ServicePoint servedAt, String notes) {
        try {
            ServiceRecord record = new ServiceRecord();
            record.setCustomer(customer);
            
            // Convert Customer.ServicePoint to ServiceRecord.Role for arrivedFrom
            if (arrivedFrom != null) {
                record.setArrivedFrom(convertToServiceRecordRole(arrivedFrom));
            }
            
            record.setServedBy(getCurrentUsername());
            record.setNotes(notes);
            record.setStartTime(new Timestamp(new Date().getTime()));
            
            // DO NOT set completionTime here - it should be set when service is actually completed
            // Time spent should be 0 initially - calculated when service is completed
            record.setTimeSpent(0L);
            
            serviceRecordDAO.save(record);
            logger.log(Level.INFO, "Service record created for customer ID: {0}", customer.getId());
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to create service record for customer ID: " + customer.getId(), e);
            // Don't throw exception - service record failure shouldn't break customer operations
        }
    }

    /**
     * Find active service record for a customer at specific service point
     */
    private ServiceRecord findActiveServiceRecord(Long customerId, Customer.ServicePoint servicePoint) {
        try {
            List<ServiceRecord> records = serviceRecordDAO.findByCustomerId(customerId);
            // Find the most recent record for this service point that hasn't been completed
            return records.stream()
                    .filter(record -> record.getArrivedFrom() != null && 
                                     convertToCustomerServicePoint(record.getArrivedFrom()).equals(servicePoint) &&
                                     record.getCompletionTime() == null)
                    .reduce((first, second) -> second) // Get the last one
                    .orElse(null);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error finding active service record for customer: " + customerId, e);
            return null;
        }
    }

    /**
     * Determine the next service point based on current role and service requested
     */
    private Customer.ServicePoint determineNextServicePoint(Customer.ServicePoint currentRole, String serviceRequested) {
        if (currentRole == null) return null;
        
        switch (currentRole) {
            case GATE_ATTENDANT:
                return Customer.ServicePoint.RECEPTIONIST;
                
            case RECEPTIONIST:
                // Determine next point based on service requested
                if ("Sales".equalsIgnoreCase(serviceRequested)) {
                    return Customer.ServicePoint.SALES_ORDER_DESK;
                } else if ("Invoices".equalsIgnoreCase(serviceRequested)) {
                    return Customer.ServicePoint.INVOICING_DESK;
                } else if ("Store".equalsIgnoreCase(serviceRequested)) {
                    return Customer.ServicePoint.STORE_CLERK;
                }
                return Customer.ServicePoint.SALES_ORDER_DESK; // Default
                
            case SALES_ORDER_DESK:
                return Customer.ServicePoint.INVOICING_DESK;
                
            case INVOICING_DESK:
                return Customer.ServicePoint.STORE_CLERK;
                
            case STORE_CLERK:
                return null; // Final service point - customer can exit
                
            default:
                return null;
        }
    }

    /**
     * Convert Customer.ServicePoint to ServiceRecord.Role
     */
    private ServiceRecord.Role convertToServiceRecordRole(Customer.ServicePoint servicePoint) {
        if (servicePoint == null) {
            return null;
        }
        
        switch (servicePoint) {
            case GATE_ATTENDANT:
                return ServiceRecord.Role.GATE_ATTENDANT;
            case RECEPTIONIST:
                return ServiceRecord.Role.RECEPTIONIST;
            case SALES_ORDER_DESK:
                return ServiceRecord.Role.SALES_ORDER_DESK;
            case INVOICING_DESK:
                return ServiceRecord.Role.INVOICING_DESK;
            case STORE_CLERK:
                return ServiceRecord.Role.STORE_CLERK;
            default:
                throw new IllegalArgumentException("Unknown service point: " + servicePoint);
        }
    }

    /**
     * Convert ServiceRecord.Role back to Customer.ServicePoint
     */
    private Customer.ServicePoint convertToCustomerServicePoint(ServiceRecord.Role role) {
        if (role == null) return null;
        
        switch (role) {
            case GATE_ATTENDANT: return Customer.ServicePoint.GATE_ATTENDANT;
            case RECEPTIONIST: return Customer.ServicePoint.RECEPTIONIST;
            case SALES_ORDER_DESK: return Customer.ServicePoint.SALES_ORDER_DESK;
            case INVOICING_DESK: return Customer.ServicePoint.INVOICING_DESK;
            case STORE_CLERK: return Customer.ServicePoint.STORE_CLERK;
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    /**
     * Get the current authenticated username
     */
    private String getCurrentUsername() {
        // For Java EE Security (WildFly):
        try {
            javax.security.auth.Subject subject = javax.security.auth.Subject.getSubject(java.security.AccessController.getContext());
            if (subject != null && !subject.getPrincipals().isEmpty()) {
                return subject.getPrincipals().iterator().next().getName();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not get current user from security context", e);
        }
        
        // Temporary fallback
        return "system_user";
    }
}