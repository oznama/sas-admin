import React from 'react'
import { Loading } from './custom/loading/Loading'
import { NavBarPage } from './custom/NavBarPage'
import { AppRouter } from '../router/AppRouter'
import { Footer } from './custom/Footer'

export const MainPage = () => {
  return (
    <>
        <Loading />
        <NavBarPage />
        <div className='w-100 p-2'>
            <AppRouter />
        </div>
        <Footer />
    </>
  )
}
