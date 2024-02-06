import { useEffect, useState } from 'react';
import { InputText } from '../custom/InputText';
import { Select } from '../custom/Select';
import { DatePicker } from '../custom/DatePicker';
import { useNavigate } from 'react-router-dom';
import { save, update } from '../../services/ProjectService';
import { displayNotification, genericErrorMsg, handleDateStr, handleText, numberToString } from '../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { alertType } from '../custom/alerts/types/types';
import { getEmployessByCompanyIdAndPosition } from '../../services/EmployeeService';
import { TextArea } from '../custom/TextArea';
import { getCompanySelect } from '../../services/CompanyService';

export const DetailProject = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { user, permissions } = useSelector( state => state.auth );
    const { project } = useSelector( state => state.projectReducer );

    const [projectId, setProjectId] = useState(project.id);
    const [pKey, setPKey] = useState(project.key ? project.key : '');
    const [keyError, setKeyError] = useState('');
    const [description, setDescription] = useState(project.description ? project.description : '');
    const [createdBy, setCreatedBy] = useState(project.createdBy);
    const [dateCreated, setDateCreated] = useState(handleDateStr(project.creationDate));
    const [pm, setPm] = useState(numberToString(project.projectManagerId, ''));
    const [installationDate, setInstallationDate] = useState(handleDateStr(project.installationDate));
    const [observations, setObservations] = useState(project.observations ? project.observations : '');
    const [pms, setPms] = useState([]);

    const [companyId, setCompanyId] = useState(user.companyId);
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
        const companySelected = companies.find( c => c.id === companyId );
        if( companySelected ) {
            setPms( companySelected.employess );
        }
    }

    useEffect(() => {
        fetchCatalogCompanies();
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
        if ( projectId ) {
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
                displayNotification(dispatch, '¡Proyecto creado correctamente!', alertType.success);
                navigate('/', { replace: true });
            }
        }).catch(error => {
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    };

    const updateProject = request => {
        update(projectId, request).then( response => {
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

    const renderCreatedBy = () => permissions.isAdminRoot && projectId && (
        <InputText name='createdBy' label='Creado por' value={ createdBy } disabled />
    )
    
    const renderCreationDate = () => permissions.isAdminRoot && projectId && (
        <DatePicker name='creationDate' label="Fecha" disabled value={ dateCreated } onChange={ (date) => onChangeCreatedDate(date) } />
    )

    return (
        <div className='d-grid gap-2 col-6 mx-auto'>
            <form className="needs-validation" onSubmit={ onSubmit }>
                <Select name="companyId" label="Empresa" options={ companies } disabled={ projectId && !project.active } value={ companyId } required onChange={ onChangeCompany } />
                <InputText name='key' label='Clave' placeholder='Ingresa clave' disabled={ projectId && !project.active }
                    value={ pKey } error={ keyError } required maxLength={ 12 } 
                    onChange={ onChangePKey } onFocus={ () => setKeyError('') } onBlur={ onBlurPKey } />
                <InputText name='description' label='Descripci&oacute;n' placeholder='Ingresa descripci&oacute;n'  disabled={ projectId && !project.active }
                    value={ description } required onChange={ onChangeDesc } maxLength={ 255 } />
                <DatePicker name='installationDate' label="Fecha instalaci&oacute;n" disabled={ projectId && !project.active }
                    value={ installationDate } onChange={ (date) => onChangeInstallationDate(date) } />
                { renderCreatedBy() }
                { renderCreationDate() }
                <Select name="projectManagerId" label="Project Manager" options={ pms } value={ pm } required onChange={ onChangePm } disabled={ projectId && !project.active } />
                <TextArea name='observations' label='Observaciones' placeholder='Escribe observaciones' 
                    value={ observations } maxLength={ 1500 } onChange={ onChangeObservations } disabled={ projectId && !project.active } />

                { (!projectId || project.active) &&
                    (
                        <div className="pt-3 d-flex flex-row-reverse">
                            <button type="submit" className="btn btn-primary" disabled={ (projectId && !permissions.canEditProj) }>Guardar</button>
                            &nbsp;
                            <button type="button" className="btn btn-danger" onClick={ () => navigate(`/home`) }>Cancelar</button>
                        </div>
                    )
                }
            </form>
        </div>
    )
}