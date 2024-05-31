import { useParams } from "react-router-dom";
import { useState } from "react";
import 'react-datepicker/dist/react-datepicker.css';
import { REPORT_MAP } from "../../helpers/utils";
import { InputSearcher } from "../custom/InputSearcher";
import { TableReport } from "./TableReport";
import { Select } from "../custom/Select";

export const PageReport = () => {
    
    const { reportName, repotTypeDefault } = useParams();
    const report = REPORT_MAP.find(rc => rc.reportName === reportName);

    const [filter, setFilter] = useState('');
    const [reportType, setReportType] = useState(`${repotTypeDefault}`);

    const onChangeFilter = ({ target }) => {
        setFilter(target.value);
    };

    const onChangeReportType = ({ target }) => {
        const reportType = target.value;
        if( reportType !== '' ) {
            setReportType(reportType);
            const r = report.labels.find( l => l.id === reportType);
        }
    }

    const onClean = () => {
        setFilter('');
    };

    const renderFilter = () => (
        <div>
            <InputSearcher name={'filter'} placeholder={'Escribe para filtrar...'} value={filter} onChange={onChangeFilter} onClean={onClean} />
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
            { report && !report.labels && <TableReport filter={ filter } params={ report.params } /> }
            { report && report.labels && (
                <div >
                    <h4 className={`card-title fw-bold text-${report.labels.find( l => l.id === reportType ).styleTitle}`}>
                        { report.labels.find( l => l.id === reportType ).value }
                    </h4>
                    { <TableReport filter={ filter } params={ report.options.find( o => o.id === reportType ) } /> }
                </div>
            ) }
        </div>
    )
}