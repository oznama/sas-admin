import { useNavigate } from 'react-router-dom';
import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { Footer } from '../../custom/Footer';
import { renderErrorMessage } from '../../../helpers/handleErrors';

export const LoginPage = () => {

    const { login, message, errors } = useContext( AuthContext );

    const navigate = useNavigate();
    
    const onLogin = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        login(request);

        navigate('/', { replace: true });
    }

    return (
        <section className="h-100">
            <div className="container h-100">
                <div className="row justify-content-sm-center h-100">
                    <div className="col-xxl-4 col-xl-5 col-lg-5 col-md-7 col-sm-9">
                        <div className="text-center my-5">
                            <img src="https://getbootstrap.com/docs/5.0/assets/brand/bootstrap-logo.svg" alt="logo" width="100" />
                        </div>
                        <div className="card shadow-lg">
                            <div className="card-body p-5">
                                <h1 className="fs-4 card-title fw-bold mb-4">Iniciar Sesi&oacute;n</h1>
                                { renderErrorMessage(message, errors, null) }
                                <form className="needs-validation" onSubmit={ onLogin }>
                                    <div className="mb-3">
                                        <label className="mb-2 text-muted">Email</label>
                                        <input className="form-control" name="email" type="email" required />
                                        <div className="invalid-feedback">
                                            eMail invalido
                                        </div>
                                    </div>
                                    <div className="mb-2">
                                        <label className="mb-2 text-muted">Password</label>
                                        <input type="password" className="form-control" name="password" required />
                                        <div className="invalid-feedback">
                                            Password obligatorio
                                        </div>
                                        {/* <div className="mb-2 w-100">
                                            <label className="text-muted" for="password">Password</label>
                                            <a href="forgot.html" className="float-end">
                                                Olvide mi password
                                            </a>
                                        </div> */}
                                    </div>

                                    <div className="d-flex align-items-center">
                                        {/* <div className="form-check">
                                            <input type="checkbox" name="remember" id="remember" className="form-check-input" />
                                            <label for="remember" className="form-check-label">Mantener sesi&oacute;n</label>
                                        </div> */}
                                        <button type="submit" className="btn btn-primary ms-auto">
                                            Login
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                        { <Footer /> }
                    </div>
                </div>
            </div>
        </section>
    );

};