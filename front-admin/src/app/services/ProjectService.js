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

export const update = async(id, data) => {
    const request = {
        method: "PUT",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( `${context}/${id}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
}

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

export const updateApplication = async(id, data) => {
    const request = {
        method: "PUT",
        headers: getHeaders(),
        body: JSON.stringify(data)
    }
    const response = await api( `${context}/application/${id}`, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};

export const getProjectApplicationById = async(projectId, id) => {
    const request = {
        headers: getHeaders()
    }
    const urlProject = `${context}/${projectId}/application/${id}`;
    const response = await api( urlProject, request );
    const projectApplication = await response.json();
    return projectApplication;
};