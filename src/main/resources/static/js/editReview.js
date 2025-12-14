import {initializeSpicyIcons} from "./spicyFilter.js";
import {fetchRecipeData} from "./reviewUtil.js"
import {showAlertModal, showValidationModal} from "./modal.js";

let recipeNo = 0;
const reviewNo = window.location.pathname.split("/").at(-2);
const fetchReviewData = async function(reviewNo){
    try{
        const response = await fetch(`/api/reviews/${reviewNo}`)
        const reviewData = await response.json();
        console.log(reviewData)
        await fetchRecipeData(reviewData.recipeNo);
        recipeNo = reviewData.recipeNo;
        await insertReviewData(reviewData);
    }catch(error){
        console.error('리뷰 데이터 가져오는 중 오류 발생: ', error);
    }
}
const insertReviewData = function (data){
    document.querySelector('input[placeholder="제목"]').value = data.title;
    document.querySelector('textarea[placeholder="후기 내용을 입력해주세요"]').value = data.content;

    const stars = document.querySelectorAll('.star-rating i');
    stars.forEach((s, index) => {
        const rating = data.rating;
        if (index < rating) {
            s.classList.remove('bi-star');
            s.classList.add('bi-star-fill');
        } else {
            s.classList.remove('bi-star-fill');
            s.classList.add('bi-star');
        }
    });

    const spicyLevel = data.spicyLevel;
    document.querySelectorAll('.spicy-level-icon').forEach(icon => {
        icon.classList.remove('active');
        if (parseInt(icon.dataset.level) === spicyLevel) {
            icon.classList.add('active');
        }
    });

    const photoPlaceholder = document.querySelector('.photo-placeholder');
    if (data.reviewImage) {
        photoPlaceholder.style.backgroundImage = `url(${data.reviewImage})`;
        photoPlaceholder.innerHTML = '';
        photoPlaceholder.classList.add('has-image');
    }

    if(data.alternatives && data.alternatives.length > 0){
        const useOtherIngredientsCheckbox = document.getElementById('useOtherIngredients');
        const ingredientList = document.querySelector('.ingredient-list');

        // 체크박스 체크
        useOtherIngredientsCheckbox.checked = true;
        ingredientList.classList.add('active');

        data.alternatives.forEach(alt => {
            const wrapper = document.querySelector(`[data-ingredient-name="${alt.originalIngredientName}"]`);
            if (wrapper) {
                const inputContainer = wrapper.querySelector('.ingredient-input-container');
                const searchInput = wrapper.querySelector('.form-control');

                // 대체 재료 폼 생성
                const namePrefix = `unit_${alt.originalIngredientName.replace(/\s/g, '_')}_${Date.now()}`;
                inputContainer.innerHTML = `
                        <div class="ingredient-input-form mt-2">
                            <div class="d-flex align-items-center p-2 bg-light rounded">
                                <input type="text" class="form-control form-control-sm me-2 substitute-name" style="width: 35%;" placeholder="대체재료" readonly value="${alt.alternativeIngredientName}">
                                <input type="text" class="form-control form-control-sm me-2" style="width: 20%;" placeholder="수량" value="${alt.amount}">
                                <div class="d-flex unit-radio-group" style="width: 45%;">
                                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ml" value="ml" autocomplete="off" ${alt.unit === 'ml' ? 'checked' : ''}>
                                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ml">ml</label>

                                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ea" value="개" autocomplete="off" ${alt.unit === '개' ? 'checked' : ''}>
                                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ea">개</label>

                                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_sp" value="스푼(T)" autocomplete="off" ${alt.unit === '스푼(T)' ? 'checked' : ''}>
                                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_sp">스푼(T)</label>

                                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_g" value="g" autocomplete="off" ${alt.unit === 'g' ? 'checked' : ''}>
                                    <label class="btn btn-outline-secondary btn-sm" for="${namePrefix}_g">그램(g)</label>
                                </div>
                            </div>
                        </div>
                    `;

                // 검색 인풋 숨기기
                if (searchInput && searchInput.parentElement) {
                    searchInput.parentElement.style.display = 'none';
                }

                // wrapper에 표시 상태 마킹
                wrapper.dataset.formVisible = 'true';
            }
        });
    }

}

