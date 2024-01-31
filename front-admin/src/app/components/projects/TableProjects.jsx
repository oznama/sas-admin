import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { Pagination } from '../custom/pagination/page/Pagination';
import { deleteLogic, getProjectById, getProjects } from '../../services/ProjectService';
import { useDispatch, useSelector } from 'react-redux';
import { setCurrentTab, setProject } from '../../../store/project/projectSlice';
import { useNavigate } from 'react-router-dom';
import { alertType } from '../custom/alerts/types/types';
import { displayNotification, styleInput, styleTableRow, styleTableRowBtn } from '../../helpers/utils';

export const TableProject = ({
    pageSize = 10,
    sort = 'id,asc',
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions } = useSelector( state => state.auth );
    const { project } = useSelector( state => state.projectReducer );

    const [currentPage, setCurrentPage] = useState(0);
    const [projects, setProjects] = useState([]);
    const [totalProjects, setTotalProjects] = useState(0);
    const [filter, setFilter] = useState( project ? project.key : '');

    const onChangeFilter = ({ target }) => {
        setFilter(target.value);
        fetchProjects(currentPage, target.value);
    };

    const fetchProjects = (page, filter) => {
        getProjects(page, pageSize, sort, filter)
            .then( response => {
                if( response.code && response.code === 401 ) {
                    displayNotification(dispatch, response.message, alertType.error);
                }
                setProjects(response.content);
                setTotalProjects(response.totalElements);
            }).catch( error => {
                console.log(error);
                displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
    }

    const fetchProject = id => {
        getProjectById(id).then( response => {
            if( response.code ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                dispatch(setProject(response));
                dispatch(setCurrentTab( permissions.canEditEmp ? 1 : 2));
                navigate(`/project/${id}/edit`);
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
      }

    useEffect(() => {
        setCurrentPage(0);
        fetchProjects(0, filter);
    }, [currentPage]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchProjects(currentPage, filter);
    }

    const handleAddProject = () => {
        dispatch(setCurrentTab(1));
        dispatch(setProject({}));
        navigate(`/project/add`);
    }

    const renderAddButton = () => permissions.canCreateProj && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddProject }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const cleanSearcher = () => {
        setFilter('');
        setCurrentPage(0);
        fetchProjects(0, '');
    }

    const renderSearcher = () => (
        <div className={`input-group w-${ permissions.canCreateProj ? '25' : '50' } py-1`}>
            <input name="filter" type="text" className="form-control" style={ styleInput } placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ onChangeFilter }></input>
            <span className="input-group-text" id="basic-addon2" onClick={ () => cleanSearcher() }>
                <i className="bi bi-x-lg"></i>
            </span>
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
        fetchProject(id);
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteProject = (id, active) => {
        deleteLogic(id).then( response => {
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, `Proyecto ${ active ? 'eliminado' : 'reactivado' } correctamente!`, active ? alertType.warning : alertType.success);
                fetchProjects();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

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
        amount,
        tax,
        total,
        active
    }) => (
        <tr key={ id }>
            <th className="text-center" style={ styleTableRow } scope="row">{ key }</th>
            {/* <td className="text-start">{ name }</td> */}
            <td className="text-start" style={ styleTableRow }>{ description.substring(0,70) }</td>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            { permissions.isAdminRoot && (<td className="text-start" style={ styleTableRow }>{ createdBy }</td>) }
            { permissions.isAdminRoot && (<td className="text-center" style={ styleTableRow }>{ creationDate }</td>) }
            { permissions.isAdminRoot && (<th className="text-start" style={ styleTableRow }>{ company }</th>) }
            <td className="text-start" style={ styleTableRow }>{ projectManager }</td>
            <td className="text-center" style={ styleTableRow }>{ installationDate }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ amount }</td>
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ tax }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ total }</td>) }
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" className={`btn btn-${ active && permissions.canEditEmp ? 'success' : 'primary' } btn-sm`} style={ styleTableRowBtn } onClick={ () => handledSelect(id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditEmp ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelEmp && 
                (
                    <td className="text-center" style={ styleTableRow }>
                        <button type="button" className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`} style={ styleTableRowBtn } onClick={ () => deleteProject(id, active) }>
                            <span><i className={`bi bi-${ active ? 'trash' : 'folder-symlink'}`}></i></span>
                        </button>
                    </td>
                )
            }
        </tr>
    ));

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center">
                { renderSearcher() }
                { renderAddButton() }
            </div>
            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Clave</th>
                            {/* <th className="text-center fs-6" scope="col">Nombre</th> */}
                            <th className="text-center fs-6" scope="col">Descripci&oacute;n</th>
                            {/* <th className="text-center fs-6" scope="col">Status</th> */}
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Creado por</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Fecha creaci&oacute;n</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Cliente</th>) }
                            <th className="text-center fs-6" scope="col">Project Manager</th>
                            <th className="text-center fs-6" scope="col">Instalaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Iva</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Total</th>) }
                            <th className="text-center fs-6" scope="col">Estatus</th>
                            <th className="text-center fs-6" scope="col">{permissions.canEditProj ? 'Editar' : 'Ver'}</th>
                            { permissions.canDelProj && (<th className="text-center fs-6" scope="col">Borrar</th>)}
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