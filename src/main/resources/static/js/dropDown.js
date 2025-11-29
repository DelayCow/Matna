import { currentSort, dispatchFilterChange, setCurrentSort } from './searchRecipe.js';

export function initializeDropdown() {
    const sortDropdownItems = document.querySelectorAll('.sort-dropdown .dropdown-item');
    const sortButton = document.querySelector('.sort-dropdown .btn');

    sortDropdownItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();

            sortDropdownItems.forEach(i => i.classList.remove('active'));
            this.classList.add('active');
            sortButton.textContent = this.textContent;

            const newSort = this.getAttribute('data-sort');
            setCurrentSort(newSort)
            dispatchFilterChange('sort', newSort);
        });
    });
}