document.addEventListener('DOMContentLoaded',function (){
    initializeSpicyIcons();
    fetchReviewData(reviewNo);

    const ingredientList = document.querySelector('.ingredient-list');
    const useOtherIngredientsCheckbox = document.getElementById('useOtherIngredients');

    useOtherIngredientsCheckbox.addEventListener('change', function() {
        if (this.checked) {
            ingredientList.classList.add('active');
        } else {
            ingredientList.classList.remove('active');
        }
    });

    const stars = document.querySelectorAll('.star-rating i');
    stars.forEach(star => {
        star.addEventListener('click', function() {
            const rating = parseInt(this.getAttribute('data-rating'));

            stars.forEach((s, index) => {
                if (index < rating) {
                    s.classList.remove('bi-star');
                    s.classList.add('bi-star-fill');
                } else {
                    s.classList.remove('bi-star-fill');
                    s.classList.add('bi-star');
                }
            });
        });
    });

    const fileUpload = document.getElementById('file-upload');
    const photoPlaceholder = document.querySelector('.photo-placeholder');
    fileUpload.addEventListener('change', function(event) {
        if (event.target.files.length > 0) {
            const file = event.target.files[0];
            const reader = new FileReader();

            reader.onload = function(e) {
                photoPlaceholder.style.backgroundImage = `url(${e.target.result})`;
                photoPlaceholder.innerHTML = '';
                photoPlaceholder.classList.add('has-image');
            }
            reader.readAsDataURL(file);
        } else {
            photoPlaceholder.style.backgroundImage = 'none';
            photoPlaceholder.innerHTML = '<i class="bi bi-plus fs-1 text-secondary"></i>';
            photoPlaceholder.classList.remove('has-image');
        }
    });
    document.getElementById('handleEditReview').addEventListener('click', function() {
        const formData = new FormData();
        const reviewData = {};
        const errors = [];

        const title = document.querySelector('input[placeholder="제목"]').value.trim();
        const content = document.querySelector('textarea[placeholder="후기 내용을 입력해주세요"]').value.trim();

        const filledStars = document.querySelectorAll('.star-rating .bi-star-fill').length;
        const rating = filledStars;

        const selectedSpicy = document.querySelector('.spicy-level-icon.active');
        const spicyLevel = selectedSpicy ? parseInt(selectedSpicy.dataset.level) : null;

        const ingredients = [];

        document.querySelectorAll('.ingredient-item-wrapper').forEach(wrapper => {
            const originalName = wrapper.dataset.ingredientName;
            const inputForm = wrapper.querySelector('.ingredient-input-form');

            if (inputForm) {
                const substituteName = inputForm.querySelector('.substitute-name').value;
                const amount = inputForm.querySelector('input[placeholder="수량"]').value;
                const unitRadio = inputForm.querySelector('input[type="radio"]:checked');
                const unit = unitRadio ? unitRadio.value : '';

                ingredients.push({
                    originalIngredientName: originalName,
                    alternativeIngredientName: substituteName,
                    amount: amount,
                    unit: unit,
                });
            }
        });

        const photoFile = fileUpload.files[0];
        const hasExistingImage = photoPlaceholder.classList.contains('has-image');

        if (!title) {
            errors.push('제목을 입력해주세요.');
        }

        if (rating === 0) {
            errors.push('별점을 선택해주세요.');
        }

        if (spicyLevel === null) {
            errors.push('맵기를 선택해주세요.');
        }

        if (!content) {
            errors.push('후기 내용을 입력해주세요.');
        }

        if(photoFile == null && !hasExistingImage){
            errors.push('리뷰 사진은 필수입니다');
        }

        if (errors.length > 0) {
            showValidationModal(errors);
            return;
        }

        reviewData.reviewNo = reviewNo;
        reviewData.recipeNo = recipeNo;
        reviewData.title = title;
        reviewData.content =  content;
        reviewData.rating = rating;
        reviewData.spicyLevel = spicyLevel;
        reviewData.alternatives = ingredients;

        const reviewJsonString = JSON.stringify(reviewData);

        formData.append('reviewRequest', reviewJsonString)

        if (photoFile) {
            formData.append('reviewImage', photoFile);
        }

        for (let [key, value] of formData.entries()) {
            if (value instanceof File) {
                console.log(key, ':', value.name, '(File)');
            } else {
                console.log(key, ':', value);
            }
        }

        fetch(`/api/reviews/${recipeNo}`, {
            method: 'PUT',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    showAlertModal(
                        '수정 실패',
                        `리뷰 수정에 실패했습니다.`,
                        'error'
                    );
                }
                return response.json();
            })
            .then(data => {
                showAlertModal(
                    '수정 완료',
                    '후기가 성공적으로 수정되었습니다!',
                    'success',
                    () => {
                        window.location.href = `/review/detail/${reviewNo}`;
                    }
                );
            })
            .catch(error => {
                console.error('Error:', error);
                showAlertModal(
                    '네트워크 오류',
                    '서버와 통신할 수 없습니다.<br>잠시 후 다시 시도해주세요.',
                    'error'
                );
            });
    });
})