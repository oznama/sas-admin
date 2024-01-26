import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { handleDateStr, numberToString, mountMax, taxRate, genericErrorMsg, displayNotification } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../../custom/alerts/types/types';
import { TableLog } from '../../custom/TableLog';
import { getInvoiceById, save, update } from '../../../services/InvoiceService';
import { getCatalogChilds } from '../../../services/CatalogService';
import { TextArea } from '../../custom/TextArea';

const getPaymentDate = () => {
  const currentDate = new Date();
  currentDate.setDate(currentDate.getDate() + 22);
  return currentDate;
}

export const DetailInvoice = () => {

  const dispatch = useDispatch();
  const { project, order, paid } = useSelector( state => state.projectReducer );
  const { permissions } = useSelector( state => state.auth );
  const { projectId, orderId, id } = useParams();

  const navigate = useNavigate();
  const [invoiceNum, setInvoiceNum] = useState('');
  const [issuedDate, setIssuedDate] = useState(new Date());
  const [paymentDate, setPaymentDate] = useState( getPaymentDate() );
  const [percentage, setPercentage] = useState(0);
  const [currentTab, setCurrentTab] = useState(1);
  const [amount, setAmount] = useState('');
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [observations, setObservations] = useState('');
  const [status, setStatus] = useState('2000800001');
  const [catStatus, setCatStatus] = useState([])

  const fetchSelects = () => {
    getCatalogChilds(1000000008).then( response => {
      setCatStatus(response.filter( cat => cat.status === 2000100001 ));
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
    if( amount >= 0 && amount <= mountMax ) {
        const tax = amount * taxRate;
        const total = amount + tax;
        setAmount(amount);
        setTax(tax.toFixed(2));
        setTotal(total.toFixed(2));

        const porc = (amount * 100)/order.amount;
        setPercentage(porc);
      }
  }

  const onChangeAmount = ({ target }) => {
    const amount = Number(target.value);
    calculateAmounts(amount);
  }
  const onChangeStatus = ({target }) => setStatus(target.value);
  const onChangeNumOrder = ({ target }) => setInvoiceNum(target.value);
  const onChangeIssuedDate = date => setIssuedDate(date)
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
      displayNotification(dispatch, 'El monto de la factura no puede superar el monto de la orden', alertType.warning);
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
    return ( ( id && permissions.canEditOrd ) || permissions.canCreateOrd ) ? saveButton : null;
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

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
        <p className="h5">Valor de la orden: <span className='text-primary'>{ order.amount }</span> Iva: <span className='text-primary'>{ order.tax }</span> Total: <span className='text-primary'>{ order.total }</span></p>
        {/* <p className="h5">Monto pagado: <span className='text-success'>{ paid.amount }</span> Iva: <span className='text-success'>{ paid.tax }</span> Total: <span className='text-success'>{ paid.total }</span></p> */}
        <form onSubmit={ onSubmit }>
            <div className='text-center'>
                <div className="row text-start">
                    <div className='col-4'>
                        <InputText name='invoiceNum' label='No. de factura' placeholder='Ingresa no. de factura' value={ invoiceNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
                    </div>
                    <div className='col-4'>
                        <InputText name="percentage" label='Porcentaje' type='number' placeholder='Ingresa porcentaje' value={ `${percentage}` } required onChange={ onChangePercentage } />
                    </div>
                    <div className='col-4'>
                        <Select name = "status" label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
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
                  <div className='col-12'>
                    <TextArea name='observations' label='Observaciones' placeholder='Escribe observaciones' 
                          value={ observations } maxLength={ 1500 } onChange={ onChangeObservations } />
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