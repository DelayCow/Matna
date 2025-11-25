document.addEventListener('DOMContentLoaded', function() {

    // --------------------------------------------------------
    // [학습 포인트 1] 데이터 정의 (나중에 서버에서 받아올 부분)
    // --------------------------------------------------------
    const recipeData = [
        {
            id: 1,
            title: "안주로 딱~ 폭신하고 촉촉한 간단 폭탄계란찜",
            image: "../static/img/steamedeggs.jpg",
            rating: 5,
            reviewCount: 8,
            serving: 1,        // 1인분
            time: "10분이내",   // 시간
            difficulty: "쉬움", // 난이도
            spicy: "약간매워요" // 맵기
        },
        {
            id: 2,
            title: "속을 뜨끈하게! 한국인 입맛저격 라비올리",
            image: "../static/img/ravioli.jpg",
            rating: 4.5,
            reviewCount: 14,
            serving: 1,        // 1인분
            time: "10분이내",   // 시간
            difficulty: "쉬움", // 난이도
            spicy: "약간매워요" // 맵기
        },
        {
            id: 3,
            title: "촉촉한 패티! 황금비율 수제버거",
            image: "../static/img/hambugi.jpg", // 임시 이미지 경로
            rating: 5.0,
            reviewCount: 19,
            serving: 1,        // 1인분
            time: "10분이내",   // 시간
            difficulty: "쉬움", // 난이도
            spicy: "약간매워요" // 맵기
        },
        {
            id: 4,
            title: "한국에서 영국맛내기~ 바삭바삭 피쉬앤칩스",
            image: "../static/img/fishAndChips.jpg",
            rating: 4.0,
            reviewCount: 6,
            serving: 1,        // 1인분
            time: "10분이내",   // 시간
            difficulty: "쉬움", // 난이도
            spicy: "약간매워요" // 맵기
        },
        {
            id: 5,
            title: "소고기로 깊은 맛내기! 시원한 미역국",
            image: "../static/img/miyuckguck.jpg",
            rating: 3.5,
            reviewCount: 21,
            serving: 1,        // 1인분
            time: "10분이내",   // 시간
            difficulty: "쉬움", // 난이도
            spicy: "약간매워요" // 맵기
        },
        {
            id: 6,
            title: "오~래 끓여서 부드러운 꼬꼬뱅",
            image: "../static/img/cokkioo.jpg",
            rating: 4.5,
            reviewCount: 41,
            serving: 1,        // 1인분
            time: "10분이내",   // 시간
            difficulty: "쉬움", // 난이도
            spicy: "약간매워요" // 맵기
        }
    ];

    // --------------------------------------------------------
    // [학습 포인트 2] 별점 HTML 생성 헬퍼 함수
    // 점수(4.5)를 받아서 별 아이콘 문자열로 반환
    // --------------------------------------------------------
    const createStarHtml = (rating) => {
        let stars = '';
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;

        // 꽉 찬 별
        for (let i = 0; i < fullStars; i++) {
            stars += '<i class="bi bi-star-fill"></i>';
        }
        // 반 쪽 별
        if (hasHalfStar) {
            stars += '<i class="bi bi-star-half"></i>';
        }
        // 비어있는 별 (선택사항, 5개 채우기용)
        const emptyStars = 5 - Math.ceil(rating);
        for (let i = 0; i < emptyStars; i++) {
            stars += '<i class="bi bi-star"></i>';
        }

        return stars;
    };

    // --------------------------------------------------------
    // [학습 포인트 3] 템플릿 리터럴로 카드 HTML 생성
    // --------------------------------------------------------
    const createRecipeCard = (recipe) => {
        // 이미지가 없을 경우를 대비한 대체 이미지 처리 (옵션)
        const imgSrc = recipe.image ? recipe.image : 'https://via.placeholder.com/300x200';

        // 수정 페이지 URL 나중에 만들어서 넣을것
        const editUrl = `/recipe/edit?id=${recipe.id}`;

        return `
            <div class="recipe-card" data-id="${recipe.id}">
                <div class="card-img-wrap">
                    <img src="${imgSrc}" alt="${recipe.title}">
                </div>
                <div class="card-info">
                    <h5 class="card-title">${recipe.title}</h5>
                    <div class="d-flex align-items-center">
                        <span class="star-rating me-1" >${createStarHtml(recipe.rating)}</span>
                        <span class="review-count">(${recipe.reviewCount})</span>
                        <div class="dropdown ms-auto"> <button class="btn btn-link text-secondary p-0 border-0 text-decoration-none dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
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
                    </div>
                    
                    <div class="d-flex align-items-center text-muted small gap-3">
                    <span class="d-flex align-items-center font-size-recipe-list">
                        <i class="bi bi-people me-1"></i> ${recipe.serving}인분
                    </span>
                    
                    <span class="d-flex align-items-center font-size-recipe-list">
                        <i class="bi bi-clock me-1"></i> ${recipe.time}
                    </span>

                    <span class="d-flex align-items-center font-size-recipe-list">
                        <i class="bi bi-star me-1"></i> ${recipe.difficulty}
                    </span>

                    ${ recipe.spicy ? `
                    <span class="d-flex align-items-center text-danger font-size-recipe-list">
                        <i class="bi bi-fire me-1"></i> 
                        ${recipe.spicy}
                    </span>` : '' }
                    </div>
                </div>
            </div>
        `;
    };

    // --------------------------------------------------------
    // [학습 포인트 4] 렌더링 실행
    // --------------------------------------------------------
    const recipeContainer = document.getElementById('recipe-list');

    if (recipeContainer) {
        recipeData.forEach(recipe => {
            const cardHtml = createRecipeCard(recipe);
            recipeContainer.insertAdjacentHTML('beforeend', cardHtml);
        });
    }

    // --------------------------------------------------------
    // [이벤트 처리]
    // --------------------------------------------------------

    // 1. 카드 클릭 이벤트 (이벤트 위임)
    recipeContainer.addEventListener('click', function(e) {
        const card = e.target.closest('.recipe-card');
        if (card) {
            const id = card.getAttribute('data-id');
            alert(`레시피 상세 페이지(${id})로 이동합니다.`);
            // window.location.href = `/recipe/detail/${id}`;
        }
    });



    // 2. 헤더 메뉴 토글
    const menuBtn = document.getElementById('headerMenuBtn');
    const dropdown = document.getElementById('headerDropdown');

    if(menuBtn && dropdown) {
        menuBtn.addEventListener('click', (e) => {
            e.stopPropagation(); // 버튼 클릭 시 상위로 이벤트 전파 방지
            dropdown.classList.toggle('show');
        });

        // 화면 아무곳이나 클릭하면 메뉴 닫기
        document.addEventListener('click', () => {
            dropdown.classList.remove('show');
        });
    }
});