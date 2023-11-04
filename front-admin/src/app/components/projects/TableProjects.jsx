import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useFetchProjects } from '../../../hooks/useFetchProjects';
import { Pagination } from '../custom/pagination/page/Pagination';

export const TableProject = ({
    pageSize = 10
}) => {

    const navigate = useNavigate();

    const [currentPage, setCurrentPage] = useState(0);
    const { projects, isLoading } = useFetchProjects(currentPage, pageSize, 'createdBy,desc');

    const onPaginationClick = page => {
        setCurrentPage(page);
    }
    

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

    const renderStatus = (status) => {
        const backColor = status === 3 ? 'bg-danger' : ( status === 1 || status === 4 ? 'bg-success' : 'bg-warning' );
        // TODO Remove by value
        const statusDesc = status === 3 ? 'Desfasado' : ( status === 2 ? 'Retrazado' : 'En tiempo' );
        return (<span className={ `w-100 p-2 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRows = () => projects && projects.content && projects.content.map(({
        id,
        clave,
        name,
        description,
        createdBy,
        creationDate,
        dueDate,
        client,
        pm,
        leader,
        status
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <th className="text-left" scope="row">{ clave }</th>
            <td className="text-left">{ name }</td>
            <td className="text-left">{ description }</td>
            <td className="text-center">{ renderStatus(status, '') }</td>
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

    return (
        <>
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
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ projects.totalElements }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
        </>
    )
}

TableProject.propTypes = {
    pageSize: PropTypes.number,
}