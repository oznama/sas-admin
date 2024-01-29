import { createSlice } from "@reduxjs/toolkit";

export const modalSlice = createSlice({
    name: 'modal',
    initialState: {
        child: null
    },
    reducers: {
        setModalChild: (state, action) => {
            state.child = action.payload;
        },
    }
})

export const { 
    setModalChild, 
} = modalSlice.actions;