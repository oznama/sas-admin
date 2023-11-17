import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Footer } from '../custom/Footer';
import { alertType } from '../custom/alerts/types/types';
import { Alert } from '../custom/alerts/page/Alert';
import { login } from '../../../store/auth/authSlice';
import { setMessage } from '../../../store/alert/alertSlice';
import { doLogin } from '../../services/AuthService';
import { buildPayloadMessage } from '../../helpers/utils';

export const LoginPage = () => {

    const dispatch = useDispatch();

    const navigate = useNavigate();
    
    const onLogin = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        doLogin(request).then( response => {
            if( response.code ) {
                dispatch(setMessage(buildPayloadMessage(response.message, alertType.error)));
            } else {
                dispatch(login(response));
                localStorage.setItem('token', response.accessToken);
                navigate('/', { replace: true });
            }
        }).catch( error => {
            dispatch(setMessage(buildPayloadMessage('Ha ocurrido un error, contacta al area de sistemas', alertType.error)));
        });
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
                                <Alert />
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