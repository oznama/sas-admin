import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getEmployess } from '../../../services/EmployeeService';
import { getProjectApplicationById, saveApplication, updateApplication } from '../../../services/ProjectService';
import { handleDateStr, numberToString, mountMax, numberMaxLength, taxRate, genericErrorMsg, displayNotification, formatter, isNumDec, removeCurrencyFormat } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../../custom/alerts/types/types';
import { TableLog } from '../../custom/TableLog';
import { setCurrentAppTab } from '../../../../store/project/projectSlice';
import { TextArea } from '../../custom/TextArea';
import { getAplications } from '../../../services/ApplicationService';

export const DetailApplications = () => {

  const dispatch = useDispatch();
  const { project } = useSelector( state => state.projectReducer );
  const { permissions } = useSelector( state => state.auth );
  const {currentAppTab: currentTab} = useSelector( state => state.projectReducer );
  const { projectKey, id } = useParams();

  const navigate = useNavigate();
  const [aplication, setAplication] = useState('');
  const [amount, setAmount] = useState('');
  const [tax, setTax] = useState('');
  const [total, setTotal] = useState('');
  // const [status, setStatus] = useState('2000900001');
  const [leader, setLeader] = useState('');
  const [developer, setDeveloper] = useState('');
  const [hours, setHours] = useState('');
  const [endDate, setEndDate] = useState();
  const [designDate, setDesignDate] = useState();
  const [startDate, setStartDate] = useState()
  const [developmentDate, setDevelopmentDate] = useState();
  const [active, setActive] = useState(true)
  const [observations, setObservations] = useState('');

  // const [catStatus, setCatStatus] = useState([]);
  const [catAplications, setCatApliations] = useState([]);
  const [catEmployees, setCatEmployees] = useState([]);

  const isModeEdit = ( id && !permissions.canEditProjApp );

  const fetchApplication = () => {
    getProjectApplicationById(projectKey, id).then( response => {
      if( response.code ) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        setAplication(numberToString(response.applicationId, ''));
        setAmount(numberToString(response.amount, ''));
        setTax(numberToString(response.tax, ''))
        setTotal(numberToString(response.total, ''))
        setLeader(numberToString(response.leaderId, ''));
        setDeveloper(numberToString(response.developerId, ''));
        setHours(numberToString(response.hours, ''));
        setStartDate(handleDateStr(response.startDate));
        setEndDate(handleDateStr(response.endDate));
        setDesignDate(handleDateStr(response.designDate));
        setDevelopmentDate(handleDateStr(response.developmentDate));
        setActive(response.active);
        setObservations(response.observations ? response.observations : '');
      }
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const fetchSelects = () => {
    
    getAplications()
      .then( response => {
        const cat = [];
        response.map( r => cat.push({ ...r, id: r.value }) );
        setCatApliations(cat);
      }).catch( error => {
        console.log(error);
      });

    // getCatalog(1000000009)
    //   .then( response => {
    //     setCatStatus(response);
    //   }).catch( error => {
    //     console.log(error);
    //   });
    
    getEmployess()
      .then( response => {
        setCatEmployees(response);
      }).catch( error => {
        console.log(error);
      });
  };

  useEffect(() => {
    if( id ) {
      fetchApplication();
    }
    fetchSelects();
  }, []);

  const onChangeAplication = ({ target }) => setAplication(target.value);
  const onChangeAmount = ({ target }) => {
    if( target.value === '' ) {
      setAmount('');
      setTax('');
      setTotal('');
    } else if( isNumDec(target.value) ) {
      const amount = Number(target.value);
      if( amount >= 0 && amount <= mountMax ) {
        const tax = amount * taxRate;
        const total = amount + tax;
        setAmount(target.value);
        setTax(tax.toFixed(2));
        setTotal(total.toFixed(2));
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
  const onChangeStatus = ({target }) => setStatus(target.value);
  const onChangeLeader = ({ target }) => setLeader(target.value);
  const onChangeDeveloper = ({ target }) => setDeveloper(target.value);
  const onChangeHours = ({ target }) => {
    if( target.value.length  <= numberMaxLength ) {
      setHours(target.value);
    }
  }
  const onChangeEndDate = date => setEndDate(date);
  const onChangeDesignDate = date => setDesignDate(date);
  const onChangeDevelopmentDate = date => setDevelopmentDate(date);
  const onChangeStartDate = date => setStartDate(date);
  const onChangeObservations = ({ target }) => setObservations(target.value);

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const requestObj = Object.fromEntries(data.entries());
    const request = { ...requestObj, 
      amount: removeCurrencyFormat(amount),
      tax: removeCurrencyFormat(tax),
      total: removeCurrencyFormat(total)
    };
    if( request.amount == 0 ) {
      displayNotification(dispatch, 'El monto del proyecto no puede ser 0', alertType.error);
    } else {
      if ( id && (permissions.canEditProjApp || permissions.canEditRequi) ) {
        update(request);
      } else if ( permissions.canCreateProjApp ) {
        request.projectKey = projectKey;
        save(request);
      }
    }
  }

  const save = request => {
    saveApplication(request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, '¡Aplicación agregada correctamente al proyecto!', alertType.success);
        navigate(`/project/${projectKey}/edit`, { replace: true });
      }
    }).catch(error => {
      console.log(error)
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const update = request => {
    updateApplication(id, request).then( response => {
      if(response.code && response.code !== 201) {
        displayNotification(dispatch, response.message, alertType.error);
      } else {
        displayNotification(dispatch, '¡Aplicacion actualizada correctamente!', alertType.success);
        navigate(`/project/${projectKey}/edit`, { replace: true });
      }
    }).catch(error => {
      console.log(error)
      displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  const renderSaveButton = () => {
    const saveButton = (<button type="submit" className="btn btn-primary">Guardar</button>);
    if(isModeEdit) {
      return permissions.canEditRequi ? saveButton : null;
    } else {
      return saveButton ;
    }
  };

  const renderTabs = () => id && (
    <div className='d-flex flex-row-reverse'>
      <ul className="nav nav-tabs">
        <li>
          <button type="button" className="btn btn-link" onClick={ () => navigate(`/project/${ projectKey }/edit`) }>&lt;&lt; Regresar</button>
        </li>
        <li className="nav-item" onClick={ () => dispatch(setCurrentAppTab(1)) }>
          <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
        </li>
        <li className="nav-item" onClick={ () => dispatch(setCurrentAppTab(2)) }>
          <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
        </li>
      </ul>
    </div>
  )

  const renderDetail = () => (
    <div className='d-grid gap-2 col-6 mx-auto'>
      <form onSubmit={ onSubmit }>
        <div className='text-center'>
          <div className="row text-start">
            <div className='col-6'>
              <Select name="application" label="Aplicaci&oacute;n" disabled={ !active || isModeEdit } options={ catAplications } value={ aplication } required onChange={ onChangeAplication } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-6'>
              <InputText name="amount" label='Monto' placeholder='Ingresa monto' 
                disabled={ !active ||isModeEdit } value={ `${amount}` } required 
                onChange={ onChangeAmount } onFocus={ onFocusAmount } onBlur={ onBlurAmount } />
            </div>
            <div className='col-3'>
              <InputText name="tax" label='Iva' disabled={ !active ||isModeEdit } readOnly value={ `${tax}` } />
            </div>
            <div className='col-3'>
              <InputText name="total" label='Total' disabled={ !active ||isModeEdit } readOnly value={ `${total}` } />
            </div>
          </div>
          <div className="row text-start">
            {/* <div className='col-4'>
              <Select name = "statusId" label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
            </div> */}
            <div className='col-6'>
              <InputText name="hours" label='Horas' type='number'  placeholder='Ingresa las horas' disabled={ !active ||isModeEdit } required value={ hours } onChange={ onChangeHours } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-6'>
              <Select name="leaderId" label="L&iacute;der" disabled={ !active ||isModeEdit } options={ catEmployees } value={ leader } required onChange={ onChangeLeader } />
            </div>
            <div className='col-6'>
              <Select name="developerId" label="Desarrollador" disabled={ !active || isModeEdit } options={ catEmployees } value={ developer } required onChange={ onChangeDeveloper } />
            </div>
          </div>

          <div className="row text-start">
            <div className='col-6'>
              <DatePicker name="startDate" label="Inicio" disabled={ !active || isModeEdit } value={ startDate } required onChange={ (date) => onChangeStartDate(date) } />
            </div>
            <div className='col-6'>
              <DatePicker name="designDate" label="Analisis y dise&ntilde;o" disabled={ !active || isModeEdit } value={ designDate } required onChange={ (date) => onChangeDesignDate(date) } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-6'>
              <DatePicker name="developmentDate" label="Construcci&oacute;n" disabled={ !active || isModeEdit } value={ developmentDate } required onChange={ (date) => onChangeDevelopmentDate(date) } />
            </div>
            <div className='col-6'>
              <DatePicker name="endDate" label="Cierre" value={ endDate } disabled={ !active || isModeEdit } required onChange={ (date) => onChangeEndDate(date) } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-12'>
              <TextArea name='observations' label='Observaciones' placeholder='Escribe observaciones' 
                    value={ observations } maxLength={ 1500 } onChange={ onChangeObservations } disabled={ !active || isModeEdit } />
            </div>
          </div>
        </div>
        { active &&
          (
            <div className="pt-3 d-flex flex-row-reverse">
                { renderSaveButton() }
                &nbsp;
                <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/${ projectKey }/edit`) }>Cancelar</button>
            </div>
          )
        }
      </form>
    </div>
  )
 
  return (
    <div className='px-5'>
      <h3 className="fs-4 card-title fw-bold">{ `${project.key} ${project.description}` } &gt; Aplicacion</h3>
      { renderTabs() }
      { currentTab === 1 ? renderDetail() : ( <TableLog tableName='ProjectApplication' recordId={ id } />) }
    </div>
  )
}