import { useState } from 'react';
import { InputText } from '../custom/InputText';
import { Select } from '../custom/Select';
import './project.main.css';
import { TextArea } from '../custom/TextArea';
import { DatePicker } from '../custom/DatePicker';
import { render } from 'react-dom';
import { useNavigate } from 'react-router-dom';

export const ProjectPage = () => {

  const navigate = useNavigate();

  const [pKey, setPKey] = useState('');
  const [name, setName] = useState('');
  const [status, setStatus] = useState('1');
  const [description, setDescription] = useState('');
  const [createdBy, setCreatedBy] = useState('Selene Pascali');
  const [dateCreated, setDateCreated] = useState('12/02/2012')
  const [client, setClient] = useState('-1');
  const [owner, setOwner] = useState('-1');
  const [ownerDate, setOwnerDate] = useState(new Date());
  const [designer, setDesigner] = useState('-1');
  const [designerDate, setDesignerDate] = useState(new Date());
  const [leader, setLeader] = useState('-1');
  const [leaderDate, setLeaderDate] = useState(new Date());
  const [catStatus, setCatStatus] = useState([
    { id: '1', value: 'Nuevo' },
    { id: '2', value: 'Especificación técnica' },
    { id: '3', value: 'Diseño' },
    { id: '4', value: 'Desarrollo' },
    { id: '5', value: 'Pendiente de pago' },
    { id: '6', value: 'Cerrado' },
  ]);
  const [clients, setClients] = useState([
    { id: '1', value: 'SAS' },
    { id: '2', value: 'Prosa' },
    { id: '3', value: 'Multiva' },
  ]);
  const [owners, setOwners] = useState([
    { id: '1', value: 'Jaime Carreño' },
    { id: '2', value: 'Rogelio Zavaleta' },
  ]);
  const [designers, setDesigners] = useState([
    { id: '1', value: 'Israel Chino' },
  ])
  const [leaders, setLeaders] = useState([
    { id: '1', value: 'Gerardo Lopez' },
    { id: '2', value: 'Juan Baños' },
    { id: '3', value: 'Oziel Naranjo' },
  ])

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
  const onChangeClient = ({ target }) => setClient(target.value);
  const onChangeOwner = ({ target }) => setOwner(target.value);
  const onChangeDesigner = ({ target }) => setDesigner(target.value);
  const onChangeLeader = ({ target }) => setLeader(target.value);
  const onChangeDate = ({ target }) => setDateCreated(target.value);
  const onChangeOwnerDate = date => setOwnerDate(date);
  const onChangeDesignerDate = date => setDesignerDate(date);
  const onChangeLeaderDate = (date) => setLeaderDate(date);

  const renderBackButton = () => (
    <div className="d-flex">
        <button type="button" className="btn btn-link" onClick={ () => navigate(`/home`) }>&#60;&#60; Regresar</button>
    </div>
  );

  return (
    <div className='w-100 p-5'>
      <div className="d-flex justify-content-center">
        <h1 className="fs-4 card-title fw-bold mb-4">Proyecto Nuevo</h1>
      </div>
      { renderBackButton() }
      <form>
        <div className='text-center'>
          <div className="row text-start">
            <div className='col-2'>
              <InputText label='Clave' placeholder='Ingresa clave' value={ pKey } onChange={ onChangePKey } maxLength={ 12 } />
            </div>
            <div className='col-6'>
              <InputText label='Nombre' placeholder='Ingresa nombre' value={ name } onChange={ onChangeName } maxLength={ 70 } />
            </div>
            <div className='col-4'>
              <Select label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-12'>
              <TextArea label='Descripci&oacute;n' placeholder='Agrega alguna descripci&oacute;n al proyecto' value={ description } onChange={ onChangeDesc } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-4'>
              <InputText label='Creado por' value={ createdBy } disabled />
            </div>
            <div className='col-4'>
              <InputText label='Fecha' value={ dateCreated } disabled />
            </div>
            <div className='col-4'>
              <Select label="Cliente" options={ clients } value={ client } onChange={ onChangeClient } />
            </div>
          </div>

          <div className="row text-start">
            <div className='col-2'>
              <Select label="Owner" options={ owners } value={ owner } onChange={ onChangeOwner } />
            </div>
            <div className='col-2'>
              <DatePicker label="Fecha" value={ ownerDate } onChange={ (date) => onChangeOwnerDate(date) } />
            </div>
            <div className='col-2'>
              <Select label="Dise&ntilde;ador" options={ designers } value={ designer } onChange={ onChangeDesigner } />
            </div>
            <div className='col-2'>
              <DatePicker label="Fecha" value={ designerDate } onChange={ (date) => onChangeDesignerDate(date) } />
            </div>
            <div className='col-2'>
              <Select label="L&iacute;der desarrollo" options={ leaders } value={ leader } onChange={ onChangeLeader } />
            </div>
            <div className='col-2'>
              <DatePicker label="Fecha" value={ leaderDate } onChange={ (date) => onChangeLeaderDate(date) } />
            </div>
          </div>

        </div>
      </form>
    </div>
  )


}
