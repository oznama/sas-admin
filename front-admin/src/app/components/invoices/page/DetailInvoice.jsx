import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { handleDateStr, numberToString, buildPayloadMessage, mountMax, taxRate } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { setMessage } from '../../../../store/alert/alertSlice';
import { alertType } from '../../custom/alerts/types/types';
import { Alert } from '../../custom/alerts/page/Alert';
import { TableLog } from '../../custom/TableLog';
import { TableOrders } from '../../orders/page/TableOrders';
import { getOrderById } from '../../../services/OrderService';
import { getInvoiceById, save, update } from '../../../services/InvoiceService';

export const DetailInvoice = () => {

  const dispatch = useDispatch();
  const { permissions } = useSelector( state => state.auth );
  const { projectId, projectApplicationId, orderId, id } = useParams();

  const navigate = useNavigate();
  const [invoiceNum, setInvoiceNum] = useState('');
  const [issuedDate, setIssuedDate] = useState();
  const [paymentDate, setPaymentDate] = useState();
  const [percentage, setPercentage] = useState(0);
  const [currentTab, setCurrentTab] = useState(1);
  const [amount, setAmount] = useState('');
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [order, setOrder] = useState({});
  // const [status, setStatus] = useState('2000900001');

  const fetchOrder = () => {
    getOrderById(orderId).then( response => {
      if( response.code ) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        setOrder(response);
      }
    }).catch( error => {
        dispatch(setMessage(
            buildPayloadMessage(
                'Ha ocurrido un error al cargar los montos de la orden, contacte al adminitrador', 
                alertType.error
            )
        ));
    });
  }

  const fetchInvoice = () => {
    getInvoiceById(id).then( response => {
      if( response.code ) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        setAmount(numberToString(response.amount, ''));
        setTax(numberToString(response.tax, ''))
        setTotal(numberToString(response.total, ''))
        setInvoiceNum(response.invoiceNum ? response.invoiceNum : '');
        setIssuedDate(handleDateStr(response.issuedDate));
        setPaymentDate(handleDateStr(response.paymentDate));
        setPercentage(response.percentage ? response.percentage : 0);
      }
    }).catch( error => {
        dispatch(setMessage(
            buildPayloadMessage(
                'Ha ocurrido un error al cargar la orden, contacte al adminitrador', 
                alertType.error
            )
        ));
    });
  }

  useEffect(() => {
    fetchOrder();
    if( id ) {
      fetchInvoice();
    }
  }, []);

  const onChangeAmount = ({ target }) => {
    const amount = Number(target.value);
    if( amount <= mountMax ) {
      const tax = amount * taxRate;
      const total = amount + tax;
      setAmount(amount);
      setTax(tax.toFixed(2));
      setTotal(total.toFixed(2));
    }
  }
  const onChangeStatus = ({target }) => setStatus(target.value);
  const onChangeNumOrder = ({ target }) => setInvoiceNum(target.value);
  const onChangeIssuedDate = date => setIssuedDate(date)
  const onChangePaymentDate = date => setPaymentDate(date);
  const onChangePercentage = ({target}) => setPercentage(target.value);

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    // TODO Validacion para monto vs porcentaje y orden de pago
    if ( id && permissions.canEditRequi ) {
      updateInvoice(request);
    } else if ( permissions.canEditRequi ) {
      request.orderId = orderId;
      saveInvoice(request);
    }    
  }

  const saveInvoice = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('Factura agregada correctamente!', alertType.success)));
        navigate(`/project/${projectId}/application/${projectApplicationId}/order/${orderId}/edit`, { replace: true });
      }
    }).catch(error => {
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear la factura, contacte al administrador', alertType.error)));
    });
  }

  const updateInvoice = request => {
    update(id, request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('Factura actualizada correctamente!', alertType.success)));
        if( request.requisition ) {
          setHasRequisition(true);
        }
      }
    }).catch(error => {
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar la factura, contacte al administrador', alertType.error)));
    });
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary">Guardar</button>);
    return permissions.canEditRequi ? saveButton : null;
  };

  const renderTabs = () => (
    <ul className="nav nav-tabs">
      <li className="nav-item" onClick={ () => setCurrentTab(1) }>
        <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
      </li>
      <li className="nav-item" onClick={ () => setCurrentTab(2) }>
        <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
      </li>
    </ul>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
      <form onSubmit={ onSubmit }>
          <div className='text-center'>
            <div className="row text-start">
              <div className='col-6'>
                <InputText name='invoiceNum' label='No. de factura' placeholder='Ingresa no. de factura' 
                    value={ invoiceNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
              </div>
              <div className='col-6'>
              <InputText name="percentage" label='Porcentaje' type='number' placeholder='Ingresa porcentaje' value={ `${percentage}` } required onChange={ onChangePercentage } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-6'>
                <DatePicker name="issuedDate" label="Fecha De Emisi&oacute;n" value={ issuedDate } onChange={ (date) => onChangeIssuedDate(date) } />
              </div>
              <div className='col-6'>
                <DatePicker name="paymentDate" label="Fecha De Pago" value={ paymentDate } onChange={ (date) => onChangePaymentDate(date) } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-6'>
                <InputText name="amount" label='Monto' type='number' placeholder='Ingresa monto' value={ `${amount}` } required onChange={ onChangeAmount } />
              </div>
              <div className='col-3'>
                <InputText name="tax" label='Iva' type='text' readOnly value={ `${tax}` } />
              </div>
              <div className='col-3'>
                <InputText name="total" label='Total' type='text' readOnly value={ `${total}` } />
              </div>
            </div>
            <div className="row text-start">
              {/* <div className='col-4'>
                <Select name = "statusId" label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
              </div> */}
            </div>
          </div>
          <div className="pt-3 d-flex flex-row-reverse">
              { renderSaveButton() }
              &nbsp;
              <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/${ projectId }/application/${ projectApplicationId }/order/${orderId}/edit`) }>Cancelar</button>
          </div>
      </form>
    </div>
  )
 
  return (
    <div className='px-5'>
      <div className='d-flex justify-content-between'>
        <h1 className="fs-4 card-title fw-bold mb-4">Factura</h1>
        { id && renderTabs() }
      </div>
      <Alert />
      {
        currentTab === 1 ? renderDetail() : ( currentTab === 3 
          ? <TableOrders projectApplicationId={ id } /> 
          : <TableLog tableName='Invoice' recordId={ id } />
        )
      }
    </div>
  )
}