// 전역 변수
let currentStatus = 'normal';
let currentData = null;
let myGroupBuyParticipantNo = null;
let groupBuyNo = null;

const normalBtn = document.querySelector('.normal-btn');
const participantBtn = document.querySelector('.participant-btn');
const creatorRunningBtn = document.querySelector('.creator-running-btn');

// 상태에 따른 버튼 표시
function showBtnByStatus(status) {

    const allBtns = [normalBtn, participantBtn, creatorRunningBtn];

    allBtns.forEach(btn => {
        if (btn) {
            btn.classList.add('d-none');
        }
    });

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
            if (creatorRunningBtn) {
                creatorRunningBtn.classList.remove('d-none');
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

    // 공동구매 상세 조회
    getDetail: () =>
        fetch(`/api/periodGroupBuy/detail/${PAGE_CONFIG.periodGroupBuyNo}`, {
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
    joinGroupBuy: (groupBuyNo) =>
        fetch(`/api/periodGroupBuy/join`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ groupBuyNo: groupBuyNo })
        })
            .then(res => res.json()),

    // 참여 취소 API
    cancelParticipation: (groupBuyParticipantNo) =>
        fetch(`/api/periodGroupBuy/cancelParticipant/${groupBuyParticipantNo}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(res => res.json()),

    // 개설자 중단 API
    stopGroupBuy: (groupBuyNo, reason) =>
        fetch(`/api/periodGroupBuy/cancelCreator/${groupBuyNo}`, {
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
    detail: (data) => {

        const { groupBuyDetail, participants, recipes } = data;
        currentData = data;

        render.groupBuyInfo(groupBuyDetail, participants);
        render.participants(participants);
        render.recipes(recipes);

        determineUserStatus(groupBuyDetail, participants);
    },

    groupBuyInfo: (detail, participants) => {

        const productImage = document.getElementById('data-product-image');
        if (productImage && detail.imageUrl) {
            productImage.src = detail.imageUrl;
            productImage.alt = detail.title || '공동구매 이미지';
        }

        const authorProfile = document.getElementById('data-author-profile');
        if (authorProfile && detail.profileImageUrl) {
            authorProfile.src = detail.profileImageUrl;
        }
        const authorNickname = document.getElementById('data-author-nickname');
        if (authorNickname) {
            authorNickname.textContent = detail.nickname || '익명';
        }

        const productTitle = document.getElementById('data-product-title');
        if (productTitle) {
            productTitle.textContent = detail.title || '제목 없음';
        }
        const authorLink = document.getElementById('data-author-link');
        if (authorLink && detail.creatorNo) {
            authorLink.href = `/mypage/${detail.creatorNo}`;
        }

        const itemSaleUrl = document.getElementById('data-item-sale-url');
        if (itemSaleUrl) {
            itemSaleUrl.href = detail.itemSaleUrl || '#';
            if (!detail.itemSaleUrl) {
                itemSaleUrl.style.display = 'none';
            }
        }

        const currentParticipants = participants ? participants.length : 0;
        const maxParticipants = detail.maxParticipants || 1;
        const totalPrice = detail.price || 0;
        const feeRate = detail.feeRate || 0;
        const totalWithFee = totalPrice * (1 + feeRate / 100);

        const minPrice = Math.round(totalWithFee / maxParticipants);
        const maxPrice = Math.round(totalWithFee / 2);

        const minPriceEl = document.getElementById('data-min-price');
        if (minPriceEl) {
            minPriceEl.textContent = minPrice.toLocaleString();
        }
        const maxPriceEl = document.getElementById('data-max-price');
        if (maxPriceEl) {
            maxPriceEl.textContent = maxPrice.toLocaleString();
        }

        const feeRateEl = document.getElementById('data-fee-rate');
        if (feeRateEl) {
            feeRateEl.textContent = feeRate.toFixed(0);
        }

        const currentParticipantsEl = document.getElementById('data-current-participants');
        if (currentParticipantsEl) {
            currentParticipantsEl.textContent = (currentParticipants + 1);
        }
        const maxParticipantsEl = document.getElementById('data-max-participants');
        if (maxParticipantsEl) {
            maxParticipantsEl.textContent = maxParticipants;
        }

        const remainingTimeEl = document.getElementById('data-remaining-time');
        const dueDate = detail.dueDate;

        if (remainingTimeEl && dueDate) {
            const targetTimeMs = new Date(dueDate).getTime();
            const now = new Date().getTime();
            const initialRemainingMs = targetTimeMs - now;

            if (initialRemainingMs > 0) {
                utils.startCountdown(remainingTimeEl, targetTimeMs);
            } else {
                remainingTimeEl.innerHTML = '모집마감';
                remainingTimeEl.classList.remove('text-danger');
                remainingTimeEl.classList.add('text-muted');
            }
        }

        const estimatedPriceEl = document.getElementById('data-estimated-price');
        if (estimatedPriceEl) {
            const pricePerUnit = currentParticipants > 0
                ? (totalWithFee / detail.quantity).toFixed(1)
                : maxPrice;
            estimatedPriceEl.innerHTML = `<span class="text-danger">( 1${detail.unit || 'g'} 당 ${parseFloat(pricePerUnit).toLocaleString()}원 )</span>`;
        }

        const priceTableEl = document.getElementById('data-price-table');
        if (priceTableEl) {
            priceTableEl.innerHTML = utils.generatePriceTable(detail);
        }

        const productContentEl = document.getElementById('data-product-content');
        if (productContentEl) {
            productContentEl.textContent = detail.content || '공동구매에 대한 상세 설명이 없습니다.';
        }

        const shareLocationEl = document.getElementById('data-share-location');
        if (shareLocationEl) {
            shareLocationEl.textContent = detail.shareLocation || '장소 정보 없음';
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

        const shareDateEl = document.getElementById('data-buy-date');
        if (shareDateEl) {
            shareDateEl.textContent = `상품 수령 후 수령일포함 ${detail.shareEndDate || '?'}일 뒤 ${detail.shareTime || ''}`;
        }
        const buyDateEl = document.getElementById('data-share-date');
        if (buyDateEl) {
            buyDateEl.textContent = `모집 마감 후 ${detail.buyEndDate || '?'}일 이내`;
        }
    },

    participants: (participants) => {
        const participantListEl = document.getElementById('data-participants-list');

        if (!participantListEl) return;

        const participantCount = participants ? participants.length : 0;
        const countElement = document.getElementById('data-participants-count');
        if (countElement) {
            countElement.textContent = participantCount;
        }

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
                const memberNo = p.memberNo || '';

                const item = `
          <div class="d-flex align-items-center mb-3">
            <a href="/mypage/${memberNo}" style="text-decoration: none; color: inherit;">
              <img src="${profileUrl}" class="rounded-circle me-3" alt="참여자 프로필" 
                   style="width:50px; height:50px; cursor: pointer;" 
                   onerror="this.onerror=null; this.src='/img/user.png';">
            </a>
            <div class="d-flex flex-column">
              <span class="fw-bold fs-6">${nickname}</span>
              <span class="small text-muted">${date}</span>
            </div>
          </div>
        `;
                participantListEl.insertAdjacentHTML('beforeend', item);
            });
        } else {
            participantListEl.innerHTML = '<p class="text-muted small">아직 참여자가 없습니다.</p>';
        }
    },

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

// 사용자 상태 결정
function determineUserStatus(detail, participants) {

    const currentMemberNo = PAGE_CONFIG.currentMemberNo;
    const creatorNo = detail.creatorNo;
    groupBuyNo = detail.groupBuyNo;

    if (!currentMemberNo || currentMemberNo === null) {
        currentStatus = 'normal';
        showBtnByStatus(currentStatus);
        return;
    }

    if (currentMemberNo === creatorNo) {
        currentStatus = 'creator';
        showBtnByStatus(currentStatus);
        return;
    }

    const myParticipation = participants.find(p => {
        return p.memberNo === currentMemberNo;
    });

    if (myParticipation) {
        currentStatus = 'participant';
        myGroupBuyParticipantNo = myParticipation.groupParticipantNo;
        showBtnByStatus(currentStatus);
        return;
    }

    currentStatus = 'normal';
    showBtnByStatus(currentStatus);
}

// === 유틸리티 함수 ===
const utils = {
    startCountdown: (timerElement, targetTimestampMs) => {
        const targetTime = targetTimestampMs;

        if (timerElement.dataset.timerIntervalId) {
            clearInterval(parseInt(timerElement.dataset.timerIntervalId));
        }

        let timerInterval;

        const updateCountdown = () => {
            const now = new Date().getTime();
            let distance = targetTime - now;

            if (distance <= 0) {
                clearInterval(timerInterval);
                timerElement.textContent = '모집마감';
                timerElement.classList.remove('text-danger');
                timerElement.classList.add('text-muted');
                delete timerElement.dataset.timerIntervalId;
                return;
            }

            const D_IN_MS = 1000 * 60 * 60 * 24;
            const H_IN_MS = 1000 * 60 * 60;
            const M_IN_MS = 1000 * 60;

            const days = Math.floor(distance / D_IN_MS);
            const hours = Math.floor((distance % D_IN_MS) / H_IN_MS);
            const minutes = Math.floor((distance % H_IN_MS) / M_IN_MS);
            const seconds = Math.floor((distance % M_IN_MS) / 1000);

            timerElement.innerHTML = `
                <span class="font-bold text-lg">${days}</span>일 
                <span class="font-bold text-lg">${String(hours).padStart(2, '0')}</span> :
                <span class="font-bold text-lg">${String(minutes).padStart(2, '0')}</span> :
                <span class="font-bold text-lg">${String(seconds).padStart(2, '0')}</span>
            `;
        };

        timerInterval = setInterval(updateCountdown, 1000);
        timerElement.dataset.timerIntervalId = timerInterval.toString();
        updateCountdown();
    },

    generatePriceTable: (detail) => {
        const maxParticipants = detail.maxParticipants || 5;
        const totalPrice = detail.price || 0;
        const feeRate = detail.feeRate || 0;
        const totalQuantity = detail.quantity || 0;
        const unit = detail.unit || 'g';
        const totalWithFee = totalPrice * (1 + feeRate / 100);

        if (totalPrice === 0 || maxParticipants <= 1) {
            return '<p class="text-muted small">예상 금액 테이블을 표시할 수 없습니다.</p>';
        }

        let theadHtml = '<thead><tr>';
        let tbodyHtml = '<tbody><tr>';

        for (let count = 2; count <= maxParticipants; count++) {
            const pricePerUnit = Math.round(totalWithFee / count);
            const amountPerUnit = (totalQuantity / count).toFixed(1);

            theadHtml += `<th scope="col">${count}명</th>`;
            tbodyHtml += `<td>${pricePerUnit.toLocaleString()}원 <br> ${amountPerUnit}${unit}</td>`;
        }

        theadHtml += '</tr></thead>';
        tbodyHtml += '</tr></tbody>';

        return `
      <table class="table table-bordered text-center group-buy-table mb-0">
        ${theadHtml}
        ${tbodyHtml}
      </table>
    `;
    }
};

// === 모달 설정 (기존과 동일) ===
document.addEventListener('DOMContentLoaded', function () {
    const dynamicMainModal = document.getElementById('dynamicMainModal');
    if (!dynamicMainModal) return;

    dynamicMainModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const modalStatus = button?.getAttribute('data-bs-status');

        const titleElement = document.getElementById('dynamicTitle');
        const messageElement = document.getElementById('dynamicMessage');
        const personArea = document.getElementById('personArea');
        const cancelConfirmArea = document.getElementById('cancelConfirmArea');
        const reasonContainer = document.getElementById('reasonInputContainer');
        const mainActionButton = document.getElementById('mainActionButton');

        personArea?.classList.add('d-none');
        cancelConfirmArea?.classList.add('d-none');
        reasonContainer?.classList.add('d-none');

        if (modalStatus === 'normal') {
            titleElement.textContent = '공동구매 참여하기';
            messageElement.textContent = '아래 금액으로 참여하시겠습니까?';
            personArea?.classList.remove('d-none');
            mainActionButton.textContent = '참여하기';
            mainActionButton.className = 'btn btn-danger py-2 fw-bold';

            if (currentData) {
                const participants = currentData.participants || [];
                const detail = currentData.groupBuyDetail;
                const currentCount = participants.length + 1;
                const totalPrice = detail.price || 0;
                const feeRate = detail.feeRate || 0;
                const totalWithFee = totalPrice * (1 + feeRate / 100);
                const estimatedAmount = Math.round(totalWithFee / (currentCount + 1));

                document.getElementById('personCount').textContent = currentCount;
                document.getElementById('amountValue').textContent = estimatedAmount.toLocaleString();

                const modalPriceTable = document.getElementById('modal-price-table');
                if (modalPriceTable) {
                    modalPriceTable.innerHTML = utils.generatePriceTable(detail);
                }
            }
        } else if (modalStatus === 'participant') {
            titleElement.textContent = '공동구매 참여 취소';
            messageElement.textContent = '정말 참여를 취소하시겠습니까?';
            cancelConfirmArea?.classList.remove('d-none');
            mainActionButton.textContent = '취소하기';
            mainActionButton.className = 'btn btn-danger py-2 fw-bold';

            if (currentData) {
                const detail = currentData.groupBuyDetail;
                const participants = currentData.participants || [];
                const currentCount = participants.length + 1;
                const totalPrice = detail.price || 0;
                const feeRate = detail.feeRate || 0;
                const totalWithFee = totalPrice * (1 + feeRate / 100);
                const refundAmount = Math.round(totalWithFee / currentCount);

                document.getElementById('refundAmount').textContent = refundAmount.toLocaleString();
            }
        } else if (modalStatus === 'creator') {
            titleElement.innerHTML = '공동구매를 정말 <span class="text-danger">중단</span>하시겠습니까?';
            messageElement.textContent = '모든 참여자에게 포인트가 환불됩니다.';
            reasonContainer?.classList.remove('d-none');
            mainActionButton.textContent = '중단하기';
            mainActionButton.className = 'btn btn-danger py-2 fw-bold';
        }
    });
});

// 모달 액션 버튼 이벤트
document.addEventListener('DOMContentLoaded', function() {
    const mainActionButton = document.getElementById('mainActionButton');
    if (!mainActionButton) return;

    mainActionButton.addEventListener('click', async function() {
        if (currentStatus === 'normal') {
            if (confirm('공동구매에 참여하시겠습니까?')) {
                try {
                    const response = await api.joinGroupBuy(groupBuyNo);

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
        } else if (currentStatus === 'participant') {
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
        } else if (currentStatus === 'creator') {
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
});

// === 초기화 ===
document.addEventListener('DOMContentLoaded', async function () {
    try {
        // 1. 인증 정보 먼저 로드
        const authData = await api.getCurrentUser();

        PAGE_CONFIG.currentMemberNo = authData.memberNo;

        // 2. 공동구매 데이터 로드 및 렌더링
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