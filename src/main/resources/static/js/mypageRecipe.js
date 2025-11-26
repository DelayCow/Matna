document.addEventListener('DOMContentLoaded', function() {

    // ==================================================================
    // [STEP 1] 화면 모드 설정 (테스트할 때 여기만 바꾸세요!)
    // ==================================================================

    // true  => [내 페이지 모드]: 메뉴 보임, 돈 보임, 수정/삭제 가능
    // false => [남 페이지 모드]: 메뉴 숨김, 신고/채팅 보임
    const isMyPage = true;


    // ==================================================================
    // [STEP 2] 사용자 데이터 정의 (상황에 따라 바뀌는 데이터)
    // ==================================================================
    const userData = {
        name: "베베는오리",
        image: "../../static/img/user1.png",
        money: 5600,
        isOwner: isMyPage // 위에서 설정한 모드를 따름
    };


    // ==================================================================
    // [STEP 3] 화면 그리기 함수들
    // ==================================================================

    // 1. 헤더 메뉴 (점 3개) 렌더링
    const RenderHeader = () => {
        const headerArea = document.getElementById('header-right-area');
        if (!headerArea) return;

        // 내 페이지일 때만 메뉴 버튼을 HTML에 집어넣음
        if (userData.isOwner) {
            headerArea.innerHTML = `
                <button class="btn p-0 border-0" id="headerMenuBtn">
                    <i class="bi bi-three-dots-vertical fs-4 text-dark"></i>
                </button>
                <ul class="custom-dropdown" id="headerDropdown">
                    <li><a href="#">내 정보 수정</a></li>
                    <li><a href="#">로그아웃</a></li>
                    <li><a href="#" class="text-danger">회원 탈퇴</a></li>
                </ul>
            `;
        } else {
            // 남의 페이지면 깨끗하게 비움
            headerArea.innerHTML = '';
        }
    };

    // 2. 프로필 메인 정보 (닉네임, 돈 vs 신고) 렌더링
    const RenderProfileMain = () => {
        const profileArea = document.getElementById('profile-main-area');
        if (!profileArea) return;

        // 내꺼면 돈, 남꺼면 신고버튼
        let subInfoHtml = '';
        if (userData.isOwner) {
            subInfoHtml = `<small class="text-muted">내 맛나머니 : ${userData.money.toLocaleString()} 원</small>`;
        } else {
            subInfoHtml = `
                <button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1" style="font-size: 0.75rem;">
                    <i class="bi bi-exclamation-circle me-1"></i>신고하기
                </button>
            `;
        }

        // HTML 조립
        profileArea.innerHTML = `
            <img src="${userData.image}" alt="프로필" class="rounded-circle border me-3" width="60" height="60">
            <div>
                <h5 class="fw-bold mb-1">${userData.name}</h5>
                <div id="profile-sub-text">
                    ${subInfoHtml}
                </div>
            </div>
        `;
    };

    // 3. 하단 버튼 (채팅/팔로우) 렌더링
    const RenderActionBtns = () => {
        const btnsArea = document.getElementById('profile-action-btns');
        if (!btnsArea) return;

        if (userData.isOwner) {
            btnsArea.innerHTML = ''; // 내꺼면 버튼 없음
        } else {
            btnsArea.innerHTML = `
                <div class="d-flex gap-2">
                    <button class="btn btn-success flex-grow-1 fw-bold text-white shadow-sm py-2" style="background-color: #6CC537; border: none;">
                        채팅 보내기
                    </button>
                    <button class="btn btn-success flex-grow-1 fw-bold text-white shadow-sm py-2" style="background-color: #6CC537; border: none;">
                        팔로우하기
                    </button>
                </div>
            `;
        }
    };


    // ==================================================================
    // [STEP 4] 렌더링 실행 (함수 호출)
    // ==================================================================
    RenderHeader();
    RenderProfileMain();
    RenderActionBtns();


    // ==================================================================
    // [STEP 5] 이벤트 리스너 (헤더 드롭다운 기능)
    // ==================================================================
    // 동적으로 생성된 요소이므로 document에 이벤트를 걸어 위임합니다.
    document.addEventListener('click', function(e) {
        // 클릭된 요소가 헤더 메뉴 버튼인지 확인
        const menuBtn = e.target.closest('#headerMenuBtn');
        const dropdown = document.getElementById('headerDropdown');

        // 1. 메뉴 버튼 클릭 시 드롭다운 열기/닫기
        if (menuBtn && dropdown) {
            e.stopPropagation(); // 이벤트가 퍼지는 것을 막음
            dropdown.classList.toggle('show');
            return;
        }

        // 2. 그 외 다른 곳 클릭 시 드롭다운 닫기
        if (dropdown && dropdown.classList.contains('show')) {
            dropdown.classList.remove('show');
        }
    });



    // db 값이랑 하드 코딩 값이랑 타입이 다름 나중에 맞춰 줘
    const recipeData = [
        { id: 1, title: "폭탄계란찜", image: "../../static/img/steamedeggs.jpg", rating: 5, reviewCount: 8, serving: 1, time: "10분", difficulty: "쉬움", spicy: "안 매워요" },
        { id: 2, title: "라비올리", image: "../../static/img/ravioli.jpg", rating: 4.5, reviewCount: 14, serving: 1, time: "15분", difficulty: "중급", spicy: "약간매워요" },
        { id: 3, title: "수제버거", image: "../../static/img/hambugi.jpg", rating: 5.0, reviewCount: 19, serving: 1, time: "30분", difficulty: "상급", spicy: "안 매워요" },
        { id: 4, title: "피쉬앤칩스", image: "../../static/img/fishAndChips.jpg", rating: 4.0, reviewCount: 6, serving: 2, time: "20분", difficulty: "중급", spicy: "안 매워요" },
        { id: 5, title: "미역국", image: "../../static/img/miyuckguck.jpg", rating: 3.0, reviewCount: 10, serving: 1, time: "10분", difficulty: "중급", spicy: "완젼 매워요" },
        { id: 6, title: "코코뱅", image: "../../static/img/cokkioo.jpg", rating: 3.5, reviewCount: 11, serving: 2, time: "40분", difficulty: "중급", spicy: "매워요" }
    ];

    const  CreateRecipeCard = (recipe) => {
        const imgSrc = recipe.image;
        const editUrl = `/recipe/edit?id=${recipe.id}`;

        // ★ 레시피 카드 위 케밥 메뉴도 'isOwner'가 true일 때만 생성
        const kebabMenuHtml = userData.isOwner ? `
            <div class="dropdown ms-auto">
                <button class="btn btn-link text-secondary p-0 border-0 text-decoration-none dropdown-toggle" type="button" data-bs-toggle="dropdown">
                    <i class="bi bi-three-dots-vertical"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                    <li><a class="dropdown-item small" href="${editUrl}">수정</a></li>
                    <li><hr class="dropdown-divider my-1"></li>
                    <li><button class="dropdown-item small text-danger btn-delete" data-id="${recipe.id}">삭제</button></li>
                </ul>
            </div>
        ` : '';

        return `
            <div class="recipe-card mb-4 col-12" data-id="${recipe.id}">
                <div class="card-img-wrap overflow-hidden rounded shadow-sm">
                    <img src="${imgSrc}" alt="${recipe.title}" class="w-100 object-fit-cover" style="height: 100%;">
                </div>
                <div class="card-info mt-2 p-2">
                    <h5 class="card-title fw-bold text-truncate mb-2">${recipe.title}</h5>
                    <div class="d-flex align-items-center mb-2">
                        <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                        <span class="fw-bold me-1">${recipe.rating}</span>
                        <span class="text-muted small me-2">(${recipe.reviewCount})</span>
                        ${kebabMenuHtml}
                    </div>
                     <div class="d-flex flex-wrap align-items-center text-muted small gap-2">
                        <span class="d-flex align-items-center text-nowrap bg-light px-2 py-1 rounded">
                            <i class="bi bi-people me-1"></i> ${recipe.serving}인분
                        </span>
                        <span class="d-flex align-items-center text-nowrap bg-light px-2 py-1 rounded">
                            <i class="bi bi-clock me-1"></i> ${recipe.time}
                        </span>
                        <span class="d-flex align-items-center text-nowrap bg-light px-2 py-1 rounded">
                            <i class="bi bi-difficulty me-1"></i> ${recipe.difficulty}
                        </span>
                        <span class="d-flex align-items-center text-nowrap bg-light px-2 py-1 rounded">
                            <i class="bi bi-spicy me-1"></i> ${recipe.spicy}
                        </span>
                    </div>
                </div>
            </div>
        `;
    };

    const recipeContainer = document.getElementById('recipe-list');
    if (recipeContainer) {
        recipeContainer.innerHTML = ''; // 초기화
        recipeData.forEach(recipe => {
            recipeContainer.insertAdjacentHTML('beforeend',  CreateRecipeCard(recipe));
        });

        // 카드 클릭 이벤트 (dropdown 제외하고 클릭 처리)
        recipeContainer.addEventListener('click', function(e) {
            if (e.target.closest('.dropdown') || e.target.closest('.dropdown-menu')) return;
            // 카드 클릭 시 상세 페이지 이동 로직은 여기에...
            console.log("카드 클릭됨");
        });
    }
});