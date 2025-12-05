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
    const fetchRecipeData = async function(){
        try{
            const response = await fetch(`api/recipes?size=10&sort=reviewCount,desc`)
            const recipeData = await response.json();
            recipeData.content.forEach(recipe => {
                const translatedFormatRecipe = translateRecipeData(recipe);
                const cardHtml = createRecipeCard(translatedFormatRecipe);
                recipeContainer.insertAdjacentHTML('beforeend', cardHtml)
            })
        }catch(error){
            console.error('레시피 데이터 가져오는 중 오류 발생: ', error);
        }
    }

    const quantityGroupBuyContainer = document.getElementById('quantityBuyScroll');
    const fetchQuantityGroupBuyData = async function(){
        try{
            const response = await fetch(`api/quantityGroupBuy/home?orderBy=dueSoon`)
            const quantityGroupBuyData = await response.json();
            quantityGroupBuyData.forEach(groupBuy => {
                const translatedFormatGroupBuy = translateGroupBuyData(groupBuy);
                const cardHtml = createQuantityGBCard(translatedFormatGroupBuy);
                quantityGroupBuyContainer.insertAdjacentHTML('beforeend', cardHtml)
            })
        }catch(error){
            console.error('수량공구 데이터 가져오는 중 오류 발생: ', error);
        }
    }


    const periodGroupBuyContainer = document.getElementById('periodBuyScroll');
    const fetchPeriodGroupBuyData = async function(){
        try{
            const response = await fetch(`api/periodGroupBuy/home?orderBy=dueSoon`)
            const periodGroupBuyData = await response.json();
            periodGroupBuyData.forEach(groupBuy => {
                const translatedFormatGroupBuy = translateGroupBuyData(groupBuy);
                const cardHtml = createPeriodGBCard(translatedFormatGroupBuy);
                periodGroupBuyContainer.insertAdjacentHTML('beforeend', cardHtml)
            })
            const timerElements = document.querySelectorAll('.countdown-timer');
            timerElements.forEach(timerElement => {
                startCountdownTimer(timerElement);
            });
        }catch(error){
            console.error('기간공구 데이터 가져오는 중 오류 발생: ', error);
        }
    }

    const reviewContainer = document.getElementById('reviewScroll');
    const fetchReviewData = async function(){
        try{
            const response = await fetch(`api/reviews/recent`)
            const reviewData = await response.json();
            reviewData.forEach(review => {
                const translatedFormatReview = translateReviewData(review);
                const cardHtml = createReviewCard(translatedFormatReview);
                reviewContainer.insertAdjacentHTML('beforeend', cardHtml)
            })
        }catch(error){
            console.error('리뷰 데이터 가져오는 중 오류 발생: ', error);
        }
    }

    fetchRecipeData();
    fetchQuantityGroupBuyData();
    fetchPeriodGroupBuyData();
    fetchReviewData();
    document.body.addEventListener('click', function(event) {
        const clickedCard = event.target.closest('.card-custom');
        if (clickedCard) {
            const noValue = clickedCard.getAttribute('data-no');
            const type = clickedCard.getAttribute('data-type');
            if (noValue && type) {
                location.href = `/${type}/detail/${noValue}`;
            }
        }
    });
});