import {
    currentPage,
    currentSpicyLevel,
    currentKeyword,
    currentSort,
    setCurrentPage, setCurrentKeyword
} from './searchRecipe.js';

import { initializeSpicyIcons } from './spicyFilter.js';
import { initializeDropdown } from './dropDown.js';

const recipeContainer = document.getElementById('recipeContainer');

const addRecipeCard = function(recipeData) {
    if (recipeContainer === null) {
        console.error("Recipe container element not found.");
        return;
    }

    const recipes = recipeData.content;
    // hasNext = !recipeData.last; //나중에 무한스크롤 구현시 사용

    recipes.forEach(recipe => {
        const translatedFormatRecipe = translateRecipeData(recipe);
        const cardHtml = createRecipeCard(translatedFormatRecipe);
        recipeContainer.insertAdjacentHTML('beforeend', `<div class="col d-flex justify-content-center" > ${cardHtml} </div>`)
    })

    document.querySelectorAll('.card-custom').forEach(card => {
        card.addEventListener('click', function() {
            const noValue = this.getAttribute('data-no');
            const type = this.getAttribute('data-type');
            if (noValue) {
                alert(type + noValue)
            }
        });
    });
};

const fetchRecipeData = function(resetPage = true) {
    if (resetPage) {
        setCurrentPage(0);
        // hasNext = true;
        if (recipeContainer) {
            recipeContainer.innerHTML = '';
        }
    }

    // if (!hasNext) {
    //     return;
    // }

    let url = `api/recipes/scroll?page=${currentPage}&size=8&sort=${currentSort},desc`; //임시로 8개로

    if (currentSpicyLevel !== null) {
        url += `&spicyLevel=${currentSpicyLevel}`;
    }
    if (currentKeyword !== null && currentKeyword.trim() !== "") {
        url += `&keyword=${encodeURIComponent(currentKeyword.trim())}`;
    }

    fetch(url)
        .then(response => {
            return response.json();
        })
        .then(recipeData => {
            addRecipeCard(recipeData);
            setCurrentPage(currentPage + 1);
        })
        .catch(error => {
            console.error("데이터를 불러오는 중 오류 발생:", error);
        });
};

function initializeSearch() {
    const searchInput = document.querySelector('.search-input');

    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            setCurrentKeyword(this.value);
            fetchRecipeData(true);
        }
    });
}

document.addEventListener('recipeFilterChange', function(e) {
    console.log(`[recipeList.js] 필터/정렬 변경 이벤트 감지: ${e.detail.type}`);
    fetchRecipeData(true);
});

document.addEventListener('DOMContentLoaded', function() {
    initializeSpicyIcons();
    initializeDropdown();
    initializeSearch();

    fetchRecipeData();
});
