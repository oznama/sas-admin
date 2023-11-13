import { api } from '../api/Api';

const context = 'users';

export const getUsersByRole = async(roleId) => {
    const token = localStorage.getItem('token');
    const request = {
        headers: { Authorization: `Bearer ${token}` }
    }
    const url = `${context}/select/${roleId}`;
    const response = await api( url, request );
    return await response.json();
};