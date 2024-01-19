import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { getCatalogs, save, update } from '../../services/CatalogService';
import { setMessage } from '../../../store/alert/alertSlice';
import { buildPayloadMessage } from '../../helpers/utils';
import { alertType } from '../custom/alerts/types/types';
import { CatalogSingle } from './CatalogSingle';

export const Catalogs = () => {
    const dispatch = useDispatch();

    const [catalogs, setCatalogs] = useState([]);
    const [currentOpen, setCurrentOpen] = useState();
    
    const fetchCatalogs = () => {
        getCatalogs()
        .then( response => {
            if( response.code && response.code === 401 ) {
            dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            }
            setCatalogs(response);
        }).catch( error => {
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar la información, contacte al administrador', alertType.error)));
        });
    }
  
    useEffect(() => {
        fetchCatalogs();
    }, []);

    const renderAccordion = () => catalogs.map(({
        id,
        value,
        description,
        companyId,
        company,
    }) => (
        <div key={ id } className="accordion-item" onClick={ () => setCurrentOpen(id === currentOpen ? null : id) }>
            <h2 className="accordion-header" id={ `heading${id}` }>
                <button className="accordion-button collapsed" type="button" 
                    data-bs-toggle="collapse" data-bs-target={ `#collapse${id}` } aria-expanded="false" aria-controls={ `collapse${id}` }>
                    { `${value} ${ description ? '-' : '' } ${description ?? ''}` }
                </button>
            </h2>
            <div id={ `collapse${id}` } className="accordion-collapse collapse" aria-labelledby={ `heading${id}` } data-bs-parent="#accordionCatalogs">
                <div className="accordion-body">
                    { <CatalogSingle catalogId={ id } /> }
                </div>
            </div>
        </div> 
    ));

    return (
        <div className="accordion" id="accordionCatalogs">
            { renderAccordion() }
        </div>
    )
}
