import './navbar.main.css';

export const NavBarPage = () => {
  return (
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
      <div class="container-fluid">
        <a class="navbar-brand" href="/">SAS Administrator</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarText">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <a class="nav-link active" aria-current="page" href="#">Opcion 1</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Opcion 2</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Opcion 3</a>
            </li>
          </ul>
          <span class="navbar-text">
            Selene Pascali
          </span>
        </div>
      </div>
    </nav>
  )
}
