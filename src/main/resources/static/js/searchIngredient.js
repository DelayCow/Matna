export function debounce(func, delay) {
        let timeoutId;
        return function(...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => {
                func.apply(this, args);
            }, delay);
        };
    }

export function fetchSearchResults(query, updateDropdownMenu) {
    fetch(`/api/ingredients?keyword=${encodeURIComponent(query)}`)
        .then(response => {
            return response.json();
        })
        .then(data =>{updateDropdownMenu(data)})
        .catch(error => {
        console.error('재료 검색 중 오류 발생:', error);
        document.getElementById('itemDropdownMenu').classList.remove('show');
    });
};

//나중에 공구 등록할 때 써야해서 삭제안하고 주석처리해둡니다
    // function updateDropdownMenu(results) {
    //     const menu = document.getElementById('itemDropdownMenu');
    //     menu.innerHTML = '';
    //
    //     if (results && results.length > 0) {
    //         results.forEach(item => {
    //             const a = document.createElement('a');
    //             a.classList.add('dropdown-item');
    //             a.href = '#';
    //             a.textContent = item.ingredientName;
    //
    //             a.addEventListener('click', function(e) {
    //                 e.preventDefault();
    //                 isItemClicked = true;
    //                 searchInput.value = item.ingredientName;
    //                 menu.classList.remove('show');
    //                 searchInput.blur();
    //             });
    //
    //             menu.appendChild(a);
    //         });
    //         menu.classList.add('show');
    //     } else {
    //         menu.classList.remove('show');
    //     }
    // }

    // document.addEventListener('click', function(e) {
    //     if (!searchInput.contains(e.target) && !itemMenu.contains(e.target)) {
    //         itemMenu.classList.remove('show');
    //     }
    // });
