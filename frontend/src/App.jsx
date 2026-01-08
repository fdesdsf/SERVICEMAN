// src/App.jsx
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Login from './components/Login.jsx';
import Register from './components/Register.jsx';
import ForgotPassword from './components/ForgotPassword.jsx';
import Configuration from './components/Configuration.jsx';
import AdminDashboard from './components/AdminDashboard.jsx';
import GateAttendantDashboard from './components/GateAttendantDashboard.jsx';
import ReceptionistDashboard from './components/ReceptionistDashboard.jsx';
import SalesOrderDeskDashboard from './components/SalesOrderDeskDashboard.jsx';
import InvoicingDeskDashboard from './components/InvoicingDeskDashboard.jsx';
import StoreClerkDashboard from './components/StoreClerkDashboard.jsx';

function App() {
  return (
    <Routes>
      {/* Route for company configuration (initial setup) */}
      <Route path="/" element={<Configuration />} />
      
      {/* Explicit route for the Login page */}
      <Route path="/login" element={<Login />} />
      
      {/* Other routes for Register and Forgot Password */}
      <Route path="/register" element={<Register />} />
      <Route path="/forgotpassword" element={<ForgotPassword />} />

      {/* Dashboard routes */}
      <Route path="/dashboard/admin" element={<AdminDashboard />} />
      <Route path="/dashboard/gateattendant" element={<GateAttendantDashboard />} />
      <Route path="/dashboard/receptionist" element={<ReceptionistDashboard />} />
      <Route path="/dashboard/salesorderdesk" element={<SalesOrderDeskDashboard />} />
      <Route path="/dashboard/invoicingdesk" element={<InvoicingDeskDashboard />} />
      <Route path="/dashboard/storeclerk" element={<StoreClerkDashboard />} />
    </Routes>
  );
}

export default App;