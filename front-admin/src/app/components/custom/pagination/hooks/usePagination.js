// Reference: https://www.freecodecamp.org/news/build-a-custom-pagination-component-in-react/
// Reference: https://github.com/mayankshubham/react-pagination/tree/master

import { useMemo } from "react"

export const DOTS = '...';

const range = (start, end) => {
    const length = end - start + 1;
    return Array.from({length}, (_, idx) => idx + start);
}

export const usePagination = ({
    totalCount,
    pageSize,
    siblingCount = 1,
    currentPage
}) => {
    const paginationRange = useMemo(() => {

        const totalPageCount = Math.ceil(totalCount / pageSize);
        const totalPageNumbers = siblingCount + 5;

        if ( totalPageNumbers >= totalPageCount ) {
            return range(1, totalPageCount);
        }

        const leftSiblingIndex = Math.max(currentPage - siblingCount, 1);
        const rightSiblingIndex = Math.min(currentPage + siblingCount, totalPageCount);

        const shouldShowLeftDocs = leftSiblingIndex > 2;
        const shouldShowRightDocs = rightSiblingIndex < totalPageCount - 2;

        const firstPageIndex = 1;
        const lastPageIndex = totalPageCount;

        if (!shouldShowLeftDocs && shouldShowRightDocs) {
            const leftItemCount = 3 + 2 * siblingCount;
            const leftRange = range(1, leftItemCount);

            return [...leftRange, DOTS, totalPageCount];
        }

        if (shouldShowLeftDocs && !shouldShowRightDocs) {
            const rightItemCount = 3 + 2 * siblingCount;
            const rightRange = range(totalPageCount - rightItemCount + 1, totalPageCount);

            return [firstPageIndex, DOTS, ...rightRange];
        }

        if ( shouldShowLeftDocs && shouldShowRightDocs ) {
            const middleRange = range(leftSiblingIndex, rightSiblingIndex);
            return [firstPageIndex, DOTS, ...middleRange, DOTS, lastPageIndex];
        }

    }, [totalCount, pageSize, siblingCount, currentPage]);
    
    return paginationRange;
}