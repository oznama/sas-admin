import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { handleDateStr, numberToString, mountMax, taxRate, genericErrorMsg, displayNotification, getPaymentDate, formatter, isNumDec, removeCurrencyFormat } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../../custom/alerts/types/types';
import { TableLog } from '../../custom/TableLog';
import { getInvoiceById, getInvoicePaid, save, update } from '../../../services/InvoiceService';
import { getCatalogChilds } from '../../../services/CatalogService';
import { TextArea } from '../../custom/TextArea';
import { setModalChild } from '../../../../store/modal/modalSlice';
import { setOrder, setPaid, setProject } from '../../../../store/project/projectSlice';
import { getProjectById } from '../../../services/ProjectService';
import { getOrderById, getOrderSelect } from '../../../services/OrderService';
import { SelectSearcher } from '../../custom/SelectSearcher';

export const DetailInvoice = () => {

  const dispatch = useDispatch();
  const { project, order, paid } = useSelector( state => state.projectReducer );
  const { permissions } = useSelector( state => state.auth );
  const { projectId, orderId, id } = useParams();

  const navigate = useNavigate();
  const [holyDates, setHolyDates] = useState([]);
  const [invoiceNum, setInvoiceNum] = useState('');
  const [issuedDate, setIssuedDate] = useState(new Date());
  const [paymentDate, setPaymentDate] = useState( getPaymentDate(new Date(), holyDates) );
  const [percentage, setPercentage] = useState(0);
  const [currentTab, setCurrentTab] = useState(1);
  const [amount, setAmount] = useState('');
  const [amountInDb, setAmountInDb] = useState();
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [observations, setObservations] = useState('');
  const [status, setStatus] = useState('2000800001');
  const [catStatus, setCatStatus] = useState([]);
  const [pId, setPId] = useState(projectId === '0' ? '' : projectId);
  const [oId, setOId] = useState(orderId === '0' ? '' : orderId);
  const [orders, setOrders] = useState([]);
  const [pFilter, setPFilter] = useState('');

  const fetchSelects = () => {
    getCatalogChilds(1000000008).then( response => {
      setCatStatus(response.filter( cat => cat.status === 2000100001 ));
    }).catch( error => {
      console.log(error);
    });

    getCatalogChilds(1000000007).then( response => {
      const catHolyDates = response.filter( cat => cat.status === 2000100001 );
      const arrayHolyDates = [];
      catHolyDates.forEach( ({ value }) => arrayHolyDates.push(new Date(value)) );
      setHolyDates(arrayHolyDates);
    }).catch( error => {
      console.log(error);
    });
  };

  const fetchOrders = () => {
    getOrderSelect().then( response => {
      setOrders(response);
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const fetchProject = (projectId, orderId) => {
    getProjectById(projectId).then( response => {
      dispatch(setProject(response));
      fetchOrder(orderId);
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const fetchOrder = id => {
    getOrderById(id).then( response => {
      if( response.code ) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        dispatch( setOrder(response) );
        fetchPaid(id);
      }
    }).catch( error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const fetchPaid = orderId => {
    getInvoicePaid(orderId).then( response => {
      dispatch(setPaid(response));
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const fetchInvoice = () => {
    getInvoiceById(id).then( response => {
      if( response.code ) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        setAmount(formatter.format(response.amount));
        setAmountInDb(response.amount);
        setTax(formatter.format(response.tax));
        setTotal(formatter.format(response.total));
        setInvoiceNum(response.invoiceNum ? response.invoiceNum : '');
        setIssuedDate(handleDateStr(response.issuedDate));
        setPaymentDate(handleDateStr(response.paymentDate));
        setPercentage(response.percentage ? response.percentage : 0);
        setStatus(numberToString(response.status, ''));
        setObservations(response.observations);
      }
    }).catch( error => {
        console.log(error)
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  useEffect(() => {
    fetchSelects();
    if( orderId === '0' ) {
      fetchOrders();
    }
    if( !(order && order.id) && orderId !== '0' ) {
      fetchProject(pId, oId);
    }
    if( id ) {
      fetchInvoice();
    }
  }, []);

  const calculateAmounts = amount => {
    const allowAmount = amount >= 0 && amount <= mountMax;
    if( allowAmount ) {
      const tax = amount * taxRate;
      const total = amount + tax;
      setAmount(amount);
      setTax(tax.toFixed(2));
      setTotal(total.toFixed(2));

      const porc = Math.round((amount * 100)/order.amount);
      setPercentage(porc);
    }
  }

  const onChangeAmount = ({ target }) => {
    if( target.value === '' ) {
      setAmount('');
      setTax('');
      setTotal('');
    } else if( isNumDec(target.value) ) {
      const amount = Number(target.value);
      calculateAmounts(amount);
    }
  }
  const onFocusAmount = () => {
    if( amount && amount !== '' ) {
      setAmount(removeCurrencyFormat(amount));
      setTax(removeCurrencyFormat(tax));
      setTotal(removeCurrencyFormat(total));
    }
  }
  const onBlurAmount = () => {
    if( amount && amount !== '' ) {
      setAmount(formatter.format(amount));
      setTax(formatter.format(tax));
      setTotal(formatter.format(total));
    }
  }
  const onChangeStatus = ({target }) => {
    if( status !== '2000800003' && target.value === '2000800003' ) {
      const newAmount = paid.amount - amount;
      dispatch( setPaid( { ...paid, amount: newAmount } ) );
    }
    setStatus(target.value)
  };
  const onChangeNumOrder = ({ target }) => setInvoiceNum(target.value);
  const onChangeIssuedDate = date => {
    setIssuedDate(date)
    setPaymentDate(getPaymentDate(new Date(date), holyDates));
  }
  const onChangePaymentDate = date => setPaymentDate(date);
  const onChangePercentage = ({target}) => {
    setPercentage(target.value)
    const porcDec = Number(target.value) / 100;
    calculateAmounts(order.amount * porcDec);
  };
  const onChangeObservations = ({ target }) => setObservations(target.value);
  const onChangeOId = ({ target }) => {
    setPFilter(target.value);
    const order = orders.find( o => o.value === target.value );
    if( order ) {
      setOId(order.id);
      const parentId = orders.find( o => o.id === order.id ).parentId;
      setPId(parentId);
      fetchProject(parentId, order.id);
    } else {
      setPId('');
      dispatch(setProject({}));
      dispatch(setOrder({}));
      dispatch(setPaid({}));
    }
  }

  const onClean = () => {
    setPFilter('');
    setPId('');
    dispatch(setProject({}));
    dispatch(setPaid({}));
  }

  const onSubmit = event => {
    event.preventDefault();
    const isAmountPending = ( order.amount - paid.amount ) > 0 && removeCurrencyFormat(amount) > ( order.amount - paid.amount );
    const isAmoutPaid = ( order.amount - paid.amount ) === 0 && removeCurrencyFormat(amount) > amountInDb;
    if( isAmountPending || isAmoutPaid ) {
      dispatch(setModalChild(renderModal()));
    } else {
      const data = new FormData(event.target);
      const request = Object.fromEntries(data.entries());
      request.amount = removeCurrencyFormat(amount);
      request.tax = removeCurrencyFormat(tax);
      request.total = removeCurrencyFormat(total);
      persistInvoice(request);   
    }

  }

  const saveInvoice = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, 'Factura agregada correctamente!', alertType.success);
        if( orderId === '0' ) {
          navigate('/invoices');
        } else {
          navigate(`/project/${pId}/order/${oId}/edit`, { replace: true });
        }
      }
    }).catch(error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const updateInvoice = request => {
    update(id, request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, 'Factura actualizada correctamente!', alertType.success);
        if( request.requisition ) {
          setHasRequisition(true);
        }
        navigate(`/project/${pId}/order/${oId}/edit`, { replace: true });
      }
    }).catch(error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const persistInvoice = request => {
    if ( id && permissions.canEditOrd ) {
      updateInvoice(request);
      console.log('update', request);
    } else if ( permissions.canCreateOrd ) {
      request.projectId = pId;
      request.orderId = oId;
      saveInvoice(request);
    }
    dispatch( setModalChild(null) );
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary">Guardar</button>);
    return ( ( id && permissions.canEditOrd ) || permissions.canCreateOrd ) ? saveButton : null;
  };

  const onClickBack = () => {
    if ( currentTab === 1 ) {
      navigate(`/project/${ pId }/order/${ oId }/edit`)
    } else {
      setCurrentTab(currentTab - 1);
    }
  }

  const renderTabs = () => (
    <ul className="nav nav-tabs">
      <li>
        <button type="button" className="btn btn-link" onClick={ () => onClickBack() }>&lt;&lt; Regresar</button>
      </li>
      <li className="nav-item" onClick={ () => setCurrentTab(1) }>
        <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
      </li>
      <li className="nav-item" onClick={ () => setCurrentTab(2) }>
        <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
      </li>
    </ul>
  )

  const renderModal = () => (
    <div className='p-5 bg-white rounded-3'>
      <div className='text-start'>
          <h3>¡El monto de la factura no puede ser mayor al monto pendiente!</h3>
          <ul style={ { marginBottom: '0' } }>
            <li key={ 1 }><p className="h3">Monto pendiente: <span className='text-primary'>{ formatter.format(order.amount - paid.amount) }</span></p></li>
            <li key={ 2 }><p className="h3">Monto capturado: <span className='text-primary'>{ amount }</span></p></li>
          </ul>
      </div>
      <div className="pt-3 d-flex justify-content-center">
          <button type="button" className="btn btn-danger" onClick={ () => dispatch( setModalChild(null) ) }>Cerrar</button>
      </div>
    </div>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
      { order.id && (<p className="h5">Valor de la orden: <span className='text-primary'>{ formatter.format(order.amount) }</span> Iva: <span className='text-primary'>{ formatter.format(order.tax) }</span> Total: <span className='text-primary'>{ formatter.format(order.total) }</span></p>) }
      { order.id && (<p className="h5">Monto pendiente: <span className='text-danger'>{ formatter.format(order.amount - paid.amount) }</span></p>) }
      <form onSubmit={ onSubmit }>
          <div className='text-center'>
              { orderId === '0' && (
                <div className="row text-start">
                  <div className='col-12'>
                    <SelectSearcher label="Orden" options={ orders } disabled={ !permissions.canEditOrd } value={ pFilter } required 
                      onChange={ onChangeOId } onClean={ onClean } />
                  </div>
                </div>
              )}
              <div className="row text-start">
                  <div className='col-4'>
                      <InputText name='invoiceNum' label='No. de factura' placeholder='Ingresa no. de factura' disabled = { !permissions.canEditOrd } value={ invoiceNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
                  </div>
                  <div className='col-4'>
                      <InputText name="percentage" label='Porcentaje' type='number' placeholder='Ingresa porcentaje' 
                        disabled = { !permissions.canEditOrd } value={ `${percentage}` } required 
                        onChange={ onChangePercentage } onBlur={ onBlurAmount } />
                  </div>
                  <div className='col-4'>
                      <Select name = "status" label="Status" options={ catStatus } disabled={ !permissions.canEditOrd || (Number(status) === 2000800003 && paid.amount && paid.amount >= order.amount) } value={ status } onChange={ onChangeStatus } />
                  </div>
              </div>
              <div className="row text-start">
                  <div className='col-6'>
                      <DatePicker name="issuedDate" label="Fecha De Emisi&oacute;n" disabled = { !permissions.canEditOrd } value={ issuedDate } onChange={ (date) => onChangeIssuedDate(date) } maxDate={ new Date() } />
                  </div>
                  <div className='col-6'>
                      <DatePicker name="paymentDate" label="Fecha De Pago" disabled = { !permissions.canEditOrd } value={ paymentDate } onChange={ (date) => onChangePaymentDate(date) } />
                  </div>
              </div>
              <div className="row text-start">
                  <div className='col-6'>
                      <InputText name="amount" label='Monto' placeholder='Ingresa monto' disabled = { !permissions.canEditOrd } value={ `${amount}` } required 
                        onChange={ onChangeAmount } onFocus={ onFocusAmount } onBlur={ onBlurAmount } />
                  </div>
                  <div className='col-3'>
                      <InputText name="tax" label='Iva' disabled = { !permissions.canEditOrd } readOnly value={ `${tax}` } />
                  </div>
                  <div className='col-3'>
                      <InputText name="total" label='Total' disabled = { !permissions.canEditOrd } readOnly value={ `${total}` } />
                  </div>
              </div>
              <div className="row text-start">
                <div className='col-12'>
                  <TextArea name='observations' label='Observaciones' placeholder='Escribe observaciones' 
                          disabled = { !permissions.canEditOrd } value={ observations } maxLength={ 1500 } onChange={ onChangeObservations } />
                </div>
              </div>
          </div>
          <div className="pt-3 d-flex flex-row-reverse">
              { renderSaveButton() }
              &nbsp;
              <button type="button" className="btn btn-danger" onClick={ () => navigate(  orderId !== '0' ? `/project/${ projectId }/order/${ orderId }/edit` : '/invoices') }>Cancelar</button>
          </div>
      </form>
    </div>
  )

  const titleWithOrder = `${ oId !== '' ? ': ' + order.orderNum : '' }${order.requisition ? ' > Requisición: ' + order.requisition : ''}`;
  const title = pId !== '' ? `${project.key} ${project.description} > Orden${ titleWithOrder }` : 'Factura nueva';
 
  return (
    <div className='px-5'>
      <div className='d-flex justify-content-between'>
      <h4 className="card-title fw-bold mb-4">{ title }</h4>
      { id && renderTabs() }
      </div>
      { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Invoice' recordId={ id } />) }
    </div>
  )
}