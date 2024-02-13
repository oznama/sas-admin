import { createSlice } from "@reduxjs/toolkit";

export const catalogSlice = createSlice({
    name: 'catalogReducer',
    initialState: {
        catalogName: ''
    },
    reducers: {
        setCatalogName: (state, action) => {
            // console.log('Action payload: '+action.payload)
            state.catalogName = action.payload;
        }
    }
})

export const { 
    setCatalogName
} = catalogSlice.actions;