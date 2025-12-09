// === 전역 상태 ===
let currentKeyword = '';
let currentOrderBy = 'recent';

// === API 호출 ===
const api = {
    // 기간공구 목록 조회
    getPeriodGroupBuyList: (keyword, orderBy) => {
        let url = '/api/periodGroupBuy/home';
        const params = new URLSearchParams();

        if (keyword && keyword.trim()) {
            params.append('keyword', keyword);
        }
        if (orderBy && orderBy !== 'recent') {
            params.append('orderBy', orderBy);
        }

        if (params.toString()) {
            url += '?' + params.toString();
        }

        return fetch(url, {
            method: 'GET'
        }).then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        });
    },
    // 수량공구 목록 조회 (새로 추가한다고 가정)
    getQuantityGroupBuyList: (keyword, orderBy) => {
        let url = '/api/quantityGroupBuy/home';
        const params = new URLSearchParams();

        if (keyword && keyword.trim()) {
            params.append('keyword', keyword);
        }
        if (orderBy && orderBy !== 'recent') {
            params.append('orderBy', orderBy);
        }

        if (params.toString()) {
            url += '?' + params.toString();
        }

        return fetch(url, {
            method: 'GET'
        }).then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        });
    }
};

// === 데이터 변환(유틸리티) 함수 ===
const utils = {
    // 주소를 구 단위로 축약
    formatAddress: (fullAddress) => {
        if (!fullAddress) return '주소 정보 없음';

        const addressParts = fullAddress.split(' ');
        const district = addressParts.find(part => part.endsWith('구'));

        if (district) {
            return district;
        }
        return addressParts.length > 1 ? addressParts[1] : '주소 오류';
    },

    // 남은 시간 계산 (실시간 카운트다운)
    startCountdown: (timerElement) => {
        const dueDateString = timerElement.dataset.dueDate;
        if (!dueDateString) {
            timerElement.innerHTML = '마감일 정보 없음';
            return;
        }

        const targetDate = new Date(dueDateString);

        const updateCountdown = () => {
            const now = new Date().getTime();
            const distance = targetDate.getTime() - now;

            if (distance <= 0) {
                clearInterval(timerInterval);
                timerElement.innerHTML = '모집마감';
                timerElement.classList.add('text-muted');
                timerElement.classList.remove('text-danger');
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
        남은 기간 : 
        ${days}일 
        ${String(hours).padStart(2, '0')}시간 
        ${String(minutes).padStart(2, '0')}분 
        ${String(seconds).padStart(2, '0')}초
      `;
        };

        const timerInterval = setInterval(updateCountdown, 1000);
        updateCountdown();
    }
};

// === 카드 생성 함수 ===
const card = {
    // 기간공구 카드 생성
    createPeriodCard: (item) => {
        const address = utils.formatAddress(item.shareLocation);
        const creatorImageUrl = item.creatorImageUrl || '/img/user.png';
        const groupBuyImageUrl = item.groupBuyImageUrl || '/img/placeholder.jpg';
        const nickname = item.nickname || '익명';
        const title = item.title || '제목 없음';
        const minPrice = item.minPricePerPerson ? item.minPricePerPerson.toLocaleString() : '0';
        const maxPrice = item.maxPricePerPerson ? item.maxPricePerPerson.toLocaleString() : '0';
        const participants = item.participants || 0;
        const maxParticipants = item.maxParticipants || 0;
        const groupBuyNo = item.groupBuyNo || 0;
        const periodGroupBuyNo = item.periodGroupBuyNo || 0;

        return `
      <div class="col d-flex justify-content-center">
        <div class="card card-custom card-wide" data-type="period" data-no="${periodGroupBuyNo}" style="cursor: pointer;">
          <img src="${groupBuyImageUrl}" class="card-img-top" alt="${title}"
               onerror="this.onerror=null; this.src='/img/placeholder.jpg';">
          <div class="card-body px-0 py-2">
            <div class="d-flex align-items-center mb-1">
              <img src="${creatorImageUrl}" class="profile-img" alt="User" 
                   style="width: 30px; height: 30px; border-radius: 50%; object-fit: cover;"
                   onerror="this.onerror=null; this.src='/img/user.png';">
              <div class="overflow-hidden w-100 ms-2">
                <div class="d-flex overflow-hidden w-100">
                  <small class="fw-bold text-nowrap">${nickname}</small>
                  <p class="card-text text-truncate mb-0 ms-2">${title}</p>
                </div>
                <small class="text-danger d-block mb-1 countdown-timer" data-due-date="${item.dueDate}">
                  남은 기간 : 계산 중...
                </small>
              </div>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-bold">
                ${minPrice}~${maxPrice}원 
                <small class="text-muted fw-normal">
                  (<span class="text-danger">${participants}</span>/${maxParticipants})
                </small>
              </span>
              <span class="badge badge-location">${address}</span>
            </div>
          </div>
        </div>
      </div>
    `;
    },

    // 수량공구 카드 생성 (함수 이름 수정 및 내용 업데이트)
    createQuantityCard: (item) => {
        const address = utils.formatAddress(item.shareLocation);
        const creatorImageUrl = item.creatorImageUrl || '/img/user.png';
        const groupBuyImageUrl = item.groupBuyImageUrl || '/img/placeholder.jpg';
        const nickname = item.nickname || '익명';
        const title = item.title || '제목 없음';
        const quantity = item.quantity || 0;
        const remainingQty = item.remainingQty || 0;
        const unit = item.unit || 'g';
        const shareAmount = item.shareAmount || 0;
        const pricePerUnit = item.pricePerUnit ? item.pricePerUnit.toLocaleString() : '0';

        // 수량공구 고유 번호 사용
        const quantityGroupBuyNo = item.quantityGroupBuyNo || 0;

        // 남은 수량 계산
        const totalQty = quantity ? quantity.toLocaleString() : '0';
        const remaining = remainingQty ? remainingQty.toLocaleString() : '0';


        return `
      <div class="col d-flex justify-content-center">
        <!-- data-type과 data-no에 수량공구 정보 사용 -->
        <div class="card card-custom card-wide" data-type="quantity" data-no="${quantityGroupBuyNo}" style="cursor: pointer;">
          <img src="${groupBuyImageUrl}" class="card-img-top" alt="${title}"
               onerror="this.onerror=null; this.src='/img/placeholder.jpg';">
          <div class="card-body px-0 py-2">
            <div class="d-flex align-items-center mb-1">
              <img src="${creatorImageUrl}" class="profile-img" alt="User" 
                   style="width: 30px; height: 30px; border-radius: 50%; object-fit: cover;"
                   onerror="this.onerror=null; this.src='/img/user.png';">
              <div class="overflow-hidden w-100 ms-2">
                <div class="d-flex overflow-hidden w-100">
                  <small class="fw-bold text-nowrap">${nickname}</small>
                  <p class="card-text text-truncate mb-0 ms-2">${title}</p>
                </div>
                <!-- 수량공구는 마감일이 아닌 남은 수량을 강조 -->
                <small class="text-danger d-block mb-1 fw-bold">
                  남은 수량 : ${remaining} ${unit} / ${totalQty} ${unit}
                </small>
              </div>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-bold">
                ${pricePerUnit}원/${shareAmount}${unit} 
              </span>
              <span class="badge badge-location">${address}</span>
            </div>
          </div>
        </div>
      </div>
    `;
    }
};



// === 렌더링 함수 ===
const render = {
    // 기간공구 목록 렌더링
    periodGroupBuyList: (items) => {
        const container = document.getElementById('periodBuyContainer');
        if (!container) return;

        container.innerHTML = '';

        if (!items || items.length === 0) {
            container.innerHTML = `
        <div class="col-12 text-center py-5">
          <p class="text-muted">검색 결과가 없습니다.</p>
        </div>
      `;
            return;
        }

        items.forEach(item => {
            container.insertAdjacentHTML('beforeend', card.createPeriodCard(item));
        });

        // 카운트다운 타이머 시작
        const timers = container.querySelectorAll('.countdown-timer');
        timers.forEach(timer => utils.startCountdown(timer));

        // 카드 클릭 이벤트 추가
        const cards = container.querySelectorAll('.card[data-type="period"]');
        cards.forEach(cardEl => {
            cardEl.addEventListener('click', function() {
                const periodGroupBuyNo = this.dataset.no;
                if (periodGroupBuyNo) {
                    window.location.href = `/periodGroupBuy/detail/${periodGroupBuyNo}`;
                }
            });
        });
    },

    // 수량공구 목록 렌더링
    quantityGroupBuyList: (items) => {
        const container = document.getElementById('quantityBuyContainer'); // 별도의 컨테이너 가정
        if (!container) return;

        container.innerHTML = '';

        if (!items || items.length === 0) {
            container.innerHTML = `
                <div class="col-12 text-center py-5">
                    <p class="text-muted">검색 결과가 없습니다.</p>
                </div>
            `;
            return;
        }

        items.forEach(item => {
            container.insertAdjacentHTML('beforeend', card.createQuantityCard(item));
        });

        // 수량공구 카드 클릭 이벤트 추가
        const cards = container.querySelectorAll('.card[data-type="quantity"]');
        cards.forEach(cardEl => {
            cardEl.addEventListener('click', function() {
                const quantityGroupBuyNo = this.dataset.no;
                if (quantityGroupBuyNo) {
                    window.location.href = `/quantityGroupBuy/detail/${quantityGroupBuyNo}`; // URL 변경 가정
                }
            });
        });
    }
};

// 기간공구 로드 함수
async function loadPeriodGroupBuyList() {
    try {
        const data = await api.getPeriodGroupBuyList(currentKeyword, currentOrderBy);
        render.periodGroupBuyList(data);
    } catch (error) {
        console.error('기간공구 데이터 로드 중 오류 발생:', error);

        const container = document.getElementById('periodBuyContainer');
        if (container) {
            container.innerHTML = `
        <div class="col-12 text-center py-5">
          <p class="text-danger">기간공구 데이터를 불러오는 중 오류가 발생했습니다.</p>
        </div>
      `;
        }
    }
}

// 수량공구 로드
async function loadQuantityGroupBuyList() {
    try {
        const data = await api.getQuantityGroupBuyList(currentKeyword, currentOrderBy);
        render.quantityGroupBuyList(data);
    } catch (error) {
        console.error('수량공구 데이터 로드 중 오류 발생:', error);

        const container = document.getElementById('quantityBuyContainer');
        if (container) {
            container.innerHTML = `
                <div class="col-12 text-center py-5">
                    <p class="text-danger">수량공구 데이터를 불러오는 중 오류가 발생했습니다.</p>
                </div>
            `;
        }
    }
}

// === 이벤트 핸들러 ===
function setupEventHandlers() {
    // 검색 입력
    const searchInput = document.getElementById('search-input');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                currentKeyword = this.value.trim();
                // 활성화된 탭에 따라 로드 함수 호출
                const activeTab = document.querySelector('.nav-link.active');
                if (activeTab && activeTab.id === 'quantity-tab') {
                    loadQuantityGroupBuyList();
                } else {
                    loadPeriodGroupBuyList();
                }
            }
        });
    }

    // 정렬 드롭다운
    const sortDropdownItems = document.querySelectorAll('.sort-dropdown .dropdown-item');
    const sortButton = document.querySelector('.sort-dropdown .btn');

    sortDropdownItems.forEach(item => {
        item.addEventListener('click', function(e) {
            e.preventDefault();

            sortDropdownItems.forEach(i => i.classList.remove('active'));
            this.classList.add('active');

            if (sortButton) {
                sortButton.textContent = this.textContent;
            }

            const sortType = this.getAttribute('data-sort');
            if (sortType) {
                currentOrderBy = sortType;
                // 활성화된 탭에 따라 로드 함수 호출
                const activeTab = document.querySelector('.gb-nav-link.active');
                if (activeTab && activeTab.id === 'quantity-tab') {
                    loadQuantityGroupBuyList();
                } else {
                    loadPeriodGroupBuyList();
                }
            }
        });
    });

    // 편집 박스 토글
    const editBox = document.querySelector('#edit-box');
    const editBoxContent = document.querySelector('.edit-box');

    if (editBox && editBoxContent) {
        editBox.addEventListener('click', function(e) {
            e.stopPropagation();
            editBoxContent.classList.toggle('show');
        });

        document.addEventListener('click', function() {
            if (editBoxContent.classList.contains('show')) {
                editBoxContent.classList.remove('show');
            }
        });
    }

    // 탭 전환 이벤트
    const periodTab = document.getElementById('main-tab');
    if (periodTab) {
        periodTab.addEventListener('shown.bs.tab', function() {
            // 기간공구 탭으로 전환시 데이터 새로고침
            loadPeriodGroupBuyList();
        });
    }

    // 수량공구 탭 전환 이벤트
    const quantityTab = document.getElementById('quantity-tab');
    if (quantityTab) {
        quantityTab.addEventListener('shown.bs.tab', function() {
            // 수량공구 탭으로 전환시 데이터 새로고침
            loadQuantityGroupBuyList();
        });
    }
}

// === 초기화 ===
document.addEventListener('DOMContentLoaded', function() {
    // 이벤트 핸들러 설정
    setupEventHandlers();

    // 초기 데이터 로드 (활성화된 탭에 따라)
    const activeTab = document.querySelector('.nav-link.active');
    if (activeTab && activeTab.id === 'quantity-tab') {
        loadQuantityGroupBuyList();
    } else {
        loadPeriodGroupBuyList();
    }
});
