// src/components/StoreClerkDashboard.jsx
import React, { useState, useEffect } from 'react';
import '../styles/Dashboard.css';
import logo from '../assets/Serviceman.jpeg';

function StoreClerkDashboard() {
  const [userName, setUserName] = useState('');
  const [userCompanyId, setUserCompanyId] = useState(''); // ✅ ADDED: Company ID state
  const [queue, setQueue] = useState([]);
  const [currentCustomer, setCurrentCustomer] = useState(null);
  const [serviceNotes, setServiceNotes] = useState('');
  const [timer, setTimer] = useState(0);
  const [isTimerRunning, setIsTimerRunning] = useState(false);
  const [error, setError] = useState(null);

  const API_BASE_URL = 'http://localhost:8086/serviceman/api';

  // Effect to get the user's name AND company ID
  useEffect(() => {
    const storedName = localStorage.getItem('userName');
    const storedCompanyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
    
    if (storedName) setUserName(storedName);
    if (storedCompanyId) setUserCompanyId(storedCompanyId);
  }, []);

  // Fetch queue from API - NOW FILTERED BY COMPANY
  const fetchQueue = async () => {
    try {
      const companyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
      
      const response = await fetch(
        `${API_BASE_URL}/customers/store-clerk?companyId=${companyId}` // ✅ ADD COMPANY FILTER
      );
      
      if (!response.ok) throw new Error('Failed to fetch queue');
      const data = await response.json();
      if (!currentCustomer) {
        setQueue(data);
      }
      setError(null);
    } catch (error) {
      console.error('Error fetching queue:', error);
      setError('Failed to load customer queue. Please try again later.');
    }
  };

  // Effect to load the queue from API
  useEffect(() => {
    fetchQueue(); // Initial fetch
    const intervalId = setInterval(fetchQueue, 5000); // Poll every 5 seconds
    return () => clearInterval(intervalId);
  }, [currentCustomer]);

  // Timer logic
  useEffect(() => {
    let intervalId;
    if (isTimerRunning) {
      intervalId = setInterval(() => {
        setTimer((prevTimer) => prevTimer + 1);
      }, 1000);
    }
    return () => clearInterval(intervalId);
  }, [isTimerRunning]);

  const handlePullCustomer = async (customerToServe) => {
    try {
      // Start service via API
      const response = await fetch(
        `${API_BASE_URL}/customers/${customerToServe.id}/start-service?servicePoint=STORE_CLERK`,
        { method: 'POST' }
      );
      
      if (!response.ok) throw new Error('Failed to start service');
      
      setCurrentCustomer(customerToServe);
      setIsTimerRunning(true);
      
      // Update local queue state
      setQueue(prevQueue => prevQueue.filter(customer => customer.id !== customerToServe.id));
      
    } catch (error) {
      console.error('Error starting service:', error);
      alert('Failed to start service. Please try again.');
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsTimerRunning(false);

    try {
      // 1. Complete service via API
      const completeResponse = await fetch(
        `${API_BASE_URL}/customers/${currentCustomer.id}/complete-service?servicePoint=STORE_CLERK`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            notes: serviceNotes,
            servedBy: userName,
            timeSpent: timer,
          }),
        }
      );

      if (!completeResponse.ok) throw new Error('Service completion failed');

      // 2. Forward customer to gate clearance
      const forwardResponse = await fetch(
        `${API_BASE_URL}/customers/${currentCustomer.id}/forward?currentServicePoint=STORE_CLERK&nextServicePoint=GATE_ATTENDANT`,
        { method: 'PUT' }
      );

      if (!forwardResponse.ok) throw new Error('Forwarding failed');
      
      alert(`Service for ${currentCustomer.name} completed! Forwarded to Gate for Clearance. Time spent: ${timer} seconds.`);
      
      // Reset state
      setCurrentCustomer(null);
      setServiceNotes('');
      setTimer(0);
      
      // Refresh queue
      fetchQueue();
      
    } catch (error) {
      console.error('Error:', error);
      alert('Failed to process customer. Please try again.');
      setIsTimerRunning(true);
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="main-container">
      {/* Top Navigation Bar */}
      <div className="top-navbar">
        <div className="navbar-left">
          <div className="system-name">SERVICEMAN</div>
          <div className="system-description">Customer Time Tracking System</div>
        </div>
        <div className="navbar-right">
          <img src={logo} alt="Company Logo" className="logo" />
        </div>
      </div>
      
      <div className="dashboard-container">
        {/* Welcome and Stats Card */}
        <div className="dashboard-form-container">
          <h2 className="welcome-message">
            Welcome, <strong>{userName}</strong>! (Store Clerk)
          </h2>
          
          {/* ✅ ADDED: Company Info Display */}
          {userCompanyId && (
            <div className="company-info">
              Company ID: <strong>{userCompanyId}</strong>
            </div>
          )}
          
          <div className="stats-card">
            <h4>Service Status:</h4>
            <p>
              Customers in Queue: <strong>{queue.length}</strong> | Currently Serving: <strong>{currentCustomer ? currentCustomer.name : 'None'}</strong>
            </p>
          </div>
        </div>

        <div className="dashboard-form-container">
          {currentCustomer ? (
            <>
              <h3>Currently Serving: <strong>{currentCustomer.name}</strong></h3>
              <p>Service Started: {new Date().toLocaleTimeString()}</p>
              <p style={{ color: 'red', fontWeight: 'bold' }}>Time Elapsed: {formatTime(timer)}</p>
              
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label htmlFor="serviceNotes">Service Notes:</label>
                  <textarea 
                    id="serviceNotes" 
                    name="serviceNotes" 
                    value={serviceNotes} 
                    onChange={(e) => setServiceNotes(e.target.value)} 
                    rows="3" 
                    placeholder="Add any relevant notes about this service interaction..."
                  />
                </div>
                <div className="form-group">
                  <label>Next Action:</label>
                  <div className="radio-group-container">
                    <label>
                      <input 
                        type="radio" 
                        name="nextServicePoint" 
                        value="Gate for Clearance" 
                        checked={true} 
                        readOnly
                      />
                      Gate for Clearance
                    </label>
                  </div>
                </div>
                <button type="submit" className="submit-button">Complete Service & Notify Gate</button>
              </form>
            </>
          ) : (
            <div>
              <h3>Your Customer Queue (Ready for Service)</h3>
              {error ? (
                <p className="error-message">{error}</p>
              ) : queue.length > 0 ? (
                <table className="customer-table">
                  <thead>
                    <tr>
                      <th>Customer</th>
                      <th>Service Requested</th>
                      <th>Arrived At</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {queue.map((customer) => (
                      <tr key={customer.id}>
                        <td><strong>{customer.name}</strong></td>
                        <td>{customer.serviceRequested}</td>
                        <td>{customer.createdAt ? new Date(customer.createdAt).toLocaleTimeString() : 'N/A'}</td>
                        <td>
                          <button 
                            onClick={() => handlePullCustomer(customer)}
                            className="acknowledge-button"
                          >
                            Pull Customer
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <p>No customers in your queue.</p>
              )}
            </div>
          )}
        </div>
      </div>
      
      {/* Footer */}
      <div className="footer">
        Powered by Mansoft<br />Infinite Possibilities
      </div>
    </div>
  );
}

export default StoreClerkDashboard;