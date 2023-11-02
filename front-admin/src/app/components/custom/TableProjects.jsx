import { useNavigate } from 'react-router-dom';
import { useFetchProjects } from '../../../hooks/useFetchProjects';
import { Loading } from './Loading';

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
            <th scope="row">{ clave }</th>
            <td>{ name }</td>
            <td>{ description }</td>
            <td>{ createdBy }</td>
            <td>{ creationDate }</td>
            <td></td>
            <td></td>
            <td>{ dueDate }</td>
            <td>{ client }</td>
            <td></td>
            <td>{ pm }</td>
            <td>{ leader }</td>
        </tr>
    ));

    const pagination = () => (
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
    );

    return (
        <>
            { <Loading isLoading={ isLoading } /> }

            <table className="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Clave</th>
                        <th scope="col">Nombre</th>
                        <th scope="col">Descripci&oacute;n</th>
                        <th scope="col">Creado por</th>
                        <th scope="col">Fecha creaci&oacute;n</th>
                        <th scope="col">Fecha entrega (Propuesta)</th>
                        <th scope="col">Fecha entrega (Dise&ntilde;o)</th>
                        <th scope="col">Fecha entrega (Desarrollo)</th>
                        <th scope="col">Cliente</th>
                        <th scope="col">L&iacute;der Dise&ntilde;o</th>
                        <th scope="col">Project Manager</th>
                        <th scope="col">L&iacute;der Desarrollo</th>
                    </tr>
                </thead>
                <tbody>
                    { renderRows() }
                </tbody>
            </table>
            { pagination() }
        </>
    )
}