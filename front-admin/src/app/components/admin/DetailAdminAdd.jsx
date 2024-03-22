import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { InputText } from "../custom/InputText";
import { Select } from "../custom/Select";
import { displayNotification, genericErrorMsg, handleDateStr } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
import 'react-dual-listbox/lib/react-dual-listbox.css';
// import { setCatalogName, setCatalogObj, setCatalogParent } from '../../../store/catalog/catalogSlice';
// import { save, update, getCatalogById } from '../../services/CatalogService';
import { setRole  } from '../../../store/admin/adminSlice';
import { save } from '../../services/RoleService';

export const DetailAdminAdd = () => {
    const { role } = useSelector( state => state.adminReducer );
    const { permissions} = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [name, setValue] = useState('');
    const [description, setDescription] = useState('');

    const onChangeValue = ({ target }) => setValue(target.value);
    const onChangeDescription = ({ target }) => setDescription(target.value);

    const [active, setActive] = useState('');

    
    const isModeEdit = (!permissions.canAdminUsr);
    
    const onSubmit = event => {
        event.preventDefault()
        const data = new FormData(event.target)
        const request = Object.fromEntries(data.entries());
        saveChild(request);
        navigate('/admin');
    }

    const onClickBack = () => {
        if ( (!permissions.canEditEmp && currentTab == 2) || currentTab === 1 ) {
        navigate('/admin')
        } else {
        setCurrentTab(currentTab - 1);
        }
    }

    const renderTabs = () => (//Esto controla los tabs
        <ul className="nav nav-tabs d-flex flex-row-reverse">
        {(<li className="nav-item" onClick={ () => setCurrentTab(2) }>
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
        save(request).then( response => {
            if(response.code && response.code !== 201) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡El registro se ha creado correctamente!', alertType.success);
                dispatch(setRole(request));
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    useEffect(() => {
        // setValue(obj.value)
        // fetchSelects()
    }, [])
    
    const renderDetail = () => {
        return (<div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                    <div className="row text-start">
                        <div className='col-12'>
                            <InputText name='name' label='Rol' placeholder='Escribe el nombre' 
                            // disabled={ obj.value ? true :false} value={ value } required
                            disabled={ isModeEdit} value={ name } required 
                            onChange={ onChangeValue } maxLength={ 255 } />
                        </div>
                    </div>

                    <div className="row text-start">
                        <div className='col-12'>
                            <InputText name='description' label='Descripci&oacute;n' placeholder='Escribe descripción' disabled={ isModeEdit } value={ description } onChange={ onChangeDescription } maxLength={ 255 } />
                        </div>
                    </div>
                    
                    <div className="pt-3 d-flex flex-row-reverse">
                        {permissions.canEditEmp &&(<button type="submit" className="btn btn-primary" >Guardar</button>)}
                        &nbsp;
                        <button type="button" className="btn btn-danger" onClick={ () => navigate(`/admin`) }>{permissions.canAdminUsr ? 'Cancelar' : 'Regresar'}</button>
                    </div>
                </form>
            </div>)
    };

    return (
        <>
        <div className="d-flex d-flex justify-content-center">
            <h3 className="fs-4 card-title fw-bold mb-4">{`Agregar Permisos`}</h3>
        </div>
        {renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='Aplication' recordId={ name } />) }
        </>
    )
}
