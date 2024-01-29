import { api, getHeaders } from '../api/Api';

const context = 'companies';

export const getCompanySelect = async() => {
    const request = {
        headers: getHeaders()
    }
    const urlCompany = `${context}/select`;
    const response = await api( urlCompany, request );
    return await response.json();
};

export const getCompanies = async(page=0, size=10, sort='id,asc', filter='', type='') => {
    const request = {
        headers: getHeaders()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const typeParam = type ? `&type=${type}` : ''
    const urlCompany = `${context}?page=${page}&size=${size}&sort=${sort}${ filterParam }${typeParam}`;
    const response = await api( urlCompany, request );
    const companies = await response.json();
    return companies;
};

export const getCompanyById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const urlCompany = `${context}/${id}`;
    const response = await api( urlCompany, request );
    const company = await response.json();
    return company;
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
    const urlInternal = `${context}/${id}`;
    const response = await api( urlInternal, request );
    return await response.json();
};