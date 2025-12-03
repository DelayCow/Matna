document.addEventListener('DOMContentLoaded', function() {

    let isOwner = true; // 내 페이지인가 dkslsrk

    const memberNo = 17; // [테스트용] 로그인 기능 구현 후 세션값으로 대체 필요
    let currentGroupTab = 'participate';
    let currentFilterStatus = 'ALL';


    const getStatusStep = (status) => {

        const cleanStatus = String(status).trim().toUpperCase();


        switch (cleanStatus) {
            case 'OPEN':
            case 'RECRUITING':
                return 1;

            case 'CLOSED':
            case 'PAYMENT_WAIT':
                return 2;

            case 'PAID':
            case 'DELIVERED':
                return 3;

            case 'SHARED':
            case 'COMPLETED':
                return 4;

            case 'CANCELED':
                return 0;

            default:
                return 1;
        }
    };

    const getButtonConfig = (status) => {
        if (status === 'OPEN') return { text: "참여 취소", cls: "btn-outline-danger" };
        if (status === 'PAID') return { text: "배송 조회", cls: "btn-outline-primary" };
        if (status === 'DELIVERED') return { text: "수령 확인", cls: "btn-success" };
        return { text: "상세 보기", cls: "btn-outline-secondary" };
    };


    const renderCommonArea = (data) => {
        const headerArea = document.getElementById('header-right-area');
        const profileArea = document.getElementById('profile-main-area');
        const btnsArea = document.getElementById('profile-action-btns');

        if (!data) data = {};

        const nickname = data.nickname || "맛도리 회원님";
        const image = data.imageUrl || "/img/default_profile.jpg";
        const money = data.points || 0;
        const profileMemberNo = data.memberNo;

        // const isOwner = false; // [테스트용] 일단 내 페이지라고 가정

        if (isOwner && headerArea) {
            headerArea.innerHTML = `<button class="btn p-0 border-0" id="headerMenuBtn"><i class="bi bi-three-dots-vertical fs-4 text-dark"></i></button>
            <ul class="custom-dropdown" id="headerDropdown"><li><a href="#">정보 수정</a></li><li><a href="#">로그아웃</a></li><li><a href="#" class="text-danger">탈퇴</a></li></ul>`;
        } else if (headerArea) { headerArea.innerHTML = ''; }

        let subInfo = isOwner
            ? `<small class="text-muted">내 맛나머니 : ${money.toLocaleString()} 원</small>`
            : `<button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1"><i class="bi bi-exclamation-circle me-1"></i>신고하기</button>`;

        if(profileArea) {
            profileArea.innerHTML =
                `<img src="${image}" class="rounded-circle border me-3" width="60" height="60"><div><h5 class="fw-bold mb-1">${nickname}</h5><div>${subInfo}</div></div>`;
        }

        if(btnsArea) {
            btnsArea.innerHTML = isOwner ? '' : `
                <div class="d-flex gap-2">
                    <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">채팅</button>
                    <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">팔로우</button>
                </div>`;
        }
    };


    const createRecipeCard = (item) => {


        const imgUrl = item.image ? item.image : '/img/default_food.jpg';


        let difficultyKor = item.difficulty;
        if (item.difficulty === 'easy' || item.difficulty === '쉬움') difficultyKor = '쉬움';
        else if (item.difficulty === 'normal' || item.difficulty === '보통') difficultyKor = '보통';
        else if (item.difficulty === 'hard' || item.difficulty === '어려움') difficultyKor = '어려움';


        let spicyText = '';

        switch(item.spicy){
            case 0: spicyText = '안매워요'; break;
            case 1: spicyText = '약간매워요'; break;
            case 2: spicyText = '신라면맵기'; break;
            case 3: spicyText = '열라면맵기'; break;
            case 4: spicyText = '불닭맵기'; break;
            case 5: spicyText = '불닭보다매워요'; break;
            default: spicyText = '';
        }


        const editUrl = `/recipe/edit?id=${item.id}`;

        const kebabMenuHtml = (typeof isOwner !== 'undefined' && isOwner) ? `
        <div class="dropdown ms-auto">
            <button class="btn btn-link text-secondary p-0 border-0" type="button" data-bs-toggle="dropdown"><i class="bi bi-three-dots-vertical"></i></button>
            <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                <li><a class="dropdown-item small" href="${editUrl}">수정</a></li>
                <li><hr class="dropdown-divider my-1"></li>
                <li><button class="dropdown-item small text-danger btn-delete" data-id="${item.id}">삭제</button></li>
            </ul>
        </div>` : '';

        return `
    <div class="recipe-card mb-4 col-12" data-id="${item.id}">
        <div class="card-img-wrap">
            <img src="${imgUrl}" alt="${item.title}" onerror="this.src='/img/default_food.jpg'">
        </div>
        <div class="card-info mt-2 p-2">
            <h5 class="card-title">${item.title}</h5>
            <div class="d-flex align-items-center mb-2">
                <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                <span class="fw-bold me-1">${item.rating}</span>
                <span class="text-muted small">(${item.reviewCount || 0})</span>
                ${kebabMenuHtml}
            </div>
            <div class="d-flex flex-wrap gap-2 text-secondary" style="font-size: 0.8rem;">
                <span class="bg-light px-2 py-1 rounded-pill border"><i class="bi bi-clock me-1"></i>${item.time}</span>
                <span class="bg-light px-2 py-1 rounded-pill border"><i class="bi bi-bar-chart me-1"></i>${difficultyKor}</span>
                ${ spicyText ? `<span class="bg-danger-subtle text-danger px-2 py-1 rounded-pill border border-danger-subtle"><i class="bi bi-fire me-1"></i>${spicyText}</span>` : '' }
            </div>
        </div>
    </div>`;
    };

    const createReviewCard = (item) => {

        const imgUrl = item.imageUrl ? item.imageUrl : '/img/default_profile.jpg';

        return `
    <div class="review-card mb-4 col-12" data-id="${item.reviewNo}">
        <div class="card-img-wrap">
            <img src="${imgUrl}" alt="${item.title}" onerror="this.src='/img/default_profile.jpg'">
        </div>
        <div class="card-info mt-2 p-2">
            <h5 class="card-title">${item.title}</h5>
            <div class="d-flex align-items-center mb-2">
                <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                <span class="fw-bold me-1">${item.rating}</span>
            </div>
            </div>
    </div>`;
    };


    const createGroupCard = (item) => {

        console.log("단위 데이터 확인:", item.unit);
        const unit = item.unit || '';

        const currentStep = getStatusStep(item.status);

        const btnConfig = getButtonConfig(item.status);

        const steps = ["모집", "상품결제", "상품도착", "나눔진행"];
        let timelineHtml = '<div class="timeline-steps">';
        // 계산 식 다시 해야 함 아오
        steps.forEach((stepName, index) => {
            const stepNum = index + 1;
            let activeClass = "";
            if (stepNum < currentStep) activeClass = "active";
            else if (stepNum === currentStep) activeClass = "current";
            timelineHtml += `<div class="step-item ${activeClass}"><div class="step-circle"></div><span class="step-text">${stepName}</span></div>`;
        });
        timelineHtml += '</div>';



        const buttonHtml = isOwner
            ? `<button class="btn ${btnConfig.cls} btn-sm text-nowrap z-index-front" style="font-size: 0.75rem;">${btnConfig.text}</button>`
            : '';

        // (B) 상세 정보(수량, 가격 등): 주인이 아니면 안 보여줌
        const detailsHtml = isOwner
            ? `
            <div class="group-details">
                <span>신청 수량 <strong>${item.myQuantity}${unit}</strong></span>
           
                <div class="text-muted" style="font-size: 0.8rem;">나 외에 ${item.participantExMe}명 참여 중</div>
                ${ item.remainingQuantity > 0
                ? `<div class="text-primary fw-bold mt-1" style="font-size: 0.8rem;">남은 수량: ${item.remainingQuantity}${unit}</div>`
                : `<div class="text-secondary fw-bold mt-1" style="font-size: 0.8rem;">모집 완료</div>`
            }
            </div>`
            : '';


        return `
        <div class="group-card mb-3 p-3 border rounded bg-white shadow-sm" onclick="location.href='/groupBuy/detail?no=${item.groupBuyNo}'" style="cursor:pointer;">
        <div class="d-flex justify-content-between align-items-start mb-2">
            <div class="flex-grow-1 me-3">${timelineHtml}</div>
            
            ${buttonHtml} 
            
        </div>
        <div class="d-flex align-items-center gap-3">
            <div class="rounded overflow-hidden border" style="width: 80px; height: 80px; flex-shrink: 0;">
                <img src="${item.imageUrl || '/img/default_food.jpg'}" alt="${item.title}" class="w-100 h-100 object-fit-cover">
            </div>
            <div class="group-info flex-grow-1">
                <h5 class="fw-bold mb-1" style="font-size: 1rem;">${item.title}</h5>
                
                ${detailsHtml}
                
            </div>
        </div>
    </div>`;
    };


    function fetchProfileData(memberNo) {
        fetch(`/api/mypage/${memberNo}/profile`)
            .then(res => res.json())
            .then(data => {
                renderCommonArea(data);

            })
            .catch(err => console.error("프로필 로드 실패", err));
    }

    const fetchRecipeData = function(memberNo){
        return fetch(`/api/mypage/${memberNo}/recipe`,{ method: 'GET' })
            .then(response => response.json());
    };

    const fetchReviewData = function(memberNo) {
        return fetch(`/api/mypage/${memberNo}/reviewList`, { method: 'GET' })
            .then(response => response.json());
    };


    const fetchGroupData = async () => {
        const listEl = document.getElementById('group-list');
        const countEl = document.getElementById('statGroupCount');


        listEl.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success" role="status"></div></div>';

        try {

            const baseUrl = (currentGroupTab === 'participate')
                ? `/api/mypage/${memberNo}/groupBuy/participation`
                : `/api/mypage/${memberNo}/groupBuy/host`;

            const url = `${baseUrl}?filter=${currentFilterStatus}`;

            const response = await fetch(url);
            if (!response.ok) throw new Error("Network Error");
            const dataList = await response.json();

            if (!dataList || dataList.length === 0) {
                listEl.innerHTML = '<div class="text-center py-5 text-muted">내역이 없습니다.</div>';
                if(countEl) countEl.innerText = '0';
            } else {
                listEl.innerHTML = dataList.map(createGroupCard).join('');
                if(countEl) countEl.innerText = dataList.length;
            }

        } catch (error) {
            console.error(error);
            listEl.innerHTML = '<div class="text-center py-5 text-danger">데이터를 불러오지 못했습니다.</div>';
        }
    };


    const statTabRecipe = document.getElementById('statTabRecipe');
    const statTabGroup = document.getElementById('statTabGroup');
    const wrapRecipe = document.getElementById('recipe-section-wrapper');
    const wrapGroup = document.getElementById('group-section-wrapper');

    if(statTabRecipe) statTabRecipe.addEventListener('click', () => {
        statTabRecipe.classList.add('active'); statTabGroup.classList.remove('active');
        wrapRecipe.style.display = 'block'; wrapGroup.style.display = 'none';
    });
    if(statTabGroup) statTabGroup.addEventListener('click', () => {
        statTabGroup.classList.add('active'); statTabRecipe.classList.remove('active');
        wrapGroup.style.display = 'block'; wrapRecipe.style.display = 'none';
    });


    const btnParticipate = document.getElementById('btnParticipate');
    const btnOpen = document.getElementById('btnOpen');

    if(btnParticipate) {
        btnParticipate.addEventListener('change', () => {
            if(btnParticipate.checked) {
                currentGroupTab = 'participate';
                fetchGroupData(); // 데이터 다시 로드
            }
        });
    }
    if(btnOpen) {
        btnOpen.addEventListener('change', () => {
            if(btnOpen.checked) {
                currentGroupTab = 'host';
                fetchGroupData(); // 데이터 다시 로드
            }
        });
    }


    const statusFilterEl = document.getElementById('groupStatusFilter');
    if (statusFilterEl) {
        statusFilterEl.addEventListener('change', function(e) {
            currentFilterStatus = e.target.value; // ALL, OPEN, PAID ...
            fetchGroupData(); // 데이터 다시 로드
        });
    }


    document.addEventListener('click', (e) => {
        const btn = e.target.closest('#headerMenuBtn');
        const menu = document.getElementById('headerDropdown');
        if(btn && menu) { e.stopPropagation(); menu.classList.toggle('show'); }
        else if(menu) { menu.classList.remove('show'); }
    });

    const filterRecipe = document.getElementById('filterRecipe');
    const filterReview = document.getElementById('filterReview');
    const listRecipe = document.getElementById('recipe-list');
    const listReview = document.getElementById('review-list');

    // 레시피 목록 보여주는 함수
    const showRecipeList = () => {
        if(listRecipe) listRecipe.style.display = 'grid'; // 또는 'block' (CSS에 맞게)
        if(listReview) listReview.style.display = 'none';

        // 버튼 스타일 활성화 (선택사항: CSS에 .active가 있다면)
        if(filterRecipe) filterRecipe.classList.add('active');
        if(filterReview) filterReview.classList.remove('active');
    };

    // 후기 목록 보여주는 함수
    const showReviewList = () => {
        if(listReview) listReview.style.display = 'grid'; // 또는 'block'
        if(listRecipe) listRecipe.style.display = 'none';

        // 버튼 스타일 활성화
        if(filterReview) filterReview.classList.add('active');
        if(filterRecipe) filterRecipe.classList.remove('active');
    };

    // 이벤트 리스너 연결 (클릭 시 실행)
    if (filterRecipe) {
        filterRecipe.addEventListener('click', showRecipeList);
        // 만약 라디오 버튼(<input type="radio">)이라면 'change' 이벤트도 추가
        filterRecipe.addEventListener('change', () => { if(filterRecipe.checked) showRecipeList(); });
    }

    if (filterReview) {
        filterReview.addEventListener('click', showReviewList);
        // 만약 라디오 버튼이라면 'change' 이벤트도 추가
        filterReview.addEventListener('change', () => { if(filterReview.checked) showReviewList(); });
    }

    renderCommonArea();
    fetchProfileData(memberNo);

    // 레시피 로드
    fetchRecipeData(memberNo).then(recipeData => {
        const listEl = document.getElementById('recipe-list');
        const countEl = document.getElementById('statRecipeCount');
        if(recipeData && listEl) {
            listEl.innerHTML = recipeData.map(createRecipeCard).join('');
            if(countEl) countEl.innerText = recipeData.length;
        }
    }).catch(err => console.error(err));

    // 후기 로드
    fetchReviewData(memberNo).then(reviewList => {
        const listContainer = document.getElementById('review-list');
        if(listContainer) {
            if (!reviewList || reviewList.length === 0) {
                listContainer.innerHTML = '<div class="text-center w-100 py-5 text-muted">작성한 후기가 없습니다.</div>';
            } else {
                listContainer.innerHTML = reviewList.map(item => createReviewCard(item)).join('');
            }
        }
    }).catch(err => console.error(err));

    // 공동구매 리스트 초기 로드
    fetchGroupData();
});