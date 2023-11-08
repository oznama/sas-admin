import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { renderErrorMessage } from '../../../helpers/handleErrors';

export const TableApplications = ({
    applications,
}) => {

    const navigate = useNavigate();

    const [msgError, setMsgError] = useState();

    const handledSelect = id => {
        navigate(`/application/${id}/edit`);
    }

    // const renderStatus = (status) => {
    //     const backColor = status === 3 ? 'bg-danger' : ( status === 1 || status === 4 ? 'bg-success' : 'bg-warning' );
    //     // TODO Remove by value
    //     const statusDesc = status === 3 ? 'Desfasado' : ( status === 2 ? 'Retrazado' : 'En tiempo' );
    //     return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    // }

    const renderRows = () => applications && applications.map(({
        id,
        application,
        amount,
        // status,
        leader,
        developer,
        hours,
        developmentDate,
        designDate,
        endDate,
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <th className="text-start" scope="row">{ application }</th>
            <td className="text-end text-primary">{ amount }</td>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            <td className="text-start">{ leader }</td>
            <td className="text-start">{ developer }</td>
            <td className="text-center">{ hours }</td>
            <td className="text-center">{ designDate }</td>
            <td className="text-center">{ developmentDate }</td>
            <td className="text-center">{ endDate }</td>
        </tr>
    ));

    return (
        <>
            { renderErrorMessage(msgError, null, null) }
            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Aplicaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                            {/* <th className="text-center fs-6" scope="col">Status</th> */}
                            <th className="text-center fs-6" scope="col">L&iacute;der SAS</th>
                            <th className="text-center fs-6" scope="col">Desarrollador SAS</th>
                            <th className="text-center fs-6" scope="col">Horas</th>
                            <th className="text-center fs-6" scope="col">Analisis y Dise&ntilde;o</th>
                            <th className="text-center fs-6" scope="col">Construcci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Cierre</th>
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                </table>

            </div>
        </>
    )
}

TableApplications.propTypes = {
    pageSize: PropTypes.number,
}