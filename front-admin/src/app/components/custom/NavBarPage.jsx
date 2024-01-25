import './custom.main.css';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../../store/auth/authSlice';
import { useState } from 'react';

export const NavBarPage = () => {

  const dispatch = useDispatch();

  const navigate = useNavigate();
  const { user, permissions } = useSelector( state => state.auth );
  const [currentTab, setCurrentTab] = useState(0);

  const onLogout = () => {
    localStorage.removeItem('token');
    dispatch(logout());
    navigate('/login', { replace: true })
  }

  const renderTablCatalogs = () => permissions.canAdminCat && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 1) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(1) } to="catalog">
        Cat&aacute;logos
      </NavLink>
    </li>
  );

  const renderTabApplications = () => permissions.canAdminApp && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 2) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(2) } to="application">
        Aplicaciones
      </NavLink>
    </li>
  );

  const renderTabCompany = () => permissions.isAdminRoot && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 3) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(3) } to="company">
        Compa&ntilde;ias
      </NavLink>
    </li>
  );

  const renderTabEmployee = () => permissions.canGetEmp && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 4) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(4) } to="employee">
        Empleados
      </NavLink>
    </li>
  );

  const renderTabClients = () => permissions.isAdminSas && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 5) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(5) } to="client">
        Clientes
      </NavLink>
    </li>
  );

  return (
    <nav className="navbar navbar-expand-lg bg-primary" style={{ padding: '0'}} data-bs-theme="dark">
      <div className="container-fluid">
        
        <Link className="navbar-brand" onClick={ () => setCurrentTab(0) } to="/">SAS Administrador</Link>
        
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            { renderTablCatalogs() }
            { renderTabApplications() }
            { renderTabCompany() }
            { renderTabEmployee() }
            { renderTabClients() }
          </ul>
        </div>

        <div className="d-flex flex-row-reverse bd-highlight">
          <div className="p-1 bd-highlight">
            <button className="btn btn-danger" onClick={ onLogout }>
              <span><i className="bi bi-power"></i></span>
            </button>
          </div>
          <div className="p-1 bd-highlight">
            <span className="d-block text-end text-white">Bienvenid@ { user?.name } [{user?.role?.name}]</span>
            <span className="d-block text-end text-white">{ user?.position } { (user?.position) && '-' } { user?.company }</span>
          </div>
        </div>

      </div>
    </nav>
  )
}
