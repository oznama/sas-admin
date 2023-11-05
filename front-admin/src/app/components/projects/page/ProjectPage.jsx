import { useNavigate } from 'react-router-dom';
import { DetailProject } from './DetailProject';
import { useState } from 'react';
import { TableApplications } from '../../applications/page/TableApplications';

export const ProjectPage = () => {

  const navigate = useNavigate();

  const [selectedTab, setSelectedTab] = useState(1);

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
    <div className='px-5'>
      <div className='d-flex justify-content-between'>
        <h1 className="fs-4 card-title fw-bold mb-4">Proyecto Nuevo</h1>
        { renderTabs() }
      </div>
      { (selectedTab === 2) ? renderAddButton() : null }
      { (selectedTab === 2) ? ( <TableApplications /> ) : ( <DetailProject /> )  }
    </div>
  )

}
