import PropTypes from 'prop-types';
import { styleInput } from '../../helpers/utils';

export const InputText = ({
    name,
    label,
    type,
    placeholder, 
    value,
    required,
    maxLength,
    onChange,
    onFocus,
    onBlur,
    disabled,
    error,
    readOnly
}) => {
  return (
    <div className='mb-3'>
      <label className="form-label">{ label }</label>
      <div className="input-group has-validation">
        <input className="form-control"
          style={ styleInput }
          name={ name }
          type={ type }
          placeholder={ placeholder }
          required={ required }
          value={ value } 
          maxLength={ maxLength }
          disabled={ disabled }
          onChange={ onChange }
          onFocus={ onFocus }
          onBlur={ onBlur }
          readOnly={ readOnly }
          autoComplete='off'>
        </input>
      </div>
      {
          error && (
            <p className='text-danger text-end'>{ error }</p>
          )
        }
    </div>
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
    onFocus: PropTypes.func,
    onBlur: PropTypes.func,
    error: PropTypes.string,
    readOnly: PropTypes.bool
}

InputText.defaultProps = {
    maxLength: 10,
    type: 'text,',
    require: false,
    error: null,
    readOnly: false,
    onChange: () => {},
    onFocus: () => {},
    onBlur: () => {}
}