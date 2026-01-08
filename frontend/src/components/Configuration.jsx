// src/components/Configuration.jsx
import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/Configuration.css';

const modules = [
  'Gate Attendant',
  'Receptionist',
  'Sales Order Desk',
  'Invoicing Desk',
  'Store Clerk'
];

function Configuration() {
  const navigate = useNavigate();

  // State to manage the user's flow: new registration or existing user verification
  const [isExistingUserFlow, setIsExistingUserFlow] = useState(false);

  // States for the new company registration flow
  const [step, setStep] = useState(1);
  const [companyName, setCompanyName] = useState('');
  const [companyId, setCompanyId] = useState(null);
  const [selectedModules, setSelectedModules] = useState([]);
  const [isConfigured, setIsConfigured] = useState(false);

  // States for both flows
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Backend base URL
  const API_BASE = 'http://localhost:8086/serviceman/api';

  useEffect(() => {
    // Check if the configuration flag is present in local storage
    if (localStorage.getItem('isConfigured') === 'true') {
      // If it exists, navigate the user to the login page
      navigate('/login');
    }
  }, [navigate]);

  // Handle new company registration (Step 1 of the original flow)
  const handleCompanySubmit = async (event) => {
    event.preventDefault();
    if (!companyName.trim()) return;

    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_BASE}/companies`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: companyName })
      });

      if (!response.ok) {
        throw new Error(`Failed to create company (${response.status})`);
      }

      const createdCompany = await response.json();
      setCompanyId(createdCompany.id); // save the company ID
      setStep(2); // move to module selection
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Handle module checkbox change (Step 2 of the original flow)
  const handleCheckboxChange = (event) => {
    const moduleName = event.target.name;
    setSelectedModules((prev) =>
      prev.includes(moduleName)
        ? prev.filter((name) => name !== moduleName)
        : [...prev, moduleName]
    );
  };

  // Handle module configuration submission (Step 2 of the original flow)
  const handleModulesSubmit = async (event) => {
    event.preventDefault();
    if (!companyId) return;

    setLoading(true);
    setError('');

    const moduleConfigData = {
      company: { id: companyId },
      gateAttendantEnabled: selectedModules.includes('Gate Attendant'),
      receptionistEnabled: selectedModules.includes('Receptionist'),
      salesOrderDeskEnabled: selectedModules.includes('Sales Order Desk'),
      invoicingDeskEnabled: selectedModules.includes('Invoicing Desk'),
      storeClerkEnabled: selectedModules.includes('Store Clerk'),
    };

    try {
      const response = await fetch(`${API_BASE}/module-config`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(moduleConfigData),
      });

      if (!response.ok) {
        throw new Error(`Failed to save modules (${response.status})`);
      }

      // Set the configuration flag in local storage
      localStorage.setItem('isConfigured', 'true');
      setIsConfigured(true);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Handle existing company verification
  const handleCheckCompanyId = async (event) => {
    event.preventDefault();
    if (!companyId) {
      setError('Please enter your company ID.');
      return;
    }
    setLoading(true);
    setError('');

    try {
      const response = await fetch(`${API_BASE}/companies/${companyId}`, {
        method: 'GET',
      });

      if (response.ok) {
        // Company found, proceed to login
        localStorage.setItem('isConfigured', 'true');
        navigate('/login');
      } else if (response.status === 404) {
        throw new Error('Company not found. Please check your ID.');
      } else {
        throw new Error(`Failed to verify company ID (${response.status})`);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Success screen (after new company registration)
  if (isConfigured) {
    return (
      <div className="config-container">
        <div className="config-success-card">
          <div className="header-section">
            <img src="/serviceman/Serviceman.jpeg" alt="SERVICEMAN Logo" className="logo" />
            <h1 className="title">SERVICEMAN</h1>
          </div>
          <h2>Configuration Complete!</h2>
          <p>
            Company <strong>{companyName}</strong> has been registered with your selected modules.
            You can now proceed to log in.
          </p>
          <Link to="/login" className="login-link">Go to Login</Link>
        </div>
        <div className="footer">
          <p>Powered by: <span className="footer-brand">MANSOFT</span></p>
          <p className="footer-sub">Infinite Possibilities</p>
        </div>
      </div>
    );
  }

  // Render the existing company verification form
  if (isExistingUserFlow) {
    return (
      <div className="config-container">
        <div className="config-card">
          <div className="header-section">
            <img src="/serviceman/Serviceman.jpeg" alt="SERVICEMAN Logo" className="logo" />
            <h1 className="title">SERVICEMAN</h1>
          </div>
          <h2 className="card-title">Company Verification</h2>
          <p className="card-subtitle">
            Please enter your company ID to proceed to login.
          </p>
          <form onSubmit={handleCheckCompanyId} className="config-form">
            <input
              type="text"
              placeholder="Enter company ID"
              value={companyId}
              onChange={(e) => setCompanyId(e.target.value)}
              required
              className="company-input"
            />
            <button type="submit" className="save-button" disabled={loading}>
              {loading ? 'Verifying...' : 'Proceed to Login'}
            </button>
          </form>
          {error && <p className="error">{error}</p>}
          <div className="flow-switch">
            <p>New company? <span onClick={() => setIsExistingUserFlow(false)}>Register here.</span></p>
          </div>
        </div>
        <div className="footer">
          <p>Powered by: <span className="footer-brand">MANSOFT</span></p>
          <p className="footer-sub">Infinite Possibilities</p>
        </div>
      </div>
    );
  }

  // Render the new company registration forms
  if (step === 1) {
    return (
      <div className="config-container">
        <div className="config-card">
          <div className="header-section">
            <img src="/serviceman/Serviceman.jpeg" alt="SERVICEMAN Logo" className="logo" />
            <h1 className="title">SERVICEMAN</h1>
          </div>
          <h2 className="card-title">Company Registration</h2>
          <p className="card-subtitle">Please enter your company name to continue.</p>
          <form onSubmit={handleCompanySubmit} className="config-form">
            <input
              type="text"
              placeholder="Enter company name"
              value={companyName}
              onChange={(e) => setCompanyName(e.target.value)}
              required
              className="company-input"
            />
            <button type="submit" className="save-button" disabled={loading}>
              {loading ? 'Saving...' : 'Continue'}
            </button>
          </form>
          {error && <p className="error">{error}</p>}
          <div className="flow-switch">
            <p>Already registered? <span onClick={() => setIsExistingUserFlow(true)}>Verify your ID.</span></p>
          </div>
        </div>
        <div className="footer">
          <p>Powered by: <span className="footer-brand">MANSOFT</span></p>
          <p className="footer-sub">Infinite Possibilities</p>
        </div>
      </div>
    );
  }

  // Render module selection (Step 2 of new company registration)
  return (
    <div className="config-container">
      <div className="config-card">
        <div className="header-section">
          <img src="/serviceman/Serviceman.jpeg" alt="SERVICEMAN Logo" className="logo" />
          <h1 className="title">SERVICEMAN</h1>
        </div>
        <h2 className="card-title">Company Configuration</h2>
        <p className="card-subtitle">
          Select the service points for <strong>{companyName}</strong>.
        </p>
        <form onSubmit={handleModulesSubmit} className="config-form">
          <div className="modules-list">
            {modules.map((module) => (
              <div key={module} className="checkbox-group">
                <input
                  type="checkbox"
                  id={module}
                  name={module}
                  checked={selectedModules.includes(module)}
                  onChange={handleCheckboxChange}
                />
                <label htmlFor={module}>{module}</label>
              </div>
            ))}
          </div>
          <button type="submit" className="save-button" disabled={loading}>
            {loading ? 'Saving...' : 'Save Configuration'}
          </button>
        </form>
        {error && <p className="error">{error}</p>}
      </div>
      <div className="footer">
        <p>Powered by: <span className="footer-brand">MANSOFT</span></p>
        <p className="footer-sub">Infinite Possibilities</p>
      </div>
    </div>
  );
}

export default Configuration;