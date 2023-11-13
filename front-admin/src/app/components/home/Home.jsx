import { useContext } from 'react';
import { TableProject } from '../projects/page/TableProjects';
import { useNavigate } from 'react-router-dom';
import { ProjectContext } from '../projects/context/ProjectContext';

export const Home = () => {

    const { projectNew } = useContext( ProjectContext );

    const navigate = useNavigate();

    const handleAddProject = () => {
        projectNew();
        navigate(`/project/add`);
    }

    const renderAddButton = () => (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddProject }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    return (
        <div className='px-5'>
            <div>
                <div className="d-flex justify-content-center">
                    <h1 className="fs-4 card-title fw-bold mb-1">Proyectos</h1>
                </div>
                { renderAddButton() }
            </div>
            <TableProject />
        </div>
    );
};