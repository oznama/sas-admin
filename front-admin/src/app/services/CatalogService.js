import { api, getHeaders } from '../api/Api';

const context = 'catalogs';

export const getCatalogs = async() => {
    const request = {
        headers: getHeaders()
    }
    const response = await api( context, request );
    return await response.json();
};

export const getCatalogById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const urlInternal = `${context}/${id}`;
    const response = await api( urlInternal, request );
    return await response.json();
};

export const getCatalogChilds = async(parentId) => {
    const request = {
        headers: getHeaders()
    }
    const urlInternal = `${context}/${parentId}/childs`;
    const response = await api( urlInternal, request );
    return await response.json();
};

export const save = async(data) => {
    const request = {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( context, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const update = async(id, data) => {
    const request = {
        method: "PUT",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( `${context}/${id}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const deleteLogic = async(id) => {
    const request = {
        method: "DELETE",
        headers: getHeaders()
    }
    const urlInternal = `${context}/${id}`;
    const response = await api( urlInternal, request );
    return await response.json();
};