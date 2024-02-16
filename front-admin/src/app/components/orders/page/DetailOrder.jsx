import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getOrderPaid, save, update } from '../../../services/OrderService';
import { handleDateStr, numberToString, mountMax, taxRate, genericErrorMsg, displayNotification, formatter, isNumDec, removeCurrencyFormat } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../../custom/alerts/types/types';
import { TableLog } from '../../custom/TableLog';
import { getOrderById } from '../../../services/OrderService';
import { TableInvoices } from '../../invoices/page/TableInvoices';
import { getCatalogChilds } from '../../../services/CatalogService';
import { setCurrentOrdTab, setCurrentTab, setOrder, setProjectPaid, setProject } from '../../../../store/project/projectSlice';
import { TextArea } from '../../custom/TextArea';
import { getProjectById, getProjectSelect } from '../../../services/ProjectService';
import { setModalChild } from '../../../../store/modal/modalSlice';
import { SelectSearcher } from '../../custom/SelectSearcher';

export const DetailOrder = () => {
  
  const dispatch = useDispatch();
  const { project, order, projectPaid, orderPaid } = useSelector( state => state.projectReducer );
  const { permissions } = useSelector( state => state.auth );
  const {currentOrdTab: currentTab} = useSelector( state => state.projectReducer );
  const { projectKey, id } = useParams();

  const navigate = useNavigate();
  const [orderNum, setOrderNum] = useState('');
  const [orderDate, setOrderDate] = useState(new Date());
  const [status, setStatus] = useState('2000600001');
  const [requisition, setRequisition] = useState('');
  const [requisitionDate, setRequisitionDate] = useState(new Date());
  const [requisitionStatus, setRequisitionStatus] = useState('2000600001');
  const [amount, setAmount] = useState('');
  const [amountInDb, setAmountInDb] = useState(0);
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [observations, setObservations] = useState('');
  const [pId, setPId] = useState(projectKey === '0' ? '' : projectKey);
  const [projects, setProjects] = useState([]);
  const [pFilter, setPFilter] = useState('');
  const [currentPaid, setCurrentPaid] = useState(projectPaid.amount ? projectPaid.amount : 0);

  const [catStatus, setCatStatus] = useState([]);

  const fetchSelects = () => {
    getCatalogChilds(1000000006).then( response => {
      setCatStatus(response.filter( cat => cat.status === 2000100001 ));
    }).catch( error => {
      console.log(error);
    });
  };

  const fetchProjects = () => {
    getProjectSelect().then( response => {
      setProjects(response);
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }
  
  const fetchProject = projectId => {
    getProjectById(projectId).then( response => {
      dispatch(setProject(response));
      fetchPaid(projectId);
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const fetchPaid = projectId => {
    getOrderPaid(projectId).then( response => {
      dispatch(setProjectPaid(response));
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
        setAmount(formatter.format(response.amount));
        setAmountInDb( response.amount );
        setTax(formatter.format(response.tax));
        setTotal(formatter.format(response.total));
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
    if( projectKey === '0' ) {
      fetchProjects();
    }
    if( !(project && project.id) && projectKey !== '0' ) {
      fetchProject(projectKey);
    }
    if( id ) {
      fetchOrder();
    }
  }, []);

  const onChangeAmount = ({ target }) => {
    const inputAmount = target.value;
    if( inputAmount === '' ) {
      setAmount('');
      setTax('');
      setTotal('');
      const newPaid = id ? currentPaid - amountInDb : currentPaid;
      dispatch( setProjectPaid( { ...projectPaid, amount: newPaid } ) )
    } else if( isNumDec(inputAmount) ) {
      if( inputAmount >= 0 && inputAmount <= mountMax ) {
        const tax = Number(inputAmount) * taxRate;
        const total = Number(inputAmount) + tax;
        setAmount(inputAmount);
        setTax(tax.toFixed(2));
        setTotal(total.toFixed(2));
        const newPaid = id ? ((currentPaid - amountInDb) + Number(inputAmount)) : currentPaid + Number(inputAmount);
        dispatch( setProjectPaid( { ...projectPaid, amount: newPaid } ) )
      }
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
    if( status !== '2000600003' && target.value === '2000600003' && status !== '2000600004' && target.value === '2000600004' ) {
      const newAmount = projectPaid.amount - amount;
      dispatch( setProjectPaid( { ...projectPaid, amount: newAmount } ) );
    }
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
    setPFilter(target.value);
    const project = projects.find( p => p.value === target.value );
    if( project ) {
      setPId(project.id);
      fetchProject(project.id);
    } else {
      setPId('');
      dispatch(setProject({}));
      dispatch(setProjectPaid({}));
    }
  }

  const onClean = () => {
    setPFilter('');
    setPId('');
    dispatch(setProject({}));
    dispatch(setProjectPaid({}));
  }

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const requestObj = Object.fromEntries(data.entries());
    const request = { ...requestObj, 
      amount: removeCurrencyFormat(amount),
      tax: removeCurrencyFormat(tax),
      total: removeCurrencyFormat(total)
    };
    const isAmountPending = (project.amount - projectPaid.amount) !== 0;
    const isAmountDiff = removeCurrencyFormat(amount) !== (project.amount - projectPaid.amount);
    if ( isAmountPending && isAmountDiff ) {
      dispatch( setModalChild( renderModal(request) ) )
    } else {
      persistOrder(request);
    }
  }

  const saveOrder = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, '¡Orden agregada correctamente!', alertType.success);
        navigate(`/project/${pId}/edit`, { replace: true });
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

  const persistOrder = request => {
    if ( id && permissions.canEditOrd ) {
      updateOrder(request);
    } else if ( permissions.canCreateOrd ) {
      request.projectKey = pId;
      saveOrder(request);
    }
    dispatch( setModalChild(null) );
    if( projectKey === '0' ) {
      dispatch( setCurrentTab(3) );
    }
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary" disabled={ pId === '' }>Guardar</button>);
    return ( ( id && permissions.canEditOrd ) || permissions.canCreateOrd ) ? saveButton : null;
  };

  const onClickBack = () => {
    if ( /*(!permissions.canCreateOrd && currentTab == 2) ||*/ currentTab === 1 ) {
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

  const renderModal = request => (
    <div className='p-5 bg-white rounded-3'>
      <div className='text-start'>
          <p className="h2">¡El monto capturado es diferente al monto pendiente!</p>
          <ul style={ { marginBottom: '0' } }>
            {/* <li key={ 1 }><p className="h3">Monto pagado: <span className='text-primary'>{ formatter.format(paid.amount) }</span></p></li> */}
            <li key={ 1 }>{ renderPendingAmount("h3") }</li>
            <li key={ 2 }><p className="h3">Monto capturado: <span className='text-success'>{ amount }</span></p></li>
          </ul>
          <p className="h4">Puede continuar, pero es necesario que verifique los montos</p>
      </div>
      <div className="pt-3 d-flex justify-content-between">
          <button type="button" className="btn btn-danger" onClick={ () => dispatch( setModalChild(null) ) }>Cancelar</button>
          &nbsp;
          <button type="button" className="btn btn-primary" onClick={ () => persistOrder(request) }>Guardar</button>
      </div>
    </div>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
      <form onSubmit={ onSubmit }>
          <div className='text-center'>
            { projectKey === '0' && (
              <div className="row text-start">
                <div className='col-12'>
                  <SelectSearcher label="Proyecto" options={ projects } disabled={ !permissions.canEditOrd } value={ pFilter } required 
                    onChange={ onChangePId } onClean={ onClean } />
                </div>
              </div>
            )}
            <div className="row text-start">
              <div className='col-4'>
                <InputText name='orderNum' label='No. de orden' placeholder='Ingresa no. de orden' required
                  disabled = { !permissions.canEditOrd } value={ orderNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
              </div>
              <div className='col-4'>
                <DatePicker name="orderDate" label="Fecha De Orden" required maxDate={ new Date() }
                  disabled = { !permissions.canEditOrd }value={ orderDate } onChange={ (date) => onChangeOrderDate(date) } />
              </div>
              <div className='col-4'>
                <Select name = "status" label="Status" required options={ catStatus } value={ status } onChange={ onChangeStatus } 
                  disabled = { !permissions.canEditOrd || ((order.status === 2000600003 || order.status === 2000600004) && projectPaid.amount >= order.amount) }  />
              </div>
            </div>
            <div className='row text-start'>
              <div className='col-4'>
                <InputText name='requisition' label='No. de Requisici&oacute;n' placeholder='Ingresa no. de requisici&oacute;n' required
                  disabled = { !permissions.canEditOrd } value={ requisition } onChange={ onChangeRequisition } maxLength={ 8 } />
              </div>
              <div className='col-4'>
                <DatePicker name="requisitionDate" label="Fecha No. De Requisici&oacute;n" required maxDate={ new Date() }
                  disabled = { !permissions.canEditOrd } value={ requisitionDate } onChange={ (date) => onChangeRequisitionDate(date) } />
              </div>
              <div className='col-4'>
                <Select name = "requisitionStatus" label="Status de Requisici&oacute;n" required options={ catStatus } 
                  disabled = { !permissions.canEditOrd } value={ requisitionStatus } onChange={ onChangeRequisitionStatus } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-4'>
                <InputText name="amount" label='Monto' placeholder='Ingresa monto' 
                  disabled = { !permissions.canEditOrd } value={ `${amount}` } required 
                  onChange={ onChangeAmount } onFocus={ onFocusAmount } onBlur={ onBlurAmount } />
              </div>
              <div className='col-4'>
                <InputText name="tax" label='Iva' readOnly disabled = { !permissions.canEditOrd } value={ `${tax}` } />
              </div>
              <div className='col-4'>
                <InputText name="total" label='Total' readOnly disabled = { !permissions.canEditOrd } value={ `${total}` } />
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
              { ( !id || (id && order.active) ) && renderSaveButton() }
              &nbsp;
              <button type="button" className="btn btn-danger" onClick={ () => navigate( projectKey !== '0' ? `/project/${ projectKey }/edit` : '/orders') }>Cancelar</button>
          </div>
      </form>
    </div>
  )

  const titleWithOrder = `${order.orderNum ? ': ' + order.orderNum : '' }${order.requisition ? ' > Requisición: ' + order.requisition : ''}`;
  const title = pId !== '' ? `${project.key} ${project.description} > Orden${ titleWithOrder }` : 'Orden nueva';

  const renderPendingAmount = classP => {
    // const pendingAmount = currentTab === 1 ? project.amount - currentPaid : order.amount - paid.amount;
    const pendingAmount = currentTab === 1 ? project.amount - projectPaid.amount : order.amount - orderPaid.amount;
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
      { pId !== '' && currentTab === 1 && (
        <>
          <p className="h4">
            {/* Iva: <span className='text-primary'>{ project.tax - paid.taxPaid }</span> Total: <span className='text-primary'>{ project.total - paid.totalPaid }</span> */}
            Costo del proyecto: <span className='text-primary'>{ formatter.format(project.amount) }</span>
          </p>
          { renderPendingAmount("h4") }
        </>
      )}
      { pId !== '' && currentTab === 2 && (
        <>
          <p className="h4">
            Costo de la orden: <span className='text-primary'>{ formatter.format(order.amount) }</span> Iva: <span className='text-primary'>{ formatter.format(order.tax) }</span> Total: <span className='text-primary'>{ formatter.format(order.total) }</span>
          </p>
          { renderPendingAmount("h4") }
        </>
      )}
      { renderTabs() }
      {
        currentTab === 1 ? renderDetail() : ( currentTab === 2 
          ? <TableInvoices projectId={pId} orderId={id} /> 
          : <TableLog tableName='Order' recordId={ id } />
        )
      }
    </div>
  )
}