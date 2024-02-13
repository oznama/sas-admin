import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { getCatalogChilds } from "../../services/CatalogService";
import { getCompanySelect } from "../../services/CompanyService";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { getEmployeeById, getEmployessByCompanyId, save, update } from "../../services/EmployeeService";
import { displayNotification, genericErrorMsg, numberToString } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
import { parsePhoneNumberFromString } from 'libphonenumber-js'
import { setEmployeeS } from '../../../store/company/companySlice';

export const DetailEmployee = () => {
  const { companyObj } = useSelector( state => state.companyReducer );
  const { companyS } = useSelector( state => state.companyReducer );
  const { permissions} = useSelector( state => state.auth );
  const [currentTab, setCurrentTab] = useState(1);
  const {id} = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const onChangeEmail = ({ target }) => setEmail(target.value);
  const [emailDomain, setEmailDomain] = useState(companyObj ? companyObj.emailDomain : '');
  const onChangeEmailDomain = ({ target }) => setEmailDomain(target.value);
  const [name, setName] = useState('');
  const onChangeName = ({ target }) => setName(target.value);
  const [secondName, setSecondName] = useState('');
  const onChangeSecondName = ({ target }) => setSecondName(target.value);
  const [surname, setSurname] = useState('');
  const onChangeSurname = ({ target }) => setSurname(target.value);
  const [secondSurname, setSecondSurname] = useState('');
  const onChangeSecondSurname = ({ target }) => setSecondSurname(target.value);
  const [phone, setPhone] = useState('');
  const [phoneError, setPhoneError] = useState('');
  
  const [company, setCompany] = useState(companyS ? companyS : '');
  
  const [companyDesc, setCompanyDesc] = useState('');
  const onChangeCompany = ({ target }) => {
    fetchLeaders(target.value);
    setCompany(target.value);
    const companySelected = companies.find( c => { //Cuando selecciona una compañia
      const isFind = c.id === Number(target.value)
      console.log('isFind???', isFind, 'companyId', c.id, 'companySelected', Number(target.value));
      return isFind;
    });
    setEmailDomain( companySelected ? companySelected.emailDomain : '' );
    setCompanyDesc( companySelected ? `> ${companySelected.value}` : '' );
  }

  const [position, setPosition] = useState('');
  const onChangePosition = ({ target }) => setPosition(target.value);
  const [companies, setCompanies] = useState([]);
  const [catEmployees, setCatEmployees] = useState([]);
  const [positions, setPositions] = useState([]);
  const [active, setActive] = useState('');
  const [leader, setLeader] = useState('');
  const [country, setCountry] = useState('');
  const [cellphone, setCellphone] = useState('');
  const [cellphoneError, setCellphoneError] = useState('');
  const [ext, setExt] = useState('');
  const [city, setCity] = useState('');
  const [errorCity, setErrorCity] = useState('');
  const onChangeCountry = ({target}) => {
    setCountry(target.value);
  };
  const onChangeExt = ({target}) => {
    setExt(target.value);
  };
  const onChangeCity = ({target}) => {
    setCity(target.value);
  };
  const onChangeCellphone= ({target}) => {
    const phoneNumber = parsePhoneNumberFromString(target.value, country) // 'MX' es el indicativo de país para México
    setCellphoneError('');
        if (phoneNumber) {
            if (phoneNumber.isPossible() &&  phoneNumber.isValid()) {
              setCellphoneError('')
              setCellphone(target.value)
              phoneNumber.formatNational()
            }else{  
              setCellphone(target.value)
                setCellphoneError("Teléfono invalido.")
            }
        } else {
            setCellphoneError("Teléfono no válido, ingrese lada.");
            setCellphone(target.value)
        }
  };

  const onChangePhone = ({ target }) => {
    const phoneNumber = parsePhoneNumberFromString(target.value, country) // 'MX' es el indicativo de país para México
    setPhoneError('');
        if (phoneNumber) {
            if (phoneNumber.isPossible() &&  phoneNumber.isValid()) {
              setPhoneError('')
              setPhone(target.value)
              phoneNumber.formatNational()
            }else{  
              setPhone(target.value)
                setPhoneError("Teléfono invalido.")
            }
        } else {
            setPhoneError("Teléfono no válido, ingrese lada.");
            setPhone(target.value)
        }
  };

  const countriesList = [
    { id: 'MX', name: 'México' },
    { id: 'US', name: 'Estados Unidos' },
      // Agrega más países según tus necesidades
  ];

  const countries = countriesList.map(country => ({
    value: country.id,
    label: country.name
  }));

  const onChangeLeader = ({ target }) => setLeader(target.value);

  const isModeEdit = ( (id && !permissions.canEditEmp) || (id && !active ));
  console.log('id: '+id+' Permission: '+!permissions.canEditEmp+' Active: '+active);

  const onSubmit = event => {
    event.preventDefault()
    const data = new FormData(event.target)
    const request = Object.fromEntries(data.entries())
	  request['email'] = email +'@'+ emailDomain
    if (id){
      updateEmployee(request);
    }else{
      saveEmployee(request);
    }
  }
  
  const onClickBack = () => {
    if ( (!permissions.canEditEmp && currentTab == 2) || currentTab === 1 ) {
      navigate('/employee')
    } else {
      setCurrentTab(currentTab - 1);
    }
  }

  const renderTabs = () => (//Esto controla los tabs
    <ul className="nav nav-tabs d-flex flex-row-reverse">
      {id && (<li className="nav-item" onClick={ () => setCurrentTab(2) }>
        <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
      </li>)}
      <li className="nav-item" onClick={ () => setCurrentTab(1) }>
        <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
      </li>
      <li>
        <button type="button" className="btn btn-link" onClick={ () => onClickBack() }>&lt;&lt; Regresar</button>
      </li>
    </ul>
  )

  const fetchEmployee = () => {
    //console.log('Aqui carga el usuario seleccionado')
    getEmployeeById(id).then( response => {
        if( response.code ) {
            displayNotification(dispatch, response.message, alertType.error);
        } else {
            setEmail((response.email+'').substring(0, (response.email+'').indexOf('@')));
            setEmailDomain((response.email+'').substring((response.email+'').indexOf('@')+1, (response.email+'').length));
            setName(response.name);
            setSecondName(response.secondName);
            setSurname(response.surname);
            setSecondSurname(response.secondSurname);
            setPhone(response.phone);
            if( response.companyId ) {
              fetchLeaders(response.companyId);
            }
            setCompany(numberToString(response.companyId, ''));
            setPosition(numberToString(response.positionId, ''));
            setLeader(numberToString(response.bossId, ''));
            setActive(response.active);
            setCountry(response.country);
            setCellphone(response.cellPhone);
            setExt(response.Ext);
            setCity(response.city);
            dispatch(setEmployeeS(response));
        }
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  };

  const saveEmployee = request => {
    //console.log('Aqui guarda el usuario')
    save(request).then( response => {
        if(response.code && response.code === 401) {
            displayNotification(dispatch, response.message, alertType.error);
        } else if (response.code && response.code !== 201) {
            if( response.message ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else if (response.errors) {
                console.log(response.errors);
                displayNotification(dispatch, 'No se ha podido crear el empleado', alertType.error);
            }
        } else {
            displayNotification(dispatch, '¡Empleado creado correctamente!', alertType.success);
            navigate('/employee', { replace: true });
        }
    }).catch(error => {
        console.log(error)
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  };
  
  const updateEmployee = request => {
    //console.log('Aqui actualiza el usuario seleccionado')
    update(id, request).then( response => {
      console.log(response);
        if(response.code && response.code === 401) {
          displayNotification(dispatch, response.message, alertType.error);
        } else if (response.code && response.code !== 200) {
            if( response.message ) {
              displayNotification(dispatch, response.message, alertType.error);
            } else if (response.errors) {
                console.log(response.errors);
                displayNotification(dispatch, 'No se ha podido actualizar el empleado', alertType.error);
            }
        } else {
            displayNotification(dispatch, '¡Empleado actualizado correctamente!', alertType.success);
            navigate('/employee', { replace: true });
        }
    }).catch(error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
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
      setCatEmployees(response);console.log('Empleados sin ordenar:', JSON.stringify(response, null, 2));
      // const sortedEmployees = response.sort((a, b) => a.name.localeCompare(b.name));console.log('Empleados ordenados:', JSON.stringify(response, null, 2));
      // setCatEmployees(sortedEmployees);
    }).catch( error => {
      console.log(error);
    });
  }

  useEffect(() => {
    //console.log('Aqui pasa cuando agrega un usuario')
    fetchSelects()
    if(id){
      fetchEmployee();
    }
    if (companyObj && companyObj.id) {
      fetchLeaders(companyObj.id);
    }
  }, [companyObj])
  
  const renderDetail = () => {
    return (<div className='d-grid gap-2 col-6 mx-auto'>
              <form className="needs-validation" onSubmit={ onSubmit }>
                <div className="row text-start">
                  <div className='col-4'>
                    <Select name="companyId" label="Empresa" options={ companies } disabled={ isModeEdit } value={ company } required onChange={ onChangeCompany } />
                  </div>
                  <div className='col-4'>
                    <InputText name='email' label='Correo' placeholder='Ingresa correo' disabled={ isModeEdit } value={ email } required onChange={ onChangeEmail } maxLength={ 50 } />
                  </div>
                  <div className='col-4'>
                    <InputText name='emaildomain' label='‎ ' readOnly disabled={ true } value={ emailDomain } onChange= {onChangeEmailDomain} />
                  </div>
                </div>
                <div className="row text-start">
                <div className='col-6'>
                    <InputText name='name' label='Nombre' placeholder='Escribe el nombre' disabled={ isModeEdit } value={ name } required onChange={ onChangeName } maxLength={ 50 } />
                  </div>
                  <div className='col-6'>
                    <InputText name='secondName' label='Segundo Nombre' placeholder='Escribe segundo nombre' disabled={ isModeEdit } value={ secondName } onChange={ onChangeSecondName } maxLength={ 50 } />
                  </div>
                </div>
                <div className="row text-start">
                  <div className='col-6'>
                    <InputText name='surname' label='Apellido paterno' placeholder='Escribe apellido paterno' disabled={ isModeEdit } value={ surname } required onChange={ onChangeSurname } maxLength={ 50 } />
                  </div>
                  <div className='col-6'>
                    <InputText name='secondSurname' label='Segundo Apellido' placeholder='Escribe apellido materno' disabled={ isModeEdit } value={ secondSurname } onChange={ onChangeSecondSurname } maxLength={ 50 } />
                  </div>
                </div>
                <div className="row text-start">
                  <div className='col-6'>
                      <InputText name='city' label='Ciudad:' placeholder='Escribe la ciudad' disabled={ isModeEdit } value={ city } onChange={ onChangeCity } maxLength={ 50 } error={errorCity}/>
                  </div>
                  <div className='col-6'>
                    <Select name="country" label="País:" options={countries} disabled={isModeEdit} value={country} onChange={onChangeCountry} />
                  </div>
                </div>
                <div className="row text-start">
                  <div className='col-4'>
                    <InputText name='phone' label='Telefono' placeholder='Escribe telefono' disabled={ isModeEdit } value={ phone } onChange={ onChangePhone } maxLength={ 13 } error={phoneError}/>
                  </div>
                  <div className='col-4'>
                    <InputText name='ext' label='Extenci&oacute;n' placeholder='Escribe extencion' disabled={ isModeEdit } value={ ext } onChange={ onChangeExt } maxLength={ 5 } />
                  </div>
                  <div className='col-4'>
                    <InputText name='cellphone' label='Celular' placeholder='Escribe telefono celular' disabled={ isModeEdit } value={ cellphone } onChange={ onChangeCellphone } maxLength={ 13 } error={cellphoneError} />
                  </div>
                </div>
                <div className="row text-start">
                  <div className='col-6'>
                    <Select name="positionId" label="Puesto" options={ positions } disabled={ isModeEdit } value={ position } onChange={ onChangePosition } />
                  </div>
                  <div className='col-6'>
                  <Select name="bossId" label="L&iacute;der" options={ catEmployees } disabled={ isModeEdit } value={ leader } onChange={ onChangeLeader } />
                  </div>
                </div>
                  <div className="pt-3 d-flex flex-row-reverse">
                      {permissions.canEditEmp && (<button type="submit" className="btn btn-primary" >Guardar</button>)}
                      &nbsp;
                      <button type="button" className="btn btn-danger" onClick={ () => navigate(`/employee`) }>{permissions.canEditEmp ? 'Cancelar' : 'Regresar'}</button>
                  </div>
              </form>
          </div>)
  };

  return (
    <>
      <div className="d-flex d-flex justify-content-center">
        <h3 className="fs-4 card-title fw-bold mb-4">{`Empleado ${companyDesc}${name ? ' > Detalles de ' + name + ' ' + surname : ''}`}</h3>
      </div>
      {renderTabs() }
      { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Employee' recordId={ id } />) }
    </>
  )
}
