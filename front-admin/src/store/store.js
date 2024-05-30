import { configureStore } from "@reduxjs/toolkit";
import { alertSlice } from "./alert/alertSlice";
import { loadingSlice } from "./loading/loadingSlice";
import { modalSlice } from "./modal/modalSlice";
import { authSlice } from "./auth/authSlice";
import { projectSlice } from "./project/projectSlice";
import { companySlice } from "./company/companySlice";
import { catalogSlice } from "./catalog/catalogSlice";
import { applicationSlice } from "./application/applicationSlice";
import { adminSlice } from "./admin/adminSlice";
import { userSlice } from "./user/userSlice";

export const store = configureStore({
    reducer: {
        alert: alertSlice.reducer,
        loading: loadingSlice.reducer,
        modal: modalSlice.reducer,
        auth: authSlice.reducer,
        projectReducer: projectSlice.reducer,
        companyReducer: companySlice.reducer,
        catalogReducer: catalogSlice.reducer,
        applicationReducer: applicationSlice.reducer,
        adminReducer: adminSlice.reducer,
        userReducer: userSlice.reducer
    }
});

export default store;