import PropTypes from 'prop-types';

export const TextArea = ({
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
        <textarea className="form-control text-area-100"
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