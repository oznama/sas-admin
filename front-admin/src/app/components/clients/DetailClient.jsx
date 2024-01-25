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

export const DetailClient = () => {

    const { permissions, user } = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const {id} = useParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const onChangeEmail = ({ target }) => setEmail(target.value);
    const [emailDomain, setEmailDomain] = useState('');
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
    const onChangePhone = ({ target }) => setPhone(target.value);
    const [company, setCompany] = useState( permissions.isAdminRoot ? '' : user.companyId);
    
    const [companyDesc, setCompanyDesc] = useState(!permissions.isAdminRoot ? user.company : '');
    const onChangeCompany = ({ target }) => {
    if (permissions.isAdminRoot) {
        fetchLeaders(target.value);
    }
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
    const [leader, setLeader] = useState('');
    const onChangeLeader = ({ target }) => setLeader(target.value);

    const isModeEdit = ( id && !permissions.canEditEmp );

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
    const renderTabs = () => (//Esto controla los tabs
    <ul className="nav nav-tabs">
        <li className="nav-item" onClick={ () => setCurrentTab(1) }>
            <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
        </li>
        <li className="nav-item" onClick={ () => setCurrentTab(2) }>
            <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
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
            setEmailDomain((response.email+'').substring((response.email+'').indexOf('@'), (response.email+'').length));
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
        setCatEmployees(response);
    }).catch( error => {
        console.log(error);
    });
    }

    useEffect(() => {
    //console.log('Aqui pasa cuando agrega un usuario')
    fetchSelects()
    if (!permissions.isAdminRoot) { //aqui coloca el valor de lideres
        fetchLeaders(user.companyId);
        setEmailDomain(user.emailDomain);
    }
    if(id){
        fetchEmployee();
    }
    }, [])

    const renderDetail = () => {
    return (<div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                { permissions.isAdminRoot ? (<div className="row text-start">
                    <div className='col-6'>
                        <Select name="companyId" label="Empresa" options={ companies } disabled={ isModeEdit } value={ company } required onChange={ onChangeCompany } />
                    </div>
            </div>) : (<div className="row text-start" hidden>
                    <div className='col-6'>
                        <Select name="companyId" label="Empresa" readOnly options={ companies } disabled={ isModeEdit } value={ company } required onChange={ onChangeCompany } />
                    </div>
            </div>)}
                <div className="row text-start">
                    <div className='col-6'>
                        <InputText name='email' label='Correo' placeholder='Ingresa correo' disabled={ isModeEdit } value={ email } required onChange={ onChangeEmail } maxLength={ 50 } />
                    </div>
                    <div className='col-6'>
                        <InputText name='emaildomain' label='‎ ' readOnly disabled={ isModeEdit } value={ emailDomain } onChange= {onChangeEmailDomain} />
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
                        <InputText name='phone' label='Telefono' placeholder='Escribe telefono' disabled={ isModeEdit } value={ phone } onChange={ onChangePhone } maxLength={ 13 } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-6'>
                        <Select name="bossId" label="L&iacute;der" options={ catEmployees } disabled={ isModeEdit } value={ leader } onChange={ onChangeLeader } />
                    </div>
                    <div className='col-6'>
                        <Select name="positionId" label="Puesto" options={ positions } disabled={ isModeEdit } value={ position } onChange={ onChangePosition } />
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
        { id && renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Employee' recordId={ id } />) }
    </>
    )
}
