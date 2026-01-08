package co.ke.serviceman.api;

import co.ke.serviceman.dto.CustomerDTO;
import co.ke.serviceman.model.Customer;
import co.ke.serviceman.service.CustomerService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private static final Logger logger = Logger.getLogger(CustomerResource.class.getName());

    @Inject
    private CustomerService customerService;

    // ✅ UPDATED: Added companyId parameter to all queue endpoints

    @GET
    public Response getAllCustomers(@QueryParam("companyId") Long companyId) {
        try {
            List<CustomerDTO> customers;
            if (companyId != null) {
                customers = customerService.findByCompanyIdDTO(companyId);
            } else {
                customers = customerService.findAllDTO();
            }
            return Response.ok(customers).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving all customers", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving customers: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getCustomer(@PathParam("id") Long id) {
        try {
            CustomerDTO customer = customerService.findByIdDTO(id);
            if (customer == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Customer not found with id: " + id)
                        .build();
            }
            return Response.ok(customer).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving customer by ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving customer: " + e.getMessage())
                    .build();
        }
    }

    @POST
    public Response createCustomer(Customer customer) {
        try {
            Customer createdCustomer = customerService.create(customer);
            CustomerDTO createdCustomerDTO = new CustomerDTO(createdCustomer);
            return Response.status(Response.Status.CREATED).entity(createdCustomerDTO).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating user: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, Customer customer) {
        try {
            customer.setId(id);
            Customer updatedCustomer = customerService.update(customer);
            CustomerDTO updatedCustomerDTO = new CustomerDTO(updatedCustomer);
            return Response.ok(updatedCustomerDTO).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating customer", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating customer: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        try {
            boolean deleted = customerService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Customer not found with id: " + id)
                        .build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting customer", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting customer: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/national_id/{nationalId}")
    public Response getCustomerByNationalId(@PathParam("nationalId") String nationalId) {
        try {
            CustomerDTO customer = customerService.findByNationalIdDTO(nationalId);
            if (customer == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Customer not found with national ID: " + nationalId)
                        .build();
            }
            return Response.ok(customer).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving customer by national ID: " + nationalId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving customer: " + e.getMessage())
                    .build();
        }
    }

    // ✅ UPDATED: Added companyId parameter
    @GET
    @Path("/clearance")
    public Response getClearanceQueue(@QueryParam("companyId") Long companyId) {
        try {
            List<CustomerDTO> clearanceQueue;
            if (companyId != null) {
                clearanceQueue = customerService.getCustomersForClearanceByCompanyDTO(companyId);
            } else {
                clearanceQueue = customerService.getCustomersForClearanceDTO();
            }
            return Response.ok(clearanceQueue).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving clearance queue", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving clearance queue: " + e.getMessage())
                    .build();
        }
    }

    // ✅ UPDATED: Added companyId parameter
    @GET
    @Path("/receptionist")
    public Response getReceptionistQueue(@QueryParam("companyId") Long companyId) {
        try {
            List<CustomerDTO> receptionistQueue;
            if (companyId != null) {
                receptionistQueue = customerService.getCustomersForReceptionistByCompanyDTO(companyId);
            } else {
                receptionistQueue = customerService.getCustomersForReceptionistDTO();
            }
            return Response.ok(receptionistQueue).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving receptionist queue", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving receptionist queue: " + e.getMessage())
                    .build();
        }
    }

    // ✅ UPDATED: Added companyId parameter
    @GET
    @Path("/sales-order-desk")
    public Response getSalesOrderDeskQueue(@QueryParam("companyId") Long companyId) {
        try {
            List<CustomerDTO> salesOrderQueue;
            if (companyId != null) {
                salesOrderQueue = customerService.getCustomersForSalesOrderDeskByCompanyDTO(companyId);
            } else {
                salesOrderQueue = customerService.getCustomersForSalesOrderDeskDTO();
            }
            return Response.ok(salesOrderQueue).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving sales order desk queue", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving sales order desk queue: " + e.getMessage())
                    .build();
        }
    }

    // ✅ UPDATED: Added companyId parameter
    @GET
    @Path("/invoicing-desk")
    public Response getInvoicingDeskQueue(@QueryParam("companyId") Long companyId) {
        try {
            List<CustomerDTO> invoicingQueue;
            if (companyId != null) {
                invoicingQueue = customerService.getCustomersForInvoicingDeskByCompanyDTO(companyId);
            } else {
                invoicingQueue = customerService.getCustomersForInvoicingDeskDTO();
            }
            return Response.ok(invoicingQueue).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving invoicing desk queue", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving invoicing desk queue: " + e.getMessage())
                    .build();
        }
    }

    // ✅ UPDATED: Added companyId parameter
    @GET
    @Path("/store-clerk")
    public Response getStoreClerkQueue(@QueryParam("companyId") Long companyId) {
        try {
            List<CustomerDTO> storeClerkQueue;
            if (companyId != null) {
                storeClerkQueue = customerService.getCustomersForStoreClerkByCompanyDTO(companyId);
            } else {
                storeClerkQueue = customerService.getCustomersForStoreClerkDTO();
            }
            return Response.ok(storeClerkQueue).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving store clerk queue", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving store clerk queue: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Clears a customer from the system, updating their status to SERVED.
     * Accessible via PUT /api/customers/clearance/{id}
     */
    @PUT
    @Path("/clearance/{id}")
    public Response clearCustomer(@PathParam("id") Long id) {
        try {
            Customer clearedCustomer = customerService.clearCustomer(id);
            CustomerDTO clearedCustomerDTO = new CustomerDTO(clearedCustomer);
            return Response.ok(clearedCustomerDTO).build();
        } catch (RuntimeException e) {
            // Catch specific exceptions thrown by the service layer
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(e.getMessage())
                        .build();
            }
            logger.log(Level.SEVERE, "Error clearing customer with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error clearing customer: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error clearing customer with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    // ✅ UPDATED: Added companyId parameter to total-times endpoint
    @GET
    @Path("/total-times")
    public Response getCustomersWithTotalTimes(@QueryParam("companyId") Long companyId) {
        try {
            List<CustomerDTO> customers;
            if (companyId != null) {
                customers = customerService.findServedCustomersByCompanyDTO(companyId);
            } else {
                customers = customerService.findAllDTO();
                
                // Filter only served customers with total time (for backward compatibility)
                customers = customers.stream()
                        .filter(c -> c.getStatus() == Customer.Status.SERVED && c.getTotalTimeInSystem() != null)
                        .collect(Collectors.toList());
            }
                    
            return Response.ok(customers).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving customers with total times", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving total times: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Forwards a customer to the next service point, updating current role and status to FORWARDED
     * Accessible via PUT /api/customers/{id}/forward?currentServicePoint=RECEPTIONIST&nextServicePoint=SALES_ORDER_DESK
     */
    @PUT
    @Path("/{id}/forward")
    public Response forwardCustomer(
            @PathParam("id") Long id,
            @QueryParam("currentServicePoint") String currentServicePointStr,
            @QueryParam("nextServicePoint") String nextServicePointStr) {
        
        try {
            // Convert strings to enums
            Customer.ServicePoint currentServicePoint = Customer.ServicePoint.valueOf(currentServicePointStr);
            Customer.ServicePoint nextServicePoint = Customer.ServicePoint.valueOf(nextServicePointStr);
            
            // Call the service to forward the customer
            Customer forwardedCustomer = customerService.forwardCustomer(id, currentServicePoint, nextServicePoint);
            CustomerDTO forwardedCustomerDTO = new CustomerDTO(forwardedCustomer);
            
            return Response.ok(forwardedCustomerDTO).build();
            
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid service point", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid service point. Valid values: GATE_ATTENDANT, RECEPTIONIST, SALES_ORDER_DESK, INVOICING_DESK, STORE_CLERK")
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(e.getMessage())
                        .build();
            }
            logger.log(Level.SEVERE, "Error forwarding customer with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error forwarding customer: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error forwarding customer with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Starts a service for a customer at a specific service point
     * Accessible via POST /api/customers/{id}/start-service?servicePoint=RECEPTIONIST
     */
    @POST
    @Path("/{id}/start-service")
    public Response startService(
            @PathParam("id") Long customerId,
            @QueryParam("servicePoint") String servicePointStr) {
        
        try {
            // Convert string to enum
            Customer.ServicePoint servicePoint = Customer.ServicePoint.valueOf(servicePointStr);
            
            // Call the service to start the service
            customerService.startService(customerId, servicePoint);
            
            return Response.ok()
                    .entity("Service started successfully for customer " + customerId + " at " + servicePoint)
                    .build();
                    
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid service point: " + servicePointStr, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid service point: " + servicePointStr + 
                           ". Valid values: GATE_ATTENDANT, RECEPTIONIST, SALES_ORDER_DESK, INVOICING_DESK, STORE_CLERK")
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(e.getMessage())
                        .build();
            }
            logger.log(Level.SEVERE, "Error starting service for customer: " + customerId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error starting service: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error starting service for customer: " + customerId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Completes a service for a customer at a specific service point
     * Accessible via POST /api/customers/{id}/complete-service?servicePoint=GATE_ATTENDANT
     */
    @POST
    @Path("/{id}/complete-service")
    public Response completeService(
            @PathParam("id") Long customerId,
            @QueryParam("servicePoint") String servicePointStr,
            ServiceCompletionRequest completionRequest) {
        
        try {
            // Convert string to enum
            Customer.ServicePoint servicePoint = Customer.ServicePoint.valueOf(servicePointStr);
            
            // Call the service with the timeSpent from frontend
            customerService.completeService(customerId, servicePoint, completionRequest.getTimeSpent());
            
            return Response.ok()
                    .entity("Service completed successfully for customer " + customerId + 
                           " at " + servicePoint + ". Time spent: " + completionRequest.getTimeSpent() + " seconds")
                    .build();
                    
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid service point: " + servicePointStr, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid service point: " + servicePointStr + 
                           ". Valid values: GATE_ATTENDANT, RECEPTIONIST, SALES_ORDER_DESK, INVOICING_DESK, STORE_CLERK")
                    .build();
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(e.getMessage())
                        .build();
            }
            logger.log(Level.SEVERE, "Error completing service for customer: " + customerId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error completing service: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error completing service for customer: " + customerId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    // Add this inner class to handle completion requests
    public static class ServiceCompletionRequest {
        private String notes;
        private String servedBy;
        private Long timeSpent;
        private String completionTime;
        
        // Getters and setters
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public String getServedBy() { return servedBy; }
        public void setServedBy(String servedBy) { this.servedBy = servedBy; }

        public Long getTimeSpent() { return timeSpent; }
        public void setTimeSpent(Long timeSpent) { this.timeSpent = timeSpent; }

        public String getCompletionTime() { return completionTime; }
        public void setCompletionTime(String completionTime) { this.completionTime = completionTime; }
    }
}