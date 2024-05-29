import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { getPWoO, getPWoOExl, naODCNotification } from "../../services/NativeService";
import 'react-datepicker/dist/react-datepicker.css';
import { REPORT_MAP, displayNotification, styleCheckBox } from "../../helpers/utils";
import { useDispatch, useSelector } from "react-redux";
import { alertType } from "../custom/alerts/types/types";
import { Pagination } from "../custom/pagination/page/Pagination";
import { InputSearcher } from "../custom/InputSearcher";

export const FormReport = () => {

    const dispatch = useDispatch();

    //const { user } = useSelector(state => state.auth);
    // const ownBoss = user.bossEmail;
    // const ownEmail = user.email;
    const ownBoss = 'jaime.carreno@sas-mexico.com';
    const ownEmail = 'selene.pascalis@sas-mexico.com';
    
    const { reportName } = useParams();
    const report = REPORT_MAP.find(rc => rc.reportName === reportName);

    const [filterDate, setFilterDate] = useState(new Date());
    const [data, setData] = useState([]);
    const [filter, setFilter] = useState('');
    const [currentPage, setCurrentPage] = useState(0);
    const pageSize = 10;
    const [totalUsers, setTotalUsers] = useState(0);

    const [allChecked, setAllChecked] = useState(true);
    const [keys, setKeys] = useState([]);
    const [allKeys, setAllKeys] = useState([]);
    const [isCheck, setIsCheck] = useState(true);

    const allKeysGet = () => {
        getPWoO(report.context, 0, 2147483647, null, report.filter).then(resp => {
            setAllKeys(resp.content.map(item => item.projectKey));
            console.log('Todas las llaves', resp.content);
        })
        .catch(err => {
            console.error('Error fetching data:', err);
        });
    }

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchOrders(page, filter);
    };

    useEffect(() => {
        if (report) {
            fetchOrders(currentPage, filter);
            allKeysGet();
        }
    }, [report]);

    useEffect(() => {
        if (data.length > 0 && allChecked) {
            setKeys(allKeys);  // Select all keys when allChecked is true
        }
        setIsCheck(allChecked || keys.length > 0);
    }, [allChecked, data]);

    const fetchOrders = (currentPage, filter) => {
        getPWoO(report.context, currentPage, pageSize, filter, report.filter).then(resp => {
            const data = resp.content.map(r => ({
                ...r,
                checked: allChecked || keys.includes(r.projectKey)
            }));
            setData(data);
            setTotalUsers(resp.totalElements);
        })
        .catch(err => {
            console.error('Error fetching data:', err);
        });
    };

    const onChangeFilter = ({ target }) => {
        setCurrentPage(0);
        setFilter(target.value);
        fetchOrders(0, target.value);
    };

    const download = () => {
        let downloadKeys = keys.length === allKeys.length ? [] : [...keys];
        console.log('downloadKeys', downloadKeys);
        getPWoOExl(report.context, downloadKeys).then(response => response.blob()
        ).then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${report.excel}.xlsx`;
            document.body.appendChild(a);
            a.click();
            a.remove();
        });
    };

    const email = () => {
        naODCNotification(report.context, keys, ownBoss, ownEmail).then(resp => {
            console.log(resp);
            displayNotification(dispatch, '¡Correo enviado!', alertType.success);
        }).catch(err => {
            console.error('Error fetching data:', err);
        });
    };

    const renderHeader = () => (
        <div className="d-flex justify-content-between align-items-center">
            {renderSearcher()}
        </div>
    );

    const toggleAllCheckboxes = () => {
        const newAllChecked = !allChecked;
        setAllChecked(newAllChecked);
        const updatedKeys = newAllChecked ? allKeys : [];
        setKeys(updatedKeys);
        const updatedData = data.map(item => ({ ...item, checked: newAllChecked }));
        setData(updatedData);
    };

    const toggleCheckbox = (pKey, index) => {
        const dataUpdated = [...data];
        dataUpdated[index].checked = !dataUpdated[index].checked;

        const keySelecteds = dataUpdated[index].checked
            ? [...keys, pKey]
            : keys.filter(k => k !== pKey);

        setData(dataUpdated);
        setKeys(keySelecteds);

        if (dataUpdated.every(item => item.checked)) {
            setAllChecked(true);
        } else {
            setAllChecked(false);
        }
    };

    const onClean = () => {
        setFilter('');
        setCurrentPage(0);
        fetchOrders(0, '');
    };

    const projectSelected = pKey => {
        const found = keys.includes(pKey);
        console.log(pKey, 'exist in', keys, found);
        return found;
    };

    const renderRows = () => data && data.map(({
        projectKey,
        projectName,
        pmMail,
        pmName,
        tax,
        total,
        projectAmount,
        checked
    }, index) => (
        <tr key={projectKey} onClick={() => console.log('Click en row')}>
            <td className="text-center">
                <input style={styleCheckBox}
                    type="checkbox"
                    checked={checked}
                    onChange={() => toggleCheckbox(projectKey, index)}
                />
            </td>
            <td className="text-start">{projectKey}</td>
            <td className="text-start">{projectName}</td>
            <td className="text-start">{pmName}</td>
            <td className="text-start">{pmMail}</td>
            <td className="text-center">{projectAmount}</td>
            <td className="text-center">{tax}</td>
            <td className="text-center">{total}</td>
        </tr>
    ));

    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Reporte: {report.title}</h4>
            <InputSearcher name={'filter'} placeholder={'Escribe para filtrar...'} value={filter} onChange={onChangeFilter} onClean={onClean} />
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
                                <input  style={styleCheckBox}
                                    type="checkbox"
                                    checked={allChecked}
                                    onChange={toggleAllCheckboxes}
                                />
                            </th>
                            <th className="text-center fs-6" scope="col">Clave</th>
                            <th className="text-center fs-6" scope="col">Proyecto</th>
                            <th className="text-center fs-6" scope="col">PM</th>
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