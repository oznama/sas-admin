import { api, getHeaders } from '../api/Api';

const context = 'roles/permissions';

export const deleteLogic = async(id) => {
    const request = {
        method: "DELETE",
        headers: getHeaders()
    }
    const url = `${context}/${id}`;
    const response = await api( url, request );
    return await response.json();
};

export const savePermissions = async(data) => {
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

export const getRolePermissionsByRoleId = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const urlRole = `${context}/catalog/${id}`;
    const response = await api( urlRole, request );
    const role = await response.json();
    return role;
};

export const getRolesPermissions = async() => {
    const request = {
        headers: getHeaders()
    }
    const urlRole = `${context}/catalog`;
    const response = await api( urlRole, request );
    return await response.json();
};