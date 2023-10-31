import './login.main.css';

const Login = () => {

    const handleSubmmit = event => {
        event.preventDefault();
        const request = JSON.parse(event.target);
        console.log('Request', request);
    }

    return (
        <div className="container-main">
            <form onSubmit={ handleSubmmit }>
                <input type="text" name="email" placeholder='Usuario' />
                <input type="password" name="password" placeholder='Password' />
                <button type="submit">Iniciar sesi&oacute;n</button>
            </form>
        </div>
    );

};

export default Login;