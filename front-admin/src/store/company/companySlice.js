import { createSlice } from "@reduxjs/toolkit";

export const companySlice = createSlice({
    name: 'companyReducer',
    initialState: {
        companyS: '',
        companyName: '',
        employeeS: {}
    },
    reducers: {
        setCompanyS: (state, action) => {
            // console.log('Action payload: '+action.payload)
            state.companyS = action.payload;
        },
        setEmployeeS: (state, action) => {
            state.employeeS = action.payload;
            console.log('Aqui imprimo lo que creo que es el nombre del registrado: '+action.payload.name)
        },
        setCompanyName: (state, action) => {
            state.companyName = action.payload;
        }
    }
})

export const { 
    setCompanyS,
    setEmployeeS,
    setCompanyName
} = companySlice.actions;