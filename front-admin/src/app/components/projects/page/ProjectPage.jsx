import { useNavigate, useParams } from 'react-router-dom';
import { DetailProject } from './DetailProject';
import { useContext, useEffect, useState } from 'react';
import { TableApplications } from '../../applications/page/TableApplications';
import { ProjectContext } from '../context/ProjectContext';
import { ProjectProvider } from '../context/ProjectProvider';

export const ProjectPage = () => {

  const navigate = useNavigate();
  const { getById, project } = useContext( ProjectContext );
  const { id } = useParams();

  const [selectedTab, setSelectedTab] = useState(1);

  useEffect(() => {
    getById(id);
  }, []);

  const handleAddApplication = () => {
      navigate(`/application/add`);
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
      <li className="nav-item" onClick={ () => setSelectedTab(1) }>
        <a className={ `nav-link ${ (selectedTab === 1) ? 'active' : '' }` } aria-current="page">Detalle</a>
      </li>
      <li className="nav-item" onClick={ () => setSelectedTab(2) }>
        <a className={ `nav-link ${ (selectedTab === 2) ? 'active' : '' }` }>Aplicaciones</a>
      </li>
    </ul>
  )

  return (
    <ProjectProvider>
      <div className='px-5'>
        <div className='d-flex justify-content-between'>
          <h1 className="fs-4 card-title fw-bold mb-4">Proyecto Nuevo</h1>
          { renderTabs() }
        </div>
        { (selectedTab === 2) ? renderAddButton() : null }
        { (selectedTab === 2) ? ( <TableApplications applications={ project.applications } /> ) : ( <DetailProject project={ project } /> )  }
      </div>
    </ProjectProvider>
  )

}
