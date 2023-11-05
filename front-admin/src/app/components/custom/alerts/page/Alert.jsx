import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';

export const Alert = ({ type, children }) => {

  const [show, setShow] = useState(true);
  
  useEffect(() => {
    const timeId = setTimeout(() => setShow(false), 5000);
    return () => clearTimeout(timeId);
  }, []);

  if (!show) {
    return null;
  }
  return (
    <div className={ `alert alert-${ type }` } role="alert">
      { children }
    </div>
  )
}

Alert.propTypes = {
  type: PropTypes.string,
}

Alert.defaultProps = {
  type: 'secondary',
}