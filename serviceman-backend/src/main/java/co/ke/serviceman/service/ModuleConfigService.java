package co.ke.serviceman.service;

import co.ke.serviceman.dao.ModuleConfigDAO;
import co.ke.serviceman.model.ModuleConfig;
//import co.ke.serviceman.model.Company;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service layer for the ModuleConfig entity.
 * Handles business logic and transaction management for ModuleConfig operations.
 */
@Stateless
public class ModuleConfigService {

    private static final Logger logger = Logger.getLogger(ModuleConfigService.class.getName());

    @Inject
    private ModuleConfigDAO moduleConfigDAO;

    /**
     * Finds a ModuleConfig entity by its ID.
     * @param id The ID of the entity to find.
     * @return The found ModuleConfig entity.
     */
    public ModuleConfig findById(Long id) {
        try {
            logger.log(Level.INFO, "Finding module config by ID: {0}", id);
            ModuleConfig config = moduleConfigDAO.findById(id);
            if (config == null) {
                logger.log(Level.WARNING, "Module config not found with ID: {0}", id);
                throw new RuntimeException("Module config not found with ID: " + id);
            }
            return config;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding module config by ID: " + id, e);
            throw new RuntimeException("Failed to retrieve module config with ID: " + id, e);
        }
    }

    /**
     * Finds all ModuleConfig entities.
     * @return A list of all ModuleConfig entities.
     */
    public List<ModuleConfig> findAll() {
        try {
            logger.info("Finding all module configs");
            return moduleConfigDAO.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all module configs", e);
            throw new RuntimeException("Failed to retrieve module configs: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a ModuleConfig entity by the ID of its associated Company.
     * @param companyId The ID of the company.
     * @return The found ModuleConfig.
     */
    public ModuleConfig findByCompanyId(Long companyId) {
        try {
            logger.log(Level.INFO, "Finding module config by company ID: {0}", companyId);
            ModuleConfig config = moduleConfigDAO.findByCompanyId(companyId);
            if (config == null) {
                logger.log(Level.WARNING, "Module config not found for company ID: {0}", companyId);
                throw new RuntimeException("Module config not found for company ID: " + companyId);
            }
            return config;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding module config by company ID: " + companyId, e);
            throw new RuntimeException("Failed to retrieve module config by company ID: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new ModuleConfig entity.
     * @param config The entity to save.
     * @return The persisted entity.
     */
    @Transactional
    public ModuleConfig create(ModuleConfig config) {
        try {
            if (config.getCompany() == null || config.getCompany().getId() == null) {
                throw new RuntimeException("Company ID is required to create a module config.");
            }
            logger.log(Level.INFO, "Creating new module config for company ID: {0}", config.getCompany().getId());

            // A company should only have one module configuration.
            ModuleConfig existingConfig = moduleConfigDAO.findByCompanyId(config.getCompany().getId());
            if (existingConfig != null) {
                throw new RuntimeException("Module config already exists for company ID: " + config.getCompany().getId());
            }

            return moduleConfigDAO.save(config);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating module config", e);
            throw new RuntimeException("Failed to create module config: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing ModuleConfig entity.
     * @param config The entity to update.
     * @return The updated entity.
     */
    @Transactional
    public ModuleConfig update(ModuleConfig config) {
        try {
            logger.log(Level.INFO, "Updating module config with ID: {0}", config.getId());

            ModuleConfig existingConfig = moduleConfigDAO.findById(config.getId());
            if (existingConfig == null) {
                throw new RuntimeException("Module config not found with ID: " + config.getId());
            }

            return moduleConfigDAO.update(config);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating module config with ID: " + config.getId(), e);
            throw new RuntimeException("Failed to update module config: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a ModuleConfig entity by its ID.
     * @param id The ID of the entity to delete.
     * @return True if the entity was deleted, false otherwise.
     */
    @Transactional
    public boolean delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting module config with ID: {0}", id);
            boolean deleted = moduleConfigDAO.delete(id);
            if (!deleted) {
                logger.log(Level.WARNING, "Module config not found for deletion with ID: {0}", id);
            }
            return deleted;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting module config with ID: " + id, e);
            throw new RuntimeException("Failed to delete module config: " + id, e);
        }
    }
}
