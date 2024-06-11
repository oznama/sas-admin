import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Footer } from '../custom/Footer';
import { login } from '../../../store/auth/authSlice';
import { doLogin } from '../../services/AuthService';
import { encrypt, genericErrorMsg } from '../../helpers/utils';
import logo from '../../../assets/img/SAS_logo.png';
import { setProject } from '../../../store/project/projectSlice';
import { useState } from 'react';

export const LoginPage = () => {

    const dispatch = useDispatch();

    const navigate = useNavigate();

    const [errorMessage, setErrorMessage] = useState();
    
    const onLogin = event => {
        event.preventDefault();
        const data = new FormData(event.target);
        const request = Object.fromEntries(data.entries());
        const passwordEncryp = encrypt(request.password);
        const jsonRequest = { ...request, password: passwordEncryp};
        doLogin(jsonRequest).then( response => {
            if( response.code ) {
                setErrorMessage(response.message);
            } else {
                dispatch(login(response));
                dispatch(setProject({}));
                localStorage.setItem('token', response.accessToken);
                navigate('/', { replace: true });
            }
        }).catch( error => {
            console.log(error);
            setErrorMessage(genericErrorMsg);
        });
    }

    return (
        <section className="h-100 pt-5">
            <div className="container h-100">
                <div className="row justify-content-sm-center h-100">
                    <div className="col-xxl-4 col-xl-5 col-lg-5 col-md-7 col-sm-9">
                        <div className="card shadow-lg">
                            <div className="card-body p-5">
                                <h1 className="fs-4 card-title fw-bold mb-4 text-center">Iniciar Sesi&oacute;n</h1>
                                <div className="text-center my-2">
                                    <img src={ logo } alt="logo" width="100" />
                                </div>
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
                                    <p className='text-danger text-center'>{ errorMessage }</p>
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