package co.ke.serviceman.dao;

import co.ke.serviceman.model.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class UserDAO {

    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    // Inject the EntityManager directly, eliminating the need for BaseDAO
    @PersistenceContext(unitName = "servicemanPU")
    private EntityManager em;

    // --- Core CRUD methods (formerly in BaseDAO) ---

    public User findById(Long id) {
    try {
        logger.log(Level.FINE, "Finding user by ID: {0}", id);
        
        // ✅ UPDATED: Eagerly fetch company when finding by ID
        String jpql = "SELECT u FROM User u LEFT JOIN FETCH u.company WHERE u.id = :id";
        
        return em.createQuery(jpql, User.class)
                 .setParameter("id", id)
                 .getSingleResult();
    } catch (NoResultException e) {
        logger.log(Level.FINE, "No user found with ID: {0}", id);
        return null;
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error finding user by ID: " + id, e);
        throw new RuntimeException("Database error while finding user by ID", e);
    }
}

    public List<User> findAll() {
    try {
        logger.log(Level.FINE, "Finding all users");
        
        // ✅ UPDATED: Eagerly fetch company for all users
        String jpql = "SELECT u FROM User u LEFT JOIN FETCH u.company";
        
        return em.createQuery(jpql, User.class).getResultList();
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error finding all users", e);
        throw new RuntimeException("Database error while finding all users", e);
    }
}

    public User save(User entity) {
        try {
            logger.log(Level.INFO, "Saving user");
            em.persist(entity);
            logger.log(Level.INFO, "Successfully saved user");
            return entity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving user", e);
            throw new RuntimeException("Database error while saving user", e);
        }
    }

    public User update(User entity) {
        try {
            logger.log(Level.INFO, "Updating user with ID: {0}", entity.getId());
            User mergedEntity = em.merge(entity);
            logger.log(Level.INFO, "Successfully updated user");
            return mergedEntity;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating user", e);
            throw new RuntimeException("Database error while updating user", e);
        }
    }

    public void delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting user with ID: {0}", id);
            User entity = em.find(User.class, id);
            if (entity != null) {
                em.remove(entity);
                logger.log(Level.INFO, "Successfully deleted user with ID: {0}", id);
            } else {
                logger.log(Level.WARNING, "User not found for deletion with ID: {0}", id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
            throw new RuntimeException("Database error while deleting user", e);
        }
    }

    // --- Original methods you provided ---

    public User findByUsername(String username) {
    try {
        logger.log(Level.FINE, "Finding user by username: {0}", username);
        
        // ✅ UPDATED: Use JOIN FETCH to eagerly load company data
        String jpql = "SELECT u FROM User u LEFT JOIN FETCH u.company WHERE u.username = :username";
        
        return em.createQuery(jpql, User.class)
                 .setParameter("username", username)
                 .getSingleResult();
    } catch (NoResultException e) {
        logger.log(Level.FINE, "No user found with username: {0}", username);
        return null;
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error finding user by username: " + username, e);
        throw new RuntimeException("Database error while finding user by username", e);
    }
}

    public User findByEmail(String email) {
        try {
            logger.log(Level.FINE, "Finding user by email: {0}", email);
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                     .setParameter("email", email)
                     .getSingleResult();
        } catch (NoResultException e) {
            logger.log(Level.FINE, "No user found with email: {0}", email);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by email: " + email, e);
            throw new RuntimeException("Database error while finding user by email", e);
        }
    }

    public List<User> findByActiveStatus(boolean active) {
        try {
            logger.log(Level.FINE, "Finding users by active status: {0}", active);
            return em.createQuery("SELECT u FROM User u WHERE u.active = :active", User.class)
                     .setParameter("active", active)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding users by active status: " + active, e);
            throw new RuntimeException("Database error while finding users by active status", e);
        }
    }

    public List<User> findByRole(String role) {
        try {
            logger.log(Level.FINE, "Finding users by role: {0}", role);
            return em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                     .setParameter("role", role)
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding users by role: " + role, e);
            throw new RuntimeException("Database error while finding users by role", e);
        }
    }

    public boolean usernameExists(String username) {
        try {
            logger.log(Level.FINE, "Checking if username exists: {0}", username);
            Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                           .setParameter("username", username)
                           .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking if username exists: " + username, e);
            throw new RuntimeException("Database error while checking username existence", e);
        }
    }

    public boolean emailExists(String email) {
        try {
            logger.log(Level.FINE, "Checking if email exists: {0}", email);
            Long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                           .setParameter("email", email)
                           .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking if email exists: " + email, e);
            throw new RuntimeException("Database error while checking email existence", e);
        }
    }

    public List<User> searchUsers(String searchTerm) {
        try {
            logger.log(Level.FINE, "Searching users with term: {0}", searchTerm);
            return em.createQuery("SELECT u FROM User u WHERE " +
                                  "LOWER(u.username) LIKE LOWER(:searchTerm) OR " +
                                  "LOWER(u.email) LIKE LOWER(:searchTerm) OR " +
                                  "LOWER(u.firstName) LIKE LOWER(:searchTerm) OR " +
                                  "LOWER(u.lastName) LIKE LOWER(:searchTerm)", User.class)
                     .setParameter("searchTerm", "%" + searchTerm + "%")
                     .getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching users with term: " + searchTerm, e);
            throw new RuntimeException("Database error while searching users", e);
        }
    }

    public List<User> findAllPaginated(int firstResult, int maxResults) {
        try {
            logger.log(Level.FINE, "Finding paginated users: {0} to {1}", new Object[]{firstResult, maxResults});
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u ORDER BY u.id", User.class);
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving paginated users", e);
            throw new RuntimeException("Database error while retrieving paginated users", e);
        }
    }

    public long countAll() {
        try {
            logger.log(Level.FINE, "Counting all users");
            return em.createQuery("SELECT COUNT(u) FROM User u", Long.class)
                     .getSingleResult();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting users", e);
            throw new RuntimeException("Database error while counting users", e);
        }
    }
}
