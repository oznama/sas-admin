import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getOrdersByProjectApplicationId } from "../../../services/OrderService";
import { setMessage } from "../../../../store/alert/alertSlice";
import { buildPayloadMessage } from "../../../helpers/utils";
import { alertType } from "../../custom/alerts/types/types";

export const TableOrders = ({
    projectId,
    projectApplicationId
}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );

    const [orders, setOrders] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);

    const fetchOrders = () => {
        getOrdersByProjectApplicationId(projectApplicationId).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else {
                setOrders(response.filter( r => r.orderNum !== 'total' ));
                const { amount, tax, total } = response.find( r => r.orderNum === 'total' );
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
            }
        }).catch( error => {
            console.log(error);
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar las ordenes, contacte al administrador', alertType.error)));
        });
    }

    useEffect(() => {
        fetchOrders();
    }, []);

    const handledSelect = id => {
        const urlRedirect = `/project/${ projectId }/application/${ projectApplicationId }/order/${ id }/edit`;
        navigate(urlRedirect);
    }

    // const renderStatus = (status) => {
    //     const backColor = status === 3 ? 'bg-danger' : ( status === 1 || status === 4 ? 'bg-success' : 'bg-warning' );
    //     // TODO Remove by value
    //     const statusDesc = status === 3 ? 'Desfasado' : ( status === 2 ? 'Retrazado' : 'En tiempo' );
    //     return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    // }

    const renderRows = () => orders && orders.map(({
        id,
        orderNum,
        orderDate,
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
            <th className="text-center" scope="row">{ orderNum }</th>
            <td className="text-center">{ orderDate }</td>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            <td className="text-end text-primary">{ amount }</td>
            <td className="text-end text-primary">{ tax }</td>
            <td className="text-end text-primary">{ total }</td>
            {
                permissions.canDelOrders && (
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
                        <th className="text-center fs-6" scope="col">No. orden</th>
                        <th className="text-center fs-6" scope="col">Fecha No. De Orden</th>
                        {/* <th className="text-center fs-6" scope="col">Status</th> */}
                        <th className="text-center fs-6" scope="col">Monto</th>
                        <th className="text-center fs-6" scope="col">Iva</th>
                        <th className="text-center fs-6" scope="col">Total</th>
                        { permissions.canDelOrders && (<th className="text-center fs-6" scope="col">Borrar</th>) }
                    </tr>
                </thead>
                <tbody>
                    { renderRows() }
                </tbody>
                <tfoot className="thead-dark">
                    <tr>
                        <th className="text-center fs-6" scope="col">TOTALES</th>
                        <th></th>
                        <th className="text-end fs-6" scope="col">{ totalAmount }</th>
                        <th className="text-end fs-6" scope="col">{ totalTax }</th>
                        <th className="text-end fs-6" scope="col">{ totalT }</th>
                        { permissions.canDelOrders && (<th></th>) }
                    </tr>
                </tfoot>
            </table>
        </div>
    )
}
