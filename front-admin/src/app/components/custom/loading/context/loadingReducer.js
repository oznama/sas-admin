import { types } from "../types/types";

export const loadingReducer = ( state = {}, action ) => {

    switch ( action.type ) {
        case types.show:
            return {
                ...state,
                isLoading: true,
            };
        case types.hidden:
            return {
                isLoading: false,
            };
        default:
            return state;
    }
}