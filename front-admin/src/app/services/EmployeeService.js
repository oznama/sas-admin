import { api, getHeaders } from '../api/Api';

const context = 'employees';

export const getEmployess = async() => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/select`;
    const response = await api( url, request );
    return await response.json();
};