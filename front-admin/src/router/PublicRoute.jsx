import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

export const PublicRoute = ({ children }) => {

    const { logged } = useSelector( state => state.auth );

    return logged ? <Navigate to="/" /> : children;
}
