import { useState } from 'react';
import { TablePendings, pendingType } from './TablePendings';

const PENDING_SETTING = [
    {
        currentTab: 1,
        title: 'Vencidos',
        styleTitle: 'danger',
        service: pendingType.due
    },
    {
        currentTab: 1,
        title: 'Vigentes',
        styleTitle: 'success',
        service: pendingType.crt
    },
    {
        currentTab: 1,
        title: 'Próximos',
        styleTitle: 'primary',
        service: pendingType.nxt
    },
    {
        currentTab: 2,
        title: 'Completados',
        styleTitle: 'dark',
        service: pendingType.end
    }
];

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
                {
                    PENDING_SETTING.map( (p, index) => (
                        currentTab === p.currentTab && <TablePendings key={index} title={p.title} styleTitle={p.styleTitle} service={ p.service } />
                    ))
                }
            </div>
        </>
    )
}