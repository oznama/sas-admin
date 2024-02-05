import React, { useState } from 'react'
import { useDispatch } from 'react-redux';
import { setModalChild } from '../../../../store/modal/modalSlice';
import { displayNotification, genericErrorMsg } from '../../../helpers/utils';
import { alertType } from '../../custom/alerts/types/types';
import { save, update } from '../../../services/CatalogService';
import { InputText } from '../../custom/InputText';

export const FormApplication = ({
    catalogChild,
    onCancelModal
}) => {

    const dispatch = useDispatch();

    const [value, setValue] = useState(catalogChild && catalogChild.value ? catalogChild.value : '');
    const [description, setDescription] = useState(catalogChild && catalogChild.description ? catalogChild.description : '');

    const onChangeCValue = ({ target }) => setValue(target.value);
    const onChangeDescription = ({ target }) => setDescription(target.value);

    const onSubmit = event => {
        event.preventDefault()
        const data = new FormData(event.target)
        const request = Object.fromEntries(data.entries());
        request.catalogParent = 1000000004;
        if (catalogChild){
            updateChild(request);
        }else{
            saveChild(request);
        }
        dispatch( setModalChild(null) )
    }

    const saveChild = request => {
        save(request).then( response => {
            if(response.code && response.code !== 201) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡El registro se ha creado correctamente!', alertType.success);
                onCancelModal();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const updateChild = request => {
        update(catalogChild.id, request).then( response => {
            if(response.code && response.code !== 201) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡El registro se ha actualizado correctamente!', alertType.success);
                onCancelModal();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    return (
        <div className='p-5 bg-white rounded-3'>
            <h1>Aplicaci&oacute;n nueva</h1>
            <form className="needs-validation" onSubmit={ onSubmit }>
                
                <div className="row text-start">
                    <div className='col-12'>
                        <InputText name='value' label='Nombre:*' placeholder='Escribe el nombre' value={ value } required onChange={ onChangeCValue } maxLength={ 250 } />
                    </div>
                </div>
                <div className="row text-start">
                    <div className='col-12'>
                        <InputText name='description' label='Descripcion:' placeholder='Escribe la descripcion' value={ description } required onChange={ onChangeDescription } maxLength={ 250 } />
                    </div>
                </div>
                <div className="pt-3 d-flex justify-content-between">
                    <button type="button" className="btn btn-danger" onClick={ () => onCancelModal() }>Cancelar</button>
                    <button type="submit" className="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    )
}
