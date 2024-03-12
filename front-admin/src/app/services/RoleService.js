import { api, getHeaders } from '../api/Api';

const context = 'roles';

export const save = async(data) => {
    const request = {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const urlRole = `${context}/save`;
    const response = await api( urlRole, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const getRoleById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const urlRole = `${context}/${id}`;
    const response = await api( urlRole, request );
    const role = await response.json();
    return role;
};

export const getRoles = async() => {
    const request = {
        headers: getHeaders()
    }
    const response = await api( context, request );
    return await response.json();
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
}

export const deleteLogic = async(id) => {
    const request = {
        method: "DELETE",
        headers: getHeaders()
    }
    const url = `${context}/${id}`;
    const response = await api( url, request );
    return await response.json();
};