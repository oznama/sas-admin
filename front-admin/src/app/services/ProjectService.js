import { api, getHeaders } from '../api/Api';

const context = 'projects';

export const getProjects = async(page=0, size=10, sort='id,asc') => {
    const request = {
        headers: getHeaders()
    }
    const urlProjects = `${context}?page=${page}&size=${size}&sort=${sort}`;
    const response = await api( urlProjects, request );
    const projects = await response.json();
    return projects;
};

export const getProjectById = async(id) => {
    const request = {
        headers: getHeaders()
    }
    const urlProject = `${context}/${id}`;
    const response = await api( urlProject, request );
    const project = await response.json();
    return project;
};

export const save = async(data) => {
    const request = {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( context, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const saveApplication = async(data) => {
    const request = {
        method: "POST",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( `${context}/application`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};