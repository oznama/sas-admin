import { useNavigate } from 'react-router-dom';
import { DetailProject } from './DetailProject';
import { TableApplications } from '../applications/page/TableApplications';
import { useDispatch, useSelector } from 'react-redux';
import { cleanProjectApplication, setCurrentTab } from '../../../store/project/projectSlice';
import { Alert } from '../custom/alerts/page/Alert';

export const ProjectPage = () => {

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const {project, currentTab} = useSelector( state => state.projectReducer );
  
  const handleAddApplication = () => {
    dispatch(cleanProjectApplication(project.id));
    navigate(`/project/${ project.id }/application/add`);
  }

  const renderAddButton = () => (
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
    </ul>
  )

  return (
    <>
      <div className='px-5'>
        <div className='d-flex justify-content-between'>
          <h1 className="fs-4 card-title fw-bold mb-4">Proyecto Nuevo</h1>
          { ( project.id ) ? renderTabs() : null }
        </div>
        <Alert />
        { (currentTab === 2 && project.id ) ? renderAddButton() : null }
        { (currentTab === 2) ? ( <TableApplications projectId = { project.id } /> ) : ( <DetailProject /> )  }
      </div>
    </>
  )

}
