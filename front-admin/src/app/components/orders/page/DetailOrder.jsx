import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getProjectById } from '../../../services/ProjectService';
import { save, update } from '../../../services/OrderService';
import { handleDateStr, numberToString, mountMax, taxRate, genericErrorMsg, displayNotification } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../../custom/alerts/types/types';
import { TableLog } from '../../custom/TableLog';
import { getOrderById } from '../../../services/OrderService';
import { TableInvoices } from '../../invoices/page/TableInvoices';
import { getCatalogChilds } from '../../../services/CatalogService';
import { setCurrentOrdTab, setOrder } from '../../../../store/project/projectSlice';

export const DetailOrder = () => {

  const dispatch = useDispatch();
  const { project, order, paid } = useSelector( state => state.projectReducer );
  const { permissions } = useSelector( state => state.auth );
  const {currentOrdTab: currentTab} = useSelector( state => state.projectReducer );
  const { projectId, id } = useParams();

  const navigate = useNavigate();
  const [orderNum, setOrderNum] = useState('');
  const [orderDate, setOrderDate] = useState();
  const [status, setStatus] = useState('');
  const [requisition, setRequisition] = useState('');
  const [requisitionDate, setRequisitionDate] = useState();
  const [requisitionStatus, setRequisitionStatus] = useState('')
  const [amount, setAmount] = useState('');
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [application, setApplication] = useState({});

  const [catStatus, setCatStatus] = useState([]);
  const [catReqStatus, setCatReqStatus] = useState([]);

  const fetchSelects = () => {
    getCatalogChilds(1000000006).then( response => {
      setCatStatus(response.filter( cat => cat.status === 2000100001 ));
    }).catch( error => {
      console.log(error);
    });
    getCatalogChilds(1000000007).then( response => {
      setCatReqStatus(response.filter( cat => cat.status === 2000100001 ));
    }).catch( error => {
      console.log(error);
    });
  };

  const fetchApplication = () => {
    getProjectById(projectId).then( response => {
      if( response.code ) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        setApplication(response);
      }
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const fetchOrder = () => {
    getOrderById(id).then( response => {
      if( response.code ) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        setAmount(numberToString(response.amount, ''));
        setTax(numberToString(response.tax, ''))
        setTotal(numberToString(response.total, ''))
        setOrderNum(response.orderNum ? response.orderNum : '');
        setOrderDate(handleDateStr(response.orderDate));
        setStatus(numberToString(response.status, ''));
        setRequisition(response.requisition ? response.requisition : '');
        setRequisitionDate(handleDateStr(response.requisitionDate));
        setRequisitionStatus(numberToString(response.requisitionStatus, ''));
        dispatch( setOrder(response) );
      }
    }).catch( error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  useEffect(() => {
    if( currentTab === 1 ) {
      dispatch(setCurrentOrdTab(permissions.canCreateOrd ? 1 : 3));
    }
    fetchSelects();
    fetchApplication();
    if( id ) {
      fetchOrder();
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
  const onChangeNumOrder = ({ target }) => setOrderNum(target.value);
  const onChangeOrderDate = date => setOrderDate(date)
  const onChangeRequisition = ({ target }) => setRequisition(target.value);
  const onChangeRequisitionDate = date => setRequisitionDate(date)
  const onChangeRequisitionStatus = ({target }) => setRequisitionStatus(target.value);

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    // if ( amount !== numberToString(application.amount) ) {
    //   dispatch(setMessage(buildPayloadMessage(`El monto '${amount}' no coincide con el de la aplicación '${application.amount}'`, alertType.warning)));
    //   return;
    // }
    if ( id && permissions.canEditOrd ) {
      updateOrder(request);
    } else if ( permissions.canCreateOrd ) {
      request.projectId = projectId;
      saveOrder(request);
    }    
  }

  const saveOrder = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, '¡Orden agregada correctamente!', alertType.success);
        navigate(`/project/${projectId}/edit`, { replace: true });
      }
    }).catch(error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.success);
    });
  }

  const updateOrder = request => {
    update(id, request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, '¡Orden actualizada correctamente!', alertType.success);
        navigate(`/project/${projectId}/edit`, { replace: true });
      }
    }).catch(error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.success);
    });
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary">Guardar</button>);
    return ( ( id && permissions.canEditOrd ) || permissions.canCreateOrd ) ? saveButton : null;
  };

  const handleAddInvoice = () => {
    navigate(`/project/${ projectId }/order/${id}/invoice/add`);
  }

  const renderAddInvoiceButton = () => permissions.canCreateOrd && (
    <div className="d-flex flex-row-reverse p-2">
      {/* <p className="h5">Valor de la orden: <span className='text-primary'>{ order.amount }</span> Iva: <span className='text-primary'>{ order.tax }</span> Total: <span className='text-primary'>{ order.total }</span></p> */}
      {/* <p className="h5">Monto pagado: <span className='text-success'>{ paid.amount }</span> Iva: <span className='text-success'>{ paid.tax }</span> Total: <span className='text-success'>{ paid.total }</span></p> */}
      <button type="button" className="btn btn-primary" onClick={ handleAddInvoice }>
          <span className="bi bi-plus"></span>
      </button>
    </div>
  );

  const renderTabs = () => (
    <ul className="nav nav-tabs">
      <li>
        <button type="button" className="btn btn-link" onClick={ () => navigate(`/project/${ projectId }/edit`) }>&lt;&lt; Regresar</button>
      </li>
      {
        permissions.canCreateOrd && (
          <li className="nav-item" onClick={ () => dispatch(setCurrentOrdTab(1)) }>
            <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
          </li>
        )
      }
      {
        permissions.canAdminOrd && (
          <li className="nav-item" onClick={ () => dispatch(setCurrentOrdTab(3)) }>
            <a className={ `nav-link ${ (currentTab === 3) ? 'active' : '' }` }>Facturas</a>
          </li>
        )
      }
      <li className="nav-item" onClick={ () => dispatch(setCurrentOrdTab(2)) }>
        <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
      </li>
    </ul>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
      <form onSubmit={ onSubmit }>
          <div className='text-center'>
            <div className="row text-start">
              <div className='col-4'>
                <InputText name='orderNum' label='No. de orden' placeholder='Ingresa no. de orden' required
                    value={ orderNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
              </div>
              <div className='col-4'>
                <DatePicker name="orderDate" label="Fecha De Orden" required value={ orderDate } onChange={ (date) => onChangeOrderDate(date) } />
              </div>
              <div className='col-4'>
                <Select name = "status" label="Status" required options={ catStatus } value={ status } onChange={ onChangeStatus } />
              </div>
            </div>
            <div className='row text-start'>
              <div className='col-4'>
                <InputText name='requisition' label='No. de Requisici&oacute;n' placeholder='Ingresa no. de requisici&oacute;n' required
                    value={ requisition } onChange={ onChangeRequisition } maxLength={ 8 } />
              </div>
              <div className='col-4'>
                <DatePicker name="requisitionDate" label="Fecha No. De Requisici&oacute;n" required value={ requisitionDate } onChange={ (date) => onChangeRequisitionDate(date) } />
              </div>
              <div className='col-4'>
                <Select name = "requisitionStatus" label="Status de Requisici&oacute;n" required options={ catReqStatus } value={ requisitionStatus } onChange={ onChangeRequisitionStatus } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-4'>
                <InputText name="amount" label='Monto' type='number' placeholder='Ingresa monto' value={ `${amount}` } required onChange={ onChangeAmount } />
              </div>
              <div className='col-4'>
                <InputText name="tax" label='Iva' type='text' readOnly value={ `${tax}` } />
              </div>
              <div className='col-4'>
                <InputText name="total" label='Total' type='text' readOnly value={ `${total}` } />
              </div>
            </div>
          </div>
          <div className="pt-3 d-flex flex-row-reverse">
              { renderSaveButton() }
              &nbsp;
              <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/${ projectId }/edit`) }>Cancelar</button>
          </div>
      </form>
    </div>
  )

  const titleWithOrder = () => {
    return currentTab !== 1 ? `${order.orderNum ? ': ' + order.orderNum : '' }${order.requisition ? ' > Requisición: ' + order.requisition : ''}` : '';
  }
 
  return (
    <div className='px-5'>
      <div className='d-flex justify-content-between'>
      <span className={`fs-${ currentTab === 1 ? '4' : '6'} card-title fw-bold mb-4`}>{ `${project.key} ${project.description}` } &gt; Orden{ titleWithOrder() }</span>
        { id && renderTabs() }
      </div>
      { (currentTab === 3 && id ) ? renderAddInvoiceButton() : null }
      {
        currentTab === 1 ? renderDetail() : ( currentTab === 3 
          ? <TableInvoices projectId={projectId} orderId={id} /> 
          : <TableLog tableName='Order' recordId={ id } />
        )
      }
    </div>
  )
}