import { api, getHeaders } from '../api/Api';

const context = 'companies';

export const getCompanySelect = async() => {
    const request = {
        headers: getHeaders()
    }
    const urlCompany = `${context}/select`;
    const response = await api( urlCompany, request );
    return await response.json();
};

export const getCompanies = async(page=0, size=10, sort='id,asc', filter='', type='') => {
    const request = {
        headers: getHeaders()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const typeParam = type ? `&type=${type}` : ''
    const urlCompany = `${context}?page=${page}&size=${size}&sort=${sort}${ filterParam }${typeParam}`;
    const response = await api( urlCompany, request );
    const companies = await response.json();
    return companies;
};