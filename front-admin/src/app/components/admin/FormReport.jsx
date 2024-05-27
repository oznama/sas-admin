import PropTypes from 'prop-types';
import { useNavigate, useParams } from "react-router-dom";
import DatePicker from 'react-datepicker';
import { useState, useEffect } from "react";
import { getPWoO, getPWoOExl, naODCNotification } from "../../services/NativeService";
import 'react-datepicker/dist/react-datepicker.css';
import { displayNotification } from "../../helpers/utils";
import { useDispatch, useSelector } from "react-redux";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from "../custom/pagination/page/Pagination";

export const FormReport = (
    pageSize = 10,
    sort = 'name,asc',
) => {
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
    const dispatch = useDispatch();
    const { user } = useSelector( state => state.auth );
    const {reportName} = useParams();
    const report = (REPORT_MAP.find(rc => rc.reportName === reportName))

    const [filterDate, setFilterDate] = useState(new Date())
    const [data, setData] = useState([]);
    const [allChecked, setAllChecked] = useState(false);
    const [filter, setFilter] = useState('');
    const [keys, setKeys] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalUsers, setTotalUsers] = useState(0);

    const onPaginationClick = page => {
        setCurrentPage(page);
    }

    useEffect(() => {
        if (reportName) {
            fetchOrders();
        }
    }, [reportName, pageSize, currentPage]);

    const isCheck = keys.length > 0;

    const fetchOrders = () => {
        getPWoO(filter)
        .then(resp => {
            console.log('resp', resp)
            setData(resp.content);
            setTotalUsers(resp.totalElements);
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
        getPWoO(filter ? filter : '').then(resp => {
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

    const download = () => {
        const downloadKeys = allChecked ? [] : keys;
        getPWoOExl(downloadKeys).then(response => response.blob()
        ).then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'report.xlsx';
            document.body.appendChild(a);
            a.click();
            a.remove();
        })
    }

    const email = () => {
        naODCNotification(keys)
        .then(resp => {
            console.log(resp);
            displayNotification(dispatch, '¡Correo enviado!', alertType.success);
        })
        .catch(err => {
            console.error('Error fetching data:', err);
        });
    }
    
    const renderHeader = () => (
        <div className="d-flex justify-content-between align-items-center">
            { renderSearcher() }
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalUsers }
                pageSize={ pageSize }
                onPageChange={  page => onPaginationClick(page)  } 
            />
        </div>
    )

    const toggleAllCheckboxes = () => {
        const newAllChecked = !allChecked;
        setAllChecked(newAllChecked);
        const updatedData = data.map(item => ({ ...item, checked: newAllChecked }));
        setData(updatedData);
        setKeys(newAllChecked ? updatedData.map(item => item.projectKey) : []);
    };

    const toggleCheckbox = (index) => {
        const updatedData = [...data];
        updatedData[index].checked = !updatedData[index].checked;
        setData(updatedData);

        const updatedKeys = updatedData[index].checked
            ? [...keys, updatedData[index].projectKey]
            : keys.filter(key => key !== updatedData[index].projectKey);
        
        setKeys(updatedKeys);
        setAllChecked(updatedData.every(item => item.checked));
    };

    const renderSearcher = () => (
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
                        <button type="button" disabled={!isCheck} className={`btn ${isCheck ? 'btn-success' : 'btn-secondary'}`} onClick={email}>
                            Enviar Correo &nbsp;
                            <span>
                                <i className="bi bi-envelope"></i>
                            </span>
                        </button>
                        &nbsp;
                        <button type="button" disabled={!isCheck} className={`btn ${isCheck ? 'btn-primary' : 'btn-secondary'}`} onClick={download}>
                            Exportar &nbsp;
                            <span>
                            <i className="bi bi-arrow-bar-down"></i>
                            </span>
                        </button>
                </div>
                <div className="form-check">
                    {/* <input className="form-check-input" type="checkbox" value="" id="ownBoss" /> */}
                    <input type="checkbox" value="" id="boss"/>
                    <label className="form-check-label" htmlFor="boss">
                        ¿Copiar a jefes en correo?
                    </label>
                    <br /> 
                    {/* <input className="form-check-input" type="checkbox" value="" id="ownBoss" /> */}
                    <input type="checkbox" value="" id="ownBoss"/>
                    <label className="form-check-label" htmlFor="ownBoss">
                        ¿Copiar a&nbsp;
                        {
                            `${user.bossName} al correo ${user.bossEmail}?`
                        }
                    </label>
                </div>
            </div>
            
        </form>
    )

    const renderRows = () => data && data.map(({
        projectKey,
        projectName,
        pmMail,
        pmName,
        bossMail,
        bossName,
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
            <td className="text-start">{bossName}</td>
            <td className="text-start">{bossMail}</td>
            <td className="text-center">{projectAmount}</td>
        </tr>
    ));

    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Reporte: {report.title}</h4>
            {/* {report && renderFilter()} */}
            { report && renderHeader() }
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
                            <th className="text-center fs-6" scope="col">Clave</th>
                            <th className="text-center fs-6" scope="col">Proyecto</th>
                            <th className="text-center fs-6" scope="col">PM</th>
                            <th className="text-center fs-6" scope="col">Correo</th>
                            <th className="text-center fs-6" scope="col">Jefe</th>
                            <th className="text-center fs-6" scope="col">Correo</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                        </tr>
                    </thead>
                    <tbody>
                        {renderRows()}
                    </tbody>
                </table>
            </div>
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalUsers }
                pageSize={ pageSize }
                onPageChange={  page => onPaginationClick(page)  } 
            />
        </div>
    )
}

FormReport.propTypes = {
    pageSize: PropTypes.number,
}