import { createSlice } from "@reduxjs/toolkit";

export const applicationSlice = createSlice({
    name: 'applicationReducer',
    initialState: {
        app: {}
    },
    reducers: {
        setApp: (state, action) => {
            state.app = action.payload;
        }
    }
})

export const { 
    setApp
} = applicationSlice.actions;