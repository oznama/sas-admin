const url = import.meta.env.VITE_API_URL;

export const getProjects = async(page=0, size=10, sort='id,asc') => {
    const urlProjects = `${url}dummy?page=${page}&size=${size}&sort=${sort}`;
    const response = await fetch( urlProjects );
    const projects = await response.json();
    return projects;
};