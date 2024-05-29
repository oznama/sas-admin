import { apiNative, getHeadersSimple } from '../api/Api';
import { linkQueryBuilder } from '../helpers/utils'; 

export const getPWoO = async(context, page=0, size=10,filter='', customFilter) => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = filter ? `&filter=${filter}` : '';
    const reportParam = customFilter && customFilter.report ? `&report=${customFilter.report}` : ''
    const paStatusParam = customFilter && customFilter.paStatus ? `&paStatus=${customFilter.paStatus}` : ''
    const urlProjectsNa = `${context}?page=${page}&size=${size}${ filterParam }${ paStatusParam }${ reportParam }`;
    const response = await apiNative( urlProjectsNa, request );
    const projects = await response.json();
    return projects;
};

export const naODCNotification = async(context, list = []) => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = linkQueryBuilder(list, 'pKeys') ; //&currentUserEmail=selene.pascalis%40sas-mexico.com
    const urlProjectsNa = `${context}/notification${ filterParam }`;
    const response = await apiNative( urlProjectsNa, request );
    return response;
};

export const getPWoOExl = async(context, list = []) => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = linkQueryBuilder(list, 'pKeys') ;
    const urlProjectsNa = `${context}/export${filterParam}`;
    const response = await apiNative( urlProjectsNa, request );
    return response
};
