import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { setMessage } from "../../../../store/alert/alertSlice";
import { buildPayloadMessage } from "../../../helpers/utils";
import { alertType } from "../../custom/alerts/types/types";
import { getInvoicesByOrderId } from "../../../services/InvoiceService";

export const TableInvoices = ({
    projectId,
    projectApplicationId,
    orderId
}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );

    const [invoices, setInvoices] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);

    const fetchInvoices = () => {
        getInvoicesByOrderId(orderId).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else {
                setInvoices(response.filter( r => r.invoiceNum !== 'total' ));
                const { amount, tax, total } = response.find( r => r.invoiceNum === 'total' );
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
            }
        }).catch( error => {
            console.log(error);
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar las facturas, contacte al administrador', alertType.error)));
        });
    }

    useEffect(() => {
        fetchInvoices();
    }, []);

    const handledSelect = id => {
        const urlRedirect = `/project/${ projectId }/application/${ projectApplicationId }/order/${ orderId }/invoice/${ id }/edit`;
        navigate(urlRedirect);
    }

    // const renderStatus = (status) => {
    //     const backColor = status === 3 ? 'bg-danger' : ( status === 1 || status === 4 ? 'bg-success' : 'bg-warning' );
    //     // TODO Remove by value
    //     const statusDesc = status === 3 ? 'Desfasado' : ( status === 2 ? 'Retrazado' : 'En tiempo' );
    //     return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    // }

    const renderRows = () => invoices && invoices.map(({
        id,
        invoiceNum,
        issuedDate,
        paymentDate,
        percentage,
        amount,
        tax,
        total
        // status,
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            {/* <td className="text-center">
                <button type="button" className="btn btn-primary">
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td> */}
            <th className="text-start" scope="row">{ invoiceNum }</th>
            <td className="text-center">{ issuedDate }</td>
            <td className="text-center">{ paymentDate }</td>
            <td className="text-center">{ percentage }</td>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            <td className="text-end text-primary">{ amount }</td>
            <td className="text-end text-primary">{ tax }</td>
            <td className="text-end text-primary">{ total }</td>
            {
                permissions.canDelInvoices && (
                    <td className="text-center">
                        <button type="button" className="btn btn-danger">
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
                        {/* <th className="text-center fs-6" scope="col">Editar</th> */}
                        <th className="text-center fs-6" scope="col">No. factura</th>
                        <th className="text-center fs-6" scope="col">Fecha Emisi&oacute;n</th>
                        <th className="text-center fs-6" scope="col">Fecha Pago</th>
                        <th className="text-center fs-6" scope="col">Porcentaje</th>
                        {/* <th className="text-center fs-6" scope="col">Status</th> */}
                        <th className="text-center fs-6" scope="col">Monto</th>
                        <th className="text-center fs-6" scope="col">Iva</th>
                        <th className="text-center fs-6" scope="col">Total</th>
                        { permissions.canDelInvoices && (<th className="text-center fs-6" scope="col">Borrar</th>) }
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
                        <th className="text-end fs-6" scope="col">{ totalAmount }</th>
                        <th className="text-end fs-6" scope="col">{ totalTax }</th>
                        <th className="text-end fs-6" scope="col">{ totalT }</th>
                        { permissions.canDelInvoices && (<th></th>) }
                    </tr>
                </tfoot>
            </table>
        </div>
    )
}
