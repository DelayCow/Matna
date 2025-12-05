import {initializeSpicyIcons} from "./spicyFilter.js";
import {debounce, fetchSearchResults} from "./searchIngredient.js";
import {showAlertModal, showValidationModal} from "./modal.js";

function handleImageUpload(input, target) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();

        reader.onload = function(e) {
            target.style.backgroundImage = `url(${e.target.result})`;
            target.style.backgroundSize = 'cover';
            target.style.backgroundPosition = 'center';
            target.style.backgroundRepeat = 'no-repeat';

            target.removeAttribute('data-original-url');
            //아이콘만 숨기기
            const icon = target.querySelector('i');
            if (icon) {
                icon.style.display = 'none';
            }

            target.classList.add('has-image');
        };
        reader.readAsDataURL(file);
    } else {
        target.style.backgroundImage = 'none';

        // 아이콘 다시 보이기
        const icon = target.querySelector('i');
        if (icon) {
            icon.style.display = 'block';
        } else {
            // 아이콘이 없으면 다시 추가
            target.innerHTML = '<i class="bi bi-plus fs-1 text-secondary"></i>';
        }

        target.classList.remove('has-image');
    }
}

function setupImageUploadListeners(container) {
    const fileInput = container.querySelector('.img-file-upload');

    if (fileInput) {
        fileInput.addEventListener('change', function() {
            handleImageUpload(this, container);
        });

        container.addEventListener('click', function() {
            fileInput.click();
        });
    }
}

document.getElementById('addStepBtn')?.addEventListener('click', function (e) {
    e.preventDefault();
    const parentDiv = this.parentElement;
    const newStep = document.createElement('div');
    newStep.className = 'recipe-step d-flex mb-3';
    newStep.innerHTML = `
        <div class="img-upload-area me-3 flex-shrink-0">
            <i class="bi bi-plus fs-1 text-secondary"></i>
            <input type="file" class="img-file-upload" accept="image/*" style="display: none;" />
        </div>
        <textarea class="form-control" rows="3" placeholder="레시피 설명"></textarea>
    `;
    parentDiv.before(newStep);

    const newStepImageContainer = newStep.querySelector('.img-upload-area');
    if (newStepImageContainer) {
        setupImageUploadListeners(newStepImageContainer);
    } else {
        console.error('새 단계의 .img-upload-area를 찾을 수 없습니다');
    }
});

const ingredientContainer = document.getElementById('ingredientContainer');
const other = document.getElementById('otherItem');
// document.getElementById('addOtherItemBtn').addEventListener('click', (e) => addIngredient(e, other.value))
let addedIngredients = [];

const searchInput = document.getElementById('itemSelect');
const itemMenu = document.getElementById('itemDropdownMenu');
let isItemClicked = false;

searchInput?.addEventListener('input', debounce(function() {
    if (isItemClicked) {
        isItemClicked = false;
        return;
    }
    const query = this.value.trim();
    if (query.length > 0) {
        fetchSearchResults(query, updateDropdownMenu);
    } else {
        itemMenu.classList.remove('show');
    }
}, 300));

function updateDropdownMenu(results) {
    itemMenu.innerHTML = '';

    if (results && results.length > 0) {
        results.forEach(item => {
            const a = document.createElement('a');
            a.classList.add('dropdown-item');
            a.href = '#';
            a.textContent = item.ingredientName;

            // a.addEventListener('click', (e) => addIngredient(e, item.ingredientName));

            itemMenu.appendChild(a);
        });
        itemMenu.classList.add('show');
    } else {
        itemMenu.classList.remove('show');
    }
}

const deleteIngredient = function (id) {
    const elementToRemove = document.getElementById(id);
    if (elementToRemove) {
        elementToRemove.remove();

        const ingredientName = id.replace('_div', '');
        const index = addedIngredients.indexOf(ingredientName);
        if (index > -1) {
            addedIngredients.splice(index, 1);
        }
    }
}

// const handleDeleteIngredient = function(button) {
//     const ingredientId = button.getAttribute('data-ingredient-id');
//     if (ingredientId) {
//         deleteIngredient(ingredientId);
//     }
// }

window.deleteIngredient = deleteIngredient;
// window.handleDeleteIngredient = handleDeleteIngredient;

