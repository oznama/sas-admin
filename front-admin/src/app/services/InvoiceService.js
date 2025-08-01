import { api, getHeaders } from '../api/Api';

const context = 'invoices';

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

export const getInvoicesByOrderId = async(orderId) => {
    const request = {
        headers: getHeaders()
    }
    const response = await api( `${context}/byOrder/${orderId}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const getInvoiceById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const response = await api( `${context}/${id}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const getInvoices = async(page=0, size=10, filter='') => {
    const request = {
        headers: getHeaders()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const urlInvoices = `${context}?page=${page}&size=${size}&sort=invoiceNum,asc&sort=issuedDate,asc${ filterParam }`;
    const response = await api( urlInvoices, request );
    const invoices = await response.json();
    return invoices;
};

export const getInvoicePaid = async(orderId) => {
    const request = {
        headers: getHeaders()
    }
    const urlInvoices = `${context}/${orderId}/paid`;
    const response = await api( urlInvoices, request );
    const totalPaid = await response.json();
    return totalPaid;
};