import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { save, update } from '../../../services/OrderService';
import { handleDateStr, numberToString, mountMax, taxRate, genericErrorMsg, displayNotification } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../../custom/alerts/types/types';
import { TableLog } from '../../custom/TableLog';
import { getOrderById } from '../../../services/OrderService';
import { TableInvoices } from '../../invoices/page/TableInvoices';
import { getCatalogChilds } from '../../../services/CatalogService';
import { setCurrentOrdTab, setOrder, setProject } from '../../../../store/project/projectSlice';
import { TextArea } from '../../custom/TextArea';
import { getProjectById, getProjectSelect } from '../../../services/ProjectService';

export const DetailOrder = () => {

  const dispatch = useDispatch();
  const { project, order, paid } = useSelector( state => state.projectReducer );
  const { permissions } = useSelector( state => state.auth );
  const {currentOrdTab: currentTab} = useSelector( state => state.projectReducer );
  const { projectId, id } = useParams();

  const navigate = useNavigate();
  const [orderNum, setOrderNum] = useState('');
  const [orderDate, setOrderDate] = useState(new Date());
  const [status, setStatus] = useState('2000600001');
  const [requisition, setRequisition] = useState('');
  const [requisitionDate, setRequisitionDate] = useState(new Date());
  const [requisitionStatus, setRequisitionStatus] = useState('2000600001')
  const [amount, setAmount] = useState('');
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [observations, setObservations] = useState('');
  const [pId, setPId] = useState(projectId === '0' ? '' : projectId);
  const [projects, setProjects] = useState([]);

  const [catStatus, setCatStatus] = useState([]);

  const fetchSelects = () => {
    getCatalogChilds(1000000006).then( response => {
      setCatStatus(response.filter( cat => cat.status === 2000100001 ));
    }).catch( error => {
      console.log(error);
    });
  };

  const fetProjects = () => {
    getProjectSelect().then( response => {
      setProjects(response);
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
        setObservations(response.observations);
        dispatch( setOrder(response) );
      }
    }).catch( error => {
      console.log(error);
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  useEffect(() => {
    fetchSelects();
    if( projectId === '0' ) {
      fetProjects();
    }
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
  const onChangeStatus = ({target }) => {
    setStatus(target.value);
    setRequisitionStatus(target.value);
  }
  const onChangeNumOrder = ({ target }) => setOrderNum(target.value);
  const onChangeOrderDate = date => {
    setOrderDate(date);
    setRequisitionDate(date);
  }
  const onChangeRequisition = ({ target }) => setRequisition(target.value);
  const onChangeRequisitionDate = date => setRequisitionDate(date)
  const onChangeRequisitionStatus = ({target }) => setRequisitionStatus(target.value);
  const onChangeObservations = ({ target }) => setObservations(target.value);
  const onChangePId = ({ target }) => {
    setPId(target.value);
    getProjectById(target.value).then( response => {
      dispatch(setProject(response));
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const onSubmit = event => {
    console.log(project);
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    if ( amount > project.amount ) {
      displayNotification(dispatch, 'El monto de la orden supera el monto del proyecto', alertType.info);
    } else {
      if ( id && permissions.canEditOrd ) {
        updateOrder(request);
      } else if ( permissions.canCreateOrd ) {
        request.projectId = pId;
        saveOrder(request);
      }    
    }
  }

  const saveOrder = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, '¡Orden agregada correctamente!', alertType.success);
        if( projectId === '0' ) {
          navigate('/orders');
        } else {
          navigate(`/project/${pId}/edit`, { replace: true });
        }
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
        navigate(`/project/${pId}/edit`, { replace: true });
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
    navigate(`/project/${ pId }/order/${id}/invoice/add`);
  }

  const renderAddInvoiceButton = () => permissions.canCreateOrd && (
    <div className="d-flex flex-row-reverse p-2">
      <button type="button" className="btn btn-primary" onClick={ handleAddInvoice }>
          <span className="bi bi-plus"></span>
      </button>
    </div>
  );

  const onClickBack = () => {
    if ( (!permissions.canCreateOrd && currentTab == 2) || currentTab === 1 ) {
      navigate(`/project/${ pId }/edit`)
    } else {
      dispatch(setCurrentOrdTab(currentTab - 1));
    }
  }

  const renderTabs = () => id && (
    <div className='d-flex flex-row-reverse'>
      <ul className="nav nav-tabs">
        <li>
          <button type="button" className="btn btn-link" onClick={ () => onClickBack() }>&lt;&lt; Regresar</button>
        </li>
        {
          permissions.canAdminOrd && (
            <li className="nav-item" onClick={ () => dispatch(setCurrentOrdTab(1)) }>
              <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
            </li>
          )
        }
        {
          permissions.canAdminOrd && (
            <li className="nav-item" onClick={ () => dispatch(setCurrentOrdTab(2)) }>
              <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Facturas</a>
            </li>
          )
        }
        <li className="nav-item" onClick={ () => dispatch(setCurrentOrdTab(3)) }>
          <a className={ `nav-link ${ (currentTab === 3) ? 'active' : '' }` }>Historial</a>
        </li>
      </ul>
    </div>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
      <form onSubmit={ onSubmit }>
          <div className='text-center'>
            { projectId === '0' && (
              <div className="row text-start">
                <div className='col-12'>
                  <Select name="companyId" label="Proyecto" options={ projects } disabled={ !permissions.canEditOrd } value={ pId } required onChange={ onChangePId } />
                </div>
              </div>
            )}
            <div className="row text-start">
              <div className='col-4'>
                <InputText name='orderNum' label='No. de orden' placeholder='Ingresa no. de orden' required
                  disabled = { !permissions.canEditOrd } value={ orderNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
              </div>
              <div className='col-4'>
                <DatePicker name="orderDate" label="Fecha De Orden" required 
                  disabled = { !permissions.canEditOrd }value={ orderDate } onChange={ (date) => onChangeOrderDate(date) } />
              </div>
              <div className='col-4'>
                <Select name = "status" label="Status" required options={ catStatus } 
                  disabled = { !permissions.canEditOrd } value={ status } onChange={ onChangeStatus } />
              </div>
            </div>
            <div className='row text-start'>
              <div className='col-4'>
                <InputText name='requisition' label='No. de Requisici&oacute;n' placeholder='Ingresa no. de requisici&oacute;n' required
                  disabled = { !permissions.canEditOrd } value={ requisition } onChange={ onChangeRequisition } maxLength={ 8 } />
              </div>
              <div className='col-4'>
                <DatePicker name="requisitionDate" label="Fecha No. De Requisici&oacute;n" required
                  disabled = { !permissions.canEditOrd } value={ requisitionDate } onChange={ (date) => onChangeRequisitionDate(date) } />
              </div>
              <div className='col-4'>
                <Select name = "requisitionStatus" label="Status de Requisici&oacute;n" required options={ catStatus } 
                  disabled = { !permissions.canEditOrd } value={ requisitionStatus } onChange={ onChangeRequisitionStatus } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-4'>
                <InputText name="amount" label='Monto' type='number' placeholder='Ingresa monto' 
                  disabled = { !permissions.canEditOrd } value={ `${amount}` } required onChange={ onChangeAmount } />
              </div>
              <div className='col-4'>
                <InputText name="tax" label='Iva' type='text' readOnly disabled = { !permissions.canEditOrd } value={ `${tax}` } />
              </div>
              <div className='col-4'>
                <InputText name="total" label='Total' type='text' readOnly disabled = { !permissions.canEditOrd } value={ `${total}` } />
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
              <button type="button" className="btn btn-danger" onClick={ () => navigate( projectId !== '0' ? `/project/${ projectId }/edit` : '/orders') }>Cancelar</button>
          </div>
      </form>
    </div>
  )

  const titleWithOrder = currentTab !== 1 ? `${order.orderNum ? ': ' + order.orderNum : '' }${order.requisition ? ' > Requisición: ' + order.requisition : ''}` : '';
  const title = pId !== '' ? `${project.key} ${project.description} > Orden ${ titleWithOrder }` : 'Orden nueva';
 
  return (
    <div className='px-5'>
      <span className='fs-6 card-title fw-bold mb-4'>{ title }</span>
      { pId !== '' && (
        <p className="h5">
          Costo del proyecto: <span className='text-primary'>{ project.amount }</span> Iva: <span className='text-primary'>{ project.tax }</span> Total: <span className='text-primary'>{ project.total }</span>
        </p>
      )}
      { renderTabs() }
      { (currentTab === 2 && id ) ? renderAddInvoiceButton() : null }
      {
        currentTab === 1 ? renderDetail() : ( currentTab === 2 
          ? <TableInvoices projectId={pId} orderId={id} /> 
          : <TableLog tableName='Order' recordId={ id } />
        )
      }
    </div>
  )
}