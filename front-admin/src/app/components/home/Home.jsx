import './home.main.css'
import { TableProject } from '../custom/TableProjects';

const Home = () => {

    return (
        <div className='main-container'>
            <div className="header-content">
                <h1 className='title'>SAS Admin Home</h1>
            </div>
            <div className="body-content">
                <h2 className="subtitle">Proyectos</h2>
                <TableProject />
            </div>
        </div>
    )
}

export default Home