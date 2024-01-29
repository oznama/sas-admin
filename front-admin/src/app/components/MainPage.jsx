import React from 'react'
import { Loading } from './custom/loading/Loading'
import { Alert } from './custom/alerts/page/Alert'
import { NavBarPage } from './custom/NavBarPage'
import { AppRouter } from '../router/AppRouter'
import { Footer } from './custom/Footer'
import { useSelector } from 'react-redux'
import { ModalContainer } from './custom/ModalContainer'

export const MainPage = () => {

  const { show } = useSelector(state => state.alert);
  const classNameDivMain = show ? `mt-2 position-absolute z-2 w-100 align-items-center ${ show ? 'show' : '' }` : 'd-none';

  return (
    <>
        {/* <Loading /> */}
        <ModalContainer />
        <NavBarPage />
        <div className={ classNameDivMain } >
          <Alert padded />
        </div>
        <div className='w-100 p-1'>
            <AppRouter />
        </div>
        <Footer />
    </>
  )
}
