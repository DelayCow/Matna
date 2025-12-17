import {initializeSpicyIcons} from "./spicyFilter.js";
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
            const icon = target.querySelector('i');
            if (icon) {
                icon.style.display = 'none';
            }

            target.classList.add('has-image');
        };
        reader.readAsDataURL(file);
    } else {
        target.style.backgroundImage = 'none';

        const icon = target.querySelector('i');
        if (icon) {
            icon.style.display = 'block';
        } else {
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

let addedIngredients = [];

const addIngredientHtml = function (name) {
    const id = name.replace(/[^a-zA-Z0-9가-힣]/g, '_');
    const uniqueUnitName = `${id}_unit`;

    return `<div class="d-flex align-items-center mt-3 p-2 bg-light rounded ingredient-item justify-content-between flex-wrap" id="${id}_div">
            <input type="text" class="form-control form-control-sm me-2" style="width: 30%; flex-shrink: 0;" placeholder="재료명" name="ingredientName" value="${name}" readonly>

            <input type="number" class="form-control form-control-sm text-end me-2" style="width: 20%; flex-shrink: 0;" placeholder="수량" name="amount" min="0" step="0.1">

            <div class="d-flex unit-radio-group flex-wrap" style="width: 40%;">
                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_ml" value="ml" autocomplete="off" checked>
                <label class="btn btn-outline-secondary btn-sm me-1" style="height: 32px; width: 30px;" for="${id}_unit_ml">ml</label>

                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_ea" value="개" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm me-1" style="height: 32px; width: 30px;" for="${id}_unit_ea">개</label>

                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_sp" value="T" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm me-1 text-nowrap" style="height: 32px; width: 60px;" for="${id}_unit_sp">스푼(T)</label>

                <input type="radio" class="btn-check" name="${uniqueUnitName}" id="${id}_unit_g" value="g" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm text-nowrap" style="height: 32px; width: 60px;" for="${id}_unit_g">그램(g)</label>
            </div>
        </div>`
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

const collectIngredients = function() {
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

const collectSteps = function(formData) {
    const steps = [];
    const stepDivs = document.querySelectorAll('.recipe-step');

    stepDivs.forEach((div, index) => {
        const order = index + 1;
        const contentTextarea = div.querySelector('textarea');
        const imgUploadArea = div.querySelector('.img-upload-area');
        let fileInput = imgUploadArea?.querySelector('.img-file-upload');
        let imageFileName = null;

        if (fileInput && fileInput.files.length > 0) {
            const file = fileInput.files[0];
            const fieldName = `stepImage_${order}`;

            formData.append(fieldName, file);
            imageFileName = fieldName;
        }else if (imgUploadArea) {
            const originalUrl = imgUploadArea.getAttribute('data-original-url');
            const backgroundImage = imgUploadArea.style.backgroundImage;
            if (originalUrl && originalUrl.trim() !== '') {
                imageFileName = originalUrl;
            } else if (backgroundImage && backgroundImage !== 'none' && backgroundImage !== '') {
                imageFileName = 'EXISTING';
            }
        }
        steps.push({
            stepOrder: order,
            content: contentTextarea ? contentTextarea.value : '',
            imageUrl: imageFileName,
        });
    });

    return steps;
}

async function submitRecipeData(form, recipeNo) {
    const formData = new FormData();
    const recipeData = {};
    const errors = [];
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

const renderRecipeDetail = function(data) {
    document.getElementById('recipeNo').value = data.recipeNo;
    document.getElementById('recipeTitle').value = data.title;
    document.getElementById('recipeSummary').value = data.summary;
    document.getElementById('category').value = data.category;
    document.getElementById('prepTime').value = data.prepTime;
    document.getElementById('servings').value = data.servings;

    const thumbArea = document.getElementById('thumbnailArea');
    if (data.thumbnailUrl) {
        thumbArea.style.backgroundImage = `url(${data.thumbnailUrl})`;
        thumbArea.style.backgroundSize = 'cover';
        thumbArea.classList.add('has-image');
        thumbArea.setAttribute('data-original-url', data.thumbnailUrl);
        thumbArea.querySelector('i').style.display = 'none';
    }
    setupImageUploadListeners(thumbArea);

    const ingContainer = document.getElementById('ingredientContainer');
    data.ingredients.forEach(ing => {
        const ingHtml = addIngredientHtml(ing.ingredientName);
        ingContainer.insertAdjacentHTML('beforeend', ingHtml);

        const div = document.getElementById(`${ing.ingredientName.replace(/[^a-zA-Z0-9가-힣]/g, '_')}_div`);
        div.querySelector('input[name="amount"]').value = ing.amount;
        const unitRadio = div.querySelector(`input[value="${ing.unit}"]`);
        if (unitRadio) unitRadio.checked = true;

        addedIngredients.push(ing.ingredientName);
    });

    const spicyIcons = document.querySelectorAll('.spicy-level-icon');
    spicyIcons.forEach(icon => {
        if (parseInt(icon.dataset.level) === data.spicyLevel) {
            icon.classList.add('active');
        }
    });

    const diffRadio = document.querySelector(`input[name="difficulty"][value="${data.difficulty}"]`);
    if (diffRadio) diffRadio.checked = true;

    const stepContainer = document.getElementById('stepContainer');
    data.steps.forEach(step => {
        const stepDiv = document.createElement('div');
        stepDiv.className = 'recipe-step d-flex mb-3';
        stepDiv.innerHTML = `
            <div class="img-upload-area me-3 flex-shrink-0 ${step.imageUrl ? 'has-image' : ''}" 
                 data-original-url="${step.imageUrl || ''}"
                 style="${step.imageUrl ? `background-image: url(${step.imageUrl}); background-size: cover;` : ''}">
                <i class="bi bi-plus fs-1 text-secondary" style="${step.imageUrl ? 'display: none;' : ''}"></i>
                <input type="file" class="img-file-upload" accept="image/*" style="display: none;" />
            </div>
            <textarea class="form-control" rows="3" placeholder="레시피 설명">${step.content}</textarea>
        `;
        stepContainer.appendChild(stepDiv);
        setupImageUploadListeners(stepDiv.querySelector('.img-upload-area'));
    });
};

const fetchRecipeDetail = function(recipeNo){
    fetch(`/api/recipes/detail/${recipeNo}`)
        .then(response => {
            return response.json()
        }).then(data => {
        renderRecipeDetail(data.recipeDetail)
    }).catch(error => {
        console.error("레시피 데이터 로드 중 오류", errror);
    })

}
document.addEventListener('DOMContentLoaded',function (){
    const recipeNo = document.location.pathname.split("/").at(-1);

    fetchRecipeDetail(recipeNo);

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

    const editRecipe = document.querySelector('#editForm');
    const submitBtn = editRecipe.querySelector('button[type="submit"]');
    submitBtn.addEventListener('click', async function(e) {
        e.preventDefault();
        try {
            await submitRecipeData(editRecipe, recipeNo);
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
