import { createSlice } from "@reduxjs/toolkit";

export const userSlice = createSlice({
    name: 'userReducer',
    initialState: {
        employeesObj: {},
        companyID: '',
        roleObj: {}
    },
    reducers: {
        setEmployeesObj(state, action) {
            state.employeesObj = action.payload;
        },
        setCompanyID: (state, action) => {
            state.companyID = action.payload;
        },
        setRoleObj: (state, action) => {
            state.roleObj = action.payload;
        }
    }
})

export const { 
    setEmployeesObj,
    setCompanyID,
    setRoleObj
} = userSlice.actions;