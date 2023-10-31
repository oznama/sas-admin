import { useEffect, useState } from "react";
import { getProjects } from "../app/api/ApiDummy";

export const useFetchProjects = () => {

    const [projects, setProjects] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    const getImages = async() => {
        const allProjects = await getProjects();
        setProjects(allProjects);
        setIsLoading(false);
    }

    useEffect( () => {
        getImages();
    }, []);

    return {
        projects,
        isLoading
    }

}
