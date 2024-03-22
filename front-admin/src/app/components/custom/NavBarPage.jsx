import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../../store/auth/authSlice';
import { useEffect, useRef, useState } from 'react';

export const NavBarPage = () => {

  const dispatch = useDispatch();
  const refAdminTab = useRef();
  const refReportTab = useRef();

  const navigate = useNavigate();
  const { user, permissions } = useSelector( state => state.auth );
  const [currentTab, setCurrentTab] = useState(0);
  const [showTabAdmin, setShowTabAdmin] = useState(false);
  const [showTabReport, setShowTabReport] = useState(false);

  const userAdmin = user && user.role && user.role.id < 4;

  useEffect(() => {
    
    const handleTabOnBlur = event => {
      if( refAdminTab.current && !refAdminTab.current.contains(event.target) ) {
        setShowTabAdmin(false);
      }
      if( refReportTab.current && !refReportTab.current.contains(event.target) ) {
        setShowTabReport(false);
      }
    }

    document.addEventListener('click', handleTabOnBlur);
  
    return () => {
      document.removeEventListener('click', handleTabOnBlur)
    }
  }, [refAdminTab, refReportTab])
  

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

  const renderTabPendings = () => permissions.isAdminSas && (
    <li className="nav-item">
      <NavLink className={ `nav-item nav-link ${ (currentTab === 4) ? 'active' : '' }` }
        onClick={ () => setCurrentTab(4) } to="pendings">
        Pendientes
      </NavLink>
    </li>
  );

  const gotoAdminOption = urlRedirect => {
    setShowTabAdmin(!showTabAdmin);
    navigate(`/${urlRedirect}`, { replace: true })
    
  }

  const gotoReportOption = report => {
    setShowTabReport(!showTabReport);
    navigate(`/reports/${report}`, { replace: true });
  }
  
  const renderTabAdmin = () => userAdmin && (
    <li className={ `nav-item dropdown ${ showTabAdmin ? 'show' : '' }` } ref={ refAdminTab }>
      <a className="nav-link dropdown-toggle" href="#" id="adminMenu" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded={ showTabAdmin } onClick={ () => setShowTabAdmin(!showTabAdmin) }>
        Administraci&oacute;n
      </a>
      <div className={ `dropdown-menu bg-primary ${ showTabAdmin ? 'show' : '' }` } aria-labelledby="adminMenu">
        { permissions.canAdminApp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('application') }>Aplicaciones</a>) }
        { permissions.canAdminCat && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('role') }>Puestos de trabajo</a>) }
        { permissions.canAdminCat && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('companyType') }>Tipos de empresas</a>) }
        { permissions.canAdminCat && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('days') }>Dias Feriados</a>) }
        <div className="dropdown-divider"></div>
        { permissions.canGetComp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('company') }>Empresas</a>) }
        { permissions.canGetEmp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('employee') }>Empleados</a>) }
        <div className="dropdown-divider"></div>
        { permissions.canAdminApp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('admin') }>Administraci&oacute;n de Rol</a>) }
      </div>
    </li>
  )

  const renderTabReports = () => userAdmin && (
    <li className={ `nav-item dropdown ${ showTabReport ? 'show' : '' }` } ref={ refReportTab }>
      <a className="nav-link dropdown-toggle" href="#" id="reportMenu" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded={ showTabReport } onClick={ () => setShowTabReport(!showTabReport) }>
        Reportes
      </a>
      <div className={ `dropdown-menu bg-primary ${ showTabReport ? 'show' : '' }` } aria-labelledby="reportMenu">
        <a className="dropdown-item" href="#" onClick={ () => gotoReportOption('application_pending') }>Pendientes</a>
        <a className="dropdown-item" href="#" onClick={ () => gotoReportOption('projects_orders') }>Ordenes pendientes</a>
        <a className="dropdown-item" href="#" onClick={ () => gotoReportOption('orders_invoices') }>Facturas pendientes</a>
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
                { userAdmin ? 'Proyectos' : 'Dashboard' }
              </NavLink>
            </li>
            { renderTabOrders() }
            { renderTabInvoices() }
            { renderTabPendings() }
            { renderTabReports() }
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
