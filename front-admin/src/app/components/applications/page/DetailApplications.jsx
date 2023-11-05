import { useState } from 'react';
import { InputText } from '../../custom/InputText';
import { Select } from '../../custom/Select';
import { DatePicker } from '../../custom/DatePicker';
import { useNavigate } from 'react-router-dom';

export const DetailApplications = () => {
  const navigate = useNavigate();
  const [aplication, setAplication] = useState('-1');
  const [amount, setAmount] = useState('$0');
  const [status, setStatus] = useState('1');
  const [leader, setLeader] = useState('-1');
  const [developer, setDeveloper] = useState('-1');
  const [hours, setHours] = useState('0');
  const [startDate, setStartDate] = useState(new Date());
  const [designerDate, setDesignerDate] = useState(new Date());
  const [buildDate, setBuildDate] = useState(new Date());

  const [catStatus, setCatStatus] = useState([
    { id: '1', value: 'Nuevo' },
    { id: '2', value: 'Especificación técnica' },
    { id: '3', value: 'Diseño' },
    { id: '4', value: 'Desarrollo' },
    { id: '5', value: 'Pendiente de pago' },
    { id: '6', value: 'Cerrado' },
  ]);
  const [catAplications, setCatApliations] = useState([
    { id: '1', value: 'PMT' },
    { id: '2', value: 'SIA' },
    { id: '3', value: 'SAC2' },
    { id: '4', value: 'RED' },
    { id: '5', value: 'CTM' },
  ]);
  const [catEmployees, setCatEmployees] = useState([
    { id: '1', value: 'Alejandra Labra' },
    { id: '2', value: 'Alvaro Mendoza' },
    { id: '3', value: 'Angel Calzada' },
    { id: '4', value: 'Gerardo Lopez' },
    { id: '5', value: 'Juan Baños' },
    { id: '6', value: 'Oziel Naranjo' },
  ])

  const onChangeAplication = ({ target }) => setAplication(target.value);
  const onChangeAmount = ({ target }) => setAmount(target.value);
  const onChangeStatus = ({target }) => setStatus(target.value);
  const onChangeLeader = ({ target }) => setLeader(target.value);
  const onChangeDeveloper = ({ target }) => setDeveloper(target.value);
  const onChangeHours = ({ target }) => setHours(target.value);
  const onChangeStartDate = (date) => setStartDate(date);
  const onChangeDesignerDate = (date) => setDesignerDate(date);
  const onChangeBuildDate = (date) => setBuildDate(date);

  const onSubmit = formData => {
    console.log(formData);
  }

  return (
    <div className='px-5'>
      <h1 className="fs-4 card-title fw-bold mb-4">Aplicacion</h1>
      <form onSubmit={ onSubmit }>
        <div className='text-center'>
          <div className="row text-start">
            <div className='col-4'>
              <Select label="Aplicaci&oacute;n" options={ catAplications } value={ aplication } onChange={ onChangeAplication } />
            </div>
            <div className='col-4'>
              <InputText label='Monto' placeholder='Ingresa monto' value={ amount } onChange={ onChangeAmount } maxLength={ 10 } />
            </div>
            <div className='col-4'>
              <Select label="Status" options={ catStatus } value={ status } onChange={ onChangeStatus } />
            </div>
          </div>
          <div className="row text-start">
            <div className='col-4'>
            <Select label="L&iacute;der" options={ catEmployees } value={ leader } onChange={ onChangeLeader } />
            </div>
            <div className='col-4'>
              <Select label="Desarrollador" options={ catEmployees } value={ developer } onChange={ onChangeDeveloper } />
            </div>
            <div className='col-4'>
              <InputText label='Horas' placeholder='Ingresa monto' value={ hours } onChange={ onChangeHours } maxLength={ 10 } />
            </div>
          </div>

          <div className="row text-start">
            <div className='col-4'>
            <DatePicker label="Inicio" value={ startDate } onChange={ (date) => onChangeStartDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker label="Analisis y dise&ntilde;o" value={ designerDate } onChange={ (date) => onChangeDesignerDate(date) } />
            </div>
            <div className='col-4'>
              <DatePicker label="Construcci&oacute;n" value={ buildDate } onChange={ (date) => onChangeBuildDate(date) } />
            </div>
          </div>
        </div>
        <div className="pt-3 d-flex flex-row-reverse">
            <button type="submit" className="btn btn-primary">Guardar</button>
            &nbsp;
            <button type="button" className="btn btn-danger" onClick={ () => navigate(`/project/1/edit`) }>Cancelar</button>
        </div>
      </form>
    </div>
  )
}
