import React from 'react'
import ReactDOM from 'react-dom/client'

import './styles.css';
import { BrowserRouter } from 'react-router-dom';
import { SasApp } from './SasApp';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <SasApp />
    </BrowserRouter>,
  </React.StrictMode>
)
