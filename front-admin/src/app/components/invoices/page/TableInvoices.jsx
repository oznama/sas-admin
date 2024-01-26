import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { alertType } from "../../custom/alerts/types/types";
import { getInvoices, getInvoicesByOrderId } from "../../../services/InvoiceService";
import { setPaid } from "../../../../store/project/projectSlice";
import { displayNotification, genericErrorMsg, styleTable } from "../../../helpers/utils";

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
                const { amount, tax, total, percentage, status } = response.find( r => r.invoiceNum === 'total' );
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

    useEffect(() => {
        if( projectId ) {
            fetchInvoicesByOrder();
        } else {
            fetchInvoices();
        }
    }, []);

    const handledSelect = (projectId, orderId, id) => {
        if ( permissions.canCreateOrd ) {
            const urlRedirect = `/project/${ projectId }/order/${ orderId }/invoice/${ id }/edit`;
            navigate(urlRedirect);
        }
    }

    const renderStatus = (status) => {
        const backColor = status === 2000800003 ? 'bg-danger' : ( status === 2000800002 ? 'bg-success' : ( status === 2000800001 ? 'bg-warning' : '') );
        const statusDesc = status === 2000800003 ? 'Cancelada' : ( status === 2000800002 ? 'Pagada' : ( status === 2000800001 ? 'Proceso' : '') );
        return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
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
        projectId
    }) => (
        <tr key={ id } onClick={ () => handledSelect(projectId, orderId, id) }>
            {/* <td className="text-center">
                <button type="button" className="btn btn-primary">
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td> */}
            <th className="text-center" scope="row">{ invoiceNum }</th>
            <td className="text-center">{ issuedDate }</td>
            <td className="text-center">{ paymentDate }</td>
            <td className="text-center">{ percentage }</td>
            <td className="text-center">{ renderStatus(status) }</td>
            <td className="text-end text-primary">{ amountStr }</td>
            <td className="text-end text-primary">{ taxStr }</td>
            <td className="text-end text-primary">{ totalStr }</td>
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
        <div className='table-responsive text-nowrap' style={ styleTable }>
            <table className="table table-sm table-bordered table-striped table-hover">
                <thead className="thead-dark">
                    <tr>
                        {/* <th className="text-center fs-6" scope="col">Editar</th> */}
                        <th className="text-center fs-6" scope="col">No. factura</th>
                        <th className="text-center fs-6" scope="col">Fecha Emisi&oacute;n</th>
                        <th className="text-center fs-6" scope="col">Fecha Pago</th>
                        <th className="text-center fs-6" scope="col">Porcentaje</th>
                        <th className="text-center fs-6" scope="col">Status</th>
                        <th className="text-center fs-6" scope="col">Monto</th>
                        <th className="text-center fs-6" scope="col">Iva</th>
                        <th className="text-center fs-6" scope="col">Total</th>
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
                        { permissions.canDelOrd && (<th></th>) }
                    </tr>
                </tfoot>
            </table>
            {
                !permissions.canCreateOrd && (
                    <div>
                        <button type="button" className="btn btn-link" onClick={ () => navigate(`/project/${ projectId }/edit`) }>&lt;&lt; Regresar</button>
                    </div>
                 )
            }
        </div>
    )
}
