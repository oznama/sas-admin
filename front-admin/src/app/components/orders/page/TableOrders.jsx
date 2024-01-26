import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getOrders, getOrdersByProjectId } from "../../../services/OrderService";
import { displayNotification, genericErrorMsg } from "../../../helpers/utils";
import { alertType } from "../../custom/alerts/types/types";

export const TableOrders = ({
    projectId
}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );

    const [orders, setOrders] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);
    const [totalAmountPaid, setTotalAmountPaid] = useState(0);

    const fetchOrders = () => {
        getOrders(null,null,null,null).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setOrders(response.content.filter( r => r.orderNum !== 'total' ));
                const { amount, tax, total, amountPaid } = response.content.find( r => r.orderNum === 'total' );
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

    useEffect(() => {
        if( projectId ) {
            fetchOrdersByProject();
        } else {
            fetchOrders();
        }
    }, []);

    const handledSelect = (projectId, id) => {
        const urlRedirect = `/project/${ projectId }/order/${ id }/edit`;
        navigate(urlRedirect);
    }

    const renderStatus = (status) => {
        const backColor = status === 2000600003 ? 'bg-danger' : ( status === 2000600002 ? 'bg-success' : 'bg-warning' );
        const statusDesc = status === 2000600003 ? 'Cancelada' : ( status === 2000600002 ? 'Pagada' : 'Proceso' );
        return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRequisitionStatus = (status) => {
        const backColor = status === 2000700003 ? 'bg-danger' : ( status === 2000700002 ? 'bg-success' : 'bg-warning' );
        const statusDesc = status === 2000700003 ? 'Cancelada' : ( status === 2000700002 ? 'Pagada' : 'Proceso' );
        return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
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
    }) => (
        <tr key={ id } onClick={ () => handledSelect(projectId, id) }>
            {/* <td className="text-center">
                <button type="button" className="btn btn-primary">
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td> */}
            <th className="text-center" scope="row">{ orderNum }</th>
            <td className="text-center">{ orderDate }</td>
            <td className="text-center">{ renderStatus(status, '') }</td>
            <td className="text-center">{ requisition }</td>
            <td className="text-center">{ requisitionDate }</td>
            <td className="text-center">{ renderRequisitionStatus(requisitionStatus, '') }</td>
            <td className="text-end text-primary">{ amount }</td>
            { permissions.isAdminRoot && (<td className="text-end text-primary">{ tax }</td>) }
            { permissions.isAdminRoot && (<td className="text-end text-primary">{ total }</td>) }
            <td className="text-end text-primary">{ amountPaid }</td>
            {
                permissions.canDelOrd && (
                    <td className="text-center">
                        <button type="button" className="btn btn-danger btn-sm">
                            <span><i className="bi bi-trash"></i></span>
                        </button>
                    </td>
                )
            }
        </tr>
    ));

    return (
        <div className='table-responsive text-nowrap' style={{ height: '350px' }}>
            <table className="table table-sm table-bordered table-striped table-hover">
                <thead className="thead-dark">
                    <tr>
                        {/* <th className="text-center fs-6" scope="col">Editar</th> */}
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
                        { permissions.canDelOrd && (<th className="text-center fs-6" scope="col">Borrar</th>) }
                    </tr>
                </thead>
                <tbody>
                    { renderRows() }
                </tbody>
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
                        { permissions.canDelOrd && (<th></th>) }
                    </tr>
                </tfoot>
            </table>
        </div>
    )
}
