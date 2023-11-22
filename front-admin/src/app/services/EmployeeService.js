import { api, getHeaders } from '../api/Api';

const context = 'employees';

export const getEmployess = async(withDevelopers) => {
    const request = {
        headers: getHeaders()
    }
    const url = `${context}/select${ withDevelopers ? '/developers' : '' }`;
    const response = await api( url, request );
    return await response.json();
};