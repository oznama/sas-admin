import { apiNative, getHeadersSimple } from '../api/Api';
import { linkQueryBuilder } from '../helpers/utils'; 

const context = 'projects/withoutorders';

export const getPWoO = async(page=0, size=10,filter='') => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const urlProjectsNa = `${context}?page=${page}&size=${size}${ filterParam }`;
    const response = await apiNative( urlProjectsNa, request );
    const projects = await response.json();
    return projects;
};

export const naODCNotification = async(list = [],boss='') => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = linkQueryBuilder(list, 'pKeys',boss) ;
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
