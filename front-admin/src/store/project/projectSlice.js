import { createSlice } from "@reduxjs/toolkit";

export const projectSlice = createSlice({
    name: 'projectReducer',
    initialState: {
        currentTab: 1,
        project: {},
        order: {},
        paid: {
            amount: 0,
            tax: 0,
            total: 0
        }
    },
    reducers: {
        setCurrentTab: (state, action) => {
            state.currentTab = action.payload;
        },
        setProject: (state, action) => {
            state.project = action.payload;
        },
        setOrder: (state, action) => {
            state.order = action.payload;
        },
        setPaid: (state, action) => {
            state.paid = action.payload
        }
    }
})

export const { 
    setCurrentTab,
    setProject,
    setOrder,
    setPaid
} = projectSlice.actions;