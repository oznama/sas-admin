import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
//import { deleteLogic, getCompanies, getCompanySelect } from '../../services/CompanyService';
import { deleteLogic, getCatalogChilds, save, update } from '../../services/CatalogService';
import { setModalChild } from '../../../store/modal/modalSlice';
import { InputText } from '../custom/InputText';

export const TableApplication = ({
    pageSize = 10,
    catalogId,
}) => {
    console.log(catalogId);
    const dispatch = useDispatch();

    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [totalCatalogChilds, setTotalCatalogChilds] = useState(0);  
    const [catalogChilds, setCatalogChilds] = useState([]);
    const [id, setId] = useState(null);

    const [cValue, setCValue] = useState('');
    const onChangeCValue = ({ target }) => {
        console.log('Por aqui pasa el nombre: '+target.value)
        setCValue(target.value);
    }
    const [description, setDescription] = useState('');
    const onChangeDescription = ({ target }) => setDescription(target.value);
    
    const [filter, setFilter] = useState('')

    const showModal = () => dispatch( setModalChild(renderModal()) );

    const onSubmit = event => {
        event.preventDefault()
        const data = new FormData(event.target)
        const request = Object.fromEntries(data.entries())
        if (id){
            updateChild(request);
        }else{
            saveChild(request);
        }
        dispatch( setModalChild(null) )
    }

    const renderModal = () => {
        console.log('Value: '+cValue+' Description: '+description)
        return(
        <div className='p-5 bg-white rounded-3'>

            {/* TODO Formulario */}
            <div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                    
                    <div className="row text-start">
                        <div className='col-12'>
                            <InputText name='value' label='Nombre:*' placeholder='Escribe el nombre' value={ cValue } required onChange={ onChangeCValue } maxLength={ 250 } />
                        </div>
                    </div>
                    <div className="row text-start">
                        <div className='col-12'>
                            <InputText name='description' label='Descripcion:' placeholder='Escribe la descripcion' value={ description } required onChange={ onChangeDescription } maxLength={ 250 } />
                        </div>
                    </div>
                    <div className="pt-3 d-flex flex-row-reverse">
                        <button type="submit" className="btn btn-primary">Guardar</button>
                        <button type="button" className="btn btn-danger" onClick={ () => dispatch( setModalChild(null) ) }>Cancelar</button>
                    </div>
                </form>
            </div>
            {/* TODO Forumulario */}
        </div>
    )}

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
    

    // const fetchSelects = () => {
        
    //     getCompanySelect().then( response => {
    //         setCompanies(response);
    //     }).catch( error => {
    //         console.log(error);
    //     });
    // }
    

    // useEffect(() => {
    //     // fetchSelects();
    //     fetchChilds(currentPage);
    // }, [currentPage]);
    
    useEffect(() => {
        fetchChilds();
        if (id) {
            const catSelected = catalogChilds.find(cat => cat.id === id);
            setCValue(catSelected.value);
            setDescription(catSelected.description ?? '');
            showModal();
        }
    }, [id]);  

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchChilds(currentPage);
    }

    const handleAddCompany = () => {
        // navigate(`/company/add`);
        setCValue('');
        setDescription('');
        showModal();
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
        const catSelected = catalogChilds.find( cat => cat.id === id );
        console.log(catSelected);
        setCValue(catSelected.value);
        setDescription(catSelected.description ?? '');
        showModal();
    }

    const renderStatus = (status) => {
        const backColor = status === 2000100003 ? 'bg-danger' : ( status === 2000100001 ? 'bg-success' : ( status === 2000100002 ? 'bg-warning' : '') );
        const statusDesc = status === 2000100003 ? 'Eliminado' : ( status === 2000100002 ? 'Inactivo' : ( status === 2000100001 ? 'Activo' : '') );
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white `}>{ statusDesc }</span>);
    }

    const saveChild = request => {
        save(request).then( response => {
            if(response.code && response.code !== 201) {
            displayNotification(dispatch, response.message, alertType.error);
            } else {
            displayNotification(dispatch, '¡El registro se ha creado correctamente!', success);
            setCatalogChilds([...catalogChilds, response]);
            cleanForm();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const updateChild = request => {
        update(id, request).then( response => {
            if(response.code && response.code !== 201) {
            displayNotification(dispatch, response.message, alertType.error);
            } else {
            displayNotification(dispatch, '¡El registro se ha actualizado correctamente!', alertType.success);
            fetchChilds();
            cleanForm();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
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

TableApplication.propTypes = {
    pageSize: PropTypes.number,
    catalogId: PropTypes.number,
}
