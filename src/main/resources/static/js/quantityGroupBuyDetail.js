// 전역 변수
let currentStatus = 'normal'; // normal, participant, creator
let currentData = null;
let myGroupBuyParticipantNo = null;
let groupBuyNo = null;
let myCurrentQuantity = 0; // 현재 참여한 수량

const normalBtn = document.querySelector('.normal-btn');
const participantBtn = document.querySelector('.participant-btn');
const creatorRunningBtn = document.querySelector('.creator-running-btn');
const creatorStoppedBtn = document.querySelector('.creator-stopped-btn');

// 상태에 따른 버튼 표시
function showBtnByStatus(status) {
    console.log('showBtnByStatus called with:', status);

    const allBtns = [normalBtn, participantBtn, creatorRunningBtn, creatorStoppedBtn];

    // 모든 버튼 숨기기
    allBtns.forEach(btn => {
        if (btn) {
            btn.classList.add('d-none');
        }
    });

    // 상태에 맞는 버튼 보이기
    switch(status) {
        case 'normal':
            if (normalBtn) {
                normalBtn.classList.remove('d-none');
            }
            break;
        case 'participant':
            if (participantBtn) {
                participantBtn.classList.remove('d-none');
            }
            break;
        case 'creator':
            // 개설자는 2개 버튼 모두 표시
            if (creatorRunningBtn) {
                creatorRunningBtn.classList.remove('d-none');
            }
            if (creatorStoppedBtn) {
                creatorStoppedBtn.classList.remove('d-none');
            }
            break;
    }
}

// === API 호출 ===
const api = {
    // 현재 사용자 인증 정보 조회
    getCurrentUser: () =>
        fetch('/api/auth/currentUser', {
            method: 'GET',
            credentials: 'include'  // 쿠키 포함
        })
            .then(res => {
                if (!res.ok) throw new Error('인증 정보 조회 실패');
                return res.json();
            }),

    getDetail: () =>
        fetch(`/api/quantityGroupBuy/detail/${PAGE_CONFIG.quantityGroupBuyNo}`, {
            method: 'GET'
        })
            .then(res => {
                if (!res.ok) {
                    if (res.status === 404) {
                        throw new Error('존재하지 않는 공동구매 정보입니다.');
                    }
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            }),

    // 참여하기 API
    joinGroupBuy: (groupBuyNo, myQuantity) =>
        fetch(`/api/quantityGroupBuy/join`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ groupBuyNo: groupBuyNo, myQuantity: myQuantity })
        })
            .then(res => res.json()),

    // 수량 수정 API
    modifyQuantity: (groupBuyParticipantNo, newQuantity) =>
        fetch(`/api/quantityGroupBuy/quantityModify/${groupBuyParticipantNo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ newQuantity: newQuantity })
        })
            .then(res => res.json()),

    // 참여 취소 API
    cancelParticipation: (groupBuyParticipantNo) =>
        fetch(`/api/quantityGroupBuy/cancelParticipant/${groupBuyParticipantNo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(res => res.json()),

    // 개설자 진행 API (강제 마감)
    forceClose: (groupBuyNo) =>
        fetch(`/api/quantityGroupBuy/forcedCreator/${groupBuyNo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(res => res.json()),

    // 개설자 중단 API
    stopGroupBuy: (groupBuyNo, reason) =>
        fetch(`/api/quantityGroupBuy/cancelCreator/${groupBuyNo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ cancelReason: reason })
        })
            .then(res => res.json())
};

