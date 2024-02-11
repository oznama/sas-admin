import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { deleteLogic, getCompanies, getCompanySelect } from '../../services/CompanyService';
import { setCompanyName } from '../../../store/company/companySlice';

export const TableCompany = ({
    pageSize = 10,
    sort = 'name,asc',
    type,
    catalogId,
}) => {
    console.log(type+' '+catalogId);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { companyName } = useSelector( state => state.companyReducer );

    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [company, setCompany] = useState([]);
    const [totalCompanies, setTotalCompanies] = useState(0);
    const [filter, setFilter] = useState(companyName)
    const [companies, setCompanies] = useState([]);

    const onChangeFilter = ({ target }) => setFilter(target.value);
    console.log(companies);

    const fetchCompanies = (page) => {
        getCompanies(page, pageSize, sort, filter, '')
            .then( response => {
                if( response.code && response.code === 401 ) {
                    displayNotification(dispatch, response.message, alertType.error);
                }
                setCompany(response.content);
                setTotalCompanies(response.totalElements);
            }).catch( error => {
                console.log(error);
                displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
    }

    const fetchSelects = () => {
        
        getCompanySelect().then( response => {
            setCompanies(response);
        }).catch( error => {
            console.log(error);
        });
    }
    

    useEffect(() => {
        fetchSelects();
        fetchCompanies(currentPage);
    }, [currentPage]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchCompanies(currentPage);
    }

    const handleAddEmployee = () => {
        dispatch(setCompanyName(''));
        navigate(`/company/add`);
    }

    const renderAddButton = () => permissions.canCreateComp && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddEmployee }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const renderSearcher = () => (
        <div className={`input-group w-${ permissions.canCreateComp ? '25' : '50' } py-3`}>
            <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ async (e) => { await onChangeFilter(e); fetchCompanies(currentPage); } } />
            <button type="button" className="btn btn-outline-primary" onClick={ () => fetchCompanies(currentPage) }>
                <i className="bi bi-search"></i>
            </button>
        </div>
    )

    const renderHeader = () => (
        <div className="d-flex justify-content-between align-items-center">
            { renderSearcher() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalCompanies }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
            { renderAddButton() }
        </div>
    )

    const handledSelect = id => {
        // dispatch(setCurrentTab(2));
        navigate(`/company/${id}/edit`);
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteChild = companyID => {
        console.log('companyID: '+companyID);
        deleteLogic(companyID).then( response => {
            console.log('Response: '+response);
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
                fetchCompanies();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => company && company.map(({
        id,
        name,
        // alias,
        rfc,
        address,
        interior,
        exterior,
        // cp,
        // city,
        state,
        // country,
        phone,
        cellphone,
        ext,
        active
    }) => (
        <tr key={ id } onClick={ () => console.log('Click en row') }>
            <th className="text-center" scope="row">{ rfc }</th>
            <td className="text-start">{ name }</td>
            {/* <td className="text-start">{ alias }</td> */}
            {/* <td className="text-start">{ address }</td> */}
            <td className="text-start">{interior ? address+' '+interior : address+' '+exterior }</td>
            {/* <td className="text-start">{ cp }</td>
            <td className="text-start">{ city }</td>*/}
            <td className="text-start">{ state }</td>
            {/*<td className="text-start">{ country }</td> */}
            <td className="text-start">{ phone ? phone : cellphone  }</td>
            <td className="text-start">{ ext }</td>
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center">
                <button type="button" className={`btn btn-${ active && permissions.canEditComp ? 'success' : 'primary' } btn-sm`} onClick={ () => handledSelect(id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditComp ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelComp && (
            <td className="text-center">
                <button type="button" className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`} onClick={ () => deleteChild(id, active) }>
                    <span><i className={`bi bi-${ active ? 'trash' : 'folder-symlink'}`}></i></span>
                </button>
            </td>
            )}
        </tr>
    ));

    return (
        <div>
            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">Empresa</h3>
            </div>

            { renderHeader() }

            <div className='table-responsive text-nowrap' style={{ height: '350px' }}>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">RFC</th>
                            <th className="text-center fs-6" scope="col">Raz&oacute;n Social</th>
                            {/* <th className="text-center fs-6" scope="col">Alias</th> */}
                            <th className="text-center fs-6" scope="col">Direcci&oacute;n</th>
                            {/* <th className="text-center fs-6" scope="col">C&oacute;digo Postal</th>
                            <th className="text-center fs-6" scope="col">Ciudad</th> */}
                            <th className="text-center fs-6" scope="col">Estado</th>
                            {/* <th className="text-center fs-6" scope="col">Pais</th> */}
                            <th className="text-center fs-6" scope="col">Telefono</th>
                            <th className="text-center fs-6" scope="col">Extencion</th>
                            <th className="text-center fs-6" scope="col">Estatus</th>
                            <th className="text-center fs-6" scope="col">{permissions.canEditComp ? 'Editar' : 'Ver'}</th>
                            { permissions.canDelComp && (<th className="text-center fs-6" scope="col">Borrar</th>)}
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

TableCompany.propTypes = {
    pageSize: PropTypes.number,
    type: PropTypes.string,
    catalogId: PropTypes.number,
}
