import { useSelector } from 'react-redux';
import { TableProject } from '../projects/TableProjects';
import { PagePendings } from '../applications/page/PagePendings';

export const Home = () => {

    const { permissions } = useSelector( state => state.auth );

    const renderProjects = () => (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Proyectos</h4>
            <TableProject />
        </div>
    )

    return permissions.isAdminSas ? renderProjects() : <PagePendings />
};