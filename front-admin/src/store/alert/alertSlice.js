import { createSlice } from "@reduxjs/toolkit";

export const alertSlice = createSlice({
    name: 'alert',
    initialState: {
        message: '',
        type: '',
        show: false,
    },
    reducers: {
        setMessage: (state, action) => {
            state.message = action.payload.message;
            state.type = action.payload.type;
            state.show = true;
        },
        hide: (state) => {
            state.show = false;
        }
    }
})

export const { 
    setMessage, 
    hide, 
} = alertSlice.actions;