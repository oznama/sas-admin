import { useContext } from 'react';
import { AuthContext } from '../../auth/context/AuthContext';

import './navbar.main.css';

export const NavBarPage = () => {

  const { user } = useContext( AuthContext )

  return (
    <nav className="navbar navbar-expand-lg bg-body-tertiary">
      <div className="container-fluid">
        <a className="navbar-brand" href="/">SAS Administrator</a>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarText">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <a className="nav-link active" aria-current="page" href="#">Opcion 1</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Opcion 2</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Opcion 3</a>
            </li>
          </ul>
          <span className="navbar-text">
            Selene Pascali
          </span>
        </div>
      </div>
    </nav>
  )
}
