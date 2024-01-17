import { useEffect, useState } from 'react';
import { InputText } from '../custom/InputText';
import { Select } from '../custom/Select';
import { DatePicker } from '../custom/DatePicker';
import { useNavigate } from 'react-router-dom';
import { getProjectById, save, update } from '../../services/ProjectService';
import { buildPayloadMessage, handleDate, handleDateStr, handleText, numberToString } from '../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { setMessage } from '../../../store/alert/alertSlice';
import { alertType } from '../custom/alerts/types/types';
import { getEmployess } from '../../services/EmployeeService';

export const DetailProject = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );
    const { project } = useSelector( state => state.projectReducer );
    const [projectId, setProjectId] = useState(project.id);
    const [pKey, setPKey] = useState(project.key);
    const [description, setDescription] = useState(project.description);
    const [createdBy, setCreatedBy] = useState(project.createdBy);
    const [dateCreated, setDateCreated] = useState(project.creationDate);
    const [pm, setPm] = useState(numberToString(project.projectManagerId, ''));
    const [installationDate, setInstallationDate] = useState(handleDateStr(project.installationDate));
    const [pms, setPms] = useState([]);

    const fetchCatalogEmployee = () => {
        getEmployess()
            .then( response => {
                setPms( response );
            }).catch( error => {
                console.log(error);
            });
    };

    useEffect(() => {
        fetchCatalogEmployee();
    }, []);

    const onChangePKey = ({ target }) => {
        setPKey(handleText(target).toUpperCase())
    };
    const onChangeDesc = ({ target }) => setDescription(target.value);
    const onChangeCreatedDate = (date) => setDateCreated(date);
    const onChangeInstallationDate = (date) => setInstallationDate(date);
    const onChangePm = ({ target }) => setPm(target.value);
    
    const onSubmit = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        if ( projectId ) {
            updateProject(request);
        } else {
            const regex = /^[A-Z]{1}-\d{2}-\d{4}-\d{2}$/;
            if( regex.test(request.key) ) {
                saveProject(request);
            } else {
                dispatch(setMessage(buildPayloadMessage('¡Clave invalida!', alertType.warning)));
            }
        }
    }

    const saveProject = request => {
        save(request).then( response => {
            if(response.code && response.code === 401) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else if (response.code && response.code !== 201) {
                if( response.message ) {
                    dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
                } else if (response.errors) {
                    console.log(response.errors);
                    dispatch(setMessage(buildPayloadMessage('No se ha podido crear el proyecto', alertType.error)));
                }
            } else {
                dispatch(setMessage(buildPayloadMessage('¡Proyecto creado correctamente!', alertType.success)));
                navigate('/', { replace: true });
            }
        }).catch(error => {
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear el proyecto, contacte al administrador', alertType.error)));
        });
    };

    const updateProject = request => {
        update(projectId, request).then( response => {
            if(response.code && response.code === 401) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else if (response.code && response.code !== 200) {
                if( response.message ) {
                    dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
                } else if (response.errors) {
                    console.log(response.errors);
                    dispatch(setMessage(buildPayloadMessage('No se ha podido actualizar el proyecto', alertType.error)));
                }
            } else {
                dispatch(setMessage(buildPayloadMessage('¡Proyecto actualizado correctamente!', alertType.success)));
            }
        }).catch(error => {
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar el proyecto, contacte al administrador', alertType.error)));
        });
    };

    const renderCreatedBy = () => projectId && (
        <InputText name='createdBy' label='Creado por' value={ createdBy } disabled />
    )
    
    const renderCreationDate = () => projectId && (
        <DatePicker name='creationDate' label="Fecha" disabled value={ dateCreated } onChange={ (date) => onChangeCreatedDate(date) } />
    )

    return (
        <div className='d-grid gap-2 col-6 mx-auto'>
            <form className="needs-validation" onSubmit={ onSubmit }>
                
                <InputText name='key' label='Clave' placeholder='Ingresa clave' 
                    disabled={ projectId } value={ pKey } required onChange={ onChangePKey } maxLength={ 12 } />
                <InputText name='description' label='Descripci&oacute;n' placeholder='Ingresa descripci&oacute;n' 
                    value={ description } required onChange={ onChangeDesc } maxLength={ 70 } />
                <DatePicker name='installationDate' label="Fecha instalaci&oacute;n" required
                    value={ installationDate } onChange={ (date) => onChangeInstallationDate(date) } />
                { renderCreatedBy() }
                { renderCreationDate() }
                <Select name="projectManagerId" label="Project Manager" options={ pms } value={ pm } required onChange={ onChangePm } />

                <div className="pt-3 d-flex flex-row-reverse">
                    <button type="submit" className="btn btn-primary" disabled={ (projectId && !permissions.canEditProj) }>Guardar</button>
                    &nbsp;
                    <button type="button" className="btn btn-danger" onClick={ () => navigate(`/home`) }>Cancelar</button>
                </div>
            </form>
        </div>
    )
}