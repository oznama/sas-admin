import './home.main.css'
import { TableProject } from '../custom/TableProjects';
import { useNavigate } from 'react-router-dom';
import { NavBarPage } from '../custom/NavBar/NavBarPage';

const Home = () => {

    const navigate = useNavigate();

    const handleAddProject = () => {
        navigate(`/project/add`);
    }

    const renderAddButton = () => (
        <div className="d-flex flex-row-reverse">
            <button type="button" className="btn btn-primary" onClick={ handleAddProject }>Nuevo</button>
        </div>
    );

    return (
        <div>
            <NavBarPage />
            <div className='container'>
                <div>
                    <div className="d-flex justify-content-center">
                        <h1 className="fs-4 card-title fw-bold mb-4">Proyectos</h1>
                    </div>
                    { renderAddButton() }
                </div>
                <TableProject />
            </div>
        </div>
    )
}

export default Home