import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { cleanMessage } from '../../../../../store/alert/alertSlice';

export const Alert = ({ type }) => {

  const message = useSelector( (state) => state.alert.message)
  const dispatch = useDispatch();
  
  useEffect(() => {
    const timeId = setTimeout(() => dispatch(cleanMessage()), 5000);
    return () => clearTimeout(timeId);
  }, []);

  if (message === '') {
    return null;
  }
  return (
    <div className={ `alert alert-${ type }` } role="alert">
      { message }
    </div>
  )
}

Alert.propTypes = {
  type: PropTypes.string,
}

Alert.defaultProps = {
  type: 'secondary',
}