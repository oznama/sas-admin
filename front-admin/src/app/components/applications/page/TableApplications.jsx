import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { Pagination } from '../../custom/pagination/page/Pagination';
import { LoadingContext } from '../../custom/loading/context/LoadingContext';
import { getProjects } from '../../../api/ApiDummy';
import { renderErrorMessage } from '../../../helpers/handleErrors';

const applicationsDummy = [
    {
        id: 1,
        name: 'PMT',
        amount: '$496.88',
        status: 4,
        leader: 'Alvaro Mendoza',
        developer:  'Alvaro Mendoza',
        hours: 40,
        startDate: '26/01/2023',
        analAndDesigDate: '30/01/2023',
        dueDate: '02/02/2023',
    },
    {
        id: 2,
        name: 'SICB2',
        amount: '$662.34',
        status: 4,
        leader: 'Alvaro Mendoza',
        developer:  'Alvaro Mendoza',
        hours: 24,
        startDate: '26/01/2023',
        analAndDesigDate: '30/01/2023',
        dueDate: '02/02/2023',
    },
    {
        id: 3,
        name: 'CVM',
        amount: '$882.90',
        status: 4,
        leader: 'Alvaro Mendoza',
        developer:  'Alvaro Mendoza',
        hours: 24,
        startDate: '26/01/2023',
        analAndDesigDate: '30/01/2023',
        dueDate: '02/02/2023',
    },
    {
        id: 4,
        name: 'GNR',
        amount: '$1,176.91',
        status: 2,
        leader: 'Alvaro Mendoza',
        developer:  'Alvaro Mendoza',
        hours: 32,
        startDate: '26/01/2023',
        analAndDesigDate: '30/01/2023',
        dueDate: '02/02/2023',
    },
    {
        id: 5,
        name: 'CTM',
        amount: '$1,568.82',
        status: 4,
        leader: 'Alvaro Mendoza',
        developer:  'Alvaro Mendoza',
        hours: 24,
        startDate: '26/01/2023',
        analAndDesigDate: '30/01/2023',
        dueDate: '02/02/2023',
    },
    {
        id: 6,
        name: 'QA Batch',
        amount: '$2,091.24',
        status: 3,
        leader: 'Juan Baños Soto',
        developer:  'Juan Baños Soto',
        hours: 24,
        startDate: '06/01/2023',
        analAndDesigDate: '',
        dueDate: '30/03/2023',
    },
]

export const TableApplications = ({
    pageSize = 10,
    sort = 'creationDate,desc',
}) => {

    const navigate = useNavigate();

    const { changeLoading } = useContext( LoadingContext );
    const [currentPage, setCurrentPage] = useState(0);
    const [applications, setApplications] = useState(applicationsDummy);
    const [totalApplications, setTotalApplications] = useState(0);
    const [msgError, setMsgError] = useState();

    const handledSelect = id => {
        navigate(`/application/${id}/edit`);
    }

    const renderStatus = (status) => {
        const backColor = status === 3 ? 'bg-danger' : ( status === 1 || status === 4 ? 'bg-success' : 'bg-warning' );
        // TODO Remove by value
        const statusDesc = status === 3 ? 'Desfasado' : ( status === 2 ? 'Retrazado' : 'En tiempo' );
        return (<span className={ `w-100 p-1 rounded ${backColor} text-white` }>{ statusDesc }</span>);
    }

    const renderRows = () => applications && applications.map(({
        id,
        name,
        amount,
        status,
        leader,
        developer,
        hours,
        startDate,
        analAndDesigDate,
        dueDate,
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <th className="text-start" scope="row">{ name }</th>
            <td className="text-end text-primary">{ amount }</td>
            <td className="text-center">{ renderStatus(status, '') }</td>
            <td className="text-start">{ leader }</td>
            <td className="text-start">{ developer }</td>
            <td className="text-center">{ hours }</td>
            <td className="text-center">{ startDate }</td>
            <td className="text-center">{ analAndDesigDate }</td>
            <td className="text-center">{ dueDate }</td>
        </tr>
    ));

    return (
        <>
            { renderErrorMessage(msgError, null) }
            <div className='table-responsive text-nowrap'>

                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">Aplicaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">Monto</th>
                            <th className="text-center fs-6" scope="col">Status</th>
                            <th className="text-center fs-6" scope="col">L&iacute;der SAS</th>
                            <th className="text-center fs-6" scope="col">Desarrollador SAS</th>
                            <th className="text-center fs-6" scope="col">Horas</th>
                            <th className="text-center fs-6" scope="col">Inicio</th>
                            <th className="text-center fs-6" scope="col">Analisis y Dise&ntilde;o</th>
                            <th className="text-center fs-6" scope="col">Construcci&oacute;n</th>
                        </tr>
                    </thead>
                    <tbody>
                        { renderRows() }
                    </tbody>
                </table>

            </div>
            <Pagination
                currentPage={ currentPage + 1 }
                totalCount={ totalApplications }
                pageSize={ pageSize }
                onPageChange={ page => onPaginationClick(page) } 
            />
        </>
    )
}

TableApplications.propTypes = {
    pageSize: PropTypes.number,
}