// === 렌더링 함수 ===
const render = {
    // 전체 상세 페이지 렌더링
    detail: (data) => {
        console.log('Received data:', data);

        const { groupBuyDetail, participant, recipes } = data;
        currentData = data;

        render.groupBuyInfo(groupBuyDetail, participant);
        render.participants(participant);
        render.recipes(recipes);

        // 사용자 상태 결정
        determineUserStatus(groupBuyDetail, participant);
    },

    // 공동구매 기본 정보 렌더링
    groupBuyInfo: (detail, participants) => {
        console.log('groupBuyInfo detail:', detail);

        // 상품 이미지
        const productImage = document.getElementById('data-product-image');
        if (productImage && detail.imageUrl) {
            productImage.src = detail.imageUrl;
            productImage.alt = detail.title || '공동구매 이미지';
        }

        // 작성자 정보
        const authorProfile = document.getElementById('data-author-profile');
        if (authorProfile && detail.creatorProfileUrl) {
            authorProfile.src = detail.creatorProfileUrl;
        }
        const authorNickname = document.getElementById('data-author-nickname');
        if (authorNickname) {
            authorNickname.textContent = detail.creatorNickname || '익명';
        }
        const authorLink = document.getElementById('data-author-link');
        if (authorLink && detail.creatorNo) {
            authorLink.href = `/mypage/${detail.creatorNo}`;
        }

        // 상품 정보
        const productTitle = document.getElementById('data-product-title');
        if (productTitle) {
            productTitle.textContent = detail.title || '제목 없음';
        }

        const itemSaleUrl = document.getElementById('data-item-sale-url');
        if (itemSaleUrl) {
            itemSaleUrl.href = detail.itemSaleUrl || '#';
            if (!detail.itemSaleUrl) {
                itemSaleUrl.style.display = 'none';
            }
        }

        // 가격 정보
        const priceEl = document.getElementById('data-price');
        if (priceEl && detail.pricePerUnit) {
            priceEl.textContent = detail.pricePerUnit.toLocaleString();
        }

        const shareAmountEl = document.getElementById('data-share-amount');
        if (shareAmountEl && detail.shareAmount) {
            shareAmountEl.textContent = detail.shareAmount;
        }

        const unitEl = document.getElementById('data-unit');
        if (unitEl && detail.unit) {
            unitEl.textContent = detail.unit;
        }

        const feeRateEl = document.getElementById('data-fee-rate');
        if (feeRateEl && detail.feeRate !== null) {
            feeRateEl.textContent = detail.feeRate;
        }

        // 남은 수량
        const remainingQtyEl = document.getElementById('data-remaining-qty');
        if (remainingQtyEl && detail.remainingQty !== null) {
            remainingQtyEl.textContent = detail.remainingQty + (detail.unit || 'g');
        }

        // 공구 오픈일
        const openDateEl = document.getElementById('data-open-date');
        if (openDateEl && detail.inDate) {
            const inDate = new Date(detail.inDate);
            const now = new Date();
            const diffTime = Math.abs(now - inDate);
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

            const formattedDate = inDate.toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });

            openDateEl.textContent = `${formattedDate} (${diffDays}일 지남)`;
        }

        // 상세 내용
        const productContentEl = document.getElementById('data-product-content');
        if (productContentEl && detail.content) {
            productContentEl.textContent = detail.content;
        }

        // 나눔 장소
        const shareLocationEl = document.getElementById('data-share-location');
        if (shareLocationEl && detail.shareLocation) {
            shareLocationEl.textContent = detail.shareLocation;
        }

        const shareAddressEl = document.getElementById('data-share-address');
        if (shareAddressEl && detail.shareDetailAddress) {
            shareAddressEl.textContent = ' ' + detail.shareDetailAddress;
        }

        // 지도 아이콘 클릭 이벤트 설정
        const mapIcon = document.getElementById('address-map');
        if (mapIcon && detail.shareLocation) {
            mapIcon.style.cursor = 'pointer';
            mapIcon.addEventListener('click', function() {
                openKakaoMap(detail.shareLocation);
            });
        }

        // 상품 구매/나눔 날짜 정보
        const shareDateEl = document.getElementById('data-share-date');
        if (shareDateEl && detail.shareEndDate) {
            shareDateEl.textContent = `상품 수령 후 수령일포함 ${detail.shareEndDate}일 뒤 ${detail.shareTime || ''}`;
        }

        const buyDateEl = document.getElementById('data-buy-date');
        if (buyDateEl && detail.buyEndDate) {
            buyDateEl.textContent = `모집 마감 후 ${detail.buyEndDate}일 이내`;
        }
    },

    // 참여자 목록 렌더링
    participants: (participants) => {
        const countHeader = document.getElementById('data-participants-count-header');
        const participantListEl = document.getElementById('data-participants-list');

        if (!countHeader || !participantListEl) return;

        const participantCount = participants ? participants.length : 0;
        countHeader.textContent = `참여자 ${participantCount}명`;

        participantListEl.innerHTML = '';

        if (participants && participants.length > 0) {
            participants.forEach(p => {
                const date = p.participatedDate
                    ? new Date(p.participatedDate).toLocaleDateString('ko-KR', {
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit'
                    })
                    : '';

                const profileUrl = p.profileUrl || '/img/user.png';
                const nickname = p.nickname || '익명';
                const myQuantity = p.myQuantity || 0;
                const memberNo = p.memberNo || '';

                const item = `
          <div class="d-flex align-items-center mb-3">
            <a href="/mypage/${memberNo}" style="text-decoration: none; color: inherit;">
              <img src="${profileUrl}" class="rounded-circle me-3" alt="참여자 프로필" 
                   style="width:50px; height:50px; cursor: pointer;" 
                   onerror="this.onerror=null; this.src='/img/user.png';">
            </a>
            <div class="d-flex flex-column flex-grow-1">
              <span class="fw-bold fs-6">${nickname}</span>
              <span class="small text-muted">${date}</span>
            </div>
            <span class="fw-bold text-danger">${myQuantity}${currentData.groupBuyDetail.unit || 'g'}</span>
          </div>
        `;
                participantListEl.insertAdjacentHTML('beforeend', item);
            });
        } else {
            participantListEl.innerHTML = '<p class="text-muted small">아직 참여자가 없습니다.</p>';
        }
    },

    // 레시피 목록 렌더링
    recipes: (recipes) => {
        const recipeListEl = document.getElementById('data-recipe-list');
        if (!recipeListEl) return;

        recipeListEl.innerHTML = '';

        if (recipes && recipes.length > 0) {
            recipes.forEach(recipe => {
                const imageUrl = recipe.imageUrl || 'https://placehold.co/150x100/A0B2C9/ffffff?text=Recipe';
                const title = recipe.title || '제목 없음';
                const authorNickname = recipe.authorNickname || '익명';
                const recipeNo = recipe.recipeNo;

                const card = `
          <div class="card card-custom card-wide me-3 flex-shrink-0" style="cursor: pointer;" onclick="location.href='/recipe/detail/${recipeNo}'">
            <img src="${imageUrl}" class="card-img-top" alt="레시피 이미지" 
                 onerror="this.onerror=null; this.src='https://placehold.co/150x100/A0B2C9/ffffff?text=Recipe';">
            <div class="card-body px-2 py-2">
              <p class="card-text fw-bold text-truncate mb-1">${title}</p>
              <small class="text-muted d-block">작성자 | ${authorNickname}</small>
            </div>
          </div>
        `;
                recipeListEl.insertAdjacentHTML('beforeend', card);
            });
        } else {
            recipeListEl.innerHTML = '<p class="text-muted small">이 상품과 관련된 추천 레시피가 없습니다.</p>';
        }
    }
};

