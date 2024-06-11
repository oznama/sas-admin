import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { DatePicker } from '../../custom/DatePicker';
import { displayNotification, handleDateStr, numberToString, removeCurrencyFormat } from '../../../helpers/utils';
import { Select } from '../../custom/Select';
import { alertType } from '../../custom/alerts/types/types';
import { updateApplication } from '../../../services/ProjectService';
import { getCatalogChilds } from '../../../services/CatalogService';

export const FormApplication = ({
    application,
    onCancelModal
}) => {

    const dispatch = useDispatch();

    const [catStatus, setCatStatus] = useState([]);

    const [designDate, setDesignDate] = useState(handleDateStr(application.designDate));
    const [designStatus, setDesignStatus] = useState(numberToString(application.designStatus,''));
    const [developmentDate, setDevelopmentDate] = useState(handleDateStr(application.developmentDate));
    const [developmentStatus, setDevelopmentStatus] = useState(numberToString(application.developmentStatus,''));
    const [endDate, setEndDate] = useState(handleDateStr(application.endDate));
    const [endStatus, setEndStatus] = useState(numberToString(application.endStatus,''));

    const onChangeDesignDate = date => setDesignDate(date);
    const onChangeDevelopmentDate = date => setDevelopmentDate(date);
    const onChangeEndDate = date => setEndDate(date);

    const onChangeDesignStatus = ({ target }) => setDesignStatus(target.value);
    const onChangeDevelopmentStatus = ({ target }) => setDevelopmentStatus(target.value);
    const onChangeEndStatus = ({ target }) => setEndStatus(target.value);

    const fetchSelects = () => {
        getCatalogChilds(1000000010).then( response => {
          setCatStatus(response.filter( cat => cat.status === 2000100001 ));
        }).catch( error => {
          console.log(error);
        });
    };

    const onSave = () => {
        updateApplication(application.id, {
            ...application,
            'amount': removeCurrencyFormat(application.amount),
            'tax': removeCurrencyFormat(application.tax),
            'total': removeCurrencyFormat(application.total),
            'designStatus': Number(designStatus),
            'developmentStatus': Number(developmentStatus),
            'endStatus': Number(endStatus)
        }).then( response => {
            if(response.code && response.code !== 201) {
              displayNotification(dispatch, response.message, alertType.error);
            } else {
              displayNotification(dispatch, 'Estatus actualizados correctamente!', alertType.success);
              onCancelModal(true);
            }
        }).catch(error => {
            console.log(error)
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    useEffect(() => {
        fetchSelects();
    }, [])

    return (
        <div className='p-5 bg-white rounded-3'>
            <h2 className='text-center'>Actualizaci&oacute;n de estatus</h2>
            <h4>{ `Clave: ${application.projectKey}` }</h4>
            <h4>{ `Aplicación: ${application.application}` }</h4>
                
            <div className="row text-start py-2">
                <div className='col-6'>
                    <DatePicker name="designDate" label="Analisis y dise&ntilde;o" value={ designDate } onChange={ (date) => onChangeDesignDate(date) } disabled={true} />
                </div>
                <div className='col-6'>
                    <Select name="designDateStatus" label="Estatus" options={ catStatus } value={ designStatus } required onChange={ onChangeDesignStatus } />
                </div>
            </div>
            <div className="row text-start">
                <div className='col-6'>
                    <DatePicker name="developmentDate" label="Construcci&oacute;n" value={ developmentDate } onChange={ (date) => onChangeDevelopmentDate(date) } disabled={true} />
                </div>
                <div className='col-6'>
                    <Select name="developmentDateStatus" label="Estatus" options={ catStatus } value={ developmentStatus } required onChange={ onChangeDevelopmentStatus } />
                </div>
            </div>
            <div className="row text-start">
                <div className='col-6'>
                    <DatePicker name="endDate" label="Cierre" value={ endDate } onChange={ (date) => onChangeEndDate(date) } disabled={true} />
                </div>
                <div className='col-6'>
                    <Select name="endDateStatus" label="Estatus" options={ catStatus } value={ endStatus } required onChange={ onChangeEndStatus } />
                </div>
            </div>
            
            <div className="pt-3 d-flex justify-content-between">
                <button type="button" className="btn btn-danger" onClick={ () => onCancelModal(false) }>Cancelar</button>
                <button type="button" className="btn btn-primary" onClick={ () => onSave() }>Guardar</button>
            </div>
        </div>
    )
}
