import PropTypes from 'prop-types';

export const Loading = ({isLoading}) => {

  const buildSpinner = () => (
    <div class="spinner-border" role="status">
      <span class="visually-hidden">Loading...</span>
      </div>
  );

  return (
    isLoading && buildSpinner()
  );
}

Loading.propTypes = {
    isLoading: PropTypes.bool.isRequired
}
