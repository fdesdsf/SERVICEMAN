import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
    PieChart, Pie, Cell, Tooltip, Legend,
    BarChart, Bar, XAxis, YAxis, CartesianGrid, ResponsiveContainer,
    LineChart, Line
} from 'recharts';
import '../styles/AdminDashboard.css';
import logo from '../assets/Serviceman.jpeg';

// Helper function to format a date to 'YYYY-MM-DD'
const formatDate = (date) => {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

// 👉 PLACE THE NEW HELPER FUNCTION HERE 👈
const formatCompletionTime = (timestamp) => {
    if (!timestamp) return 'N/A';
    const date = new Date(timestamp);
    if (isNaN(date.getTime())) return 'Invalid Date';
    return date.toLocaleString();
};

const AdminDashboard = () => {
    const [userName, setUserName] = useState('');
    const [userCompanyId, setUserCompanyId] = useState(''); // ✅ ADDED: Company ID state
    const navigate = useNavigate();
    const [reportData, setReportData] = useState([]);
    const [summary, setSummary] = useState({
        totalServed: 0,
        avgTime: 0,
        byRole: {}
    });
    const [avgTimeByRole, setAvgTimeByRole] = useState({});
    const [dateFilter, setDateFilter] = useState(formatDate(new Date()));
    const [tableRoleFilter, setTableRoleFilter] = useState('All');
    const [dailyTrends, setDailyTrends] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [queueStatus, setQueueStatus] = useState({});
    const [totalTimeData, setTotalTimeData] = useState([]);
    const [showTotalTimeTable, setShowTotalTimeTable] = useState(false);

    const API_BASE_URL = 'http://localhost:8086/serviceman/api';

    useEffect(() => {
        const storedName = localStorage.getItem('userName');
        const storedCompanyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
        
        if (storedName) setUserName(storedName);
        if (storedCompanyId) setUserCompanyId(storedCompanyId);

        fetchReportData();
        fetchDailyTrends();
        fetchQueueStatus();
    }, [dateFilter]);

    const fetchReportData = async () => {
        setLoading(true);
        setError(null);
        try {
            const companyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
            
            // Fetch service records for the selected date WITH COMPANY FILTER
            const response = await fetch(
                `${API_BASE_URL}/service-records/by-date?date=${dateFilter}&companyId=${companyId}` // ✅ ADD COMPANY FILTER
            );
            if (!response.ok) throw new Error('Failed to fetch report data');
            
            const data = await response.json();
            
            // 🔥 FILTER OUT ENTRIES WITH NULL/ZERO TIME SPENT
            const filteredData = data.filter(item => item.timeSpent && item.timeSpent > 0);
            
            setReportData(filteredData);

            // Calculate summary statistics using FILTERED data
            const totalServed = filteredData.length;
            let totalTime = 0;
            const roleCounts = {};
            const roleTotalTimes = {};

            filteredData.forEach(item => {  // Use filteredData here
                const role = item.arrivedFrom || 'Unknown';
                const timeSpent = item.timeSpent || 0;

                // Count by role
                roleCounts[role] = (roleCounts[role] || 0) + 1;
                
                // Sum time by role
                roleTotalTimes[role] = (roleTotalTimes[role] || 0) + timeSpent;
                
                totalTime += timeSpent;
            });

            // Calculate average times by role
            const calculatedAvgTimes = {};
            Object.keys(roleCounts).forEach(role => {
                calculatedAvgTimes[role] = (roleTotalTimes[role] / roleCounts[role] / 60).toFixed(2);
            });

            setSummary({
                totalServed,
                avgTime: totalServed > 0 ? (totalTime / totalServed / 60).toFixed(2) : 0,
                byRole: roleCounts
            });

            setAvgTimeByRole(calculatedAvgTimes);

        } catch (error) {
            console.error('Error fetching report data:', error);
            setError('Failed to load report data. Please try again later.');
        } finally {
            setLoading(false);
        }
    };

    // Fetch total time data WITH COMPANY FILTER
    const fetchTotalTimeData = async () => {
        try {
            const companyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
            
            const response = await fetch(
                `${API_BASE_URL}/customers/total-times?companyId=${companyId}` // ✅ ADD COMPANY FILTER
            );
            if (!response.ok) throw new Error('Failed to fetch total time data');
            
            const data = await response.json();
            setTotalTimeData(data);
        } catch (error) {
            console.error('Error fetching total time data:', error);
            setError('Failed to load total time data');
        }
    };

    // Fetch daily trends WITH COMPANY FILTER
    const fetchDailyTrends = async () => {
        try {
            const companyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
            
            // Fetch 30-day trends from API WITH COMPANY FILTER
            const response = await fetch(
                `${API_BASE_URL}/service-records/daily-trends?days=30&companyId=${companyId}` // ✅ ADD COMPANY FILTER
            );
            if (!response.ok) throw new Error('Failed to fetch trends data');
            
            const trendsData = await response.json();
            setDailyTrends(trendsData);

        } catch (error) {
            console.error('Error fetching trends data:', error);
            // Don't set error for trends - it's secondary data
        }
    };

    // Fetch queue status WITH COMPANY FILTER
    const fetchQueueStatus = async () => {
        try {
            const companyId = localStorage.getItem('userCompanyId'); // ✅ GET COMPANY ID
            
            const endpoints = {
                clearance: `${API_BASE_URL}/customers/clearance?companyId=${companyId}`,
                receptionist: `${API_BASE_URL}/customers/receptionist?companyId=${companyId}`,
                salesOrderDesk: `${API_BASE_URL}/customers/sales-order-desk?companyId=${companyId}`,
                invoicingDesk: `${API_BASE_URL}/customers/invoicing-desk?companyId=${companyId}`,
                storeClerk: `${API_BASE_URL}/customers/store-clerk?companyId=${companyId}`
            };

            const results = await Promise.all(
                Object.entries(endpoints).map(async ([key, url]) => {
                    const res = await fetch(url);
                    if (!res.ok) throw new Error(`Failed to fetch ${key} queue`);
                    const data = await res.json();
                    return [key, data.length];
                })
            );

            setQueueStatus(Object.fromEntries(results));
        } catch (err) {
            console.error("Error fetching queue status:", err);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('userRole');
        localStorage.removeItem('userName');
        localStorage.removeItem('userCompanyId'); // ✅ CLEAR COMPANY ID ON LOGOUT
        navigate('/login');
    };

    const handleDateChange = (event) => {
        setDateFilter(event.target.value);
    };

    const handleTableRoleFilterChange = (event) => {
        setTableRoleFilter(event.target.value);
    };

    const chartData = Object.keys(summary.byRole).map(role => ({
        name: role,
        value: summary.byRole[role]
    }));

    const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#AF19FF'];

    const filteredReportData = tableRoleFilter === 'All'
        ? reportData
        : reportData.filter(item => item.arrivedFrom === tableRoleFilter);

    const uniqueRoles = ['All', ...new Set(reportData.map(item => item.arrivedFrom).filter(Boolean))];

    return (
        <div className="admin-dashboard">
            {/* Top Navigation Bar */}
            <div className="top-navbar">
                <div className="navbar-left">
                    <div className="system-name">SERVICEMAN</div>
                    <div className="system-description">Customer Time Tracking System</div>
                </div>
                <div className="navbar-right">
                    <div className="user-info">
                        Welcome, <strong>{userName}</strong>! (Admin)
                    </div>
                    
                    {/* ✅ ADDED: Company Info Display */}
                    {userCompanyId && (
                        <div className="company-info">
                            Company ID: <strong>{userCompanyId}</strong>
                        </div>
                    )}
                    
                    {/* <Link to="/" onClick={handleLogout} className="logout-btn">
                        Logout
                    </Link> */}
                    <img src={logo} alt="Company Logo" className="logo" />
                </div>
            </div>

            {/* Date Filter */}
            <div className="date-filter-container">
                <label htmlFor="report-date">Select Report Date:</label>
                <input 
                    type="date" 
                    id="report-date"
                    value={dateFilter}
                    onChange={handleDateChange}
                />
                {loading && <span className="loading-spinner">Loading...</span>}
            </div>

            {error && <div className="error-message">{error}</div>}

            {/* Summary Section */}
            {!loading && (
                <>
                    <div className="summary-cards">
                        <div className="card">
                            <h3>Service points Count</h3>
                            <p>{summary.totalServed}</p>
                        </div>
                        <div className="card">
                            <h3>Average Total Time (mins)</h3>
                            <p>{summary.avgTime}</p>
                        </div>
                        {Object.keys(summary.byRole).map(role => (
                            <div className="card" key={role}>
                                <h3>{role} Served</h3>
                                <p>{summary.byRole[role]}</p>
                            </div>
                        ))}
                    </div>

                    {/* Bottleneck Analysis Section */}
                    <hr />
                    <div className="bottleneck-section">
                        <h2>Bottleneck Analysis 🐢</h2>
                        <div className="bottleneck-cards">
                            {Object.keys(avgTimeByRole).length > 0 ? (
                                Object.keys(avgTimeByRole).sort((a, b) => avgTimeByRole[b] - avgTimeByRole[a]).map(role => (
                                    <div className="card" key={role}>
                                        <h3>Avg Time at {role}</h3>
                                        <p>{avgTimeByRole[role]} minutes</p>
                                    </div>
                                ))
                            ) : (
                                <p>No bottleneck data available for this date.</p>
                            )}
                        </div>
                    </div>

                    <hr />
                    <div className="queue-status-section">
                        <h2>Current Queue Status ⏳</h2>
                        <div className="summary-cards">
                            <div className="card">
                                <h3>Clearance(GATE)</h3>
                                <p>{queueStatus.clearance ?? 0} in queue</p>
                            </div>
                            <div className="card">
                                <h3>Receptionist</h3>
                                <p>{queueStatus.receptionist ?? 0} in queue</p>
                            </div>
                            <div className="card">
                                <h3>Sales Order Desk</h3>
                                <p>{queueStatus.salesOrderDesk ?? 0} in queue</p>
                            </div>
                            <div className="card">
                                <h3>Invoicing Desk</h3>
                                <p>{queueStatus.invoicingDesk ?? 0} in queue</p>
                            </div>
                            <div className="card">
                                <h3>Store Clerk</h3>
                                <p>{queueStatus.storeClerk ?? 0} in queue</p>
                            </div>
                        </div>
                    </div>
                    
                    {/* Customer Flow Trend Report */}
                    <hr />
                    <div className="charts-container">
                        <div className="chart">
                            <h3>Customer Flow Over Last 30 Days</h3>
                            <ResponsiveContainer width="100%" height={300}>
                                <LineChart data={dailyTrends}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="date" />
                                    <YAxis />
                                    <Tooltip />
                                    <Legend />
                                    <Line type="monotone" dataKey="customers" stroke="#0088FE" name="Customers Served" />
                                </LineChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    <div className="charts-container">
                        <div className="chart">
                            <h3>Activity Distribution by Role</h3>
                            <ResponsiveContainer width="100%" height={300}>
                                <PieChart>
                                    <Pie
                                        data={chartData}
                                        cx="50%"
                                        cy="50%"
                                        outerRadius={100}
                                        dataKey="value"
                                        label
                                    >
                                        {chartData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                        ))}
                                    </Pie>
                                    <Tooltip />
                                    <Legend />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                        <div className="chart">
                            <h3>Bar Chart - Served by Role</h3>
                            <ResponsiveContainer width="100%" height={300}>
                                <BarChart data={chartData}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="name" />
                                    <YAxis />
                                    <Tooltip />
                                    <Legend />
                                    <Bar dataKey="value" fill="#0088FE" />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </div>

                    {/* Table Report with Filter */}
                    <div className="table-container">
                        <div className="table-header">
                            <h3>Detailed Activity Report</h3>
                            <div className="table-filter">
                                <label htmlFor="role-filter">Filter by Role:</label>
                                <select id="role-filter" value={tableRoleFilter} onChange={handleTableRoleFilterChange}>
                                    {uniqueRoles.map(role => (
                                        <option key={role} value={role}>{role}</option>
                                    ))}
                                </select>
                            </div>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Arrived From</th>
                                    <th>Customer</th>
                                    {/* <th>Served By</th> */}
                                    <th>Time Spent (mins)</th>
                                    <th>Completion Time</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredReportData.length > 0 ? (
                                    filteredReportData.map((item, idx) => (
                                        <tr key={idx}>
                                            <td>{item.arrivedFrom || 'N/A'}</td>
                                            <td>{item.customer && item.customer.name ? item.customer.name : 'N/A'}</td>
                                            {/* <td>{item.servedBy || 'N/A'}</td> */}
                                            <td>{item.timeSpent ? (item.timeSpent / 60).toFixed(2) : 'N/A'}</td>
                                            <td>{formatCompletionTime(item.completionTime)}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="5">No data available for this date or filter.</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </>
            )}
            
            {/* Total Time in System Table */}
            <div className="table-container">
                <div className="table-header">
                    <h3>Total Time in System Report</h3>
                    <button 
                        onClick={() => {
                            setShowTotalTimeTable(!showTotalTimeTable);
                            if (!showTotalTimeTable) fetchTotalTimeData();
                        }}
                        className="toggle-table-btn"
                    >
                        {showTotalTimeTable ? 'Hide Total Times' : 'Show Total Times'}
                    </button>
                </div>
                
                {showTotalTimeTable && (
                    <table>
                        <thead>
                            <tr>
                                <th>Customer ID</th>
                                <th>Customer Name</th>
                                <th>National ID</th>
                                <th>Total Time in System (mins)</th>
                                <th>Total Time (hours)</th>
                                <th>Entry Time</th>
                                {/* <th>Exit Time</th> */}
                            </tr>
                        </thead>
                        <tbody>
                            {totalTimeData.length > 0 ? (
                                totalTimeData.map((customer) => (
                                    <tr key={customer.id}>
                                        <td>{customer.id}</td>
                                        <td>{customer.name}</td>
                                        <td>{customer.nationalId}</td>
                                        <td>{(customer.totalTimeInSystem / 60).toFixed(2)}</td>
                                        <td>{(customer.totalTimeInSystem / 3600).toFixed(2)}</td>
                                        <td>{formatCompletionTime(customer.createdAt)}</td>
                                        {/* <td>{formatCompletionTime(customer.exitTime)}</td> */}
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="7">No customers with total time data available.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                )}
            </div>

            {/* Footer */}
            <div className="footer">
                Powered by Mansoft<br />Infinite Possibilities
            </div>
        </div>
    );
};

export default AdminDashboard;