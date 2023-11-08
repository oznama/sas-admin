import { api } from '../api/Api';

const context = 'internal';

export const getCatalog = async(parentId) => {
    console.log('Loading catalog of parent id', parentId);
    const token = localStorage.getItem('token');
    console.log('Token', token);
    const request = {
        headers: { Authorization: `Bearer ${token}` }
    }
    const urlInternal = `${context}${parentId}/childs`;
    const response = await api( urlInternal, request );
    return await response.json();
};