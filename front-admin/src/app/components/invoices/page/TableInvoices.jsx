import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { alertType } from "../../custom/alerts/types/types";
import { deleteLogic, getInvoices, getInvoicesByOrderId, pay } from "../../../services/InvoiceService";
import { setPaid } from "../../../../store/project/projectSlice";
import { displayNotification, genericErrorMsg, styleTable, styleTableRow, styleTableRowBtn } from "../../../helpers/utils";

export const TableInvoices = ({
    projectId,
    orderId
}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );

    const [invoices, setInvoices] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);
    const [totapP, setTotapP] = useState(0);
    const [totalStatus, setTotalStatus] = useState();

    const fetchInvoices = () => {
        getInvoices(null,null,null,null).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                const paid = response.content.find( r => r.invoiceNum === 'paid' );
                dispatch(setPaid(paid));
                setInvoices(response.content.filter( r => r.invoiceNum !== 'total' && r.invoiceNum !== 'paid' ));
                const { amount, tax, total, percentage, status } = response.content.find( r => r.invoiceNum === 'total' );
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
                setTotapP( percentage );
                // setTotalStatus( status )
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const fetchInvoicesByOrder = () => {
        getInvoicesByOrderId(orderId).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {                
                const paid = response.find( r => r.invoiceNum === 'paid' );
                dispatch(setPaid(paid));
                setInvoices(response.filter( r => r.invoiceNum !== 'total' && r.invoiceNum !== 'paid' ));
                const { amountStr, taxStr, totalStr, percentage, status } = response.find( r => r.invoiceNum === 'total' );
                setTotalAmount( amountStr );
                setTotalTax( taxStr ) ;
                setTotalT( totalStr );
                setTotapP( percentage );
                // setTotalStatus( status )
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const loadInvoices = () => {
        if( projectId ) {
            fetchInvoicesByOrder();
        } else {
            fetchInvoices();
        }
    }

    useEffect(() => {
        loadInvoices();
    }, []);

    const handledSelect = (projectId, orderId, id) => {
        if ( permissions.canAdminOrd ) {
            const urlRedirect = `/project/${ projectId }/order/${ orderId }/invoice/${ id }/edit`;
            navigate(urlRedirect);
        }
    }

    const renderStatus = (status) => {
        const backColor = status === 2000800003 ? 'bg-danger' : ( status === 2000800002 ? 'bg-success' : ( status === 2000800001 ? 'bg-warning' : '') );
        const statusDesc = status === 2000800003 ? 'Cancelada' : ( status === 2000800002 ? 'Pagada' : ( status === 2000800001 ? 'Proceso' : '') );
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const deleteInvoice = (id, active) => {
        deleteLogic(id).then( response => {
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, `Factura ${ active ? 'eliminada' : 'reactivada' } correctamente!`, active ? alertType.warning : alertType.success);
                loadInvoices();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const doPay = id => {
        pay(id).then( response => {
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, 'Factura pagada correctamente', alertType.success);
                loadInvoices();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => invoices && invoices.map(({
        id,
        invoiceNum,
        issuedDate,
        paymentDate,
        percentage,
        amountStr,
        taxStr,
        totalStr,
        status,
        orderId,
        projectId,
        active
    }) => (
        <tr key={ id }>
            <th className="text-center" style={ styleTableRow } scope="row">{ invoiceNum }</th>
            <td className="text-center" style={ styleTableRow }>{ issuedDate }</td>
            <td className="text-center" style={ styleTableRow }>{ paymentDate }</td>
            <td className="text-center" style={ styleTableRow }>{ percentage }</td>
            <td className="text-center" style={ styleTableRow }>{ renderStatus(status) }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ amountStr }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ taxStr }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ totalStr }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" className={`btn btn-${ active && permissions.canEditOrd && status !== 2000800002 ? 'success' : 'primary' } btn-sm`} style={ styleTableRowBtn } onClick={ () => handledSelect(projectId, orderId, id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditOrd && status !== 2000800002 ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" disabled={ status === 2000800002 || !permissions.canEditOrd } className={`btn btn-${ status === 2000800002 ? 'success' : 'primary' } btn-sm`} style={ styleTableRowBtn } onClick={ () => doPay(id) }>
                    <span><i className={`bi bi-cash${ status === 2000800002 ? '-coin' : ''}`}></i></span>
                </button>
            </td>
            {
                permissions.canDelOrd && (
                    <td className="text-center" style={ styleTableRow }>
                        <button type="button" disabled={ status === 2000800002 } className="btn btn-danger btn-sm" style={ styleTableRowBtn } onClick={ () => deleteInvoice(id, active) }>
                            <span><i className="bi bi-trash"></i></span>
                        </button>
                    </td>
                )
            }
        </tr>
    ));

    return (
        <div className='table-responsive text-nowrap'>
            <table className="table table-sm table-bordered table-striped table-hover">
                <thead className="thead-dark">
                    <tr>
                        <th className="text-center fs-6" scope="col">No. factura</th>
                        <th className="text-center fs-6" scope="col">Fecha Emisi&oacute;n</th>
                        <th className="text-center fs-6" scope="col">Fecha Pago</th>
                        <th className="text-center fs-6" scope="col">Porcentaje</th>
                        <th className="text-center fs-6" scope="col">Status</th>
                        <th className="text-center fs-6" scope="col">Monto</th>
                        <th className="text-center fs-6" scope="col">Iva</th>
                        <th className="text-center fs-6" scope="col">Total</th>
                        <th className="text-center fs-6" scope="col">{permissions.canEditOrd ? 'Editar' : 'Ver'}</th>
                        <th className="text-center fs-6" scope="col">Pagar</th>
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
                        <th className="text-center fs-6" scope="col">{ totapP }</th>
                        <td className="text-center">{ renderStatus(totalStatus) }</td>
                        <th className="text-end fs-6" scope="col">{ totalAmount }</th>
                        <th className="text-end fs-6" scope="col">{ totalTax }</th>
                        <th className="text-end fs-6" scope="col">{ totalT }</th>
                        <th></th>
                        <th></th>
                        { permissions.canDelOrd && (<th></th>) }
                    </tr>
                </tfoot>
            </table>
        </div>
    )
}
