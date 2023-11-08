import PropTypes from 'prop-types';

export const InputText = ({
    name,
    label, 
    placeholder, 
    value,
    maxLength,
    onChange,
    disabled,
}) => {
  return (
    <>
        <label className="form-label">{ label }</label>
        <input className="form-control"
            name={ name }
            type="text"
            placeholder={ placeholder } 
            value={ value } 
            maxLength={ maxLength }
            disabled={ disabled }
            onChange={ onChange }></input>
    </>
  )
}

InputText.propTypes = {
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    placeholder: PropTypes.string,
    value: PropTypes.string.isRequired,
    maxLength: PropTypes.number,
    disabled: PropTypes.bool,
    onChange: PropTypes.func,
}

InputText.defaultProps = {
    maxLength: 10,
}