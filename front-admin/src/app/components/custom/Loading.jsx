import PropTypes from 'prop-types';

export const Loading = ({isLoading}) => {

  const buildSpinner = () => (
    <div className="spinner-border" role="status">
      <span className="visually-hidden">Loading...</span>
      </div>
  );

  return (
    isLoading && buildSpinner()
  );
}

Loading.propTypes = {
    isLoading: PropTypes.bool.isRequired
}
