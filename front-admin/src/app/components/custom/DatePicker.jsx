// Reference: https://github.com/Hacker0x01/react-datepicker
// Properties: https://github.com/Hacker0x01/react-datepicker/blob/main/docs/datepicker.md
// I18n: https://date-fns.org/v2.0.0-alpha.18/docs/I18n

import PropTypes from 'prop-types';
import DatePickerReact from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { registerLocale, setDefaultLocale } from 'react-datepicker';
import es from 'date-fns/locale/es';
registerLocale('es', es)

export const DatePicker = ({
  name, 
  label, 
  value, 
  required,
  onChange, 
  disabled 
}) => {
  
    return (
    <>
        <label className="form-label">{ label }</label>
        <br/>
        <DatePickerReact className="form-control" style={ { display: 'block' } } name={ name } disabled={ disabled } required={ required }
          selected={ value } onChange={ onChange } locale='es' dateFormat='dd/MM/yyyy' autoComplete='off' />
    </>
  )
}

DatePicker.propTypes = {
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    required: PropTypes.bool,
    onChange: PropTypes.func.isRequired
}

DatePicker.defaultProps = {
  disabled: false,
  required: false,
}