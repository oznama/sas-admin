import React from 'react'
import ReactDOM from 'react-dom/client'

import './styles.css';
import { BrowserRouter } from 'react-router-dom';
import { AppRouter } from './router/AppRouter';
import { AuthProvider } from './app/components/auth/context/AuthProvider';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </BrowserRouter>,
  </React.StrictMode>
)
