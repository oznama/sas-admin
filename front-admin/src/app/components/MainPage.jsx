import React from 'react'
import { LoadingProvider } from './custom/loading/context/LoadingProvider'
import { Loading } from './custom/loading/page/Loading'
import { NavBarPage } from './custom/NavBarPage'
import { AppRouter } from '../router/AppRouter'
import { Footer } from './custom/Footer'

export const MainPage = () => {
  return (
    <LoadingProvider>
        <Loading />
        <NavBarPage />
        <div className='w-100 p-2'>
            <AppRouter />
        </div>
        <Footer />
    </LoadingProvider>
  )
}
