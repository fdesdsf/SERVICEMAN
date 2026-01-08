package co.ke.serviceman.dao;

import co.ke.serviceman.model.Company;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class CompanyDAO {

    private static final Logger logger = Logger.getLogger(CompanyDAO.class.getName());

    // Inject the EntityManager directly into this class
    @PersistenceContext(unitName = "servicemanPU")
    private EntityManager em;

    public Company findById(Long id) {
        try {
            logger.log(Level.FINE, "Finding Company by ID: {0}", id);
            return em.find(Company.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding Company by ID: " + id, e);
            throw new RuntimeException("Database error while finding entity by ID", e);
        }
    }

    public List<Company> findAll() {
        try {
            logger.log(Level.FINE, "Finding all Companies");
            return em.createQuery("SELECT c FROM Company c", Company.class).getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all Companies", e);
            throw new RuntimeException("Database error while finding all entities", e);
        }
    }

    public Company findByName(String companyName) {
        try {
            logger.log(Level.FINE, "Finding Company by name: {0}", companyName);
            return em.createQuery("SELECT c FROM Company c WHERE c.companyName = :name", Company.class)
                     .setParameter("name", companyName)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void save(Company entity) {
        try {
            logger.log(Level.INFO, "Saving Company");
            em.persist(entity);
            logger.log(Level.INFO, "Successfully saved Company");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving Company", e);
            throw new RuntimeException("Database error while saving entity", e);
        }
    }

    public Company update(Company entity) {
        try {
            logger.log(Level.INFO, "Updating Company");
            Company mergedEntity = em.merge(entity);
            logger.log(Level.INFO, "Successfully updated Company");
            return mergedEntity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating Company", e);
            throw new RuntimeException("Database error while updating entity", e);
        }
    }

    public void delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting Company with ID: {0}", id);
            Company entity = em.find(Company.class, id);
            if (entity != null) {
                em.remove(entity);
                logger.log(Level.INFO, "Successfully deleted Company with ID: {0}", id);
            } else {
                logger.log(Level.WARNING, "Company not found for deletion with ID: {0}", id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting Company with ID: " + id, e);
            throw new RuntimeException("Database error while deleting entity", e);
        }
    }
}
