import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { getCatalogChilds } from "../../services/CatalogService";
import { getCompanySelect } from "../../services/CompanyService";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { getEmployeeById, getEmployess, getEmployessByCompanyId, save, update } from "../../services/EmployeeService";
import { setMessage } from "../../../store/alert/alertSlice";
import { buildPayloadMessage, numberToString } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";

export const DetailEmployee = () => {

  const { permissions, user } = useSelector( state => state.auth );
  const {id} = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const onChangeEmail = ({ target }) => setEmail(target.value);
  const [name, setName] = useState('');
  const onChangeName = ({ target }) => setName(target.value);
  const [secondName, setSecondName] = useState('');
  const onChangeSecondName = ({ target }) => setSecondName(target.value);
  const [surname, setSurname] = useState('');
  const onChangeSurname = ({ target }) => setSurname(target.value);
  const [secondSurname, setSecondSurname] = useState('');
  const onChangeSecondSurname = ({ target }) => setSecondSurname(target.value);
  const [phone, setPhone] = useState('');
  const onChangePhone = ({ target }) => setPhone(target.value);
  const [company, setCompany] = useState( permissions.isAdminRoot ? '' : user.companyId);
  
  const [companyDesc, setCompanyDesc] = useState(!permissions.isAdminRoot ? user.company : '');
  const onChangeCompany = ({ target }) => {
    fetchLeaders(target.value);
    setCompany(target.value);
    const companySelected = companies.find( c => {
      const isFind = c.id === Number(target.value)
      console.log('isFind???', isFind, 'companyId', c.id, 'companySelected', Number(target.value));
      return isFind;
    });
    setCompanyDesc( companySelected ? `> ${companySelected.value}` : '' );
  }

  const [position, setPosition] = useState('');
  const onChangePosition = ({ target }) => setPosition(target.value);
  const [companies, setCompanies] = useState([]);
  const [catEmployees, setCatEmployees] = useState([]);
  const [positions, setPositions] = useState([]);
  const [leader, setLeader] = useState('');
  const onChangeLeader = ({ target }) => setLeader(target.value);

  const onSubmit = event => {
    event.preventDefault()
    const data = new FormData(event.target)
    const request = Object.fromEntries(data.entries())
	  request['email'] = email + (user.companyDomain ? user.companyDomain : '@sas-mexico.com')
    if (id){
      updateEmployee(request);
    }else{
      saveEmployee(request);
    }
  }
  const fetchEmployee = () => {
    getEmployeeById(id).then( response => {
        if( response.code ) {
            dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
        } else {
            setEmail((response.email+'').substring(0, (response.email+'').indexOf('@')));
            setName(response.name);
            setSecondName(response.secondName);
            setSurname(response.surname);
            setSecondSurname(response.secondSurname);
            setPhone(response.phone);
            if( response.companyId ) {
              fetchLeaders(response.companyId);
            }
            setCompany(numberToString(user.companyId, ''));
            setPosition(numberToString(response.positionId, ''));
            setLeader(numberToString(response.bossId, ''));
        }
    }).catch( error => {
        console.log(error);
        dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar el empleado, contacte al administrador', alertType.error)));
    });
  };

  const saveEmployee = request => {
    save(request).then( response => {
        if(response.code && response.code === 401) {
            dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
        } else if (response.code && response.code !== 201) {
            if( response.message ) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else if (response.errors) {
                console.log(response.errors);
                dispatch(setMessage(buildPayloadMessage('No se ha podido crear el empleado', alertType.error)));
            }
        } else {
            dispatch(setMessage(buildPayloadMessage('¡Empleado creado correctamente!', alertType.success)));
            navigate('/employee', { replace: true });
        }
    }).catch(error => {
        dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear el empleado, contacte al administrador', alertType.error)));
    });
  };
  
  const updateEmployee = request => {
    update(id, request).then( response => {
      console.log(response);
        if(response.code && response.code === 401) {
            dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
        } else if (response.code && response.code !== 200) {
            if( response.message ) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else if (response.errors) {
                console.log(response.errors);
                dispatch(setMessage(buildPayloadMessage('No se ha podido actualizar el empleado', alertType.error)));
            }
        } else {
            dispatch(setMessage(buildPayloadMessage('¡Empleado actualizado correctamente!', alertType.success)));
            navigate('/employee', { replace: true });
        }
    }).catch(error => {
        dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar el empleado, contacte al administrador', alertType.error)));
    });
  };
  
  const fetchSelects = () => {
    getCatalogChilds(1000000005).then( response => {
      setPositions(response.filter( cat => cat.status === 2000100001 ));
    }).catch( error => {
      console.log(error);
    });
    
    getCompanySelect().then( response => {
      setCompanies(response);
    }).catch( error => {
      console.log(error);
    });
  };

  const fetchLeaders = companyId => {
    getEmployessByCompanyId(companyId).then( response => {
      setCatEmployees(response);
    }).catch( error => {
      console.log(error);
    });
  }

  useEffect(() => {
    fetchSelects()
    if(id){
      fetchEmployee();
    }
  }, [])
  

  return (
      <div className='d-grid gap-2 col-6 mx-auto'>
          <form className="needs-validation" onSubmit={ onSubmit }>
          <div className="d-flex d-flex justify-content-center">
                <h3 className="fs-4 card-title fw-bold mb-4">{`Empleado ${companyDesc}${name ? ' > Detalles de ' + name + ' ' + surname : ''}`}</h3>
            </div>
            { permissions.isAdminRoot ? (<div className="row text-start">
              <div className='col-6'>
                <Select name="companyId" label="Empresa" options={ companies } value={ company } required onChange={ onChangeCompany } />
              </div>
            </div>) : (<div className="row text-start" hidden>
              <div className='col-6'>
                <Select name="companyId" label="Empresa" readOnly options={ companies } value={ company } required onChange={ onChangeCompany } />
              </div>
            </div>)}
            <div className="row text-start">
              <div className='col-6'>
                <InputText name='email' label='Correo' placeholder='Ingresa correo' value={ email } required onChange={ onChangeEmail } maxLength={ 50 } />
              </div>
              <div className='col-6'>
                <InputText name='emaildomain' label='‎ ' readOnly value={ user.companyDomain ? user.companyDomain : '@sas-mexico.com'} />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-6'>
                <InputText name='name' label='Nombre' placeholder='Escribe el nombre' value={ name } required onChange={ onChangeName } maxLength={ 50 } />
              </div>
              <div className='col-6'>
                <InputText name='secondName' label='Segundo Nombre' placeholder='Escribe segundo nombre' value={ secondName } onChange={ onChangeSecondName } maxLength={ 50 } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-6'>
                <InputText name='surname' label='Apellido paterno' placeholder='Escribe apellido paterno' value={ surname } required onChange={ onChangeSurname } maxLength={ 50 } />
              </div>
              <div className='col-6'>
                <InputText name='secondSurname' label='Segundo Apellido' placeholder='Escribe apellido materno' value={ secondSurname } onChange={ onChangeSecondSurname } maxLength={ 50 } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-6'>
                <InputText name='phone' label='Telefono' placeholder='Escribe telefono' value={ phone } onChange={ onChangePhone } maxLength={ 13 } />
              </div>
            </div>
            <div className="row text-start">
              <div className='col-6'>
                <Select name="bossId" label="L&iacute;der" options={ catEmployees } value={ leader } onChange={ onChangeLeader } />
              </div>
              <div className='col-6'>
                <Select name="positionId" label="Puesto" options={ positions } value={ position } onChange={ onChangePosition } />
              </div>
            </div>

              <div className="pt-3 d-flex flex-row-reverse">
                  <button type="submit" className="btn btn-primary" >Guardar</button>
                  &nbsp;
                  <button type="button" className="btn btn-danger" onClick={ () => navigate(`/employee`) }>Cancelar</button>
              </div>
          </form>
      </div>
  )
}
