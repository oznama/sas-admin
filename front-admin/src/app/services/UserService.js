import { api, getHeaders } from '../api/Api';

const context = 'users';

export const getUsersByRole = async(roleId) => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/select/${roleId}`;
    const response = await api( url, request );
    return await response.json();
};