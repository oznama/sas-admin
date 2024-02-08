import { configureStore } from "@reduxjs/toolkit";
import { alertSlice } from "./alert/alertSlice";
import { loadingSlice } from "./loading/loadingSlice";
import { modalSlice } from "./modal/modalSlice";
import { authSlice } from "./auth/authSlice";
import { projectSlice } from "./project/projectSlice";
import { companySlice } from "./company/companySlice";

export const store = configureStore({
    reducer: {
        alert: alertSlice.reducer,
        loading: loadingSlice.reducer,
        modal: modalSlice.reducer,
        auth: authSlice.reducer,
        projectReducer: projectSlice.reducer,
        companyReducer: companySlice.reducer,
    }
});

export default store;