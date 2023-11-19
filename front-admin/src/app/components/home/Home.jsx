import { TableProject } from '../projects/TableProjects';
import { useNavigate } from 'react-router-dom';
import { Alert } from '../custom/alerts/page/Alert';
import { useDispatch, useSelector } from 'react-redux';
import { cleanProject } from '../../../store/project/projectSlice';

export const Home = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { permissions } = useSelector( state => state.auth );
    console.log(permissions);

    const handleAddProject = () => {
        dispatch(cleanProject());
        navigate(`/project/add`);
    }

    const renderAddButton = () => permissions.canCreateProj && (
        <div className="d-flex flex-row-reverse pb-2">
            <button type="button" className="btn btn-primary" onClick={ handleAddProject }>
                <span className="bi bi-plus"></span>
            </button>
        </div>
    );

    return (
        <div className='px-5'>
            <div>
                <div className="d-flex justify-content-center">
                    <h1 className="fs-4 card-title fw-bold mb-1">Proyectos</h1>
                </div>
                { renderAddButton() }
            </div>
            <Alert />
            <TableProject />
        </div>
    );
};