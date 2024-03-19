import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { getPendings } from '../../../services/ProjectService';
import { displayNotification, styleTableRow, styleTableRowBtn } from '../../../helpers/utils';
import { alertType } from '../../custom/alerts/types/types';
import { InputSearcher } from '../../custom/InputSearcher';
import { Pagination } from '../../custom/pagination/page/Pagination';
import { setModalChild } from '../../../../store/modal/modalSlice';
import { FormPending } from './FormPending';

export const TablePendings = ({
    pageSize = 10
}) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions } = useSelector( state => state.auth );
    const { project } = useSelector( state => state.projectReducer );

    const [currentPage, setCurrentPage] = useState(0);
    const [projects, setProjects] = useState([]);
    const [totalProjects, setTotalProjects] = useState(0);
    const [filter, setFilter] = useState( project &&  project.key ? project.key : '');

    const onChangeFilter = ({ target }) => {
        setCurrentPage(0)
        setFilter(target.value);
        fetchProjects(0, target.value);
    };

    const fetchProjects = (page, filter) => {
        getPendings(page, pageSize, filter)
            .then( response => {
                if( response.code && response.code === 401 ) {
                    displayNotification(dispatch, response.message, alertType.error);
                }
                setProjects(response.content);
                setTotalProjects(response.totalElements);
            }).catch( error => {
                console.log(error);
                displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
    }

    useEffect(() => {
        fetchProjects(currentPage, filter);
    }, [currentPage]);

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchProjects(page, filter);
    }

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        fetchProjects(0, '');
    }

    const onCancelModal = () => {
        dispatch( setModalChild(null) );
    }

    const handledSelect = application => {
        //dispatch(setCurrentTab( permissions.canEditProj ? 1 : 2));
        //navigate(`/project/${key}/edit`);
        dispatch(setModalChild( <FormPending application={application} onCancelModal={onCancelModal} /> ));
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const renderRows = () => projects && projects.map((pa, index) => (
        <tr key={ index }>
            <th className="text-center" style={ styleTableRow } scope="row">{ pa.application }</th>
            <td className="text-center" style={ styleTableRow }>{ pa.projectKey }</td>
            <td className="text-center" style={ styleTableRow }>{ pa.hours }</td>
            <td className="text-center" style={ styleTableRow }>{ pa.startDate }</td>
            <td className="text-center" style={ styleTableRow }>{ pa.designDate }</td>
            <td className="text-center" style={ styleTableRow }>{ pa.developmentDate }</td>
            <td className="text-center" style={ styleTableRow }>{ pa.endDate }</td>
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ pa.amount }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ pa.tax }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ pa.total }</td>) }
            <td className="text-start" style={ styleTableRow }>{ pa.leader }</td>
            <td className="text-start" style={ styleTableRow }>{ pa.developer }</td>
            <td className="text-center">{ renderStatus(pa.active) }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" className="btn btn-success btn-sm" style={ styleTableRowBtn } onClick={ () => handledSelect(pa) }>
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td>
        </tr>
    ));

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center">
                { <InputSearcher name={ 'filter' } placeholder={ 'Escribe para filtrar...' } value={ filter } onChange={ onChangeFilter } onClean={ onClean } /> }
            </div>
            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Aplicaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Clave</th>
                            <th className="text-center fs-6" scope="col">Horas</th>
                            <th className="text-center fs-6" scope="col">Fecha de inicio</th>
                            <th className="text-center fs-6" scope="col">Analisis y Dise&ntilde;o</th>
                            <th className="text-center fs-6" scope="col">Construcci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Cierre</th>
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Monto</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Iva</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Total</th>) }
                            <th className="text-center fs-6" scope="col">L&iacute;der SAS</th>
                            <th className="text-center fs-6" scope="col">Desarrollador SAS</th>
                            <th className="text-center fs-6" scope="col">Estatus</th>
                            <th className="text-center fs-6" scope="col">Editar</th>
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                </table>

            </div>
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalProjects }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
        </div>
    )
}

TablePendings.propTypes = {
    pageSize: PropTypes.number,
}