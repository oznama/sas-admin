import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { changeLoading } from '../../../store/loading/loadingSlice';
import { getCatalogById, getCatalogChilds, save } from '../../services/CatalogService';
import { setMessage } from '../../../store/alert/alertSlice';
import { buildPayloadMessage } from '../../helpers/utils';
import { alertType } from '../custom/alerts/types/types';

export const CatalogSingle = ({
  catalogId,
}) => {

  const dispatch = useDispatch();

  const [catalog, setCatalog] = useState();
  const [catalogChilds, setCatalogChilds] = useState([]);
  const [value, setValue] = useState('');
  const [description, setDescription] = useState('');

  const onChangeValue = ({ target }) => setValue(target.value);
  const onChangeDescription = ({ target }) => setDescription(target.value);

  const fetchSingle = () => {
    dispatch(changeLoading(true));
    getCatalogById(catalogId)
      .then( response => {
        if( response.code && response.code === 401 ) {
          dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
        }
        setCatalog(response);
        fetchChilds(catalogId);
        dispatch(changeLoading(false));
      }).catch( error => {
        dispatch(changeLoading(false));
        dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar la información, contacte al administrador', alertType.error)));
      });
  }

  const fetchChilds = () => {
    getCatalogChilds(catalogId)
      .then( response => {
        if( response.code && response.code === 401 ) {
          dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
        }
        setCatalogChilds(response);
      }).catch( error => {
        dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar la información, contacte al administrador', alertType.error)));
      });
  }
  
  useEffect(() => {
    if( catalogId ) {
      fetchSingle();
    }
  }, []);

  const onSubmit = event => {
    console.log('OnSubmit');
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    request.catalogParent = catalogId;
    saveChild(request);   
  }

  const saveChild = request => {
    console.log('Saving ...');
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡El registro se ha creado correctamente!', alertType.success)));
        setCatalogChilds([...catalogChilds, request]);
      }
    }).catch(error => {
      console.log(error);
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear el registro, contacte al administrador', alertType.error)));
    });
  }

  const renderFormCatalogSingle = () => catalogId && (
    <>
      <h5>Nuevo registro</h5>
      <form onSubmit={ onSubmit }>
        <div className="input-group mb-3">
          <input type="text" className="form-control" placeholder="Nombre" value={ value } required onChange={ onChangeValue } /> 
          <input type="text" className="form-control w-50" placeholder="description"
            maxLength={ 100 }
            value={ description } required onChange={ onChangeDescription } />
          <div className="input-group-append">
            <button type="submit" className="btn btn-primary">Guardar</button>
          </div>
        </div>
      </form>
    </>
  );

  const renderRows = () => catalogChilds.map(({
    id,
    value,
    description,
    isRequired,
    status,
  }) => (
    <tr key={ id } onClick={ () => console.log(id) }>
      <th className="text-start" scope="row">{ value }</th>
      <td className="text-start">{ description }</td>
    </tr>
  ));

  const renderSingleCatalog = () => (
    <div className='table-responsive text-nowrap'>
      <table className="table table-sm table-bordered table-striped table-hover">
        <thead className="thead-dark">
          <tr>
            <th className="text-center fs-6" scope="col">Nombre</th>
            <th className="text-center fs-6" scope="col">Descripci&oacute;n</th>
          </tr>
        </thead>
        <tbody>
          { renderRows() }
        </tbody>
      </table>
    </div>
  );

  return (
    <>
      { renderFormCatalogSingle() }
      { renderSingleCatalog() }
    </>
  )
}