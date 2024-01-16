import React from 'react'
import { Route, Routes } from 'react-router-dom'
import { ProjectPage } from './ProjectPage'
import { ApplicationRouter } from '../applications/ApplicationRouter'
import { OrderRouter } from "../orders/OrderRouter";

export const ProjectRouter = () => (
    <Routes>
        <Route path="/add" element={ <ProjectPage /> } />
        <Route path="/:id/edit" element={ <ProjectPage /> } />
        <Route path="/:projectId/application/*" element={ <ApplicationRouter /> } />
        <Route path="/:projectId/order/*" element={ <OrderRouter /> } />
    </Routes>
)
