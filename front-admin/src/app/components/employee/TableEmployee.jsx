import PropTypes from 'prop-types';
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getEmployees } from "../../services/EmployeeService";
import { setMessage } from "../../../store/alert/alertSlice";
import { buildPayloadMessage } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from '../custom/pagination/page/Pagination';

export const TableEmployee = ({
    pageSize = 10,
    sort = 'id,asc',
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions } = useSelector( state => state.auth );

    const [currentPage, setCurrentPage] = useState(0);
    const [employees, setEmployees] = useState([]);
    const [totalEmployees, setTotalEmployees] = useState(0);
    const [filter, setFilter] = useState('')

    const onChangeFilter = ({ target }) => setFilter(target.value);

    const fetchEmployees = (page) => {
        getEmployees(page, pageSize, sort, filter)
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

    useEffect(() => {
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
    
    const renderRows = () => employees && employees.map(({
        id,
        email,
        name,
        secondName,
        surname,
        secondSurname
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <th className="text-start" scope="row">{ email }</th>
            <td className="text-start">{ `${name} ${secondName ? secondName : ''} ${surname} ${secondSurname ? secondSurname : ''}` }</td>
        </tr>
    ));

    return (
        <div>
            { renderSearcher() }

            { renderAddButton() }

            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Correo</th>
                            <th className="text-center fs-6" scope="col">Nombre</th>
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
