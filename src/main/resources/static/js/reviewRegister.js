import {initializeSpicyIcons} from "./spicyFilter.js";
import {debounce, fetchSearchResults} from "./searchIngredient.js";
import {showAlertModal, showValidationModal} from "./modal.js";

document.addEventListener('DOMContentLoaded', function (){
    initializeSpicyIcons();
    const ingredientList = document.querySelector('.ingredient-list');
    const useOtherIngredientsCheckbox = document.getElementById('useOtherIngredients');

    useOtherIngredientsCheckbox.addEventListener('change', function() {
        if (this.checked) {
            ingredientList.classList.add('active');
        } else {
            ingredientList.classList.remove('active');
        }
    });

    function createIngredientFormHtml(ingredientName) {
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

    function updateDropdownMenu(results, dropdownMenu, searchInput, inputContainer, originalIngredientName) {
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

        const recipeNo = document.querySelector('#recipeNo').value;
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
        fetch(`/api/reviews/${recipeNo}`, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    const errorData = response.json();
                    const errorMessage = errorData.message || '서버 오류가 발생했습니다.';

                    showAlertModal(
                        '등록 실패',
                        `리뷰 등록에 실패했습니다.`,
                        'error'
                    );
                }
                return response.json();
            })
            .then(data => {
                showAlertModal(
                    '등록 완료',
                    '후기가 성공적으로 등록되었습니다!',
                    'success',
                    () => {
                        window.location.href = '/recipe';
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