import { api, getHeaders } from '../api/Api';

const context = 'employees';

export const getEmployess = async(withDevelopers) => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/select${ withDevelopers ? '/developers' : '' }`;
    const response = await api( url, request );
    return await response.json();
};

export const getEmployessByCompanyId = async(companyId) => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/select/${ companyId }`;
    const response = await api( url, request );
    return await response.json();
};

export const getEmployees = async(page=0, size=10, sort='id,asc', filter='', companyId='') => {
    const request = {
        headers: getHeaders()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const companyParam = companyId ? `&companyId=${companyId}` : ''
    const urlEmployees = `${context}?page=${page}&size=${size}&sort=${sort}${ filterParam }${ companyParam }`;
    const response = await api( urlEmployees, request );
    const employees = await response.json();
    return employees;
};

export const getEmployeeById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const urlEmployee = `${context}/${id}`;
    const response = await api( urlEmployee, request );
    const employee = await response.json();
    return employee;
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