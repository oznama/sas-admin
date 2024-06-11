import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { setRole  } from '../../../store/admin/adminSlice';
import { getRoles, deleteLogic, getRoleById } from '../../services/RoleService';
import { useNavigate } from "react-router-dom";
import { InputSearcher } from '../custom/InputSearcher';

export const TableAdmin = ({
    pageSize = 10,
}) => {

    const dispatch = useDispatch();
    const { role } = useSelector( state => state.adminReducer );

    const navigate = useNavigate();
    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [totalRoles, setTotalRoles] = useState(0);  
    const [roles, setRoles] = useState([]);
    const [id, setId] = useState(null);
    const [filter, setFilter] = useState(role.name ? (role.name+'').toLowerCase() : '');

    const onChangeFilter = ({ target }) => setFilter(target.value.toLowerCase());

    const fetchRoles = () => {
        getRoles()
        .then( response => {
            if( response.code && response.code === 401 ) {
                displayNotification(dispatch, response.message, alertType.error);
            }
            if (filter==='') {
                const sortedRoles = response.content.sort((a, b) => a.id - b.id);
                setRoles(sortedRoles);
                setTotalRoles(response.totalElements);
            }else{
            const filteredCatalogRoles = response.content.filter(child => child.name.toLowerCase().includes(filter));
            // Ordenar por el campo id después de filtrar
            const sortedFilteredRoles = filteredCatalogRoles.sort((a, b) => a.id - b.id);
                setRoles(sortedFilteredRoles);
                setTotalRoles(sortedFilteredRoles.totalElements);
            }
            
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
    }
    
    useEffect(() => {  
        fetchRoles();
    }, [filter]);  

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchRoles(currentPage);
    }

    const handleAddCatalog = () => {
        dispatch(setRole({}));
        navigate(`/admin/add`);
    }

    const renderAddButton = () => permissions.canCreateCat && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddCatalog }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        fetchRoles();
    }

    const renderSearcher = () => (
        <div className={`input-group w-${ permissions.canCreateCat ? '40' : '50' } py-3`}>
            { <InputSearcher name={ 'filter' } placeholder={ 'Escribe para filtrar...' } value={ filter } onChange={ onChangeFilter } onClean={ onClean } /> }
        </div>
    )

    const renderHeader = () => (
        <div className="d-flex justify-content-between align-items-center">
            { renderSearcher() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalRoles }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
            { renderAddButton() }
        </div>
    )

    const handledSelect = roleSel => {
        dispatch(setRole(roleSel));
        navigate(`/admin/edit`);
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteChild = role => {
        // dispatch(setCatalogObj(catalog));
        deleteLogic(role.id).then( response => {
            if(response.code && response.code !== 200) {
            displayNotification(dispatch, response.message, alertType.error);
            } else {
            displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
            fetchRoles('');
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => roles && roles.map((role) => (
        <tr key={ role.id } >
            { permissions.isAdminRoot && <th className="text-center" scope="row">{ role.id }</th> }
            <th className="text-center" scope="row">{ role.name }</th>
            <td className="text-start">{ role.description }</td>
            <td className="text-center">{ renderStatus(role.active) }</td>
            <td className="text-center">
                <button type="button" className={`btn btn-${ role.active && permissions.canEditCat ? 'success' : 'primary' } btn-sm`} onClick={ () => handledSelect(role) }>
                    <span><i className={`bi bi-${ role.active && permissions.canEditCat ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelCat && (
            <td className="text-center">
                <button type="button" className={`btn btn-${ role.active ? 'danger' : 'warning'} btn-sm`} onClick={ () => deleteChild(role) }>
                    <span><i className={`bi bi-${ role.active ? 'trash' : 'folder-symlink'}`}></i></span>
                </button>
            </td>
            )}
        </tr>
    ));

    return (
        <div>
            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">Roles</h3>
            </div>

            { renderHeader() }

            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            { permissions.isAdminRoot && <th className="text-center fs-6" scope="col">Id</th> }
                            <th className="text-center fs-6" scope="col">Rol</th>
                            <th className="text-center fs-6" scope="col">Descripcion</th>
                            <th className="text-center fs-6" scope="col">Estatus</th>
                            <th className="text-center fs-6" scope="col">{permissions.canEditCat ? 'Editar' : 'Ver'}</th>
                            { permissions.canDelCat && (<th className="text-center fs-6" scope="col">Borrar</th>)}
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                </table>

            </div>
        </div>
    )
}

TableAdmin.propTypes = {
    pageSize: PropTypes.number,
}
