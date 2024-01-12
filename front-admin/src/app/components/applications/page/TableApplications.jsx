import PropTypes from 'prop-types';
import { getApplicationsByProjectId } from '../../../services/ProjectService';
import { useDispatch, useSelector } from 'react-redux';
import { setMessage } from '../../../../store/alert/alertSlice';
import { useNavigate } from 'react-router-dom';
import { alertType } from '../../custom/alerts/types/types';
import { buildPayloadMessage } from '../../../helpers/utils';
import { useEffect, useState } from 'react';

export const TableApplications = ({ projectId }) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );

    const [applications, setApplications] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);

    const fetchApplications = () => {
        getApplicationsByProjectId(projectId).then( response => {
            if( response.code ) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else {
                setApplications(response.filter( r => r.application !== 'total' ));
                const { amount, tax, total } = response.find( r => r.application === 'total' );
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
            }
        }).catch( error => {
            console.log(error);
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar el proyecto, contacte al administrador', alertType.error)));
        });
    }

    useEffect(() => {
        fetchApplications();
    }, []);

    const handledSelect = id => {
        const urlRedirect = `/project/${ projectId }/application/${ id }/edit`;
        navigate(urlRedirect);
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
        tax,
        total,
        // status,
        leader,
        developer,
        hours,
        developmentDate,
        designDate,
        endDate,
        startDate,
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            {/* <td className="text-center">
                <button type="button" className="btn btn-primary">
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td> */}
            <th className="text-start" scope="row">{ application }</th>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            <td className="text-start">{ leader }</td>
            <td className="text-start">{ developer }</td>
            <td className="text-center">{ hours }</td>
            <td className="text-center">{ startDate }</td>
            <td className="text-center">{ designDate }</td>
            <td className="text-center">{ developmentDate }</td>
            <td className="text-center">{ endDate }</td>
            <td className="text-end text-primary">{ amount }</td>
            <td className="text-end text-primary">{ tax }</td>
            <td className="text-end text-primary">{ total }</td>
            {
                permissions.canDelProjApp && (
                    <td className="text-center">
                        <button type="button" className="btn btn-danger">
                            <span><i className="bi bi-trash"></i></span>
                        </button>
                    </td>
                )
            }
        </tr>
    ));

    return (
        <>
            <div className='table-responsive text-nowrap'>
                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            {/* <th className="text-center fs-6" scope="col">Editar</th> */}
                            <th className="text-center fs-6" scope="col">Aplicaci&oacute;n</th>
                            {/* <th className="text-center fs-6" scope="col">Status</th> */}
                            <th className="text-center fs-6" scope="col">L&iacute;der SAS</th>
                            <th className="text-center fs-6" scope="col">Desarrollador SAS</th>
                            <th className="text-center fs-6" scope="col">Horas</th>
                            <th className="text-center fs-6" scope="col">Fecha de inicio</th>
                            <th className="text-center fs-6" scope="col">Analisis y Dise&ntilde;o</th>
                            <th className="text-center fs-6" scope="col">Construcci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Cierre</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                            <th className="text-center fs-6" scope="col">Iva</th>
                            <th className="text-center fs-6" scope="col">Total</th>
                            { permissions.canDelProjApp && (<th className="text-center fs-6" scope="col">Borrar</th>) }
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                    <tfoot className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">TOTALES</th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th></th>
                            <th className="text-end fs-6" scope="col">{ totalAmount }</th>
                            <th className="text-end fs-6" scope="col">{ totalTax }</th>
                            <th className="text-end fs-6" scope="col">{ totalT }</th>
                            { permissions.canDelProjApp && (<th></th>) }
                        </tr>
                    </tfoot>
                </table>

            </div>
        </>
    )
}

TableApplications.propTypes = {
    pageSize: PropTypes.number,
}