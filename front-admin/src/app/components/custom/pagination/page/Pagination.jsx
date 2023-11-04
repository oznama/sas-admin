import { DOTS, usePagination } from "../hooks/usePagination"

export const Pagination = ({
    onPageChange,
    totalCount,
    siblingCount = 1,
    currentPage,
    pageSize,
}) => {
    const paginationRange = usePagination({
        currentPage,
        totalCount,
        siblingCount,
        pageSize,
    })

    if ( !paginationRange || paginationRange.length < 1) {
        return null;
    }

    const onNext = () => {
        if ( currentPage < lastPage ) {
            onPageChange(currentPage);
        }
    }

    const onPrevious = () => {
        if ( currentPage > 1 ) {
            onPageChange(currentPage - 2);
        }
    }

    const classPageItemWithDisabled = disabled => {
        const classDisabled = disabled ? 'disabled' : '';
        return `page-item ${classDisabled}`;
    }

    const lastPage = paginationRange[paginationRange.length - 1];

    const renderItems = () => paginationRange.map( (pageNumber, index) => {
        if(pageNumber === DOTS) {
            return (
                <li key={ index } className={ classPageItemWithDisabled(true) }> 
                    <a className="page-link"> &#8230; </a>
                </li>
            )
        }
        
        return (
            <li key={ index } className={ classPageItemWithDisabled( pageNumber === currentPage ) } onClick={ () => onPageChange(pageNumber - 1) }>
                <a className="page-link">{ pageNumber }</a>
            </li>
        )
    });
    return (
        <div className="d-flex justify-content-end mt-3">
            <nav aria-label="Page navigation example">
                <ul className="pagination">
                    
                    <li className={ classPageItemWithDisabled( currentPage === 1 ) } onClick={ onPrevious }>
                        <a className="page-link" href="#" aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                        </a>
                    </li>

                    { renderItems() }
                    
                    <li className={ classPageItemWithDisabled( currentPage === lastPage ) } onClick={ onNext } >
                        <a className="page-link" href="#" aria-label="Next">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>

                </ul>
            </nav>
        </div>
    )
}
