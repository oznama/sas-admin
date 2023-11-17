import { createSlice } from "@reduxjs/toolkit";

export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        logged: false,
        user: null,
    },
    reducers: {
        login: (state, action) => {
            state.user = action.payload.user;
            state.logged = true;
        },
        logout: (state) => {
            state.user = null;
            state.logged = false;
        }
    }
})

export const { 
    login, 
    logout,
} = authSlice.actions;