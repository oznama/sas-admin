import { api, getHeaders } from '../api/Api';

const context = 'logs';

export const get = async(tableName, recordId) => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/${tableName}/${recordId}`;
    const response = await api( url, request );
    return await response.json();
};