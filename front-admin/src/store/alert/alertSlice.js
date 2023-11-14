import { createSlice } from "@reduxjs/toolkit";

export const alertSlice = createSlice({
    name: 'alert',
    initialState: {
        message: '',
    },
    reducers: {
        setMessage: (state, action) => {
            state.message = action.payload;
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