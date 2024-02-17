import { useEffect, useState } from 'react';
import { InputText } from '../custom/InputText';
import { Select } from '../custom/Select';
import { DatePicker } from '../custom/DatePicker';
import { useNavigate, useParams } from 'react-router-dom';
import { getProjectById, save, update } from '../../services/ProjectService';
import { displayNotification, formatter, genericErrorMsg, handleDateStr, handleText, numberToString } from '../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../custom/alerts/types/types';
import { TextArea } from '../custom/TextArea';
import { getCompanySelect } from '../../services/CompanyService';
import { setCurrentAppTab, setCurrentTab, setProject } from '../../../store/project/projectSlice';

import { TableApplications } from '../applications/page/TableApplications';
import { TableLog } from '../custom/TableLog';
import { TableOrders } from '../orders/page/TableOrders';

export const DetailProject = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { key } = useParams();
    const { user, permissions } = useSelector( state => state.auth );
    const {currentTab, project, projectPaid} = useSelector( state => state.projectReducer );

    const [pKey, setPKey] = useState('');
    const [keyError, setKeyError] = useState('');
    const [description, setDescription] = useState('');
    const [createdBy, setCreatedBy] = useState('');
    const [dateCreated, setDateCreated] = useState();
    const [pm, setPm] = useState('');
    const [installationDate, setInstallationDate] = useState();
    const [observations, setObservations] = useState('');
    const [pms, setPms] = useState([]);

    const [companyId, setCompanyId] = useState( project && project.companyId ? numberToString(project.companyId) : '2');
    const [companies, setCompanies] = useState([]);

    const fetchCatalogCompanies = () => {
        getCompanySelect().then( response => {
            setCompanies(response);
            fetchCatEmployees(response, companyId);
        }).catch( error => {
            console.log(error);
        });
    }

    const fetchCatEmployees = ( companies, companyId ) => {
        const companySelected = companies.find( c => c.id === Number(companyId) );
        if( companySelected ) {
            setPms( companySelected.employess );
        }
    }

    const fetchProject = () => {
        if( key ) {
          getProjectById(key).then( response => {
              if( response.code ) {
                  displayNotification(dispatch, response.message, alertType.error);
              } else {
                  dispatch(setProject(response));
                  setPKey(response.key);
                  setDescription(response.description);
                  setCreatedBy(response.createdBy);
                  setDateCreated(handleDateStr(response.creationDate));
                  setPm(numberToString(response.projectManagerId, ''));
                  setInstallationDate(handleDateStr(response.installationDate));
                  setObservations(response.observations ? response.observations : '');
              }
          }).catch( error => {
              console.log(error);
              displayNotification(dispatch, genericErrorMsg, alertType.error);
          });
        }
    }

    useEffect(() => {
        fetchCatalogCompanies();
        fetchProject();
    }, []);

    const onChangeCompany = ({ target }) => {
        setCompanyId(target.value);
        fetchCatEmployees(companies, Number(target.value));
    };
    const onChangePKey = ({ target }) => {
        setPKey(handleText(target).toUpperCase())
    };
    const onBlurPKey = ({ target }) => {
        if( !validateKey(target.value) ) {
            setKeyError('Formato de clave incorrecta');
        }
    };
    const onChangeDesc = ({ target }) => setDescription(target.value);
    const onChangeCreatedDate = (date) => setDateCreated(date);
    const onChangeInstallationDate = (date) => setInstallationDate(date);
    const onChangePm = ({ target }) => setPm(target.value);
    const onChangeObservations = ({ target }) => setObservations(target.value);

    const validateKey = value => {
        const regex = /^[A-Z]{1}-\d{2}-\d{4}-\d{2}$/;
        return regex.test(value);
    }
    
    const onSubmit = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        if ( key ) {
            updateProject(request);
        } else {
            if( validateKey(request.key) ) {
                saveProject(request);
            } else {
                displayNotification(dispatch, '¡Clave invalida!', alertType.warning);
            }
        }
    }

    const saveProject = request => {
        save(request).then( response => {
            if(response.code && response.code === 401) {
                displayNotification(dispatch, response.message, alertType.error);
            } else if (response.code && response.code !== 201) {
                if( response.message ) {
                    displayNotification(dispatch, response.message, alertType.error);
                } else if (response.errors) {
                    console.log(response.errors);
                    displayNotification(dispatch, 'No se ha podido crear el proyecto', alertType.error);
                }
            } else {
                dispatch(setProject(request));
                displayNotification(dispatch, '¡Proyecto creado correctamente!', alertType.success);
                navigate('/', { replace: true });
            }
        }).catch(error => {
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    };

    const updateProject = request => {
        update(pKey, request).then( response => {
            if(response.code && response.code === 401) {
                displayNotification(dispatch, response.message, alertType.error);
            } else if (response.code && response.code !== 200) {
                if( response.message ) {
                    displayNotification(dispatch, response.message, alertType.error);
                } else if (response.errors) {
                    console.log(response.errors);
                    displayNotification(dispatch, 'No se ha podido actualizar el proyecto', alertType.error);
                }
            } else {
                displayNotification(dispatch, '¡Proyecto actualizado correctamente!', alertType.success);
                navigate('/', { replace: true });
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    };

    const renderCreatedBy = () => permissions.isAdminRoot && key && (
        <InputText name='createdBy' label='Creado por' value={ createdBy } disabled />
    )
    
    const renderCreationDate = () => permissions.isAdminRoot && key && (
        <DatePicker name='creationDate' label="Fecha" disabled value={ dateCreated } onChange={ (date) => onChangeCreatedDate(date) } />
    )

    const handleAddApplication = () => {
        dispatch(setCurrentAppTab(1));
        navigate(`/project/${ key }/application/add`);
    }
    
    const renderAddButton = () => permissions.canCreateProjApp && (
        <div className="d-flex flex-row-reverse p-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddApplication }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );
    
    const onClickBack = () => {
        if ( (!permissions.canEditEmp && currentTab == 2) || currentTab === 1 ) {
            navigate('/home')
        } else {
            onClickTab(currentTab - 1);
        }
    }
    
    const onClickTab = tagId => {
        fetchProject();
        dispatch(setCurrentTab(tagId));
    }
    
    const renderTabs = () => key && (
        <div className="d-flex flex-row-reverse">
            <ul className="nav nav-tabs">
            <li>
                <button type="button" className="btn btn-link" onClick={ () => onClickBack() }>&lt;&lt; Regresar</button>
            </li>
            {
                permissions.canEditEmp && (
                <li className="nav-item" onClick={ () => onClickTab(1) }>
                    <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` } aria-current="page">Detalle</a>
                </li>
                )
            }
            <li className="nav-item" onClick={ () => onClickTab(2) }>
                <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Aplicaciones</a>
            </li>
            {
                permissions.canAdminOrd && (
                <li className="nav-item" onClick={ () => onClickTab(3) }>
                    <a className={ `nav-link ${ (currentTab === 3) ? 'active' : '' }` }>Ordenes</a>
                </li>
                )
            }
            <li className="nav-item" onClick={ () => dispatch(setCurrentTab(4)) }>
                <a className={ `nav-link ${ (currentTab === 4) ? 'active' : '' }` }>Historial</a>
            </li>
            </ul>
        </div>
    )
    
    const title = project && project.key ? `${project.key} ${project.description}` : 'Proyecto nuevo';
      
    const renderPendingAmount = () => {
        const pendingAmount = project.amount - projectPaid.amount;
        const labelText = pendingAmount >= 0 ? 'Monto pendiente:' : 'Saldo a favor';
        const cssText = pendingAmount > 0 ? 'danger' : 'success';
        return (
            <p className="h4">
            { labelText } <span className={ `text-${cssText}` } >{ formatter.format( Math.abs(pendingAmount) ) }</span>
            </p>
        )
    }

    const renderDetailProject = () => (
        <div className='d-grid gap-2 col-6 mx-auto'>
            <form className="needs-validation" onSubmit={ onSubmit }>
                <Select name="companyId" label="Empresa" options={ companies } disabled={ key && !project.active } value={ companyId } required onChange={ onChangeCompany } />
                <InputText name='key' label='Clave' placeholder='Ingresa clave' disabled={ key }
                    value={ pKey } error={ keyError } required maxLength={ 12 } 
                    onChange={ onChangePKey } onFocus={ () => setKeyError('') } onBlur={ onBlurPKey } />
                <InputText name='description' label='Descripci&oacute;n' placeholder='Ingresa descripci&oacute;n'  disabled={ key && !project.active }
                    value={ description } required onChange={ onChangeDesc } maxLength={ 255 } />
                <DatePicker name='installationDate' label="Fecha instalaci&oacute;n" disabled={ key && !project.active }
                    value={ installationDate } onChange={ (date) => onChangeInstallationDate(date) } />
                { renderCreatedBy() }
                { renderCreationDate() }
                <Select name="projectManagerId" label="Project Manager" options={ pms } value={ pm } required onChange={ onChangePm } disabled={ key && !project.active } />
                <TextArea name='observations' label='Observaciones' placeholder='Escribe observaciones' 
                    value={ observations } maxLength={ 1500 } onChange={ onChangeObservations } disabled={ key && !project.active } />

                { (!key || project.active) &&
                    (
                        <div className="pt-3 d-flex flex-row-reverse">
                            <button type="submit" className="btn btn-primary" disabled={ (key && !permissions.canEditProj) }>Guardar</button>
                            &nbsp;
                            <button type="button" className="btn btn-danger" onClick={ () => navigate(`/home`) }>Cancelar</button>
                        </div>
                    )
                }
            </form>
        </div>
    )
    
    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">{ title }</h4>
            { project && project.key && currentTab === 3 && (
            <>
                <p className="h4">
                {/* Iva: <span className='text-primary'>{ project.tax }</span> Total: <span className='text-primary'>{ project.total }</span> */}
                Costo del proyecto: <span className='text-primary'>{ formatter.format(project.amount) }</span>
                </p>
                { renderPendingAmount() }
            </>
            )}
            { renderTabs() }
            { (currentTab === 2 && key ) && renderAddButton() }
            { 
                currentTab === 2 ? ( <TableApplications projectId = { key } /> ) : ( 
                    currentTab === 4 ? (<TableLog tableName={ 'Project' } recordId={ key } />) : (
                    currentTab === 3 ? (<TableOrders projectId={ key } />) : renderDetailProject()
                    ) 
                )
            }
        </div>
    )
}