import { api, getHeaders } from '../api/Api';

const context = 'internal';

export const getCatalog = async(parentId) => {
    const request = {
        headers: getHeaders()
    }
    const urlInternal = `${context}/${parentId}/childs`;
    const response = await api( urlInternal, request );
    return await response.json();
};