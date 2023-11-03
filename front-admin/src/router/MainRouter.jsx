import { Route, Routes } from 'react-router-dom';
import { LoginPage } from '../app/components/auth/pages/LoginPage';
import { AppRouter } from '../app/router/AppRouter';
import { PrivateRoute } from './PrivateRoute';
import { PublicRoute } from './PublicRoute';

export const MainRouter = () => (
  <Routes>
      <Route path="/login" element={ 
        <PublicRoute>
          <LoginPage />
        </PublicRoute>
      } />

      <Route path='/*' element={
        <PrivateRoute>
          <AppRouter />
        </PrivateRoute>
      } />
      
  </Routes>
)
