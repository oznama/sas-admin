const url = import.meta.env.VITE_API_URL;
const lang = import.meta.env.VITE_LANG;
const urlNative = import.meta.env.VITE_API_URLNATIVE;

export const getHeadersSimple = () => {
    return {    
        "Accept-Language": lang,
        "Content-Type": "application/json",
    };
}

export const getHeaders = () => {
    return {
        ...getHeadersSimple(),
        Authorization: `Bearer ${localStorage.getItem('token')}`
    }
}

export const api = (context, request) => {
    return fetch( `${url}${context}`, request );
};

export const apiNative = (context, request) => {
    return fetch( `${urlNative}${context}`, request );
};