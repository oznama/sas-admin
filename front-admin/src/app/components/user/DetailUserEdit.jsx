import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { Select } from "../custom/Select";
import { InputText } from "../custom/InputText";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
// import { setCatalogName, setCatalogObj, setCatalogParent } from '../../../store/catalog/catalogSlice';
import { setRole  } from '../../../store/admin/adminSlice';
import { update} from "../../services/UserService";
import { getRolesS } from "../../services/RoleService";
import { getEmployeeById } from "../../services/EmployeeService";

export const DetailUserEdit = () => {
    //const { role } = useSelector( state => state.adminReducer );
    // const { name } = useParams();

    
    const { employeesObj } = useSelector( state => state.userReducer );
    console.log('slice',employeesObj);
    const { permissions} = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const onChangeName = ({ target }) => setName(target.value);
    const [role, setRoles] = useState([]);

    const [roleV, setRoleV] = useState(employeesObj ? employeesObj.role.id:'');

    const onChangeRoleV = ({ target }) => setRoleV(target.value);



    //----------------------------Seccion de role, compañia y empleado 

    const getEmployee = () => {
        getEmployeeById(employeesObj.employee.id).then( response => {
            if( response.code ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setName(response.name+' '+response.surname);
            }
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const fetchSelects = () => {

        getRolesS().then( response => {
                setRoles(response);
        }).catch( error => {
            console.log(error);
        });
    };

    // const rolesOptions = role.map(role => ({
    //     value: role.id, // id
    //     label: role.name // nombre
    // }));
    //----------------------------Seccion de compañia y empleado
    
    // const isModeEdit = (( !permissions.canAdminUsr) || ( !active ));
    const isModeEdit = (!employeesObj.active);
    
    const onSubmit = event => {
        event.preventDefault()
        const data = new FormData(event.target)
        const request = Object.fromEntries(data.entries());
        // request['id'] = employeesObj.id;
        // console.log('request', request);
        updateUser(request);
        //dispatch(setRole(request));
        navigate('/users');
    }

    const onClickBack = () => {
        if ( (!permissions.canAdminUsr && currentTab == 2) || currentTab === 1 ) {
        navigate('/users')
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

    const updateUser = request => {
        update(employeesObj.id, request).then( response => {
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

    useEffect(() => {
        fetchSelects();
        getEmployee();
    }, [employeesObj, name ])
    
    const renderDetail = () => {
        return (<div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                    <div className="row text-start">
                        <div className='col-6'>
                            <InputText name='name' label='Nombre' placeholder='Escribe el nombre' disabled={ true } value={ name } required onChange={ onChangeName } maxLength={ 50 } />
                        </div>
                    </div>
                    <div className="row text-start">
                        <div className='col-12'>
                            <Select name="role" label="Roles" disabled={ isModeEdit } options={ role } value={ roleV } onChange={ onChangeRoleV } />
                        </div>
                    </div>
                    <div className="pt-3 d-flex flex-row-reverse">
                        {permissions.canAdminUsr &&(<button type="submit" className="btn btn-primary" >Guardar</button>)}
                        &nbsp;
                        <button type="button" className="btn btn-danger" onClick={ () => navigate(`/users`) }>{permissions.canAdminUsr ? 'Cancelar' : 'Regresar'}</button>
                    </div>
                </form>
            </div>)
    };

    return (
        <>
        <div className="d-flex d-flex justify-content-center">
            <h3 className="fs-4 card-title fw-bold mb-4">{`Edicion de Usuarios`}</h3>
        </div>
        {renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='User' recordId={ employeesObj.id } />) }
        </>
    )
}
