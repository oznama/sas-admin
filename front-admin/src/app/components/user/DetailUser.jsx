import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { Select } from "../custom/Select";
import { displayNotification, genericErrorMsg } from "../../helpers/utils";
import { alertType } from "../custom/alerts/types/types";
import { TableLog } from "../custom/TableLog";
// import { setCatalogName, setCatalogObj, setCatalogParent } from '../../../store/catalog/catalogSlice';
// import { save, update, getCatalogById } from '../../services/CatalogService';
import { setEmployeesObj  } from '../../../store/user/userSlice';
import { save} from "../../services/UserService";
import { getCompanySelect } from "../../services/CompanyService";
import { getRolesS } from "../../services/RoleService";
import { getEmployees, getEmployessByCompanyId } from "../../services/EmployeeService";

export const DetailUser = () => {
    //const { role } = useSelector( state => state.adminReducer );
    // const { name } = useParams();
    const { permissions} = useSelector( state => state.auth );
    const [currentTab, setCurrentTab] = useState(1);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [name, setName] = useState('');
    const [company, setCompany] = useState();
    const [companyIDs, setCompanyIDs] = useState('');
    const [companies, setCompanies] = useState([]);
    const [role, setRoles] = useState([]);
    const [roleV, setRoleV] = useState([]);

    const onChangeRoleV = ({ target }) => setRoleV(target.value);
    const [leader, setLeader] = useState('');
    const onChangeLeader = ({ target }) => setLeader(target.value);

    const [active, setActive] = useState('');
    const [catEmployees, setCatEmployees] = useState([]);

    //----------------------------Seccion de role, compañia y empleado 
    const onChangeCompany = ({ target }) => {
        fetchLeaders(target.value);
        setCompany(target.value);
        const companySelected = companies.find( c => { //Cuando selecciona una compañia
            const isFind = c.id === Number(target.value)
            // console.log('isFind???', isFind, 'companyId', c.id, 'companySelected', Number(target.value));
            return isFind;
        });
        setCompanyIDs( companySelected ? `> ${companySelected.value}` : '' );
        // console.log('CompanyID: ',companyIDs)
    }
    
    const fetchLeaders = companyId => {
        getEmployessByCompanyId(companyId).then( response => {
            setCatEmployees(response);
        }).catch( error => {
            console.log(error);
        });
    }

    const fetchSelects = () => {
        getCompanySelect().then( response => {
            setCompanies(response);
        }).catch( error => {
            console.log(error);
        });

        getRolesS().then( response => {
                setRoles(response);
                console.log('role', response);
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
    const isModeEdit = (false);
    
    const onSubmit = event => {
        event.preventDefault()
        const data = new FormData(event.target)
        const request = Object.fromEntries(data.entries());
        // saveUser(request);
        console.log(request);
        const leaderObj = catEmployees.find(element => element.id == request.employeeId);
        request.employee={};
        request.employee.name = leaderObj.value.split(' ')[0];
        // dispatch(setEmployeesObj(request));
        // navigate('/users');
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

    const saveUser = request => {
        save(request).then( response => {
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

    useEffect(() => {
        fetchSelects();
    }, [catEmployees, companyIDs])
    
    const renderDetail = () => {
        return (<div className='d-grid gap-2 col-6 mx-auto'>
                <form className="needs-validation" onSubmit={ onSubmit }>
                    <div className='col-6'>
                        <Select name="companyId" label="Empresa" options={ companies } disabled={ isModeEdit } value={ company } onChange={ onChangeCompany } />
                    </div>
                    <div className='col-6'>
                        <Select name="employeeId" label="Empleado" disabled={ isModeEdit } options={ catEmployees } value={ leader }  onChange={ onChangeLeader } />
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
            <h3 className="fs-4 card-title fw-bold mb-4">{`Creacion de Usuario`}</h3>
        </div>
        {renderTabs() }
        { currentTab === 1 ? renderDetail() : ( <TableLog tableName='User' recordId={ leader } />) }
        </>
    )
}
