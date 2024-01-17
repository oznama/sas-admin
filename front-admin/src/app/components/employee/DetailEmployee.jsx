import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { getCatalogChilds } from "../../services/CatalogService";
import { getCompanySelect } from "../../services/CompanyService";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { getEmployeeById, getEmployess, save, update } from "../../services/EmployeeService";
import { setMessage } from "../../../store/alert/alertSlice";
import { buildPayloadMessage, numberToString } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";

export const DetailEmployee = () => {

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
  const [company, setCompany] = useState('');
  const onChangeCompany = ({ target }) => setCompany(target.value);
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
            setEmail(response.email);
            setName(response.name);
            setSecondName(response.secondName);
            setSurname(response.surname);
            setSecondSurname(response.secondSurname);
            setPhone(response.phone);
            console.log("Response Company ", response.companyId, numberToString(response.companyId, ''))
            setCompany(numberToString(response.companyId, ''));
            setPosition(numberToString(response.positionId, ''));
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
        }
    }).catch(error => {
        dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar el empleado, contacte al administrador', alertType.error)));
    });
};

  
  const fetchSelects = () => {
    
    getCatalogChilds(1000000005)
      .then( response => {
        setPositions(response.filter( cat => cat.status === 2000100001 ));
      }).catch( error => {
        console.log(error);
      });
    
    getCompanySelect()
      .then( response => {
        setCompanies(response);
      }).catch( error => {
        console.log(error);
      });

    getEmployess(true)
      .then( response => {
        setCatEmployees(response);
      }).catch( error => {
        console.log(error);
      });
  };

console.log(id);

  useEffect(() => {
    fetchSelects()
    if(id){
      fetchEmployee();
    }
  }, [])
  

  return (
      <div className='d-grid gap-2 col-6 mx-auto'>
          <form className="needs-validation" onSubmit={ onSubmit }>
              
              <InputText name='email' label='Correo' placeholder='Ingresa correo' 
                  value={ email } required onChange={ onChangeEmail } maxLength={ 50 } />
              <InputText name='name' label='Nombre' placeholder='Ingresa Nombre' 
                  value={ name } required onChange={ onChangeName } maxLength={ 50 } />
              <InputText name='secondName' label='Segundo Nombre' placeholder='Ingresa segundo nombre' 
                  value={ secondName } onChange={ onChangeSecondName } maxLength={ 50 } />
              <InputText name='surname' label='Apellido' placeholder='Ingresa primer apellido' 
                  value={ surname } required onChange={ onChangeSurname } maxLength={ 50 } />
              <InputText name='secondSurname' label='Segundo Apellido' placeholder='Ingresa segundo apellido' 
                  value={ secondSurname } onChange={ onChangeSecondSurname } maxLength={ 50 } />
              <InputText name='phone' label='Telefono' placeholder='Ingresa telefono' 
                  value={ phone } onChange={ onChangePhone } maxLength={ 13 } />
              
              <Select name="companyId" label="Empresa" options={ companies } value={ company } required onChange={ onChangeCompany } />
              <Select name="leaderId" label="L&iacute;der" options={ catEmployees } value={ leader } required onChange={ onChangeLeader } />
              <Select name="positionId" label="Puesto" options={ positions } value={ position } required onChange={ onChangePosition } />

              <div className="pt-3 d-flex flex-row-reverse">
                  <button type="submit" className="btn btn-primary" >Guardar</button>
                  &nbsp;
                  <button type="button" className="btn btn-danger" onClick={ () => navigate(`/employee`) }>Cancelar</button>
              </div>
          </form>
      </div>
  )
}
