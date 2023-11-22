import { api, getHeaders } from '../api/Api';

const context = 'logs';

export const get = async(tableName, recordId) => {
    const request = {
        headers: getHeaders()
    }
    const urlInternal = `${context}/${tableName}/${recordId}`;
    const response = await api( urlInternal, request );
    return await response.json();
};