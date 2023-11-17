import './custom.main.css';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../../store/auth/authSlice';
import { useState } from 'react';

export const NavBarPage = () => {

  const dispatch = useDispatch();

  const navigate = useNavigate();
  const user = useSelector( (state) => state.auth.user );
  const [currentTab, setCurrentTab] = useState(0);
  const roleId = user?.role?.id;

  const onLogout = () => {
    localStorage.removeItem('token');
    dispatch(logout());
    navigate('/login', { replace: true })
  }

  const renderTabApplications = () => roleId && (roleId === 1 || roleId === 2) && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 1) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(1) } to="application">
        Aplicaciones
      </NavLink>
    </li>
  );

  const renderTablCatalogs = () => roleId && roleId === 1 && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 2) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(2) } to="catalog">
        Cat&aacute;logos
      </NavLink>
    </li>
  );

  const renderTablRoles = () => roleId && roleId === 1 && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 3) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(3) } to="security-rol">
        Roles
      </NavLink>
    </li>
  );

  const renderTablUsers = () => roleId && (roleId === 1 || roleId === 2) && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 4) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(4) } to="security-user">
        Usuarios
      </NavLink>
    </li>
  );

  return (
    <nav className="navbar navbar-expand-lg bg-primary" data-bs-theme="dark">
      <div className="container-fluid">
        
        <Link className="navbar-brand" onClick={ () => setCurrentTab(0) } to="/">SAS Administrador</Link>
        
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            { renderTabApplications() }
            { renderTablCatalogs() }
            { renderTablRoles() }
            { renderTablUsers() }
          </ul>
        </div>

        <div className="d-flex flex-row-reverse bd-highlight">
          <div className="p-2 bd-highlight">
            <button className="btn btn-danger" onClick={ onLogout }>
              <span><i className="bi bi-power"></i></span>
            </button>
          </div>
          <div className="p-2 bd-highlight">
            <span className="text-white">{ user?.name }</span>
          </div>
        </div>

      </div>
    </nav>
  )
}
