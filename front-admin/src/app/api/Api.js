const url = import.meta.env.VITE_API_URL;

export const api = (context, request) => {
    return fetch( `${url}${context}`, request );
};