// 카카오맵에서 주소 검색 (새 창 열기)
function openKakaoMap(address) {
    if (!address) {
        alert('주소 정보가 없습니다.');
        return;
    }

    // 카카오맵 검색 URL (주소로 검색)
    const kakaoMapUrl = `https://map.kakao.com/link/search/${encodeURIComponent(address)}`;

    // 새 창으로 열기
    window.open(kakaoMapUrl, '_blank', 'width=900,height=700');
}

// 사용자 상태 결정 (normal, participant, creator)
function determineUserStatus(detail, participants) {
    console.log('=== determineUserStatus called ===');
    console.log('Current member no:', PAGE_CONFIG.currentMemberNo);
    console.log('Creator no:', detail.creatorNo);
    console.log('GroupBuy no:', detail.groupBuyNo);
    console.log('Participants:', participants);

    const currentMemberNo = PAGE_CONFIG.currentMemberNo;
    const creatorNo = detail.creatorNo;
    groupBuyNo = detail.groupBuyNo;


    // 1순위: 개설자인 경우
    if (currentMemberNo === creatorNo) {
        console.log('✓ User is CREATOR');
        currentStatus = 'creator';
        showBtnByStatus(currentStatus);
        return;
    }

    // 2순위: 참여자인 경우 (memberNo로 확인)
    const myParticipation = participants.find(p => {
        console.log(`Checking participant - memberNo: ${p.memberNo}, vs current: ${currentMemberNo}`);
        return p.memberNo === currentMemberNo;
    });

    if (myParticipation) {
        console.log('✓ User is PARTICIPANT');
        console.log('  - groupParticipantNo:', myParticipation.groupParticipantNo);
        console.log('  - memberNo:', myParticipation.memberNo);
        console.log('  - myQuantity:', myParticipation.myQuantity);
        currentStatus = 'participant';
        myGroupBuyParticipantNo = myParticipation.groupParticipantNo;
        myCurrentQuantity = myParticipation.myQuantity || 0;
        showBtnByStatus(currentStatus);
        return;
    }

    // 3순위: 일반 사용자
    console.log('✓ User is NORMAL (not participant, not creator)');
    currentStatus = 'normal';
    showBtnByStatus(currentStatus);
}

