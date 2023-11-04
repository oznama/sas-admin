import './home.main.css'
import { TableProject } from '../projects/TableProjects';
import { useNavigate } from 'react-router-dom';

export const Home = () => {

    const navigate = useNavigate();

    const handleAddProject = () => {
        navigate(`/project/add`);
    }

    const renderAddButton = () => (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddProject }>Nuevo</button>
        </div>
    );

    return (
        <>
            <div>
                <div className="d-flex justify-content-center">
                    <h1 className="fs-4 card-title fw-bold mb-1">Proyectos</h1>
                </div>
                { renderAddButton() }
            </div>
            <TableProject />
        </>
    );
};