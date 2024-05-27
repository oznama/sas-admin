import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getUsers, getUsersS, deleteLogic, getUserById } from "../../services/UserService";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
//import { getCompanyById, getCompanySelect } from '../../services/CompanyService';
import { setCompanyID,setEmployeesObj,setRoleObj } from '../../../store/user/userSlice';
//import { setCompanyS, setEmployeeS, setCompanyObj} from '../../../store/company/companySlice';

export const TableUser = ({
    pageSize = 10,
    sort = 'name,asc',
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    // const { companyS } = useSelector( state => state.companyReducer );
    const { employeesObj } = useSelector( state => state.userReducer );
    const { permissions, user } = useSelector( state => state.auth );
    const [currentPage, setCurrentPage] = useState(0);
    const [users, setUsers] = useState([]);
    const [totalUsers, setTotalUsers] = useState(0);
    console.log('employeesObj',employeesObj)
    const [filter, setFilter] = useState(employeesObj.employee ? employeesObj.employee.name:''); 
    // const [filter, setFilter] = useState(''); 
    // const [companyId, setCompanyId] = useState(0) ;
    // const [companies, setCompanies] = useState([]);

    const onChangeFilter = ({ target }) => { 
        // console.log('Filtro: ',filter);
        // console.log('Usuarios: ',users);
        setFilter(target.value);
        fetchUsers(currentPage);
    };
    // const onChangeCompany = ({ target }) => {
    //     setCompanyId(target.value);
    //     //dispatch(setCompanyS(target.value));
    //     const companyIds = parseInt(target.value, 10);
    //     setCompanyId(companyIds);
    //     const selectedCompany = companies.find(company => company.id === companyIds);
    //     if (selectedCompany) {
    //         dispatch(setCompanyObj(selectedCompany));
    //     }else{
    //         dispatch(setCompanyObj({}));
    //     }
    //     fetchEmployees(currentPage);
    // };

    const fetchUsers = (page) => {
        // getUsers(page, pageSize, sort, filter, companyId)
        //console.log('Filter: ', filter);
        getUsers(page, pageSize, sort, filter)
            .then( response => {
                if( response.code && response.code === 401 ) {
                    displayNotification(dispatch, response.message, alertType.error);
                }
                // console.log('resultado', response);
                setUsers(response.content);
                setTotalUsers(response.totalElements);
            }).catch( error => {
                console.log(error);
                displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
        
        getUsersS().then( response => {
            if( response.code && response.code === 401 ) {
                displayNotification(dispatch, response.message, alertType.error);
            }
        })
    }

    // const fetchSelects = () => {
    //     getCompanySelect().then( response => {
    //         setCompanies(response);
    //         // console.log('Valor de companies: '+JSON.stringify(response, null, 2));
    //     }).catch( error => {
    //         console.log(error);
    //     });
    // }
    

    useEffect(() => {
        //fetchSelects();
        fetchUsers(currentPage);
    }, [currentPage, filter, pageSize, sort]);
    //    }, [currentPage, filter, pageSize, sort, companyId]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchUsers(currentPage);
    }

    const handleAddEmployee = () => {
        dispatch(setEmployeesObj([]));
        dispatch(setRoleObj([]));
        navigate(`/users/add`);
    }

    const renderAddButton = () => permissions.canCreateEmp && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddEmployee }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        // setCompanyId(0);
        fetchUsers(currentPage);
    }

    const renderSearcher = () => (
        <div className={`input-group w-${ permissions.canCreateEmp ? '25' : '50' } py-3`}>
            {/* <select className="form-select" name="companyId" value={ companyId }  onChange={ onChangeCompany }>
                <option value=''>Seleccionar...</option>
                { companies && companies.map( option  => ( <option key={ option.id } value={ option.id }>{ option.value }</option> )) }
            </select> */}
            <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ async (e) => { await onChangeFilter(e); fetchUsers(currentPage); } } />
            <button type="button" className="btn btn-outline-secondary" onClick={onClean}>
                <i className="bi bi-x"></i>
            </button>
            <button type="button" className="btn btn-outline-primary" onClick={ () => fetchUsers(currentPage) }>
                <i className="bi bi-search"></i>
            </button>
        </div>
    )

    const renderHeader = () => (
        <div className="d-flex justify-content-between align-items-center">
            { renderSearcher() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalUsers }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
            { renderAddButton() }
        </div>
    )

    const handledSelect = id => {
        console.log('id: {}',id)
        getUserById(id).then( response => {
            if( response.code && response.code === 401 ) {
                displayNotification(dispatch, response.message, alertType.error);
            }
            console.log('response',response);
            dispatch(setEmployeesObj((response)));
        })
        // dispatch(setRoleObj([]));
        navigate(`/users/${id}/edit`);
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteEmployee = roleID => {
        deleteLogic(roleID).then( response => {
            //console.log('Response: '+response);
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
                fetchUsers();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }
    console.log('Usuario: ',users)
    const renderRows = () => users && users.map(({
        employee,
        role,
        id,
        active
    }) => (
        <tr key={ id } onClick={ () => console.log('Click en row') }>
            { permissions.isAdminRoot && (<td className="text-start">{ id }</td>) }
            <td className="text-start" scope="row">{ employee.name+' '+employee.surname }</td>
            <th className="text-start">{ employee.email }</th>
            <th className="text-start">{ role.name }</th>
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center">
                <button type="button" className={`btn btn-${ active && permissions.canEditComp ? 'success' : 'primary' } btn-sm`} onClick={ () => handledSelect(id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditComp ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            { permissions.canDelEmp && (
            <td className="text-center">
                <button type="button" className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`} onClick={ () => deleteEmployee(id) }>
                    <span><i className={`bi bi-${ role.active ? 'trash' : 'folder-symlink'}`}></i></span>
                </button>
            </td>
            )}
        </tr>
    ));

    return (
        <div>
            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">{`Usuarios`}</h3>
            </div>

            { renderHeader() }

            <div className='table-responsive text-nowrap' style={{ height: '350px' }}>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">ID</th>) }
                            <th className="text-center fs-6" scope="col">Nombre</th>
                            <th className="text-center fs-6" scope="col">Correo</th>
                            <th className="text-center fs-6" scope="col">Tipo de usuario</th>
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

TableUser.propTypes = {
    pageSize: PropTypes.number,
}
