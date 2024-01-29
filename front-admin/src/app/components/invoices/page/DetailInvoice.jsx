import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { handleDateStr, numberToString, mountMax, taxRate, genericErrorMsg, displayNotification, getPaymentDate } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../../custom/alerts/types/types';
import { TableLog } from '../../custom/TableLog';
import { getInvoiceById, save, update } from '../../../services/InvoiceService';
import { getCatalogChilds } from '../../../services/CatalogService';
import { TextArea } from '../../custom/TextArea';
import { setModalChild } from '../../../../store/modal/modalSlice';

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
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [observations, setObservations] = useState('');
  const [status, setStatus] = useState('2000800001');
  const [catStatus, setCatStatus] = useState([]);

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

  const fetchInvoice = () => {
    getInvoiceById(id).then( response => {
      if( response.code ) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        setAmount(numberToString(response.amount, ''));
        setTax(numberToString(response.tax, ''))
        setTotal(numberToString(response.total, ''))
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
    const amount = Number(target.value);
    calculateAmounts(amount);
  }
  const onChangeStatus = ({target }) => setStatus(target.value);
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

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    
    if( amount > order.amount ) {
      dispatch(setModalChild(renderModal()));
    } else {
      if ( id && permissions.canEditOrd ) {
        updateInvoice(request);
      } else if ( permissions.canCreateOrd ) {
        request.orderId = orderId;
        saveInvoice(request);
      }    
    }

  }

  const saveInvoice = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, 'Factura agregada correctamente!', alertType.success);
        navigate(`/project/${projectId}/order/${orderId}/edit`, { replace: true });
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
        navigate(`/project/${projectId}/order/${orderId}/edit`, { replace: true });
      }
    }).catch(error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary">Guardar</button>);
    return (( ( id && permissions.canEditOrd ) || permissions.canCreateOrd ) && status !== '2000800002' ) ? saveButton : null;
  };

  const onClickBack = () => {
    if ( currentTab === 1 ) {
      navigate(`/project/${ projectId }/order/${orderId}/edit`)
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
          <h3>¡El monto de la factura no puede ser mayor al monto de la orden!</h3>
      </div>
      <div className="pt-3 d-flex justify-content-center">
          <button type="button" className="btn btn-danger" onClick={ () => dispatch( setModalChild(null) ) }>Cerrar</button>
      </div>
    </div>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
        <p className="h5">Valor de la orden: <span className='text-primary'>{ order.amount }</span> Iva: <span className='text-primary'>{ order.tax }</span> Total: <span className='text-primary'>{ order.total }</span></p>
        {/* <p className="h5">Monto pagado: <span className='text-success'>{ paid.amount }</span> Iva: <span className='text-success'>{ paid.tax }</span> Total: <span className='text-success'>{ paid.total }</span></p> */}
        <form onSubmit={ onSubmit }>
            <div className='text-center'>
                <div className="row text-start">
                    <div className='col-4'>
                        <InputText name='invoiceNum' label='No. de factura' placeholder='Ingresa no. de factura' disabled={ status === '2000800002' } value={ invoiceNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
                    </div>
                    <div className='col-4'>
                        <InputText name="percentage" label='Porcentaje' type='number' placeholder='Ingresa porcentaje' disabled={ status === '2000800002' } value={ `${percentage}` } required onChange={ onChangePercentage } />
                    </div>
                    <div className='col-4'>
                        <Select name = "status" label="Status" options={ catStatus } disabled={ status === '2000800002' } value={ status } onChange={ onChangeStatus } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-6'>
                        <DatePicker name="issuedDate" label="Fecha De Emisi&oacute;n" disabled={ status === '2000800002' } value={ issuedDate } onChange={ (date) => onChangeIssuedDate(date) } maxDate={ new Date() } />
                    </div>
                    <div className='col-6'>
                        <DatePicker name="paymentDate" label="Fecha De Pago" disabled={ status === '2000800002' } value={ paymentDate } onChange={ (date) => onChangePaymentDate(date) } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-6'>
                        <InputText name="amount" label='Monto' type='number' placeholder='Ingresa monto' value={ `${amount}` } required onChange={ onChangeAmount } />
                    </div>
                    <div className='col-3'>
                        <InputText name="tax" label='Iva' type='text' readOnly disabled={ status === '2000800002' } value={ `${tax}` } />
                    </div>
                    <div className='col-3'>
                        <InputText name="total" label='Total' type='text' readOnly disabled={ status === '2000800002' } value={ `${total}` } />
                    </div>
                </div>
                <div className="row text-start">
                  <div className='col-12'>
                    <TextArea name='observations' label='Observaciones' placeholder='Escribe observaciones' 
                          disabled={ status === '2000800002' } value={ observations } maxLength={ 1500 } onChange={ onChangeObservations } />
                  </div>
                </div>
            </div>
            <div className="pt-3 d-flex flex-row-reverse">
                { renderSaveButton() }
                &nbsp;
                <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/${ projectId }/order/${orderId}/edit`) }>Cancelar</button>
            </div>
        </form>
    </div>
  )
 
  return (
    <div className='px-5'>
      <div className='d-flex justify-content-between'>
      <h3 className="fs-4 card-title fw-bold mb-4">{ `${project.key} ${project.description} > Orden${order.orderNum ? ': ' + order.orderNum : '' }${order.requisition ? ' > Requisición: ' + order.requisition : ''}`}</h3>
        { id && renderTabs() }
      </div>
      { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Invoice' recordId={ id } />) }
    </div>
  )
}