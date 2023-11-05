import { useReducer } from 'react';
import { loadingReducer } from './loadingReducer';
import { LoadingContext } from './LoadingContext';
import { types } from '../types/types';

const init = () => ({
  isLoading: false,
})

export const LoadingProvider = ({ children }) => {

  const [ loadingState, dispatch ] = useReducer( loadingReducer, {}, init );

  const changeLoading = loading => {
    const action = {
      type: loading ? types.show : types.hidden,
    };
    dispatch(action);
  }

  return (
    <LoadingContext.Provider value={{
      ...loadingState,
      changeLoading,
    }}>
        { children }
    </LoadingContext.Provider>
  );
}
