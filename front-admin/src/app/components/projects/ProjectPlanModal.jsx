import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { setModalChild } from '../../../store/modal/modalSlice';
import { encrypt } from '../../helpers/utils';

export const ProjectPlanModal = () => {

    const dispatch = useDispatch();
    const { user } = useSelector( state => state.auth );
    
    const [password, setPassword] = useState('');

    const onChangePassword = ({ target }) => setPassword(target.value);

    const onClickSend = () => {
        const passwordEncrypted = encrypt(password);
        console.log('passwordEncrypted', passwordEncrypted);
        dispatch( setModalChild(null));
    }

    return (
        <div className='bg-white rounded-3'>
            <div className="d-flex flex-row-reverse gap-3 p-2">
                <button type="button" className="btn btn-linl" onClick={ () => dispatch( setModalChild(null) ) }>
                    <span className="bi bi-x-lg"></span>
                </button>
            </div>
            <div className='px-5 pb-5'>
                <h4>El correo ser&aacute; env&iacute;ado con la cuenta</h4>
                <h5 className='text-center text-success'>{ user.email }</h5>
                <label>Contrase&ntilde;a de tu correo</label>
                <input type="password" className="form-control" name="password" required value={ password } onChange={ onChangePassword } />
                <div className="pt-3 d-flex flex-row-reverse">
                    <button type="button" className="btn btn-primary" onClick={ onClickSend }>
                        Enviar Correo&nbsp;<span><i className="bi bi-envelope"></i></span>
                    </button>
                </div>
            </div>
        </div>
    )
}
