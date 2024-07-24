import React from 'react';
import { Pagination } from 'react-bootstrap';
import '../../assets/style/Pagination.css';

const PaginationComponent = ({ currentPage, totalPages, onPageChange }) => {
    const handleClick = (page) => {
        if (page !== currentPage && page > 0 && page <= totalPages) {
            onPageChange(page);
        }
    };

    const generatePaginationItems = () => {
        const items = [];
        for (let page = 1; page <= totalPages; page++) {
            items.push(
                <Pagination.Item key={page} active={page === currentPage} onClick={() => handleClick(page)}>
                    {page}
                </Pagination.Item>
            );
        }
        return items;
    };

    return (
        <Pagination className="justify-content-center">
            <Pagination.Prev onClick={() => handleClick(currentPage - 1)} disabled={currentPage === 1} />
            {generatePaginationItems()}
            <Pagination.Next onClick={() => handleClick(currentPage + 1)} disabled={currentPage === totalPages} />
        </Pagination>
    );
};

export default PaginationComponent;
