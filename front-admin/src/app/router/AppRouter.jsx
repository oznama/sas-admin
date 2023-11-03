import { Navigate, Route, Routes } from 'react-router-dom';
import { Home } from '../components/home/Home';
import { ProjectPage } from '../components/projects/ProjectPage';
import { NavBarPage } from '../components/custom/NavBarPage';
import { Footer } from '../components/custom/Footer';

export const AppRouter = () => {
    return (
        <>
            <NavBarPage />
            <div className='w-100 p-5'>
                <Routes>
                    <Route path="home" element={ <Home /> } />
                    <Route path="project/add" element={ <ProjectPage /> } />
                    <Route path="project/:projectId/edit" element={ <ProjectPage /> } />

                    <Route path="/" element={ <Navigate to="/home" /> } />
                </Routes>
            </div>
            <Footer />
        </>
    );
}