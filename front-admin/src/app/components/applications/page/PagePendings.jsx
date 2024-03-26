import { useSelector } from 'react-redux';
import { TablePendings, pendingType } from './TablePendings';

export const PagePendings = () => {

    const { user, permissions } = useSelector( state => state.auth );

    return (
        <div className="d-flex flex-column gap-3 my-3">
            { user.role.id === 3 ? <div>Pendientes de Selene</div> : <TablePendings title={'Vencidos'} styleTitle={'danger'} service={ pendingType.due } /> }
            { !permissions.isAdminSas && <TablePendings title={'Vigentes'} styleTitle={'success'} service={ pendingType.crt } /> }
            { !permissions.isAdminSas && <TablePendings title={'Próximos'} styleTitle={'primary'} service={ pendingType.nxt } /> }
        </div>
    )
}