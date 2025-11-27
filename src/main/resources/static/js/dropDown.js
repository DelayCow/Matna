const sortDropdownItems = document.querySelectorAll('.sort-dropdown .dropdown-item');
const sortButton = document.querySelector('.sort-dropdown .btn');

sortDropdownItems.forEach(item => {
    item.addEventListener('click', function(e) {
        e.preventDefault();
        sortDropdownItems.forEach(i => i.classList.remove('active'));
        this.classList.add('active');
        sortButton.textContent = this.textContent;
        const sort = sortButton.getAttribute('data-sort')
        // 실제 정렬 로직은 여기에 추가
    });
});