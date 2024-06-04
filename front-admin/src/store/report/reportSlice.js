import { createSlice } from "@reduxjs/toolkit";

export const reportSlice = createSlice({
    name: 'reportReducer',
    initialState: {
        currentFilter: "",
        reportType: "1",
        check: {},
        keysToPrint: [],
        stack: [],
    },
    reducers: {
        setReportType: (state,action)=>{
            state.reportType = action.payload;
        },
        clean: (state) => {
            state.currentFilter = '';
            state.reportType = '1';
            state.check = true;
            state.stack = [];
            state.stack = [];
        },
        setKeysToPrint: (state, action) => {
            state.keysToPrint = action.payload;
        },
        pushKey: (state, action) => {
            state.keysToPrint.push(action.payload);
        },
        pullKey: (state, action) => {
            const index = state.keysToPrint.indexOf(action.payload);
            if (index !== -1) {
                state.keysToPrint.splice(index, 1);
            }
        },
        setCurrentFilter: (state, action) => {
            state.currentFilter = action.payload;
        },
        setCheck: (state, action) => {
            state.check = action.payload;
        },
        addStack: (state, action) => {
            state.stack.push(action.payload);
        },
        setTrueById: (state, action) => {
            state.stack.forEach(item => {
                if (item.id === action.payload) {
                    item.boolean = true;
                }
            });
        },
        setFalseById: (state, action) => {
            state.stack.forEach(item => {
                if (item.id === action.payload) {
                    item.boolean = false;
                }
            });
        },
        clearStack: (state) => {
            state.stack = [];
        },
        clearKeys:(state) => {
            state.keysToPrint = [];
        }
    }
});

export const {
    setReportType,
    setCurrentFilter,
    setCheck,
    clean,
    addStack,
    setTrueById,
    setFalseById,
    clearStack,
    setKeysToPrint,
    pushKey,
    pullKey,
    clearKeys
} = reportSlice.actions;

export default reportSlice.reducer;
