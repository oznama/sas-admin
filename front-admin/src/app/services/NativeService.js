import { apiNative, getHeadersSimple } from '../api/Api';

const context = 'projects/withoutorders';

export const getPWoO = async(filter='') => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = filter ? `?filter=${filter}` : ''
    const urlProjectsNa = `${context}${ filterParam }`;
    const response = await apiNative( urlProjectsNa, request );
    const projects = await response.json();
    return projects;
};

export const naODCNotification = async(filter='') => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = filter ? `?filter=${filter}` : ''
    const urlProjectsNa = `${context}/notification${ filterParam }`;
    const response = await apiNative( urlProjectsNa, request );
    return response;
};

export const getPWoOExl = async(list = []) => {
    const request = {
        headers: getHeadersSimple()
    }
    //Ejemplo: http://localhost:9101/api/projects/withoutorders/export?pKeys=O-62-9854-23&pKeys=O-62-9854-24
    const filterParam = '' ;//To Do Agregar logica para url del filtro
    const urlProjectsNa = `${context}/export${filterParam}`;
    const response = await apiNative( urlProjectsNa, request );
    return response
};
