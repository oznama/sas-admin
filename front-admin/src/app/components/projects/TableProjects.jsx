import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { Pagination } from '../custom/pagination/page/Pagination';
import { getProjects } from '../../services/ProjectService';
import { useDispatch, useSelector } from 'react-redux';
import { setMessage } from '../../../store/alert/alertSlice';
import { setCurrentTab } from '../../../store/project/projectSlice';
import { useNavigate } from 'react-router-dom';
import { alertType } from '../custom/alerts/types/types';
import { buildPayloadMessage } from '../../helpers/utils';

export const TableProject = ({
    pageSize = 10,
    sort = 'creationDate,desc',
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions } = useSelector( state => state.auth );

    const [currentPage, setCurrentPage] = useState(0);
    const [projects, setProjects] = useState([]);
    const [totalProjects, setTotalProjects] = useState(0);
    const [filter, setFilter] = useState('')

    const onChangeFilter = ({ target }) => {
        setFilter(target.value);
        fetchProjects(currentPage, target.value);
    };

    const fetchProjects = (page, filter) => {
        getProjects(page, pageSize, sort, filter)
            .then( response => {
                if( response.code && response.code === 401 ) {
                    dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
                }
                setProjects(response.content);
                setTotalProjects(response.totalElements);
            }).catch( error => {
                dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar los proyectos, contacte al administrador', alertType.error)));
            });
    }

    useEffect(() => {
      fetchProjects(currentPage, filter);
    }, [currentPage]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchProjects(currentPage, filter);
    }

    const handleAddProject = () => {
        dispatch(setCurrentTab(1));
        navigate(`/project/add`);
    }

    const renderAddButton = () => permissions.canCreateProj && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddProject }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const renderSearcher = () => (
        <div className="input-group w-50 py-3">
            <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ onChangeFilter } />
            {/* <button type="button" className="btn btn-outline-primary" onClick={ () => fetchProjects(currentPage) }>
                <i className="bi bi-search"></i>
            </button> */}
        </div>
    )

    // const addNewElement = newElement => {
    //     Manera 1 para agregar un nuevo elemento al estado
    //     Con desustructuracion
    //     setProjects([ ...projects , newElement]);

    //     Manera 2 para agregar un nuevo elemento al estado
    //     Referenciando al callback del estado
    //     setProjects( project => [ ...project, newElement] );
    // }

    const handledSelect = id => {
        dispatch(setCurrentTab(2));
        navigate(`/project/${id}/edit`);
    }

    // const renderStatus = (status) => {
    //     const backColor = status === 3 ? 'bg-danger' : ( status === 1 || status === 4 ? 'bg-success' : 'bg-warning' );
    //     // TODO Remove by value
    //     const statusDesc = status === 3 ? 'Desfasado' : ( status === 2 ? 'Retrazado' : 'En tiempo' );
    //     return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    // }

    const renderRows = () => projects && projects.map(({
        id,
        key,
        // name,
        description,
        createdBy,
        creationDate,
        installationDate,
        company,
        projectManager,
        status,
        amount,
        tax,
        total
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <th className="text-start" scope="row">{ key }</th>
            {/* <td className="text-start">{ name }</td> */}
            <td className="text-start">{ description }</td>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            { permissions.isAdminSas && (<td className="text-start">{ createdBy }</td>) }
            <td className="text-center">{ creationDate }</td>
            { permissions.isAdminRoot && (<th className="text-start">{ company }</th>) }
            <td className="text-start">{ projectManager }</td>
            <td className="text-center">{ installationDate }</td>
            <td className="text-end text-primary">{ amount }</td>
            <td className="text-end text-primary">{ tax }</td>
            <td className="text-end text-primary">{ total }</td>
        </tr>
    ));

    return (
        <div>
            { renderSearcher() }

            { renderAddButton() }

            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Clave</th>
                            {/* <th className="text-center fs-6" scope="col">Nombre</th> */}
                            <th className="text-center fs-6" scope="col">Descripci&oacute;n</th>
                            {/* <th className="text-center fs-6" scope="col">Status</th> */}
                            { permissions.isAdminSas && (<th className="text-center fs-6" scope="col">Creado por</th>) }
                            <th className="text-center fs-6" scope="col">Fecha creaci&oacute;n</th>
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Cliente</th>) }
                            <th className="text-center fs-6" scope="col">Project Manager</th>
                            <th className="text-center fs-6" scope="col">Instalaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                            <th className="text-center fs-6" scope="col">Iva</th>
                            <th className="text-center fs-6" scope="col">Total</th>
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
        </div>
    )
}

TableProject.propTypes = {
    pageSize: PropTypes.number,
}