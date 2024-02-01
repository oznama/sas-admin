import { TableProject } from '../projects/TableProjects';

export const Home = () => {
    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Proyectos</h4>
            <TableProject />
        </div>
    );
};