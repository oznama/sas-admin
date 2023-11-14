import PropTypes from 'prop-types';

export const InputText = ({
    name,
    label,
    type,
    placeholder, 
    value,
    required,
    maxLength,
    onChange,
    disabled,
}) => {
  return (
    <>
      <label className="form-label">{ label }</label>
      <div className="input-group has-validation">
        <input className="form-control"
          name={ name }
          type={ type }
          placeholder={ placeholder }
          required={ required }
          value={ value } 
          maxLength={ maxLength }
          disabled={ disabled }
          onChange={ onChange }>
        </input>
      </div>
    </>
  )
}

InputText.propTypes = {
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    type: PropTypes.string,
    placeholder: PropTypes.string,
    value: PropTypes.string.isRequired,
    require: PropTypes.bool,
    maxLength: PropTypes.number,
    max: PropTypes.number,
    disabled: PropTypes.bool,
    onChange: PropTypes.func,

}

InputText.defaultProps = {
    maxLength: 10,
    type: 'text,',
    require: false,
}