import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { getReport, getReportKeys } from "../../services/NativeService";
import 'react-datepicker/dist/react-datepicker.css';
import { REPORT_MAP, styleCheckBox } from "../../helpers/utils";
import { Pagination } from "../custom/pagination/page/Pagination";
import { addStack, setTrueById, setFalseById, setKeysToPrint, pushKey, pullKey } from "../../../store/report/reportSlice";
import { useDispatch, useSelector } from "react-redux";

export const TableReport = ({ params, setIsCheck }) => {
    const { stack, keysToPrint } = useSelector(state => state.reportReducer);
    console.log('llaves pa imprimir: ',keysToPrint);
    const dispatch = useDispatch();
    const { reportName } = useParams();
    const report = REPORT_MAP.find(rc => rc.reportName === reportName);

    const [data, setData] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const pageSize = 10;
    const [totalReports, setTotalReports] = useState(0);

    const [allChecked, setAllChecked] = useState(true);
    const [keys, setKeys] = useState([]);
    const [allKeysT, setAllKeysT] = useState([]);

    const { currentFilter } = useSelector(state => state.reportReducer);

    const handleAddStack = () => {
        const id = params ? params.id : 1;
        let bool = keys.length > 0 || allChecked;
        const isInStack = stack.some(item => item.id === params.id);
        if (!isInStack) {
            dispatch(addStack({ id, bool }));
        }
    };

    const handleSetTrueById = (id) => {
        dispatch(setTrueById(id));
    };

    const handleSetFalseById = (id) => {
        dispatch(setFalseById(id));
    };

    const fetchOrders = (currentPage) => {
        getReport(report.context, currentPage, pageSize, currentFilter, params).then(resp => {
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
        getReportKeys(report.context, currentFilter, params).then(resp => {
            setAllKeysT(resp);
        }).catch(err => {
            console.error('Error fetching data:', err);
        });
    }

    const anyCheck = () => {
        if (allChecked || keys.length > 0) {
        }
    }

    const onPaginationClick = page => {
        setCurrentPage(page);
        fetchOrders(page);
    };

    useEffect(() => {
        if (report) {
            fetchOrders(currentPage);
            allKeysGet();
            handleAddStack();
        }
    }, [report, currentFilter, params]);

    useEffect(() => {
        if (allKeysT.length > 0) {
            if (keysToPrint.length === 0) {
                dispatch(setKeysToPrint(allKeysT));
            } else {
                allKeysT.forEach(key => {
                    dispatch(pushKey(key));
                });
            }
        }
    }, [allKeysT]);

    useEffect(() => {
        if (data.length > 0 && allChecked) {
            setKeys(allKeysT);  // Select all keys when allChecked is true
        }
        anyCheck();
        setIsCheck(allChecked || keys.length > 0);
    }, [allChecked, data]);

    const toggleAllCheckboxes = () => {
        const newAllChecked = !allChecked;
        setAllChecked(newAllChecked);
        const updatedKeys = newAllChecked ? allKeysT : [];
        setKeys(updatedKeys);
        const updatedData = data.map(item => ({ ...item, checked: newAllChecked }));
        setData(updatedData);
        if (newAllChecked) {
            allKeysT.forEach(key => dispatch(pushKey(key)));
        } else {
            allKeysT.forEach(key => dispatch(pullKey(key)));
        }
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
            dispatch(setTrueById(params.id));
        } else {
            setAllChecked(false);
            dispatch(setFalseById(params.id));
        }

        if (dataUpdated[index].checked) {
            dispatch(pushKey(pKey));
        } else {
            dispatch(pullKey(pKey));
        }
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
        <div className='table-responsive text-nowrap'>
            <table className="table table-sm table-bordered table-striped table-hover">
                <thead className="thead-dark">
                    <tr>
                        <th className="text-center fs-6" scope="col">
                            <input style={styleCheckBox}
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
                currentPage={currentPage + 1}
                totalCount={totalReports}
                pageSize={pageSize}
                onPageChange={page => onPaginationClick(page)}
            />
        </div>
    );
}
