import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { downloadExcel, getReport, getReportKeys, sendNotification } from "../../services/NativeService";
import 'react-datepicker/dist/react-datepicker.css';
import { REPORT_MAP, displayNotification, styleCheckBox } from "../../helpers/utils";
import { Pagination } from "../custom/pagination/page/Pagination";
import { useDispatch, useSelector } from "react-redux";
import { alertType } from "../custom/alerts/types/types";

export const TableReport = ({
    filter,
    params
}) => {

    const { permissions } = useSelector( state => state.auth );
    
    const { reportName } = useParams();
    const report = REPORT_MAP.find(rc => rc.reportName === reportName);

    const dispatch = useDispatch();
    
    const [data, setData] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const pageSize = 10;
    const [totalReports, setTotalReports] = useState(0);

    const [allChecked, setAllChecked] = useState(true);
    const [keys, setKeys] = useState([]);
    const [allKeys, setAllKeys] = useState([]);
    const [isCheck, setIsCheck] = useState(true);

    const fetchOrders = (currentPage, filter) => {
        getReport(report.context, currentPage, pageSize, filter, params).then(resp => {
            const data = resp.content.map(r => ({
                ...r,
                checked: allChecked || keys.includes(r.projectKey)
            }));
            setData(data);
            setTotalReports(resp.totalElements);
        }).catch(err => {
            console.error('Error fetching data:', err);
        });
    };

    const allKeysGet = () => {
        getReportKeys(report.context, filter, params).then(resp => {
            setAllKeys(resp);
        }).catch(err => {
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
    }, [report, filter, params]);

    useEffect(() => {
        if (data.length > 0 && allChecked) {
            setKeys(allKeys);  // Select all keys when allChecked is true
        }
        setIsCheck(allChecked || keys.length > 0);
    }, [allChecked, data]);

    const onClickDownload = () => {
        let downloadKeys = keys.length === allKeys.length ? [] : [...keys];
        downloadExcel(report.context, downloadKeys).then(response => response.blob()
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

    const onClickSendEmail = () => {
        sendNotification(report.context, keys).then(resp => {
            displayNotification(dispatch, '¡Correo enviado!', alertType.success);
        }).catch(err => {
            console.error('Error fetching data:', err);
        });
    };

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

    const renderButtons = () => (
        <div className="py-2 d-flex flex-row-reverse bd-highlight">
            <button type="button" disabled={!isCheck} className={`btn ${isCheck ? 'btn-success' : 'btn-secondary'}`} onClick={onClickSendEmail}>
                Enviar Correo &nbsp;
                <span>
                    <i className="bi bi-envelope"></i>
                </span>
            </button>
            &nbsp;
            <button type="button" disabled={!isCheck} className={`btn ${isCheck ? 'btn-primary' : 'btn-secondary'}`} onClick={onClickDownload}>
                Exportar &nbsp;
                <span>
                    <i className="bi bi-arrow-bar-down"></i>
                </span>
            </button>
        </div>
    )

    const renderRows = () => data && data.map(({
        projectKey,
        projectName,
        pmMail,
        pmName,
        bossName,
        bossMail,
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
            { permissions.isAdminRoot && (<td className="text-start">{ bossName }</td>) }
            { permissions.isAdminRoot && (<td className="text-center">{ bossMail }</td>) }
            <td className="text-center">{projectAmount}</td>
            <td className="text-center">{tax}</td>
            <td className="text-center">{total}</td>
        </tr>
    ));

    const renderTable = () => (
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
                        { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Jefe</th>) }
                        { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Correo</th>) }
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
                totalCount={ totalReports }
                pageSize={ pageSize }
                onPageChange={  page => onPaginationClick(page)  } 
            />
        </div>
    )

    return (
        <div>
            { renderButtons() }
            { renderTable() }
        </div>
    )
}