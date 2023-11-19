import { useDispatch, useSelector } from 'react-redux';
import { cleanMessage } from '../../../../../store/alert/alertSlice';

export const Alert = () => {

  const { message, type } = useSelector(state => state.alert);
  const dispatch = useDispatch();

  if (message === '') {
    return null;
  }
  return (
    <div className={ `alert alert-${ type } alert-dismissible fade show` } role="alert">
      { message }
      <button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close" onClick={ () => dispatch(cleanMessage()) } ></button>
    </div>
  )
}