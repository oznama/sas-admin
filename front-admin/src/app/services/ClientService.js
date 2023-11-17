import { api, getHeaders } from '../api/Api';

const context = 'clients';

export const getSelect = async() => {
    const request = {
        headers: getHeaders()
    }
    const urlClients = `${context}/select`;
    const response = await api( urlClients, request );
    return await response.json();
};