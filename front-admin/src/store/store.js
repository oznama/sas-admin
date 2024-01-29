import { configureStore } from "@reduxjs/toolkit";
import { alertSlice } from "./alert/alertSlice";
import { loadingSlice } from "./loading/loadingSlice";
import { modalSlice } from "./modal/modalSlice";
import { authSlice } from "./auth/authSlice";
import { projectSlice } from "./project/projectSlice";

export const store = configureStore({
    reducer: {
        alert: alertSlice.reducer,
        loading: loadingSlice.reducer,
        modal: modalSlice.reducer,
        auth: authSlice.reducer,
        projectReducer: projectSlice.reducer,
    }
});

export default store;