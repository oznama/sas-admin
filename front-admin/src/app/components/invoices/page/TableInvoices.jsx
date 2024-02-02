import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { alertType } from "../../custom/alerts/types/types";
import { deleteLogic, getInvoices, getInvoicesByOrderId } from "../../../services/InvoiceService";
import { setCurrentOrdTab, setCurrentTab, setOrder, setPaid, setProject } from "../../../../store/project/projectSlice";
import { displayNotification, genericErrorMsg, styleTableRow, styleTableRowBtn } from "../../../helpers/utils";
import { Pagination } from "../../custom/pagination/page/Pagination";
import { InputSearcher } from "../../custom/InputSearcher";

const pageSize = 10;
const sort = 'invoiceNum,desc';

export const TableInvoices = ({
    projectId,
    orderId
}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );
    const { project, order, paid } = useSelector( state => state.projectReducer );

    const [invoices, setInvoices] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);
    const [totapP, setTotapP] = useState(0);
    const [totalAmountStr, setTotalAmountStr] = useState(0);
    const [totalTaxStr, setTotalTaxStr] = useState(0);
    const [totalTStr, setTotalTStr] = useState(0);
    const [totalStatus, setTotalStatus] = useState();

    const [currentPage, setCurrentPage] = useState(0);
    const [totalInvoices, setTotalInvoices] = useState(0);
    const [filter, setFilter] = useState('');

    const onChangeFilter = ({ target }) => {
        setCurrentPage(0)
        setFilter(target.value);
        fetchInvoices(0, target.value);
    };

    const fetchInvoices = (page, filter) => {
        getInvoices(page, pageSize, sort, filter).then( response => {
            if( (response.status && response.status !== 200 ) || (response.code && response.code !== 200)  ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setInvoices(response.content);
                setTotalInvoices(response.totalElements);
                const { amount, tax, total, amountStr, taxStr, totalStr, percentage, status } = response.content;
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
                setTotalAmountStr( amountStr );
                setTotalTaxStr( taxStr ) ;
                setTotalTStr( totalStr );
                setTotapP( percentage );
                setTotalStatus( status );
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
                setInvoices(response.filter( r => r.invoiceNum !== 'total' ));
                const invoiceTotal = response.find( r => r.invoiceNum === 'total' );
                dispatch(setPaid(invoiceTotal));
                const { amount, tax, total, amountStr, taxStr, totalStr, percentage, status } = invoiceTotal;
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
                setTotalAmountStr( amountStr );
                setTotalTaxStr( taxStr ) ;
                setTotalTStr( totalStr );
                setTotapP( percentage );
                setTotalStatus( status );
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
            fetchInvoices(currentPage, filter);
        }
    }

    useEffect(() => {
        loadInvoices();
        if( !projectId || !orderId ) {
            dispatch(setProject({}));
            dispatch(setOrder({}));
            dispatch(setPaid({}));
        }
    }, []);

    const handledSelect = (projectIdParam, orderIdParam, id) => {
        if( !projectId || !orderIdParam ) {
            dispatch(setCurrentTab(3));
            dispatch(setCurrentOrdTab(2));
        }
        if ( permissions.canAdminOrd ) {
            const urlRedirect = `/project/${ projectIdParam }/order/${ orderIdParam }/invoice/${ id }/edit`;
            navigate(urlRedirect);
        }
    }

    const handleAddInvoice = () => {
        navigate(`/project/${ projectId ? projectId : 0 }/order/${ orderId ? orderId : 0 }/invoice/add`);
    }

    const renderAddInvoiceButton = () => permissions.canCreateOrd && (( !projectId || !orderId) || ((paid.amount < order.amount) && (order.status < 2000600003))) && (
        <div className="d-flex flex-row-reverse p-2">
          <button type="button" className="btn btn-primary" onClick={ handleAddInvoice }>
              <span className="bi bi-plus"></span>
          </button>
        </div>
      );

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchInvoices(page, filter);
    }

    const renderSearcher = () => !orderId && (
        <div className="input-group w-50 py-1">
            <input name="filter" type="text" className="form-control input-padding-sm" placeholder="Escribe para filtrar..."
                maxLength={ 100 } autoComplete='off'
                value={ filter } required onChange={ onChangeFilter } />
            <span className="input-group-text" id="basic-addon2" onClick={ () => onClean() }>
                <i className="bi bi-x-lg"></i>
            </span>
        </div>   
    );

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        fetchInvoices(0, '');
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
                displayNotification(dispatch, `Factura ${ active ? 'cancelada' : 'reactivada' } correctamente!`, active ? alertType.warning : alertType.success);
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
        orderId: odrId,
        projectId,
        active
    }) => (
        <tr key={ id }>
            <th className="text-center" style={ styleTableRow } scope="row">{ invoiceNum }</th>
            <td className="text-center" style={ styleTableRow }>{ issuedDate }</td>
            <td className="text-center" style={ styleTableRow }>{ paymentDate }</td>
            { orderId && (<td className="text-center" style={ styleTableRow }>{ percentage }</td>) }
            <td className="text-center" style={ styleTableRow }>{ renderStatus(status) }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ amountStr }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ taxStr }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ totalStr }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" 
                    className={`btn btn-${ active && permissions.canEditOrd ? 'success' : 'primary' } btn-sm`} 
                    style={ styleTableRowBtn } 
                    onClick={ () => handledSelect(projectId, odrId, id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditOrd ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            {
                permissions.canDelOrd && (
                    <td className="text-center" style={ styleTableRow }>
                        <button type="button"
                            className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`}
                            style={ styleTableRowBtn }
                            disabled={ status === 2000800002 || ( status === 2000800003 && paid.amount >= order.amount ) }
                            onClick={ () => deleteInvoice(id, active) }>
                            <span><i className={`bi bi-${ active ? 'trash' : 'folder-symlink'}`}></i></span>
                        </button>
                    </td>
                )
            }
        </tr>
    ));

    const tableByOrder = () => (
        <div>
            <div className={`d-flex ${ projectId ? 'flex-row-reverse' : 'justify-content-between align-items-center' }`}>
                { !orderId && <InputSearcher name={ 'filter' } placeholder={ 'Escribe para filtrar...' } value={ filter } onChange={ onChangeFilter } onClean={ onClean } /> }
                { renderAddInvoiceButton() }
            </div>
            <div className='table-responsive text-nowrap'>
                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">No. factura</th>
                            <th className="text-center fs-6" scope="col">Fecha Emisi&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Fecha Pago</th>
                            { orderId && (<th className="text-center fs-6" scope="col">Porcentaje</th>) }
                            <th className="text-center fs-6" scope="col">Status</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                            <th className="text-center fs-6" scope="col">Iva</th>
                            <th className="text-center fs-6" scope="col">Total</th>
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
                                <th className="text-center fs-6" scope="col">TOTAL</th>
                                <th></th>
                                <th></th>
                                { orderId && (<th className="text-center fs-6" scope="col">{ totapP }</th>) }
                                <td className="text-center fs-6" scope="col">{ renderStatus(totalStatus) }</td>
                                <th className="text-end fs-6" scope="col">{ totalAmountStr }</th>
                                <th className="text-end fs-6" scope="col">{ totalTaxStr }</th>
                                <th className="text-end fs-6" scope="col">{ totalTStr }</th>
                                <th></th>
                                { permissions.canDelOrd && (<th></th>) }
                            </tr>
                        </tfoot>
                    )}
                </table>
            </div>
        </div>
    )

    const tablePaged = () => (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Facturas</h4>
            { tableByOrder() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalInvoices }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
        </div>
    )

    return orderId ? tableByOrder() : tablePaged();
}
