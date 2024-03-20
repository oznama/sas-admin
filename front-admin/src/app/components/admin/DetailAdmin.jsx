import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { InputText } from "../custom/InputText";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
import 'react-dual-listbox/lib/react-dual-listbox.css';
import DualListBox from "react-dual-listbox";
// import { setCatalogName, setCatalogObj, setCatalogParent } from '../../../store/catalog/catalogSlice';
// import { save, update, getCatalogById } from '../../services/CatalogService';
import { setRole  } from '../../../store/admin/adminSlice';
import { update } from '../../services/RoleService';
import { getRolesPermissions, getRolePermissionsByRoleId, savePermissions, deleteLogic} from "../../services/RolesPermissionsService";

export const DetailAdmin = () => {
    const { role } = useSelector( state => state.adminReducer );
    // const { name } = useParams();
    const { permissions} = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [name, setName] = useState(role.name);
    const [description, setDescription] = useState(role.description);
    const [selected, setSelected] = useState([]);
    
    const [totalPermissionByRoles, setTotalPermissionByRoles] = useState(0);  
    const [permissionByRole, setPermissionByRoles] = useState([]);
    const [permissionByRoleResp, setPermissionByRolesResp] = useState([]);
    const [totalPermission, setTotalPermission] = useState(0);  
    const [permission, setPermission] = useState([]);

    const onChangeValue = ({ target }) => setName(target.value);
    const onChangeDescription = ({ target }) => setDescription(target.value);

    const [active, setActive] = useState(role.active);

    const options = permission.reduce((acc, role) => {
        // Extraer el primer elemento de la descripción para obtener la etiqueta del grupo
        const groupLabel = role.description.split('-')[0];
        // Verificar si el grupo ya existe en el acumulador
        const existingGroup = acc.find(group => group.label === groupLabel);
        // Si el grupo no existe, agregarlo al acumulador
        if (!existingGroup) {
            acc.push({
                label: groupLabel,
                options: [{ value: role.name, label: role.description }]
            });
        } else {
            // Si el grupo ya existe, agregar la opción al grupo existente
            existingGroup.options.push({ value: role.name, label: role.description });
        }
        
        return acc;
    }, []);


    const renderDualList = () => {
        return (
            <DualListBox
                options={options}
                selected={selected}
                onChange={(newValue) => setSelected(newValue)}
            />
        );
    }
    
    const isModeEdit = ((role.id && !permissions.canAdminUsr) || (role.id && !active ));
    
    const onSubmit = event => {
        event.preventDefault()
        // const data = new FormData(event.target)
        // const request = Object.fromEntries(data.entries());
        deleteMissingIds();
        printMissingSelected();
        //updateRole(request);
        navigate('/admin');
    }
    
    const selectedNames = selected; // Copia los nombres seleccionados

    const selectedIds = permissionByRoleResp
        .filter(item => selectedNames.includes(item.name))
        .map(item => item.idRolePermission);

    const deleteMissingIds = () => {
        permissionByRoleResp.forEach(item => {
            if (!selectedIds.includes(item.idRolePermission)) {
                console.log('dato eliminao: ',item.idRolePermission);
                deleteChild(item.idRolePermission);
            }
        });
    };

    const permissionNames = permissionByRoleResp.map(item => item.name);

    const printMissingSelected = () => {
        selected.forEach(name => {
            if (!permissionNames.includes(name)) {
                const dsave = permission.find(item => item.name === name);
                const dato = '{"permissionId":'+dsave.id+', "roleId":'+role.id+'}';
                const datoJ = JSON.parse(dato);
                console.log('Dato agregado: ', datoJ);
                saveChild(datoJ);
            }
        });
    };


    const deleteChild = id => {
        for (let index = 0; index < permissionByRoleResp.length; index++) {
            deleteLogic(id).then( response => {
                if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
                } else {
                displayNotification(dispatch, '¡Registro eliminado correctamente!', alertType.success);
                fetchRoles('');
                }
            }).catch(error => {
                console.log(error);
                displayNotification(dispatch, genericErrorMsg, alertType.error);
            });
        }
        
    }

    const onClickBack = () => {
        if ( (!permissions.canAdminUsr && currentTab == 2) || currentTab === 1 ) {
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

    const updateRole = request => {
        update(role.id, request).then( response => {
            if(response.code && response.code !== 201) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡El registro se ha actualizado correctamente!', alertType.success);
                dispatch(setRole(request));
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const saveChild = request => {
        savePermissions(request).then( response => {
            if(response.code && response.code !== 201) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, '¡El registro se ha creado correctamente!', alertType.success);
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const fetchRoles = () => {
        getRolePermissionsByRoleId(role.id)
        .then( response => {
            const idsArray = response.map( item => item.name );
            setSelected(idsArray);
            setPermissionByRoles(idsArray);
            setPermissionByRolesResp(response);
            setTotalPermissionByRoles(response.totalElements);
        }).catch( error => {
            console.log(error);
        });
    }

    const fetchAllRoles = () =>{
        getRolesPermissions()
        .then( response => {
            setPermission(response);
            setTotalPermission(response.totalElements);
        }).catch( error => {
            console.log(error);
        })
    }
    
    useEffect(() => {
        fetchRoles();
        fetchAllRoles();
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
                    
                {renderDualList()}
                    <div className="pt-3 d-flex flex-row-reverse">
                        {permissions.canAdminUsr &&(<button type="submit" className="btn btn-primary" >Guardar</button>)}
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
