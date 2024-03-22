import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { DatePicker } from '../../custom/DatePicker';
import { displayNotification, handleDateStr } from '../../../helpers/utils';
import { Select } from '../../custom/Select';
import { alertType } from '../../custom/alerts/types/types';

const catStatus = [
    {
        id: '1',
        value: 'Pendiente'
    },
    {
        id: '2',
        value: 'En proceso'
    },
    {
        id: '3',
        value: 'Entregado'
    },
]

export const FormPending = ({
    application,
    onCancelModal
}) => {

    const dispatch = useDispatch();

    const [designDate, setDesignDate] = useState(handleDateStr(application.designDate));
    const [designDateStatus, setDesignDateStatus] = useState('1');
    const [developmentDate, setDevelopmentDate] = useState(handleDateStr(application.developmentDate));
    const [developmentDateStatus, setDevelopmentDateStatus] = useState('1');
    const [endDate, setEndDate] = useState(handleDateStr(application.endDate));
    const [endDateStatus, setEndDateStatus] = useState('1');

    const onChangeDesignDate = date => setDesignDate(date);
    const onChangeDevelopmentDate = date => setDevelopmentDate(date);
    const onChangeEndDate = date => setEndDate(date);

    const onChangeDesignDateStatus = ({ target }) => setDesignDateStatus(target.value);
    const onChangeDevelopmentDateStatus = ({ target }) => setDevelopmentDateStatus(target.value);
    const onChangeEndDateStatus = ({ target }) => setEndDateStatus(target.value);

    const onSave = () => {
        displayNotification(dispatch, 'Estatus actualizados correctamente!', alertType.success);
        onCancelModal();
    }

    return (
        <div className='p-5 bg-white rounded-3'>
            <h2>{ `Actualización de estatus para ${application.application}` }</h2>
                
            <div className="row text-start">
                <div className='col-6'>
                    <DatePicker name="designDate" label="Analisis y dise&ntilde;o" value={ designDate } onChange={ (date) => onChangeDesignDate(date) } disabled={true} />
                </div>
                <div className='col-6'>
                    <Select name="designDateStatus" label="Estatus" options={ catStatus } value={ designDateStatus } required onChange={ onChangeDesignDateStatus } />
                </div>
            </div>
            <div className="row text-start">
                <div className='col-6'>
                    <DatePicker name="developmentDate" label="Construcci&oacute;n" value={ developmentDate } onChange={ (date) => onChangeDevelopmentDate(date) } disabled={true} />
                </div>
                <div className='col-6'>
                    <Select name="developmentDateStatus" label="Estatus" options={ catStatus } value={ developmentDateStatus } required onChange={ onChangeDevelopmentDateStatus } />
                </div>
            </div>
            <div className="row text-start">
                <div className='col-6'>
                    <DatePicker name="endDate" label="Cierre" value={ endDate } onChange={ (date) => onChangeEndDate(date) } disabled={true} />
                </div>
                <div className='col-6'>
                    <Select name="endDateStatus" label="Estatus" options={ catStatus } value={ endDateStatus } required onChange={ onChangeEndDateStatus } />
                </div>
            </div>
            
            <div className="pt-3 d-flex justify-content-between">
                <button type="button" className="btn btn-danger" onClick={ () => onCancelModal() }>Cancelar</button>
                <button type="button" className="btn btn-primary" onClick={ () => onSave() }>Guardar</button>
            </div>
        </div>
    )
}
