import PropTypes from 'prop-types';

export const TableLog = ({
    history,
}) => {

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
    history: PropTypes.array.isRequired
}

TableLog.defaultProps = {
    history: [

    ]
}