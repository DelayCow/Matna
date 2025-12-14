import {debounce, fetchSearchResults} from "./searchIngredient.js";
import {showAlertModal} from "./modal.js";
export const removeReview = async function(reviewNo, recipeNo){
    try{
        const response = await fetch(`/api/reviews/${reviewNo}`,{
            method: 'DELETE'
        });

        if(response.ok) {
            showAlertModal(
                '삭제 완료',
                '리뷰가 성공적으로 삭제되었습니다!',
                'success',
                () => {
                    window.location.href = `/review/recipe/${recipeNo}`;
                }
            );
        }else{
            const errorData = await response.json();
            const errorMessage = errorData.message || '서버 오류가 발생했습니다.';

            showAlertModal(
                '삭제 실패',
                `리뷰 삭제에 실패했습니다.<br><br><small class="text-muted">${errorMessage}</small>`,
                'error'
            );
        }

    }catch(error){
        console.error('네트워크 오류:', error);
        showAlertModal(
            '네트워크 오류',
            '서버와 통신할 수 없습니다.<br>잠시 후 다시 시도해주세요.',
            'error'
        );
    }
}
export const createRecipeInfo = function (data){
    return `<img id="recipe-thumbnailUrl" src="${data.thumbnailUrl}" alt="레시피 이미지" class="rounded me-3" style="width: 15%; height: 15%; object-fit: cover;">
            <div>
                <h6 class="fw-bold mb-3">${data.title}</h6>
                <small class="text-muted">작성자 | <span id="recipe-writerNickname">${data.writerNickname}</span></small>
            </div>`
}
export const createIngredientPart = function(ing){
    return `<div class="ingredient-item-wrapper mb-2" data-ingredient-name="${ing.ingredientName}">
                    <div class="d-flex align-items-center">
                        <span class="ingredient-name me-3">${ing.ingredientName}</span>
                        <span class="ingredient-info me-auto">${ing.amount}${ing.unit}</span>

                        <i class="bi bi-arrow-right fs-5 ms-2 me-2"></i>

                        <div class="select-input-group">
                            <input type="text"
                                   class="form-control form-control-sm"
                                   id="${ing.ingredientName}_itemSelect"
                                   placeholder="대체재료 검색">
                            <i class="bi bi-search select-icon"></i>
                            <div class="dropdown-menu" id="${ing.ingredientName}_itemDropdownMenu"></div>
                        </div>
                    </div>
                    <div class="ingredient-input-container"></div>
                </div>`
}
export const initializeIngredient = function (){
    document.querySelectorAll('.ingredient-item-wrapper').forEach(wrapper => {
        const arrow = wrapper.querySelector('.bi-arrow-right');
        const ingredientName = wrapper.dataset.ingredientName;
        const searchInput = wrapper.querySelector(`#${ingredientName}_itemSelect`);
        const dropdownMenu = wrapper.querySelector(`#${ingredientName}_itemDropdownMenu`);
        const inputContainer = wrapper.querySelector('.ingredient-input-container');
        let isItemClicked = false;
        let isFormVisible = false;

        arrow.addEventListener('click', function() {
            if (isFormVisible) {
                inputContainer.innerHTML = '';
                searchInput.parentElement.style.display = 'none';
                isFormVisible = false;
            } else {
                searchInput.parentElement.style.display = 'flex';
                searchInput.focus();
            }
        });

        searchInput.addEventListener('input', debounce(function() {
            if (isItemClicked) {
                isItemClicked = false;
                return;
            }
            const query = this.value.trim();
            if (query.length > 0) {
                fetchSearchResults(query, (results) => updateDropdownMenu(results, dropdownMenu, searchInput, inputContainer, ingredientName));
            } else {
                dropdownMenu.classList.remove('show');
            }
        }, 300));

        document.addEventListener('click', function(e) {
            if (!searchInput.parentElement.contains(e.target)) {
                dropdownMenu.classList.remove('show');
            }
        });
    });
}
export const createIngredientFormHtml = function(ingredientName) {
    const namePrefix = `unit_${ingredientName.replace(/\s/g, '_')}_${Date.now()}`;

    return `
        <div class="ingredient-input-form mt-2">
            <div class="d-flex align-items-center p-2 bg-light rounded">
                <input type="text" class="form-control form-control-sm me-2 substitute-name" style="width: 35%;" placeholder="대체재료" readonly value="">
                <input type="text" class="form-control form-control-sm me-2" style="width: 20%;" placeholder="수량" value="0">
                <div class="d-flex unit-radio-group" style="width: 45%;">
                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ml" value="ml" autocomplete="off" checked>
                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ml">ml</label>

                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ea" value="개" autocomplete="off">
                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ea">개</label>

                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_sp" value="스푼(T)" autocomplete="off">
                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_sp">스푼(T)</label>

                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_g" value="g" autocomplete="off">
                    <label class="btn btn-outline-secondary btn-sm" for="${namePrefix}_g">그램(g)</label>
                </div>
            </div>
        </div>
    `;
}
export const updateDropdownMenu = function (results, dropdownMenu, searchInput, inputContainer, originalIngredientName) {
    dropdownMenu.innerHTML = '';

    if (results && results.length > 0) {
        results.forEach(item => {
            const a = document.createElement('a');
            a.classList.add('dropdown-item');
            a.href = '#';
            a.textContent = item.ingredientName;

            a.addEventListener('click', (e) => {
                e.preventDefault();

                if (inputContainer.innerHTML.trim() === '') {
                    inputContainer.innerHTML = createIngredientFormHtml(originalIngredientName);
                }

                const substituteInput = inputContainer.querySelector('.substitute-name');
                substituteInput.value = item.ingredientName;

                searchInput.value = '';
                dropdownMenu.classList.remove('show');
                searchInput.parentElement.style.display = 'none';

                inputContainer.closest('.ingredient-item-wrapper').dataset.formVisible = 'true';
            });

            dropdownMenu.appendChild(a);
        });
        dropdownMenu.classList.add('show');
    } else {
        dropdownMenu.classList.remove('show');
    }
}
export const fetchRecipeData = function(recipeNo){
    const ingredientPart = document.getElementById('original-ingredients-list')
    const recipeInfo = document.querySelector('.recipe-info');
    let ingredientData = '';
    return fetch(`/api/recipes/detail/${recipeNo}`)
        .then(response => {
            return response.json();
        }).then(data => {
            ingredientData += data.ingredients.map(i => createIngredientPart(i)).join('')
            recipeInfo.insertAdjacentHTML('beforeend', createRecipeInfo(data))
            ingredientPart.insertAdjacentHTML('beforeend', ingredientData)
            initializeIngredient();
        }).catch(e => {
            console.error("레시피 데이터 로드 중 오류: ", e);
        })
}