import { Navigate, Route, Routes } from 'react-router-dom';
import { Home } from '../components/home/Home';
import { ProjectRouter } from '../components/projects/ProjectRouter';
import { CatalogPage } from '../components/Catalogs/CatalogPage';
import { UserPage } from '../components/auth/UserPage';

export const AppRouter = () => (
    <Routes>
        <Route path="home" element={ <Home /> } />
        <Route path="project/*" element={ <ProjectRouter /> } />
        <Route path="catalog" element={ <CatalogPage /> } />
        <Route path="application" element={ <CatalogPage title="Aplicaciones" catalogId="1000000005" /> } />
        <Route path="user" element={ <UserPage /> } />

        <Route path="/" element={ <Navigate to="/home" /> } />
    </Routes>
)