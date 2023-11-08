import { useContext, useEffect, useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
// import { TextArea } from '../../custom/TextArea';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate } from 'react-router-dom';
import { save } from '../../../services/ProjectService';
import { getSelect } from '../../../services/ClientService';
import { LoadingContext } from '../../custom/loading/context/LoadingContext';
import { renderErrorMessage } from '../../../helpers/handleErrors';
import { AuthContext } from '../../auth/context/AuthContext';
import { checkResponse } from '../../../helpers/handleResponse';

export const DetailProject = ({
    project
}) => {

    console.log(project);

    const navigate = useNavigate();
    const { changeLoading } = useContext( LoadingContext );
    const [pKey, setPKey] = useState('');
    const [description, setDescription] = useState('');
    const [createdBy, setCreatedBy] = useState('');
    const [dateCreated, setDateCreated] = useState(new Date());
    const [client, setClient] = useState('-1');
    const [pm, setPm] = useState('-1');
    const [installationDate, setInstallationDate] = useState();
    const [clients, setClients] = useState([]);
    const [pms, setPms] = useState([]);
    const [msgError, setMsgError] = useState();
    const [errors, setErros] = useState();

    const isModeEdit = () => ( project && project.id !== undefined );

    const fetchCatalogClient = () => {
        changeLoading(true);
        getSelect()
            .then( response => {
                checkResponse(response);
                setClients(response);
                changeLoading(false);
            }).catch( error => {
                changeLoading(false);
                setMsgError(`Imposible cargar catalogo de clientes, contacta al administrador del sistema!`);
            });
    };

    const { logout } = useContext( AuthContext );

    const onLogout = () => {
      logout();
      navigate('/login', { replace: true })
    }

    const setProjectToForm = () => {
        setPKey(project.key);
        setDescription(project.description);
        setCreatedBy(project.createdBy);
        setClient(project.clientId);
        setPm(project.projectManagerId);
        setInstallationDate(project.installationDate);
    }

    useEffect(() => {
        fetchCatalogClient();
        setProjectToForm();
    }, []);


    // ALL Functions to helper
    const handleText = ( { value, maxLength } ) => value.slice(0, maxLength);
    // const upperFirstChar = word => word[0].toUpperCase() + word.substring(1).toLowerCase();
    // const upperAllFirtsChars = word => {
    //     const words = word.split(" ");
    //     for (let i = 0; i < words.length; i++) {
    //     words[i] = words[i][0].toUpperCase() + words[i].substr(1);
    //     }
    //     words.join(" ");
    //     return wors;
    // }

    const onChangePKey = ({ target }) => setPKey(handleText(target).toUpperCase());
    const onChangeDesc = ({ target }) => setDescription(target.value);
    const onChangeCreatedDate = (date) => setDateCreated(date);
    const onChangeInstallationDate = (date) => setInstallationDate(date);
    const onChangeClient = ({ target }) => {
        setClient(target.value);
        const index = target.value - 1;
        if( index < 0 ) {
            setPm("-1");
            setPms([]);
        } else {
            setPms( clients[index].employess);
        }
    }
    const onChangePm = ({ target }) => setPm(target.value);
    
    const onSubmit = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        
        save(request).then( response => {
            if(response.code && response.code === 401) {
                onLogout();
            } else if (response.code && response.code !== 200) {
                if( response.message ) {
                    setMsgError(response.message)
                } else if (response.errors) {
                    setErros(response.errors);
                }
            } else {
                navigate('/', { replace: true });
            }
        }).catch(error => {
            setMsgError(error.message);
        });
    }

    const renderCreatedBy = () => isModeEdit() && (
        <div className='col-3'>
            <InputText name='createdBy' label='Creado por' value={ createdBy } disabled />
        </div>
    )
    
    const renderCreationDate = () => isModeEdit() && (
        <div className='col-3'>
            <DatePicker name='creationDate' label="Fecha" value={ dateCreated } onChange={ (date) => onChangeCreatedDate(date) } />
        </div>
    )

    return (
        <div>
            { renderErrorMessage(msgError, errors, true) }
            <form onSubmit={ onSubmit }>
                <div className='text-center'>
                <div className="row text-start">
                    <div className='col-3'>
                        <InputText name='key' label='Clave' placeholder='Ingresa clave' disabled={ isModeEdit() } value={ pKey } onChange={ onChangePKey } maxLength={ 12 } />
                    </div>
                    <div className='col-6'>
                        <InputText name='description' label='Descripci&oacute;n' placeholder='Ingresa descripci&oacute;n' value={ description } onChange={ onChangeDesc } maxLength={ 70 } />
                    </div>
                    <div className='col-3'>
                        <DatePicker name='installationDate' label="Fecha instalaci&oacute;n" value={ installationDate } onChange={ (date) => onChangeInstallationDate(date) } />
                    </div>
                </div>
                {/* <div className="row text-start">
                    <div className='col-12'>
                    <TextArea label='Descripci&oacute;n' placeholder='Agrega alguna descripci&oacute;n al proyecto' value={ description } onChange={ onChangeDesc } />
                    </div>
                </div> */}
                <div className="row text-start">
                    { renderCreatedBy() }
                    { renderCreationDate() }
                    <div className='col-3'>
                    <Select name="clientId" label="Cliente" options={ clients } value={ client } onChange={ onChangeClient } />
                    </div>
                    <div className='col-3'>
                    <Select name="projectManagerId" label="Project Manager" options={ pms } value={ pm } onChange={ onChangePm } />
                    </div>
                </div>
                </div>
                <div className="pt-3 d-flex flex-row-reverse">
                    <button type="submit" className="btn btn-primary">Guardar</button>
                    &nbsp;
                    <button type="button" className="btn btn-danger" onClick={ () => navigate(`/home`) }>Cancelar</button>
                </div>
            </form>
        </div>
    )
}

