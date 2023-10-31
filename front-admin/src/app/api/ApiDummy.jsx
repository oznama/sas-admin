const url = import.meta.env.VITE_API_URL;

export const getProjects = async() => {
    const urlProjects = `${url}dummy`;
    const response = await fetch( urlProjects );
    const projects = await response.json();
    return projects;
};