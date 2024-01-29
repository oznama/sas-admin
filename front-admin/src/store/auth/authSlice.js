import { createSlice } from "@reduxjs/toolkit";

const findPermmission = ( permissions = [], roleName = '' ) => permissions.some( p => p.name === roleName);

const permissionsInit = {
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
    canDelProj: false,
    canCreateProjApp: false,
    canEditProjApp: false,
    canDelProjApp: false,
    canAdminOrd: false,
    canCreateOrd: false,
    canEditOrd: false,
    canDelOrd: false,
    canGetEmp: false,
    canCreateEmp: false,
    canEditEmp: false,
    canDelEmp: false,
    canGetComp: false,
    canCreateComp: false,
    canEditComp: false,
    canDelComp: false,
};

export const authSlice = createSlice({
    name: 'auth',
    initialState: {
        logged: false,
        user: null,
        permissions: permissionsInit,
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
                canDelProj: findPermmission( permissions, 'Edit-proj' ),
                canCreateProjApp: findPermmission( permissions, 'Create-proj-app' ),
                canEditProjApp: findPermmission( permissions, 'Del-proj' ),
                canDelProjApp: findPermmission( permissions, 'Del-proj-app' ),
                canAdminOrd: findPermmission( permissions, 'Get-Ord' ),
                canCreateOrd: findPermmission( permissions, 'Create-Ord' ),
                canEditOrd: findPermmission( permissions, 'Edit-Ord' ),
                canDelOrd: findPermmission( permissions, 'Del-Ord' ),
                canGetEmp: findPermmission( permissions, 'Get-Emp' ),//Ver o no empleados
                canCreateEmp: findPermmission( permissions, 'Create-Emp' ),//Ver o no el boton de +
                canEditEmp: findPermmission( permissions, 'Edit-Emp' ),// Ver el boton de lapiz
                canDelEmp: findPermmission( permissions, 'Del-Emp' ),// Ver el boton de basurero
                canGetComp: findPermmission( permissions, 'Get-Comp' ),//Ver o no empresas
                canCreateComp: findPermmission( permissions, 'Create-Comp' ),//Ver o no el boton de +
                canEditComp: findPermmission( permissions, 'Edit-Comp' ),// Ver el boton de lapiz
                canDelComp: findPermmission( permissions, 'Del-Comp' )// Ver el boton de basurero
            };
        },
        logout: (state) => {
            state.user = null;
            state.logged = false;
            state.permissions = permissionsInit;
        }
    }
})

export const { 
    login, 
    logout,
} = authSlice.actions;