import { createSlice } from "@reduxjs/toolkit";

export const alertSlice = createSlice({
    name: 'alert',
    initialState: {
        message: '',
        type: '',
    },
    reducers: {
        setMessage: (state, action) => {
            state.message = action.payload.message;
            state.type = action.payload.type;
        },
        cleanMessage: (state) => {
            state.message = '';
        }
    }
})

export const { 
    setMessage, 
    cleanMessage 
} = alertSlice.actions;