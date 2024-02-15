import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { getCompanySelect } from "../../services/CompanyService";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { getAplicationsF, getApplicationByName, save, update } from "../../services/ApplicationService";
import { displayNotification, genericErrorMsg, numberToString } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
import { setApp } from '../../../store/application/applicationSlice';
// import { setEmployeeS } from '../../../store/company/companySlice';

export const DetailApplication = () => {
    const { app } = useSelector( state => state.applicationReducer );
    console.log("El app es:"+JSON.stringify(app, null, 2));
    const {id} = useParams();
    const { permissions} = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [name, setName] = useState(app.name ? app.name : '');
    const onChangeName = ({ target }) => setName(target.value);
    const [description, setDescription] = useState(app ? app.description : '');
    const onChangeDescription = ({ target }) => setDescription(target.value);
    const [company, setCompany] = useState(app.companyId ? app.companyId : '');
    const [companyDesc, setCompanyDesc] = useState(app.companyName? app.companyName: '');
    const [companies, setCompanies] = useState([]);
    const [active, setActive] = useState('');
    console.log("permissions: "+ !permissions.isAdminSas+ ' name: '+name+' active: '+!active)
    const isModeEdit = ( (app.name && !permissions.isAdminSas) || (app.name && !active ));
    
    const onChangeCompany = ({ target }) => {
        setCompany(target.value);
        const companySelected = companies.find( c => { //Cuando selecciona una compañia
            const isFind = c.id === Number(target.value)
            return isFind;
        });
        setCompanyDesc( companySelected ? `> ${companySelected.value}` : '' );
    }


    const onSubmit = event => {
        event.preventDefault()
        const data = new FormData(event.target)
        const request = Object.fromEntries(data.entries())
        if (app.name){
            updateApplication(request);
        }else{
            saveAplication(request);
        }
    }
    
    const onClickBack = () => {
        if ( (!permissions.canEditEmp && currentTab == 2) || currentTab === 1 ) {
        navigate('/application')
        } else {
        setCurrentTab(currentTab - 1);
        }
    }

    const renderTabs = () => (//Esto controla los tabs
        <ul className="nav nav-tabs d-flex flex-row-reverse">
        {/* {app && (<li className="nav-item" onClick={ () => setCurrentTab(2) }>
            <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
        </li>)} */}
        <li className="nav-item" onClick={ () => setCurrentTab(1) }>
            <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
        </li>
        <li>
            <button type="button" className="btn btn-link" onClick={ () => onClickBack() }>&lt;&lt; Regresar</button>
        </li>
        </ul>
    )

    const fetchApplication = () => {
        getApplicationByName(app.name).then( response => {
            if( response.code ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setDescription(response.description);
                setActive(response.active);
                console.log('Valor de employee: '+JSON.stringify(response, null, 2));
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    };

    const saveAplication = request => {
        console.log('Se va a guardar este valor:'+JSON.stringify(request, null, 2));
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
                dispatch(setApp(request));
                navigate('/application', { replace: true });
            }
        }).catch(error => {
            console.log(error)
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    };
    
    const updateApplication = request => {
        //console.log('Aqui actualiza el usuario seleccionado')
        console.log('Se manda este valor'+JSON.stringify(request, null, 2));
        update(name, request).then( response => {
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
                request['name'] = name
                dispatch(setApp(request));
                navigate('/application', { replace: true });
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    };
    
    const fetchSelects = () => {

        getCompanySelect().then( response => {
        setCompanies(response);
        }).catch( error => {
        console.log(error);
        });
    };

    useEffect(() => {
        fetchSelects()
        if(name){
            fetchApplication();
        }
    }, [])
    
    const renderDetail = () => {
        return (<div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                    <div className="row text-start">
                        <div className='col-4'>
                            <Select name="companyId" label="Empresa" options={ companies } disabled={ isModeEdit } value={ company } required onChange={ onChangeCompany } />
                        </div>
                    </div>
                    <div className="row text-start">
                        <div className='col-12'>
                            <InputText name='name' label='Nombre' placeholder='Escribe el nombre' disabled={ app.name } value={ name } required onChange={ onChangeName } maxLength={ 255 } />
                        </div>
                    </div>
                    <div className="row text-start">
                        <div className='col-12'>
                            <InputText name='description' label='Descripci&oacute;n' placeholder='Escribe descripción' disabled={ isModeEdit } value={ description } onChange={ onChangeDescription } maxLength={ 255 } />
                        </div>
                    </div>
                    <div className="pt-3 d-flex flex-row-reverse">
                        {permissions.canEditEmp &&(<button type="submit" className="btn btn-primary" >Guardar</button>)}
                        &nbsp;
                        <button type="button" className="btn btn-danger" onClick={ () => navigate(`/application`) }>{permissions.canEditEmp ? 'Cancelar' : 'Regresar'}</button>
                    </div>
                </form>
            </div>)
    };

    return (
        <>
        <div className="d-flex d-flex justify-content-center">
            <h3 className="fs-4 card-title fw-bold mb-4">{`Aplicación ${name ? ' > Detalles de ' + name : ''}`}</h3>
        </div>
        {renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Aplication' recordId={ name } />) }
        </>
    )
}
