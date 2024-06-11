import { api, getHeaders } from '../api/Api';

const context = 'users';

// export const getUsersByRole = async(roleId) => {
//     const request = {
//         headers: getHeaders()
//     }
//     const url = `${context}/select/${roleId}`;
//     const response = await api( url, request );
//     return await response.json();
// };

export const getUsers = async(page=0, size=10, sort='id,asc', filter='', companyId='') => {
    const request = {
        headers: getHeaders()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const companyParam = companyId ? `&companyId=${companyId}` : ''
    // const urlEmployees = `${context}?pageNumber=${page}&pageSize=${size}&sort=${sort}${ filterParam }${ companyParam }`;
    const urlEmployees = `${context}?pageNumber=${page}&pageSize=${size}${ filterParam }${ companyParam }`;
    const response = await api( urlEmployees, request );
    const employees = await response.json();
    return employees;
};

export const getUsersS = async() => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/select`;
    const response = await api( url, request );
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

export const getUserById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const urlUser = `${context}/${id}`;
    const response = await api( urlUser, request );
    const user = await response.json();
    return user;
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
    const url = `${context}/${id}`;
    const response = await api( url, request );
    return await response.json();
};
