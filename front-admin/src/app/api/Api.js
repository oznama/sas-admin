const url = import.meta.env.VITE_API_URL;
const lang = import.meta.env.VITE_LANG;

export const getHeaders = () => {
    const token = localStorage.getItem('token');
    const headers = {    
        Authorization: `Bearer ${token}`,
        "Accept-Language": lang,
        "Content-Type": "application/json",
    }
    return headers;
}

export const api = (context, request) => {
    return fetch( `${url}${context}`, request );
};