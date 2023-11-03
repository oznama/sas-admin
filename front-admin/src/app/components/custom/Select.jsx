import PropTypes from 'prop-types';

export const Select = ({ label, value, options, onChange }) => {

    const renderItems = () => options.map( option  => (
        <option key={ option.id } value={ option.id }>{ option.value }</option>
    ));

  return (
    <>
        <label className="form-label">{ label }</label>
        <select className="form-select" aria-label="Default select example" value={ value } onChange={ onChange }>
            <option key= '-1' value='-1'>Seleccionar...</option>
            { renderItems() }
        </select>
    </>
  );
}

Select.propTypes = {
    label: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    options: PropTypes.array.isRequired,
    onChange: PropTypes.func.isRequired,
};