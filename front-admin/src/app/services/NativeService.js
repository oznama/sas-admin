import { apiNative, getHeadersSimple } from '../api/Api';
import { linkQueryBuilder } from '../helpers/utils'; 

export const getReport = async(context, page=0, size=10, filter='', params) => {
    // console.log("Get Report with", context, page, size, filter, params);
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = filter ? `&filter=${filter}` : '';
    let dinamicParams = '';
    if( params && params.id ) {
        dinamicParams += `&report=${params.id}`;
    }
    if( params && params.orderCanceled ) {
        dinamicParams += `&orderCanceled=${params.orderCanceled}`;
    }
    if( params && params.percentage ) {
        dinamicParams += `&percentage=${params.percentage}`;
    }
    if( params && params.installation ) {
        dinamicParams += `&installation=${params.installation}`;
    }
    if( params && params.monitoring ) {
        dinamicParams += `&monitoring=${params.monitoring}`;
    }
    const urlProjectsNa = `${context}?page=${page}&size=${size}${ filterParam }${ dinamicParams }`;
    const response = await apiNative( urlProjectsNa, request );
    const projects = await response.json();
    return projects;
};

export const sendNotification = async(context, list = []) => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = linkQueryBuilder(list, 'pKeys') ; //&currentUserEmail=selene.pascalis%40sas-mexico.com
    const urlProjectsNa = `${context}/notification${ filterParam }`;
    const response = await apiNative( urlProjectsNa, request );
    return response;
};

export const downloadExcel = async(context, list = []) => {
    const request = {
        headers: getHeadersSimple()
    }
    const filterParam = linkQueryBuilder(list, 'pKeys') ;
    const urlProjectsNa = `${context}/export${filterParam}`;
    const response = await apiNative( urlProjectsNa, request );
    return response
};
