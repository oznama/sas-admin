import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { downloadExcel, sendNotification } from "../../services/NativeService";
import 'react-datepicker/dist/react-datepicker.css';
import { REPORT_MAP, displayNotification } from "../../helpers/utils";
import { useDispatch, useSelector } from "react-redux";
import { alertType } from "../custom/alerts/types/types";
import { InputSearcher } from "../custom/InputSearcher";
import { TableReport } from "./TableReport";
import { Select } from "../custom/Select";
import { setCurrentFilter } from "../../../store/report/reportSlice";

export const PageReport = () => {

    const dispatch = useDispatch();
    
    const { reportName, repotTypeDefault } = useParams();
    const report = REPORT_MAP.find(rc => rc.reportName === reportName);

    const [reports, setReports] = useState();
    const [reportType, setReportType] = useState();
    
    const [allChecked, setAllChecked] = useState(true);
    const [keys, setKeys] = useState([]);
    const [allKeys, setAllKeys] = useState([]);
    const [isCheck, setIsCheck] = useState(true);

    const {currentFilter} = useSelector(state => state.reportReducer);
    const {keysToPrint} = useSelector(state => state.reportReducer);
    const {stack} = useSelector(state => state.reportReducer);
    // console.log('stack', stack)
    const check = stack.some(obj => obj.bool === true);
    // const check = true;


    
    useEffect(() => {
        setReportType( `${repotTypeDefault}` );
        if( report && report.labels) {
            setReports(report.labels);
        }
    }, [report])
    

    const onChangeFilter = ({ target }) => {
        dispatch(setCurrentFilter(target.value));
    };

    const onChangeReportType = ({ target }) => {
        const reportType = target.value;
        if( reportType !== '' ) {
            setReportType(reportType);
            const r = report.labels.find( l => l.id === reportType);
            const reports = reportType === `${repotTypeDefault}` ? report.labels : [r];
            setReports( reports );
        }
    }

    const onClickDownload = () => {
        let downloadKeys = keys.length === allKeys.length ? [] : [...keys];
        console.log('downloadKeys', downloadKeys);
        // console.log('keys', keys);
        console.log('allKeys', allKeys);
        // downloadExcel(report.context, downloadKeys).then(response => response.blob()
        // ).then(blob => {
        //     const url = window.URL.createObjectURL(blob);
        //     const a = document.createElement('a');
        //     a.href = url;
        //     a.download = `${report.excel}.xlsx`;
        //     document.body.appendChild(a);
        //     a.click();
        //     a.remove();
        // });
    };

    const onClickSendEmail = () => {
        // console.log('Llaves: ',keys)
        // sendNotification(report.context, keys).then(resp => {
        //     console.log(resp);
        //     displayNotification(dispatch, '¡Correo enviado!', alertType.success);
        // }).catch(err => {
        //     console.error('Error fetching data:', err);
        // });
    };

    const onClean = () => {
        dispatch(setCurrentFilter(''));
    };

    const renderFilter = () => (
        <div>
            <InputSearcher name={'filter'} placeholder={'Escribe para filtrar...'} value={currentFilter} onChange={onChangeFilter} onClean={onClean} />
            {
                report && report.labels && reportType &&
                <div className='col-6'>
                    <Select name="reportType" label="" 
                    options={ report.options } value={ reportType } required onChange={ onChangeReportType } />
                </div>
            }
        </div>
    )

    return (
        <div className='px-5'>
            <h4 className="card-title fw-bold">Reporte: {report.title}</h4>
            { renderFilter() }
            <div className="py-2 d-flex flex-row-reverse bd-highlight">
                <button type="button" disabled={!check} className={`btn ${check ? 'btn-success' : 'btn-secondary'}`} onClick={onClickSendEmail}>
                    Enviar Correo &nbsp;
                    <span>
                        <i className="bi bi-envelope"></i>
                    </span>
                </button>
                &nbsp;
                <button type="button" disabled={!check} className={`btn ${check ? 'btn-primary' : 'btn-secondary'}`} onClick={onClickDownload}>
                    Exportar &nbsp;
                    <span>
                        <i className="bi bi-arrow-bar-down"></i>
                    </span>
                </button>
            </div>
            { report && !report.labels && 
                <TableReport 
                    params={ report.params } 
                    setIsCheck={ setIsCheck } 
                    // allChecked={ allChecked } 
                    // setAllChecked={setAllChecked}
                    // keys={keys} 
                    // setKeys={setKeys} 
                    allKeys={allKeys} 
                    setAllKeys={setAllKeys}
                /> 
            }
            { report && report.labels && reports && reports.map( (l, index) => (
                <div key={ index } className="border rounded">
                    <h4 className={`card-title fw-bold text-${l.styleTitle}`}>{ l.value }</h4>
                    { 
                        <TableReport 
                            params={ report.options.find( o => o.id === l.id ) } 
                            setIsCheck={ setIsCheck } 
                            // allChecked={ allChecked } 
                            // setAllChecked={setAllChecked}
                            // keys={keys} 
                            // setKeys={setKeys} 
                            allKeys={allKeys} 
                            setAllKeys={setAllKeys}
                        /> 
                    }
                </div>
            )) }
        </div>
    )
}