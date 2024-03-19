import { useSelector } from 'react-redux';
import { TableProject } from '../projects/TableProjects';
import { TablePendings } from '../applications/page/TablePendings';

export const Home = () => {

    const { user } = useSelector( state => state.auth );

    const userAdmin = user && user.role && user.role.id < 4;

    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">{ userAdmin ? 'Proyectos' : 'Pendientes' }</h4>
            { userAdmin ? <TableProject /> : <TablePendings /> }
        </div>
    );
};