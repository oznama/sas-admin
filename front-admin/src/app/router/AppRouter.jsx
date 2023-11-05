import { Navigate, Route, Routes } from 'react-router-dom';
import { Home } from '../components/home/Home';
import { ProjectPage } from '../components/projects/page/ProjectPage';
import { NavBarPage } from '../components/custom/NavBarPage';
import { Footer } from '../components/custom/Footer';
import { Loading } from '../components/custom/loading/page/Loading';
import { DetailApplications } from '../components/applications/page/DetailApplications';

export const AppRouter = () => {
    return (
        <>
            <Loading />
            <NavBarPage />
            <div className='w-100 p-2'>
                <Routes>
                    <Route path="home" element={ <Home /> } />
                    <Route path="project/add" element={ <ProjectPage /> } />
                    <Route path="project/:projectId/edit" element={ <ProjectPage /> } />
                    <Route path="application/add" element={ <DetailApplications /> } />
                    <Route path="application/:applicationId/edit" element={ <DetailApplications /> } />

                    <Route path="/" element={ <Navigate to="/home" /> } />
                </Routes>
            </div>
            <Footer />
        </>
    );
}