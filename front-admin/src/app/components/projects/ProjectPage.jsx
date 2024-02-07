import { useNavigate, useParams } from 'react-router-dom';
import { DetailProject } from './DetailProject';
import { TableApplications } from '../applications/page/TableApplications';
import { useDispatch, useSelector } from 'react-redux';
import { setCurrentAppTab, setCurrentTab, setProject } from '../../../store/project/projectSlice';
import { TableLog } from '../custom/TableLog';
import { displayNotification, formatter, numberToString } from '../../helpers/utils';
import { TableOrders } from '../orders/page/TableOrders';
import { getProjectById } from '../../services/ProjectService';
import { alertType } from '../custom/alerts/types/types';
import { useEffect } from 'react';

export const ProjectPage = () => {

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { id } = useParams();
  const { permissions } = useSelector( state => state.auth );
  const {currentTab, project, paid} = useSelector( state => state.projectReducer );

  const fetchProject = id => {
    getProjectById(id).then( response => {
        if( response.code ) {
            displayNotification(dispatch, response.message, alertType.error);
        } else {
            dispatch(setProject(response));
        }
    }).catch( error => {
        console.log(error);
        displayNotification(dispatch, genericErrorMsg, alertType.error);
    });
  }

  useEffect(() => {
    if( id ) {
      fetchProject(id);
    }
  });
  
  const handleAddApplication = () => {
    dispatch(setCurrentAppTab(1));
    navigate(`/project/${ id }/application/add`);
  }

  const renderAddButton = () => permissions.canCreateProjApp && (
      <div className="d-flex flex-row-reverse p-2">
          <button type="button" className="btn btn-primary" onClick={ handleAddApplication }>
              <span className="bi bi-plus"></span>
          </button>
      </div>
  );

  const onClickBack = () => {
    if ( (!permissions.canEditEmp && currentTab == 2) || currentTab === 1 ) {
      navigate('/home')
    } else {
      dispatch(setCurrentTab(currentTab - 1));
    }
  }

  const renderTabs = () => id && (
    <div className="d-flex flex-row-reverse">
      <ul className="nav nav-tabs">
        <li>
          <button type="button" className="btn btn-link" onClick={ () => onClickBack() }>&lt;&lt; Regresar</button>
        </li>
        {
          permissions.canEditEmp && (
            <li className="nav-item" onClick={ () => dispatch(setCurrentTab(1)) }>
              <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` } aria-current="page">Detalle</a>
            </li>
          )
        }
        <li className="nav-item" onClick={ () => dispatch(setCurrentTab(2)) }>
          <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Aplicaciones</a>
        </li>
        {
          permissions.canAdminOrd && (
            <li className="nav-item" onClick={ () => dispatch(setCurrentTab(3)) }>
              <a className={ `nav-link ${ (currentTab === 3) ? 'active' : '' }` }>Ordenes</a>
            </li>
          )
        }
        <li className="nav-item" onClick={ () => dispatch(setCurrentTab(4)) }>
          <a className={ `nav-link ${ (currentTab === 4) ? 'active' : '' }` }>Historial</a>
        </li>
      </ul>
    </div>
  )

  const title = project && project.key ? `${project.key} ${project.description}` : 'Proyecto nuevo';

  return (
    <div className='px-5'>
      <h4 className="card-title fw-bold">{ title }</h4>
      { project && project.key && currentTab === 3 && (
        <>
          <p className="h4">
            {/* Iva: <span className='text-primary'>{ project.tax }</span> Total: <span className='text-primary'>{ project.total }</span> */}
            Costo del proyecto: <span className='text-primary'>{ formatter.format(project.amount) }</span>
          </p>
          <p className="h4">
            Monto pendiente: <span className='text-danger'>{ formatter.format(project.amount - paid.amount) }</span>
          </p>
        </>
      )}
      { renderTabs() }
      { (currentTab === 2 && id ) && renderAddButton() }
      { 
        currentTab === 2 ? ( <TableApplications projectId = { id } /> ) : ( 
          currentTab === 4 ? (<TableLog tableName={ 'Project' } recordId={ numberToString(id, '') } />) : (
          currentTab === 3 ? (<TableOrders projectId={ id } />) : (<DetailProject />)
          ) 
        )
      }
    </div>
  )

}
