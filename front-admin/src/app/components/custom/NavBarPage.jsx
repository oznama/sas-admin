import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../../store/auth/authSlice';
import { useEffect, useRef, useState } from 'react';

export const NavBarPage = () => {

  const dispatch = useDispatch();
  const refAdminTab = useRef();

  const navigate = useNavigate();
  const { user, permissions } = useSelector( state => state.auth );
  const [currentTab, setCurrentTab] = useState(0);
  const [showTabAdmin, setShowTabAdmin] = useState(false);

  useEffect(() => {
    
    const handleAdminTabOnBlur = event => {
      if( refAdminTab.current && !refAdminTab.current.contains(event.target) ) {
        setShowTabAdmin(false);
      }
    }

    document.addEventListener('click', handleAdminTabOnBlur);
  
    return () => {
      document.removeEventListener('click', handleAdminTabOnBlur)
    }
  }, [refAdminTab])
  

  const onLogout = () => {
    localStorage.removeItem('token');
    dispatch(logout());
    navigate('/login', { replace: true })
  }

  const renderTabOrders = () => permissions.isAdminSas && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 2) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(2) } to="orders">
        Ordenes
      </NavLink>
    </li>
  );

  const renderTabInvoices = () => permissions.isAdminSas && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 3) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(3) } to="invoices">
        Facturas
      </NavLink>
    </li>
  );

  const gotoAdminOption = urlRedirect => {
    setShowTabAdmin(!showTabAdmin);
    navigate(`/${urlRedirect}`, { replace: true })
    
  }
  
  const renderTabAdmin = () => (
    <li className={ `nav-item dropdown ${ showTabAdmin ? 'show' : '' }` } ref={ refAdminTab }>
        <a className="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded={ showTabAdmin } onClick={ () => setShowTabAdmin(!showTabAdmin) }>
          Administraci&oacute;n
        </a>
        <div className={ `dropdown-menu bg-primary ${ showTabAdmin ? 'show' : '' }` } aria-labelledby="navbarDropdown">
          { permissions.canAdminApp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('application') }>Aplicaciones</a>) }
          { permissions.canAdminCat && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('role') }>Puestos de trabajo</a>) }
          { permissions.canAdminCat && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('companyType') }>Tipos de empresas</a>) }
          { permissions.canAdminCat && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('days') }>Dias Feriados</a>) }
          { permissions.canAdminApp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('admin') }>Administraci&oacute;n</a>) }
          <div className="dropdown-divider"></div>
          { permissions.canGetComp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('company') }>Empresas</a>) }
          { permissions.canGetEmp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('employee') }>Empleados</a>) }
        </div>
      </li>
  )

  return (
    <nav className="navbar navbar-expand-lg bg-primary" style={{ padding: '0'}} data-bs-theme="dark">
      <div className="container-fluid">
        
        <Link className="navbar-brand">SAS Administrador</Link>
        
        <div className="collapse navbar-collapse">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <NavLink className={ `nav-item nav-link ${ (currentTab === 1) ? 'active' : '' }` }
                onClick={ () => setCurrentTab(0) } to="/">
                Proyectos
              </NavLink>
            </li>
            { renderTabOrders() }
            { renderTabInvoices() }
            { renderTabAdmin() }
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
