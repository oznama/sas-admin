import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { deleteLogic, getCatalogChilds, save, update } from '../../services/CatalogService';
import { setMessage } from '../../../store/alert/alertSlice';
import { buildPayloadMessage, numberToString } from '../../helpers/utils';
import { alertType } from '../custom/alerts/types/types';

export const CatalogSingle = ({
  catalogId,
}) => {

  const dispatch = useDispatch();
  const { permissions } = useSelector( state => state.auth );

  const [catalogChilds, setCatalogChilds] = useState([]);
  const [id, setId] = useState(null);
  const [value, setValue] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('');
  const [catStatus, setCatStatus] = useState([]);

  const onChangeValue = ({ target }) => setValue(target.value);
  const onChangeDescription = ({ target }) => setDescription(target.value);
  const onChangeStatus = ({ target }) => setStatus(target.value);
  
  const handledSelect = id => {
    if ( permissions.canEditCat ) {
      setId(id);
      const catSelected = catalogChilds.find( cat => cat.id === id );
      setValue(catSelected.value);
      setDescription(catSelected.description ?? '');
      setStatus(numberToString(catSelected.status, ''));
    }
  };

  const getCatStatus = () => {
    getCatalogChilds(1000000001)
      .then( response => {
        if( response.code && response.code === 401 ) {
          dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
        }
        setCatStatus(response);
      }).catch( error => {
        console.log(error)
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
    fetchChilds();
    getCatStatus();
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
    save(request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡El registro se ha creado correctamente!', alertType.success)));
        setCatalogChilds([...catalogChilds, response]);
        cleanForm();
      }
    }).catch(error => {
      console.log(error);
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al crear el registro, contacte al administrador', alertType.error)));
    });
  }

  const updateChild = request => {
    update(id, request).then( response => {
      if(response.code && response.code !== 201) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡El registro se ha actualizado correctamente!', alertType.success)));
        fetchChilds();
        cleanForm();
      }
    }).catch(error => {
      console.log(error);
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al actualizar el registro, contacte al administrador', alertType.error)));
    });
  }

  const deleteChild = catalogId => {
    deleteLogic(catalogId).then( response => {
      if(response.code && response.code !== 200) {
        dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
      } else {
        dispatch(setMessage(buildPayloadMessage('¡Registro eliminado correctamente!', alertType.success)));
        fetchChilds();
      }
    }).catch(error => {
      console.log(error);
      dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error al eliminar el registro, contacte al administrador', alertType.error)));
    });
  }

  const cleanForm = () => {
    setId(null);
    setValue('');
    setDescription('');
  }

  const renderItems = () => catStatus && catStatus.map( option  => (
    <option key={ option.id } value={ option.id }>{ option.value }</option>
  ));

  const renderStatusSelect = () => permissions.isAdminRoot && id && (
    <select className="form-select"
      name="status" value={ status } required onChange={ onChangeStatus }>
        <option value=''>Seleccionar...</option>
        { renderItems() }
    </select>
  )

  const renderCleanButton = () => id && (
    <button type="button" className="btn btn-secondary" onClick={ () => cleanForm() }>Limpiar</button>
  )

  const renderFormCatalogSingle = () => catalogId && (permissions.canCreateCat || permissions.canEditCat) && (
    <div className='px-4'>
      <h5>Nuevo registro</h5>
      <form onSubmit={ onSubmit }>
        <div className="input-group mb-3">
          <input name="value" type="text" className="form-control" placeholder="Nombre" 
            value={ value } required onChange={ onChangeValue } autoComplete='off' /> 
          <input name="description" type="text" className="form-control" placeholder="description"
            maxLength={ 100 } autoComplete='off'
            value={ description } required onChange={ onChangeDescription } />
          { renderStatusSelect() }
          <div className="input-group-append">
            { renderCleanButton() }
            <button type="submit" className="btn btn-primary">Guardar</button>
          </div>
        </div>
      </form>
    </div>
  );
  
  const renderStatus = (status, statusDesc) => {
    const backColor = status === 2000100003 ? 'bg-danger' : ( status === 2000100001 ? 'bg-success' : 'bg-warning' );
    return (<span className={ `w-50 px-2 m-3 rounded ${backColor} text-white` }>{ statusDesc }</span>);
  }

  const renderRows = () => catalogChilds.map(({
    id,
    value,
    description,
    status,
    statusDesc,
    company
  }) => (
    <tr key={ id }>
      <th className="text-start" scope="row" onClick={ () => handledSelect(id) }>{ value }</th>
      <td className="text-start">{ description }</td>
      { permissions.isAdminRoot && (<td className="text-center">{ company }</td>)}
      { permissions.isAdminRoot && (<td className="text-center">{ renderStatus(status, statusDesc) }</td>)}
      { permissions.canDelCat && (<td className="text-center">
          <button type="button" className="btn btn-danger btn-sm" onClick={ () => deleteChild(id) }>
              <span><i className="bi bi-trash"></i></span>
          </button>
      </td>)}
    </tr>
  ));

  const renderSingleCatalog = () => (
    <div className='table-responsive text-nowrap'>
      <table className="table table-sm table-bordered table-striped table-hover">
        <thead className="thead-dark">
          <tr>
            <th className="text-center fs-6" scope="col">Nombre</th>
            <th className="text-center fs-6" scope="col">Descripci&oacute;n</th>
            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Empresa</th>)}
            { permissions.isAdminRoot && (<th className="text-center fs-6" scope="col">Estatus</th>)}
            { permissions.canDelCat && (<th className="text-center fs-6" scope="col">Borrar</th>)}
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
  canDelete: PropTypes.bool,
}