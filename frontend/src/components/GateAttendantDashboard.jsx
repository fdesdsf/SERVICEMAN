// src/components/GateAttendantDashboard.jsx
import React, { useState, useEffect } from 'react';
import '../styles/Dashboard.css';
import logo from '../assets/Serviceman.jpeg';

function GateAttendantDashboard() {
    const [customer, setCustomer] = useState({
        name: '',
        nationalId: '',
        customerType: '',
        serviceRequested: '',
        status: 'IN_QUEUE',
        currentRole: 'GATE_ATTENDANT',
        nextServicePoint: 'RECEPTIONIST',
        // ❌ REMOVED: companyId from here - we'll use stored company ID
    });
    const [timer, setTimer] = useState(0);
    const [isRunning, setIsRunning] = useState(true);
    const [userName, setUserName] = useState('');
    const [userCompanyId, setUserCompanyId] = useState(''); // ✅ ADDED: Store company ID
    const [clearanceQueue, setClearanceQueue] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    // Fetch clearance queue from backend - NOW FILTERED BY COMPANY
    const fetchClearanceQueue = async () => {
        try {
            setIsLoading(true);
            const companyId = localStorage.getItem('userCompanyId'); // ✅ GET STORED COMPANY ID
            
            const response = await fetch(
                `http://localhost:8086/serviceman/api/customers/clearance?companyId=${companyId}` // ✅ ADD COMPANY FILTER
            );

            if (!response.ok) throw new Error('Failed to fetch clearance queue');

            const data = await response.json();
            setClearanceQueue(data);
            setError(null);
        } catch (err) {
            console.error('Failed to fetch clearance queue:', err);
            setError('Failed to load clearance queue. Please try again later.');
        } finally {
            setIsLoading(false);
        }
    };

    // Load logged in username AND company ID
    useEffect(() => {
        const storedName = localStorage.getItem('userName');
        const storedCompanyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
        
        if (storedName) setUserName(storedName);
        if (storedCompanyId) {
            setUserCompanyId(storedCompanyId);
            console.log('User company ID:', storedCompanyId); // Debug log
        }
    }, []);

    // Poll clearance queue
    useEffect(() => {
        fetchClearanceQueue();
        const intervalId = setInterval(fetchClearanceQueue, 5000);
        return () => clearInterval(intervalId);
    }, []);

    // Timer effect
    useEffect(() => {
        let intervalId;
        if (isRunning) {
            intervalId = setInterval(() => {
                setTimer((prevTimer) => prevTimer + 1);
            }, 1000);
        }
        return () => clearInterval(intervalId);
    }, [isRunning]);

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setCustomer((prevCustomer) => ({
            ...prevCustomer,
            [name]: value,
        }));
    };

    // Helper function to handle error responses (both JSON and text)
    const handleErrorResponse = async (response) => {
        const contentType = response.headers.get('content-type');
        let errorData;
        
        if (contentType && contentType.includes('application/json')) {
            errorData = await response.json();
        } else {
            const errorText = await response.text();
            errorData = { message: errorText };
        }
        
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        
        // ✅ VALIDATE: Check if company ID is available
        if (!userCompanyId) {
            alert('Company information not found. Please log in again.');
            return;
        }
        
        setIsRunning(false);

        try {
            const entryTime = new Date().toLocaleString();

            // Build payload - USE STORED COMPANY ID
            const newCustomer = {
                ...customer,
                status: 'IN_QUEUE',
                currentRole: 'GATE_ATTENDANT',
                nextServicePoint: 'RECEPTIONIST',
                company: { id: parseInt(userCompanyId) } // ✅ USE STORED COMPANY ID
            };

            const response = await fetch(
                'http://localhost:8086/serviceman/api/customers',
                {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(newCustomer)
                }
            );

            // ✅ FIXED: Handle error responses properly
            if (!response.ok) {
                await handleErrorResponse(response);
            }

            const data = await response.json();
            console.log('Customer created:', data);

            // Complete the gate service before forwarding to receptionist
            await completeService(data.id, 'GATE_ATTENDANT');

            alert(
                `Customer ${customer.name} details captured and forwarded to Receptionist at ${entryTime}. Time spent: ${timer} seconds.`
            );

            // Reset form & timer
            setCustomer({
                name: '',
                nationalId: '',
                customerType: '',
                serviceRequested: '',
                status: 'IN_QUEUE',
                currentRole: 'GATE_ATTENDANT',
                nextServicePoint: 'RECEPTIONIST',
            });
            setTimer(0);
            setIsRunning(true);
        } catch (err) {
            console.error('Error creating customer:', err);
            alert(err.message);
            setIsRunning(true);
        }
    };

    // Complete service at a specific point
    const completeService = async (customerId, servicePoint) => {
        try {
            const response = await fetch(
                `http://localhost:8086/serviceman/api/customers/${customerId}/complete-service?servicePoint=${servicePoint}`,
                { 
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        timeSpent: timer,
                        servedBy: userName,
                        notes: `Service completed at ${servicePoint}`
                    })
                }
            );

            // ✅ FIXED: Handle error responses properly
            if (!response.ok) {
                await handleErrorResponse(response);
            }

            console.log(`Service completed for customer ${customerId} at ${servicePoint}`);
        } catch (err) {
            console.error('Failed to complete service:', err);
            throw err;
        }
    };

    const handleClearCustomer = async (customerId, customerName) => {
        try {
            const response = await fetch(
                `http://localhost:8086/serviceman/api/customers/clearance/${customerId}`,
                { method: 'PUT' }
            );

            // ✅ FIXED: Handle error responses properly
            if (!response.ok) {
                await handleErrorResponse(response);
            }

            const data = await response.json();
            alert(`Customer ${customerName} has been cleared. Total time in system: ${data.totalTimeInSystem} seconds.`);

            fetchClearanceQueue();
        } catch (err) {
            console.error('Failed to clear customer:', err);
            alert(err.message);
        }
    };

    const formatTime = (seconds) => {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    };

    const formatTimestamp = (timestamp) => {
        if (!timestamp) return 'N/A';
        return new Date(timestamp).toLocaleString();
    };

    return (
        <div className="main-container">
            {/* Top Navbar */}
            <div className="top-navbar">
                <div className="navbar-left">
                    <div className="system-name">SERVICEMAN</div>
                    <div className="system-description">Customer Time Tracking System</div>
                </div>
                <div className="navbar-right">
                    <img src={logo} alt="Company Logo" className="logo" />
                </div>
            </div>

            {/* Dashboard */}
            <div className="dashboard-container">
                {/* Form Section */}
                <div className="dashboard-form-container">
                    <div className="timer-display">Time: {formatTime(timer)}</div>
                    <h2 className="welcome-message">Welcome, <strong>{userName}</strong>!</h2>
                    <h2 className="dashboard-title">Gate Attendant Dashboard</h2>
                    
                    {/* ✅ DISPLAY COMPANY INFO */}
                    {userCompanyId && (
                        <div className="company-info">
                            Company ID: <strong>{userCompanyId}</strong>
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="name">Customer Name:</label>
                            <input type="text" id="name" name="name" value={customer.name} onChange={handleInputChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="nationalId">National ID:</label>
                            <input type="text" id="nationalId" name="nationalId" value={customer.nationalId} onChange={handleInputChange} required />
                        </div>
                        {/* ❌ REMOVED: Manual Company ID input */}
                        <div className="form-group">
                            <label htmlFor="customerType">Customer Type:</label>
                            <select id="customerType" name="customerType" value={customer.customerType} onChange={handleInputChange} required>
                                <option value="">-- Select Type --</option>
                                <option value="New">New</option>
                                <option value="Existing">Existing</option>
                            </select>
                        </div>
                        <div className="form-group">
                            <label htmlFor="serviceRequested">Service Requested:</label>
                            <select id="serviceRequested" name="serviceRequested" value={customer.serviceRequested} onChange={handleInputChange} required>
                                <option value="">-- Select Service --</option>
                                <option value="Sales">Sales</option>
                                <option value="Invoices">Invoices</option>
                                <option value="Store">Store</option>
                            </select>
                        </div>
                        <button type="submit" className="submit-button">Record Entry and Notify Receptionist</button>
                    </form>
                </div>

                {/* Clearance Section */}
                <div className="clearance-section">
                    <h3>Gate for Clearance:</h3>
                    {isLoading ? (
                        <p>Loading customers for clearance...</p>
                    ) : error ? (
                        <p className="error-message">{error}</p>
                    ) : clearanceQueue.length > 0 ? (
                        <table className="customer-table">
                            <thead>
                                <tr>
                                    <th>Customer</th>
                                    <th>National ID</th>
                                    <th>Last Service Completed</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {clearanceQueue.map((c) => (
                                    <tr key={c.id}>
                                        <td><strong>{c.name}</strong></td>
                                        <td>{c.nationalId}</td>
                                        <td>{c.currentRole || 'N/A'}</td>
                                        <td>
                                            <button
                                                onClick={() => handleClearCustomer(c.id, c.name)}
                                                className="acknowledge-button"
                                            >
                                                Clear & Exit
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No customers are waiting for clearance.</p>
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

export default GateAttendantDashboard;