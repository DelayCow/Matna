import {showAlertModal, showValidationModal} from "./modal.js";
document.addEventListener('DOMContentLoaded', async function() {
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
    let currentMemberNo = null; // 로그인한 사용자의 memberNo

    // ========== 현재 사용자 정보 가져오기 ==========
    try {
        const authData = await getCurrentUser();
        currentMemberNo = authData.memberNo;
    } catch (error) {
        console.error('인증 정보 조회 실패:', error);
        showAlertModal(
            '로그인 필요',
            '로그인 정보를 가져올 수 없습니다.<br>다시 로그인해주세요.',
            'error',
            () => window.location.href = '/login'
        );
        return;
    }

    // ========== 초기화 ==========
    init();

    function init() {
        createDayOptions(daySelects, 7, 1);
        createTimeOptions(hourSelects);
        updateUnits();
        setupEventListeners();
    }
    // ========== API 호출 함수 ==========
    async function getCurrentUser() {
        const response = await api.fetch('/api/auth/currentUser', {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('인증 정보 조회 실패');
        }

        return await response.json();
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
        api.fetch(`/api/ingredients/search?keyword=${encodeURIComponent(query)}`)
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
            showAlertModal(
                '입력 필요',
                '상품 가격, 상품 총량, 나눔 단위를 모두 입력해주세요.',
                'info'
            );
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
    // ======= 폼 오류 모음 ======



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
        quantityData.creatorNo = parseInt(currentMemberNo);

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

        // 상품 총량
        const quantity = parseInt(document.getElementById('quantity').value);
        if (!quantity || quantity <= 0) {
            errors.push('상품 총량을 입력해주세요');
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

        const totalWithFee = price * (1 + feeRate / 100);
        const shareUnits = quantity / shareAmount;
        const pricePerUnit = Math.round(totalWithFee / shareUnits);
        quantityData.pricePerUnit = pricePerUnit;

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
            showValidationModal(errors);
            return;
        }

        // 나눔 단위 검증
        if (quantity % shareAmount !== 0) {
            showAlertModal(
                '입력 오류',
                `상품 총량은 나눔 단위(${shareAmount}${unit})의 배수여야 합니다.`,
                'error'
            );
            return;
        }

        if (myQuantity % shareAmount !== 0) {
            showAlertModal(
                '입력 오류',
                `내가 쓸 양은 나눔 단위(${shareAmount}${unit})의 배수여야 합니다.`,
                'error'
            );
            return;
        }

        // JSON 문자열로 변환하여 FormData에 추가
        const quantityJsonString = JSON.stringify(quantityData);
        formData.append('quantityRegisterRequest', quantityJsonString);

        // 로딩 표시
        submitBtn.disabled = true;
        submitBtn.textContent = '등록 중...';

        try {
            const response = await api.fetch('/api/quantityGroupBuy/register', {
                method: 'POST',
                body: formData
            });

            const result = await response.json();

            if (response.ok && result.success) {
                showAlertModal(
                    '등록 완료',
                    result.message || '수량 공동구매가 성공적으로 등록되었습니다!',
                    'success',
                    () => {
                        if (result.data && result.data.quantityGroupBuyNo) {
                            window.location.href = `/quantityGroupBuy/detail/${result.data.quantityGroupBuyNo}`;
                        } else {
                            window.location.href = '/groupBuy';
                        }
                    }
                );
            } else {
                const errorMessage = result.message || '등록에 실패했습니다.';
                showAlertModal(
                    '등록 실패',
                    errorMessage,
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
        } finally {
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
                    api.fetch(`/api/ingredients/add?creatorNo=${currentMemberNo}&ingredientName=${encodeURIComponent(otherItem)}`, {
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
                                showAlertModal(
                                    '추가 완료',
                                    data.message,
                                    'success'
                                );
                                document.getElementById('otherItem').value = '';
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            showAlertModal(
                                '추가 실패',
                                error.message,
                                'error'
                            );
                        });
                } else {
                    showAlertModal(
                        '입력 필요',
                        '품목명을 입력해주세요.',
                        'info'
                    );
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

// 다음 우편번호 API + 카카오맵 주소 검색 기능
// 카카오맵 API 로드 대기 및 초기화
window.addEventListener('load', function() {
    // kakao 객체가 로드될 때까지 대기
    const checkKakaoLoaded = setInterval(function() {
        if (typeof kakao !== 'undefined' && kakao.maps) {
            clearInterval(checkKakaoLoaded);
            initKakaoMap();
        }
    }, 100);
});

// 카카오맵 초기화 및 이벤트 설정
function initKakaoMap() {

    const mapContainer = document.getElementById('map');
    const mapOption = {
        center: new kakao.maps.LatLng(37.537187, 127.005476), // 지도의 중심좌표
        level: 5 // 지도의 확대 레벨
    };

    // 지도를 미리 생성
    const map = new kakao.maps.Map(mapContainer, mapOption);

    // 주소-좌표 변환 객체를 생성
    const geocoder = new kakao.maps.services.Geocoder();

    // 마커를 미리 생성
    const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(37.537187, 127.005476),
        map: map
    });

    // 다음 우편번호 API 실행 함수
    function execDaumPostcode() {
        new daum.Postcode({
            oncomplete: function(data) {
                const addr = data.address; // 최종 주소 변수

                // 주소 정보를 해당 필드에 넣는다.
                document.getElementById("addressSearch").value = addr;

                // 주소로 상세 정보를 검색
                geocoder.addressSearch(data.address, function(results, status) {
                    // 정상적으로 검색이 완료됐으면
                    if (status === kakao.maps.services.Status.OK) {
                        const result = results[0]; // 첫번째 결과의 값을 활용

                        // 해당 주소에 대한 좌표를 받아서
                        const coords = new kakao.maps.LatLng(result.y, result.x);

                        // 좌표 정보 저장
                        document.getElementById('latitude').value = result.y;
                        document.getElementById('longitude').value = result.x;

                        // 지도를 보여준다.
                        mapContainer.style.display = "block";
                        map.relayout();

                        // 지도 중심을 변경한다.
                        map.setCenter(coords);

                        // 마커를 결과값으로 받은 위치로 옮긴다.
                        marker.setPosition(coords);

                        // 상세주소 입력란으로 포커스 이동
                        document.getElementById('detailAddress').focus();
                    }
                });
            }
        }).open();
    }

    // 주소 검색 버튼 클릭 이벤트
    const searchBtn = document.getElementById('addressSearchBtn');
    if (searchBtn) {
        searchBtn.addEventListener('click', execDaumPostcode);
    }

    // 주소 입력창 클릭 시에도 검색 창 열기
    const addressInput = document.getElementById('addressSearch');
    if (addressInput) {
        addressInput.addEventListener('click', execDaumPostcode);
    }
}