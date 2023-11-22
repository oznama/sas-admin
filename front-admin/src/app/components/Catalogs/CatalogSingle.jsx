import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { changeLoading } from '../../../store/loading/loadingSlice';
import { getCatalogChilds, save, update } from '../../services/CatalogService';
import { setMessage } from '../../../store/alert/alertSlice';
import { buildPayloadMessage } from '../../helpers/utils';
import { alertType } from '../custom/alerts/types/types';

export const CatalogSingle = ({
  catalogId,
}) => {

  const dispatch = useDispatch();

  const [catalogChilds, setCatalogChilds] = useState([]);
  const [id, setId] = useState(null);
  const [value, setValue] = useState('');
  const [description, setDescription] = useState('');

  const onChangeValue = ({ target }) => setValue(target.value);
  const onChangeDescription = ({ target }) => setDescription(target.value);
  
  const handledSelect = id => {
    setId(id);
    const catSelected = catalogChilds.find( cat => cat.id === id );
    setValue(catSelected.value);
    setDescription(catSelected.description ?? '');
  };

  const fetchChilds = () => {
    dispatch(changeLoading(true));
    getCatalogChilds(catalogId)
      .then( response => {
        if( response.code && response.code === 401 ) {
          dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
        }
        setCatalogChilds(response);
        dispatch(changeLoading(false));
      }).catch( error => {
        dispatch(changeLoading(false));
        dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al cargar la información, contacte al administrador', alertType.error)));
      });
  }
  
  useEffect(() => {
    fetchChilds();
  }, []);

  const onSubmit = event => {
    event.preventDefault();
    const data = new FormData(event.target);
    const request = Object.fromEntries(data.entries());
    request.catalogParent = catalogId;
    if ( id ) {
      updateChild(request);
    } else {
      saveChild(request);
    }
  }

  const saveChild = request => {
    dispatch(changeLoading(true));
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡El registro se ha creado correctamente!', alertType.success)));
        setCatalogChilds([...catalogChilds, response]);
        cleanForm();
      }
      dispatch(changeLoading(false));
    }).catch(error => {
      dispatch(changeLoading(false));
      console.log(error);
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear el registro, contacte al administrador', alertType.error)));
    });
  }

  const updateChild = request => {
    dispatch(changeLoading(true));
    update(id, request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡El registro se ha actualizado correctamente!', alertType.success)));
        fetchChilds();
        cleanForm();
      }
      dispatch(changeLoading(false));
    }).catch(error => {
      dispatch(changeLoading(false));
      console.log(error);
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar el registro, contacte al administrador', alertType.error)));
    });
  }

  const cleanForm = () => {
    setId(null);
    setValue('');
    setDescription('');
  }

  const renderCleanButton = () => id && (
    <button type="button" className="btn btn-secondary" onClick={ () => cleanForm() }>Limpiar</button>
  )

  const renderFormCatalogSingle = () => catalogId && (
    <div className='px-4'>
      <h5>Nuevo registro</h5>
      <form onSubmit={ onSubmit }>
        <div className="input-group mb-3">
          <input name="value" type="text" className="form-control" placeholder="Nombre" 
            value={ value } required onChange={ onChangeValue } autoComplete='off' /> 
          <input name="description" type="text" className="form-control" placeholder="description"
            maxLength={ 100 } autoComplete='off'
            value={ description } required onChange={ onChangeDescription } />
          <div className="input-group-append">
            { renderCleanButton() }
            <button type="submit" className="btn btn-primary">Guardar</button>
          </div>
        </div>
      </form>
    </div>
  );

  const renderRows = () => catalogChilds.map(({
    id,
    value,
    description,
  }) => (
    <tr key={ id } onClick={ () => handledSelect(id) }>
      <th className="text-start" scope="row">{ value }</th>
      <td className="text-start">{ description }</td>
      <td className="text-center">
          <button type="button" className="btn btn-danger">
              <span><i className="bi bi-trash"></i></span>
          </button>
      </td>
    </tr>
  ));

  const renderSingleCatalog = () => (
    <div className='table-responsive text-nowrap'>
      <table className="table table-sm table-bordered table-striped table-hover">
        <thead className="thead-dark">
          <tr>
            <th className="text-center fs-6" scope="col">Nombre</th>
            <th className="text-center fs-6" scope="col">Descripci&oacute;n</th>
            <th className="text-center fs-6" scope="col">Borrar</th>
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

CatalogSingle.propTypes = {
  catalogId: PropTypes.number.isRequired,
}