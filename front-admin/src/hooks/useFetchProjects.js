import { useContext, useEffect, useState } from "react";
import { getProjects } from "../app/api/ApiDummy";
import { LoadingContext } from "../app/components/custom/loading/context/LoadingContext";

export const useFetchProjects = () => {

    const { changeLoading } = useContext( LoadingContext );
    const [projects, setProjects] = useState({});

    const getPageProjects = async(page=0, size=10, sort='id,asc') => {
        changeLoading(true);
        const allProjects = await getProjects(page, size, sort);
        setProjects(allProjects);
        changeLoading(false);
    }

    useEffect( () => {
        getPageProjects();
    }, []);

    return {
        projects,
    }

}
