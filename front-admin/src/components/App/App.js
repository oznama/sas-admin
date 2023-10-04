import React, { useState } from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from '../Login/Login';
import Dashboard from '../Dashboard/Dashboard';
import Administrator from '../Administrator/Administrator';

const getToken = () => sessionStorage.getItem('token');

function App() {

  const token = getToken();

  if(!token) {
    return <Login />
  }

  return (
    <div className="wrapper">
      <h1>SAS Administrator</h1>
      <Router>
        <Routes>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/administrator" element={<Administrator />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
