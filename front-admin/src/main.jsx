import React from 'react';
import ReactDOM from 'react-dom/client'

import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from './store/store';
import { MainRouter } from './router/MainRouter';

import './styles.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Provider store={ store }>
      <BrowserRouter>
        <MainRouter />
      </BrowserRouter>,
    </Provider>
  </React.StrictMode>
)
