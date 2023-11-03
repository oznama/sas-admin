import { Alert } from "../components/custom/alerts/page/Alert";
import { alertType } from "../components/custom/alerts/types/types";

const renderErrorsMessage = errors => (
    errors.map( (element, index) => (
        <li key={ index }><span className="font-weight-bold">{element.entity}</span>: {element.message}</li>
    ))
);

export const renderErrorMessage = (message, errors) => {
    const children = message ? (<span>{ message }</span>) : 
        (errors ? ( <ul>{ renderErrorsMessage(errors) }</ul> ) : null );
    return !children ? null :
        (<Alert children={ children } type={ alertType.error } />);
};