// 수량 옵션 생성 (참여자 수정 시 현재 수량 제외)
function generateQuantityOptions(shareAmount, unit, maxQuantity, excludeQuantity = null) {
    const options = [];
    for (let qty = shareAmount; qty <= maxQuantity; qty += shareAmount) {
        if (excludeQuantity && qty === excludeQuantity) {
            continue; // 현재 수량은 제외
        }
        options.push({
            value: qty,
            text: `${qty}${unit}`
        });
    }
    return options;
}

// === 모달 동적 콘텐츠 설정 ===
document.addEventListener('DOMContentLoaded', function () {
    const participantQuantityModal = document.getElementById('participantQuantityModal');
    if (!participantQuantityModal) return;

    participantQuantityModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const modalStatus = button?.getAttribute('data-bs-status');

        const remainingQtyDisplay = document.getElementById('modal-remaining-qty');
        const currentQtyRow = document.getElementById('modal-current-qty-row');
        const currentQtyDisplay = document.getElementById('modal-current-qty');
        const quantityLabel = document.getElementById('quantityLabel');
        const amountLabel = document.getElementById('amountLabel');
        const amountValue = document.getElementById('modal-amount-value');
        const normalBtnContainer = document.getElementById('normalButtonContainer');
        const participantBtnContainer = document.getElementById('participantButtonContainer');
        const quantitySelect = document.getElementById('quantitySelect');

        if (!currentData) return;

        const detail = currentData.groupBuyDetail;
        const shareAmount = detail.shareAmount || 100;
        const unit = detail.unit || 'g';
        const pricePerUnit = detail.pricePerUnit || 0;
        const remainingQty = detail.remainingQty || 0;

        // 남은 수량 표시
        if (remainingQtyDisplay) {
            remainingQtyDisplay.textContent = `${remainingQty}${unit}`;
        }

        let options = [];

        if (modalStatus === 'participant') {
            // 참여자 - 수정
            currentQtyRow?.classList.remove('d-none');
            if (currentQtyDisplay) {
                currentQtyDisplay.textContent = `${myCurrentQuantity}${unit}`;
            }

            quantityLabel.textContent = '수량 수정';
            amountLabel.textContent = '추가 결제 / 환불 금액';
            normalBtnContainer.classList.add('d-none');
            participantBtnContainer.classList.remove('d-none');

            // 현재 수량 제외하고 옵션 생성
            const maxQuantity = remainingQty + myCurrentQuantity;
            options = generateQuantityOptions(shareAmount, unit, maxQuantity, myCurrentQuantity);

        } else { // normal
            // 일반 사용자 - 참여
            currentQtyRow?.classList.add('d-none');

            quantityLabel.textContent = '수량 선택';
            amountLabel.textContent = '결제 금액';
            normalBtnContainer.classList.remove('d-none');
            participantBtnContainer.classList.add('d-none');

            // 남은 수량 범위 내에서 옵션 생성
            options = generateQuantityOptions(shareAmount, unit, remainingQty);
        }

        // 옵션 렌더링
        quantitySelect.innerHTML = '';
        if (options.length === 0) {
            quantitySelect.innerHTML = '<option>선택 가능한 수량이 없습니다</option>';
            quantitySelect.disabled = true;
        } else {
            quantitySelect.disabled = false;
            options.forEach((opt, idx) => {
                const option = document.createElement('option');
                option.value = opt.value;
                option.textContent = opt.text;
                if (idx === 0) option.selected = true;
                quantitySelect.appendChild(option);
            });
        }

        // 수량 변경 시 금액 업데이트
        const updateAmount = () => {
            const selectedQty = parseInt(quantitySelect.value) || 0;
            const shareUnits = selectedQty / shareAmount;
            const amount = shareUnits * pricePerUnit;

            if (modalStatus === 'participant') {
                // 차액 계산
                const currentShareUnits = myCurrentQuantity / shareAmount;
                const currentAmount = currentShareUnits * pricePerUnit;
                const diff = amount - currentAmount;

                amountValue.textContent = (diff >= 0 ? '+' : '') + diff.toLocaleString() + '원';
                amountValue.className = diff >= 0 ? 'fs-4 fw-bold text-danger' : 'fs-4 fw-bold text-primary';
            } else {
                amountValue.textContent = amount.toLocaleString() + '원';
                amountValue.className = 'fs-4 fw-bold text-danger';
            }
        };

        quantitySelect.addEventListener('change', updateAmount);
        updateAmount(); // 초기 금액 설정
    });
});

