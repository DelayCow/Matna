import {showAlertModal} from "./modal.js";

function getDifficultyText(difficulty) {
    if (difficulty === 'easy') return '쉬움';
    if (difficulty === 'normal') return '보통';
    return '어려움';
}

function getSpicyLevelText(level) {
    switch (level) {
        case 0: return '안매워요';
        case 1: return '약간매워요';
        case 2: return '신라면맵기';
        case 3: return '열라면맵기';
        case 4: return '불닭맵기';
        case 5: return '불닭보다매워요';
        default: return '';
    }
}

function generateIngredientRow(ing) {
    const gongguBadge = `<span class="badge badge-gonggu ${ing.isGroupBuying ? 'active' : 'inactive'}">
        ${ing.isGroupBuying ? '진행 중인 공구가 있어요!' : '진행 중인 공구가 없어요'}
    </span>`;

    const alternativesHtml = ing.alternatives && ing.alternatives.length > 0
        ? `<div class="col-6 ps-2 d-flex align-items-center">
            <i class="bi bi-arrow-right me-2 text-muted"></i>
            <div class="w-100">
                ${ing.alternatives.map(alt => `
                    <div class="d-flex justify-content-between flex-wrap mb-1">
                        <div class="d-flex flex-nowrap me-2">
                            <span class="fw-600 text-nowrap me-1">${alt.ingredientName}</span>
                            <span class="text-muted text-nowrap">${alt.amount}${alt.unit}</span>
                        </div>
                        <span class="badge badge-gonggu ${alt.isGroupBuying ? 'active' : 'inactive'}">
                            ${alt.isGroupBuying ? '진행 중인 공구가 있어요!' : '진행 중인 공구가 없어요'}
                        </span>
                    </div>
                `).join('')}
            </div>
        </div>`
        : '';

    return `
        <div class="d-flex ingredient-row">
            <div class="col-6 pe-2">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <div class="d-flex flex-nowrap me-2">
                        <span class="fw-600 text-nowrap me-1">${ing.ingredientName}</span>
                        <span class="text-muted text-nowrap">${ing.amount}${ing.unit}</span>
                    </div>
                    ${gongguBadge}
                </div>
            </div>
            ${alternativesHtml}
        </div>
    `;
}

function generateStepDiv(step) {
    return `
        <div class="d-flex mb-4">
            <div class="step-number">${step.stepOrder}.</div>
            <div class="d-flex flex-column flex-sm-row w-100">
                <img src="${step.imageUrl}" class="step-img me-3 mb-2 mb-sm-0" alt="Step ${step.stepOrder}">
                <p class="text-secondary m-0 lh-base">${step.content}</p>
            </div>
        </div>
    `;
}

function generateProgressBar(level, percentage) {
    const spicyLevelNames = ['0단계', '1단계', '2단계', '3단계', '4단계', '5단계'];
    return `
        <div class="progress-wrapper">
            <span class="fw-bold" style="width: 50px;">${spicyLevelNames[level]}</span>
            <div class="progress progress-custom">
                <div class="progress-bar progress-bar-custom" role="progressbar"
                     style="width:${percentage}%"></div>
            </div>
            <span class="text-muted" style="width: 30px; text-align: right;">${percentage}%</span>
        </div>
    `;
}

function renderReviewSection(reviews, rating, reviewCount, recipeNo) {
    const reviewSectionContainer = document.getElementById('review-section');
    let reviewHtml = '';

    const reviewHeader = `
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h6 class="fw-bold m-0">다른 회원의 후기 <span class="text-warning ms-1"><i class="bi bi-star-fill"></i> <span>${rating.toFixed(1)}</span></span> (${reviewCount})</h6>
            ${reviews && reviews.length > 0 ? `<a href="/review/recipe/${recipeNo}" class="text-dark text-decoration-none fw-bold">전체 보기 <i class="bi bi-chevron-right"></i></a>` : ''}
        </div>
    `;

    if (reviews && reviews.length > 0) {
        const thumbHtml = reviews.map(review => `
            <img src="${review.imageUrl}" alt="review${review.reviewNo}" class="review-thumb">
        `).join('');
        reviewHtml = reviewHeader + `<div class="d-flex gap-2" style="overflow: hidden;">${thumbHtml}</div>`;
    } else {
        reviewHtml = reviewHeader + `
            <div class="text-center text-muted py-4">
                <i class="bi bi-chat-square-text fs-1 d-block mb-2"></i>
                <p class="mb-0">아직 작성된 후기가 없습니다</p>
            </div>
        `;
    }
    reviewSectionContainer.innerHTML = reviewHtml;
}

