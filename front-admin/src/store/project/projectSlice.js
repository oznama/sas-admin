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
            companyId: "",
            projectManagerId: "",
            installationDate: null,
        },
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
                companyId: "",
                projectManagerId: "",
                installationDate: null,
            }
        },
        addAplication: (state, action) => {
            state.project.applications.push(action.payload);
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
            };
            state.currentTab = 1;
        },
        setCurrentTab: (state, action) => {
            state.currentTab = action.payload;
        },
    }
})

export const { 
    setProject,
    cleanProject,
    addAplication,
    setProjectApplication,
    cleanProjectApplication,
    setCurrentTab,
} = projectSlice.actions;