let currentPage = 0; // 현재 페이지 번호 (무한 스크롤용)
let hasNext = true; // 다음 페이지 존재 여부
let currentSpicyLevel = null;
let currentKeyword = null;
let currentSort = 'inDate'; // 초기 정렬 기준 (최신순)

const recipeContainer = document.getElementById('recipeContainer');

const addRecipeCard = function(recipeData) {
    if (recipeContainer === null) {
        console.error("Recipe container element not found.");
        return;
    }

    const recipes = recipeData.content;
    hasNext = recipeData.hasNext;

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
        currentPage = 0;
        hasNext = true;
        if (recipeContainer) {
            recipeContainer.innerHTML = '';
        }
    }

    if (!hasNext) {
        return;
    }

    let url = `api/recipe/scroll?page=${currentPage}&size=8&sort=${currentSort},desc`;

    if (currentSpicyLevel !== null) {
        url += `&spicyLevel=${currentSpicyLevel}`;
    }
    if (currentKeyword !== null && currentKeyword.trim() !== "") {
        url += `&keyword=${encodeURIComponent(currentKeyword.trim())}`;
    }

    console.log("Fetching URL:", url);

    fetch(url)
        .then(response => {
            return response.json();
        })
        .then(recipeData => {
            addRecipeCard(recipeData);
            currentPage++;
        })
        .catch(error => {
            console.error("데이터를 불러오는 중 오류 발생:", error);
        });
};

function initializeSearch() {
    const searchInput = document.querySelector('.search-input');

    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            currentKeyword = this.value;
            fetchRecipeData(true);
        }
    });
}

document.addEventListener('DOMContentLoaded', function() {
    initializeSpicyIcons();
    initializeDropdown();
    initializeSearch();

    fetchRecipeData();
});

window.fetchRecipeData = fetchRecipeData;
window.currentSpicyLevel = currentSpicyLevel;
window.currentKeyword = currentKeyword;
window.currentSort = currentSort;