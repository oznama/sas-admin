import { api, getHeaders } from '../api/Api';

const context = 'orders';

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
    const url = `${context}/${id}`;
    const response = await api( url, request );
    return await response.json();
};

export const getOrdersByProjectId = async(projectId) => {
    const request = {
        headers: getHeaders()
    }
    const response = await api( `${context}/byProject/${projectId}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const getOrderById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const response = await api( `${context}/${id}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const getOrders = async(page=0, size=10, filter='') => {
    const request = {
        headers: getHeaders()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const urlOrders = `${context}?page=${page}&size=${size}&sort=orderNum,asc&sort=orderDate,asc${ filterParam }`;
    const response = await api( urlOrders, request );
    const orders = await response.json();
    return orders;
};

export const getOrderSelect = async() => {
    const request = {
        headers: getHeaders()
    }
    const urlOrders = `${context}/select`;
    const response = await api( urlOrders, request );
    const orders = await response.json();
    return orders;
};

export const getOrderPaid = async(projectId) => {
    const request = {
        headers: getHeaders()
    }
    const urlOrders = `${context}/${projectId}/paid`;
    const response = await api( urlOrders, request );
    const totalPaid = await response.json();
    return totalPaid;
};