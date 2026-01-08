import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/Login.css';

function Login() {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    role: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const availableRoles = [
    'ADMIN',
    'GATE_ATTENDANT',
    'RECEPTIONIST',
    'SALES_ORDER_DESK',
    'INVOICING_DESK',
    'STORE_CLERK'
  ];

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleLogin = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);

    if (!formData.username || !formData.password || !formData.role) {
      setError('Please fill in all fields');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch(
        'http://localhost:8086/serviceman/api/auth/login',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: formData.username,
            password: formData.password
          })
        }
      );

      const data = await response.json();

      if (response.ok && data.success && data.user) {
        const userRoleFromBackend = data.user.role;

        // Role verification
        if (userRoleFromBackend !== formData.role) {
          setError('Login failed. The selected role does not match the role associated with your account.');
          setLoading(false);
          return;
        }

        // ✅ STORE COMPANY ID FOR DATA ISOLATION
        localStorage.setItem('userRole', formData.role);
        localStorage.setItem('userName', `${data.user.firstName} ${data.user.lastName}`);
        localStorage.setItem('userData', JSON.stringify(data.user));
        
        // ✅ CRITICAL: Store company ID for filtering customers
        if (data.user.company && data.user.company.id) {
          localStorage.setItem('userCompanyId', data.user.company.id.toString());
          localStorage.setItem('userCompanyName', data.user.company.name);
        } else {
          console.error('Company data not found in user response');
          setError('User company information not found. Please contact administrator.');
          setLoading(false);
          return;
        }
        
        alert(`Logged in successfully as ${data.user.firstName}!`);
        
        const path = formData.role.toLowerCase().replace(/_/g, '');
        navigate(`/dashboard/${path}`);
      } else {
        setError(data.message || 'Login failed');
      }
    } catch (error) {
      console.error('Login error:', error);
      setError('Network error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-form">
        <div className="header-section">
          <img src="/serviceman/Serviceman.jpeg" alt="SERVICEMAN Logo" className="logo" />
          <h1 className="title">SERVICEMAN</h1>
        </div>
        
        <p>Please log in to access the Customer Time-Tracking System.</p>
        
        {error && (
          <div className="error-message" style={{ color: 'red', marginBottom: '15px' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="username">Username:</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="password">Password:</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="role">Select Your Role:</label>
            <select
              id="role"
              name="role"
              value={formData.role}
              onChange={handleChange}
              required
            >
              <option value="">-- Select Role --</option>
              {availableRoles.map((availableRole) => (
                <option key={availableRole} value={availableRole}>
                  {availableRole}
                </option>
              ))}
            </select>
          </div>
          
          <button
            type="submit"
            className="login-button"
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Log In'}
          </button>
        </form>
        
        <div className="links-container">
          <Link to="/forgotpassword">Forgot Password?</Link>
          <Link to="/register">Register</Link>
        </div>
      </div>
      
      <div className="footer">
        <p>Powered by: <span className="footer-brand">MANSOFT</span></p>
        <p className="footer-sub">Infinite Possibilities</p>
      </div>
    </div>
  );
}

export default Login;