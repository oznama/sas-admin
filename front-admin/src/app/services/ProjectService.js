import { api } from '../api/Api';

const context = 'projects';

export const getProjects = async(page=0, size=10, sort='id,asc') => {
    const token = localStorage.getItem('token');
    const request = {
        headers: { Authorization: `Bearer ${token}` }
    }
    const urlProjects = `${context}?page=${page}&size=${size}&sort=${sort}`;
    const response = await api( urlProjects, request );
    const projects = await response.json();
    return projects;
};

export const getProjectById = async(id) => {
    const token = localStorage.getItem('token');
    const request = {
        headers: { Authorization: `Bearer ${token}` }
    }
    const urlProject = `${context}/${id}`;
    const response = await api( urlProject, request );
    const project = await response.json();
    return project;
};

export const save = async(data) => {
    console.log('Request project save with data', data)
    const token = localStorage.getItem('token');
    const request = {
        method: "POST",
        headers: { 
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json" 
        },
        body: JSON.stringify(data)
    }
    const response = await api( context, request );
    const jsonResponse = await response.json();
    return jsonResponse;
};