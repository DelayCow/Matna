document.addEventListener('DOMContentLoaded',function (){
    const searchInput = document.getElementById('itemSelect');
    const itemMenu = document.getElementById('itemDropdownMenu');
    let isItemClicked = false;
    function debounce(func, delay) {
        let timeoutId;
        return function(...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => {
                func.apply(this, args);
            }, delay);
        };
    }

    searchInput.addEventListener('input', debounce(function() {
        if (isItemClicked) {
            isItemClicked = false;
            return;
        }
        const query = this.value.trim();
        if (query.length > 0) {
            fetchSearchResults(query);
        } else {
            itemMenu.classList.remove('show');
        }
    }, 300));

    function fetchSearchResults(query) {
        //여기서 재료목록 받아와야함
        const mockData = [
            { "id": 1, "name": "어간장" },
            { "id": 2, "name": "머리고깃살" },
            { "id": 3, "name": "어묵" },
            { "id": 4, "name": "쌀떡" },
            { "id": 5, "name": "라면" },
            { "id": 6, "name": "김치" }
        ];
        const filteredData = mockData.filter(item => item.name.includes(query));
        updateDropdownMenu(filteredData);
    }

    function updateDropdownMenu(results) {
        const menu = document.getElementById('itemDropdownMenu');
        menu.innerHTML = '';

        if (results && results.length > 0) {
            results.forEach(item => {
                const a = document.createElement('a');
                a.classList.add('dropdown-item');
                a.href = '#';
                a.textContent = item.name;

                a.addEventListener('click', function(e) {
                    e.preventDefault();
                    isItemClicked = true;
                    searchInput.value = item.name;
                    menu.classList.remove('show');
                    searchInput.blur();
                });

                menu.appendChild(a);
            });
            menu.classList.add('show');
        } else {
            menu.classList.remove('show');
        }
    }

    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !itemMenu.contains(e.target)) {
            itemMenu.classList.remove('show');
        }
    });
})