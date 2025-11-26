document.addEventListener('DOMContentLoaded', function() {
    const scrollButtons = document.querySelectorAll('.scroll-btn');
    const scrollDistance = 280;

    scrollButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const scrollArea = document.querySelector(targetId);

            if (!scrollArea) return;
            const isRight = this.classList.contains('right-arrow');

            const newScrollLeft = isRight
                ? scrollArea.scrollLeft + scrollDistance
                : scrollArea.scrollLeft - scrollDistance;

            scrollArea.scrollTo({
                left: newScrollLeft,
                behavior: 'smooth'
            });
        });
    });

    const recipeContainer = document.getElementById('recipeScroll');
    const recipeData = [
        {
            "recipeNo": 45,
            "thumbnailUrl": "../static/img/steamedeggs.jpg",
            "writerProfile": "../static/img/user.png",
            "writerNickname": "침대견",
            "title": "안주로 딱~ 폭신하고 부드러운 계란찜",
            "averageRating": 4.5,
            "reviewCount": 8,
            "servings": 1,
            "prepTime": 10,
            "difficulty": "easy",
            "spicyLevel": 0
        },
        {
            "recipeNo": 5,
            "thumbnailUrl": "../static/img/ravioli.jpg",
            "writerProfile": "../static/img/user.png",
            "writerNickname": "눕오리",
            "title": "속을 뜨끈하게 한국인 입맛에 맞춘 라비올리",
            "averageRating": 4.0,
            "reviewCount": 14,
            "servings": 2,
            "prepTime": 20,
            "difficulty": "normal",
            "spicyLevel": 0
        },
    ]
    recipeData.forEach(recipe => {
        const translatedFormatRecipe = translateRecipeData(recipe);
        const cardHtml = createRecipeCard(translatedFormatRecipe);
        recipeContainer.insertAdjacentHTML('beforeend', cardHtml)
    })

    const quantityGroupBuyContainer = document.getElementById('quantityBuyScroll');
    const quantityGroupBuyData = [
        {
            "groupBuyNo": 45,
            "groupBuyImageUrl": "../static/img/orange.jpg",
            "creatorImageUrl": "../static/img/user.png",
            "nickname": "해피",
            "title": "고당도 제주 감귤 1kg",
            "remainingQty": 300,
            "unit": "g",
            "pricePerUnit": 1100,
            "shareAmount": 100,
            "address": "서울 금천구 두산로11길 22"
        },
        {
            "groupBuyNo": 12,
            "groupBuyImageUrl": "../static/img/mushroom.jpg",
            "creatorImageUrl": "../static/img/user.png",
            "nickname": "말이오",
            "title": "표고버섯 살 사람",
            "remainingQty": 10,
            "unit": "개",
            "pricePerUnit": 100,
            "shareAmount": 1,
            "address": "서울 금천구 두산로11길 22"
        },
        {
            "groupBuyNo": 35,
            "groupBuyImageUrl": "../static/img/tomato.jpg",
            "creatorImageUrl": "../static/img/user.png",
            "nickname": "도맛도",
            "title": "방울토마토 공구해요",
            "remainingQty": 300,
            "unit": "g",
            "pricePerUnit": 500,
            "shareAmount": 100,
            "address": "서울 구로구 경인로43길 49"
        },
    ]
    quantityGroupBuyData.forEach(groupBuy => {
        const translatedFormatGroupBuy = translateGroupBuyData(groupBuy);
        const cardHtml = createQuantityGBCard(translatedFormatGroupBuy);
        quantityGroupBuyContainer.insertAdjacentHTML('beforeend', cardHtml)
    })

    const periodGroupBuyContainer = document.getElementById('periodBuyScroll');
    const periodGroupBuyData = [
        {
            "groupBuyNo": 4,
            "groupBuyImageUrl": "../static/img/basil.jpg",
            "creatorImageUrl": "../static/img/user.png",
            "nickname": "밥도둑",
            "title": "바질 같이사기",
            "dueDate": "2025-12-11 14:30",
            "participants": 2,
            "maxParticipants": 5,
            "minPricePerPerson": 1100,
            "maxPricePerPerson": 5500,
            "address": "서울 동작구 사당로16길 35"
        },
        {
            "groupBuyNo": 24,
            "groupBuyImageUrl": "../static/img/potato.jpg",
            "creatorImageUrl": "../static/img/user.png",
            "nickname": "흙감자",
            "title": "수미감자 왔어요",
            "dueDate": "2025-12-12 17:30",
            "participants": 2,
            "maxParticipants": 5,
            "minPricePerPerson": 1100,
            "maxPricePerPerson": 5500,
            "address": "서울 동작구 사당로16길 35"
        },
    ]
    periodGroupBuyData.forEach(groupBuy => {
        const translatedFormatGroupBuy = translateGroupBuyData(groupBuy);
        const cardHtml = createPeriodGBCard(translatedFormatGroupBuy);
        periodGroupBuyContainer.insertAdjacentHTML('beforeend', cardHtml)
    })

    const timerElements = document.querySelectorAll('.countdown-timer');
    timerElements.forEach(timerElement => {
        startCountdownTimer(timerElement);
    });

    const reviewContainer = document.getElementById('reviewScroll');
    const reviewData = [
        {
            "reviewNo": 46,
            "reviewImage": "../static/img/tteokbokki.jpg",
            "writerProfileImage": "../static/img/user.png",
            "writerNickname": "베베는오리",
            "title": "다이어트 떡볶이 만들어봤어요",
            "rating": 4.5,
            "inDate": "2025-01-11 16:30:00"
        },
        {
            "reviewNo": 4,
            "reviewImage": "../static/img/salad.jpg",
            "writerProfileImage": "../static/img/user.png",
            "writerNickname": "춘냥이",
            "title": "유자드레싱 따라해봤어요",
            "rating": 5.0,
            "inDate": "2025-11-05 16:30:00"
        },
    ]
    reviewData.forEach(review => {
        const translatedFormatReview = translateReviewData(review);
        const cardHtml = createReviewCard(translatedFormatReview);
        reviewContainer.insertAdjacentHTML('beforeend', cardHtml)
    })

    document.querySelectorAll('.card-custom').forEach(card => {
        card.addEventListener('click', function() {
            const noValue = this.getAttribute('data-no');
            const type = this.getAttribute('data-type');

            if (noValue) {
                // window.location.href = `http://127.0.0.1:5432/${type}/detail/${noValue}`; //api 예상
                alert(type + noValue) //테스트
            }
        });
    });
});