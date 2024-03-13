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
import { setOrder, setProject, setOrderPaid } from '../../../../store/project/projectSlice';
import { getProjectById } from '../../../services/ProjectService';
import { getOrderById, getOrderSelect } from '../../../services/OrderService';
import { SelectSearcher } from '../../custom/SelectSearcher';

export const DetailInvoice = () => {

  const dispatch = useDispatch();
  const { project, order, orderPaid } = useSelector( state => state.projectReducer );
  const [totalPaid, setTotalPaid] = useState(orderPaid.amount ? orderPaid.amount : 0);
  const { permissions } = useSelector( state => state.auth );
  const { projectKey, orderId, id } = useParams();

  const navigate = useNavigate();
  const [holyDates, setHolyDates] = useState([]);
  const [invoiceNum, setInvoiceNum] = useState('');
  const [issuedDate, setIssuedDate] = useState(new Date());
  const [paymentDate, setPaymentDate] = useState( getPaymentDate(new Date(), holyDates) );
  const [percentage, setPercentage] = useState(0);
  const [currentTab, setCurrentTab] = useState(1);
  const [amount, setAmount] = useState('');
  const [amountInDb, setAmountInDb] = useState(0);
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [observations, setObservations] = useState('');
  const [status, setStatus] = useState('2000800001');
  const [catStatus, setCatStatus] = useState([]);
  const [pId, setPId] = useState(projectKey === '0' ? '' : projectKey);
  const [oId, setOId] = useState(orderId === '0' ? '' : orderId);
  const [orders, setOrders] = useState([]);
  const [pFilter, setPFilter] = useState('');
  const [amountError, setAmountError] = useState('');

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
      const cat = [];
      response.map( r => cat.push({ ...r, id: r.idStr }) );
      setOrders(cat);
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
      setTotalPaid(response.amount);
      dispatch(setOrderPaid(response));
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
        setAmountInDb(response.amount ? response.amount : 0);
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

  const calculateAmounts = newAmount => {
    setAmountError(Number(newAmount) === 0 ? '¡El monto de la factura es requerido!' : '');
    const allowAmount = newAmount >= 0 && newAmount <= mountMax;
    if( allowAmount ) {
      const nA = Number(newAmount);
      const tax = nA * taxRate;
      const total = nA + tax;
      let amountDiff = Number(nA < amountInDb ? amountInDb - nA : nA - amountInDb).toFixed(2);      
      amountDiff = nA === amountInDb ? totalPaid : (nA < amountInDb ? totalPaid - amountDiff : (totalPaid + Number(amountDiff)));
      setAmount(newAmount);
      setTax(tax.toFixed(2));
      setTotal(total.toFixed(2));
      // dispatch( setOrderPaid( { ...orderPaid, amount: nA === amountInDb ? totalPaid : amountDiff } ) );
      dispatch( setOrderPaid( { ...orderPaid, amount: amountDiff } ) );

      const porc = Math.round((nA * 100)/order.amount);
      setPercentage(porc);
    }
  }

  const onChangeAmount = ({ target }) => {
    if( target.value === '' ) {
      setAmount('');
      setTax('');
      setTotal('');
      dispatch( setOrderPaid( { ...orderPaid, amount: 0 } ) )
    } else if( isNumDec(target.value) ) {
      calculateAmounts(target.value);
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
      const newAmount = orderPaid.amount - removeCurrencyFormat(amount);
      dispatch( setOrderPaid( { ...orderPaid, amount: newAmount } ) );
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
    if( target.value === '' ) {
      dispatch( setOrderPaid( { ...orderPaid, amount: 0 } ) );
    }
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
      dispatch(setOrderPaid({}));
    }
  }

  const onClean = () => {
    setPFilter('');
    setPId('');
    dispatch(setProject({}));
    dispatch(setOrder({}));
    dispatch(setOrderPaid({}));
  }

  const onSubmit = event => {
    event.preventDefault();
    if( removeCurrencyFormat(amount) === 0 ) {
      displayNotification(dispatch, 'El monto de la factura es requerido!', alertType.warning);
    } else {
      const oA = order.amount.toFixed(2);
      const pA = orderPaid.amount.toFixed(2);
      const isAmountDiffZero = (oA - pA) !== 0;
      const isAmountPending = (oA - pA) < 0;
      if( isAmountDiffZero && isAmountPending ) {
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
  }

  const saveInvoice = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, 'Factura agregada correctamente!', alertType.success);
        navigate(`/project/${pId}/order/${oId}/edit`, { replace: true });
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
    } else if ( permissions.canCreateOrd ) {
      request.projectKey = pId;
      request.orderNum = oId;
      saveInvoice(request);
    }
    dispatch( setModalChild(null) );
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary" disabled={ pId === '' || oId === '' }>Guardar</button>);
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
    <div className="d-flex flex-row-reverse">
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
    </div>
  )

  const renderModal = () => (
    <div className='p-5 bg-white rounded-3'>
      <div className='text-start'>
          <h3>¡El monto de la factura no puede ser mayor al monto pendiente!</h3>
          <ul style={ { marginBottom: '0' } }>
            {/* <li key={ 1 }><p className="h3">Monto pagado: <span className='text-primary'>{ formatter.format(paid.amount) }</span></p></li> */}
            <li key={ 1 }>{ renderPendingAmount('h3') }</li>
            <li key={ 2 }><p className="h3">Monto capturado: <span className='text-success'>{ amount }</span></p></li>
          </ul>
      </div>
      <div className="pt-3 d-flex justify-content-center">
          <button type="button" className="btn btn-danger" onClick={ () => dispatch( setModalChild(null) ) }>Cerrar</button>
      </div>
    </div>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
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
                      <Select name = "status" label="Status" options={ catStatus } disabled={ !permissions.canEditOrd || (Number(status) === 2000800003 && orderPaid.amount && orderPaid.amount >= order.amount) } value={ status } onChange={ onChangeStatus } />
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
                        onChange={ onChangeAmount } onFocus={ onFocusAmount } onBlur={ onBlurAmount } error={ amountError } />
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
              <button type="button" className="btn btn-danger" onClick={ () => navigate(  orderId !== '0' ? `/project/${ projectKey }/order/${ orderId }/edit` : '/invoices') }>Cancelar</button>
          </div>
      </form>
    </div>
  )

  const titleWithOrder = `${ oId !== '' ? ': ' + order.orderNum : '' }${order.requisition ? ' > Requisición: ' + order.requisition : ''}`;
  const title = pId !== '' ? `${project.key} ${project.description} > Orden${ titleWithOrder }` : 'Factura nueva';

  const renderPendingAmount = classP => {
    const pendingAmount = order.amount - orderPaid.amount;
    const labelText = pendingAmount >= 0 ? 'Monto pendiente:' : 'Saldo a favor';
    const cssText = pendingAmount > 0 ? 'danger' : 'success';
    return (
      <p className={ classP }>
        { labelText } <span className={ `text-${cssText}` } >{ formatter.format( Math.abs(pendingAmount) ) }</span>
      </p>
    )
  }

  return (
    <div className='px-5'>
      <h4 className="card-title fw-bold">{ title }</h4>
      { order.orderNum && (<p className="h4">Valor de la orden: <span className='text-primary'>{ formatter.format(order.amount) }</span> Iva: <span className='text-primary'>{ formatter.format(order.tax) }</span> Total: <span className='text-primary'>{ formatter.format(order.total) }</span></p>) }
      { /*order.orderNum && (<p className="h4">orderPaid.amount: <span className='text-success'>{ formatter.format(orderPaid.amount) }</span></p>)*/ }
      
      { order.orderNum && renderPendingAmount('h4') }
      { id && renderTabs() }
      { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Invoice' recordId={ id } />) }
    </div>
  )
}