import { useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getCatalog } from '../../../services/CatalogService';
import { getUsersByRole } from '../../../services/UserService';
import { checkResponse } from '../../../helpers/handleResponse';
import { saveApplication } from '../../../services/ProjectService';

export const DetailApplications = () => {
  const navigate = useNavigate();
  const { projectId, id } = useParams();
  const [aplication, setAplication] = useState('-1');
  const [amount, setAmount] = useState('$0');
  const [status, setStatus] = useState('2000900001');
  const [leader, setLeader] = useState('-1');
  const [developer, setDeveloper] = useState('-1');
  const [hours, setHours] = useState('0');
  const [endDate, setEndDate] = useState();
  const [designerDate, setDesignerDate] = useState(new Date());
  const [buildDate, setBuildDate] = useState(new Date());
  const [catStatus, setCatStatus] = useState([]);
  const [catAplications, setCatApliations] = useState([]);
  const [catEmployees, setCatEmployees] = useState([]);

  const fetchSelects = () => {
    
    getCatalog(1000000008)
      .then( response => {
        checkResponse(response);
        setCatApliations(response);
      }).catch( error => {
        console.log(error);
      });

    getCatalog(1000000009)
      .then( response => {
        checkResponse(response);
        setCatStatus(response);
      }).catch( error => {
        console.log(error);
      });
    
    getUsersByRole(2)
      .then( response => {
        checkResponse(response);
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
  const onChangeDesignerDate = (date) => setDesignerDate(date);
  const onChangeBuildDate = (date) => setBuildDate(date);

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    request.projectId = projectId;
    request.id = id != undefined ? id : null;
    
    saveApplication(request).then( response => {
        if(response.code && response.code === 401) {
            onLogout();
        } else if (response.code && response.code !== 201) {
            if( response.message ) {
                setMsgError(response.message)
            } else if (response.errors) {
                setErros(response.errors);
            }
        } else {
            navigate(`/project/${projectId}/edit`, { replace: true });
        }
    }).catch(error => {
        setMsgError(error.message);
    });
  }

  return (
    <div className='px-5'>
      <h1 className="fs-4 card-title fw-bold mb-4">Aplicacion</h1>
      <form onSubmit={ onSubmit }>
        <div className='text-center'>
          <div className="row text-start">
            <div className='col-4'>
              <Select name="applicationId" label="Aplicaci&oacute;n" options={ catAplications } value={ aplication } onChange={ onChangeAplication } />
            </div>
            <div className='col-4'>
              <InputText name="amount" label='Monto' placeholder='Ingresa monto' value={ amount } onChange={ onChangeAmount } maxLength={ 10 } />
            </div>
            <div className='col-4'>
              <Select name = "statusId" label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-4'>
            <Select name="leaderId" label="L&iacute;der" options={ catEmployees } value={ leader } onChange={ onChangeLeader } />
            </div>
            <div className='col-4'>
              <Select name="developerId" label="Desarrollador" options={ catEmployees } value={ developer } onChange={ onChangeDeveloper } />
            </div>
            <div className='col-4'>
              <InputText name="hours" label='Horas' placeholder='Ingresa monto' value={ hours } onChange={ onChangeHours } maxLength={ 10 } />
            </div>
          </div>

          <div className="row text-start">
            <div className='col-4'>
              <DatePicker name="designDate" label="Analisis y dise&ntilde;o" value={ designerDate } onChange={ (date) => onChangeDesignerDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker name="developmentDate" label="Construcci&oacute;n" value={ buildDate } onChange={ (date) => onChangeBuildDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker name="endDate" label="Cierre" value={ endDate } onChange={ (date) => onChangeEndDate(date) } />
            </div>
          </div>
        </div>
        <div className="pt-3 d-flex flex-row-reverse">
            <button type="submit" className="btn btn-primary">Guardar</button>
            &nbsp;
            <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/${ projectId }/edit`) }>Cancelar</button>
        </div>
      </form>
    </div>
  )
}