const addIngredientHtml = function (name) {
    const id = name.replace(/[^a-zA-Z0-9가-힣]/g, '_');
    const uniqueUnitName = `${id}_unit`;

    return `<div class="d-flex align-items-center mt-3 p-2 bg-light rounded ingredient-item" id="${id}_div">
            <input type="text" class="form-control form-control-sm me-2" style="width: 30%; flex-shrink: 0;" placeholder="재료명" name="ingredientName" value="${name}" readonly>

            <input type="number" class="form-control form-control-sm text-end me-2" style="width: 20%; flex-shrink: 0;" placeholder="수량" name="amount" min="0" step="0.1">

            <div class="d-flex unit-radio-group" style="width: 40%; flex-shrink: 0;">
                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_ml" value="ml" autocomplete="off" checked>
                <label class="btn btn-outline-secondary btn-sm me-1" for="${id}_unit_ml">ml</label>

                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_ea" value="개" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm me-1" for="${id}_unit_ea">개</label>

                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_sp" value="T" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm me-1" for="${id}_unit_sp">스푼(T)</label>

                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_g" value="g" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm" for="${id}_unit_g">그램(g)</label>
            </div>

            <button class="btn btn-danger ms-4 flex-shrink-0" type="button" style="height: 32px; width: 50px; padding: 0;" onclick="deleteIngredient('${id}_div')">
                삭제
            </button>
        </div>`
}

const addIngredient = function (e, name) {
    e.preventDefault();
    if (name.length === 0) {
        showAlertModal('알림','재료 이름을 입력해야 추가할 수 있습니다.','info');
        return;
    }
    isItemClicked = true;
    if (addedIngredients.includes(name)) {
        showAlertModal('알림',`${name}은(는) 이미 추가된 재료입니다.`,'info');
    } else {
        ingredientContainer.insertAdjacentHTML('beforeend', addIngredientHtml(name));
        addedIngredients.push(name);
    }
    searchInput.value = '';
    other.value = '';
    itemMenu.classList.remove('show');
    searchInput.blur();
}

const loadExistingIngredients = function() {
    const existingIngredients = document.querySelectorAll('#ingredientContainer .ingredient-item input[name="ingredientName"]');
    existingIngredients.forEach(input => {
        const ingredientName = input.value;
        if (ingredientName && !addedIngredients.includes(ingredientName)) {
            addedIngredients.push(ingredientName);
        }
    });
}
function collectIngredients() {
    const ingredients = [];
    const ingredientDivs = document.querySelectorAll('#ingredientContainer > div.ingredient-item');

    ingredientDivs.forEach(div => {
        const nameInput = div.querySelector('input[name="ingredientName"]');
        const amountInput = div.querySelector('input[name="amount"]');
        const unitRadio = div.querySelector('.unit-radio-group input[type="radio"]:checked');

        if (nameInput && amountInput && unitRadio) {
            const name = nameInput.value;
            const amount = parseFloat(amountInput.value);
            const unit = unitRadio.value;

            ingredients.push({
                ingredientName: name,
                amount: amount || 0,
                unit: unit
            });
        }
    });

    return ingredients;
}

function collectSteps(formData) {
    const steps = [];
    const stepDivs = document.querySelectorAll('.recipe-step');

    stepDivs.forEach((div, index) => {
        const order = index + 1;
        const contentTextarea = div.querySelector('textarea');
        const imgUploadArea = div.querySelector('.img-upload-area');
        console.log(`\n--- Step ${order} ---`);
        console.log('imgUploadArea:', imgUploadArea);
        let fileInput = imgUploadArea?.querySelector('.img-file-upload');
        let imageFileName = null;

        if (fileInput && fileInput.files.length > 0) {
            const file = fileInput.files[0];
            const fieldName = `stepImage_${order}`;

            formData.append(fieldName, file);
            imageFileName = fieldName;
            console.log('✅ 새 파일 업로드:', fieldName, file.name);
        }else if (imgUploadArea) {
            const originalUrl = imgUploadArea.getAttribute('data-original-url');
            const backgroundImage = imgUploadArea.style.backgroundImage;
            console.log('data-original-url:', originalUrl);
            if (originalUrl && originalUrl.trim() !== '') {
                imageFileName = originalUrl;
            } else if (backgroundImage && backgroundImage !== 'none' && backgroundImage !== '') {
                imageFileName = 'EXISTING';
            }
        }
        console.log('최종 imageFileName:', imageFileName);
        steps.push({
            stepOrder: order,
            content: contentTextarea ? contentTextarea.value : '',
            imageUrl: imageFileName,
        });
    });

    return steps;
}