const renderRecipeDetail = function (data){
    document.getElementById('recipe-thumbnail').src = data.thumbnailUrl;

    document.getElementById('writer-profile-img').src = data.writerProfile || '/img/user.png';
    document.getElementById('writer-nickname').innerText = data.writerNickname;
    document.getElementById('writer-profile').dataset.no = data.writerNo;

    document.getElementById('recipe-title').innerText = data.title;
    document.getElementById('recipe-rating').innerText = data.rating.toFixed(1);
    document.getElementById('recipe-review-count').innerText = data.reviewCount;
    document.getElementById('ingredient-review-count').innerText = data.reviewCount;
    document.getElementById('recipe-summary').innerText = data.summary;

    const inDate = new Date(data.inDate);
    document.getElementById('recipe-inDate').innerText = `${inDate.getFullYear()}.${String(inDate.getMonth() + 1).padStart(2, '0')}.${String(inDate.getDate()).padStart(2, '0')}`;

    const summaryInfoDiv = document.getElementById('summary-info');
    summaryInfoDiv.innerHTML = `
        <div class="flex-fill border-end"><i class="bi bi-person fs-5 d-block mb-1"></i> <span>${data.servings}</span>인분</div>
        <div class="flex-fill border-end"><i class="bi bi-clock fs-5 d-block mb-1"></i> <span>${data.prepTime}</span>분이내</div>
        <div class="flex-fill border-end"><i class="bi bi-star fs-5 d-block mb-1"></i> <span>${getDifficultyText(data.difficulty)}</span></div>
        <div class="flex-fill"><img src="/img/spicy.png" class="mx-auto d-block mt-1 mb-2"><span>${getSpicyLevelText(data.spicyLevel)}</span></div>
    `;

    const ingredientListContainer = document.getElementById('ingredient-list');
    ingredientListContainer.innerHTML = data.ingredients.map(ing => generateIngredientRow(ing)).join('');

    const stepsListContainer = document.getElementById('steps-list');
    stepsListContainer.innerHTML = data.steps.map(step => generateStepDiv(step)).join('');

    document.getElementById('spicy-review-count').innerText = data.reviewCount;
    const spicyProgressContainer = document.getElementById('spicy-level-progress');
    spicyProgressContainer.innerHTML = Object.entries(data.spicyLevelPercentages)
        .map(([level, percentage]) => generateProgressBar(level, percentage)).join('');

    renderReviewSection(data.reviews, data.rating, data.reviewCount, data.recipeNo);

    // 현재 로그인한 사용자 번호가 data.writerNo와 일치하는지 확인하여 수정/삭제 버튼을 표시하는 로직이 추가되어야 합니다.
}

const renderEditBox = function(recipeNo){
    const editBoxContainer = document.getElementById('edit-box');

    editBoxContainer.innerHTML = `<i class="bi bi-three-dots-vertical text-dark" id="edit-button"></i>
          <div class="edit-box text-center bg-white">
            <a href="/recipe/edit/${recipeNo}" class="mb-2">수정</a>
            <a class="mt-2" id="removeRecipe" data-id="${recipeNo}">삭제</a>
          </div>`;

    const editButton = editBoxContainer.querySelector('#edit-button');
    const editBoxMenu = editBoxContainer.querySelector('.edit-box');

    if(editButton && editBoxMenu){
        editButton.addEventListener('click', function(e){
            e.stopPropagation();
            editBoxMenu.classList.toggle('show');
        })
    }

    const remove = editBoxContainer.querySelector('#removeRecipe');
    if(remove){
        const targetRecipeNo = remove.getAttribute('data-id');
        remove.addEventListener('click', function(){
            editBoxMenu.classList.remove('show');
            showAlertModal(
                '레시피 삭제',
                '레시피를 삭제하시겠습니까?',
                'error',
                () => removeRecipe(targetRecipeNo)
            )
        })
    }
}
const fetchRecipeDetail = function(recipeNo){
    api.fetch(`/api/recipes/detail/${recipeNo}`)
        .then(response => {
            return response.json()
        }).then(data => {
        if(data.currentMemberNo === data.recipeDetail.writerNo){
            renderEditBox(recipeNo)
        }
        renderRecipeDetail(data.recipeDetail)
    }).catch(error => {
        console.error("레시피 데이터 로드 중 오류", errror);
    })

}
const removeRecipe = async function(recipeNo){
    try{
        const response = await api.fetch(`/api/recipes/${recipeNo}`,{
            method: 'DELETE'
        });

        if(response.ok) {
            showAlertModal(
                '삭제 완료',
                '레시피가 성공적으로 삭제되었습니다!',
                'success',
                () => {
                    window.location.href = '/recipe';
                }
            );
        }else{
            const errorData = await response.json();
            const errorMessage = errorData.message || '서버 오류가 발생했습니다.';

            showAlertModal(
                '삭제 실패',
                `레시피 삭제에 실패했습니다.<br><br><small class="text-muted">${errorMessage}</small>`,
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

document.addEventListener('DOMContentLoaded', function(){
    const recipeNo = document.location.pathname.split("/").at(-1)
    fetchRecipeDetail(recipeNo);

    document.getElementById('writer-profile').addEventListener('click', function (){
        const writerNo = this.getAttribute('data-no');
        location.href=`/mypage/${writerNo}`;
    })

    document.getElementById('addReviewBtn').addEventListener('click', function (){
        location.href = `/review/add/${recipeNo}`
    })

    document.addEventListener('click', function() {
        const activeEditBox = document.querySelector('.edit-box.show');
        if (activeEditBox) {
            activeEditBox.classList.remove('show');
        }
    });
})