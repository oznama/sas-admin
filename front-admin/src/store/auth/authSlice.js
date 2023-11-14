import { createSlice } from "@reduxjs/toolkit";

export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        logged: false,
        user: {

        },
        token:null,
    },
    reducers: {
        login: (state, action) => {
            state.user = action.payload.user;
            state.token = action.payload.accessToken;
            state.logged = true;
        },
        logout: (state) => {
            state.user = {
                
            };
            state.token = null;
            state.logged = false;
        }
    }
})

export const { 
    login, 
    logout 
} = authSlice.actions;