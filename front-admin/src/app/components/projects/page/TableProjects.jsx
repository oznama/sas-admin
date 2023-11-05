import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { Pagination } from '../../custom/pagination/page/Pagination';
import { LoadingContext } from '../../custom/loading/context/LoadingContext';
import { getProjects } from '../../../api/ApiDummy';
import { renderErrorMessage } from '../../../helpers/handleErrors';

export const TableProject = ({
    pageSize = 10,
    sort = 'creationDate,desc',
}) => {

    const navigate = useNavigate();

    const { changeLoading } = useContext( LoadingContext );
    const [currentPage, setCurrentPage] = useState(0);
    const [projects, setProjects] = useState([]);
    const [totalProjects, setTotalProjects] = useState(0);
    const [msgError, setMsgError] = useState();

    const fetchProjects = (page) => {
        changeLoading(true);
        getProjects(page, pageSize, sort)
            .then( response => {
                setProjects(response.content);
                setTotalProjects(response.totalElements);
                changeLoading(false);
            }).catch( error => {
                changeLoading(false);
                setMsgError(`Imposible cargar los proyectos, contacta al administrador del sistema!`);
            });
    }

    useEffect(() => {
      fetchProjects(currentPage);
    }, [currentPage]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchProjects(currentPage);
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
        return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRows = () => projects && projects.map(({
        id,
        clave,
        name,
        description,
        createdBy,
        creationDate,
        dueDate,
        client,
        pm,
        status
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <th className="text-start" scope="row">{ clave }</th>
            <td className="text-start">{ name }</td>
            <td className="text-start">{ description }</td>
            <td className="text-center">{ renderStatus(status, '') }</td>
            <td className="text-start">{ createdBy }</td>
            <td className="text-center">{ creationDate }</td>
            <th className="text-start">{ client }</th>
            <td className="text-start">{ pm }</td>
            <td className="text-center">{ dueDate }</td>
        </tr>
    ));

    return (
        <>
            { renderErrorMessage(msgError, null) }
            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Clave</th>
                            <th className="text-center fs-6" scope="col">Nombre</th>
                            <th className="text-center fs-6" scope="col">Descripci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Status</th>
                            <th className="text-center fs-6" scope="col">Creado por</th>
                            <th className="text-center fs-6" scope="col">Fecha creaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Cliente</th>
                            <th className="text-center fs-6" scope="col">Project Manager</th>
                            <th className="text-center fs-6" scope="col">Cierre</th>
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                </table>

            </div>
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalProjects }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
        </>
    )
}

TableProject.propTypes = {
    pageSize: PropTypes.number,
}