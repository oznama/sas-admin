import { apiNative, getHeadersSimple } from '../api/Api';
import { linkQueryBuilder } from '../helpers/utils'; 

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

export const naODCNotification = async(list = []) => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = linkQueryBuilder(list, 'pKeys') ;
    const urlProjectsNa = `${context}/notification${ filterParam }`;
    const response = await apiNative( urlProjectsNa, request );
    return response;
};

export const getPWoOExl = async(list = []) => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = linkQueryBuilder(list, 'pKeys') ;
    const urlProjectsNa = `${context}/export${filterParam}`;
    const response = await apiNative( urlProjectsNa, request );
    return response
};
