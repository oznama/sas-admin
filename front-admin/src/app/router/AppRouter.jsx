import { Navigate, Route, Routes } from 'react-router-dom';
import { Home } from '../components/home/Home';
import { ProjectRouter } from '../components/projects/page/ProjectRouter';
import { ProjectProvider } from '../components/projects/context/ProjectProvider';

export const AppRouter = () => (
    <ProjectProvider>
        <Routes>
            <Route path="home" element={ <Home /> } />
            <Route path="project/*" element={ <ProjectRouter /> } />

            <Route path="/" element={ <Navigate to="/home" /> } />
        </Routes>
    </ProjectProvider>
)