const url = import.meta.env.VITE_API_URL;

export const doLogin = async(data) => {
    const request = {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    }
    const response = await fetch( `${url}sso/login`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const doLogout = async(token) => {
    const request = {
        headers: { Authorization: `Bearer ${token}`,
        }
    }
    const response = await fetch( `${url}sso/logout`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
}