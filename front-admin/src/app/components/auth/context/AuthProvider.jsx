import { useReducer } from 'react';
import { AuthContext } from './AuthContext'
import { authReducer } from './authReducer';
import { types } from '../types/types';
import { doLogin, doLogout } from '../../../services/AuthService';

const init = () => {
  const user = JSON.parse( localStorage.getItem('user') );
  const token = localStorage.getItem('token');
  
  return {
    logged: !!user,
    user,
    token,
  }
}

export const AuthProvider = ({ children }) => {

  const [ authState, dispatch ] = useReducer( authReducer, {}, init );

  const login = async( userForm ) => {

    doLogin(userForm).then( response => {
      let action = {}
      if( response.code ) {
        action = {
          type: types.error,
          payload: response,
        }
      } else {
        action = {
          type: types.login,
          payload: response,
        };
    
        localStorage.setItem('user', JSON.stringify(response.user));
        localStorage.setItem('token', response.accessToken);
      }
      dispatch(action);
    }).catch( error => {
      console.log(error);
    });
    
  }

  const logout = () => {

    // TODO No esta funcionando correctamente esto
    // const token = localStorage.getItem('token');
    // doLogout(token).then( response => {
      const action = { type: types.logout }

      localStorage.removeItem('user');
      localStorage.removeItem('token');

      dispatch(action);
    // }).catch( error => {
    //   console.log(error);
    // })
    
  }

  return (
    <AuthContext.Provider value={{
      ...authState,
      login,
      logout,
    }}>
        { children }
    </AuthContext.Provider>
  );
}
