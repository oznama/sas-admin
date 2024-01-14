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