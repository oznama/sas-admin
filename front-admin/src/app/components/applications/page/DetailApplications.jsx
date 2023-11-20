import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getCatalogChilds } from '../../../services/CatalogService';
import { getEmployess } from '../../../services/EmployeeService';
import { saveApplication, updateApplication } from '../../../services/ProjectService';
import { handleDateStr, numberToString, buildPayloadMessage, mountMax, numberMaxLength } from '../../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { setMessage } from '../../../../store/alert/alertSlice';
import { alertType } from '../../custom/alerts/types/types';
import { Alert } from '../../custom/alerts/page/Alert';
import { addAplication } from '../../../../store/project/projectSlice';
import { TableLog } from '../../custom/TableLog';

export const DetailApplications = () => {

  const dispatch = useDispatch();
  const { permissions } = useSelector( state => state.auth );
  const { projectApplication } = useSelector( state => state.projectReducer );
  const { projectId } = useParams();

  const navigate = useNavigate();
  const [currentTab, setCurrentTab] = useState(1);
  const [aplication, setAplication] = useState(numberToString(projectApplication.applicationId, ''));
  const [amount, setAmount] = useState(numberToString(projectApplication.amount, ''));
  // const [status, setStatus] = useState('2000900001');
  const [leader, setLeader] = useState(numberToString(projectApplication.leaderId, ''));
  const [developer, setDeveloper] = useState(numberToString(projectApplication.developerId, ''));
  const [hours, setHours] = useState(numberToString(projectApplication.hours, ''));
  const [endDate, setEndDate] = useState(handleDateStr(projectApplication.endDate));
  const [designDate, setDesignDate] = useState(handleDateStr(projectApplication.designDate));
  const [developmentDate, setDevelopmentDate] = useState(handleDateStr(projectApplication.developmentDate));
  // const [catStatus, setCatStatus] = useState([]);
  const [catAplications, setCatApliations] = useState([]);
  const [catEmployees, setCatEmployees] = useState([]);

  const isModeEdit = ( projectApplication.id && projectApplication.id > 0 && !permissions.canEditProjApp );

  const fetchSelects = () => {
    
    getCatalogChilds(1000000005)
      .then( response => {
        setCatApliations(response);
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
    fetchSelects();
  }, []);

  const onChangeAplication = ({ target }) => setAplication(target.value);
  const onChangeAmount = ({ target }) => {
    if( Number(target.value) <= mountMax ) {
      setAmount(target.value);
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
  const onChangeEndDate = (date) => setEndDate(date);
  const onChangeDesignDate = (date) => setDesignDate(date);
  const onChangeDevelopmentDate = (date) => setDevelopmentDate(date);

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    if ( projectApplication.id && permissions.canEditProjApp ) {
      update(request);
    } else if ( permissions.canCreateProjApp ) {
      request.projectId = projectApplication.projectId;
      save(request);
    }    
  }

  const save = request => {
    saveApplication(request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡Aplicacion agregada a proyecto correctamente!', alertType.success)));
        dispatch(addAplication(response));
        navigate(`/project/${projectApplication.projectId}/edit`, { replace: true });
      }
    }).catch(error => {
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear aplicación, contacte al administrador', alertType.error)));
    });
  }

  const update = request => {
    updateApplication(projectApplication.id, request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡Aplicacion actualizada correctamente!', alertType.success)));
      }
    }).catch(error => {
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar aplicación, contacte al administrador', alertType.error)));
    });
  }

  const renderSaveButton = () => {
    if(isModeEdit) {
      return null;
    } else {
      return (<button type="submit" className="btn btn-primary">Guardar</button>) ;
    }
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
    <form onSubmit={ onSubmit }>
        <div className='text-center'>
          <div className="row text-start">
            <div className='col-4'>
              <Select name="applicationId" label="Aplicaci&oacute;n" disabled={ isModeEdit } options={ catAplications } value={ aplication } required onChange={ onChangeAplication } />
            </div>
            <div className='col-4'>
              <InputText name="amount" label='Monto' type='number' placeholder='Ingresa monto' disabled={ isModeEdit } value={ amount } required onChange={ onChangeAmount } />
            </div>
            {/* <div className='col-4'>
              <Select name = "statusId" label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
            </div> */}
            <div className='col-4'>
              <InputText name="hours" label='Horas' type='number'  placeholder='Ingresa las horas' disabled={ isModeEdit } required value={ hours } onChange={ onChangeHours } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-6'>
            <Select name="leaderId" label="L&iacute;der" disabled={ isModeEdit } options={ catEmployees } value={ leader } required onChange={ onChangeLeader } />
            </div>
            <div className='col-6'>
              <Select name="developerId" label="Desarrollador" disabled={ isModeEdit } options={ catEmployees } value={ developer } required onChange={ onChangeDeveloper } />
            </div>
          </div>

          <div className="row text-start">
            <div className='col-4'>
              <DatePicker name="designDate" label="Analisis y dise&ntilde;o" disabled={ isModeEdit } value={ designDate } required onChange={ (date) => onChangeDesignDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker name="developmentDate" label="Construcci&oacute;n" disabled={ isModeEdit } value={ developmentDate } required onChange={ (date) => onChangeDevelopmentDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker name="endDate" label="Cierre" value={ endDate } disabled={ isModeEdit } required onChange={ (date) => onChangeEndDate(date) } />
            </div>
          </div>
        </div>
        <div className="pt-3 d-flex flex-row-reverse">
            { renderSaveButton() }
            &nbsp;
            <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/${ projectId }/edit`) }>Cancelar</button>
        </div>
      </form>
  )
 
  return (
    <div className='px-5'>
      <div className='d-flex justify-content-between'>
        <h1 className="fs-4 card-title fw-bold mb-4">Aplicacion</h1>
        { renderTabs() }
      </div>
      <Alert />
      { (currentTab === 2) ? (<TableLog history={ projectApplication.history } />) : renderDetail() }
    </div>
  )
}
