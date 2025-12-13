document.addEventListener('DOMContentLoaded', function() {
    // ========== 전역 변수 ==========
    const countRadio = document.getElementById('count');
    const weightRadio = document.getElementById('weight');
    const unitLabels = document.querySelectorAll('.unit-label');
    const daySelects = document.querySelectorAll('.day-select');
    const hourSelects = document.querySelectorAll('.time-select');

    const searchInput = document.getElementById('itemSelect');
    const itemMenu = document.getElementById('itemDropdownMenu');
    const ingredientNoInput = document.getElementById('ingredientNo');

    const attachPhotoButton = document.getElementById('attachPhotoButton');
    const fileInput = document.getElementById('fileInput');
    const previewContainer = document.getElementById('previewContainer');

    const calculateButton = document.getElementById('calculateButton');
    const pricePerUnitDisplay = document.getElementById('price_per_unit');

    const submitBtn = document.querySelector('.btn-post');

    let isItemClicked = false;
    let selectedIngredientNo = null;
    const creatorNo = document.getElementById('creatorNo').textContent;

    // ========== 초기화 ==========
    init();

    function init() {
        createDayOptions(daySelects, 7, 1);
        createTimeOptions(hourSelects);
        updateUnits();
        setupEventListeners();
    }

    // ========== 단위 설정 ==========
    function updateUnits() {
        let unitText = weightRadio.checked ? 'g' : '개';
        unitLabels.forEach(span => span.textContent = unitText);
    }

    // ========== 일/시간 옵션 생성 ==========
    function createDayOptions(selectElements, maxDays, selectedDay = 1) {
        selectElements.forEach(selectElement => {
            selectElement.innerHTML = '';
            for (let i = 1; i <= maxDays; i++) {
                const option = document.createElement('option');
                option.value = i;
                option.textContent = i;
                if (i === selectedDay) {
                    option.selected = true;
                }
                selectElement.appendChild(option);
            }
        });
    }

    function createTimeOptions(selectElements) {
        selectElements.forEach(selectElement => {
            selectElement.innerHTML = '';
            let defaultSelected = false;

            for (let h = 0; h < 24; h++) {
                for (let m = 0; m < 60; m += 30) {
                    const hour = String(h).padStart(2, '0');
                    const minute = String(m).padStart(2, '0');
                    const timeValue = `${hour}:${minute}`;

                    const option = document.createElement('option');
                    option.value = timeValue;
                    option.textContent = timeValue;

                    if (timeValue === '00:00' && !defaultSelected) {
                        option.selected = true;
                        defaultSelected = true;
                    }

                    selectElement.appendChild(option);
                }
            }
        });
    }

    // ========== 재료 검색 ==========
    function debounce(func, delay) {
        let timeoutId;
        return function(...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => {
                func.apply(this, args);
            }, delay);
        };
    }

    function fetchSearchResults(query) {
        fetch(`/api/ingredients/search?keyword=${encodeURIComponent(query)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('재료 검색에 실패했습니다.');
                }
                return response.json();
            })
            .then(data => {
                updateDropdownMenu(data);
            })
            .catch(error => {
                console.error('Error:', error);
                itemMenu.classList.remove('show');
            });
    }

    function updateDropdownMenu(results) {
        itemMenu.innerHTML = '';

        if (results && results.length > 0) {
            results.forEach(item => {
                const a = document.createElement('a');
                a.classList.add('dropdown-item');
                a.href = '#';
                a.textContent = item.ingredientName;

                a.addEventListener('click', function(e) {
                    e.preventDefault();
                    isItemClicked = true;
                    searchInput.value = item.ingredientName;
                    ingredientNoInput.value = item.ingredientNo;
                    selectedIngredientNo = item.ingredientNo;
                    itemMenu.classList.remove('show');
                    searchInput.blur();
                });

                itemMenu.appendChild(a);
            });
            itemMenu.classList.add('show');
        } else {
            itemMenu.classList.remove('show');
        }
    }

    // ========== 가격 계산 ==========
    function calculatePricePerUnit() {
        const price = parseFloat(document.getElementById('price').value) || 0;
        const quantity = parseFloat(document.getElementById('quantity').value) || 0;
        const feeRate = parseFloat(document.getElementById('fee_rate').value) || 0;
        const shareAmount = parseFloat(document.getElementById('share_amount').value) || 0;

        if (price <= 0 || quantity <= 0 || shareAmount <= 0) {
            alert('상품 가격, 상품 이량, 나눔 단위를 모두 입력해주세요.');
            return;
        }

        // 총 가격 (수수료 포함)
        const totalWithFee = price * (1 + feeRate / 100);

        // 나눔 단위당 가격: (총 가격 * (1 + 수수료율/100)) / (총 수량 / 나눔 단위)
        const shareUnits = quantity / shareAmount;
        const pricePerUnit = Math.round(totalWithFee / shareUnits);

        pricePerUnitDisplay.textContent = pricePerUnit.toLocaleString();
    }

    // ========== 파일 업로드 ==========
    function removeFileAndReset() {
        fileInput.value = '';
        previewContainer.innerHTML = '';

        attachPhotoButton.textContent = '사진 첨부하기';
        attachPhotoButton.classList.remove('btn-success');
        attachPhotoButton.classList.add('btn-light');

        attachPhotoButton.disabled = false;
    }

    function handleFileChange(e) {
        const files = e.target.files;
        previewContainer.innerHTML = '';

        if (files.length > 0) {
            const file = files[0];

            attachPhotoButton.textContent = `사진 1개 첨부됨`;
            attachPhotoButton.classList.remove('btn-light');
            attachPhotoButton.classList.add('btn-success');
            attachPhotoButton.disabled = true;

            if (file.type.startsWith('image/')) {
                const reader = new FileReader();

                reader.onload = function(e) {
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    img.alt = file.name;

                    const wrapper = document.createElement('div');
                    wrapper.classList.add('preview-wrapper');

                    const deleteBtn = document.createElement('span');
                    deleteBtn.classList.add('delete-btn');
                    deleteBtn.textContent = 'X';
                    deleteBtn.addEventListener('click', removeFileAndReset);

                    wrapper.appendChild(img);
                    wrapper.appendChild(deleteBtn);
                    previewContainer.appendChild(wrapper);
                };

                reader.readAsDataURL(file);
            }
        } else {
            removeFileAndReset();
        }
    }

    // ========== 폼 제출 ==========
    async function submitForm() {
        const formData = new FormData();
        const quantityData = {};
        const errors = [];

        // 썸네일 이미지 검증 (필수)
        if (fileInput.files.length === 0) {
            errors.push('상품 이미지를 등록해주세요');
        } else {
            formData.append('thumbnailFile', fileInput.files[0]);
        }

        // 제목 검증
        const title = document.getElementById('title').value.trim();
        if (!title) {
            errors.push('제목을 입력해주세요');
        }
        quantityData.title = title;

        // 재료 검증
        const ingredientNo = selectedIngredientNo || ingredientNoInput.value;
        if (!ingredientNo) {
            errors.push('품목을 선택해주세요');
        }
        quantityData.ingredientNo = parseInt(ingredientNo);

        // 작성자
        quantityData.creatorNo = parseInt(creatorNo);

        // 구매 기간
        const buyEndDate = parseInt(document.getElementById('deadline').value);
        if (!buyEndDate || buyEndDate <= 0) {
            errors.push('구매 기간을 선택해주세요');
        }
        quantityData.buyEndDate = buyEndDate;

        // 나눔 기간
        const shareEndDate = parseInt(document.getElementById('sharingTimeWeek').value);
        if (!shareEndDate || shareEndDate <= 0) {
            errors.push('나눔 기간을 선택해주세요');
        }
        quantityData.shareEndDate = shareEndDate;

        const shareTime = document.getElementById('sharingTimeHour').value;
        quantityData.shareTime = shareTime;

        // 나눔 장소
        const shareLocation = document.getElementById('addressSearch').value.trim();
        if (!shareLocation) {
            errors.push('나눔 장소를 입력해주세요');
        }
        quantityData.shareLocation = shareLocation;

        const shareDetailAddress = document.getElementById('detailAddress').value.trim();
        quantityData.shareDetailAddress = shareDetailAddress;

        // 상품 가격
        const price = parseInt(document.getElementById('price').value);
        if (!price || price <= 0) {
            errors.push('상품 가격을 입력해주세요');
        }
        quantityData.price = price;

        // 상품 이량
        const quantity = parseInt(document.getElementById('quantity').value);
        if (!quantity || quantity <= 0) {
            errors.push('상품 이량을 입력해주세요');
        }
        quantityData.quantity = quantity;

        // 내가 쓸 양
        const myQuantity = parseInt(document.getElementById('myQuantity').value);
        if (!myQuantity || myQuantity <= 0) {
            errors.push('내가 쓸 양을 입력해주세요');
        }
        quantityData.myQuantity = myQuantity;

        // 나눔 단위
        const shareAmount = parseInt(document.getElementById('share_amount').value);
        if (!shareAmount || shareAmount <= 0) {
            errors.push('나눔 단위를 입력해주세요');
        }
        quantityData.shareAmount = shareAmount;

        // 단위
        const unit = weightRadio.checked ? 'g' : '개';
        quantityData.unit = unit;

        // 수수료
        const feeRate = parseInt(document.getElementById('fee_rate').value) || 0;
        quantityData.feeRate = feeRate;

        // 내용
        const content = document.getElementById('content').value.trim();
        if (!content) {
            errors.push('내용을 입력해주세요');
        }
        quantityData.content = content;

        // 상품 판매 URL
        const itemSaleUrl = document.getElementById('itemSaleUrl')?.value.trim();
        quantityData.itemSaleUrl = itemSaleUrl || null;

        // 에러가 있으면 중단
        if (errors.length > 0) {
            alert('다음 항목을 확인해주세요:\n\n' + errors.map((err, idx) => `${idx + 1}. ${err}`).join('\n'));
            return;
        }

        // 나눔 단위 검증
        if (quantity % shareAmount !== 0) {
            alert(`상품 이량은 나눔 단위(${shareAmount}${unit})의 배수여야 합니다.`);
            return;
        }

        if (myQuantity % shareAmount !== 0) {
            alert(`내가 쓸 양은 나눔 단위(${shareAmount}${unit})의 배수여야 합니다.`);
            return;
        }

        // JSON 문자열로 변환하여 FormData에 추가
        const quantityJsonString = JSON.stringify(quantityData);
        formData.append('quantityRegisterRequest', quantityJsonString);

        console.log('제출 데이터:', quantityData);
        console.log('첨부 파일:', fileInput.files[0]?.name);

        // 로딩 표시
        submitBtn.disabled = true;
        submitBtn.textContent = '등록 중...';

        try {
            const response = await fetch('/api/quantityGroupBuy/register', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (response.ok && result.success) {
                alert(result.message || '수량 공동구매가 성공적으로 등록되었습니다!');
                console.log('등록 결과:', result);
                window.location.href = '/groupBuy';
            } else {
                const errorMessage = result.message || '등록에 실패했습니다.';
                alert(`등록 실패\n\n${errorMessage}`);
            }
        } catch (error) {
            console.error('네트워크 오류:', error);
            alert('서버와 통신할 수 없습니다.\n잠시 후 다시 시도해주세요.');
        } finally {
            // 로딩 해제
            submitBtn.disabled = false;
            submitBtn.textContent = '게시';
        }
    }

    // ========== 이벤트 리스너 설정 ==========
    function setupEventListeners() {
        // 단위 변경
        countRadio.addEventListener('change', updateUnits);
        weightRadio.addEventListener('change', updateUnits);

        // 재료 검색
        searchInput.addEventListener('input', debounce(function() {
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

        // 드롭다운 외부 클릭
        document.addEventListener('click', function(e) {
            if (!searchInput.contains(e.target) && !itemMenu.contains(e.target)) {
                itemMenu.classList.remove('show');
            }
        });

        // 기타 품목 추가 버튼
        const addOtherItemBtn = document.querySelector('.btn-outline-secondary');
        if (addOtherItemBtn) {
            addOtherItemBtn.addEventListener('click', function() {
                const otherItem = document.getElementById('otherItem').value.trim();
                if (otherItem) {
                    fetch(`/api/ingredients/add?creatorNo=${creatorNo}&ingredientName=${encodeURIComponent(otherItem)}`, {
                        method: 'POST'
                    })
                        .then(response => {
                            if (!response.ok) {
                                return response.json().then(data => {
                                    throw new Error(data.message || '재료 추가에 실패했습니다.');
                                });
                            }
                            return response.json();
                        })
                        .then(data => {
                            if (data.success) {
                                searchInput.value = data.data.ingredientName;
                                ingredientNoInput.value = data.data.ingredientNo;
                                selectedIngredientNo = data.data.ingredientNo;
                                alert(data.message);
                                document.getElementById('otherItem').value = '';
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert(error.message);
                        });
                } else {
                    alert('품목명을 입력해주세요.');
                }
            });
        }

        // 가격 계산 버튼
        calculateButton.addEventListener('click', calculatePricePerUnit);

        // 파일 첨부
        attachPhotoButton.addEventListener('click', function() {
            if (!attachPhotoButton.disabled) {
                fileInput.click();
            }
        });

        fileInput.addEventListener('change', handleFileChange);

        // 폼 제출
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            submitForm();
        });
    }
});