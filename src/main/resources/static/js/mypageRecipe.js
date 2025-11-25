document.addEventListener('DOMContentLoaded', function() {

    // ========================================================
    // [STEP 1] 사용자 정보 설정 (하드코딩 동적 데이터)
    // ========================================================
    const currentUserId = "user1";    // 현재 로그인한 사람
    // const profileOwnerId = "user1"; // [상황 A] 내 프로필 볼 때 (이 줄 주석 해제)
    const profileOwnerId = "user2";   // [상황 B] 남의 프로필 볼 때 (현재 설정)

    // 내 프로필인지 판단하는 핵심 변수 (true / false)
    const isMyProfile = currentUserId === profileOwnerId;


    // ========================================================
    // [STEP 2] 화면 렌더링 (헤더 & 프로필 영역)
    // ========================================================

    // 1. 헤더 메뉴 (우측 상단 점 3개) 처리
    const headerMenuBtn = document.getElementById('headerMenuBtn');
    if (headerMenuBtn) {
        if (isMyProfile) {
            headerMenuBtn.style.display = 'block'; // 내꺼면 보임
        } else {
            headerMenuBtn.style.display = 'none';  // 남꺼면 숨김 (공통 UI지만 숨겨버림)
        }
    }

    // 2. 프로필 정보 & 버튼 영역 처리
    const subText = document.getElementById('profile-sub-text');
    const actionBtns = document.getElementById('profile-action-btns');

    if (isMyProfile) {
        // [내 프로필일 때]
        // 닉네임 아래: 맛나머니 표시
        if(subText) subText.innerHTML = `<small class="text-muted">내 맛나머니 : 5,600 원</small>`;
        // 하단 버튼: 없음
        if(actionBtns) actionBtns.innerHTML = '';

    } else {
        // [남의 프로필일 때]
        // 닉네임 아래: 신고하기 버튼
        if(subText) subText.innerHTML = `
            <button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1" style="font-size: 0.75rem;">
                <i class="bi bi-exclamation-circle me-1"></i>신고하기
            </button>
        `;
        // 하단 버튼: 채팅/팔로우 버튼 생성
        if(actionBtns) actionBtns.innerHTML = `
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


    // ========================================================
    // [STEP 3] 레시피 데이터 및 헬퍼 함수
    // ========================================================
    const recipeData = [
        {
            id: 1,
            title: "안주로 딱~ 폭신하고 촉촉한 간단 폭탄계란찜",
            image: "../static/img/steamedeggs.jpg",
            rating: 5,
            reviewCount: 8,
            serving: 1, time: "10분이내", difficulty: "쉬움", spicy: "약간매워요"
        },
        {
            id: 2,
            title: "속을 뜨끈하게! 한국인 입맛저격 라비올리",
            image: "../static/img/ravioli.jpg",
            rating: 4.5,
            reviewCount: 14,
            serving: 1, time: "10분이내", difficulty: "쉬움", spicy: "약간매워요"
        },
        // ... 나머지 데이터들 (필요하면 추가) ...
        {
            id: 3,
            title: "촉촉한 패티! 황금비율 수제버거",
            image: "../static/img/hambugi.jpg",
            rating: 5.0,
            reviewCount: 19,
            serving: 1, time: "10분이내", difficulty: "쉬움", spicy: "약간매워요"
        },
        {
            id: 4,
            title: "한국에서 영국맛내기~ 바삭바삭 피쉬앤칩스",
            image: "../static/img/fishAndChips.jpg",
            rating: 4.0,
            reviewCount: 6,
            serving: 1, time: "10분이내", difficulty: "쉬움", spicy: "약간매워요"
        },
    ];

    // 별점 HTML 생성 함수
    const createStarHtml = (rating) => {
        let stars = '';
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;

        for (let i = 0; i < fullStars; i++) stars += '<i class="bi bi-star-fill"></i>';
        if (hasHalfStar) stars += '<i class="bi bi-star-half"></i>';
        const emptyStars = 5 - Math.ceil(rating);
        for (let i = 0; i < emptyStars; i++) stars += '<i class="bi bi-star"></i>';

        return stars;
    };


    // ========================================================
    // [STEP 4] 카드 HTML 생성 (여기가 핵심 변경 포인트!)
    // ========================================================
    const createRecipeCard = (recipe) => {
        const imgSrc = recipe.image ? recipe.image : 'https://via.placeholder.com/300x200';
        const editUrl = `/recipe/edit?id=${recipe.id}`;

        // ★ 핵심: 내 프로필일 때만 드롭다운 메뉴 HTML 문자열을 생성함 ★
        const kebabMenuHtml = isMyProfile ? `
            <div class="dropdown ms-auto">
                <button class="btn btn-link text-secondary p-0 border-0 text-decoration-none dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-three-dots-vertical"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                    <li>
                        <a class="dropdown-item small" href="${editUrl}">
                            <i class="bi bi-pencil-square text-primary me-2"></i>수정
                        </a>
                    </li>
                    <li><hr class="dropdown-divider my-1"></li>
                    <li>
                        <button class="dropdown-item small text-danger btn-delete" data-id="${recipe.id}">
                            <i class="bi bi-trash me-2"></i>삭제
                        </button>
                    </li>
                </ul>
            </div>
        ` : ''; // 내 프로필 아니면 빈 문자열(아무것도 안 보임)

        return `
            <div class="recipe-card mb-4" data-id="${recipe.id}">
                <div class="card-img-wrap overflow-hidden rounded shadow-sm">
                    <img src="${imgSrc}" alt="${recipe.title}" class="w-100 object-fit-cover" style="height: 200px;">
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
                        </div>
                </div>
            </div>
        `;
    };

    // ========================================================
    // [STEP 5] 실행 및 이벤트 리스너
    // ========================================================
    const recipeContainer = document.getElementById('recipe-list');

    if (recipeContainer) {
        recipeData.forEach(recipe => {
            const cardHtml = createRecipeCard(recipe);
            recipeContainer.insertAdjacentHTML('beforeend', cardHtml);
        });

        // 클릭 이벤트 (카드 클릭 시 이동 등)
        recipeContainer.addEventListener('click', function(e) {
            // 케밥 메뉴(드롭다운) 클릭 시에는 카드 이동 막기
            if (e.target.closest('.dropdown') || e.target.closest('.dropdown-menu')) {
                return;
            }

            const card = e.target.closest('.recipe-card');
            if (card) {
                const id = card.getAttribute('data-id');
                console.log(`레시피 상세(${id}) 이동`);
                // window.location.href = ...;
            }
        });
    }

    // 헤더 드롭다운 토글 기능 (공통 UI용)
    const menuBtn = document.getElementById('headerMenuBtn');
    const dropdown = document.getElementById('headerDropdown');

    if(menuBtn && dropdown) {
        menuBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdown.classList.toggle('show');
        });
        document.addEventListener('click', () => {
            dropdown.classList.remove('show');
        });
    }
});