async function submitRecipeData(form) {
    const formData = new FormData();
    const recipeData = {};
    const errors = [];
    const recipeNo = form.querySelector('#recipeNo').value;
    recipeData.recipeNo = recipeNo;
    const thumbnailInput = document.querySelector('.img-upload-area.thumbnail .img-file-upload');
    const imgUploadArea = document.querySelector('.img-upload-area');
    if (thumbnailInput && thumbnailInput.files.length > 0) {
        formData.append('thumbnailFile', thumbnailInput.files[0]);
        recipeData.thumnailUrl = 'thumbnailFile';
    }else if (imgUploadArea) {
        const originalUrl = imgUploadArea.getAttribute('data-original-url');
        const backgroundImage = imgUploadArea.style.backgroundImage;
        if (originalUrl && originalUrl.trim() !== '') {
            recipeData.thumnailUrl = originalUrl;
        } else if (backgroundImage && backgroundImage !== 'none' && backgroundImage !== '') {
            recipeData.thumnailUrl = 'EXISTING';
        }
    }

    const title = form.querySelector('#recipeTitle').value.trim();
    if (!title) {
        errors.push('제목을 입력해주세요');
    }
    recipeData.title = title;

    const summary = form.querySelector('#recipeSummary').value.trim();
    if (!summary) {
        errors.push('레시피 요약을 입력해주세요');
    }
    recipeData.summary = summary;

    const category = form.querySelector('#category').value;
    if (!category) {
        errors.push('카테고리를 선택해주세요');
    }
    recipeData.category = category;

    const prepTime = parseInt(form.querySelector('input[name="prepTime"]').value);
    if (!prepTime || prepTime <= 0) {
        errors.push('조리 시간을 입력해주세요');
    }
    recipeData.prepTime = prepTime || 0;

    const servings = parseInt(form.querySelector('input[name="servings"]').value);
    if (!servings || servings <= 0) {
        errors.push('인분을 입력해주세요');
    }
    recipeData.servings = servings || 0;

    recipeData.difficulty = form.querySelector('input[name="difficulty"]:checked').value;

    const activeSpicyIcon = document.querySelector('.spicy-level-icon.active');
    recipeData.spicyLevel = activeSpicyIcon ? parseInt(activeSpicyIcon.dataset.level) : 0;

    recipeData.ingredient = collectIngredients();
    if (recipeData.ingredient.length === 0) {
        errors.push('재료를 최소 1개 이상 추가해주세요');
    }

    recipeData.ingredient.forEach((ing, index) => {
        if (!ing.amount || ing.amount <= 0) {
            errors.push(`재료 "${ing.name}"의 수량을 입력해주세요`);
        }
    });

    recipeData.step = collectSteps(formData);
    if (recipeData.step.length === 0) {
        errors.push('레시피 순서를 최소 1개 이상 추가해주세요');
    }

    recipeData.step.forEach((step, index) => {
        if (!step.content || step.content.trim() === '') {
            errors.push(`${step.stepOrder}번째 단계의 설명을 입력해주세요`);
        }
        if (!step.imageUrl) {
            errors.push(`${step.stepOrder}번째 단계의 이미지를 등록해주세요`);
        }
    });

    if (errors.length > 0) {
        showValidationModal(errors);
        return;
    }

    const recipeJsonString = JSON.stringify(recipeData);

    formData.append('recipeRequest', recipeJsonString);
    try {
        const response = await fetch('/api/recipes', {
            method: 'PUT',
            body: formData
        });

        if (response.ok) {
            showAlertModal(
                '수정 완료',
                '레시피가 성공적으로 수정되었습니다!',
                'success',
                () => {
                    window.location.href = '/recipe';
                }
            );
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || '서버 오류가 발생했습니다.';

            showAlertModal(
                '수정 실패',
                `레시피 수정에 실패했습니다.<br><br><small class="text-muted">${errorMessage}</small>`,
                'error'
            );
        }
    } catch (error) {
        console.error('네트워크 오류:', error);
        showAlertModal(
            '네트워크 오류',
            '서버와 통신할 수 없습니다.<br>잠시 후 다시 시도해주세요.',
            'error'
        );
    }
}

document.addEventListener('DOMContentLoaded',function (){
    const imgUpload = document.querySelector('.img-upload-area');
    if (imgUpload) {
        setupImageUploadListeners(imgUpload);
    }

    document.querySelectorAll('.recipe-step').forEach(step => {
        const stepImage = step.querySelector('.img-upload-area');
        if (stepImage) {
            setupImageUploadListeners(stepImage);
        }
    });

    initializeSpicyIcons();
    loadExistingIngredients();

    const recipeForm = document.querySelector('form.container-fluid.content-area');

    recipeForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        try {
            await submitRecipeData(this);
        } catch (error) {
            console.error("레시피 수정 처리 중 최종 오류:", error);
            showAlertModal(
                '오류 발생',
                '예상치 못한 오류가 발생했습니다.<br>잠시 후 다시 시도해주세요.',
                'error'
            );
        }
    });
})
