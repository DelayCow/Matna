document.addEventListener('DOMContentLoaded', function() {
    // const isMyPage = true;

    // const userData = {
    //     name: "베베는오리",
    //     image: "/img/user1.png",
    //     money: 5600,
    //     isOwner: isMyPage
    // };


    let isOwner = false; // "내 페이지인가?" 상태 저장용
    const memberNo = 17;

    fetchProfileData(memberNo);


    // 1. 레시피 데이터
    // const recipeData = [
    //     { id: 1, title: "폭탄계란찜", image: "/img/steamedeggs.jpg", rating: 5, reviewCount: 8, serving: 1, time: "10분", difficulty: "쉬움", spicy: "안 매워요" },
    //     { id: 2, title: "라비올리", image: "/img/ravioli.jpg", rating: 4.5, reviewCount: 14, serving: 1, time: "15분", difficulty: "중급", spicy: "약간매워요" },
    //     { id: 3, title: "수제버거", image: "/img/hambugi.jpg", rating: 5.0, reviewCount: 19, serving: 1, time: "30분", difficulty: "상급", spicy: "안 매워요" },
    //     { id: 4, title: "피쉬앤칩스", image: "/img/fishAndChips.jpg", rating: 4.0, reviewCount: 6, serving: 2, time: "20분", difficulty: "중급", spicy: "안 매워요" },
    //     { id: 5, title: "미역국", image: "/img/miyuckguck.jpg", rating: 3.0, reviewCount: 10, serving: 1, time: "10분", difficulty: "중급", spicy: "완젼 매워요" },
    //     { id: 6, title: "코코뱅", image: "/img/cokkioo.jpg", rating: 3.5, reviewCount: 11, serving: 2, time: "40분", difficulty: "중급", spicy: "매워요" }
    // ];

    function fetchProfileData(memberNo) {
        fetch(`/api/mypage/${memberNo}/profile`)
            .then(res => res.json())
            .then(data => {

                if (data.nickname != null && data.nickname != "") { // 값이 진짜 있는지 확인
                    currentNickname = data.nickname;
                }
                renderCommonArea(data);

                renderGroupList('')
            });
    }

    const fetchRecipeData = function(memberNo){
        return fetch(`/api/mypage/${memberNo}/recipe`,{
            method: 'GET'
        }).then(response => {
            return response.json();
        });
    };

    // 2. 후기 데이터
    const reviewData = [
        { id: 101, title: "인생 버거 등극!", image: "/img/hambugiReview.jpg", rating: 4.0, spicy: "안 매워요" },
        { id: 201, title: "와인 풍미 예술", image: "/img/cokkioo.jpg", rating: 3.5, spicy: "매워요" }
    ];

    // 3. 공동구매 데이터 (상세 필드 추가됨!)
    const groupData = [
        {
            id: 501,
            title: "표고버섯 500g",
            image: "/img/mushroom.jpg",
            statusStep: 1, // 1:모집
            myAmount: "50g",
            price: 650,
            participants: 2,
            remain: "50g",
            btnText: "참여 취소하기",
            btnClass: "btn-danger",
            owner: "농부김씨",
            join_users: ["베베는오리", "철수"]
        },
        {
            id: 502,
            title: "스테비아 방울토마토 1,000g",
            image: "/img/tomato.jpg",
            statusStep: 2, // 2:결제
            myAmount: "200g",
            price: 2100,
            participants: 3,
            remain: null,
            btnText: "결제정보 확인",
            btnClass: "btn-success",
            owner: "토마토농장",
            join_users: ["베베는오리", "영희"]
        },
        {
            id: 503,
            title: "햇 통마늘 1,000g",
            image: "/img/garlic.jpg",
            statusStep: 3, // 3:도착
            myAmount: "300g",
            price: 2400,
            participants: 1,
            remain: null,
            btnText: "참여자 관리",
            btnClass: "btn-dark",
            owner: "베베는오리", // 내가 개설함
            join_users: ["철수"]
        }
    ];

    // 1. 헤더 & 프로필
    const renderCommonArea = (data) => {


        const headerArea = document.getElementById('header-right-area');
        const profileArea = document.getElementById('profile-main-area');
        const btnsArea = document.getElementById('profile-action-btns');


        if (!data) data = {};

        const nickname = data.nickname || "맛도리 회원님";
        const image = data.imageUrl || "기본이미지url"; // 기본이미지 연결 할 것
        const money = data.points || 0;

        const profileMemberNo = data.memberNo;

        const isOwner = (profileMemberNo && myMemberNo === profileMemberNo);

        if (isOwner) {
            headerArea.innerHTML = `<button class="btn p-0 border-0" id="headerMenuBtn"><i class="bi bi-three-dots-vertical fs-4 text-dark"></i></button>
            <ul class="custom-dropdown" id="headerDropdown"><li><a href="#">정보 수정</a></li><li><a href="#">로그아웃</a></li><li><a href="#" class="text-danger">탈퇴</a></li></ul>`;
        } else { headerArea.innerHTML = ''; }

        let subInfo = isOwner
            ? `<small class="text-muted">내 맛나머니 : ${money.toLocaleString()} 원</small>`
            : `<button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1"><i class="bi bi-exclamation-circle me-1"></i>신고하기</button>`;

        profileArea.innerHTML =
            `<img src="${image}" class="rounded-circle border me-3" width="60" height="60"><div><h5 class="fw-bold mb-1">${nickname}</h5><div>${subInfo}</div></div>`;

        btnsArea.innerHTML = isOwner ? '' : `
            <div class="d-flex gap-2">
                <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">채팅</button>
                <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">팔로우</button>
            </div>`;
    };

    // 2. 통계 업데이트
    const updateStats = () => {
        // document.getElementById('statRecipeCount').innerText = recipeData.length;
        document.getElementById('statGroupCount').innerText = groupData.length;
    };

    // 3. 레시피 카드 생성
    const createRecipeCard = (item) => {
        const editUrl = `/recipe/edit?id=${item.id}`;
        switch(item.difficulty){
            case 'easy':
                item.difficulty = '쉬움';
                break;
            case 'normal':
                item.difficulty = '보통';
                break;
            case 'hard':
                item.difficulty = '어려움';
        }
        switch(item.spicyLevel){
            case 0:
                item.spicyLevel = '안매워요';
                break;
            case 1:
                item.spicyLevel = '약간매워요';
                break;
            case 2:
                item.spicyLevel = '신라면맵기';
                break;
            case 3:
                item.spicyLevel = '열라면맵기';
                break;
            case 4:
                item.spicyLevel = '불닭맵기';
                break;
            case 5:
                item.spicyLevel = '불닭보다매워요';
                break;
        }
        const kebabMenuHtml = isOwner ? `
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
            <div class="card-img-wrap"><img src="${item.image}" alt="${item.title}"></div>
            <div class="card-info mt-2 p-2">
                <h5 class="card-title">${item.title}</h5>
                <div class="d-flex align-items-center mb-2">
                    <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                    <span class="fw-bold me-1">${item.rating}</span>
                    <span class="text-muted small">(${item.reviewCount})</span>
                    ${kebabMenuHtml}
                </div>
                <div class="d-flex flex-wrap gap-2 text-secondary" style="font-size: 0.8rem;">
                    <span class="bg-light px-2 py-1 rounded-pill border"><i class="bi bi-clock me-1"></i>${item.time}</span>
                    <span class="bg-light px-2 py-1 rounded-pill border"><i class="bi bi-bar-chart me-1"></i>${item.difficulty}</span>
                    ${ item.spicy ? `<span class="bg-danger-subtle text-danger px-2 py-1 rounded-pill border border-danger-subtle"><i class="bi bi-fire me-1"></i>${item.spicy}</span>` : '' }
                </div>
            </div>
        </div>`;
    };

    // 4. 후기 카드 생성
    const createReviewCard = (item) => `
        <div class="review-card mb-4 col-12" data-id="${item.id}">
            <div class="card-img-wrap"><img src="${item.image}" alt="${item.title}"></div>
            <div class="card-info mt-2 p-2">
                <h5 class="card-title">${item.title}</h5>
                <div class="d-flex align-items-center mb-2">
                    <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                    <span class="fw-bold me-1">${item.rating}</span>
                </div>
                <div class="small text-danger"><i class="bi bi-fire me-1"></i>${item.spicy}</div>
            </div>
        </div>`;

    // 5. 공동구매 카드 생성 (타임라인 포함!)
    const createGroupCard = (item) => {
        // 타임라인 생성
        const steps = ["모집", "상품결제", "상품도착", "나눔진행"];
        let timelineHtml = '<div class="timeline-steps">';
        steps.forEach((stepName, index) => {
            const stepNum = index + 1;
            let activeClass = "";
            if (stepNum < item.statusStep) activeClass = "active";
            else if (stepNum === item.statusStep) activeClass = "current";

            timelineHtml += `
                <div class="step-item ${activeClass}">
                    <div class="step-circle"></div>
                    <span class="step-text">${stepName}</span>
                </div>`;
        });
        timelineHtml += '</div>';

        return `
        <div class="group-card mb-3 p-3 border rounded bg-white shadow-sm" data-id="${item.id}">
            <div class="d-flex justify-content-between align-items-start mb-2">
                <div class="flex-grow-1 me-3">${timelineHtml}</div>
                <button class="btn ${item.btnClass} btn-sm text-nowrap" style="font-size: 0.75rem;">${item.btnText}</button>
            </div>
            <div class="d-flex align-items-center gap-3">
                <div class="rounded overflow-hidden border" style="width: 80px; height: 80px; flex-shrink: 0;">
                    <img src="${item.image}" alt="${item.title}" class="w-100 h-100 object-fit-cover">
                </div>
                <div class="group-info flex-grow-1">
                    <h5 class="fw-bold mb-1" style="font-size: 1rem;">${item.title}</h5>
                    <div class="group-details">
                        <span>내가 가져갈 양 <strong>${item.myAmount}</strong></span>
                        <span class="price-highlight ms-1">${item.price.toLocaleString()}원</span>
                        <div class="text-muted" style="font-size: 0.8rem;">본인 제외 ${item.participants}명 참여중</div>
                        ${ item.remain ? `<div class="text-danger fw-bold mt-1" style="font-size: 0.8rem;">공동구매모집<br>남은 수량: ${item.remain}</div>` : '' }
                    </div>
                </div>
            </div>
        </div>`;
    };

    // 6. 공동구매 필터링 로직
    const renderGroupList = (filterType) => {
        const listEl = document.getElementById('group-list');
        let filtered = [];
        if (filterType === 'participate') {
            filtered = groupData.filter(item => item.join_users.includes(name));
        } else {
            filtered = groupData.filter(item => item.owner === name);
        }
        if (filtered.length === 0) listEl.innerHTML = '<div class="text-center py-5 text-muted">내역이 없습니다.</div>';
        else listEl.innerHTML = filtered.map(createGroupCard).join('');
    };

    const statTabRecipe = document.getElementById('statTabRecipe');
    const statTabGroup = document.getElementById('statTabGroup');
    const wrapRecipe = document.getElementById('recipe-section-wrapper');
    const wrapGroup = document.getElementById('group-section-wrapper');

    // 상단 탭
    if(statTabRecipe) statTabRecipe.addEventListener('click', () => {
        statTabRecipe.classList.add('active'); statTabGroup.classList.remove('active');
        wrapRecipe.style.display = 'block'; wrapGroup.style.display = 'none';
    });
    if(statTabGroup) statTabGroup.addEventListener('click', () => {
        statTabGroup.classList.add('active'); statTabRecipe.classList.remove('active');
        wrapGroup.style.display = 'block'; wrapRecipe.style.display = 'none';
    });

    // 하단 필터 (레시피/후기)
    const filterRecipe = document.getElementById('filterRecipe');
    const filterReview = document.getElementById('filterReview');
    const listRecipe = document.getElementById('recipe-list');
    const listReview = document.getElementById('review-list');

    if(filterRecipe) filterRecipe.addEventListener('change', () => { if(filterRecipe.checked) { listRecipe.style.display = 'grid'; listReview.style.display = 'none'; }});
    if(filterReview) filterReview.addEventListener('change', () => { if(filterReview.checked) { listReview.style.display = 'grid'; listRecipe.style.display = 'none'; }});

    // 공동구매 필터 (참여/개설)
    const btnParticipate = document.getElementById('btnParticipate');
    const btnOpen = document.getElementById('btnOpen');

    if(btnParticipate) btnParticipate.addEventListener('change', () => { if(btnParticipate.checked) renderGroupList('participate'); });
    if(btnOpen) btnOpen.addEventListener('change', () => { if(btnOpen.checked) renderGroupList('owner'); });

    renderCommonArea();
    updateStats();
    fetchRecipeData(15).then(recipeData => {
        document.getElementById('statRecipeCount').innerText = recipeData.length;
        document.getElementById('recipe-list').innerHTML = recipeData.map(createRecipeCard).join('');
    })
    // document.getElementById('recipe-list').innerHTML = recipeData.map(createRecipeCard).join('');
    document.getElementById('review-list').innerHTML = reviewData.map(createReviewCard).join('');

    // 공동구매는 '참여' 탭을 기본으로 보여줌
    renderGroupList('participate');

    // 드롭다운 토글
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('#headerMenuBtn');
        const menu = document.getElementById('headerDropdown');
        if(btn && menu) { e.stopPropagation(); menu.classList.toggle('show'); }
        else if(menu) { menu.classList.remove('show'); }
    });
});