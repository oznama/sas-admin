import { useNavigate, useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { getPWoO, getPWoOExl, naODCNotification } from "../../services/NativeService";
import 'react-datepicker/dist/react-datepicker.css';
import { displayNotification } from "../../helpers/utils";
import { useDispatch, useSelector } from "react-redux";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from "../custom/pagination/page/Pagination";
import { InputSearcher } from "../custom/InputSearcher";

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
    const dispatch = useDispatch();
    const { user } = useSelector( state => state.auth );
    const ownBoss = user.bossEmail;
    const {reportName} = useParams();
    const report = (REPORT_MAP.find(rc => rc.reportName === reportName))

    const [filterDate, setFilterDate] = useState(new Date())
    const [data, setData] = useState([]);
    const [filter, setFilter] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const pageSize = 10;
    const [totalUsers, setTotalUsers] = useState(0);

    const [allChecked, setAllChecked] = useState(true);
    const [keys, setKeys] = useState([]);
    const [isCheck, setIsCheck] = useState();

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchOrders(page, filter);
    }

    useEffect(() => {
        if (reportName) {
            fetchOrders(currentPage, filter);
        }
        setIsCheck(allChecked || keys.length > 0);
    }, [reportName]);

    const fetchOrders = (currentPage, filter) => {
        getPWoO(currentPage, pageSize,filter).then(resp => {
            let data = [];
            resp.content.map( (r) => {
                data.push( { ...r, checked: keys.length > 0 ? keys.find( k => k === r.projectKey ) : false } )
            })
            setData(data);
            setTotalUsers(resp.totalElements);
        })
        .catch(err => {
            console.error('Error fetching data:', err);
        });
    }

    const onChangeFilter = ({ target }) => {
        setCurrentPage(0)
        setFilter(target.value);
        fetchOrders(0, target.value);
    }

    const download = () => {
        let downloadKeys = [];
        if (keys.length!=data.length) {
            downloadKeys=[ ...keys];
        }
        console.log('downloadKeys',downloadKeys)
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
        naODCNotification(keys, ownBoss)
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
        </div>
    )

    const toggleAllCheckboxes = () => {
        const newAllChecked = !allChecked;
        setAllChecked(newAllChecked);
        const updatedData = data.map(item => ({ ...item, checked: newAllChecked }));
        setData(updatedData);
        setKeys(newAllChecked ? updatedData.map(item => item.projectKey) : []);
        if( newAllChecked ) {
            setKeys([]);
        }
    };

    const toggleCheckbox = (pKey, index) => {
        const checked = keys.find( k => k === pKey );
        
        const keySelecteds = checked || allChecked ? keys.filter( k => k !== pKey) : [ ...keys, pKey ];
        
        const dataUpdated = [ ...data ];
        dataUpdated[index].checked = allChecked ? false : !dataUpdated[index].checked;
        setData(dataUpdated);
        if( totalUsers === keySelecteds.length ) {
            console.log('All checkeds, keys clean');
            setAllChecked(true);
            setKeys([]);
        } else {
            console.log('No all checkeds, keys', keySelecteds);
            setAllChecked(false);
            setKeys(keySelecteds);
        }
    };

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        fetchOrders();
    }

    const projectSelected = pKey => {
        const found = keys.find( k => k === pKey );
        console.log(pKey, 'exist in', keys, found);
        return found;
    }

    const renderRows = () => data && data.map(({
        projectKey,
        projectName,
        pmMail,
        pmName,
        bossMail,
        bossName,
        tax,
        total,
        numOrders,
        projectAmount,
        checked
    }, index) => (
        <tr key={projectKey} onClick={() => console.log('Click en row')}>
            <td className="text-center">
                <input
                    type="checkbox"
                    checked={ allChecked || checked }
                    onChange={() => toggleCheckbox(projectKey, index)}
                />
            </td>
            <td className="text-start">{projectKey}</td>
            <td className="text-start">{projectName}</td>
            <td className="text-start">{pmName}</td>
            <td className="text-start">{pmMail}</td>
            <td className="text-start">{bossName}</td>
            <td className="text-start">{bossMail}</td>
            <td className="text-center">{projectAmount}</td>
            <td className="text-center">{tax}</td>
            <td className="text-center">{total}</td>
        </tr>
    ));

    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Reporte: {report.title}</h4>
            {/* {report && renderFilter()} */}
            <InputSearcher name={ 'filter' } placeholder={ 'Escribe para filtrar...' } value={ filter } onChange={ onChangeFilter } onClean={ onClean } />
            <div className="py-2 d-flex flex-row-reverse bd-highlight">
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
            <div className='table-responsive text-nowrap'>
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
                            <th className="text-center fs-6" scope="col">IVA</th>
                            <th className="text-center fs-6" scope="col">Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        {renderRows()}
                    </tbody>
                </table>
                <Pagination
                    currentPage={ currentPage + 1 }
                    totalCount={ totalUsers }
                    pageSize={ pageSize }
                    onPageChange={  page => onPaginationClick(page)  } 
                />
            </div>
        </div>
    )
}
