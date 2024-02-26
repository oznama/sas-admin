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
  const classNameAlert = show ? `d-flex align-items-center justify-content-center z-2 w-100 h-100 ${ show ? 'show' : '' }` : 'd-none';
  const classNameAlertSm = show ? `mt-5 position-absolute z-3 align-items-center ${ show ? 'show' : '' }` : 'd-none';

  return (
    <>
        <Loading />
        <ModalContainer />
        <NavBarPage />
        <div className={ classNameAlert }>
          <div className={ classNameAlertSm } >
            <Alert padded />
          </div>
        </div>
        <div className='w-100 p-1'>
            <AppRouter />
        </div>
        <Footer />
    </>
  )
}
