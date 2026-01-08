import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/Login.css'; // Re-use the same styles

function ForgotPassword() {
  const handlePasswordReset = (event) => {
    event.preventDefault();
    alert('If your email is in our system, a password reset link has been sent.');
    // In a real application, you would send a reset email here.
  };

  return (
    <div className="login-container">
      <div className="login-form">
        <div className="header-section">
          <img src="/serviceman/Serviceman.jpeg" alt="SERVICEMAN Logo" className="logo" />
          <h1 className="title">Forgot Password</h1>
        </div>
        
        <p>Enter your email and we'll send you a link to reset your password.</p>
        <form onSubmit={handlePasswordReset}>
          <div className="form-group">
            <label htmlFor="email">Email Address:</label>
            <input type="email" id="email" />
          </div>
          <button type="submit" className="login-button">Send Reset Link</button>
        </form>
        
        <div className="links-container">
          <Link to="/">Return to Login</Link>
        </div>
      </div>
      
      <div className="footer">
        <p>Powered by: <span className="footer-brand">MANSOFT</span></p>
        <p className="footer-sub">Infinite Possibilities</p>
      </div>
    </div>
  );
}

export default ForgotPassword;