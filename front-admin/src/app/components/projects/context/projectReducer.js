import { types } from "../types/types";

export const projectReducer = ( state = {}, action ) => {
    switch ( action.type ) {
        case types.success:
            return{
                ...state,
                project: action.payload,
            }
        case types.error:
            return {
                message: action.payload.mesage
            }
        default:
            return state;
    }
}