import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { deleteLogic, getOrders, getOrdersByProjectId } from "../../../services/OrderService";
import { displayNotification, genericErrorMsg, styleInput, styleTableRow, styleTableRowBtn } from "../../../helpers/utils";
import { alertType } from "../../custom/alerts/types/types";
import { setCurrentOrdTab } from "../../../../store/project/projectSlice";
import { Pagination } from "../../custom/pagination/page/Pagination";

const pageSize = 10;
const sort = 'orderDate,desc';

export const TableOrders = ({
    projectId
}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );
    const { project } = useSelector( state => state.projectReducer );

    const [orders, setOrders] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);
    const [totalAmountPaid, setTotalAmountPaid] = useState(0);

    const [currentPage, setCurrentPage] = useState(0);
    const [totalOrders, setTotalOrders] = useState(0);
    const [filter, setFilter] = useState('');

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
                const { amount, tax, total, amountPaid } = response.find( r => r.orderNum === 'total' );
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
                setTotalAmountPaid( amountPaid );
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
    }, []);

    const handledSelect = (projectId, id) => {
        dispatch(setCurrentOrdTab(1));
        const urlRedirect = `/project/${ projectId }/order/${ id }/edit`;
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

    const renderSearcher = () => !projectId && (
        <div className="input-group w-25 py-1">
            <input name="filter" type="text" className="form-control" style={ styleInput } placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ onChangeFilter } />
            {/* <button type="button" className="btn btn-outline-primary" onClick={ () => fetchProjects(currentPage) }>
                <i className="bi bi-search"></i>
            </button> */}
        </div>   
    );

    const deleteOrder = (id, active) => {
        deleteLogic(id).then( response => {
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, `Orden ${ active ? 'eliminada' : 'reactivada' } correctamente!`, active ? alertType.warning : alertType.success);
                loadOrders();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderStatus = (status) => {
        const backColor = (status === 2000600003 || status === 2000600004) ? 'bg-danger' : ( status === 2000600002 ? 'bg-success' : 'bg-warning' );
        const statusDesc = status === 2000600004 ? 'Vencida' : (status === 2000600003 ? 'Cancelada' : ( status === 2000600002 ? 'Pagada' : 'Proceso' ));
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRequisitionStatus = (status) => {
        const backColor = (status === 2000600003 || status === 2000600004) ? 'bg-danger' : ( status === 2000600002 ? 'bg-success' : 'bg-warning' );
        const statusDesc = status === 2000600004 ? 'Vencida' : (status === 2000600003 ? 'Cancelada' : ( status === 2000600002 ? 'Pagada' : 'Proceso' ));
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRows = () => orders && orders.map(({
        id,
        orderNum,
        orderDate,
        amount,
        tax,
        total,
        amountPaid,
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
            <td className="text-center" style={ styleTableRow }>{ renderRequisitionStatus(requisitionStatus, '') }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ amount }</td>
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ tax }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary" style={ styleTableRow }>{ total }</td>) }
            <td className="text-end text-primary" style={ styleTableRow }>{ amountPaid }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" className={`btn btn-${ active && permissions.canEditOrd ? 'success' : 'primary' } btn-sm`} style={ styleTableRowBtn } onClick={ () => handledSelect(projectId, id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditOrd ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            {
                permissions.canDelOrd && (
                    <td className="text-center" style={ styleTableRow }>
                        <button type="button" className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`} style={ styleTableRowBtn } onClick={ () => deleteOrder(id, active) }>
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
                                <th></th>
                                <th></th>
                                <th></th>
                                <th></th>
                                <th className="text-end fs-6" scope="col">{ totalAmount }</th>
                                { permissions.isAdminRoot && (<th className="text-end fs-6" scope="col">{ totalTax }</th>) }
                                { permissions.isAdminRoot && (<th className="text-end fs-6" scope="col">{ totalT }</th>) }
                                <th className="text-end fs-6" scope="col">{ totalAmountPaid }</th>
                                { permissions.canEditOrd && (<th></th>) }
                                { permissions.canEditOrd && (<th></th>) }
                                { permissions.canDelOrd && (<th></th>) }
                            </tr>
                        </tfoot>
                    )}
                </table>
            </div>
        </div>
    );

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
