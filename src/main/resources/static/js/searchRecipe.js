export let currentPage = 0;
export let hasNext = true;
export let currentSpicyLevel = null;
export let currentKeyword = null;
export let currentSort = 'inDate'; // 초기 정렬 기준 (최신순)

export function dispatchFilterChange(type, value) {
    const event = new CustomEvent('recipeFilterChange', {
        detail: {
            type: type,
            value: value
        }
    });
    document.dispatchEvent(event);
}

export function setCurrentPage(newPage) {
    currentPage = newPage;
}

export function setHasNext(value) {
    hasNext = value;
}

export function setCurrentSpicyLevel(newSpicyLevel) {
    currentSpicyLevel = newSpicyLevel;
}

export function setCurrentKeyword(newKeyword) {
    currentKeyword = newKeyword;
}

export function setCurrentSort(newSort) {
    currentSort = newSort;
}