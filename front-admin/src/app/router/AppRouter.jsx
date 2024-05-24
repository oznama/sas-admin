import { Navigate, Route, Routes } from 'react-router-dom';
import { Home } from '../components/home/Home';
import { ProjectRouter } from '../components/projects/ProjectRouter';
// import { CatalogPage } from '../components/Catalogs/CatalogPage';
import { UserPage } from '../components/auth/UserPage';
import { EmployeeRouter } from '../components/employee/EmployeeRouter';
import { CompanyRouter } from '../components/company/CompanyRouter';
import { TableOrders } from '../components/orders/page/TableOrders';
import { ApplicationRouter } from '../components/applicationsCatalog/ApplicationRouter';
//import { AdminRouter } from '../components/admin/AdminRouter';
import { AdminRouter } from '../components/admin/adminRouter';
import { UserRouter } from '../components/user/userRouter';
import { TableInvoices } from '../components/invoices/page/TableInvoices';
import { CatalogConexionRouter } from '../components/catalogConexion/catalogConexionRouter';
import { FormReport } from '../components/admin/FormReport';
import { PagePendings } from '../components/applications/page/PagePendings';


export const AppRouter = () => (
    <Routes>
        <Route path="home" element={ <Home /> } />
        <Route path="project/*" element={ <ProjectRouter /> } />
        <Route path="pendings" element={ <PagePendings /> } />
        <Route path="orders" element={ <TableOrders /> } />
        <Route path="invoices" element={ <TableInvoices /> } />
        {/* <Route path="catalog" element={ <CatalogPage /> } /> */}
        <Route path="application/*" element={ <ApplicationRouter /> } />
        <Route path="employee/*" element={ <EmployeeRouter /> } />
        <Route path="user" element={ <UserPage /> } />
        <Route path="company/*" element={ <CompanyRouter /> } />
        <Route path="role/*" element={ <CatalogConexionRouter catalogId={1000000005}/> } />
        <Route path="companyType/*" element={ <CatalogConexionRouter catalogId={1000000009}/> } />
        <Route path="days/*" element={ <CatalogConexionRouter catalogId={1000000007}/> } />
        <Route  path="admin/*" element={ <AdminRouter />} />
        <Route  path="users/*" element={ <UserRouter />} />
        <Route path="reports/:reportName" element={ <FormReport /> } />

        <Route path="/" element={ <Navigate to="/home" /> } />
    </Routes>
)