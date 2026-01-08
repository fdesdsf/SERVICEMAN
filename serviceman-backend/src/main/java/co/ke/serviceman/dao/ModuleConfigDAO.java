package co.ke.serviceman.dao;

import co.ke.serviceman.model.ModuleConfig;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the ModuleConfig entity.
 * Provides standard CRUD and specific query methods.
 */
@Stateless
public class ModuleConfigDAO {

    private static final Logger logger = Logger.getLogger(ModuleConfigDAO.class.getName());

    @PersistenceContext(unitName = "servicemanPU")
    private EntityManager em;

    /**
     * Finds a ModuleConfig entity by its ID.
     * @param id The ID of the entity to find.
     * @return The found ModuleConfig entity, or null if not found.
     */
    public ModuleConfig findById(Long id) {
        try {
            logger.log(Level.FINE, "Finding ModuleConfig by ID: {0}", id);
            return em.find(ModuleConfig.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding ModuleConfig by ID: " + id, e);
            throw new RuntimeException("Database error while finding entity by ID", e);
        }
    }

    /**
     * Finds all ModuleConfig entities.
     * @return A list of all ModuleConfig entities.
     */
    public List<ModuleConfig> findAll() {
        try {
            logger.log(Level.FINE, "Finding all ModuleConfigs");
            return em.createQuery("SELECT m FROM ModuleConfig m", ModuleConfig.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all ModuleConfigs", e);
            throw new RuntimeException("Database error while finding all entities", e);
        }
    }

    /**
     * Persists a new ModuleConfig entity.
     * @param entity The entity to save.
     * @return The persisted entity.
     */
    public ModuleConfig save(ModuleConfig entity) {
        try {
            logger.log(Level.INFO, "Saving ModuleConfig for company ID: {0}", entity.getCompany().getId());
            em.persist(entity);
            logger.log(Level.INFO, "Successfully saved ModuleConfig with ID: {0}", entity.getId());
            return entity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving ModuleConfig", e);
            throw new RuntimeException("Database error while saving entity", e);
        }
    }

    /**
     * Merges and updates an existing ModuleConfig entity.
     * @param entity The entity to update.
     * @return The merged entity.
     */
    public ModuleConfig update(ModuleConfig entity) {
        try {
            logger.log(Level.INFO, "Updating ModuleConfig with ID: {0}", entity.getId());
            ModuleConfig mergedEntity = em.merge(entity);
            logger.log(Level.INFO, "Successfully updated ModuleConfig with ID: {0}", mergedEntity.getId());
            return mergedEntity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating ModuleConfig", e);
            throw new RuntimeException("Database error while updating entity", e);
        }
    }

    /**
     * Deletes a ModuleConfig entity by its ID.
     * @param id The ID of the entity to delete.
     * @return True if the entity was deleted, false if it was not found.
     */
    public boolean delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting ModuleConfig with ID: {0}", id);
            ModuleConfig entity = em.find(ModuleConfig.class, id);
            if (entity != null) {
                em.remove(entity);
                logger.log(Level.INFO, "Successfully deleted ModuleConfig with ID: {0}", id);
                return true;
            } else {
                logger.log(Level.WARNING, "ModuleConfig not found for deletion with ID: {0}", id);
                return false;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting ModuleConfig with ID: " + id, e);
            throw new RuntimeException("Database error while deleting entity", e);
        }
    }

    /**
     * Finds a ModuleConfig entity by the ID of its associated Company.
     * @param companyId The ID of the company.
     * @return The found ModuleConfig, or null if not found.
     */
    public ModuleConfig findByCompanyId(Long companyId) {
        try {
            logger.log(Level.FINE, "Finding ModuleConfig by Company ID: {0}", companyId);
            return em.createQuery("SELECT m FROM ModuleConfig m WHERE m.company.id = :companyId", ModuleConfig.class)
                     .setParameter("companyId", companyId)
                     .getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.FINE, "No ModuleConfig found for Company ID: {0}", companyId);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding ModuleConfig by Company ID: " + companyId, e);
            throw new RuntimeException("Database error while finding module config by company ID", e);
        }
    }
}
        