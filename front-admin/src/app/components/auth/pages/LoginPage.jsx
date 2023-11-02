import { useNavigate } from 'react-router-dom';
import './login.main.css';
import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

export const LoginPage = () => {

    const { login } = useContext( AuthContext );
    const navigate = useNavigate();

    const onLogin = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        console.log('Data', data);
        const request = Object.fromEntries(data.entries());
        login(request);

        navigate('/', { replace: true });
    }

    return (
        <div className="container-main">
            <form onSubmit={ onLogin }>
                <input type="text" name="email" placeholder='Usuario' />
                <input type="password" name="password" placeholder='Password' />
                <button type="submit">Iniciar sesi&oacute;n</button>
            </form>
        </div>
    );

};