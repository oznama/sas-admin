import { NavBarPage } from '../custom/NavBar/NavBarPage';
import './project.main.css';

export const ProjectAdd = () => {

  return (
    <div>
      <NavBarPage />
      <div className='container'>
        <div className="d-flex justify-content-center">
          <h1 className="fs-4 card-title fw-bold mb-4">Proyecto Nuevo</h1>
        </div>
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
