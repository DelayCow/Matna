// normal, participant, creator
const status = 'normal';

const normalBtn = document.querySelector('.normal-btn');
const participantBtn = document.querySelector('.participant-btn');
const creatorStoppedBtn = document.querySelector('.creator-stopped-btn');
const creatorRunningBtn = document.querySelector('.creator-running-btn');

function showBtnByStatus(status){
    const allBtns = [normalBtn, participantBtn, creatorRunningBtn, creatorStoppedBtn];
    allBtns.forEach(btn => btn.classList.remove('show'));

    switch(status){
        case 'normal':
            normalBtn.classList.add('show');
            break;
        case 'participant':
            participantBtn.classList.add('show');
            break;
        case 'creator':
            creatorStoppedBtn.classList.add('show');
            creatorRunningBtn.classList.add('show');
            break;
    }
}
showBtnByStatus(status);

// === API 호출 ===
const api = {
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
            })
};

// === 렌더링 함수 ===
const render = {
    // 전체 상세 페이지 렌더링
    detail: (data) => {
        const { groupBuyDetail, participant, recipes } = data;

        render.groupBuyInfo(groupBuyDetail, participant);
        render.participants(participant);
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
        if (authorProfile && detail.creatorProfileUrl) {
            authorProfile.src = detail.creatorProfileUrl;
        }
        const authorNickname = document.getElementById('data-author-nickname');
        if (authorNickname) {
            authorNickname.textContent = detail.creatorNickname || '익명';
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

        // 상품 구매/나눔 날짜 정보
        const shareDateEl = document.getElementById('data-buy-date');
        if (shareDateEl && detail.buyEndDate) {
            shareDateEl.textContent = `모집 마감 후 ${detail.buyEndDate}일 이내`;
        }

        const buyDateEl = document.getElementById('data-share-date');
        if (buyDateEl && detail.shareEndDate) {
            buyDateEl.textContent = `상품 수령 후 수령일포함 ${detail.shareEndDate}일 뒤`;
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

// === 모달 동적 콘텐츠 설정 ===
const normalOptions = [
    { value: "100g", text: "100g", selected: true },
    { value: "200g", text: "200g" },
    { value: "300g", text: "300g" }
];

const participantOptions = [
    { value: "100g", text: "100g", selected: true },
    { value: "300g", text: "300g" },
    { value: "400g", text: "400g" },
    { value: "500g", text: "500g" }
];

document.addEventListener('DOMContentLoaded', function () {
    const participantQuantityModal = document.getElementById('participantQuantityModal');
    if (participantQuantityModal) {
        participantQuantityModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const modalStatus = button.getAttribute('data-bs-status');

            const participantOnlyRow = participantQuantityModal.querySelector('.participant-only');
            const quantityLabel = document.getElementById('quantityLabel');
            const amountLabel = document.getElementById('amountLabel');
            const normalBtnContainer = document.getElementById('normalButtonContainer');
            const participantBtnContainer = document.getElementById('participantButtonContainer');
            const quantitySelect = document.getElementById('quantitySelect');

            let optionsToUse = [];
            if (modalStatus === 'participant') {
                // Participant
                participantOnlyRow.classList.remove('d-none');
                quantityLabel.textContent = '수량 수정';
                amountLabel.textContent = '추가 결제 / 환불 금액';
                normalBtnContainer.classList.add('d-none');
                participantBtnContainer.classList.remove('d-none');
                optionsToUse = participantOptions;
            } else { // normal
                participantOnlyRow.classList.add('d-none');
                quantityLabel.textContent = '수량 선택';
                amountLabel.textContent = '결제 금액';
                normalBtnContainer.classList.remove('d-none');
                participantBtnContainer.classList.add('d-none');
                optionsToUse = normalOptions;
            }

            quantitySelect.innerHTML = '';
            optionsToUse.forEach(optionData => {
                const option = document.createElement('option');
                option.value = optionData.value;
                option.textContent = optionData.text;
                if (optionData.selected) {
                    option.selected = true;
                }
                quantitySelect.appendChild(option);
            });
        });
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const creatorActionModal = document.getElementById('creatorActionModal');

    if (creatorActionModal) {
        creatorActionModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const action = button.getAttribute('data-bs-action');

            const titleElement = document.getElementById('creatorActionTitle');
            const messageElement = document.getElementById('creatorActionMessage');
            const reasonContainer = document.getElementById('reasonInputContainer');
            const actionButton = document.getElementById('creatorActionButton');

            if (action === 'run') {
                // Run
                reasonContainer.classList.add('d-none');
                titleElement.innerHTML = `공동구매를 <span class="text-danger">지금</span> 진행하시겠습니까?`;
                messageElement.innerHTML = `남은 수량인 <span class="fw-bold text-decoration-underline">300g</span>은 개설자님께서 부담하게 됩니다.`;
                actionButton.textContent = '진행하기';
            } else if (action === 'stop') {
                // Stop
                reasonContainer.classList.remove('d-none');
                titleElement.innerHTML = `공동구매를 정말 <span class="text-danger">중단</span>하시겠습니까?`;
                messageElement.innerHTML = `300g만 더 참여하면 마감할 수 있습니다.`;
                actionButton.textContent = '중단하기';
            }
        });
    }
});

// === 초기화 ===
document.addEventListener('DOMContentLoaded', async function () {
    try {
        // 데이터 로드 및 렌더링
        const data = await api.getDetail();
        render.detail(data);

        // 버튼 표시 (status에 따라)
        showBtnByStatus(status);
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