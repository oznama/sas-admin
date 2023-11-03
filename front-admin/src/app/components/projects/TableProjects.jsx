import { useNavigate } from 'react-router-dom';
import { useFetchProjects } from '../../../hooks/useFetchProjects';
import { Loading } from '../custom/Loading';

export const TableProject = () => {

    const navigate = useNavigate();

    const { projects, isLoading } = useFetchProjects();

    // const addNewElement = newElement => {
    //     Manera 1 para agregar un nuevo elemento al estado
    //     Con desustructuracion
    //     setProjects([ ...projects , newElement]);

    //     Manera 2 para agregar un nuevo elemento al estado
    //     Referenciando al callback del estado
    //     setProjects( project => [ ...project, newElement] );
    // }

    const handledSelect = id => {
        navigate(`/project/${id}/edit`);
    }

    const renderStatus = (status, value) => {
        const backColor = status === 3 ? 'bg-danger' : ( status === 2 ? 'bg-warning' : 'bg-success' );
        // TODO Remove by value
        const statusDesc = status === 3 ? 'Fuera de tiempo' : ( status === 2 ? 'Pendiente de pago' : 'En proceso' );
        return (<span className={ `w-100 p-2 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRows = () => projects.map(({
        id,
        clave,
        name,
        description,
        createdBy,
        creationDate,
        dueDate,
        client,
        pm,
        leader
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <th className="text-left" scope="row">{ clave }</th>
            <td className="text-left">{ name }</td>
            <td className="text-left">{ description }</td>
            <td className="text-center">{ renderStatus(id, '') }</td>
            <td className="text-left">{ createdBy }</td>
            <td className="text-center">{ creationDate }</td>
            <td className="text-center"></td>
            <td className="text-center"></td>
            <td className="text-center">{ dueDate }</td>
            <td className="text-left">{ client }</td>
            <td className="text-left"></td>
            <td className="text-left">{ pm }</td>
            <td className="text-left">{ leader }</td>
        </tr>
    ));

    const pagination = () => (
        <div className="d-flex justify-content-end">
            <nav aria-label="Page navigation example">
                <ul className="pagination">
                    <li className="page-item">
                    <a className="page-link" href="#" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                    </li>
                    <li className="page-item"><a className="page-link" href="#">1</a></li>
                    <li className="page-item"><a className="page-link" href="#">2</a></li>
                    <li className="page-item"><a className="page-link" href="#">3</a></li>
                    <li className="page-item">
                    <a className="page-link" href="#" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                    </li>
                </ul>
            </nav>
        </div>
    );

    return (
        <>
            { <Loading isLoading={ isLoading } /> }

            <div className='table-responsive text-nowrap'>

                <table className="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th className="text-center fs-6" scope="col">Clave</th>
                            <th className="text-center fs-6" scope="col">Nombre</th>
                            <th className="text-center fs-6" scope="col">Descripci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Status</th>
                            <th className="text-center fs-6" scope="col">Creado por</th>
                            <th className="text-center fs-6" scope="col">Fecha creaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Fecha entrega (Propuesta)</th>
                            <th className="text-center fs-6" scope="col">Fecha entrega (Dise&ntilde;o)</th>
                            <th className="text-center fs-6" scope="col">Fecha entrega (Desarrollo)</th>
                            <th className="text-center fs-6" scope="col">Cliente</th>
                            <th className="text-center fs-6" scope="col">L&iacute;der Dise&ntilde;o</th>
                            <th className="text-center fs-6" scope="col">Project Manager</th>
                            <th className="text-center fs-6" scope="col">L&iacute;der Desarrollo</th>
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                </table>

            </div>
            { pagination() }
        </>
    )
}