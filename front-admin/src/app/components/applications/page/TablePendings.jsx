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

export const TablePendings = ({
    pageSize = 10
}) => {

    const dispatch = useDispatch();

    const { user, permissions } = useSelector( state => state.auth );

    const nxt = 'futures';
    const crt = 'currents';
    const due = 'pendings'

    const [currentPendPage, setCurrentPendPage] = useState(0);
    const [pendings, setPendings] = useState([]);
    const [totalPendings, setTotalPendings] = useState(0);
    const [filterPending, setFilterPending] = useState('');

    const [currentCurrPage, setCurrentCurrPage] = useState(0);
    const [currents, setCurrents] = useState([]);
    const [totalCurrents, setTotalCurrents] = useState(0);
    const [filterCurrents, setFilterCurrents] = useState('');

    const [currentNextPage, setCurrentNextPage] = useState(0);
    const [nexts, setNexts] = useState([]);
    const [totalNexts, setTotalNexts] = useState(0);
    const [filterNexts, setFilterNexts] = useState('');

    const onChangeFilterPending = ({ target }) => {
        setCurrentPendPage(0)
        setFilterPending(target.value);
        fetchProjects(due, 0, target.value);
    };

    const onChangeFilterCurrents = ({ target }) => {
        setCurrentCurrPage(0)
        setFilterCurrents(target.value);
        fetchProjects(crt, 0, target.value);
    };

    const onChangeFilterNexts = ({ target }) => {
        setCurrentNextPage(0)
        setFilterNexts(target.value);
        fetchProjects(nxt, 0, target.value);
    };

    const fetchProjects = (service, page, filter) => {
        getPendings(service, page, pageSize, filter).then( response => {
            if( response.code && response.code === 401 ) {
                displayNotification(dispatch, response.message, alertType.error);
            }
            if( service === crt) {
                setCurrents(response.content);
                setTotalCurrents(response.totalElements);
            } else if ( service === due ) {
                setPendings(response.content);
                setTotalPendings(response.totalElements);
            } else if ( service === nxt ) {
                setNexts(response.content);
                setTotalNexts(response.totalElements);
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    useEffect(() => {
        fetchProjects(due, currentPendPage, filterPending);
        fetchProjects(crt, currentPendPage, filterPending);
        fetchProjects(nxt, currentPendPage, filterPending);
    }, [currentPendPage, currentCurrPage, currentNextPage]);

    const onPaginationClick = (page, service) => {
        if( service === crt) {
            setCurrentCurrPage(page);
            fetchProjects(crt, currentPendPage, filterPending);
        } else if ( service === due ) {
            setCurrentPendPage(page);
            fetchProjects(due, currentPendPage, filterPending);
        } else if ( service === nxt ) {
            setCurrentNextPage(page);
            fetchProjects(nxt, currentNextPage, filterNexts);
        }
    }

    const onClean = service => {
        if( service === crt) {
            setFilterCurrents('');
            setCurrentCurrPage(0);
            fetchProjects(crt, 0, '');
        } else if ( service === due ) {
            setFilterPending('');
            setCurrentPendPage(0);
            fetchProjects(due, 0, '');
        } else if ( service === nxt ) {
            setFilterNexts('');
            setCurrentNextPage(0);
            fetchProjects(nxt, 0, '');
        }
    }

    const onCancelModal = () => {
        dispatch( setModalChild(null) );
    }

    const handledSelect = application => {
        dispatch(setModalChild( <FormPending application={application} onCancelModal={onCancelModal} /> ));
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const renderRows = (service, projects) => projects.map((pa, index) => (
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
            { service !== nxt && <td className="text-center">{ renderStatus(pa.active) }</td> }
            { service !== nxt && <td className="text-center" style={ styleTableRow }>
                <button type="button" className="btn btn-success btn-sm" style={ styleTableRowBtn } onClick={ () => handledSelect(pa) }>
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td> }
        </tr>
    ));

    const renderTable = ({
        title,
        styleTitle,
        service,
        filterValue,
        onChangeFilter,
        projects,
        currentPage,
        totalCount
    }) => (
        <div className="border rounded">
            <div className="d-flex justify-content-between align-items-center mx-2">
                <h4 className={`card-title fw-bold text-${styleTitle}`}>Pendientes { title }</h4>
                { projects.length > 0 && <InputSearcher
                    name={ 'filter' }
                    placeholder={ 'Escribe para filtrar...' }
                    value={ filterValue }
                    onChange={ onChangeFilter }
                    onClean={ () => onClean(service) }
                    width={ '25' }
                />}
                <Pagination
                    currentPage={ currentPage + 1 }
                    totalCount={ totalCount }
                    pageSize={ pageSize }
                    onPageChange={ page => onPaginationClick(page, service) } 
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
                            { service !== nxt && <th className="text-center fs-6" scope="col">Estatus</th> }
                            { service !== nxt && <th className="text-center fs-6" scope="col">Editar</th> }
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows(service, projects) }
                    </tbody>
                </table>
            </div>
        </div>
    )

    const renderPendings = () => pendings && (
        <div className='px-5'>
            { 
                user.role.id === 3 ? <div>Pendientes de Selene</div>
                : renderTable({
                    title: 'Vencidos',
                    styleTitle: 'danger',
                    service: due,
                    filterValue: filterPending, 
                    onChangeFilter: onChangeFilterPending, 
                    projects: pendings, 
                    currentPage: currentPendPage, 
                    totalCount: totalPendings
                })
            }
        </div>
    )

    const renderCurrents = () => !permissions.isAdminSas && currents && (
        <div className='px-5'>
            { 
                renderTable({
                    title: 'Vigentes',
                    styleTitle: 'success',
                    service: crt,
                    filterValue: filterCurrents, 
                    onChangeFilter: onChangeFilterCurrents, 
                    projects: currents, 
                    currentPage: currentCurrPage, 
                    totalCount: totalCurrents
                })
            }
        </div>
    )

    const renderNexts = () => !permissions.isAdminSas && nexts && (
        <div className='px-5'>
            { 
                renderTable({
                    title: 'Próximos',
                    styleTitle: 'primary',
                    service: nxt,
                    filterValue: filterNexts, 
                    onChangeFilter: onChangeFilterNexts, 
                    projects: nexts, 
                    currentPage: currentNextPage, 
                    totalCount: totalNexts
                })
            }
        </div>
    )

    return (
        <div className="d-flex flex-column gap-3 my-3">
            { renderPendings() }
            { renderCurrents() }
            { renderNexts() }
        </div>
    )
}

TablePendings.propTypes = {
    pageSize: PropTypes.number,
}