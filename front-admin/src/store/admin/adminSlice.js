import { createSlice } from "@reduxjs/toolkit";

export const adminSlice = createSlice({
    name: 'adminReducer',
    initialState: {
        role: {},
        permission:{}
    },
    reducers: {
        setRole: (state, action) => {
            state.role = action.payload;
        },
        setPermission: (state, action) => {
            state.permission = action.payload;
        }
    }
})

export const { 
    setRole,
    setPermission
} = adminSlice.actions;