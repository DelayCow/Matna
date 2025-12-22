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
    api.fetch(`/api/ingredients?keyword=${encodeURIComponent(query)}`)
        .then(response => {
            return response.json();
        })
        .then(data =>{
            updateDropdownMenu(data)})
        .catch(error => {
        console.error('재료 검색 중 오류 발생:', error);
        document.getElementById('itemDropdownMenu').classList.remove('show');
    });
};
