import React, { useState, useEffect } from "react";

import './Login.css';

async function loginUser(credentials) {
    return fetch('http://localhost:8990/api/sso/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
    .then(data => data.json())
}

export default function Login({ setError }) {
    
    const [username, setUserName] = useState();
    const [password, setPassword] = useState();
    const [errors, setErrors] = useState([]);

    const handleSubmit = async e => {
        setErrors([]);
        e.preventDefault();
        const response =  await loginUser({
            "email": username,
            password
        });
        if( response.code ) {
            const errors = [];
            if(response.message) {
                errors.push(response.message);
            } else if (response.errors) {
                response.errors.map(error => errors.push(error.message));
            }
            setErrors(errors);
        } else {
            sessionStorage.setItem('token', response.accessToken);
        }
    }

    const errorsMessage = () => (
        errors && errors.map((error, index) => (
            <p key={ index } className="login-error">
                { error }
            </p>
        ))
    );

    return (
        <div className="login-wrapper">
            <h1>Iniciar sesion</h1>
            <form onSubmit={handleSubmit}>
                <label>
                    <p>Usuario</p>
                    <input type="text" onChange={ e => setUserName(e.target.value) } />
                </label>
                <label>
                    <p>Contrase&ntilde;a</p>
                    <input type="password" onChange={ e => setPassword(e.target.value) } />
                </label>
                { errorsMessage() }
                <div>
                    <button type="submit">Iniciar sesion</button>
                </div>
            </form>
        </div>
    );
}