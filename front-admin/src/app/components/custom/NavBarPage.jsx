import './custom.main.css';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { doLogout } from '../../services/AuthService';
import { logout } from '../../../store/auth/authSlice';

export const NavBarPage = () => {

  const dispatch = useDispatch();

  const navigate = useNavigate();
  const user = useSelector( (state) => state.auth.user );

  const onLogout = () => {
    // TODO No esta funcionando correctamente esto
    // const token = localStorage.getItem('token');
    // doLogout(token).then( response => {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    // }).catch( error => {
    //   console.log(error);
    // })
    dispatch(logout());
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
                Aplicaciones
              </NavLink>
            </li>
            <li className="nav-item dropdown">
              <NavLink className="nav-item nav-link" to="#">
                Catalogos
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
