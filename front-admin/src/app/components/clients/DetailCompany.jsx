import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { getCatalogChilds } from "../../services/CatalogService";
import { getCompanyById, save, update } from "../../services/CompanyService";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { displayNotification, genericErrorMsg, numberToString } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";

export const DetailCompany = () => {

    const { permissions, user } = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const {id} = useParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [rfc, setRfc] = useState('');
    const onChangeRfc = ({ target }) => setRfc(target.value);
    const [emailDomain, setEmailDomain] = useState('');
    const onChangeEmailDomain = ({ target }) => setEmailDomain(target.value);
    const [name, setName] = useState('');
    const onChangeName = ({ target }) => setName(target.value);
    const [address, setAddress] = useState('');
    const onChangeAddress = ({ target }) => setAddress(target.value);
    const [phone, setPhone] = useState('');
    const onChangePhone = ({ target }) => setPhone(target.value);
    const [type, setType] = useState('');
    const onChangeType = ({ target }) => setType(target.value);
    const [types, setTypes] = useState([]);

    const isModeEdit = ( id && !permissions.canEditComp );

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
    console.log('Aqui carga la compañia seleccionada')
    getCompanyById(id).then( response => {
        if( response.code ) {
            displayNotification(dispatch, response.message, alertType.error);
        } else {
            setRfc(response.rfc);
            setName(response.name);
            setEmailDomain(response.emailDomain);
            setAddress(response.address);
            setPhone(response.phone);
            setType(response.type);
        }
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
    };

    const saveEmployee = request => {
    console.log('Aqui guarda la compañia')
    save(request).then( response => {
        if(response.code && response.code === 401) {
            displayNotification(dispatch, response.message, alertType.error);
        } else if (response.code && response.code !== 201) {
            if( response.message ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else if (response.errors) {
                console.log(response.errors);
                displayNotification(dispatch, 'No se ha podido crear la compañia', alertType.error);
            }
        } else {
            displayNotification(dispatch, '¡Compañia creado correctamente!', alertType.success);
            navigate('/company', { replace: true });
        }
    }).catch(error => {
        console.log(error)
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
    };
    
    const updateEmployee = request => {
    console.log('Aqui actualiza la compañia seleccionada')
    update(id, request).then( response => {
        console.log(response);
        if(response.code && response.code === 401) {
            displayNotification(dispatch, response.message, alertType.error);
        } else if (response.code && response.code !== 200) {
            if( response.message ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else if (response.errors) {
                console.log(response.errors);
                displayNotification(dispatch, 'No se ha podido actualizar la compañia', alertType.error);
            }
        } else {
            displayNotification(dispatch, '¡Compañia actualizado correctamente!', alertType.success);
            navigate('/company', { replace: true });
        }
    }).catch(error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
    };
    
    const fetchSelects = () => {
    getCatalogChilds(1000000009).then( response => {
        setTypes(response.filter( cat => cat.status === 2000100001 ));
    }).catch( error => {
        console.log(error);
    });
    
    };

    useEffect(() => {
    //console.log('Aqui pasa cuando agrega un usuario')
    fetchSelects()
    if(id){
        fetchEmployee();
    }
    }, [])

    const renderDetail = () => {
    return (<div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                <div className="row text-start">
                    <div className='col-6'>
                        <InputText name='rfc' label='RFC' placeholder='Ingresa RFC' disabled={ isModeEdit } value={ rfc } required onChange={ onChangeRfc } maxLength={ 12 } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-6'>
                        <InputText name='name' label='Nombre' placeholder='Escribe el nombre' disabled={ isModeEdit } value={ name } required onChange={ onChangeName } maxLength={ 50 } />
                    </div>
                    <div className='col-6'>
                        <InputText name='emailDomain' label='Dominio de correo' placeholder='Escribe el dominio de correo' disabled={ isModeEdit } value={ emailDomain } onChange={ onChangeEmailDomain } maxLength={ 50 } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-6'>
                        <InputText name='address' label='Direcci&oacute;n' placeholder='Escribe la direcci&oacute;n' disabled={ isModeEdit } value={ address } onChange={ onChangeAddress } maxLength={ 50 } />
                    </div>
                    <div className='col-6'>
                        <InputText name='phone' label='Telefono' placeholder='Escribe telefono' disabled={ isModeEdit } value={ phone } onChange={ onChangePhone } maxLength={ 13 } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-12'>
                        <Select name="type" label="Clasificaci&oacute;n" options={ types } disabled={ isModeEdit } value={ type } onChange={ onChangeType } />
                    </div>
                </div>
                    <div className="pt-3 d-flex flex-row-reverse">
                        {permissions.canEditEmp && (<button type="submit" className="btn btn-primary" >Guardar</button>)}
                        &nbsp;
                        <button type="button" className="btn btn-danger" onClick={ () => navigate(`/company`) }>{permissions.canEditEmp ? 'Cancelar' : 'Regresar'}</button>
                    </div>
                </form>
            </div>)
    };

    return (
    <>
        <div className="d-flex d-flex justify-content-center">
            <h3 className="fs-4 card-title fw-bold mb-4">{`Compañia ${name ? ' > Detalles de ' + name  : ''}`}</h3>
        </div>
        { id && renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Employee' recordId={ id } />) }
    </>
    )
}
