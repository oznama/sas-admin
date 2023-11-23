import PropTypes from 'prop-types';

export const Select = ({
  name,
  label,
  disabled,
  value,
  required,
  options,
  onChange
}) => {

    const renderItems = () => options && options.map( option  => (
        <option key={ option.id } value={ option.id }>{ option.value }</option>
    ));

  return (
    <div className='mb-3'>
        <label className="form-label">{ label }</label>
        <select className="form-select"
          name={ name } disabled={ disabled } value={ value } required={ required } onChange={ onChange }>
            <option value=''>Seleccionar...</option>
            { renderItems() }
        </select>
    </div>
  );
}

Select.propTypes = {
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    required: PropTypes.bool,
    options: PropTypes.array,
    onChange: PropTypes.func.isRequired,
};

Select.defaultProps = {
  disabled: false,
  required: false,
}