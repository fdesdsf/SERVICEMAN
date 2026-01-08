// src/components/ReceptionistDashboard.jsx
import React, { useState, useEffect } from 'react';
import '../styles/Dashboard.css';
import logo from '../assets/Serviceman.jpeg';

function ReceptionistDashboard() {
  const [queue, setQueue] = useState([]);
  const [currentCustomer, setCurrentCustomer] = useState(null);
  const [notes, setNotes] = useState('');
  const [nextServicePoint, setNextServicePoint] = useState('');
  const [timer, setTimer] = useState(0);
  const [isTimerRunning, setIsTimerRunning] = useState(false);
  const [userName, setUserName] = useState('');
  const [userCompanyId, setUserCompanyId] = useState(''); // ✅ ADDED: Company ID state

  // Base URL for the API.
  const API_BASE_URL = 'http://localhost:8086/serviceman/api'; 

  // Effect to get the user's name AND company ID from localStorage
  useEffect(() => {
    const storedName = localStorage.getItem('userName');
    const storedCompanyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
    
    if (storedName) setUserName(storedName);
    if (storedCompanyId) setUserCompanyId(storedCompanyId);
  }, []);

  // Effect to fetch the queue from the API - NOW FILTERED BY COMPANY
  const fetchQueue = async () => {
    if (!currentCustomer) {
      try {
        const companyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
        
        const response = await fetch(
          `${API_BASE_URL}/customers/receptionist?companyId=${companyId}` // ✅ ADD COMPANY FILTER
        );
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setQueue(data);
      } catch (error) {
        console.error("Failed to fetch customer queue:", error);
        // Optional: set an error state to display a message to the user
      }
    }
  };

  // Effect to load the queue from API
  useEffect(() => {
    fetchQueue(); // Initial fetch
    const intervalId = setInterval(fetchQueue, 5000); // Poll for updates every 5 seconds
    return () => clearInterval(intervalId); // Cleanup on component unmount
  }, [currentCustomer]);

  // Effect to handle the timer
  useEffect(() => {
    let intervalId;
    if (isTimerRunning) {
      intervalId = setInterval(() => {
        setTimer((prevTimer) => prevTimer + 1);
      }, 1000);
    }
    return () => clearInterval(intervalId);
  }, [isTimerRunning]);

  const handleStartService = async (customerToServe) => {
    try {
      // 1. FIRST call backend to start service (set start_time in database)
      const response = await fetch(
        `${API_BASE_URL}/customers/${customerToServe.id}/start-service?servicePoint=RECEPTIONIST`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          }
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to start service: ${response.status} - ${errorText}`);
      }

      // 2. ONLY THEN update UI state and start timer
      setCurrentCustomer(customerToServe);
      setIsTimerRunning(true);
      
      console.log(`Service started for customer: ${customerToServe.name}`);
      
    } catch (error) {
      console.error("Error starting service:", error);
      alert("Failed to start service. Please try again.");
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsTimerRunning(false);

    // Determine the next service point code
    let nextServicePointCode;
    switch (nextServicePoint) {
      case 'Sales Order Desk': nextServicePointCode = 'SALES_ORDER_DESK'; break;
      case 'Invoicing Desk': nextServicePointCode = 'INVOICING_DESK'; break;
      case 'Store Clerk': nextServicePointCode = 'STORE_CLERK'; break;
      case 'Gate for Clearance': nextServicePointCode = 'GATE_ATTENDANT'; break;
      default: return;
    }

    try {
      // 1. Send completion data WITH the timer value
      const completeResponse = await fetch(
        `${API_BASE_URL}/customers/${currentCustomer.id}/complete-service?servicePoint=RECEPTIONIST`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            notes: notes,
            servedBy: userName,
            timeSpent: timer, // ✅ JUST SEND THE FRONTEND TIMER VALUE
          }),
        }
      );

      if (!completeResponse.ok) throw new Error('Service completion failed');

      // 2. Forward customer
      const forwardResponse = await fetch(
        `${API_BASE_URL}/customers/${currentCustomer.id}/forward?currentServicePoint=RECEPTIONIST&nextServicePoint=${nextServicePointCode}`,
        { method: 'PUT', headers: { 'Content-Type': 'application/json' } }
      );

      if (!forwardResponse.ok) throw new Error('Forwarding failed');
      
      alert(`Service completed! Time spent: ${formatTime(timer)}`);

    } catch (error) {
      console.error("Error:", error);
      alert("Failed to process customer.");
    }

    // Reset UI
    setCurrentCustomer(null);
    setNotes('');
    setNextServicePoint('');
    setTimer(0);
    
    // Refresh queue after completion
    fetchQueue();
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
        <div className="dashboard-form-container">
          <div className="timer-display">
            Time: {formatTime(timer)}
          </div>

          {/* Welcome message */}
          <h2 className="welcome-message">
            Welcome, <strong>{userName}</strong>!
          </h2>
          
          {/* ✅ ADDED: Company Info Display */}
          {userCompanyId && (
            <div className="company-info">
              Company ID: <strong>{userCompanyId}</strong>
            </div>
          )}
          
          <h2 className="dashboard-title">Receptionist Dashboard</h2>
          
          {currentCustomer ? (
            <>
              <h3>Currently Serving: <strong>{currentCustomer.name}</strong></h3>
              <p>Service Started: {currentCustomer.createdAt ? new Date(currentCustomer.createdAt).toLocaleString() : 'N/A'}</p>
              <p style={{ color: 'red', fontWeight: 'bold' }}>Time Elapsed: {formatTime(timer)}</p>
              
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label htmlFor="notes">Service Notes:</label>
                  <textarea 
                    id="notes" 
                    name="notes" 
                    value={notes} 
                    onChange={(e) => setNotes(e.target.value)} 
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
                        value="Sales Order Desk" 
                        checked={nextServicePoint === 'Sales Order Desk'} 
                        onChange={(e) => setNextServicePoint(e.target.value)} 
                        required 
                      />
                      Sales Order Desk
                    </label>
                    <label>
                      <input 
                        type="radio" 
                        name="nextServicePoint" 
                        value="Invoicing Desk" 
                        checked={nextServicePoint === 'Invoicing Desk'} 
                        onChange={(e) => setNextServicePoint(e.target.value)} 
                        required 
                      />
                      Invoicing Desk
                    </label>
                    <label>
                      <input 
                        type="radio" 
                        name="nextServicePoint" 
                        value="Store Clerk" 
                        checked={nextServicePoint === 'Store Clerk'} 
                        onChange={(e) => setNextServicePoint(e.target.value)} 
                        required 
                      />
                      Store Clerk
                    </label>
                    <label>
                      <input 
                        type="radio" 
                        name="nextServicePoint" 
                        value="Gate for Clearance" 
                        checked={nextServicePoint === 'Gate for Clearance'} 
                        onChange={(e) => setNextServicePoint(e.target.value)} 
                        required 
                      />
                      Gate for Clearance
                    </label>
                  </div>
                </div>
                <button type="submit" className="submit-button">Complete Service & Notify Next Desk</button>
              </form>
            </>
          ) : (
            <div>
              <h3>Incoming Customers (Pending Acknowledgment)</h3>
              {queue.length > 0 ? (
                <table className="customer-table">
                  <thead>
                    <tr>
                      <th>Customer</th>
                      <th>Service Req.</th>
                      <th>Entered At</th>
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
                            onClick={() => handleStartService(customer)}
                            className="acknowledge-button"
                          >
                            Acknowledge
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <p>No customers in the queue.</p>
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

export default ReceptionistDashboard;