// 개설자 액션 모달
document.addEventListener('DOMContentLoaded', function () {
    const creatorActionModal = document.getElementById('creatorActionModal');
    if (!creatorActionModal) return;

    creatorActionModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const action = button?.getAttribute('data-bs-action');

        const titleElement = document.getElementById('creatorActionTitle');
        const messageElement = document.getElementById('creatorActionMessage');
        const reasonContainer = document.getElementById('reasonInputContainer');
        const actionButton = document.getElementById('creatorActionButton');

        if (!currentData) return;

        const detail = currentData.groupBuyDetail;
        const remainingQty = detail.remainingQty || 0;
        const unit = detail.unit || 'g';

        if (action === 'run') {
            // 진행하기
            reasonContainer?.classList.add('d-none');
            titleElement.innerHTML = `공동구매를 <span class="text-danger">지금</span> 진행하시겠습니까?`;
            messageElement.innerHTML = `남은 수량인 <span class="fw-bold text-decoration-underline">${remainingQty}${unit}</span>은 개설자님께서 부담하게 됩니다.`;
            actionButton.textContent = '진행하기';
            actionButton.setAttribute('data-action-type', 'run');
        } else if (action === 'stop') {
            // 중단하기
            reasonContainer?.classList.remove('d-none');
            titleElement.innerHTML = `공동구매를 정말 <span class="text-danger">중단</span>하시겠습니까?`;
            messageElement.innerHTML = `${remainingQty}${unit}만 더 참여하면 마감할 수 있습니다.<br>모든 참여자에게 포인트가 환불됩니다.`;
            actionButton.textContent = '중단하기';
            actionButton.setAttribute('data-action-type', 'stop');
        }
    });
});

