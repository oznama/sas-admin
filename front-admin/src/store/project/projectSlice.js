import { createSlice } from "@reduxjs/toolkit";
import { act } from "@testing-library/react";

export const projectSlice = createSlice({
    name: 'projectReducer',
    initialState: {
        currentTab: 1,
    },
    reducers: {
        setCurrentTab: (state, action) => {
            state.currentTab = action.payload;
        },
    }
})

export const { 
    setCurrentTab,
} = projectSlice.actions;