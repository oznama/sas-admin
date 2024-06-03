import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../../store/auth/authSlice';
import { useEffect, useRef, useState } from 'react';
import { REPORT_MAP } from '../../helpers/utils';

export const NavBarPage = () => {

  const dispatch = useDispatch();
  const refAdminTab = useRef();
  const refReportTab = useRef();
  const refNotifications = useRef();

  const navigate = useNavigate();
  const { user, permissions } = useSelector( state => state.auth );
  const [currentTab, setCurrentTab] = useState(0);
  const [showTabAdmin, setShowTabAdmin] = useState(false);
  const [showTabReport, setShowTabReport] = useState(false);
  const [menuCollapsed, setMenuCollapsed] = useState(false);

  const fetchProjectAppPendings = () => {
    getPendings(pendingType.due, 0, 20).then( response => {
      if( response.content ) {
        const pendings = [];
        response.content.map( item => {
          const now = new Date();
          const dateSubtitle = item.designStatus != 2001000003 && handleDateStr(item.designDate) < now ? `Diseño: ${ item.designDate } ${ item.designStatusDesc }` 
          : ( item.developmentStatus != 2001000003 && handleDateStr(item.developmentDate) < now ? `Desarrollo: ${ item.developmentDate } ${ item.developmentStatusDesc}` 
          : ( item.endStatus != 2001000003 && handleDateStr(item.endDate) < now ? `Cierre: ${ item.endDate } ${ item.endStatusDesc }` : '') );
          if( dateSubtitle !== '' ) {
            pendings.push({
              id:  item.id,
              title: `${ item.projectKey } - ${ item.application }`,
              detail: `${dateSubtitle}, Responsables [Líder: ${ item.leader }, Desarrollador: ${ item.developer }]`
            });
          }
        });
        setNotifications(pendings);
      }
  }).catch( error => {
      console.log(error);
  });
  }

  const fetchNotifications = () => {
    // TODO Call services for get notifications
    // Project pendings
    fetchProjectAppPendings();
    // Orders and invoices pendings
  }

  useEffect(() => {
    const handleTabOnBlur = event => {
      if( refAdminTab.current && !refAdminTab.current.contains(event.target) ) {
        setShowTabAdmin(false);
      }
      if( refReportTab.current && !refReportTab.current.contains(event.target) ) {
        setShowTabReport(false);
      }
      if( refNotifications.current && !refNotifications.current.contains(event.target) ) {
        setShowNotif(false);
      }
    }

    document.addEventListener('click', handleTabOnBlur);

    if( permissions.isAdminSas ) {
      fetchNotifications();
    }
  
    return () => {
      document.removeEventListener('click', handleTabOnBlur)
    }
  }, [refAdminTab, refReportTab, refNotifications]);  

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
    setCurrentTab(null);
    setShowTabAdmin(!showTabAdmin);
    navigate(`/${urlRedirect}`, { replace: true })
    
  }

  const gotoReportOption = report => {
    setCurrentTab(null);
    setShowTabReport(!showTabReport);
    navigate(`/reports/${report.reportName}/${report.options ? report.options.length : '0'}`, { replace: true });
  }
  
  const renderTabAdmin = () => permissions.isAdminSas && (
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
        { permissions.canAdminApp && (<a className="dropdown-item" href="#" onClick={ () => gotoAdminOption('users') }>Administraci&oacute;n de Usuarios</a>) }
      </div>
    </li>
  )

  const renderTabReports = () => permissions.isAdminSas && (
    <li className={ `nav-item dropdown ${ showTabReport ? 'show' : '' }` } ref={ refReportTab }>
      <a className="nav-link dropdown-toggle" href="#" id="reportMenu" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded={ showTabReport } onClick={ () => setShowTabReport(!showTabReport) }>
        Reportes
      </a>
      <div className={ `dropdown-menu bg-primary ${ showTabReport ? 'show' : '' }` } aria-labelledby="reportMenu">
        {
          REPORT_MAP.map( (report, index) => (
            <a key={ index } className="dropdown-item" href="#" onClick={ () => gotoReportOption(report) }>{report.title}</a>
          ))
        }
      </div>
    </li>
  )

  const renderNotifications = () => notifications && notifications.length > 0 && (
    <li className="nav-item mr-3" ref={ refNotifications }>
      <a className="nav-link nav-icon position-relative" href="#" id="notiDialog" onClick={ () => setShowNotif(true) }>
        <span><i className="bi bi-bell-fill"></i></span>
        <span className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
          { notifications.length > 10 ? '10+' : notifications.length }
        </span>
      </a>
      <div className={ `dropdown-menu bg-primary p-2 w-50 ${ showNotif ? 'show' : ''}` } aria-labelledby="notiDialog">
          <h4>Notificaciones</h4>
          <div className="d-grid gap-3 overflow-auto" style={ notifications.length > 4 ? { height: '26rem' } : {}}>
            {
              notifications.map((n, index) => ( 
                  <div key={ index } className="card bg-primary mx-2">
                    <div className="card-body">
                      <h5 className="card-title">{ n.title }</h5>
                      <p className="card-text">{ n.detail }</p>
                    </div>
                  </div>
              ))
            }
          </div>
      </div>
    </li>
  )

  return (
    <nav className="navbar navbar-expand-lg bg-primary" data-bs-theme="dark">
      <div className="container-fluid">
        
        <a className="navbar-brand">SAS Administrador</a>

        <button className="navbar-toggler" type="button" onClick={ () => setMenuCollapsed(!menuCollapsed) }>
          <span className="navbar-toggler-icon"></span>
        </button>
        
        <div className={ `collapse navbar-collapse ${ menuCollapsed ? 'show' : '' }` } >
          <ul className="navbar-nav me-auto mb-2 mb-lg-0">
            <li className="nav-item">
              <NavLink className={ `nav-item nav-link ${ (currentTab === 1) ? 'active' : '' }` }
                onClick={ () => setCurrentTab(0) } to="/">
                { permissions.isAdminSas ? 'Proyectos' : 'Dashboard' }
              </NavLink>
            </li>
            { renderTabOrders() }
            { renderTabInvoices() }
            { renderTabReports() }
            { renderTabAdmin() }
            { renderNotifications() }
          </ul>

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
      </div>
    </nav>
  )
}
