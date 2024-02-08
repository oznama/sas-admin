import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getEmployees, deleteLogic } from "../../services/EmployeeService";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { getCompanySelect } from '../../services/CompanyService';
import { setCompanyS, setEmployeeS} from '../../../store/company/companySlice';

export const TableEmployee = ({
    pageSize = 10,
    sort = 'name,asc',
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { companyS } = useSelector( state => state.companyReducer );
    const { employeeS } = useSelector( state => state.companyReducer );

    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [employees, setEmployees] = useState([]);
    const [totalEmployees, setTotalEmployees] = useState(0);
    const [filter, setFilter] = useState(employeeS.name ? employeeS.name : ''); 
    const [companyId, setCompanyId] = useState(companyS) ;
    const [companies, setCompanies] = useState([]);

    const onChangeFilter = ({ target }) => setFilter(target.value);
    const onChangeCompany = ({ target }) => {
        setCompanyId(target.value);
        dispatch(setCompanyS(target.value));
    };

    const fetchEmployees = (page) => {
        getEmployees(page, pageSize, sort, filter, companyId)
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
    console.log('Valor del reducer: '+companyS);

    const fetchSelects = () => {
        
        getCompanySelect().then( response => {
            setCompanies(response);
        }).catch( error => {
            console.log(error);
        });
    }
    

    useEffect(() => {
        fetchSelects();
        fetchEmployees(currentPage);
    }, [currentPage]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchEmployees(currentPage);
    }

    const handleAddEmployee = () => {
        dispatch(setEmployeeS(''));
        navigate(`/employee/add`);
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
            <select className="form-select" name="companyId" value={ companyId }  onChange={ onChangeCompany }>
                <option value=''>Seleccionar...</option>
                { companies && companies.map( option  => ( <option key={ option.id } value={ option.id }>{ option.value }</option> )) }
            </select>
            <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ async (e) => { await onChangeFilter(e); fetchEmployees(currentPage); } } />
            <button type="button" className="btn btn-outline-primary" onClick={ () => fetchEmployees(currentPage) }>
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
        navigate(`/employee/${id}/edit`);
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteEmployee = employeeID => {
        console.log('employeeID; '+employeeID);
        deleteLogic(employeeID).then( response => {
            console.log('Response: '+response);
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
                fetchEmployees();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => employees && employees.map(({
        id,
        email,
        phone,
        fullName,
        company,
        position,
        boss,
        createdBy,
        creationDate,
        active
    }) => (
        <tr key={ id } onClick={ () => console.log('Click en row') }>
            <td className="text-start" scope="row">{ fullName }</td>
            <th className="text-start">{ email }</th>
            { permissions.isAdminRoot && (<td className="text-start">{ company }</td>) }
            <td className="text-start">{ position }</td>
            <td className="text-start">{ boss }</td>
            { permissions.isAdminRoot && (<td className="text-start">{ createdBy }</td>) }
            { permissions.isAdminRoot && (<td className="text-center">{ creationDate }</td>) }
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center">
                <button type="button" className={`btn btn-${ active && permissions.canEditComp ? 'success' : 'primary' } btn-sm`} onClick={ () => handledSelect(id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditComp ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelEmp && (
            <td className="text-center">
                <button type="button" className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`} onClick={ () => deleteEmployee(id) }>
                    <span><i className={`bi bi-${ active ? 'trash' : 'folder-symlink'}`}></i></span>
                </button>
            </td>
            )}
        </tr>
    ));

    return (
        <div>
            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">{`Empleados${!permissions.isAdminRoot ? ` > `+user.company : ''}`}</h3>
            </div>

            { renderHeader() }

            <div className='table-responsive text-nowrap' style={{ height: '350px' }}>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Nombre</th>
                            <th className="text-center fs-6" scope="col">Correo</th>
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Empresa</th>) }
                            <th className="text-center fs-6" scope="col">Posici&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Jefe</th>
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Creado por</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Fecha creaci&oacute;n</th>) }
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

TableEmployee.propTypes = {
    pageSize: PropTypes.number,
}
