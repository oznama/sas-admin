import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getProjectApplicationById } from '../../../services/ProjectService';
import { save, update } from '../../../services/OrderService';
import { handleDateStr, numberToString, buildPayloadMessage, mountMax, taxRate } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { setMessage } from '../../../../store/alert/alertSlice';
import { alertType } from '../../custom/alerts/types/types';
import { Alert } from '../../custom/alerts/page/Alert';
import { TableLog } from '../../custom/TableLog';
import { getOrderById } from '../../../services/OrderService';
import { TableInvoices } from '../../invoices/page/TableInvoices';

export const DetailOrder = () => {

  const dispatch = useDispatch();
  const { permissions } = useSelector( state => state.auth );
  const { projectId, projectApplicationId, id } = useParams();

  const navigate = useNavigate();
  const [orderNum, setOrderNum] = useState('');
  const [orderDate, setOrderDate] = useState();
  const [currentTab, setCurrentTab] = useState(1);
  const [amount, setAmount] = useState('');
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  const [application, setApplication] = useState({});
  // const [status, setStatus] = useState('2000900001');

  const fetchApplication = () => {
    getProjectApplicationById(projectId, projectApplicationId).then( response => {
      if( response.code ) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        setApplication(response);
      }
    }).catch( error => {
        dispatch(setMessage(
            buildPayloadMessage(
                'Ha ocurrido un error al cargar los montos de la aplicación, contacte al adminitrador', 
                alertType.error
            )
        ));
    });
  }

  const fetchOrder = () => {
    getOrderById(id).then( response => {
      if( response.code ) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        setAmount(numberToString(response.amount, ''));
        setTax(numberToString(response.tax, ''))
        setTotal(numberToString(response.total, ''))
        setOrderNum(response.orderNum ? response.orderNum : '');
        setOrderDate(handleDateStr(response.orderDate));
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

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    if ( amount !== application.amount ) {
      dispatch(setMessage(buildPayloadMessage(`El monto '${amount}' no coincide con el de la aplicación '${application.amount}'`, alertType.warning)));
      return;
    }
    if ( id && permissions.canEditRequi ) {
      updateOrder(request);
    } else if ( permissions.canEditRequi ) {
      request.projectApplicationId = projectApplicationId;
      saveOrder(request);
    }    
  }

  const saveOrder = request => {
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('Orden agregada correctamente!', alertType.success)));
        navigate(`/project/${projectId}/application/${projectApplicationId}/edit`, { replace: true });
      }
    }).catch(error => {
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear la orden, contacte al administrador', alertType.error)));
    });
  }

  const updateOrder = request => {
    update(id, request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡Orden actualizada correctamente!', alertType.success)));
        if( request.requisition ) {
          setHasRequisition(true);
        }
      }
    }).catch(error => {
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar la orden, contacte al administrador', alertType.error)));
    });
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary">Guardar</button>);
    return permissions.canEditRequi ? saveButton : null;
  };

  const handleAddInvoice = () => {
    navigate(`/project/${ projectId }/application/${ projectApplicationId }/order/${id}/invoice/add`);
  }

  const renderAddInvoiceButton = () => (
    <div className="d-flex flex-row-reverse p-2">
        <button type="button" className="btn btn-primary" onClick={ handleAddInvoice }>
            <span className="bi bi-plus"></span>
        </button>
    </div>
  );

  const renderTabs = () => (
    <ul className="nav nav-tabs">
      <li className="nav-item" onClick={ () => setCurrentTab(1) }>
        <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
      </li>
      {
        permissions.canEditRequi && (
          <li className="nav-item" onClick={ () => setCurrentTab(3) }>
            <a className={ `nav-link ${ (currentTab === 3) ? 'active' : '' }` }>Facturas</a>
          </li>
        )
      }
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
                <InputText name='orderNum' label='No. de orden' placeholder='Ingresa no. de orden' 
                    value={ orderNum } onChange={ onChangeNumOrder } maxLength={ 8 } />
              </div>
              <div className='col-6'>
                <DatePicker name="orderDate" label="Fecha De Orden" value={ orderDate } onChange={ (date) => onChangeOrderDate(date) } />
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
              <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/${ projectId }/application/${ projectApplicationId }/edit`) }>Cancelar</button>
          </div>
      </form>
    </div>
  )
 
  return (
    <div className='px-5'>
      <div className='d-flex justify-content-between'>
        <h1 className="fs-4 card-title fw-bold mb-4">Orden</h1>
        { id && renderTabs() }
      </div>
      <Alert />
      { (currentTab === 3 && id ) ? renderAddInvoiceButton() : null }
      {
        currentTab === 1 ? renderDetail() : ( currentTab === 3 
          ? <TableInvoices projectId={projectId} projectApplicationId={projectApplicationId} orderId={id} /> 
          : <TableLog tableName='Order' recordId={ id } />
        )
      }
    </div>
  )
}