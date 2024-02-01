import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { deleteLogic, getOrders, getOrdersByProjectId } from "../../../services/OrderService";
import { displayNotification, genericErrorMsg, styleInput, styleTableRow, styleTableRowBtn } from "../../../helpers/utils";
import { alertType } from "../../custom/alerts/types/types";
import { setCurrentOrdTab, setCurrentTab, setPaid, setProject } from "../../../../store/project/projectSlice";
import { Pagination } from "../../custom/pagination/page/Pagination";

const pageSize = 10;
const sort = 'orderDate,desc';

export const TableOrders = ({
    projectId
}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );
    const { project, order, paid } = useSelector( state => state.projectReducer );

    const [orders, setOrders] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalAmountStr, setTotalAmountStr] = useState(0);
    const [totalTaxStr, setTotalTaxStr] = useState(0);
    const [totalTStr, setTotalTStr] = useState(0);
    const [totalAmountPaidStr, setTotalAmountPaidStr] = useState(0);
    const [totalStatus, setTotalStatus] = useState();

    const [currentPage, setCurrentPage] = useState(0);
    const [totalOrders, setTotalOrders] = useState(0);
    const [filter, setFilter] = useState(order && order.orderNum ? order.orderNum : '');

    const onChangeFilter = ({ target }) => {
        setFilter(target.value);
        fetchOrders(currentPage, target.value);
    };

    const fetchOrders = (page, filter) => {
        getOrders(page, pageSize, sort, filter).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setOrders(response.content);
                setTotalOrders(response.totalElements);
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const fetchOrdersByProject = () => {
        getOrdersByProjectId(projectId).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setOrders(response.filter( r => r.orderNum !== 'total' ));
                const orderTotal = response.find( r => r.orderNum === 'total' );
                dispatch(setPaid(orderTotal));
                const { amount, amountStr, taxStr, totalStr, amountPaidStr, status } = orderTotal;
                setTotalAmount( amount );
                setTotalAmountStr( amountStr );
                setTotalTaxStr( taxStr ) ;
                setTotalTStr( totalStr );
                setTotalAmountPaidStr( amountPaidStr );
                setTotalStatus( status );
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const loadOrders = () => {
        if( projectId ) {
            fetchOrdersByProject();
        } else {
            fetchOrders(currentPage, filter);
        }
    }

    useEffect(() => {
        loadOrders();
        if( !projectId ) {
            dispatch(setProject({}));
        }
    }, []);

    const handledSelect = (projectIdParam, id) => {
        if( !projectId ) {
            dispatch(setCurrentTab(3));
        }
        dispatch(setCurrentOrdTab(1));
        const urlRedirect = `/project/${ projectIdParam }/order/${ id }/edit`;
        navigate(urlRedirect);
    }

    const handleAddOrder = () => {
        dispatch(setCurrentOrdTab(1));
        navigate(`/project/${ projectId ? projectId : 0 }/order/add`);
    }

    const renderAddOrderButton = () => permissions.canCreateOrd && (
        <div className="d-flex justify-content-between p-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddOrder }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    const cleanSearcher = () => {
        setFilter('');
        setCurrentPage(0);
        fetchOrders(0, '');
    }

    const renderSearcher = () => !projectId && (
        <div className="input-group w-25 py-1">
            <input name="filter" type="text" className="form-control" style={ styleInput } placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ onChangeFilter } />
            <span className="input-group-text" id="basic-addon2" onClick={ () => cleanSearcher() }>
                <i className="bi bi-x-lg"></i>
            </span>
            {/* <button type="button" className="btn btn-outline-primary" onClick={ () => fetchProjects(currentPage) }>
                <i className="bi bi-search"></i>
            </button> */}
        </div>   
    );

    const deleteOrder = (id, amount, active) => {
        if( !active && (amount > (project.amount - paid.amountPaid) ) ) {
            displayNotification(dispatch, 'La orden no se puede reactivar ya que supera el monto pendiente', alertType.error);
        } else {
            deleteLogic(id).then( response => {
                if(response.code && response.code !== 200) {
                    displayNotification(dispatch, response.message, alertType.error);
                } else {
                    displayNotification(dispatch, `Orden ${ active ? 'cancelada' : 'reactivada' } correctamente!`, active ? alertType.warning : alertType.success);
                    loadOrders();
                }
            }).catch(error => {
                console.log(error);
                displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
        }
    }

    const renderStatus = (status) => {
        const backColor = status === 2000600004 ? 'bg-secondary' : (status === 2000600003 ? 'bg-danger' : ( status === 2000600002 ? 'bg-success' : 'bg-warning' ));
        const statusDesc = status === 2000600004 ? 'Vencida' : (status === 2000600003 ? 'Cancelada' : ( status === 2000600002 ? 'Pagada' : 'Proceso' ));
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRows = () => orders && orders.map(({
        id,
        orderNum,
        orderDate,
        amount,
        amountStr,
        taxStr,
        totalStr,
        amountPaid,
        amountPaidStr,
        status,
        requisition,
        requisitionDate,
        requisitionStatus,
        projectId,
        active
    }) => (
        <tr key={ id }>
            <th className="text-center" style={ styleTableRow } scope="row">{ orderNum }</th>
            <td className="text-center" style={ styleTableRow }>{ orderDate }</td>
            <td className="text-center" style={ styleTableRow }>{ renderStatus(status, '') }</td>
            <td className="text-center" style={ styleTableRow }>{ requisition }</td>
            <td className="text-center" style={ styleTableRow }>{ requisitionDate }</td>
            <td className="text-center" style={ styleTableRow }>{ renderStatus(requisitionStatus, '') }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ amountStr }</td>
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ taxStr }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ totalStr }</td>) }
            <td className="text-end text-primary" style={ styleTableRow }>{ amountPaidStr }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button"
                    className={`btn btn-${ active && permissions.canEditOrd ? 'success' : 'primary' } btn-sm`}
                    style={ styleTableRowBtn }
                    onClick={ () => handledSelect(projectId, id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditOrd ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            {
                permissions.canDelOrd && (
                    <td className="text-center" style={ styleTableRow }>
                        <button type="button"
                            className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`}
                            style={ styleTableRowBtn }
                            disabled={ status === 2000600002 || ( (status === 2000600003 || status === 2000600004) && paid.amountPaid >= project.amount ) }
                            onClick={ () => deleteOrder(id, amount, active) }>
                            <span><i className={`bi bi-${ active ? 'trash' : 'folder-symlink'}`}></i></span>
                        </button>
                    </td>
                )
            }
        </tr>
    ));

    const tableByProject = () => (
        <div>
            <div className={`d-flex ${ projectId ? 'flex-row-reverse' : 'justify-content-between align-items-center' }`}>
                { renderSearcher() }
                { renderAddOrderButton() }
            </div>
            <div className='table-responsive text-nowrap'>
                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">No. orden</th>
                            <th className="text-center fs-6" scope="col">Fecha No. De Orden</th>
                            <th className="text-center fs-6" scope="col">Status</th>
                            <th className="text-center fs-6" scope="col">No Requisici&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Fecha Requisici&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Status Requisici&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Iva</th>) }
                            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Total</th>) }
                            <th className="text-center fs-6" scope="col">Monto Pagado</th>
                            <th className="text-center fs-6" scope="col">{permissions.canEditOrd ? 'Editar' : 'Ver'}</th>
                            { permissions.canDelOrd && (<th className="text-center fs-6" scope="col">Borrar</th>) }
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                    { projectId && (
                        <tfoot className="thead-dark">
                            <tr>
                                <th className="text-center fs-6" scope="col">TOTALES</th>
                                <th></th>
                                <th className="text-center fs-6" scope="col">{ renderStatus(totalStatus, '') }</th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th className="text-end fs-6" scope="col">{ totalAmountStr }</th>
                                { permissions.isAdminRoot && (<th className="text-end fs-6" scope="col">{ totalTaxStr }</th>) }
                                { permissions.isAdminRoot && (<th className="text-end fs-6" scope="col">{ totalTStr }</th>) }
                                <th className="text-end fs-6" scope="col">{ totalAmountPaidStr }</th>
                                { permissions.canEditOrd && (<th></th>) }
                                { permissions.canDelOrd && (<th></th>) }
                            </tr>
                        </tfoot>
                    )}
                </table>
            </div>
        </div>
    );

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchOrders(page, filter);
    }

    const tablePaged = () => (
        <div className='px-5'>
            <div className="d-flex justify-content-center">
                <h1 className="fs-4 card-title fw-bold mb-1">Ordenes</h1>
            </div>
            { tableByProject() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalOrders }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
        </div>
    );

    return projectId ? tableByProject() : tablePaged();
}
