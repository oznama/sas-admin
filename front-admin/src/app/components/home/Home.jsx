import { TableProject } from '../projects/TableProjects';

export const Home = () => {
    return (
        <div className='px-5'>
            <div>
                <div className="d-flex justify-content-center">
                    <h1 className="fs-4 card-title fw-bold mb-1">Proyectos</h1>
                </div>
            </div>
            <TableProject />
        </div>
    );
};