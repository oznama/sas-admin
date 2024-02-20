import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { displayNotification, genericErrorMsg, handleDateStr, numberToString } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
import { setCatalogObj, setCatalogParent } from '../../../store/catalog/catalogSlice';
import { save, update, getCatalogById } from '../../services/CatalogService';
import { DatePicker } from "../custom/DatePicker";

export const DetailCatalogConexion = () => {
    const { catalogParent } = useSelector( state => state.catalogReducer );
    const { obj } = useSelector( state => state.catalogReducer );
    const [title, setTitle] = useState('');
    const [type, setType] = useState('');
    
    const [startDate, setStartDate] = useState();
    
    const {name} = useParams();
    const { permissions} = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const [value, setValue] = useState('');
    const [description, setDescription] = useState(obj && obj.description ? obj.description : '');

    const onChangeValue = ({ target }) => setValue(target.value);
    const onChangeDescription = ({ target }) => setDescription(target.value);
    const onChangeStartDate = date => setValue(date);

    const [active, setActive] = useState('');
    
    const isModeEdit = ((obj.id && !permissions.canEditCat) || (obj.id && !active ));
    console.log('obj: '+obj+' permissions: '+!permissions.canEditCat+' active:'+(obj.id && !active )+" idModeEdit: "+isModeEdit)
    
    const onSubmit = event => {
        event.preventDefault()
        const data = new FormData(event.target)
        const request = Object.fromEntries(data.entries());
        console.log(request);
        request.catalogParent = catalogParent;
        if (obj.value){
            updateChild(request);
            
        }else{
            saveChild(request);
        }
        navigate('/'+type);
    }

    const onClickBack = () => {
        if ( (!permissions.canEditEmp && currentTab == 2) || currentTab === 1 ) {
        navigate('/'+type)
        } else {
        setCurrentTab(currentTab - 1);
        }
    }

    const renderTabs = () => (//Esto controla los tabs
        <ul className="nav nav-tabs d-flex flex-row-reverse">
        {name && (<li className="nav-item" onClick={ () => setCurrentTab(2) }>
            <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
        </li>)} 
        <li className="nav-item" onClick={ () => setCurrentTab(1) }>
            <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
        </li>
        <li>
            <button type="button" className="btn btn-link" onClick={ () => onClickBack() }>&lt;&lt; Regresar</button>
        </li>
        </ul>
    )

    const saveChild = request => {
        request['value']=value;
        save(request).then( response => {
            if(response.code && response.code !== 201) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡El registro se ha creado correctamente!', alertType.success);
                dispatch(setCatalogObj(request));
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const updateChild = request => {
        request['value']=value;
        console.log('Se esta intentando actualizar: '+JSON.stringify(request, null, 2))
        update(obj.id, request).then( response => {
            if(response.code && response.code !== 201) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡El registro se ha actualizado correctamente!', alertType.success);
                dispatch(setCatalogObj(request));
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }
    
    useEffect(() => {
        console.log('El catalogo parent es: '+catalogParent);
        if (catalogParent == 1000000005) {
            setTitle('Puestos de trabajo');
            setType('role');
        } else if (catalogParent == 1000000009) {
            setTitle('Tipos de compañia');
            setType('companyType');
        } else {
            setTitle('Dias Feriados');
            setType('days');
        }
        console.log("El valor de type es: "+type);
        if (catalogParent===1000000007) {
            console.log("WELCOME TO DAYS");
            // Encuentra el índice de la letra "T" en la cadena value
            // const indiceT = objeto.value.indexOf('T');

            // // Recorta la cadena desde el índice de la letra "T" hasta el final
            // const valorRecortado = objeto.value.substring(indiceT);
        }else if(obj.value){
            // setValue(obj.value)
        }

        // fetchSelects()
        if(obj.status == 2000100001 || obj == {}){
            setActive(true);
        }else{
            setActive(false);
        }
    }, [])
    
    const renderDetail = () => {
        return (<div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                    {(type != 'days') &&(<div className="row text-start">
                        <div className='col-12'>
                            <InputText name='value' label='Nombre' placeholder='Escribe el nombre' disabled={ obj.value ? true :false} value={ value } required onChange={ onChangeValue } maxLength={ 255 } />
                        </div>
                    </div>)}
                    {(type == 'days') &&(<div className='col-6'>
                        <DatePicker name="value" label="Inicio" disabled={isModeEdit } value={ value } required onChange={ (date) => onChangeStartDate(date) } />
                    </div>)}
                    <div className="row text-start">
                        <div className='col-12'>
                            <InputText name='description' label='Descripci&oacute;n' placeholder='Escribe descripción' disabled={ isModeEdit } value={ description } onChange={ onChangeDescription } maxLength={ 255 } />
                        </div>
                    </div>
                    
                    <div className="pt-3 d-flex flex-row-reverse">
                        {permissions.canEditEmp &&(<button type="submit" className="btn btn-primary" >Guardar</button>)}
                        &nbsp;
                        <button type="button" className="btn btn-danger" onClick={ () => navigate(`/`+type) }>{permissions.canEditEmp ? 'Cancelar' : 'Regresar'}</button>
                    </div>
                </form>
            </div>)
    };

    return (
        <>
        <div className="d-flex d-flex justify-content-center">
            <h3 className="fs-4 card-title fw-bold mb-4">{`Agregar ${title}`}</h3>
        </div>
        {renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Aplication' recordId={ value } />) }
        </>
    )
}
