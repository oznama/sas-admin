import { apiNative, getHeadersSimple } from '../api/Api';
import { linkQueryBuilder } from '../helpers/utils';

const buildDinamycParams = ( filter='', params, isTernary) => {
    let dinamicParams = filter ? `${ isTernary ? '?' : '&'}filter=${filter}` : '';
    if( params && params.id ) {
        dinamicParams += `${ dinamicParams === '' && isTernary ? '?' : '&'}report=${params.id}`;
    }
    if( params && params.orderCanceled ) {
        dinamicParams += `${ dinamicParams === '' && isTernary ? '?' : '&'}orderCanceled=${params.orderCanceled}`;
    }
    if( params && params.percentage ) {
        dinamicParams += `${ dinamicParams === '' && isTernary ? '?' : '&'}percentage=${params.percentage}`;
    }
    if( params && params.installation ) {
        dinamicParams += `${ dinamicParams === '' && isTernary ? '?' : '&'}installation=${params.installation}`;
    }
    if( params && params.monitoring ) {
        dinamicParams += `${ dinamicParams === '' && isTernary ? '?' : '&'}monitoring=${params.monitoring}`;
    }
    return dinamicParams;
}

export const getReport = async(context, page=0, size=10, filter='', params) => {
    // console.log("Get Report with", context, page, size, filter, params);
    const request = {
        headers: getHeadersSimple()
    }
    const url = `${context}?page=${page}&size=${size}${ buildDinamycParams(filter, params) }`;
    const response = await apiNative( url, request );
    const json = await response.json();
    return json;
};

export const getReportKeys = async(context, filter='', params) => {
    const request = {
        headers: getHeadersSimple()
    }
    const url = `${context}/keys${ buildDinamycParams(filter, params, true) }`;
    const response = await apiNative( url, request );
    const json = await response.json();
    return json;
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
    return response;
};

// Project Plan

const projectPlanContext = 'projects/project-plan'

export const getProjectApps = async(pKey) => {
    const request = {
        headers: getHeadersSimple()
    }
    const url = `projects/apps?pKey=${ pKey }`;
    const response = await apiNative( url, request );
    const json = await response.json();
    return json;
};

export const projectPlanCheck = async(pKey) => {
    const request = {
        headers: getHeadersSimple()
    }
    const url = `${projectPlanContext}/check?pKey=${pKey}`;
    const response = await apiNative( url, request );
    const json = await response.json();
    return json;
}

export const projectPlanPreview = async(pKey, apps) => {
    const request = {
        headers: getHeadersSimple()
    }
    const url = `${projectPlanContext}/preview?pKey=${pKey}&apps=${apps}`;
    const response = await apiNative( url, request );
    const json = await response.json();
    return json;
}

export const projectPlanSendEmail = async(formData) => {
    const request = {
        method: "POST",
        body: formData
    }
    const response = await apiNative(`${projectPlanContext}/send`, request );
    return response;
};
