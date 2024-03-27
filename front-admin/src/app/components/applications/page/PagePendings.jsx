import { useState } from 'react';
import { TablePendings, pendingType } from './TablePendings';

export const PagePendings = () => {

    const [currentTab, setCurrentTab] = useState(1);

    const renderTabs = () => (
        <div className="d-flex flex-row-reverse">
            <ul className="nav nav-tabs">
            <li className="nav-item" onClick={ () => setCurrentTab(1) }>
                <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Pendientes</a>
            </li>
            <li className="nav-item" onClick={ () => setCurrentTab(2) }>
                <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Completados</a>
            </li>
            </ul>
        </div>
    )

    return (
        <>
            <div className='px-5'>
                { renderTabs() }
            </div>
            <div className="d-flex flex-column gap-3 my-3">
                { currentTab === 1 && <TablePendings title={'Vencidos'} styleTitle={'danger'} service={ pendingType.due } /> }
                { currentTab === 1 && <TablePendings title={'Vigentes'} styleTitle={'success'} service={ pendingType.crt } /> }
                { currentTab === 1 && <TablePendings title={'Próximos'} styleTitle={'primary'} service={ pendingType.nxt } /> }
                { currentTab === 2 && <TablePendings title={'Completados'} styleTitle={'dark'} service={ pendingType.end } /> }
            </div>
        </>
    )
}