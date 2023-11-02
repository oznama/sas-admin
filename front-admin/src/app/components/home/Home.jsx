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
        <div className="btnDiv">
            <button type="button" class="btn btn-primary" onClick={ handleAddProject }>Nuevo</button>
        </div>
    );

    return (
        <div>
            <NavBarPage />
            <div className='container'>
                <div>
                    <h1>Proyectos</h1>
                    { renderAddButton() }
                </div>
                <TableProject />
            </div>
        </div>
    )
}

export default Home