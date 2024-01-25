import { useDispatch, useSelector } from 'react-redux';
import { hide } from '../../../../../store/alert/alertSlice';

export const Alert = ({
  padded
}) => {
  const { message, type, show } = useSelector(state => state.alert);
  const dispatch = useDispatch();

  const clasSNameAlert = show ? `${padded ? 'ms-5 me-5' : '' } alert alert-${ type } fade ${ show ? 'show' : '' }` : 'd-none';
  
  return (
    <div className={ clasSNameAlert } >
      <div className='d-flex justify-content-between'>
        <div>
        </div>
        <span className='fw-bold'>{ message }</span>
        <button type="button" className="btn-close" aria-label="Close" onClick={ () => dispatch(hide()) } ></button>
      </div>
    </div>
  )
}