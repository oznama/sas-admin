import { Route, Routes } from 'react-router-dom';
import { LoginPage } from '../app/components/auth/LoginPage';
import { PrivateRoute } from './PrivateRoute';
import { PublicRoute } from './PublicRoute';
import { MainPage } from '../app/components/MainPage';

export const MainRouter = () => (
  <Routes>
      <Route path="/login" element={ 
        <PublicRoute>
          <LoginPage />
        </PublicRoute>
      } />

      <Route path='/*' element={
        <PrivateRoute>
          <MainPage />
        </PrivateRoute>
      } />
      
  </Routes>
)
