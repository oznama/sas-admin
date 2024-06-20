import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { Pagination } from '../custom/pagination/page/Pagination';
import { deleteLogic, getProjects } from '../../services/ProjectService';
import { useDispatch, useSelector } from 'react-redux';
import { setCurrentTab, setOrder, setProjectPaid, setProject } from '../../../store/project/projectSlice';
import { useNavigate } from 'react-router-dom';
import { alertType } from '../custom/alerts/types/types';
import { displayNotification, styleTableRow, styleTableRowBtn } from '../../helpers/utils';
import { InputSearcher } from '../custom/InputSearcher';

export const TableProject = ({
    pageSize = 10
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions } = useSelector( state => state.auth );
    const { project } = useSelector( state => state.projectReducer );

    const [currentPage, setCurrentPage] = useState(0);
    const [projects, setProjects] = useState([]);
    const [totalProjects, setTotalProjects] = useState(0);
    const [filter, setFilter] = useState( project &&  project.key ? project.key : '');

    const onChangeFilter = ({ target }) => {
        setCurrentPage(0)
        setFilter(target.value);
        fetchProjects(0, target.value);
    };

    const fetchProjects = (page, filter) => {
        getProjects(page, pageSize, filter)
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

    useEffect(() => {
        fetchProjects(currentPage, filter);
    }, []);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchProjects(page, filter);
    }

    const handleAddProject = () => {
        dispatch(setCurrentTab(1));
        dispatch(setProject({}));
        dispatch(setOrder({}));
        dispatch(setProjectPaid({}));
        navigate(`/project/add`);
    }

    const renderAddButton = () => permissions.canCreateProj && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddProject }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        fetchProjects(0, '');
    }

    // const addNewElement = newElement => {
    //     Manera 1 para agregar un nuevo elemento al estado
    //     Con desustructuracion
    //     setProjects([ ...projects , newElement]);

    //     Manera 2 para agregar un nuevo elemento al estado
    //     Referenciando al callback del estado
    //     setProjects( project => [ ...project, newElement] );
    // }

    const handledSelect = key => {
        dispatch(setCurrentTab( permissions.canEditProj ? 1 : 2));
        navigate(`/project/${key}/edit`);
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
        key,
        // name,
        description,
        createdBy,
        creationDate,
        installationDate,
        monitoringDate,
        company,
        projectManager,
        amount,
        tax,
        total,
        active
    }, index) => (
        <tr key={ index }>
            <th className="text-center" style={ styleTableRow } scope="row">{ key }</th>
            {/* <td className="text-start">{ name }</td> */}
            <td className="text-start" style={ styleTableRow }>{ description }</td>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            { permissions.isAdminRoot && (<td className="text-start" style={ styleTableRow }>{ createdBy }</td>) }
            { permissions.isAdminRoot && (<td className="text-center" style={ styleTableRow }>{ creationDate }</td>) }
            { permissions.isAdminSas && (<th className="text-start" style={ styleTableRow }>{ company }</th>) }
            <td className="text-start" style={ styleTableRow }>{ projectManager }</td>
            <td className="text-center" style={ styleTableRow }>{ installationDate }</td>
            <td className="text-center" style={ styleTableRow }>{ monitoringDate }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ amount }</td>
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ tax }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ total }</td>) }
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" className={`btn btn-${ active && permissions.canEditProj ? 'success' : 'primary' } btn-sm`} style={ styleTableRowBtn } onClick={ () => handledSelect(key) }>
                    <span><i className={`bi bi-${ active && permissions.canEditProj ? 'pencil-square' : 'eye'}`}></i></span>
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
                { <InputSearcher name={ 'filter' } placeholder={ 'Escribe para filtrar...' } value={ filter } onChange={ onChangeFilter } onClean={ onClean } /> }
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
                            { permissions.isAdminSas && (<th className="text-center fs-6" scope="col">Empresa</th>) }
                            <th className="text-center fs-6" scope="col">Project Manager</th>
                            <th className="text-center fs-6" scope="col">Instalaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Monitoreo</th>
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