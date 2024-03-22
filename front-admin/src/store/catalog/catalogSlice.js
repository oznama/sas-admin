import { createSlice } from "@reduxjs/toolkit";

export const catalogSlice = createSlice({
    name: 'adminReducer',
    initialState: {
        obj: {},
        catalogName: '',
        catalogParent: ''
    },
    reducers: {
        setCatalogName: (state, action) => {
            // console.log('Action payload: '+action.payload)
            state.catalogName = action.payload;
        },
        setCatalogObj: (state, action) => {
            state.obj = action.payload;
        },
        setCatalogParent: (state, action) =>{
            state.catalogParent = action.payload;
        }
    }
})

export const { 
    setCatalogName,
    setCatalogObj,
    setCatalogParent
} = catalogSlice.actions;