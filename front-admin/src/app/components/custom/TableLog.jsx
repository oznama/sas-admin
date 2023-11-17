import PropTypes from 'prop-types';

export const TableLog = ({
    history,
}) => {
    const renderRows = () => history && history.map(({
        id,
        userName,
        date,
        event,
        detail,
        description
    }) => (
        <tr key={ id }>
            <th className="text-start" scope="row">{ userName }</th>
            <td className="text-center">{ event }</td>
            <td className="text-center">{ detail }</td>
            <td className="text-center">{ date }</td>
            <th className="text-start">{ description }</th>
        </tr>
    ));

    return (
        <div className='table-responsive text-nowrap'>

            <table className="table table-sm table-bordered table-striped table-hover">
                <thead className="thead-dark">
                    <tr>
                        <th className="text-center fs-6" scope="col">Usuario</th>
                        <th className="text-center fs-6" scope="col">Acci&oacute;n</th>
                        <th className="text-center fs-6" scope="col">Movimiento</th>
                        <th className="text-center fs-6" scope="col">Fecha</th>
                        <th className="text-center fs-6" scope="col">Detalle</th>
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