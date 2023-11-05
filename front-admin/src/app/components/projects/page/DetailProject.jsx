import { useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { TextArea } from '../../custom/TextArea';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate } from 'react-router-dom';

export const DetailProject = () => {
    const navigate = useNavigate();
    const [pKey, setPKey] = useState('');
    const [name, setName] = useState('');
    const [status, setStatus] = useState('1');
    const [description, setDescription] = useState('');
    const [createdBy, setCreatedBy] = useState('Selene Pascali');
    const [dateCreated, setDateCreated] = useState(new Date());
    const [client, setClient] = useState('-1');
    const [pm, setPm] = useState('-1');
    const [catStatus, setCatStatus] = useState([
        { id: '1', value: 'Nuevo' },
        { id: '2', value: 'Control de cambio' },
        { id: '3', value: 'Extensión' },,
    ]);
    const [clients, setClients] = useState([
        { id: '1', value: 'SAS' },
        { id: '2', value: 'Prosa' },
        { id: '3', value: 'Multiva' },
    ]);
    const [pms, setPms] = useState([
        { id: '1', value: 'Jaime Carreño' },
        { id: '2', value: 'Rogelio Zavaleta' },
    ]);


    // ALL Functions to helper
    const handleText = ( { value, maxLength } ) => value.slice(0, maxLength);
    const upperFirstChar = word => word[0].toUpperCase() + word.substring(1).toLowerCase();
    const upperAllFirtsChars = word => {
        const words = word.split(" ");
        for (let i = 0; i < words.length; i++) {
        words[i] = words[i][0].toUpperCase() + words[i].substr(1);
        }
        words.join(" ");
        return wors;
    }

    const onChangePKey = ({ target }) => setPKey(handleText(target).toUpperCase());
    const onChangeName = ({ target }) => setName(target.value);
    const onChangeDesc = ({ target }) => setDescription(target.value);
    const onChangeStatus = ({target }) => setStatus(target.value);
    const onChangeCreatedDate = (date) => setDateCreated(date);
    const onChangeClient = ({ target }) => setClient(target.value);
    const onChangePm = ({ target }) => setPm(target.value);
    
    const onSubmit = formData => {
        console.log(formData);
    }

    return (
        <div>
        <form onSubmit={ onSubmit }>
            <div className='text-center'>
            <div className="row text-start">
                <div className='col-2'>
                <InputText label='Clave' placeholder='Ingresa clave' value={ pKey } onChange={ onChangePKey } maxLength={ 12 } />
                </div>
                <div className='col-6'>
                <InputText label='Nombre' placeholder='Ingresa nombre' value={ name } onChange={ onChangeName } maxLength={ 70 } />
                </div>
                <div className='col-4'>
                <Select label="Tipo" options={ catStatus } value={ status } onChange={ onChangeStatus } />
                </div>
            </div>
            <div className="row text-start">
                <div className='col-12'>
                <TextArea label='Descripci&oacute;n' placeholder='Agrega alguna descripci&oacute;n al proyecto' value={ description } onChange={ onChangeDesc } />
                </div>
            </div>
            <div className="row text-start">
                <div className='col-3'>
                <InputText label='Creado por' value={ createdBy } disabled />
                </div>
                <div className='col-3'>
                <DatePicker label="Fecha" value={ dateCreated } onChange={ (date) => onChangeCreatedDate(date) } />
                </div>
                <div className='col-3'>
                <Select label="Cliente" options={ clients } value={ client } onChange={ onChangeClient } />
                </div>
                <div className='col-3'>
                <Select label="Project Manager" options={ pms } value={ pm } onChange={ onChangePm } />
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

