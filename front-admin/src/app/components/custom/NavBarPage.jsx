import { useContext, useState } from 'react';
import { AuthContext } from '../auth/context/AuthContext';

import './custom.main.css';
import { Link, NavLink, useNavigate } from 'react-router-dom';

export const NavBarPage = () => {

  const navigate = useNavigate();
  const { user, logout } = useContext( AuthContext );

  const onLogout = () => {
    logout();
    navigate('/login', { replace: true })
  }

  return (
    <nav className="navbar navbar-expand-lg bg-primary" data-bs-theme="dark">
      <div className="container-fluid">
        
        <Link className="navbar-brand" to="/">SAS Administrador</Link>
        
        
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <NavLink className="nav-item nav-link active" to="#">
                Catalogos
              </NavLink>
            </li>
            <li className="nav-item dropdown">
              <NavLink className="nav-item nav-link" to="#">
                Seguridad
              </NavLink>
            </li>
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
