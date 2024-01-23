import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { getCatalogs } from '../../services/CatalogService';
import { alertType } from '../custom/alerts/types/types';
import { CatalogSingle } from './CatalogSingle';
import { displayNotification } from '../../helpers/utils';

export const Catalogs = () => {
    const dispatch = useDispatch();

    const [catalogs, setCatalogs] = useState([]);
    const [currentOpen, setCurrentOpen] = useState();
    
    const fetchCatalogs = () => {
        getCatalogs()
        .then( response => {
            if( response.code && response.code === 401 ) {
            displayNotification(dispatch, response.message, alertType.error);
            }
            setCatalogs(response);
        }).catch( error => {
            console.log(error);
            displayNotification(dispatch, genericErrorMsg, alertType.error);
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
                    { <CatalogSingle catalogId={ id } singleMode={ false } /> }
                </div>
            </div>
        </div> 
    ));

    return (
        <div className="table-responsive accordion" id="accordionCatalogs" style={{ height: '350px' }}>
            { renderAccordion() }
        </div>
    )
}
