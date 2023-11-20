import { api, getHeaders } from '../api/Api';

const context = 'catalogs';


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
    data.status = {
        id: 2000100001,
    }
    data.type= {
        id: 2000200002,
    }
    data.isRequired = false;
    const request = {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( context, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};