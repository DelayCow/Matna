import {initializeSpicyIcons} from "./spicyFilter.js";
import {showAlertModal, showValidationModal} from "./modal.js";
import {fetchRecipeData} from "./reviewUtil.js"
document.addEventListener('DOMContentLoaded', function (){
    const recipeNo = window.location.pathname.split('/').at(-1)
    initializeSpicyIcons();
    fetchRecipeData(recipeNo);

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

    document.getElementById('handleAddReview').addEventListener('click', function() {
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

        if(photoFile == null){
            errors.push('리뷰 사진은 필수입니다');
        }

        if (errors.length > 0) {
            showValidationModal(errors);
            return;
        }

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
        // 8. 서버로 전송
        api.fetch(`/api/reviews/${recipeNo}`, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    showAlertModal(
                        '등록 실패',
                        `리뷰 등록에 실패했습니다.`,
                        'error'
                    );
                    return;
                }
                return response.json();
            })
            .then(data => {
                if(data){
                    showAlertModal(
                        '등록 완료',
                        '후기가 성공적으로 등록되었습니다!',
                        'success',
                        () => {
                            window.location.href = '/recipe';
                        }
                    );
                }
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