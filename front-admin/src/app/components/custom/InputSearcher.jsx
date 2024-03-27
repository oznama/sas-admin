import PropTypes from 'prop-types';

export const InputSearcher = ({
    name,
    placeholder,
    value,
    maxLength,
    onChange,
    onClean,
    width='50'
}) => {

    return (
        <div className={`input-group w-${width} py-1`}>
            <input name={ name } type="text" className="form-control padding-short" placeholder={ placeholder }
                maxLength={ maxLength } autoComplete='off'
                value={ value } onChange={ onChange }></input>
            <span className="input-group-text" onClick={ onClean }>
                <i className="bi bi-x-lg"></i>
            </span>
        </div>   
    )
}

InputSearcher.propTypes = {
    name: PropTypes.string.isRequired,
    placeholder: PropTypes.string,
    value: PropTypes.string.isRequired,
    maxLength: PropTypes.number,
    onChange: PropTypes.func,
    onClean: PropTypes.func,
}

InputSearcher.defaultProps = {
    placeholder: 'Escribe para filtrar...',
    maxLength: 100
}