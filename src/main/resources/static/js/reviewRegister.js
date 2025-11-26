    let ingredientCount = 0;
    const useOtherIngredientsCheckbox = document.getElementById('useOtherIngredients');

    function initializeUnitRadios(formElement) {
    const radios = formElement.querySelectorAll('input[type="radio"]');

    // 1. 초기 스타일 설정
    radios.forEach(radio => {
    const label = formElement.querySelector(`label[for="${radio.id}"]`);
    label.classList.remove('btn-secondary');
    label.classList.add('btn-outline-secondary');

    if (radio.checked) {
    label.classList.remove('btn-outline-secondary');
    label.classList.add('btn-secondary');
}
});

    // 2. 변경 이벤트 리스너 추가 (스타일 토글)
    radios.forEach(radio => {
    radio.addEventListener('change', function() {
    formElement.querySelectorAll(`input[name="${this.name}"]`).forEach(innerRadio => {
    const label = formElement.querySelector(`label[for="${innerRadio.id}"]`);
    if (innerRadio.checked) {
    label.classList.remove('btn-outline-secondary');
    label.classList.add('btn-secondary');
} else {
    label.classList.remove('btn-secondary');
    label.classList.add('btn-outline-secondary');
}
});
});
});
}

    function createIngredientFormHtml(ingredientName, defaultCheckedId = 'ml') {
    const namePrefix = `unit_${ingredientName.replace(/\s/g, '_')}`; // 공백 제거 및 name prefix 생성

    return `
            <div class="ingredient-input-form mt-2">
                <div class="d-flex align-items-center p-2 bg-light rounded">

                    <input type="text" class="form-control form-control-sm me-2" style="width: 35%;" placeholder="대체재료" data-unit="대체재료">
                    <div class="d-flex unit-radio-group" style="width: 65%;">
                    <input type="text" class="form-control form-control-sm me-2" style="width: 35%;" placeholder="수량" data-unit="수량">
                    <div class="d-flex unit-radio-group" style="width: 65%;">
                        <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ml" value="ml" autocomplete="off" ${defaultCheckedId === 'ml' ? 'checked' : ''}>
                        <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ml">ml</label>

                        <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ea" value="개" autocomplete="off" ${defaultCheckedId === 'ea' ? 'checked' : ''}>
                        <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ea">개</label>

                        <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_sp" value="스푼(T)" autocomplete="off" ${defaultCheckedId === 'sp' ? 'checked' : ''}>
                        <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_sp">스푼(T)</label>

                        <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_g" value="g" autocomplete="off" ${defaultCheckedId === 'g' ? 'checked' : ''}>
                        <label class="btn btn-outline-secondary btn-sm" for="${namePrefix}_g">그램(g)</label>
                    </div>
                </div>
            </div>
        `;
}

    // 등록된 재료 (화살표 클릭 시 폼 동적 생성/토글)
    document.querySelectorAll('.toggle-input-btn').forEach(button => {
    button.addEventListener('click', function() {
        if (!useOtherIngredientsCheckbox.checked) {
            alert("사용 재료를 수정하려면 '다른 재료를 사용했어요'를 체크해주세요.");
            return;
        }

        const wrapper = this.closest('.ingredient-item-wrapper');
        const ingredientName = wrapper.querySelector('.ingredient-name').textContent.trim();
        const container = wrapper.querySelector('.ingredient-input-container');
        const arrowIcon = this.querySelector('i');

        let form = container.querySelector('.ingredient-input-form');

        if (!form) {
            container.innerHTML = createIngredientFormHtml(ingredientName, 'ml');
            form = container.querySelector('.ingredient-input-form');

            const unitGroupDiv = form.querySelector('.d-flex.align-items-center');
            if (unitGroupDiv) {
                initializeUnitRadios(unitGroupDiv);
            }
        }

        if (form.style.display === 'none' || !form.style.display) {
            form.style.display = 'block';
            arrowIcon.classList.remove('bi-arrow-right');
            arrowIcon.classList.add('bi-arrow-down');
        } else {
            form.style.display = 'none';
            arrowIcon.classList.remove('bi-arrow-down');
            arrowIcon.classList.add('bi-arrow-right');
        }
    });
});


    // 기타 재료 추가
    const addOtherIngredientBtn = document.getElementById('addOtherIngredientBtn');
    const otherItemNameInput = document.getElementById('otherItemName');
    const addedIngredientsList = document.getElementById('added-ingredients-list');

    addOtherIngredientBtn.addEventListener('click', function() {
    const name = otherItemNameInput.value.trim();
    if (name) {
    ingredientCount++;

    const newItem = document.createElement('div');
    newItem.className = 'd-flex align-items-center mt-3 p-2 bg-light rounded added-ingredient-item';

    const namePrefix = `unit_other_${ingredientCount}`;

    newItem.innerHTML = `
                <input type="text" class="form-control form-control-sm me-2" style="width: 30%; flex-shrink: 0;" value="${name}" readonly>
                <input type="text" class="form-control form-control-sm text-end me-2" style="width: 20%; flex-shrink: 0;" placeholder="수량">

                <div class="d-flex unit-radio-group" style="width: 40%; flex-shrink: 0;">
                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ml" value="ml" autocomplete="off" checked>
                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ml">ml</label>

                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_ea" value="개" autocomplete="off">
                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_ea">개</label>

                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_sp" value="스푼(T)" autocomplete="off">
                    <label class="btn btn-outline-secondary btn-sm me-1" for="${namePrefix}_sp">스푼(T)</label>

                    <input type="radio" class="btn-check" name="${namePrefix}" id="${namePrefix}_g" value="g" autocomplete="off">
                    <label class="btn btn-outline-secondary btn-sm" for="${namePrefix}_g">그램(g)</label>
                </div>

                <button class="btn btn-danger ms-4 flex-shrink-0 delete-item-btn" type="button" style="height: 32px; width: 50px; padding: 0;">
                    삭제
                </button>
            `;

    addedIngredientsList.appendChild(newItem);
    otherItemNameInput.value = '';

    newItem.querySelector('.delete-item-btn').addEventListener('click', function() {
    newItem.remove();
});

    initializeUnitRadios(newItem);
}
});


    // 별점 기능
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


    // 사진 첨부 (미리보기 기능)
    const fileUpload = document.getElementById('file-upload');
    const photoPlaceholder = document.querySelector('.photo-placeholder');

    fileUpload.addEventListener('change', function(event) {
    if (event.target.files.length > 0) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function(e) {
    photoPlaceholder.style.backgroundImage = `url(${e.target.result})`;
    photoPlaceholder.innerHTML = ''; // + 아이콘 제거
    photoPlaceholder.classList.add('has-image');
}
    reader.readAsDataURL(file);
} else {
    photoPlaceholder.style.backgroundImage = 'none';
    photoPlaceholder.innerHTML = '<i class="bi bi-plus fs-1 text-secondary"></i>';
    photoPlaceholder.classList.remove('has-image');
}
});


    // 맵기 정도 (라디오 버튼 스타일)
    document.querySelectorAll('.spicy-btn').forEach(label => {
    const inputId = label.getAttribute('for');
    const input = document.getElementById(inputId);

    if(input.checked) {
    label.classList.remove('btn-outline-secondary', 'btn-outline-danger');
    label.classList.add(input.id === 'spicy0' ? 'btn-secondary' : 'btn-danger');
}

    input.addEventListener('change', function() {
    document.querySelectorAll('.spicy-btn').forEach(l => {
    l.classList.remove('btn-secondary', 'btn-danger');
    l.classList.add(l.getAttribute('for') === 'spicy0' ? 'btn-outline-secondary' : 'btn-outline-danger');
});

    if (this.checked) {
    this.nextElementSibling.classList.remove('btn-outline-secondary', 'btn-outline-danger');
    this.nextElementSibling.classList.add(this.id === 'spicy0' ? 'btn-secondary' : 'btn-danger');
}
});
});