import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { useDispatch } from 'react-redux';
import { changeLoading } from '../../../store/loading/loadingSlice';
import { get } from '../../services/LogService';
import { setMessage } from '../../../store/alert/alertSlice';
import { buildPayloadMessage } from '../../helpers/utils';

export const TableLog = ({
    tableName,
    recordId,
}) => {

    const dispatch = useDispatch();
    const [history, setHistory] = useState([]);

    const fetchLogs = () => {
        dispatch(changeLoading(true));
        get(tableName, recordId)
          .then( response => {
            if( response.code && response.code === 401 ) {
              dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            }
            setHistory(response);
            dispatch(changeLoading(false));
          }).catch( error => {
            dispatch(changeLoading(false));
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar la información, contacte al administrador', alertType.error)));
          });
      }
      
      useEffect(() => {
        fetchLogs();
      }, []);

    const renderDescription = (description) => {
        const lines = description.split('\n');
        const lis = lines.map( (line, index) => ( <li key={ index }>{ line }</li> ) )
        return (<ul>{ lis }</ul>);
    }

    const renderRows = () => history && history.map(({
        id,
        userName,
        date,
        description
    }) => (
        <tr key={ id }>
            <td className="text-start">{ renderDescription(description) }</td>
            <td className="text-start" scope="row">{ userName }</td>
            <td className="text-center">{ date }</td>
        </tr>
    ));

    return (
        <div className='table-responsive text-nowrap'>

            <table className="table table-sm table-bordered table-striped table-hover">
                <thead className="thead-dark">
                    <tr>
                        <th className="text-center fs-6" scope="col">Movimiento</th>
                        <th className="text-center fs-6" scope="col">Por</th>
                        <th className="text-center fs-6" scope="col">Fecha</th>
                    </tr>
                </thead>
                <tbody>
                    { renderRows() }
                </tbody>
            </table>

        </div>
    )
}


TableLog.propTypes = {
    tableName: PropTypes.string.isRequired,
    recordId: PropTypes.number.isRequired,
}