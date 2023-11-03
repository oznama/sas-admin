import { types } from '../types/types';

export const authReducer = ( state = {}, action ) => {

    switch ( action.type ) {
        case types.login:
            return {
                ...state,
                logged: true,
                user: action.payload.user,
                token: action.payload.accessToken,
            };
        case types.logout:
            return {
                logged: false,
            };
        case types.error:
            return {
                message: action.payload.message,
                errors: action.payload.errors,
            };
        default:
            return state;
    }
}