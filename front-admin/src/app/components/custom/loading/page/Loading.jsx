import { useContext } from "react";
import { LoadingContext } from "../context/LoadingContext";

export const Loading = () => {

  const { isLoading } = useContext( LoadingContext );
  console.log('isLoading', isLoading);

  const styleChangeSizeSpinner = () => ({
    width: '3.5rem', 
    height: '3.5rem',
  })

  const buildSpinner = () => (
    <div className='w-100 h-100 z-3 position-absolute d-flex flex-column min-vh-100 justify-content-center align-items-center bg-secondary bg-opacity-75'>
      <div className="spinner-border text-primary" style={ styleChangeSizeSpinner() } role="status">
        <span className="visually-hidden">Loading...</span>
      </div>
    </div>
  );

  return (
    isLoading && buildSpinner()
  );
}
