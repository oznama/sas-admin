import PropTypes from 'prop-types';

export const Select = ({ name, label, disabled, value, options, onChange }) => {

    const renderItems = () => options && options.map( option  => (
        <option key={ option.id } value={ option.id }>{ option.value }</option>
    ));

  return (
    <>
        <label className="form-label">{ label }</label>
        <select className="form-select" aria-label="Default select example" name={ name } disabled={ disabled } value={ value } onChange={ onChange }>
            <option key= '-1' value='-1'>Seleccionar...</option>
            { renderItems() }
        </select>
    </>
  );
}

Select.propTypes = {
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    options: PropTypes.array,
    onChange: PropTypes.func.isRequired,
};

Select.defaultProps = {
    disabled: false,
  }