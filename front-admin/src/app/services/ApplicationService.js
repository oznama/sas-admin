import { api, getHeaders } from '../api/Api';

const context = 'applications';

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

export const update = async(name, data) => {
    const request = {
        method: "PUT",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( `${context}/${name}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
}

export const deleteLogic = async(name) => {
    const request = {
        method: "DELETE",
        headers: getHeaders()
    }
    const url = `${context}/${name}`;
    const response = await api( url, request );
    return await response.json();
};

export const getApplicationByName = async(name) => {
    const request = {
        headers: getHeaders()
    }
    console.log("getApplicationByName request:"+request);
    const urlApplication = `${context}/${name}`;
    console.log("getApplicationByName urlApplication:"+urlApplication);
    const response = await api( urlApplication, request );
    console.log("getApplicationByName response:"+JSON.stringify(response, null, 2));
    const application = await response.json();
    return application;
};

export const getAplications = async() => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/select`;
    const response = await api( url, request );
    return await response.json();
};

export const getAplicationsF = async(page=0, size=10, sort='id,asc', filter='', companyId='') => {
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