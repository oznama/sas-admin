import { api, getHeaders } from '../api/Api';

const context = 'projects';

export const getProjectSelect = async() => {
    const request = {
        headers: getHeaders()
    }
    const urlProjects = `${context}/select`;
    const response = await api( urlProjects, request );
    const projects = await response.json();
    return projects;
};

export const getProjects = async(page=0, size=10, filter='') => {
    const request = {
        headers: getHeaders()
    }
    const filterParam = filter ? `&filter=${filter}` : ''
    const urlProjects = `${context}?page=${page}&size=${size}&sort=key,asc&sort=description,asc${ filterParam }`;
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

export const deleteLogic = async(id) => {
    const request = {
        method: "DELETE",
        headers: getHeaders()
    }
    const url = `${context}/${id}`;
    const response = await api( url, request );
    return await response.json();
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

export const getApplicationsByProjectId = async(projectId) => {
    const request = {
        headers: getHeaders()
    }
    const urlProject = `${context}/application/${projectId}`;
    const response = await api( urlProject, request );
    const projectApplication = await response.json();
    return projectApplication;
};

export const getProjectApplicationById = async(projectId, id) => {
    const request = {
        headers: getHeaders()
    }
    const urlProject = `${context}/application/${projectId}/${id}`;
    const response = await api( urlProject, request );
    const projectApplication = await response.json();
    return projectApplication;
};

export const deleteApplicationLogic = async(id) => {
    const request = {
        method: "DELETE",
        headers: getHeaders()
    }
    const url = `${context}/application/${id}`;
    const response = await api( url, request );
    return await response.json();
};