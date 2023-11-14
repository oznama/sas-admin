import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getCatalog } from '../../../services/CatalogService';
import { getUsersByRole } from '../../../services/UserService';
import { saveApplication } from '../../../services/ProjectService';
import { handleDateStr, numberToString } from '../../../helpers/utils';
import { useSelector } from 'react-redux';

export const DetailApplications = () => {

  const projectApplication = useSelector( (state) => state.projectReducer.projectApplication );
  const { projectId } = useParams();

  const navigate = useNavigate();
  const [aplication, setAplication] = useState(numberToString(projectApplication.applicationId, '-1'));
  const [amount, setAmount] = useState(numberToString(projectApplication.amount, '$0'));
  // const [status, setStatus] = useState('2000900001');
  const [leader, setLeader] = useState(numberToString(projectApplication.leaderId, '-1'));
  const [developer, setDeveloper] = useState(numberToString(projectApplication.developerId, '-1'));
  const [hours, setHours] = useState(numberToString(projectApplication.hours, '0'));
  const [endDate, setEndDate] = useState(handleDateStr(projectApplication.endDate, false));
  const [designDate, setdesignDate] = useState(handleDateStr(projectApplication.designDate, true));
  const [developmentDate, setDevelopmentDate] = useState(handleDateStr(projectApplication.developmentDate, true));
  // const [catStatus, setCatStatus] = useState([]);
  const [catAplications, setCatApliations] = useState([]);
  const [catEmployees, setCatEmployees] = useState([]);

  const isModeEdit = () => ( projectApplication.id && projectApplication.id > 0);

  const fetchSelects = () => {
    
    getCatalog(1000000008)
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
    
    getUsersByRole(2)
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
  const onChangeAmount = ({ target }) => setAmount(target.value);
  const onChangeStatus = ({target }) => setStatus(target.value);
  const onChangeLeader = ({ target }) => setLeader(target.value);
  const onChangeDeveloper = ({ target }) => setDeveloper(target.value);
  const onChangeHours = ({ target }) => setHours(target.value);
  const onChangeEndDate = (date) => setEndDate(date);
  const onChangeDesignDate = (date) => designDate(date);
  const onChangeDevelopmentDate = (date) => setDevelopmentDate(date);

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    request.projectId = projectApplication.projectId;
    // request.id = id != undefined ? id : null;
    
    saveApplication(request).then( response => {
        if(response.code && response.code === 401) {
          setMsgError(response.message);
        } else if (response.code && response.code !== 201) {
          if( response.message ) {
              setMsgError(response.message)
          } else if (response.errors) {
              setErros(response.errors);
          }
        } else {
          navigate(`/project/${projectApplication.projectId}/edit`, { replace: true });
        }
    }).catch(error => {
        setMsgError(error.message);
    });
  }

  const renderSaveButton = () => {
    if(isModeEdit) {
      return null;
    } else {
      return (<button type="submit" className="btn btn-primary">Guardar</button>) ;
    }
  };
 
  return (
    <div className='px-5'>
      <h1 className="fs-4 card-title fw-bold mb-4">Aplicacion</h1>
      <form onSubmit={ onSubmit }>
        <div className='text-center'>
          <div className="row text-start">
            <div className='col-4'>
              <Select name="applicationId" label="Aplicaci&oacute;n" disabled={ isModeEdit() } options={ catAplications } value={ aplication } onChange={ onChangeAplication } />
            </div>
            <div className='col-4'>
              <InputText name="amount" label='Monto' placeholder='Ingresa monto' disabled={ isModeEdit() } value={ amount } onChange={ onChangeAmount } maxLength={ 10 } />
            </div>
            {/* <div className='col-4'>
              <Select name = "statusId" label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
            </div> */}
            <div className='col-4'>
              <InputText name="hours" label='Horas' placeholder='Ingresa monto' disabled={ isModeEdit() } value={ hours } onChange={ onChangeHours } maxLength={ 10 } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-6'>
            <Select name="leaderId" label="L&iacute;der" disabled={ isModeEdit() } options={ catEmployees } value={ leader } onChange={ onChangeLeader } />
            </div>
            <div className='col-6'>
              <Select name="developerId" label="Desarrollador" disabled={ isModeEdit() } options={ catEmployees } value={ developer } onChange={ onChangeDeveloper } />
            </div>
          </div>

          <div className="row text-start">
            <div className='col-4'>
              <DatePicker name="designDate" label="Analisis y dise&ntilde;o" disabled={ isModeEdit() } value={ designDate } onChange={ (date) => onChangeDesignDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker name="developmentDate" label="Construcci&oacute;n" disabled={ isModeEdit() } value={ developmentDate } onChange={ (date) => onChangeDevelopmentDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker name="endDate" label="Cierre" value={ endDate } disabled={ isModeEdit() } onChange={ (date) => onChangeEndDate(date) } />
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
}
