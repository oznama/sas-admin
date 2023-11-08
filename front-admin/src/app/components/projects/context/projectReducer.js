export const projectReducer = ( state = {}, action ) => {

    switch ( action.type ) {
        case 'getId':
            return {
                ...state,
                project: action.payload,
            }
        case 'error':
            return {
                message: action.payload.mesage
            }
        default:
            return state;
    }
}