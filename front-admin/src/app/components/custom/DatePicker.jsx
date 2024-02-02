// Reference: https://github.com/Hacker0x01/react-datepicker
// Properties: https://github.com/Hacker0x01/react-datepicker/blob/main/docs/datepicker.md
// I18n: https://date-fns.org/v2.0.0-alpha.18/docs/I18n
// How to Disable Past and Future Dates with React Date Picker: https://medium.com/@ukaohachizoba6/how-to-disable-past-and-future-dates-with-react-date-picker-916f2fc6a9b7#:~:text=In%20conclusion%2C%20you%20can%20disable,past%20or%20future%20dates%2C%20respectively.
// Disabling weekends: https://xerosource.com/how-to-disable-weekends-in-react-datepicker/

import PropTypes from 'prop-types';
import DatePickerReact from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { registerLocale, setDefaultLocale } from 'react-datepicker';
import es from 'date-fns/locale/es';
import { getCatalogChilds } from '../../services/CatalogService';
import { useState, useEffect } from 'react';
registerLocale('es', es)

export const DatePicker = ({
  name, 
  label, 
  value, 
  required,
  onChange, 
  disabled,
  minDate,
  maxDate
}) => {

  // const filterWeekends = date => date.getDay() !== 0 && date.getDay() !== 6;

  const [holyDates, setHolyDates] = useState([]);

  const fetchHolidays = () => {
    
    getCatalogChilds(1000000007)
      .then( response => {
        const catHolyDates = response.filter( cat => cat.status === 2000100001 );
        const arrayHolyDates = [];
        catHolyDates.forEach( ({ value }) => arrayHolyDates.push(new Date(value)) );
        setHolyDates(arrayHolyDates);
      }).catch( error => {
        console.log(error);
      });
  };

  useEffect(() => {
    fetchHolidays();
  }, []);
  
  return (
    <>
        <label className="form-label">{ label }</label>
        <br/>
        <DatePickerReact className="form-control padding-short" name={ name } disabled={ disabled } required={ required }
          selected={ value } onChange={ onChange } locale='es' dateFormat='dd/MM/yyyy' autoComplete='off'
          minDate={ minDate } maxDate={ maxDate } excludeDates={ holyDates } />
          {/* filterDate={ filterWeekends } */}
    </>
  )
}

DatePicker.propTypes = {
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    required: PropTypes.bool,
    onChange: PropTypes.func.isRequired,
    minDate: PropTypes.object,
    maxDate: PropTypes.object
}

DatePicker.defaultProps = {
  disabled: false,
  required: false
}