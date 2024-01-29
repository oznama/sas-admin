import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getEmployees, deleteLogic } from "../../services/EmployeeService";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { getCompanies, getCompanySelect } from '../../services/CompanyService';

export const TableClient = ({
    pageSize = 10,
    sort = 'name,asc',
    type,
    catalogId,
}) => {
    console.log(type+' '+catalogId);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [employees, setEmployees] = useState([]);
    const [totalEmployees, setTotalEmployees] = useState(0);
    const [filter, setFilter] = useState('')
    const [companyId, setCompanyId] = useState('') ;
    const [companies, setCompanies] = useState([]);

    const onChangeFilter = ({ target }) => setFilter(target.value);
    const onChangeCompany = ({ target }) => setCompanyId(target.value);
    console.log(companies);

    const fetchCompanies = (page) => {
        getCompanies(page, pageSize, sort, filter, '2000900002')
            .then( response => {
                if( response.code && response.code === 401 ) {
                    displayNotification(dispatch, response.message, alertType.error);
                }
                setEmployees(response.content);
                setTotalEmployees(response.totalElements);
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
        // navigate(`/client/add`);
    }

    const renderAddButton = () => permissions.canCreateEmp && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddEmployee }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const renderSearcher = () => (
        <div className={`input-group w-${ permissions.canCreateEmp ? '25' : '50' } py-3`}>
            {/* <select className="form-select" name="companyId" value={ companyId }  onChange={ onChangeCompany }>
                <option value=''>Seleccionar...</option>
                {/* { companies && companies.map( option  => ( <option key={ option.id } value={ option.id }>{ option.value }</option> )) } */}
                { /* companies && companies.filter(company => company.type === catalogId).map(option => (
                <option key={option.id} value={option.id}>
                    {option.value}
                </option>
                    ))
                }
            </select> */}
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
                totalCount={ totalEmployees }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
            { renderAddButton() }
        </div>
    )

    const handledSelect = id => {
        // dispatch(setCurrentTab(2));
        // navigate(`/employee/${id}/edit`);
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteClient = employeeID => {
        console.log('employeeID; '+employeeID);
        // deleteLogic(employeeID).then( response => {
        //     console.log('Response: '+response);
        //     if(response.code && response.code !== 200) {
        //         displayNotification(dispatch, response.message, alertType.error);
        //     } else {
        //         displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
        //         fetchCompanies();
        //     }
        // }).catch(error => {
        //     console.log(error);
        //     displayNotification(dispatch, genericErrorMsg, alertType.error);
        // });
    }

    const renderRows = () => employees && employees.map(({
        id,
        name,
        rfc,
        address,
        phone,
        active
    }) => (
        <tr key={ id } onClick={ () => console.log('Click en row') }>
            <th className="text-center" scope="row">{ rfc }</th>
            <td className="text-start">{ name }</td>
            <td className="text-start">{ address }</td>
            <td className="text-start">{ phone }</td>
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center">
                <button type="button" className="btn btn-success btn-sm" onClick={ () => handledSelect(id) }>
                    <span><i className={`bi bi-${permissions.canEditEmp ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelEmp && (
            <td className="text-center">
                <button type="button" className="btn btn-danger btn-sm" onClick={ () => deleteClient(id) }>
                    <span><i className="bi bi-trash"></i></span>
                </button>
            </td>
            )}
        </tr>
    ));

    return (
        <div>
            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">Clientes</h3>
            </div>

            { renderHeader() }

            <div className='table-responsive text-nowrap' style={{ height: '350px' }}>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">RFC</th>
                            <th className="text-center fs-6" scope="col">Nombre</th>
                            <th className="text-center fs-6" scope="col">Direcci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Telefono</th>
                            <th className="text-center fs-6" scope="col">Estatus</th>
                            <th className="text-center fs-6" scope="col">{permissions.canEditEmp ? 'Editar' : 'Ver'}</th>
                            { permissions.canDelEmp && (<th className="text-center fs-6" scope="col">Borrar</th>)}
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

TableClient.propTypes = {
    pageSize: PropTypes.number,
    type: PropTypes.string,
    catalogId: PropTypes.number,
}
