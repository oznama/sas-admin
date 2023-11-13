import { useReducer } from 'react';
import { useNavigate } from 'react-router-dom';
import { projectReducer } from './projectReducer';
import { getProjectById } from '../../../services/ProjectService';
import { ProjectContext } from './ProjectContext';
import { types } from '../types/types';

const init = () => {
  return {
    project: {
      id: null,
      key: "",
      description: "",
      createdBy: "",
      clientId: "-1",
      projectManagerId: "-1",
      installationDate: null,
      applications: [],
    },
  }
}

export const ProjectProvider = ({ children }) => {

  const [ projectState, dispatch ] = useReducer( projectReducer, {}, init );
  const navigate = useNavigate();

  const getById = async( id ) => {
    getProjectById(id).then( response => {
        if( response.code ) {
          dispatch({
            type: types.error,
            payload: response,
          });
        } else {
          dispatch({
            type: types.success,
            payload: response,
          });
          navigate(`/project/${id}/edit`);
        }
    }).catch( error => {
        console.log(error);
    });
    
  }

  const projectNew = () => {
    const action = {
      type: types.success,
      payload: init(),
    };
    dispatch(action);
  }

  return (
    <ProjectContext.Provider value={{
      ...projectState,
      getById,
      projectNew
    }}>
        { children }
    </ProjectContext.Provider>
  );
}