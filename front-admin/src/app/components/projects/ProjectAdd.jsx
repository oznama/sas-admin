import { NavBarPage } from '../custom/NavBar/NavBarPage';
import './project.main.css';

export const ProjectAdd = () => {

  return (
    <div>
      <NavBarPage />
      <div className='container'>
        <h1>Proyecto Nuevo</h1>
        <form>
          <label>Clave:</label>
          <input placeholder='Ingresa clave'></input>
          <label>Nombre:</label>
          <input placeholder='Ingresa nombre'></input>
          <label>Descripcion:</label>
          <input placeholder='Ingresa descripcion'></input>
        </form>
      </div>
    </div>
  )


}
