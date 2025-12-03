// === API 호출 ===
const api = {
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
            })
};

// === 렌더링 함수 ===
const render = {
    // 전체 상세 페이지 렌더링
    detail: (data) => {
        const { groupBuyDetail, participants, recipes } = data;

        render.groupBuyInfo(groupBuyDetail, participants);
        render.participants(participants);
        render.recipes(recipes);
    },

    // 공동구매 기본 정보 렌더링
    groupBuyInfo: (detail, participants) => {
        // 상품 이미지
        const productImage = document.getElementById('data-product-image');
        if (productImage && detail.imageUrl) {
            productImage.src = detail.imageUrl;
            productImage.alt = detail.title || '공동구매 이미지';
        }

        // 작성자 정보
        const authorProfile = document.getElementById('data-author-profile');
        if (authorProfile && detail.profileImageUrl) {
            authorProfile.src = detail.profileImageUrl;
        }
        const authorNickname = document.getElementById('data-author-nickname');
        if (authorNickname) {
            authorNickname.textContent = detail.nickname || '익명';
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

        // 가격 정보 계산
        const currentParticipants = participants ? participants.length : 0;
        const maxParticipants = detail.maxParticipants || 1;
        const totalPrice = detail.price || 0;
        const feeRate = detail.feeRate || 0;
        const totalWithFee = totalPrice * (1 + feeRate / 100);

        // 최소/최대 가격
        const minPrice = Math.round(totalWithFee / maxParticipants);
        const maxPrice = Math.round(totalWithFee / Math.max(currentParticipants || 2, 2));

        // 가격 정보 업데이트 (숫자만 업데이트)
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

        // 참여 인원
        const currentParticipantsEl = document.getElementById('data-current-participants');
        if (currentParticipantsEl) {
            currentParticipantsEl.textContent = currentParticipants+1 + '명';
        }
        const maxParticipantsEl = document.getElementById('data-max-participants');
        if (maxParticipantsEl) {
            maxParticipantsEl.textContent = maxParticipants + '명';
        }

        // 남은 시간
        const remainingTimeEl = document.getElementById('data-remaining-time');
        if (remainingTimeEl) {
            remainingTimeEl.textContent = utils.formatRemainingTime(detail.remainingTime);
        }

        // 예상 금액
        const estimatedPriceEl = document.getElementById('data-estimated-price');
        if (estimatedPriceEl) {
            // const pricePerPerson = currentParticipants > 0
            //     ? Math.round(totalWithFee / currentParticipants)
            //     : maxPrice;
            const pricePerPerson = currentParticipants > 0
                ? (totalWithFee / detail.quantity).toFixed(1)
                : maxPrice;
            // const quantityPerPerson = currentParticipants > 0
            //     ? Math.round((detail.quantity || 0) / currentParticipants)
            //     : Math.round((detail.quantity || 0) / maxParticipants);

            estimatedPriceEl.innerHTML = `<span class="text-danger">( 1${detail.unit || 'g'} 당 ${pricePerPerson.toLocaleString()}원 )</span>`;
        }

        // 가격 테이블
        const priceTableEl = document.getElementById('data-price-table');
        if (priceTableEl) {
            priceTableEl.innerHTML = utils.generatePriceTable(detail);
        }

        // 상세 내용
        const productContentEl = document.getElementById('data-product-content');
        if (productContentEl) {
            productContentEl.textContent = detail.content || '공동구매에 대한 상세 설명이 없습니다.';
        }

        // 나눔 장소
        const shareLocationEl = document.getElementById('data-share-location');
        if (shareLocationEl) {
            shareLocationEl.textContent = detail.shareLocation || '장소 정보 없음';
        }
        const shareAddressEl = document.getElementById('data-share-address');
        if (shareAddressEl && detail.shareDetailAddress) {
            shareAddressEl.textContent = ' ' + detail.shareDetailAddress;
        }

        // 상품 구매/나눔 날짜 정보
        const shareDateEl = document.getElementById('data-buy-date');
        if (shareDateEl) {
            shareDateEl.textContent = `상품 수령 후 수령일포함 ${detail.shareEndDate || '?'}일 뒤 ${detail.shareTime || ''}`;
        }
        const buyDateEl = document.getElementById('data-share-date');
        if (buyDateEl) {
            buyDateEl.textContent = `모집 마감 후 ${detail.buyEndDate || '?'}일 이내`;
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

                const item = `
          <div class="d-flex align-items-center mb-3">
            <img src="${profileUrl}" class="rounded-circle me-3" alt="참여자 프로필" 
                 style="width:50px; height:50px;" 
                 onerror="this.onerror=null; this.src='/img/user.png';">
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

                const card = `
          <div class="card card-custom card-wide me-3 flex-shrink-0">
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

// === 유틸리티 함수 ===
const utils = {
    // 남은 시간 포맷팅 (분 단위를 D일 HH:MM:SS로)
    formatRemainingTime: (minutes) => {
        if (typeof minutes !== 'number' || minutes < 0) return '마감됨';

        const days = Math.floor(minutes / (60 * 24));
        minutes %= 60 * 24;
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;

        return `${days}일 ${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}:00`;
    },

    // 가격 테이블 생성
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

// === 버튼 상태 관리 (normal 고정) ===
function showNormalButton() {
    const normalBtn = document.querySelector('.normal-btn');
    if (normalBtn) {
        normalBtn.classList.remove('d-none');
    }
}

// === 모달 동적 콘텐츠 설정 ===
function setupModal() {
    const dynamicMainModal = document.getElementById('dynamicMainModal');
    if (!dynamicMainModal) return;

    dynamicMainModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const modalStatus = button.getAttribute('data-bs-status');

        const titleElement = document.getElementById('dynamicTitle');
        const messageElement = document.getElementById('dynamicMessage');
        const personArea = document.getElementById('personArea');
        const personHeader = document.getElementById('personHeader');
        const personCount = document.getElementById('personCount');
        const reasonContainer = document.getElementById('reasonInputContainer');
        const mainActionButton = document.getElementById('mainActionButton');
        const amountLabel = document.getElementById('amountLabel');
        const amountValue = document.getElementById('amountValue');
        const refundNotice = document.getElementById('refundNotice');

        // 초기화
        if (titleElement) titleElement.textContent = '';
        if (messageElement) messageElement.textContent = '';
        if (personArea) personArea.classList.add('d-none');
        if (reasonContainer) reasonContainer.classList.add('d-none');

        // normal 상태만 처리
        if (modalStatus === 'normal') {
            if (personArea) personArea.classList.remove('d-none');
            if (personHeader) personHeader.textContent = '현재 참여인원';
            if (personCount) personCount.textContent = '2명'; // 실제 데이터로 대체 가능
            if (amountLabel) amountLabel.textContent = '결제 예정 금액';
            if (amountValue) amountValue.textContent = '6,000원'; // 실제 계산된 값으로 대체 가능
            if (refundNotice) {
                refundNotice.textContent = '최종 참여인원에 따른 차액은 공동구매 마감기간 이후 3일 이내 환불됩니다.';
                refundNotice.classList.remove('d-none');
            }
            if (mainActionButton) mainActionButton.textContent = '참여하기';
        }
    });
}

// === 초기화 ===
document.addEventListener('DOMContentLoaded', async function () {
    try {
        // 데이터 로드 및 렌더링
        const data = await api.getDetail();
        render.detail(data);

        // 버튼 표시 (normal 상태)
        showNormalButton();

        // 모달 설정
        setupModal();
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