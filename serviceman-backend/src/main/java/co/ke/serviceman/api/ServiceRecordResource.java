package co.ke.serviceman.api;

import co.ke.serviceman.dto.ServiceRecordDTO;
import co.ke.serviceman.model.CustomerStatus;
import co.ke.serviceman.model.ServiceRecord;
import co.ke.serviceman.service.ServiceRecordService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JAX-RS Resource for the ServiceRecord entity.
 * Exposes REST endpoints for managing service records.
 */
@Stateless
@Path("/service-records")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceRecordResource {

    private static final Logger logger = Logger.getLogger(ServiceRecordResource.class.getName());

    @Inject
    private ServiceRecordService recordService;

    /**
     * Finds a ServiceRecord entity by its ID.
     * @param id The ID of the entity to find.
     * @return The found ServiceRecord entity.
     */
    @GET
    @Path("/{id}")
    public Response getRecord(@PathParam("id") Long id) {
        try {
            ServiceRecordDTO record = recordService.findByIdDTO(id);
            return Response.ok(record).build();
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Service record not found with ID: {0}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving service record with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve service record: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Finds all ServiceRecord entities.
     * @return A list of all ServiceRecord entities.
     */
    @GET
    public Response findAll() {
        try {
            List<ServiceRecordDTO> records = recordService.findAllDTO();
            return Response.ok(records).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving all service records", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve all service records: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Finds a list of ServiceRecords by the ID of its associated Customer.
     * @param customerId The ID of the customer.
     * @return A list of ServiceRecords for the given customer.
     */
    @GET
    @Path("/customer/{customerId}")
    public Response findByCustomerId(@PathParam("customerId") Long customerId) {
        try {
            List<ServiceRecordDTO> records = recordService.findByCustomerIdDTO(customerId);
            return Response.ok(records).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving service records by customer ID: " + customerId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve service records by customer ID: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Finds a list of ServiceRecords by their status.
     * @param status The status of the service record.
     * @return A list of ServiceRecords with the given status.
     */
    @GET
    @Path("/status/{status}")
    public Response findByStatus(@PathParam("status") CustomerStatus status) {
        try {
            List<ServiceRecordDTO> records = recordService.findByStatusDTO(status);
            return Response.ok(records).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving service records by status: " + status, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve service records by status: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Finds service records by date for admin reporting
     * Accessible via GET /api/service-records/by-date?date=2024-01-15
     */
    @GET
    @Path("/by-date")
    public Response getServiceRecordsByDate(
            @QueryParam("date") String dateStr,
            @QueryParam("companyId") Long companyId) {  // ✅ ADDED COMPANY ID PARAMETER
        try {
            logger.log(Level.INFO, "Finding service records for date: {0} and company: {1}", 
                      new Object[]{dateStr, companyId});
            
            List<ServiceRecordDTO> records;
            if (companyId != null) {
                records = recordService.findByDateAndCompanyDTO(dateStr, companyId); // ✅ COMPANY FILTERED
            } else {
                records = recordService.findByDateDTO(dateStr); // ✅ BACKWARD COMPATIBLE
            }
            
            return Response.ok(records).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving service records for date: " + dateStr, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving service records: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Gets daily customer count trends for the last N days
     * Accessible via GET /api/service-records/daily-trends?days=30
     */
    @GET
    @Path("/daily-trends")
    public Response getDailyTrends(
            @QueryParam("days") @DefaultValue("30") int days,
            @QueryParam("companyId") Long companyId) {  // ✅ ADDED COMPANY ID PARAMETER
        try {
            logger.log(Level.INFO, "Getting daily trends for last {0} days and company: {1}", 
                      new Object[]{days, companyId});
            
            List<Object[]> trendsData;
            if (companyId != null) {
                trendsData = recordService.getDailyTrendsByCompanyDTO(days, companyId); // ✅ COMPANY FILTERED
            } else {
                trendsData = recordService.getDailyTrendsDTO(days); // ✅ BACKWARD COMPATIBLE
            }
            
            // Convert to frontend-friendly format
            List<DailyTrendDTO> trends = new ArrayList<>();
            for (Object[] data : trendsData) {
                trends.add(new DailyTrendDTO(
                    data[0].toString(), // date
                    ((Number) data[1]).intValue() // count
                ));
            }
            
            return Response.ok(trends).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving daily trends", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving daily trends: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Gets service statistics by role for a specific date
     * Accessible via GET /api/service-records/stats-by-role?date=2024-01-15
     */
    @GET
    @Path("/stats-by-role")
    public Response getStatsByRole(
            @QueryParam("date") String dateStr,
            @QueryParam("companyId") Long companyId) {  // ✅ ADDED COMPANY ID PARAMETER
        try {
            logger.log(Level.INFO, "Getting service statistics by role for date: {0} and company: {1}", 
                      new Object[]{dateStr, companyId});
            
            List<Object[]> statsData;
            if (companyId != null) {
                statsData = recordService.getStatsByRoleAndCompanyDTO(dateStr, companyId); // ✅ COMPANY FILTERED
            } else {
                statsData = recordService.getStatsByRoleDTO(dateStr); // ✅ BACKWARD COMPATIBLE
            }
            
            // Convert to frontend-friendly format
            List<RoleStatDTO> stats = new ArrayList<>();
            for (Object[] data : statsData) {
                stats.add(new RoleStatDTO(
                    data[0].toString(), // role
                    ((Number) data[1]).intValue(), // count
                    ((Number) data[2]).longValue() // total time
                ));
            }
            
            return Response.ok(stats).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving statistics by role", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving statistics: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Creates a new ServiceRecord entity.
     * @param record The entity to save.
     * @return The created entity with a 201 Created status.
     */
    @POST
    public Response createRecord(ServiceRecord record) {
        try {
            ServiceRecord createdRecord = recordService.create(record);
            ServiceRecordDTO createdRecordDTO = new ServiceRecordDTO(createdRecord);
            return Response.status(Response.Status.CREATED)
                    .entity(createdRecordDTO)
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating service record", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create service record: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Updates an existing ServiceRecord entity.
     * @param id The ID of the entity to update.
     * @param record The entity with updated fields.
     * @return The updated entity.
     */
    @PUT
    @Path("/{id}")
    public Response updateRecord(@PathParam("id") Long id, ServiceRecord record) {
        try {
            record.setId(id);
            ServiceRecord updatedRecord = recordService.update(record);
            ServiceRecordDTO updatedRecordDTO = new ServiceRecordDTO(updatedRecord);
            return Response.ok(updatedRecordDTO).build();
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Failed to update service record: {0}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating service record with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update service record: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Deletes a ServiceRecord entity by its ID.
     * @param id The ID of the entity to delete.
     * @return A 204 No Content status if successful.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteRecord(@PathParam("id") Long id) {
        try {
            boolean deleted = recordService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Service record not found with id: " + id)
                        .build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting service record with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete service record: " + e.getMessage())
                    .build();
        }
    }

    // DTO classes for admin reporting
    public static class DailyTrendDTO {
        private String date;
        private int customers;
        
        public DailyTrendDTO() {}
        
        public DailyTrendDTO(String date, int customers) {
            this.date = date;
            this.customers = customers;
        }
        
        // Getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public int getCustomers() { return customers; }
        public void setCustomers(int customers) { this.customers = customers; }
    }

    public static class RoleStatDTO {
        private String role;
        private int count;
        private long totalTime;
        
        public RoleStatDTO() {}
        
        public RoleStatDTO(String role, int count, long totalTime) {
            this.role = role;
            this.count = count;
            this.totalTime = totalTime;
        }
        
        // Getters and setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public long getTotalTime() { return totalTime; }
        public void setTotalTime(long totalTime) { this.totalTime = totalTime; }
    }
}