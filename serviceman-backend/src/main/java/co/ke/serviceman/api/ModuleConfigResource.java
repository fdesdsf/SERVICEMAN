package co.ke.serviceman.api;

import co.ke.serviceman.model.ModuleConfig;
import co.ke.serviceman.service.ModuleConfigService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JAX-RS Resource for the ModuleConfig entity.
 * Exposes REST endpoints for managing module configurations.
 */
@Stateless
@Path("/module-config")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ModuleConfigResource {

    private static final Logger logger = Logger.getLogger(ModuleConfigResource.class.getName());

    @Inject
    private ModuleConfigService moduleConfigService;

    /**
     * Finds a ModuleConfig entity by its ID.
     * @param id The ID of the entity to find.
     * @return The found ModuleConfig entity.
     */
    @GET
    @Path("/{id}")
    public Response getModuleConfigById(@PathParam("id") Long id) {
        try {
            ModuleConfig config = moduleConfigService.findById(id);
            return Response.ok(config).build();
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Module config not found with ID: {0}", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving module config with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve module config: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Finds all ModuleConfig entities.
     * @return A list of all ModuleConfig entities.
     */
    @GET
    public Response findAll() {
        try {
            List<ModuleConfig> configs = moduleConfigService.findAll();
            return Response.ok(configs).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving all module configs", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve all module configs: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Finds a ModuleConfig entity by the ID of its associated Company.
     * @param companyId The ID of the company.
     * @return The found ModuleConfig.
     */
    @GET
    @Path("/company/{companyId}")
    public Response getModuleConfigByCompanyId(@PathParam("companyId") Long companyId) {
        try {
            ModuleConfig config = moduleConfigService.findByCompanyId(companyId);
            return Response.ok(config).build();
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Module config not found for company ID: {0}", companyId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving module config by company ID: " + companyId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve module config: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Creates a new ModuleConfig entity.
     * @param config The entity to save.
     * @return The created entity with a 201 Created status.
     */
    @POST
    public Response createModuleConfig(ModuleConfig config) {
        try {
            ModuleConfig createdConfig = moduleConfigService.create(config);
            return Response.status(Response.Status.CREATED)
                    .entity(createdConfig)
                    .build();
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Failed to create module config: {0}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating module config", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to create module config: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Updates an existing ModuleConfig entity.
     * @param id The ID of the entity to update.
     * @param config The entity with updated fields.
     * @return The updated entity.
     */
    @PUT
    @Path("/{id}")
    public Response updateModuleConfig(@PathParam("id") Long id, ModuleConfig config) {
        try {
            config.setId(id);
            ModuleConfig updatedConfig = moduleConfigService.update(config);
            return Response.ok(updatedConfig).build();
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Failed to update module config: {0}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating module config with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to update module config: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Deletes a ModuleConfig entity by its ID.
     * @param id The ID of the entity to delete.
     * @return A 204 No Content status if successful.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteModuleConfig(@PathParam("id") Long id) {
        try {
            boolean deleted = moduleConfigService.delete(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Module config not found with id: " + id)
                        .build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting module config with ID: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete module config: " + e.getMessage())
                    .build();
        }
    }
}
