const url = import.meta.env.VITE_API_URL;
const lang = import.meta.env.VITE_LANG;
const token = localStorage.getItem('token');

export const getHeaders = () => ({    
    Authorization: `Bearer ${token}`,
    "Accept-Language": lang,
    "Content-Type": "application/json",
})

export const api = (context, request) => {
    return fetch( `${url}${context}`, request );
};