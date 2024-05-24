import { useNavigate, useParams } from "react-router-dom";
import DatePicker from 'react-datepicker';
import { useState, useEffect } from "react";
import { getPWoO } from "../../services/NativeService";
import 'react-datepicker/dist/react-datepicker.css';

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
            title: 'Proyectos sin ODC',
            filterType: 2
        }
    ]

    const navigate = useNavigate();
    const {reportName} = useParams();
    const report = (REPORT_MAP.find(rc => rc.reportName === reportName))

    const [filterDate, setFilterDate] = useState(new Date())
    const [data, setData] = useState([]);
    const [allChecked, setAllChecked] = useState(false);
    const [filter, setFilter] = useState('');

    useEffect(() => {
        if (reportName) {
            fetchOrders();
        }
    }, [reportName]);
    
    const fetchOrders = () =>{
        getPWoO(filter)
        .then(resp => {
            setData(resp);
        })
        .catch(err => {
            console.error('Error fetching data:', err);
        });
    }

    const onSubmit = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        console.log('Execute report', reportName, request);
        getPWoO('').then(resp => {
            setData(resp);
            console.log('resp', resp);
        }).catch(err => {
            console.log('err', err);
        });
    }

    const onChangeFilter = (event) => {
        setFilter(event.target.value);
        fetchOrders();
    }

    const toggleAllCheckboxes = () => {
        setAllChecked(!allChecked);
        const updatedData = data.map(item => ({ ...item, checked: !allChecked }));
        setData(updatedData);
    };

    const toggleCheckbox = (index) => {
        const updatedData = [...data];
        updatedData[index].checked = !updatedData[index].checked;
        setData(updatedData);
        setAllChecked(updatedData.every(item => item.checked));
    };

    const renderFilter = () => (
        <form className='d-grid gap-2 col-6 mx-auto' onSubmit={onSubmit}>
            {report.filterType === 1 && (
                <div className='mb-3'>
                    <select className="form-select">
                        <option value="1">Todos</option>
                        <option value="2">Con ordenes</option>
                        <option value="3">Sin ordenes</option>
                    </select>
                </div>
            )}
            {/* {report.filterType === 2 && (
                <DatePicker className="form-control padding-short" selected={filterDate} onChange={(date) => setFilterDate(date)} />
            )}
            {report.filterType === 2 && (
                <div className='mb-3'>
                    <select className="form-select">
                        <option selected>Seleccionar ...</option>
                        <option value="1">Angel Calzada</option>
                        <option value="2">Alvaro Mendoza</option>
                        <option value="3">Juan Baños</option>
                    </select>
                </div>
            )} */}
            <div className='mb-3'>
                <div className="pt-3 d-flex flex-row">
                    <input
                        type="text"
                        name="filter"
                        value={filter}
                        onChange={onChangeFilter}
                        placeholder="Escribe para filtrar..."
                        className="form-control"
                    />
                    
                </div>
                <div className="pt-3 d-flex flex-row flex-row-reverse"> 
                        <button type="button" className="btn btn-success" onClick={() => console.log('Correo')}>
                            Enviar Correo &nbsp;
                            <span>
                                <i class="bi bi-envelope"></i>
                            </span>
                        </button>
                        &nbsp;
                        <button type="submit" className="btn btn-primary">
                            Exportar &nbsp;
                            <span>
                            <i class="bi bi-arrow-bar-down"></i>
                            </span>
                        </button>
                </div>
            </div>
            
        </form>
    )

    const renderRows = () => data && data.map(({
        projectKey,
        projectName,
        pmMail,
        pmName,
        numOrders,
        projectAmount,
        checked
    }, index) => (
        <tr key={projectKey} onClick={() => console.log('Click en row')}>
            <td className="text-center">
                <input
                    type="checkbox"
                    checked={checked || false}
                    onChange={() => toggleCheckbox(index)}
                />
            </td>
            <td className="text-start">{projectKey}</td>
            <td className="text-start">{projectName}</td>
            <td className="text-start">{pmName}</td>
            <td className="text-start">{pmMail}</td>
            {/* <td className="text-center">{numOrders}</td> */}
            <td className="text-center">{projectAmount}</td>
        </tr>
    ));

    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Reporte: {report.title}</h4>
            {report && renderFilter()}
            <div className='table-responsive text-nowrap' style={{ height: '350px' }}>
                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">
                                <input
                                    type="checkbox"
                                    checked={allChecked}
                                    onChange={toggleAllCheckboxes}
                                />
                            </th>
                            <th className="text-center fs-6" scope="col">Clave del Proyecto</th>
                            <th className="text-center fs-6" scope="col">Nombre del Proyecto</th>
                            <th className="text-center fs-6" scope="col">Nombre del PM</th>
                            <th className="text-center fs-6" scope="col">Correo del PM</th>
                            {/* <th className="text-center fs-6" scope="col">Número de Órdenes</th> */}
                            <th className="text-center fs-6" scope="col">Monto del Proyecto</th>
                        </tr>
                    </thead>
                    <tbody>
                        {renderRows()}
                    </tbody>
                </table>
            </div>
        </div>
    )
}