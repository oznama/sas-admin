import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { useDispatch } from 'react-redux';
import { get } from '../../services/LogService';
import { displayNotification, genericErrorMsg } from '../../helpers/utils';

export const TableLog = ({
    tableName,
    recordId,
}) => {

    const dispatch = useDispatch();
    const [history, setHistory] = useState([]);

    const fetchLogs = () => {
        get(tableName, recordId)
          .then( response => {
            if( response.code && response.code === 401 ) {
              displayNotification(dispatch, response.message, alertType.error);
            }
            setHistory(response);
          }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
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
        <div className='table-responsive text-nowrap' style={{ height: '350px' }}>

            <table className="table table-sm table-bordered table-striped table-hover">
                <thead className="thead-dark">
                    <tr>
                        <th className="text-center fs-6" scope="col">Movimiento</th>
                        <th className="text-center fs-6" scope="col">Usuario</th>
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