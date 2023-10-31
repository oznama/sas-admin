import PropTypes from 'prop-types';

export const Loading = ({isLoading}) => {
  return (
    isLoading && (<h2>Cargando...</h2> )
  )
}

Loading.propTypes = {
    isLoading: PropTypes.bool.isRequired
}
