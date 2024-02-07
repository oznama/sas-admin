import PropTypes from 'prop-types';
import { deleteApplicationLogic, getApplicationsByProjectId } from '../../../services/ProjectService';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { alertType } from '../../custom/alerts/types/types';
import { displayNotification, genericErrorMsg, styleTableRow, styleTableRowBtn } from '../../../helpers/utils';
import { useEffect, useState } from 'react';
import { setCurrentAppTab } from '../../../../store/project/projectSlice';
import { changeLoading } from '../../../../store/loading/loadingSlice';

export const TableApplications = ({ projectId }) => {

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { permissions } = useSelector( state => state.auth );

    const [applications, setApplications] = useState([]);
    const [totalAmount, setTotalAmount] = useState(0);
    const [totalTax, setTotalTax] = useState(0);
    const [totalT, setTotalT] = useState(0);

    const fetchApplications = () => {
        dispatch( changeLoading(true) );
        getApplicationsByProjectId(projectId).then( response => {
            if( response.code ) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                setApplications(response.filter( r => r.application !== 'total' ));
                const { amount, tax, total } = response.find( r => r.application === 'total' );
                setTotalAmount( amount );
                setTotalTax( tax ) ;
                setTotalT( total );
            }
            dispatch( changeLoading(false) );
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
            dispatch( changeLoading(false) );
        });
    }

    useEffect(() => {
        fetchApplications();
    }, []);

    const handledSelect = id => {
        dispatch(setCurrentAppTab(1));
        const urlRedirect = `/project/${ projectId }/application/${ id }/edit`;
        navigate(urlRedirect);
    }

    const renderStatus = status => {
        const backColor = status ? 'bg-success' : 'bg-danger';
        const desc = status ? 'Activo' : 'Inactivo';
        return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ desc }</span>);
    }

    const deleteChild = (id, active) => {
        deleteApplicationLogic(id).then( response => {
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, response.message, alertType.error);
            } else {
                displayNotification(dispatch, `Aplicación ${ active ? 'eliminada' : 'reactivada' } correctamente!`, active ? alertType.warning : alertType.success);
                fetchApplications();
            }
        }).catch(error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
        });
    }

    const renderRows = () => applications && applications.map(({
        id,
        application,
        amount,
        tax,
        total,
        leader,
        developer,
        hours,
        developmentDate,
        designDate,
        endDate,
        startDate,
        active
    }) => (
        <tr key={ id }>
            {/* <td className="text-center">
                <button type="button" className="btn btn-primary">
                    <span><i className="bi bi-pencil"></i></span>
                </button>
            </td> */}
            <th className="text-center" style={ styleTableRow } scope="row">{ application }</th>
            {/* <td className="text-center">{ renderStatus(status, '') }</td> */}
            <td className="text-start" style={ styleTableRow }>{ leader }</td>
            <td className="text-start" style={ styleTableRow }>{ developer }</td>
            <td className="text-center" style={ styleTableRow }>{ hours }</td>
            <td className="text-center" style={ styleTableRow }>{ startDate }</td>
            <td className="text-center" style={ styleTableRow }>{ designDate }</td>
            <td className="text-center" style={ styleTableRow }>{ developmentDate }</td>
            <td className="text-center" style={ styleTableRow }>{ endDate }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ amount }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ tax }</td>
            <td className="text-end text-primary" style={ styleTableRow }>{ total }</td>
            <td className="text-center">{ renderStatus(active) }</td>
            <td className="text-center" style={ styleTableRow }>
                <button type="button" className={`btn btn-${ active && permissions.canEditEmp ? 'success' : 'primary' } btn-sm`} style={ styleTableRowBtn } onClick={ () => handledSelect(id) }>
                    <span><i className={`bi bi-${ active && permissions.canEditEmp ? 'pencil-square' : 'eye'}`}></i></span>
                </button>
            </td>
            {
                permissions.canDelProjApp && (
                    <td className="text-center" style={ styleTableRow }>
                        <button type="button" className={`btn btn-${ active ? 'danger' : 'warning'} btn-sm`} style={ styleTableRowBtn } onClick={ () => deleteChild(id, active) }>
                            <span><i className={`bi bi-${ active ? 'trash' : 'folder-symlink'}`}></i></span>
                        </button>
                    </td>
                )
            }
        </tr>
    ));

    return (
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
                        <th className="text-center fs-6" scope="col">Estatus</th>
                        <th className="text-center fs-6" scope="col">{permissions.canEditProjApp ? 'Editar' : 'Ver'}</th>
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
    )
}

TableApplications.propTypes = {
    pageSize: PropTypes.number,
}