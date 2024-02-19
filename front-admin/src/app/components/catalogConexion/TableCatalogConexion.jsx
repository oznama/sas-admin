import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { deleteLogic, getCatalogChilds } from '../../services/CatalogService';
import { setModalChild } from '../../../store/modal/modalSlice';
// import { FormApplication } from '../applications/page/FormApplication';

export const TableCatalogConexion = ({
    pageSize = 10,
    catalogId,
}) => {
    const dispatch = useDispatch();

    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [totalCatalogChilds, setTotalCatalogChilds] = useState(0);  
    const [catalogChilds, setCatalogChilds] = useState([]);
    const [id, setId] = useState(null);
    
    const [filter, setFilter] = useState('')

    const onChangeFilter = ({ target }) => setFilter(target.value);

    const fetchChilds = () => {
        getCatalogChilds(catalogId)
        .then( response => {
            if( response.code && response.code === 401 ) {
                displayNotification(dispatch, response.message, alertType.error);
            }
            setCatalogChilds(response);
            setTotalCatalogChilds(response.totalElements);
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
    }
    
    useEffect(() => {
        fetchChilds();
    }, [catalogId]);  

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchChilds(currentPage);
    }

    const onCancelModal = () => {
        dispatch( setModalChild(null) );
        fetchChilds();
    }
    // const showModal = catalogChild => dispatch( setModalChild(  <FormApplication catalogChild={ catalogChild } onCancelModal={ onCancelModal } /> ) );

    const handleAddCompany = () => {
        // showModal(null);
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
            <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ async (e) => { await onChangeFilter(e); fetchChilds(currentPage); } } />
            <button type="button" className="btn btn-outline-primary" onClick={ () => fetchChilds(currentPage) }>
                <i className="bi bi-search"></i>
            </button>
        </div>
    )

    const renderHeader = () => (
        <div className="d-flex justify-content-between align-items-center">
            { renderSearcher() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalCatalogChilds }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
            { renderAddButton() }
        </div>
    )

    const handledSelect = id => {
        setId(id);
        const catalogChild = catalogChilds.find( cat => cat.id === id );
        // showModal(catalogChild);
    }

    const renderStatus = (status) => {
        const backColor = status === 2000100003 ? 'bg-danger' : ( status === 2000100001 ? 'bg-success' : ( status === 2000100002 ? 'bg-warning' : '') );
        const statusDesc = status === 2000100003 ? 'Eliminado' : ( status === 2000100002 ? 'Inactivo' : ( status === 2000100001 ? 'Activo' : '') );
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white `}>{ statusDesc }</span>);
    }

    const deleteChild = catalogId => {
        deleteLogic(catalogId).then( response => {
            if(response.code && response.code !== 200) {
            displayNotification(dispatch, response.message, alertType.error);
            } else {
            displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
            fetchChilds();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => catalogChilds && catalogChilds.map(({
        id,
        description,
        value,
        status
    }) => (
        <tr key={ id } onClick={ () => console.log('Click en row') }>
            <th className="text-center" scope="row">{ value }</th>
            <td className="text-start">{ description }</td>
            <td className="text-center">{ renderStatus(status) }</td>
            <td className="text-center">
                <button type="button" className={`btn btn-${ status && permissions.canEditCat ? 'success' : 'primary' } btn-sm`} onClick={ () => handledSelect(id) }>
                    <span><i className={`bi bi-${ status && permissions.canEditCat ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelCat && (
            <td className="text-center">
                <button type="button" className={`btn btn-${ status ? 'danger' : 'warning'} btn-sm`} onClick={ () => deleteChild(id, status) }>
                    <span><i className={`bi bi-${ status ? 'trash' : 'folder-symlink'}`}></i></span>
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

TableCatalogConexion.propTypes = {
    pageSize: PropTypes.number,
    catalogId: PropTypes.number,
}
