import { useSelector } from "react-redux";

export const ModalContainer = () => {

    const { child } = useSelector( state => state.modal );
    
    const main = () => (
        <div className='w-100 h-100 z-3 position-absolute d-flex flex-column min-vh-100 justify-content-center align-items-center bg-secondary bg-opacity-75'>
            { child }
        </div>
    )
    
    return child && main();
}
