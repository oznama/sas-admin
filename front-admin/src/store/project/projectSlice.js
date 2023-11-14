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
            clientId: "",
            projectManagerId: "",
            installationDate: null,
        },
        applications: [],
        projectApplication: {
            id: null,
            projectId: "",
            applicationId: "",
            amount: "",
            hours: "",
            leaderId: "",
            developerId: "",
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
                clientId: "",
                projectManagerId: "",
                installationDate: null,
            }
        },
        setApplications: (state, action) => {
            state.applications = action.payload;
        },
        addAplication: (state, action) => {
            state.applications.push(action.payload);
        },
        cleanApplications: (state) => {
            state.applications = [];
        },
        setProjectApplication: (state, action) => {
            state.projectApplication = action.payload;
        },
        cleanProjectApplication: ( state, action ) => {
            state.projectApplication = {
                id: null,
                projectId: action.payload,
                applicationId: "",
                amount: "",
                hours: "",
                leaderId: "",
                developerId: "",
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
    setApplications,
    addAplication,
    cleanApplications,
    setProjectApplication,
    cleanProjectApplication,
    setCurrentTab,
} = projectSlice.actions;