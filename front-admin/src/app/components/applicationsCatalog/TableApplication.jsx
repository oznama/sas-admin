import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { deleteLogic, getAplicationsF, getApplicationByName } from '../../services/ApplicationService';
import { setApp } from '../../../store/application/applicationSlice';
import { useNavigate } from "react-router-dom";

export const TableApplication = ({
    pageSize = 10,
    sort = 'name,asc',
}) => {
    const dispatch = useDispatch();
    const { app } = useSelector( state => state.applicationReducer );
    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [totalCatalogApplications, setTotalCatalogApplication] = useState(0);  
    const [catalogApplications, setCatalogApplications] = useState([]);
    const [id, setId] = useState(null);
    const [filter, setFilter] = useState(app.name? app.name: '')
    const navigate = useNavigate();

    const onChangeFilter = async ({ target }) => {
        const value = target.value;
        setFilter(value);
        await fetchApplications(value); // Esperamos a que se actualice el estado antes de llamar fetchChilds
    };
    
    const fetchApplications = (page) => {
        getAplicationsF(page, pageSize, sort, filter)
        .then(response => {
            if (response.code && response.code === 401) {
                displayNotification(dispatch, response.message, alertType.error);
            }
            setCatalogApplications(response.content);
            setTotalCatalogApplication(response.length);
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    useEffect(() => {
        console.log('Se imprime variable global'+JSON.stringify(app, null, 2));
        fetchApplications(filter); // Ahora se ejecutará cada vez que filter cambie
    }, [filter]); 

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchApplications(filter); // Actualizado
    }

    const onCancelModal = () => {
        //dispatch( setModalChild(null) );
        fetchApplications(filter);
    }
    // const showModal = catalogChild => dispatch( setModalChild(  <FormApplication catalogChild={ catalogChild } onCancelModal={ onCancelModal } /> ) );

    const handleAddCompany = () => {
        dispatch(setApp({}))
        navigate(`/application/add`);
    }

    const renderAddButton = () => permissions.canCreateCat && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddCompany }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const renderSearcher = () => (
        <div className={`input-group w-${ permissions.canCreateCat ? '25' : '50' } py-3`}>
            {/* <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ async (e) => { await onChangeFilter(e); fetchChilds(filter); } } /> */}
                <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..." maxLength={ 100 } autoComplete='off' value={ filter } required onChange={ onChangeFilter }/>
            <button type="button" className="btn btn-outline-primary" onClick={ () => fetchApplications(filter) }>
                <i className="bi bi-search"></i>
            </button>
        </div>
    )

    const renderHeader = () => (
        <div className="d-flex justify-content-between align-items-center">
            { renderSearcher() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalCatalogApplications }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
            { renderAddButton() }
        </div>
    )

    const handledSelect = id => {
        console.log("El catalogChild es:"+id);
        console.log("El catalogChild es:"+id);
        const catalogChild = catalogApplications.find( cat => cat.name === id );
        console.log("El catalogChild es:"+JSON.stringify(catalogChild, null, 2));
        dispatch(setApp(catalogChild));
        navigate(`/application/add`);
        // showModal(catalogChild);
    }

    const renderStatus = (status) => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteChild = catalogId => {
        deleteLogic(catalogId).then( response => {
            if(response.code && response.code !== 200) {
            displayNotification(dispatch, response.message, alertType.error);
            } else {
            displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
            fetchApplications();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => catalogApplications && catalogApplications.map(({
        name,
        description,
        createdBy,
        creationDate,
        active
    }) => (
        <tr key={ name } onClick={ () => console.log('Click en row') }>
            <th className="text-center" scope="row">{ name }</th>
            <td className="text-start">{ description }</td>
            { permissions.isAdminRoot && (<td className="text-start">{ createdBy }</td>) }
            { permissions.isAdminRoot && (<td className="text-center">{ creationDate }</td>) }
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center">
                <button type="button" className={`btn btn-${ active && permissions.canEditCat ? 'success' : 'primary' } btn-sm`} onClick={ () => handledSelect(name) }>
                    <span><i className={`bi bi-${ active && permissions.canEditCat ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelCat && (
            <td className="text-center">
                <button type="button" className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`} onClick={ () => deleteChild(name, active) }>
                    <span><i className={`bi bi-${ active ? 'trash' : 'folder-symlink'}`}></i></span>
                </button>
            </td>
            )}
        </tr>
    ));

    return (
        <div>
            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">Aplicaciones</h3>
            </div>

            { renderHeader() }

            <div className='table-responsive text-nowrap' style={{ height: '350px' }}>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Nombre</th>
                            <th className="text-center fs-6" scope="col">Descripcion</th>
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Creado por</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Fecha creaci&oacute;n</th>) }
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

TableApplication.propTypes = {
    pageSize: PropTypes.number,
    catalogId: PropTypes.number,
}