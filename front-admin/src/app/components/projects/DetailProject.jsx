import { useEffect, useState } from 'react';
import { InputText } from '../custom/InputText';
import { Select } from '../custom/Select';
// import { TextArea } from '../../custom/TextArea';
import { DatePicker } from '../custom/DatePicker';
import { useNavigate } from 'react-router-dom';
import { save, update } from '../../services/ProjectService';
import { getSelect } from '../../services/CompanyService';
import { buildPayloadMessage, handleDate, handleText, numberToString } from '../../helpers/utils';
import { useDispatch, useSelector } from 'react-redux';
import { setMessage } from '../../../store/alert/alertSlice';
import { alertType } from '../custom/alerts/types/types';

export const DetailProject = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );
    const project = useSelector( state => state.projectReducer.project );
    const [pKey, setPKey] = useState(project.key);
    const [description, setDescription] = useState(project.description);
    const [createdBy, setCreatedBy] = useState(project.createdBy);
    const [dateCreated, setDateCreated] = useState(handleDate(project.creationDate, true));
    const [company, setCompany] = useState(numberToString(project.companyId, '-1'));
    const [pm, setPm] = useState(numberToString(project.projectManagerId, '-1'));
    const [installationDate, setInstallationDate] = useState(handleDate(project.installationDate, false));
    const [companies, setCompanies] = useState([]);
    const [pms, setPms] = useState([]);

    const isModeEdit = (project.id && project.id > 0);

    const fetchCatalogClient = () => {
        getSelect()
            .then( response => {
                setCompanies( response );
                if (response.length === 1) {
                    setCompany(numberToString(response[0].id, ''));
                    setPms(response[0].employess);
                } else if (company !== '-1' && pm !== '-1') {
                    const index = Number(company) - 1;
                    setPms( response[index].employess );
                }
            }).catch( error => {
                console.log(error);
            });
    };

    useEffect(() => {
        fetchCatalogClient();
    }, []);

    const onChangePKey = ({ target }) => setPKey(handleText(target).toUpperCase());
    const onChangeDesc = ({ target }) => setDescription(target.value);
    const onChangeCreatedDate = (date) => setDateCreated(date);
    const onChangeInstallationDate = (date) => setInstallationDate(date);
    const onChangeCompany = ({ target }) => {
        setCompany(target.value);
        const index = target.value - 1;
        setPms( companies[index].employess );
    }
    const onChangePm = ({ target }) => setPm(target.value);
    
    const onSubmit = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        if ( isModeEdit ) {
            updateProject(request);
        } else {
            saveProject(request);
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
        update(project.id, request).then( response => {
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
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un errro al actualizar el proyecto, contacte al administrador', alertType.error)));
        });
    };

    const renderCreatedBy = () => isModeEdit && (
        <InputText name='createdBy' label='Creado por' value={ createdBy } disabled />
    )
    
    const renderCreationDate = () => isModeEdit && (
        <DatePicker name='creationDate' label="Fecha" disabled value={ dateCreated } onChange={ (date) => onChangeCreatedDate(date) } />
    )

    const renderSelectClient = () => permissions.isAdminSas && (
        <Select name="companyId" label="Cliente" options={ companies } value={ company } disabled={ isModeEdit && permissions.canEditProjCli } required onChange={ onChangeCompany } />
    )

    return (
        <div className='d-grid gap-2 col-6 mx-auto'>
            <form className="needs-validation" onSubmit={ onSubmit }>
                
                <InputText name='key' label='Clave' placeholder='Ingresa clave' 
                    disabled={ isModeEdit } value={ pKey } required onChange={ onChangePKey } maxLength={ 12 } />
                <InputText name='description' label='Descripci&oacute;n' placeholder='Ingresa descripci&oacute;n' 
                    value={ description } required onChange={ onChangeDesc } maxLength={ 70 } />
                <DatePicker name='installationDate' label="Fecha instalaci&oacute;n" required
                    value={ installationDate } onChange={ (date) => onChangeInstallationDate(date) } />
                {/* <div className="row text-start">
                    <div className='col-12'>
                    <TextArea label='Descripci&oacute;n' placeholder='Agrega alguna descripci&oacute;n al proyecto' value={ description } onChange={ onChangeDesc } />
                    </div>
                </div> */}

                { renderCreatedBy() }
                { renderCreationDate() }
                { renderSelectClient() }
                <Select name="projectManagerId" label="Project Manager" options={ pms } value={ pm } required onChange={ onChangePm } />

                <div className="pt-3 d-flex flex-row-reverse">
                    <button type="submit" className="btn btn-primary">Guardar</button>
                    &nbsp;
                    <button type="button" className="btn btn-danger" onClick={ () => navigate(`/home`) }>Cancelar</button>
                </div>
            </form>
        </div>
    )
}

