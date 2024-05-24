import { apiNative, getHeadersSimple } from '../api/Api';

const context = 'projects/withoutorders';

export const getPWoO = async(filter='') => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = filter ? `?filter=${filter}` : ''
    const urlEmployees = `${context}${ filterParam }`;
    const response = await apiNative( urlEmployees, request );
    const projects = await response.json();
    return projects;
};
