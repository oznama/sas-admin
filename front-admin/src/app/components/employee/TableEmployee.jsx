import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getEmployees } from "../../services/EmployeeService";
import { setMessage } from "../../../store/alert/alertSlice";
import { buildPayloadMessage } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';
import { Select } from '../custom/Select';
import { getCompanySelect } from '../../services/CompanyService';

export const TableEmployee = ({
    pageSize = 10,
    sort = 'name,asc',
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions } = useSelector( state => state.auth );

    const [currentPage, setCurrentPage] = useState(0);
    const [employees, setEmployees] = useState([]);
    const [totalEmployees, setTotalEmployees] = useState(0);
    const [filter, setFilter] = useState('')
    const [companyId, setCompanyId] = useState('');
    const [companies, setCompanies] = useState([]);

    const onChangeFilter = ({ target }) => setFilter(target.value);
    const onChangeCompany = ({ target }) => setCompanyId(target.value);

    const fetchEmployees = (page) => {
        getEmployees(page, pageSize, sort, filter, companyId)
            .then( response => {
                if( response.code && response.code === 401 ) {
                    dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
                }
                setEmployees(response.content);
                setTotalEmployees(response.totalElements);
            }).catch( error => {
                dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar los empleados, contacte al administrador', alertType.error)));
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
        fetchEmployees(currentPage);
    }, [currentPage]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchEmployees(currentPage);
    }

    const handleAddEmployee = () => {
        // dispatch(setCurrentTab(1));
        navigate(`/employee/add`);
    }

    const renderAddButton = () => /*permissions.canCreateProj && */(
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddEmployee }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const renderSearcher = () => (
        <div className="input-group w-50 pt-3">
            <select className="form-select" name="companyId" value={ companyId }  onChange={ onChangeCompany }>
                <option value=''>Seleccionar...</option>
                { companies && companies.map( option  => ( <option key={ option.id } value={ option.id }>{ option.value }</option> )) }
            </select>
            <input name="filter" type="text" className="form-control" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ onChangeFilter } />
            <button type="button" className="btn btn-outline-primary" onClick={ () => fetchEmployees(currentPage) }>
                <i className="bi bi-search"></i>
            </button>
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
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <td className="text-start" scope="row">{ fullName }</td>
            <th className="text-start">{ email }</th>
            { permissions.isAdminRoot && (<td className="text-start">{ company }</td>) }
            <td className="text-start">{ position }</td>
            <td className="text-start">{ boss }</td>
            { permissions.isAdminRoot && (<td className="text-start">{ createdBy }</td>) }
            { permissions.isAdminRoot && (<td className="text-center">{ creationDate }</td>) }
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center">
                <button type="button" className="btn btn-danger btn-sm" onClick={ () => deleteChild(id) }>
                    <span><i className="bi bi-trash"></i></span>
                </button>
            </td>
        </tr>
    ));

    return (
        <div>

            <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">Empleados</h3>
            </div>

            { renderSearcher() }

            { renderAddButton() }

            <div className='table-responsive text-nowrap'>

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
                            <th className="text-center fs-6" scope="col">Borrar</th>
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                </table>

            </div>
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalEmployees }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
        </div>
    )
}

TableEmployee.propTypes = {
    pageSize: PropTypes.number,
}
