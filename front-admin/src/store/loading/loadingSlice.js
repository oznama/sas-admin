import { createSlice } from "@reduxjs/toolkit";

export const loadingSlice = createSlice({
    name: 'loading',
    initialState: {
        isLoading: false
    },
    reducers: {
        changeLoading: (state, action) => {
            state.isLoading = action.payload;
        },
    }
})

export const { 
    changeLoading, 
} = loadingSlice.actions;