import { useReducer } from 'react';
import { projectReducer } from './projectReducer';
import { getProjectById } from '../../../services/ProjectService';
import { ProjectContext } from './ProjectContext';

const init = () => {
  return {
    project,
  }
}

export const ProjectProvider = ({ children }) => {

  const [ projectState, dispatch ] = useReducer( projectReducer, {}, init );

  const getById = async( id ) => {

    getProjectById(id).then( response => {
        let action = {}
        if( response.code ) {
            action = {
              type: 'error',
              payload: response,
            }
          } else {
            action = {
              type: 'getId',
              payload: response,
            };
          }
          dispatch(action);
    }).catch( error => {
        console.log(error);
    });
    
  }

  return (
    <ProjectContext.Provider value={{
      ...projectState,
      getById,
    }}>
        { children }
    </ProjectContext.Provider>
  );
}