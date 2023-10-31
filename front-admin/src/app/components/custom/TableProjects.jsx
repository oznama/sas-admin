import { useFetchProjects } from "../../../hooks/useFetchProjects";
import { Loading } from "./Loading";

export const TableProject = () => {

    const { projects, isLoading } = useFetchProjects();

    // const addNewElement = newElement => {
    //     Manera 1 para agregar un nuevo elemento al estado
    //     Con desustructuracion
    //     setProjects([ ...projects , newElement]);

    //     Manera 2 para agregar un nuevo elemento al estado
    //     Referenciando al callback del estado
    //     setProjects( project => [ ...project, newElement] );
    // }

    const handledSelect = id => {
        console.log('Clicked', id);
        // Show detail
    }

    const renderRows = () => projects.map(({
        id,
        clave,
        name,
        description,
        createdBy,
        creationDate,
        dueDate,
        client,
        pm,
        leader
    }) => (
        <tr key={ id } onClick={ () => handledSelect(id) }>
            <td>{ clave }</td>
            <td>{ name }</td>
            <td>{ description }</td>
            <td>{ createdBy }</td>
            <td>{ creationDate }</td>
            <td></td>
            <td></td>
            <td>{ dueDate }</td>
            <td>{ client }</td>
            <td></td>
            <td>{ pm }</td>
            <td>{ leader }</td>
        </tr>
    ));

    return (
        <>
            { <Loading isLoading={ isLoading } /> }

            <table>
                <thead>
                    <tr>
                        <th>Clave</th>
                        <th>Nombre</th>
                        <th>Descripci&oacute;n</th>
                        <th>Creado por</th>
                        <th>Fecha creaci&oacute;n</th>
                        <th>Fecha entrega (Propuesta)</th>
                        <th>Fecha entrega (Dise&ntilde;o)</th>
                        <th>Fecha entrega (Desarrollo)</th>
                        <th>Cliente</th>
                        <th>L&iacute;der Dise&ntilde;o</th>
                        <th>Project Manager</th>
                        <th>L&iacute;der Desarrollo</th>
                    </tr>
                </thead>
                <tbody>
                    { renderRows() }
                </tbody>
            </table>
        </>
    )
}