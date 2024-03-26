import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { useDispatch, useSelector } from 'react-redux';
import { getPendings } from '../../../services/ProjectService';
import { displayNotification, genericErrorMsg, styleTableRow, styleTableRowBtn } from '../../../helpers/utils';
import { alertType } from '../../custom/alerts/types/types';
import { InputSearcher } from '../../custom/InputSearcher';
import { Pagination } from '../../custom/pagination/page/Pagination';
import { setModalChild } from '../../../../store/modal/modalSlice';
import { FormPending } from './FormPending';

export const pendingType = {
    nxt: 'futures',
    crt: 'currents',
    due: 'pendings'
}

export const TablePendings = ({
    title,
    styleTitle,
    service,
    pageSize = 10
}) => {

    const dispatch = useDispatch();

    const { permissions } = useSelector( state => state.auth );

    const [currentPage, setCurrentPage] = useState(0);
    const [projects, setProjects] = useState([]);
    const [total, setTotal] = useState(0);
    const [filter, setFilter] = useState('');

    const onChangeFilter = ({ target }) => {
        setCurrentPage(0)
        setFilter(target.value);
        fetchProjects(0, target.value);
    };


    const fetchProjects = (page, filter) => {
        getPendings(service, page, pageSize, filter).then( response => {
            if( response.code && response.code === 401 ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setProjects(response.content);
                setTotal(response.totalElements);
            }
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
        fetchProjects(currentPage, filter);
    }

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        fetchProjects(0, '');
    }

    const onCancelModal = refresh => {
        dispatch( setModalChild(null) );
        if(refresh) {
            fetchProjects(0, filter);
        }
    }

    const handledSelect = application => {
        dispatch(setModalChild( <FormPending application={application} onCancelModal={onCancelModal} /> ));
    }

    const renderStatus = status => {
        const backColor = status ? 'success' : 'danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded bg-${backColor} text-white` }>{ desc }</span>);
    }

    const renderDate = (status, desc) => {
        const backColor = status === 2001000002 ? 'primary' : (status === 2001000001 ? 'danger' : ( status === 2001000003 ? 'success' : 'bg-warning' ));
        return (<span className={ `w-50 px-2 m-3 rounded bg-${backColor} text-white` }>{ desc }</span>);
    }

    const renderRows = projects => projects.map((pa, index) => (
        <tr key={ index }>
            <th className="text-center" style={ styleTableRow } scope="row">{ pa.application }</th>
            <td className="text-center" style={ styleTableRow }>{ pa.projectKey }</td>
            <td className="text-center" style={ styleTableRow }>{ pa.hours }</td>
            <td className="text-center" style={ styleTableRow }>{ pa.startDate }</td>
            <td className="text-center" style={ styleTableRow }>{ renderDate(pa.designStatus, pa.designDate) }</td>
            <td className="text-center" style={ styleTableRow }>{ renderDate(pa.developmentStatus, pa.developmentDate) }</td>
            <td className="text-center" style={ styleTableRow }>{ renderDate(pa.endStatus, pa.endDate) }</td>
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ pa.amount }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ pa.tax }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ pa.total }</td>) }
            <td className="text-start" style={ styleTableRow }>{ pa.leader }</td>
            <td className="text-start" style={ styleTableRow }>{ pa.developer }</td>
            { service !== pendingType.nxt && <td className="text-center">{ renderStatus(pa.active) }</td> }
            { service !== pendingType.nxt && !permissions.isAdminSas && <td className="text-center" style={ styleTableRow }>
                <button type="button" className="btn btn-success btn-sm" style={ styleTableRowBtn } onClick={ () => handledSelect(pa) }>
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td> }
        </tr>
    ));

    const renderTable = () => (
        <div className="border rounded">
            <div className="d-flex justify-content-between align-items-center mx-2">
                <h4 className={`card-title fw-bold text-${styleTitle}`}>Pendientes { title }</h4>
                { projects.length > 0 && <InputSearcher
                    name={ 'filter' }
                    placeholder={ 'Escribe para filtrar...' }
                    value={ filter }
                    onChange={ onChangeFilter }
                    onClean={ () => onClean() }
                    width={ '25' }
                />}
                <Pagination
                    currentPage={ currentPage + 1 }
                    totalCount={ total }
                    pageSize={ pageSize }
                    onPageChange={ page => onPaginationClick(page) } 
                />
            </div>
            <div className='table-responsive text-nowrap mx-2'>
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
                            { service !== pendingType.nxt && <th className="text-center fs-6" scope="col">Estatus</th> }
                            { service !== pendingType.nxt && !permissions.isAdminSas && <th className="text-center fs-6" scope="col">Editar</th> }
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows(projects) }
                    </tbody>
                </table>
            </div>
        </div>
    )

    return (
        projects && <div className='px-5'>
            { renderTable() }
        </div>
    )
}

TablePendings.propTypes = {
    title: PropTypes.string.isRequired,
    styleTitle: PropTypes.string.isRequired,
    service: PropTypes.string.isRequired,
    pageSize: PropTypes.number,
}