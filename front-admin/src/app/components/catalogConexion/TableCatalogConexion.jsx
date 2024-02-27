import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { displayNotification, genericErrorMsg, handleDateStr } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { deleteLogic, getCatalogChilds } from '../../services/CatalogService';
import { setCatalogName, setCatalogObj, setCatalogParent } from '../../../store/catalog/catalogSlice';
import { useNavigate } from "react-router-dom";
import { InputSearcher } from '../custom/InputSearcher';

export const TableCatalogConexion = ({
    pageSize = 10,
    catalogId,
}) => {

    const dispatch = useDispatch();
    const { catalogParent } = useSelector( state => state.catalogReducer );
    const { obj } = useSelector( state => state.catalogReducer );
    const { catalogName } = useSelector( state => state.catalogReducer );
    const [title, setTitle] = useState('');
    const [type, setType] = useState('');
    const [category, setCategory] = useState('');

    const navigate = useNavigate();
    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [totalCatalogChilds, setTotalCatalogChilds] = useState(0);  
    const [catalogChilds, setCatalogChilds] = useState([]);
    const [id, setId] = useState(null);
    console.log("Obj viene asi... ",obj);
    console.log("Catalg parent es: ", catalogParent);
    console.log("Catalg id es: ", catalogId);
    console.log("El primer resultado es: "+ (obj.value && (catalogParent === catalogId)));
    console.log("Type es igual a... ", catalogName)
    const [filter, setFilter] = useState((catalogName && (catalogParent === catalogId)) ? ( type === 'days' ? handleDateStr(catalogName) : catalogName) : '');

    const onChangeFilter = ({ target }) => setFilter(target.value.toLowerCase());

    const fetchChilds = catalogId => {
        getCatalogChilds(catalogId)
        .then( response => {
            if( response.code && response.code === 401 ) {
                displayNotification(dispatch, response.message, alertType.error);
            }
            if (filter==='') {
                setCatalogChilds(response);
                setTotalCatalogChilds(response.totalElements);
            }else{
                console.log('Prueba de filter es: ', filter);
                const filteredCatalogChilds = response.filter(child => child.value.toLowerCase().includes(filter));
                setCatalogChilds(filteredCatalogChilds);
                setTotalCatalogChilds(filteredCatalogChilds.totalElements);
            }
            
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
    }
    
    useEffect(() => {
        
        if (catalogId == 1000000005) {
            setTitle('Puestos de trabajo');
            setType('role');
            setCategory('Roles');
        } else if (catalogId == 1000000009) {
            setTitle('Tipos de empresas');
            setType('companyType');
            setCategory('Tipos de empresa');
        } else {
            setTitle('Días feriados');
            setType('days');
            setCategory('Días feriados');
        }
        if (catalogParent !== catalogId) {
            dispatch(setCatalogObj({}));
            dispatch(setCatalogName(''));
            setCurrentPage(0);
            setFilter('');
            dispatch(setCatalogParent(catalogId));
        }
        fetchChilds(catalogId);
    }, [catalogId, catalogParent, filter]);  

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchChilds(currentPage);
    }

    const handleAddCatalog = () => {
        dispatch(setCatalogObj({}));
        dispatch(setCatalogName(''));
        dispatch(setCatalogParent(catalogId));
        navigate(`/`+type+`/add`);
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
        fetchChilds(catalogId);
    }

    const renderSearcher = () => (
        <div className={`input-group w-${ permissions.canCreateCat ? '40' : '50' } py-3`}>
            { <InputSearcher name={ 'filter' } placeholder={ 'Escribe para filtrar...' } value={ filter } onChange={ onChangeFilter } onClean={ onClean } /> }
            {/* <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ async (e) => { await onChangeFilter(e); fetchChilds(currentPage); } } onClean={ onClean }/> */}
            {/* <button type="button" className="btn btn-outline-primary" onClick={ () => fetchChilds(currentPage) }>
                <i className="bi bi-search"></i>
            </button> */}
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
        dispatch(setCatalogObj(catalogChild));
        dispatch(setCatalogParent(catalogId));
        dispatch(setCatalogName((catalogChild.value+'').toLowerCase()));
        navigate(`/`+type+`/add`);
        // showModal(catalogChild);
    }

    const renderStatus = status => {
        const backColor = status === 2000100003 ? 'bg-danger' : ( status === 2000100001 ? 'bg-success' : ( status === 2000100002 ? 'bg-warning' : '') );
        const statusDesc = status === 2000100003 ? 'Eliminado' : ( status === 2000100002 ? 'Inactivo' : ( status === 2000100001 ? 'Activo' : '') );
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white `}>{ statusDesc }</span>);
    }

    const deleteChild = catalog => {
        dispatch(setCatalogObj(catalog));
        deleteLogic(catalog.id).then( response => {
            if(response.code && response.code !== 200) {
            displayNotification(dispatch, response.message, alertType.error);
            } else {
            displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
            fetchChilds(catalogId);
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => catalogChilds && catalogChilds.map((catalog) => (
        <tr key={ catalog.id } >
            { permissions.isAdminRoot && <th className="text-center" scope="row">{ catalog.id }</th> }
            <th className="text-center" scope="row">{ catalog.value }</th>
            <td className="text-start">{ catalog.description }</td>
            <td className="text-center">{ renderStatus(catalog.status) }</td>
            <td className="text-center">
                <button type="button" className={`btn btn-${ permissions.canEditCat && (catalog.status === 2000100001)  ? 'success' : 'primary' } btn-sm`} onClick={ () => handledSelect(catalog.id) }>
                    <span><i className={`bi bi-${ permissions.canEditCat && (catalog.status === 2000100001) ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelCat && (
            <td className="text-center">
                <button type="button" className={`btn btn-${ (catalog.status === 2000100001) ? 'danger' : 'warning'} btn-sm`} onClick={ () => deleteChild(catalog) }>
                    <span><i className={`bi bi-${ (catalog.status === 2000100001) ? 'trash' : 'folder-symlink'}`}></i></span>
                </button>
            </td>
            )}
        </tr>
    ));

    return (
        <div>
            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">{title}</h3>
            </div>

            { renderHeader() }

            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            { permissions.isAdminRoot && <th className="text-center fs-6" scope="col">Id</th> }
                            <th className="text-center fs-6" scope="col">{category}</th>
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
