const translateRecipeData = function(recipe){
    const translatedRecipe = { ...recipe };
    switch(translatedRecipe.difficulty){
        case 'easy':
            translatedRecipe.difficulty = '쉬움';
            break;
        case 'normal':
            translatedRecipe.difficulty = '보통';
            break;
        case 'hard':
            translatedRecipe.difficulty = '어려움';
    }
    switch(translatedRecipe.spicyLevel){
        case 0:
            translatedRecipe.spicyLevel = '안매워요';
            break;
        case 1:
            translatedRecipe.spicyLevel = '약간매워요';
            break;
        case 2:
            translatedRecipe.spicyLevel = '신라면맵기';
            break;
        case 3:
            translatedRecipe.spicyLevel = '열라면맵기';
            break;
        case 4:
            translatedRecipe.spicyLevel = '불닭맵기';
            break;
        case 5:
            translatedRecipe.spicyLevel = '불닭보다매워요';
            break;
    }
    return translatedRecipe
}
const createRecipeCard = function (r){
    const writerProfileUrl = r.writerProfile || "/img/user.png";
    return `<div class="card card-custom card-wide" data-type="recipe" data-no="${r.recipeNo}">
            <img src="${r.thumbnailUrl}" class="card-img-top" alt="${r.title}">
            <div class="card-body px-0 py-2">
              <div class="d-flex align-items-center mb-1">
                <img src=${writerProfileUrl} class="profile-img" alt="User">
                <div class="overflow-hidden w-100">
                  <div class="d-flex overflow-hidden w-100">
                    <small class="fw-bold text-nowrap">${r.writerNickname}</small>
                    <p class="card-text text-truncate mb-0 ms-2">${r.title}</p>
                  </div>
                  <div class="small text-muted">
                    <span class="text-warning"><i class="bi bi-star-fill"></i> ${r.averageRating}</span>
                    <span>| 후기 ${r.reviewCount}</span>
                  </div>
                </div>
              </div>
              <div class="small text-muted text-center">
                <span class="text-secondary"><i class="bi bi-person"></i> ${r.servings}인분</span>
                <span class="text-secondary"><i class="bi bi-clock"></i> ${r.prepTime}분</span>
                <span class="text-secondary"><i class="bi bi-star"></i> ${r.difficulty}</span>
                <span><img src="/img/spicy.png" class="spicy"> ${r.spicyLevel}</span>
              </div>
            </div>
          </div>`;
}
const translateGroupBuyData = function (groupbuy){
    const translatedGroupBuy = { ...groupbuy };
    const fullAddress = translatedGroupBuy.shareLocation;
    const addressParts = fullAddress.split(' ');
    const district = addressParts.find(part => part.endsWith('구'));

    if (district) {
        translatedGroupBuy.shareLocation = district;
    } else {
        translatedGroupBuy.shareLocation = addressParts.length > 1 ? addressParts[1] : '주소 오류';
    }
    return translatedGroupBuy;
}
const createQuantityGBCard = function (q){
    const creatorImageUrl = q.creatorImageUrl || '/img/user.png';

    return `<div class="card card-custom card-wide" data-type="quantityGroupBuy" data-no="${q.quantityGroupBuyNo}">
            <img src="${q.groupBuyImageUrl}" class="card-img-top" alt="${q.title}">
            <div class="card-body px-0 py-2">
              <div class="d-flex align-items-center mb-1">
                <img src="${creatorImageUrl}" class="profile-img" alt="User">
                <div class="overflow-hidden w-100">
                  <div class="d-flex overflow-hidden w-100">
                    <small class="fw-bold text-nowrap">${q.nickname}</small>
                    <p class="card-text text-truncate mb-0 ms-2">${q.title}</p>
                  </div>
                  <small class="text-danger d-block mb-1">남은 수량 : ${q.remainingQty}${q.unit} / ${q.quantity}${q.unit}</small>
                </div>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <div >
                  <span class="fw-bold">${q.pricePerUnit}원</span>
                  <small class="text-muted">(${q.shareAmount}${q.unit}당)</small>
                </div>
                <span class="badge badge-location">${q.shareLocation}</span>
              </div>
            </div>
          </div>`;
}
const createPeriodGBCard = function (p){
    const creatorImageUrl = p.creatorImageUrl || '/img/user.png';
    return `<div class="card card-custom card-wide" data-type="periodGroupBuy" data-no="${p.periodGroupBuyNo}">
            <img src="${p.groupBuyImageUrl}" class="card-img-top" alt="${p.title}">
            <div class="card-body px-0 py-2">
              <div class="d-flex align-items-center mb-1">
                <img src="${creatorImageUrl}" class="profile-img" alt="User">
                <div class="overflow-hidden w-100">
                  <div class="d-flex overflow-hidden w-100">
                    <small class="fw-bold text-nowrap">${p.nickname}</small>
                    <p class="card-text text-truncate mb-0 ms-2">${p.title}</p>
                  </div>
                  <small class="text-danger d-block mb-1 countdown-timer" data-due-date="${p.dueDate}">남은 기간 : 계산 중...</small>
                </div>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span class="fw-bold">${p.minPricePerPerson}~${p.maxPricePerPerson}원 <small class="text-muted fw-normal">(<span class="text-danger">${p.participants}</span>/${p.maxParticipants})</small></span>
                <span class="badge badge-location">${p.shareLocation}</span>
              </div>
            </div>
          </div>`;
}
function startCountdownTimer(timerElement) {
    const dueDateString = timerElement.dataset.dueDate;
    const targetDate = new Date(dueDateString.replace(' ', 'T'));

    const updateCountdown = () => {
        const now = new Date().getTime();
        const distance = targetDate.getTime() - now;

        if (distance <= 0) {
            clearInterval(timerInterval);
            timerElement.innerHTML = '모집마감';
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
const translateReviewData = function (review){
    const translatedReview = { ...review };
    translatedReview.inDate = review.inDate.split('T')[0].replace(/-/g, '.');
    return translatedReview;
}
const createReviewCard = function (r){
    const writerImageUrl = r.writerProfileImage || '/img/user.png';
    return `<div class="card card-custom card-wide" data-type="review" data-no="${r.reviewNo}">
            <img src="${r.reviewImage}" class="card-img-top" alt="${r.reviewImage}">
            <div class="card-body px-0 py-2">
              <div class="d-flex align-items-center mb-1">
                <img src="${writerImageUrl}" class="profile-img" alt="User">
                <div class="overflow-hidden w-100">
                  <div class="d-flex overflow-hidden w-100">
                    <small class="fw-bold text-nowrap">${r.writerNickname}</small>
                    <p class="card-text text-truncate mb-0 ms-2">${r.title}</p>
                  </div>
                  <div class="small text-muted">
                    <span class="text-warning"><i class="bi bi-star-fill"></i> ${r.rating}</span>
                    <span>작성일 : ${r.inDate}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>`;
}