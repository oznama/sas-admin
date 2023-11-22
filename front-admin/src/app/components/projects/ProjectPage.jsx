import { useNavigate, useParams } from 'react-router-dom';
import { DetailProject } from './DetailProject';
import { TableApplications } from '../applications/page/TableApplications';
import { useDispatch, useSelector } from 'react-redux';
import { setCurrentTab } from '../../../store/project/projectSlice';
import { Alert } from '../custom/alerts/page/Alert';
import { TableLog } from '../custom/TableLog';
import { numberToString } from '../../helpers/utils';

export const ProjectPage = () => {

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { id } = useParams();
  const { permissions } = useSelector( state => state.auth );
  const {currentTab} = useSelector( state => state.projectReducer );
  
  const handleAddApplication = () => {
    navigate(`/project/${ id }/application/add`);
  }

  const renderAddButton = () => permissions.canCreateProjApp && (
      <div className="d-flex flex-row-reverse p-2">
          <button type="button" className="btn btn-primary" onClick={ handleAddApplication }>
              <span className="bi bi-plus"></span>
          </button>
      </div>
  );

  const renderTabs = () => (
    <ul className="nav nav-tabs">
      <li className="nav-item" onClick={ () => dispatch(setCurrentTab(1)) }>
        <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` } aria-current="page">Detalle</a>
      </li>
      <li className="nav-item" onClick={ () => dispatch(setCurrentTab(2)) }>
        <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Aplicaciones</a>
      </li>
      <li className="nav-item" onClick={ () => dispatch(setCurrentTab(3)) }>
        <a className={ `nav-link ${ (currentTab === 3) ? 'active' : '' }` }>Historial</a>
      </li>
    </ul>
  )

  return (
    <>
      <div className='px-5'>
        <div className='d-flex justify-content-between'>
          <h1 className="fs-4 card-title fw-bold mb-4">Proyecto Nuevo</h1>
          { ( id ) ? renderTabs() : null }
        </div>
        <Alert />
        { (currentTab === 2 && id ) ? renderAddButton() : null }
        { 
          (currentTab === 2) ? ( <TableApplications projectId = { id } /> ) : ( 
            (currentTab === 3) ? (<TableLog tableName={ 'Project' } recordId={ numberToString(id, '') } />) : (<DetailProject projectId={ id } />) 
          )
        }
      </div>
    </>
  )

}
