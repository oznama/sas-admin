import { createSlice } from "@reduxjs/toolkit";
import { act } from "@testing-library/react";

export const projectSlice = createSlice({
    name: 'projectReducer',
    initialState: {
        currentTab: 1,
        project: {
            id: null,
            key: "",
            description: "",
            createdBy: "",
            creationDate: null,
            clientId: "-1",
            projectManagerId: "-1",
            installationDate: null,
        },
        projectApplication: {
            id: null,
            projectId: "-1",
            applicationId: "-1",
            amount: "$0",
            hours: "0",
            leaderId: "-1",
            developerId: "-1",
            designDate: null,
            developmentDate: null,
            endDate: null
        }
    },
    reducers: {
        setProject: (state, action) => {
            state.project = action.payload;
        },
        cleanProject: ( state ) => {
            state.project = {
                id: null,
                key: "",
                description: "",
                createdBy: "",
                creationDate: null,
                clientId: "-1",
                projectManagerId: "-1",
                installationDate: null,
            }
        },
        setProjectApplication: (state, action) => {
            state.projectApplication = action.payload;
        },
        cleanProjectApplication: ( state ) => {
            state.projectApplication = {
                id: null,
                projectId: "-1",
                applicationId: "-1",
                amount: "$0",
                hours: "0",
                leaderId: "-1",
                developerId: "-1",
                designDate: null,
                developmentDate: null,
                endDate: null
            }
        },
        setCurrentTab: (state, action) => {
            state.currentTab = action.payload;
        },
    }
})

export const { 
    setProject,
    cleanProject,
    setProjectApplication,
    cleanProjectApplication,
    setCurrentTab,
} = projectSlice.actions;