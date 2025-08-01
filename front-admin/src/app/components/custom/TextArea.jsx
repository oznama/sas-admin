import PropTypes from 'prop-types';

export const TextArea = ({
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
        <textarea className="form-control"
            name={ name }
            type="textarea"
            placeholder={ placeholder } 
            value={ value } 
            maxLength={ maxLength }
            disabled={ disabled }
            onChange={ onChange }></textarea>
    </>
  )
}

TextArea.propTypes = {
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    placeholder: PropTypes.string,
    value: PropTypes.string.isRequired,
    maxLength: PropTypes.number,
    disabled: PropTypes.bool,
    onChange: PropTypes.func,
}

TextArea.defaultProps = {
    maxLength: 255,
}