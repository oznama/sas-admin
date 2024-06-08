import React, { useEffect, useRef, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { setModalChild } from '../../../store/modal/modalSlice';
import { displayNotification, encrypt, styleCheckBox, styleTableRow } from '../../helpers/utils';
import { projectPlanCheck, projectPlanPreview, projectPlanSendEmail } from '../../services/NativeService';
import { alertType } from '../custom/alerts/types/types';

export const ProjectPlanModal = () => {

    const btnLabels =  {
        1: {
            btnNext: 'Continuar'
        },
        2: {
            btnPre: 'Regresar',
            btnFile: 'Cargar Archivo',
            btnNext: 'Continuar'
        },
        3: {
            btnPre: 'Regresar',
            btnNext: 'Envíar Correo'
        }
    }

    const passwordMaxLength = 4;

    const dispatch = useDispatch();
    
    const pswRef = useRef();
    const inputFile = useRef() ;

    const { project } = useSelector( state => state.projectReducer );
    const { user } = useSelector( state => state.auth );

    const [steper, setSteper] = useState(1);

    const [showPswd, setShowPswd] = useState(false);
    const [password, setPassword] = useState('');

    const [data, setData] = useState([]);

    const [file, setFile] = useState();

    // Fix keys functionality
    const [allChecked, setAllChecked] = useState(true);
    const [keys, setKeys] = useState([]);
    const [allKeys, setAllKeys] = useState([]);
    const [isCheck, setIsCheck] = useState(true);

    const fetchCheck = () => {
        projectPlanCheck(project.key).then(response => {
            const data = response.map(r => ({
                ...r,
                checked: allChecked || keys.includes(r.appName)
            }));
            setData(data);
        }).catch(error => {
            console.log('Error to load plan check', error);
        })
    }

    const fetchPreview = () => {
        projectPlanPreview(project.key, 'PMT,CTM').then(response => {
            setData(response);
        }).catch(error => {
            console.log('Error to load plan review', error);
        })
    }

    useEffect(() => {
        if(steper === 1) {
            fetchCheck();
        } else if (steper === 2) {
            fetchPreview();
        } else if (steper === 3) {
            pswRef.current.focus();
        }
    }, [steper])

    const toggleAllCheckboxes = () => {
        const newAllChecked = !allChecked;
        setAllChecked(newAllChecked);
        const updatedKeys = newAllChecked ? allKeys : [];
        setKeys(updatedKeys);
        const updatedData = data.map(item => ({ ...item, checked: newAllChecked }));
        setData(updatedData);
    };

    const toggleCheckbox = (pKey, index) => {
        const dataUpdated = [...data];
        dataUpdated[index].checked = !dataUpdated[index].checked;

        const keySelecteds = dataUpdated[index].checked
            ? [...keys, pKey]
            : keys.filter(k => k !== pKey);

        setData(dataUpdated);
        setKeys(keySelecteds);

        if (dataUpdated.every(item => item.checked)) {
            setAllChecked(true);
        } else {
            setAllChecked(false);
        }
    };

    const onChangePassword = ({ target }) => setPassword(target.value);

    const onChangeFile = ({target}) => setFile(target.files[0]);

    const onSubmit = () => {
        const passwordEncrypted = encrypt(password);
        // TODO Remove accents in fileName
        const formData = new FormData();
        formData.append('username', user.email);
        formData.append('password', passwordEncrypted);
        formData.append('pKey', project.key);
        formData.append('apps', keys);
        formData.append('fileName', file.name);
        formData.append('file', file);
        projectPlanSendEmail(formData).then(response => {
            if(response.code && response.code !== 200) {
                displayNotification(dispatch, 'El correo no ha sido envíado', alertType.error);
            } else {
                displayNotification(dispatch, '¡Correo envíado correctamente!', alertType.success);
                dispatch( setModalChild(null));
            }
        })
    }

    const onClickNext = () => {
        if( steper === 3 && password.length > passwordMaxLength ) {
            onSubmit();
        } else if ( steper < 3 ) {
            setSteper(steper+1);
        }
    }

    const renderRowsFirstStep = () => data && data.map(({
        appName,
        leader,
        developer,
        checked
    }, index) => (
        <tr key={ index }>
            <td className="text-center">
                <input style={styleCheckBox}
                    type="checkbox"
                    checked={checked}
                    onChange={() => toggleCheckbox(appName, index)}
                />
            </td>
            <th className="text-center" style={ styleTableRow } scope="row">{ appName }</th>
            <td className="text-start" style={ styleTableRow }>{ leader }</td>
            <td className="text-start" style={ styleTableRow }>{ developer }</td>
        </tr>
    ));

    const renderFirstStep = () => (
        <div>
            <h5 className="pb-3 card-title fw-bold">Selecciona las aplicaciones del plan de trabajo</h5>
            <div className='table-responsive text-nowrap'>
                <table className="table table-sm table-bordered table-striped table-hover">
                    <thead className="thead-dark">
                        <tr>
                            <th className="text-center fs-6" scope="col">
                                <input style={styleCheckBox}
                                    type="checkbox"
                                    checked={allChecked}
                                    onChange={toggleAllCheckboxes}
                                />
                            </th>
                            <th className="text-center fs-6" scope="col">Aplicaci&oacute;n</th>
                            <th className="text-center fs-6" scope="col">L&iacute;der SAS</th>
                            <th className="text-center fs-6" scope="col">Desarrollador SAS</th>
                        </tr>
                    </thead>
                    <tbody>
                        { renderRowsFirstStep() }
                    </tbody>
                </table>
            </div>
        </div>
    )

    const styleGray = { background:'#F1F1F1', borderRadius: '5px', padding: '10px' };
    const styleParagraph = { ...styleGray, overflowX: 'scroll' };
    const stylePreviwer = { ...styleGray, overflowY: 'scroll', height: '500px' };

    const getCC = () => {
        let cc = '';
        for( const index in data ) {
            if( !cc.includes(data[index].leaderMail) ) {
                cc += `${data[index].leaderMail}, `
            }
            if( !cc.includes(data[index].developerMail) ) {
                cc += `${data[index].developerMail}, `
            }
        }
        return cc.substring(0, cc.length-2);
    }

    const renderSecondStep = () => (
        <div>
            <h5 className="pb-3 card-title fw-bold text-center">Vista previa</h5>
            <section>
                <label>TO:</label>
                <p style={styleParagraph }></p>
                <label>CC:</label>
                <p style={ styleParagraph }>{ getCC() }</p>
            </section>
            <div style={stylePreviwer}>
                PM,
                <p>Te env&iacute;o plan de trabajo y responsable por parte de SAS para este proyecto:</p>
                {
                    data && data.map( (d, index) => (
                        <div>
                            <b>{d.appName}</b> responsable <b>{d.leader}</b>
                            <ul>
                                <li>Inicio {d.startDate}</li>
                                <li>An&aacute;lisis y dise&ntilde;o {d.designDate}</li>
                                <li>Cosntrucci&oacute;n {d.developmentDate}</li>
                                <li>Cierre {d.endDate}</li>
                            </ul>
                        </div>
                    ))
                }
                Saludos<br/>
                <br/>
                ---<br/>
                Jaime Carreño Méndez.<br/>
                SAS México.<br/>
                Consultor<br/>
                email:jaime.carreno@sas-mexico.com<br/>
                Tel SAS: 55-32-36-94<br/>
                Cel. 55-37-18-66-58<br/>
                www.sas-mexico.com
            </div>
        </div>
    )

    const onClickShowPassword = () => {
        setShowPswd(!showPswd);
        pswRef.current.focus();
    }

    const renderThirdStep = () => (
        <div>
            <h4>El correo ser&aacute; env&iacute;ado con la cuenta</h4>
            <h5 className='text-center text-success'>{ user.email }</h5>
            <label>Contrase&ntilde;a de tu correo</label>
            <div class="input-group mb-3">
                <input ref={pswRef} className="form-control" name="password" required
                    type={ showPswd ? 'text' : 'password' }
                    value={ password } onChange={ onChangePassword } />
                <span class="input-group-text" onClick={ onClickShowPassword }><i className={`bi bi-eye${ showPswd ? '-slash-fill' : '-fill'}`}></i></span>
            </div>
        </div>
    )

    const renderButtons = () => (
        <div className="pt-3 d-flex justify-content-between">
            {
                steper > 0 && btnLabels[steper] && btnLabels[steper].btnPre ?
                <button type="button" className="btn btn-link" onClick={ () => setSteper(steper -1) }>
                    &lt;&lt; { btnLabels[steper].btnPre }
                </button>
                : <div></div>
            }
            {
                steper > 0 && btnLabels[steper] && btnLabels[steper].btnFile &&
                <>
                    <button type="button" className={`btn btn-${ file ? 'success' : 'warning' }`} onClick={ () => inputFile.current.click() }>
                        { btnLabels[steper].btnFile }&nbsp;&nbsp;<i class="bi bi-upload"></i>
                    </button>
                    <input type='file' id='file' ref={inputFile} style={{display: 'none'}} accept=".doc, .docx" onChange={ onChangeFile } />
                </>
            }
            {
                steper > 0 && btnLabels[steper] &&
                <button type="button" className={`btn btn-${ steper === 3 ? 'primary' : 'link'}`}
                    onClick={ () => onClickNext() } disabled={ steper === 3 && password.length < passwordMaxLength }>
                    { btnLabels[steper].btnNext }&nbsp;
                    { steper !== 3 && '>>'  }
                    { steper === 3 && <span><i className="bi bi-envelope"></i></span> }
                </button>
            }
        </div>
    )

    //const title = `${project.key} ${project.description}`;

    return (
        <div className='bg-white rounded-3'>
            <div className="d-flex flex-row-reverse gap-3 p-2">
                <button type="button" className="btn btn-linl" onClick={ () => dispatch( setModalChild(null) ) }>
                    <span className="bi bi-x-lg"></span>
                </button>
            </div>
                <div className='px-5 pb-5'>
                { steper === 1 && renderFirstStep() }
                { steper === 2 && renderSecondStep() }
                { steper === 3 && renderThirdStep() }
                { renderButtons() }
            </div>
        </div>
    )
}
