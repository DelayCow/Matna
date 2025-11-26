
// 이미지 업로드
function handleImageUpload(input, target) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();

        reader.onload = function(e) {
            target.style.backgroundImage = `url(${e.target.result})`;
            target.style.backgroundSize = 'cover';
            target.style.backgroundPosition = 'center';
            target.style.backgroundRepeat = 'no-repeat';
            // 기존의 + 아이콘 제거
            target.innerHTML = '';
            target.classList.add('has-image');
        };
        reader.readAsDataURL(file);
    } else {
        // 파일 선택 취소 시 초기화
        target.style.backgroundImage = 'none';
        target.innerHTML = '<i class="bi bi-plus fs-1 text-secondary"></i>';
        target.classList.remove('has-image');
    }
}

function setupImageUploadListeners(container) {
    const fileInput = container.querySelector('.img-file-upload');

    if (fileInput) {
        // 1. 파일을 선택하면 미리보기 함수 실행
        fileInput.addEventListener('change', function() {
            handleImageUpload(this, container);
        });

        // 2. 컨테이너 클릭 시 파일 입력창 열기
        container.addEventListener('click', function() {
            fileInput.click();
        });
    }
}

// 레시피 단계 추가 로직
document.getElementById('addStepBtn').addEventListener('click', function () {
    const parentDiv = this.parentElement;

    const newStep = document.createElement('div');
    newStep.className = 'recipe-step d-flex mb-3';
    newStep.innerHTML = `
        <div class="img-upload-area me-3 flex-shrink-0">
            <i class="bi bi-plus fs-1 text-secondary"></i>
            <input type="file" class="img-file-upload" accept="image/*" style="display: none;"> </div>
        <textarea class="form-control" rows="3" placeholder="레시피 설명"></textarea>
    `;
    parentDiv.before(newStep);

    // 새로 생성된 레시피단계의 이미지 업로드 리스너 설정
    const newStepImageContainer = newStep.querySelector('.img-upload-area');
    if (newStepImageContainer) {
        setupImageUploadListeners(newStepImageContainer);
    }
});


// 페이지 로드 시 초기 리스너 설정
document.addEventListener('DOMContentLoaded', function() {
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
});




const searchInput = document.getElementById('itemSelect');
const itemMenu = document.getElementById('itemDropdownMenu');

let isItemClicked = false;

function debounce(func, delay) {
    let timeoutId;
    return function (...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            func.apply(this, args);
        }, delay);
    };
}

// 품목 선택 검색 및 드롭다운 로직
searchInput.addEventListener('input', debounce(function () {
    if (isItemClicked) {
        isItemClicked = false;
        return;
    }
    const query = this.value.trim();
    if (query.length > 0) {
        fetchSearchResults(query);
    } else {
        itemMenu.classList.remove('show');
    }
}, 300));

function fetchSearchResults(query) {
    const mockData = [
        {"id": 1, "name": "밀떡"},
        {"id": 2, "name": "고추장"},
        {"id": 3, "name": "설탕"},
        {"id": 4, "name": "어묵"},
        {"id": 5, "name": "어간장"},
        {"id": 6, "name": "양파"},
        {"id": 7, "name": "쌀떡"}
    ];
    const filteredData = mockData.filter(item => item.name.includes(query));
    updateDropdownMenu(filteredData);
}

const ingredientContainer = document.getElementById('ingredientContainer');
const other = document.getElementById('otherItem');
document.getElementById('addOtherItemBtn').addEventListener('click', (e) => addIngredient(e, other.value))
let addedIngredients = [];

function updateDropdownMenu(results) {
    itemMenu.innerHTML = '';

    if (results && results.length > 0) {
        results.forEach(item => {
            const a = document.createElement('a');
            a.classList.add('dropdown-item');
            a.href = '#';
            a.textContent = item.name;

            a.addEventListener('click', (e) => addIngredient(e, item.name));

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

        // ID (예: "밀떡_div")에서 재료 이름 (예: "밀떡") 추출
        const ingredientName = id.replace('_div', '');

        // addedIngredients 배열에서 해당 재료 이름 제거
        const index = addedIngredients.indexOf(ingredientName);
        if (index > -1) {
            addedIngredients.splice(index, 1);
        }
    }
}
const addIngredientHtml = function (name) {
    //나중에 ingredientNo받아서 이거 기준으로 구분할 수 있게 하기
    return `<div class="d-flex align-items-center mt-3 p-2 bg-light rounded" id="${name}_div">
            <input type="text" class="form-control form-control-sm me-2" style="width: 30%; flex-shrink: 0;" placeholder="재료명" value="${name}" readonly>

            <input type="text" class="form-control form-control-sm text-end me-2" style="width: 20%; flex-shrink: 0;" placeholder="수량">

            <div class="d-flex unit-radio-group" style="width: 40%; flex-shrink: 0;">
                <input type="radio" class="btn-check" name="${name}_unit" id="${name}_unit_ml" value="ml" autocomplete="off" checked>
                <label class="btn btn-outline-secondary btn-sm me-1" for="${name}_unit_ml">ml</label>

                <input type="radio" class="btn-check" name="${name}_unit" id="${name}_unit_ea" value="개" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm me-1" for="${name}_unit_ea">개</label>

                <input type="radio" class="btn-check" name="${name}_unit" id="${name}_unit_sp" value="스푼(T)" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm me-1" for="${name}_unit_sp">스푼(T)</label>

                <input type="radio" class="btn-check" name="${name}_unit" id="${name}_unit_g" value="g" autocomplete="off">
                <label class="btn btn-outline-secondary btn-sm" for="${name}_unit_g">그램(g)</label>
            </div>

            <button class="btn btn-danger ms-4 flex-shrink-0" type="button" style="height: 32px; width: 50px; padding: 0;" onclick="deleteIngredient('${name}_div')">
                삭제
            </button>
        </div>`
}
const addIngredient = function (e, name) {
    e.preventDefault();
    if (name.length === 0) {
        alert('재료 이름을 입력해야 추가할 수 있습니다.'); //임시로 넣음
        return;
    }
    isItemClicked = true;
    if (addedIngredients.includes(name)) {
        alert(`${name}은(는) 이미 추가된 재료입니다.`); //임시로 넣음
    } else {
        ingredientContainer.insertAdjacentHTML('beforeend', addIngredientHtml(name));
        addedIngredients.push(name);
    }
    searchInput.value = '';
    other.value = '';
    itemMenu.classList.remove('show');
    searchInput.blur();
}



