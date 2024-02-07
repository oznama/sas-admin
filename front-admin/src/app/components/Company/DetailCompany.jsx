import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { getCatalogChilds } from "../../services/CatalogService";
import { getCompanyById, save, update } from "../../services/CompanyService";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
import { parsePhoneNumberFromString } from 'libphonenumber-js'

export const DetailCompany = () => {

    const { permissions } = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const {id} = useParams();
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [rfc, setRfc] = useState('');
    const [errorRfc, setErrorRfc] = useState();
    const rfcPattern = /^[A-Z]{3}\d{6}[A-Z0-9]{3}$/;
    const onChangeRfc = ({ target }) => {
        setErrorRfc(null);
        if (!rfcPattern.test(target.value)) {
            setErrorRfc('RFC inválido. Debe tener el formato XXX000000XXX');
        }
        setRfc(target.value);
    }
    const [emailDomain, setEmailDomain] = useState('');
    const [errorEmailDomain, setErrorEmailDomain] = useState();
    const emailDomainPattern = /^[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\.[a-zA-Z]{2,}$/;
    const onChangeEmailDomain = ({ target }) => {
        setErrorEmailDomain(null);
        if (!emailDomainPattern.test(target.value)) {
            setErrorEmailDomain('Email inválido.');
        }
        setEmailDomain(target.value);
    }
    const [name, setName] = useState('');
    const [errorName, setErrorName] = useState('');
    const namePattern = /^[a-zA-ZÀ-ÿ\s]{2,100}$/;
    const onChangeName = ({ target }) => {
        setErrorName(null);
        if (!namePattern.test(target.value)) {
            setErrorName('Razón Social de la empresa inválido.');
        }
        setName(target.value);
    }
    const [alias, setAlias] = useState('');
    const [errorAlias, setErrorAlias] = useState('');
    const aliasPattern = /^[a-zA-ZÀ-ÿ\s]{2,100}$/;
    const onChangeAlias = ({ target }) => {
        setErrorAlias(null);
        if (!aliasPattern.test(target.value)) {
            setErrorAlias('Alias de la empresa inválido.');
        }
        setAlias(target.value);
    }
    const [address, setAddress] = useState('');
    const onChangeAddress = ({ target }) => setAddress(target.value);
    const [cp, setCp] = useState('');
    const [errorCp, setErrorCp] = useState('');
    const cpPattern = /^[0-9]{4,7}$/;
    const onChangeCp = ({ target }) => {
        setErrorCp(null);
        if (!cpPattern.test(target.value)) {
            setErrorCp('Codigo postal de la empresa inválido.');
        }
        setCp(target.value);
    }
    const [city, setCity] = useState('');
    const [errorCity, setErrorCity] = useState('');
    const cityPattern = /^[a-zA-ZÀ-ÿ\s]{2,100}$/;
    const onChangeCity = ({ target }) => {
        setErrorCity(null);
        if (!cityPattern.test(target.value)) {
            setErrorCity('Ciudad de la empresa inválida.');
        }
        setCity(target.value);
    }
    const [state, setState] = useState('');
    const [errorState, setErrorState] = useState('');
    const statePattern = /^[a-zA-ZÀ-ÿ\s]{2,100}$/;
    const onChangeState = ({ target }) => {
        setErrorState(null);
        if (!statePattern.test(target.value)) {
            setErrorState('Estado de la empresa inválido.');
        }
        setState(target.value);
    }
    const [country, setCountry] = useState('');
    const [errorCountry, setErrorCountry] = useState('');
    const countryPattern = /^[a-zA-ZÀ-ÿ\s]{2,100}$/;
    const onChangeCountry = ({ target }) => {
        setErrorCountry(null);
        if (!countryPattern.test(target.value)) {
            setErrorCountry('Pais de la empresa inválido.');
        }
        setCountry(target.value);
    }
    const [phone, setPhone] = useState('');
    const [errorPhone, setErrorPhone] = useState('');
    const onChangePhone = ({ target }) => {
        const phoneNumber = parsePhoneNumberFromString(target.value) // 'MX' es el indicativo de país para México
        setErrorPhone('')
        if (phoneNumber) {
            if (phoneNumber.isPossible() &&  phoneNumber.isValid()) {
                setErrorPhone('')
                setPhone(target.value)
                setFormattedPhone(phoneNumber.formatNational())
            }else{  
                setPhone(target.value)
                setFormattedPhone('')
                setErrorPhone("Teléfono invalido.")
            }
        } else {
            setErrorPhone("Teléfono no válido, ingrese lada.");
            setPhone(target.value)
            setFormattedPhone('')
        }
    } 
    const [cellphone, setCellphone] = useState('');
    const [errorCellphone, setErrorCellphone] = useState('');
    const onChangeCellphone = ({ target }) => {
        const cellphoneNumber = parsePhoneNumberFromString(target.value) // 'MX' es el indicativo de país para México
        setErrorCellphone('')
        if (cellphoneNumber) {
            if (cellphoneNumber.isPossible() &&  cellphoneNumber.isValid()) {
                setErrorCellphone('')
                setCellphone(target.value)
                setFormattedCellphone(cellphoneNumber.formatNational())
            }else{  
                setCellphone(target.value)
                setFormattedCellphone('')
                setErrorCellphone("Teléfono celular invalido.")
            }
        } else {
            setErrorCellphone("Teléfono celular no válido, ingrese lada.");
            setCellphone(target.value)
            setFormattedCellphone('')
        }
    }
    const [formattedCellphone, setFormattedCellphone] = useState('');

    const [interior, setInterior] = useState('');
    const onChangeInterior = ({ target }) => setInterior(target.value);

    const [exterior, setExterior] = useState('');
    const onChangeExterior = ({ target }) => setExterior(target.value);

    const [locality, setLocality] = useState('');
    const onChangeLocality = ({ target }) => setLocality(target.value);

    const [ext, setExt] = useState('');
    const onChangeExt = ({ target }) => setExt(target.value);

    const [type, setType] = useState('');
    const onChangeType = ({ target }) => setType(target.value);
    const [types, setTypes] = useState([]);

    const isModeEdit = ( id && !permissions.canEditComp );

    const onSubmit = event => {
        if( errorRfc && errorAlias && errorPhone ) {
            displayNotification(dispatch, 'corrige los errores', alertType.error);
        } else {
            event.preventDefault()
            const data = new FormData(event.target)
            const request = Object.fromEntries(data.entries())
            if (id){
                updateEmployee(request);
            }else{
                saveEmployee(request);
            }
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
    console.log('Aqui carga la empresa seleccionada')
    getCompanyById(id).then( response => {
        if( response.code ) {
            displayNotification(dispatch, response.message, alertType.error);
        } else {
            setRfc(response.rfc);
            setName(response.name);
            setAlias(response.alias);
            setEmailDomain(response.emailDomain);
            setAddress(response.address);
            setInterior(response.interior);
            setExterior(response.exterior);
            setCp(response.cp);
            setLocality(response.locality);
            setCity(response.city);
            setState(response.state);
            setCountry(response.country);
            setPhone(response.phone);
            setCellphone(response.cellphone);
            setExt(response.ext);
            setType(response.type);
        }
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
    };

    const saveEmployee = request => {
    console.log('Aqui guarda la empresa')
    save(request).then( response => {
        if(response.code && response.code === 401) {
            displayNotification(dispatch, response.message, alertType.error);
        } else if (response.code && response.code !== 201) {
            if( response.message ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else if (response.errors) {
                console.log(response.errors);
                displayNotification(dispatch, 'No se ha podido crear la empresa', alertType.error);
            }
        } else {
            displayNotification(dispatch, '¡Empresa creado correctamente!', alertType.success);
            navigate('/company', { replace: true });
        }
    }).catch(error => {
        console.log(error)
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
    };
    
    const updateEmployee = request => {
    console.log('Aqui actualiza la empresa seleccionada')
    update(id, request).then( response => {
        console.log(response);
        if(response.code && response.code === 401) {
            displayNotification(dispatch, response.message, alertType.error);
        } else if (response.code && response.code !== 200) {
            if( response.message ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else if (response.errors) {
                console.log(response.errors);
                displayNotification(dispatch, 'No se ha podido actualizar la empresa', alertType.error);
            }
        } else {
            displayNotification(dispatch, '¡Empresa actualizado correctamente!', alertType.success);
            navigate('/company', { replace: true });
        }
    }).catch(error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
    };
    
    const fetchSelects = () => {
    getCatalogChilds(1000000009).then( response => {
        setTypes(response.filter( cat => cat.id !== 2000900001 && cat.status === 2000100001 ));
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
                    <div className='col-4'>
                        <InputText name='rfc' label='RFC:*' placeholder='Ingresa RFC' disabled={ isModeEdit } value={ rfc } required onChange={ onChangeRfc } maxLength={ 12 } error={ errorRfc } />
                    </div>
                    <div className='col-4'>
                        <InputText name='name' label='Raz&oacute;n Social:*' placeholder='Escribe la raz&oacute;n social' disabled={isModeEdit} value={name} required onChange={onChangeName} maxLength={50} error={errorName} />
                    </div>
                    <div className='col-4'>
                        <InputText name='alias' label='Alias:' placeholder='Escribe el alias' disabled={isModeEdit} value={alias} onChange={onChangeAlias} maxLength={50} error={errorAlias} />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-4'>
                        <InputText name='emailDomain' label='Dominio de correo:' placeholder='Escribe el dominio de correo' disabled={ isModeEdit } value={ emailDomain } onChange={ onChangeEmailDomain } maxLength={ 50 } error={ errorEmailDomain } />
                    </div>
                    <div className='col-4'>
                        <InputText name='address' label='Calle:' placeholder='Escribe la calle' disabled={ isModeEdit } value={ address } onChange={ onChangeAddress } maxLength={ 50 } />
                    </div>
                    <div className='col-4'>
                        <InputText name='exterior' label='# Exterior:' placeholder='Escribe el numero exterior' disabled={ isModeEdit } value={ exterior } onChange={ onChangeExterior } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-4'>
                        <InputText name='interior' label='# Interior:' placeholder='Escribe el numero interior' disabled={ isModeEdit } value={ interior } onChange={ onChangeInterior } />
                    </div>
                    <div className='col-4'>
                        <InputText name='cp' label='Codigo Postal:' placeholder='Escribe el codigo postal' disabled={ isModeEdit } value={ cp } onChange={ onChangeCp } maxLength={ 7 } error={errorCp} />
                    </div>
                    <div className='col-4'>
                        <InputText name='locality' label='Localidad/Colonia:' placeholder='Escribe la colonia' disabled={ isModeEdit } value={ locality } onChange={ onChangeLocality } maxLength={50}/>
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-4'>
                        <InputText name='city' label='Ciudad:' placeholder='Escribe la ciudad' disabled={ isModeEdit } value={ city } onChange={ onChangeCity } maxLength={ 50 } error={errorCity}/>
                    </div>
                    <div className='col-4'>
                        <InputText name='state' label='Estado:' placeholder='Escribe el estado' disabled={ isModeEdit } value={ state } onChange={ onChangeState } maxLength={ 50 } error={errorState}/>
                    </div>
                    <div className='col-4'>
                        <InputText name='country' label='Pais:' placeholder='Escribe el pais' disabled={ isModeEdit } value={ country } onChange={ onChangeCountry } maxLength={ 50 } error={errorCountry}/>
                    </div>
                </div>
                <div className="row text-start">
                    
                    <div className='col-4'>
                        <InputText name='phone' label='Teléfono:' placeholder='Escribe teléfono' disabled={isModeEdit} value={phone} onChange={onChangePhone} maxLength={15} error={errorPhone} />
                    </div>
                    <div className='col-4'>
                        <InputText name='ext' label='Extension:' placeholder='Escribe extension' disabled={ isModeEdit } value={ ext } onChange={ onChangeExt } maxLength={ 5 } />
                    </div>
                    <div className='col-4'>
                    <InputText name='cellphone' label='Teléfono celular:' placeholder='Escribe teléfono celular' disabled={isModeEdit} value={cellphone} onChange={onChangeCellphone} maxLength={20} error={errorCellphone} />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-4'>
                        <Select name="type" label="Clasificaci&oacute;n:" options={ types } disabled={ isModeEdit } value={ type } onChange={ onChangeType } />
                    </div>
                </div>
                <div className="row text-start">
                    
                </div>
                    <div className="pt-3 d-flex flex-row-reverse">
                        {permissions.canEditComp && (<button type="submit" className="btn btn-primary" >Guardar</button>)}
                        &nbsp;
                        <button type="button" className="btn btn-danger" onClick={ () => navigate(`/company`) }>{permissions.canEditComp ? 'Cancelar' : 'Regresar'}</button>
                    </div>
                </form>
            </div>)
    };

    return (
    <>
        <div className="d-flex d-flex justify-content-center">
            <h3 className="fs-4 card-title fw-bold mb-4">{`Empresa ${name ? ' > Detalles de ' + name  : ''}`}</h3>
        </div>
        { id && renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Company' recordId={ id } />) }
    </>
    )
}
