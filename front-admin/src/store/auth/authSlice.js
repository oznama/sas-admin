import { createSlice } from "@reduxjs/toolkit";

const findPermmission = ( permissions = [], roleName = '' ) => permissions.some( p => p.name === roleName);

export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        logged: false,
        user: null,
        permissions: {
            isAdminRoot: false,
            isAdminSas: false,
            canAdminCat: false,
            canCreateCat: false,
            canEditCat: false,
            canDelCat: false,
            canAdminUsr: false,
            canAdminApp: false,
            canDelCat: false,
            canCreateProj: false,
            canEditProj: false,
            canCreateProjApp: false,
            canEditProjApp: false,
            canDelProjApp: false,
            canEditRequi: false,
            canDelOrders: false,
            canDelInvoices: false,
        }
    },
    reducers: {
        login: (state, action) => {
            const permissions = action.payload.user.role.permissions;
            state.user = action.payload.user;
            state.logged = true;
            state.permissions = {
                isAdminRoot: action.payload.user.companyId === 1 && action.payload.user.role.id === 1,
                isAdminSas: action.payload.user.companyId === 1 && (action.payload.user.role.id >= 1 && action.payload.user.role.id <=3 ),
                canAdminCat: findPermmission( permissions, 'Get-Cat' ),
                canCreateCat: findPermmission( permissions, 'Create-Cat' ),
                canEditCat: findPermmission( permissions, 'Edit-Cat' ),
                canDelCat: findPermmission( permissions, 'Del-Cat' ),
                canAdminUsr: findPermmission( permissions, 'Admin-Usr' ),
                canAdminApp: findPermmission( permissions, 'Admin-App' ),
                canDelCat: findPermmission( permissions, 'Del-Cat' ),
                canCreateProj: findPermmission( permissions, 'Create-proj' ),
                canEditProj: findPermmission( permissions, 'Edit-proj' ),
                canCreateProjApp: findPermmission( permissions, 'Create-proj-app' ),
                canEditProjApp: findPermmission( permissions, 'Edit-proj-app' ),
                canDelProjApp: findPermmission( permissions, 'Del-proj-app' ),
                canEditRequi: findPermmission( permissions, 'Edit-requi' ),
                canDelOrders: findPermmission( permissions, 'Del-ord' ),
                canDelInvoices: findPermmission( permissions, 'Del-inv' )
            };
        },
        logout: (state) => {
            state.user = null;
            state.logged = false;
            state.permissions = {
                isAdminRoot: false,
                isAdminSas: false,
                canAdminCat: false,
                canCreateCat: false,
                canEditCat: false,
                canDelCat: false,
                canAdminUsr: false,
                canAdminApp: false,
                canDelCat: false,
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