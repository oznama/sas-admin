import { api } from '../api/Api';

const context = 'clients';

export const getSelect = async() => {
    const token = localStorage.getItem('token');
    const request = {
        headers: { Authorization: `Bearer ${token}` }
    }
    const urlClients = `${context}/select`;
    const response = await api( urlClients, request );
    return await response.json();
};