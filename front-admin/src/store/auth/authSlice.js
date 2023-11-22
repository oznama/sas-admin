import { createSlice } from "@reduxjs/toolkit";

export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        logged: false,
        user: null,
        permissions: {
            isAdminSas: false,
            canAdminCat: false,
            canAdminUsr: false,
            canAdminApp: false,
            canCreateProj: false,
            canEditProj: false,
            canCreateProjApp: false,
            canEditProjApp: false,
            canDelProjApp: false,
        }
    },
    reducers: {
        login: (state, action) => {
            state.user = action.payload.user;
            state.logged = true;
            state.permissions = {
                isAdminSas: action.payload.user.companyId === 1 && (action.payload.user.role.id >= 1 && action.payload.user.role.id <=3 ),
                canAdminCat: action.payload.user.role.permissions.some( p => p.name === 'Admin-Cat'),
                canAdminUsr: action.payload.user.role.permissions.some( p => p.name === 'Admin-Usr'),
                canAdminApp: action.payload.user.role.permissions.some( p => p.name === 'Admin-App'),
                canCreateProj: action.payload.user.role.permissions.some( p => p.name === 'Create-proj'),
                canEditProj: action.payload.user.role.permissions.some( p => p.name === 'Edit-proj'),
                canCreateProjApp: action.payload.user.role.permissions.some( p => p.name === 'Create-proj-app'),
                canEditProjApp: action.payload.user.role.permissions.some( p => p.name === 'Edit-proj-app'),
                canDelProjApp: action.payload.user.role.permissions.some( p => p.name === 'Del-proj-app'),
            };
        },
        logout: (state) => {
            state.user = null;
            state.logged = false;
            state.permissions = {
                isAdminSas: false,
                canAdminCat: false,
                canAdminUsr: false,
                canAdminApp: false,
                canCreateProj: false,
                canEditProj: false,
                canCreateProjApp: false,
                canEditProjApp: false,
                canDelProjApp: false,
            };
        }
    }
})

export const { 
    login, 
    logout,
} = authSlice.actions;