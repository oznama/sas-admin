import { createSlice } from "@reduxjs/toolkit";

export const companySlice = createSlice({
    name: 'adminReducer',
    initialState: {
        companyObj: {},
        companyS: '',
        companyName: '',
        employeeS: {}
    },
    reducers: {
        setCompanyObj(state, action) {
            state.companyObj = action.payload;
        },
        setCompanyS: (state, action) => {
            // console.log('Action payload: '+action.payload)
            state.companyS = action.payload;
        },
        setEmployeeS: (state, action) => {
            state.employeeS = action.payload;
        },
        setCompanyName: (state, action) => {
            state.companyName = action.payload;
        }
    }
})

export const { 
    setCompanyObj,
    setCompanyS,
    setEmployeeS,
    setCompanyName
} = companySlice.actions;