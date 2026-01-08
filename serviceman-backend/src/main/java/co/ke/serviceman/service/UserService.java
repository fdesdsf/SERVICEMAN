package co.ke.serviceman.service;

import co.ke.serviceman.dao.UserDAO;
import co.ke.serviceman.model.AuthResponse;
import co.ke.serviceman.model.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    @Inject
    private UserDAO userDAO;

    public List<User> findAll() {
        try {
            logger.info("Finding all users");
            return userDAO.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all users", e);
            throw new RuntimeException("Failed to retrieve users: " + e.getMessage(), e);
        }
    }

    public User findById(Long id) {
        try {
            logger.log(Level.INFO, "Finding user by ID: {0}", id);
            User user = userDAO.findById(id);
            if (user == null) {
                logger.log(Level.WARNING, "User not found with ID: {0}", id);
                throw new RuntimeException("User not found with ID: " + id);
            }
            return user;
        } catch (RuntimeException e) {
            // Re-throw our custom exceptions
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by ID: " + id, e);
            throw new RuntimeException("Failed to retrieve user with ID: " + id, e);
        }
    }

    @Transactional
    public User create(User user) {
        try {
            logger.log(Level.INFO, "Creating new user: {0}", user.getUsername());
            
            // Basic validation
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new RuntimeException("Username is required");
            }
            
            // Check if username already exists
            User existingUser = userDAO.findByUsername(user.getUsername());
            if (existingUser != null) {
                throw new RuntimeException("Username already exists: " + user.getUsername());
            }
            
            userDAO.save(user);
            logger.log(Level.INFO, "User created successfully with ID: {0}", user.getId());
            return user;
        } catch (RuntimeException e) {
            // Re-throw our custom exceptions
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating user: " + user.getUsername(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public User update(User user) {
        try {
            logger.log(Level.INFO, "Updating user with ID: {0}", user.getId());
            
            // Check if user exists
            User existingUser = userDAO.findById(user.getId());
            if (existingUser == null) {
                throw new RuntimeException("User not found with ID: " + user.getId());
            }
            
            // Check if username is being changed to an existing one (if username changed)
            if (!existingUser.getUsername().equals(user.getUsername())) {
                User userWithNewUsername = userDAO.findByUsername(user.getUsername());
                if (userWithNewUsername != null && !userWithNewUsername.getId().equals(user.getId())) {
                    throw new RuntimeException("Username already exists: " + user.getUsername());
                }
            }
            
            userDAO.update(user);
            logger.log(Level.INFO, "User updated successfully with ID: {0}", user.getId());
            return user;
        } catch (RuntimeException e) {
            // Re-throw our custom exceptions
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating user with ID: " + user.getId(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Transactional
    public boolean delete(Long id) {
        try {
            logger.log(Level.INFO, "Deleting user with ID: {0}", id);
            
            User user = userDAO.findById(id);
            if (user == null) {
                logger.log(Level.WARNING, "User not found for deletion with ID: {0}", id);
                return false;
            }
            
            userDAO.delete(id);
            logger.log(Level.INFO, "User deleted successfully with ID: {0}", id);
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
            throw new RuntimeException("Failed to delete user with ID: " + id, e);
        }
    }

    public User findByUsername(String username) {
        try {
            logger.log(Level.INFO, "Finding user by username: {0}", username);
            return userDAO.findByUsername(username);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding user by username: " + username, e);
            throw new RuntimeException("Failed to retrieve user with username: " + username, e);
        }
    }

    // Add this method to your existing UserService class
public AuthResponse authenticate(String username, String password) {
    try {
        logger.log(Level.INFO, "Authenticating user: {0}", username);
        
        // Basic validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return new AuthResponse(false, "Username and password are required");
        }
        
        // Find user by username
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return new AuthResponse(false, "Invalid username or password");
        }
        
        // Check password (in production, use proper password hashing!)
        // For now, we'll do plain text comparison since you don't have hashing
        if (!password.equals(user.getPassword())) {
            return new AuthResponse(false, "Invalid username or password");
        }
        
        // Check if user is active
        if (user.getStatus() != User.Status.ACTIVE) {
            return new AuthResponse(false, "Account is inactive");
        }
        
        logger.log(Level.INFO, "User authenticated successfully: {0}", username);
        return new AuthResponse(true, "Login successful", user);
        
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error during authentication for user: " + username, e);
        return new AuthResponse(false, "Authentication failed: " + e.getMessage());
    }
}

    // Optional: Additional utility methods
    public boolean existsById(Long id) {
        try {
            return userDAO.findById(id) != null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking if user exists with ID: " + id, e);
            throw new RuntimeException("Failed to check user existence", e);
        }
    }

    public boolean existsByUsername(String username) {
        try {
            return userDAO.findByUsername(username) != null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error checking if username exists: " + username, e);
            throw new RuntimeException("Failed to check username existence", e);
        }
    }
}