import PropTypes from 'prop-types';

export const SelectSearcher = ({
    label,
    placeholder,
    disabled,
    value,
    required,
    onChange,
    onClean,
    options
}) => {

    const renderOptions = () => options && options.map( option => (
        <option key={ option.id }>{ option.value }</option>
    ));
    
    return (
        <div>
            <label htmlFor="selectSearcherList" className="form-label">{ label }</label>
            <div className="input-group py-1">
                <input className="form-control"
                    list="datalistOptions"
                    id="selectSearcherList"
                    value={ value }
                    disabled={ disabled }
                    required={ required }
                    onChange={ onChange }
                    placeholder={ placeholder }
                    autoComplete="off" />
                <datalist id="datalistOptions">
                    { renderOptions() }
                </datalist>
                <span className="input-group-text" onClick={ onClean }>
                    <i className="bi bi-x-lg"></i>
                </span>
            </div>
        </div>
    )
}

SelectSearcher.propTypes = {
    label: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
    required: PropTypes.bool,
    options: PropTypes.array,
    onChange: PropTypes.func.isRequired,
    onClean: PropTypes.func
};

SelectSearcher.defaultProps = {
    placeholder: 'Escribe para filtrar',
    disabled: false,
    required: false
}