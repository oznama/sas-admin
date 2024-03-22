import { useNavigate, useParams } from "react-router-dom";
import DatePicker from 'react-datepicker';
import { useState } from "react";

export const FormReport = () => {

    const REPORT_MAP = [
        {
            reportName: 'projects_orders',
            title: 'Proyecto - Número de ordenes',
            filterType: 1
        },
        {
            reportName: 'orders_invoices',
            title: 'Orden - Facturas',
            filterType: 1
        },
        {
            reportName: 'application_pending',
            title: 'Pendientes',
            filterType: 2
        }
    ]

    const navigate = useNavigate();
    const {reportName} = useParams();
    const report = (REPORT_MAP.find( rc => rc.reportName === reportName ))

    const [filterDate, setFilterDate] = useState(new Date())

    const onSubmit = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        console.log('Execute report', reportName, request);
    }

    const renderFilter = () => (
        <form className='d-grid gap-2 col-6 mx-auto' onSubmit={ onSubmit }>
            {
                report.filterType === 1 && (
                    <div className='mb-3'>
                        <select className="form-select">
                            <option value="1">Todos</option>
                            <option value="2">Con ordenes</option>
                            <option value="3">Sin ordenes</option>
                        </select>
                    </div>
                )
            }
            {
                report.filterType === 2 && (
                    <DatePicker className="form-control padding-short" selected={filterDate} onChange={(date) => setFilterDate(date)} />
                )
            }
            {
                report.filterType === 2 && (
                    <div className='mb-3'>
                        <select className="form-select">
                            <option selected>Seleccionar ...</option>
                            <option value="1">Angel Calzada</option>
                            <option value="2">Alvaro Mendoza</option>
                            <option value="3">Juan Ba&ntilde;os</option>
                        </select>
                    </div>
                )
            }
            <div className="pt-3 d-flex flex-row-reverse">
                <button type="submit" className="btn btn-primary">Exportar</button>
                &nbsp;
                <button type="button" className="btn btn-danger" onClick={ () => navigate(`/home`) }>Cancelar</button>
            </div>
        </form>
    )

    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Reporte: { report.title }</h4>
            { report && renderFilter() }
        </div>
    )
}