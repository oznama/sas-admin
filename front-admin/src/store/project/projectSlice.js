import { createSlice } from "@reduxjs/toolkit";

export const projectSlice = createSlice({
    name: 'companyReducer',
    initialState: {
        currentTab: 1,
        currentAppTab: 1,
        currentOrdTab: 1,
        project: {},
        order: {},
        projectPaid: {
            amount: 0,
            tax: 0,
            total: 0
        },
        orderPaid: {
            amount: 0,
            tax: 0,
            total: 0
        },
        numApps: 0
    },
    reducers: {
        setCurrentTab: (state, action) => {
            state.currentTab = action.payload;
        },
        setCurrentAppTab: (state, action) => {
            state.currentAppTab = action.payload;
        },
        setCurrentOrdTab: (state, action) => {
            state.currentOrdTab = action.payload;
        },
        setProject: (state, action) => {
            state.project = action.payload;
        },
        setOrder: (state, action) => {
            state.order = action.payload;
        },
        setProjectPaid: (state, action) => {
            state.projectPaid = action.payload
        },
        setOrderPaid: (state, action) => {
            state.orderPaid = action.payload
        },
        setNumApps: (state, action) => {
            state.numApps = action.payload
        }
    }
})

export const { 
    setCurrentTab,
    setCurrentAppTab,
    setCurrentOrdTab,
    setProject,
    setOrder,
    setProjectPaid,
    setOrderPaid,
    setNumApps
} = projectSlice.actions;