import { api } from '../api/Api';

export const doLogin = async(data) => {
    const request = {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    }
    const response = await api( 'sso/login', request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const doLogout = async(token) => {
    const request = {
        headers: { Authorization: `Bearer ${token}`,
        }
    }
    const response = await api( 'sso/logout', request );
    const jsonResponse = await response.json();
    return jsonResponse;
}