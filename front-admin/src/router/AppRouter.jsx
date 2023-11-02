import { Route, Routes } from 'react-router-dom';
import Home from '../app/components/home/Home';
import { ProjectAdd } from '../app/components/projects/ProjectAdd';
import { ProjectEdit } from '../app/components/projects/ProjectEdit';
import { LoginPage } from '../app/components/auth/pages/LoginPage';

export const AppRouter = () => (
  <Routes>
      <Route path="/" element={ <Home /> } />
      <Route path="/login" element={ <LoginPage /> } />
      <Route path="/project/add" element={ <ProjectAdd /> } />
      <Route path="/project/:projectId/edit" element={ <ProjectEdit /> } />
  </Routes>
)