// === 모달 액션 버튼 이벤트 ===
document.addEventListener('DOMContentLoaded', function() {
    // 참여/수정 모달 버튼
    const joinBtn = document.getElementById('normalJoinBtn');
    const cancelBtn = document.getElementById('participantCancelBtn');
    const modifyBtn = document.getElementById('participantModifyBtn');

    if (joinBtn) {
        joinBtn.addEventListener('click', async function() {
            const quantitySelect = document.getElementById('quantitySelect');
            const selectedQuantity = parseInt(quantitySelect.value);

            if (!selectedQuantity) {
                alert('수량을 선택해주세요.');
                return;
            }

            if (confirm(`${selectedQuantity}${currentData.groupBuyDetail.unit}으로 참여하시겠습니까?`)) {
                try {
                    const response = await api.joinGroupBuy(groupBuyNo, selectedQuantity);

                    if (response.success) {
                        alert(response.message);
                        location.reload();
                    } else {
                        alert(response.message || '참여에 실패했습니다.');
                    }
                } catch (error) {
                    console.error('참여 오류:', error);
                    alert('참여 중 오류가 발생했습니다.');
                }
            }
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', async function() {
            if (!myGroupBuyParticipantNo) {
                alert('참여 정보를 찾을 수 없습니다.');
                return;
            }

            if (confirm('정말 참여를 취소하시겠습니까?')) {
                try {
                    const response = await api.cancelParticipation(myGroupBuyParticipantNo);

                    if (response.success) {
                        alert(response.message);
                        location.reload();
                    } else {
                        alert(response.message || '취소에 실패했습니다.');
                    }
                } catch (error) {
                    console.error('취소 오류:', error);
                    alert('취소 중 오류가 발생했습니다.');
                }
            }
        });
    }

    if (modifyBtn) {
        modifyBtn.addEventListener('click', async function() {
            const quantitySelect = document.getElementById('quantitySelect');
            const newQuantity = parseInt(quantitySelect.value);

            if (!newQuantity) {
                alert('수량을 선택해주세요.');
                return;
            }

            if (confirm(`수량을 ${newQuantity}${currentData.groupBuyDetail.unit}으로 수정하시겠습니까?`)) {
                try {
                    const response = await api.modifyQuantity(myGroupBuyParticipantNo, newQuantity);

                    if (response.success) {
                        alert(response.message);
                        location.reload();
                    } else {
                        alert(response.message || '수정에 실패했습니다.');
                    }
                } catch (error) {
                    console.error('수정 오류:', error);
                    alert('수정 중 오류가 발생했습니다.');
                }
            }
        });
    }

    // 개설자 액션 버튼
    const creatorActionBtn = document.getElementById('creatorActionButton');
    if (creatorActionBtn) {
        creatorActionBtn.addEventListener('click', async function() {
            const actionType = this.getAttribute('data-action-type');

            if (actionType === 'run') {
                // 진행하기
                if (confirm('공동구매를 진행하시겠습니까?')) {
                    try {
                        const response = await api.forceClose(groupBuyNo);

                        if (response.success) {
                            alert(response.message);
                            location.reload();
                        } else {
                            alert(response.message || '진행에 실패했습니다.');
                        }
                    } catch (error) {
                        console.error('진행 오류:', error);
                        alert('진행 중 오류가 발생했습니다.');
                    }
                }
            } else if (actionType === 'stop') {
                // 중단하기
                const reasonTextarea = document.getElementById('cancelReasonTextarea');
                const reason = reasonTextarea?.value.trim();

                if (!reason) {
                    alert('중단 사유를 입력해주세요.');
                    return;
                }

                if (confirm('공동구매를 중단하시겠습니까?')) {
                    try {
                        const response = await api.stopGroupBuy(groupBuyNo, reason);

                        if (response.success) {
                            alert(response.message);
                            location.reload();
                        } else {
                            alert(response.message || '중단에 실패했습니다.');
                        }
                    } catch (error) {
                        console.error('중단 오류:', error);
                        alert('중단 중 오류가 발생했습니다.');
                    }
                }
            }
        });
    }
});

// === 초기화 ===
document.addEventListener('DOMContentLoaded', async function () {
    try {
        // 1. 인증 정보 먼저 로드
        const authData = await api.getCurrentUser();

        PAGE_CONFIG.currentMemberNo = authData.memberNo;
        console.log('Updated PAGE_CONFIG:', PAGE_CONFIG);
        // 데이터 로드 및 렌더링
        const data = await api.getDetail();
        render.detail(data);
    } catch (error) {
        console.error('데이터 로드 중 오류 발생:', error);

        const contentArea = document.querySelector('.container-fluid.content-area');
        if (contentArea) {
            let errorMessage = '데이터 로드 중 심각한 오류가 발생했습니다.';
            if (error.message.includes('존재하지 않는')) {
                errorMessage = error.message;
            }
            contentArea.innerHTML = `<div class="p-5 text-center text-danger">${errorMessage}</div>`;
        }
    }
});