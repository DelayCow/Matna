export const translateReviewData = function (review){
    const translatedReview = { ...review };
    translatedReview.inDate = review.inDate.split('T')[0].replace(/-/g, '.');
    switch(translatedReview.spicyLevel){
        case 0:
            translatedReview.spicyLevel = '안매워요';
            break;
        case 1:
            translatedReview.spicyLevel = '약간매워요';
            break;
        case 2:
            translatedReview.spicyLevel = '신라면맵기';
            break;
        case 3:
            translatedReview.spicyLevel = '열라면맵기';
            break;
        case 4:
            translatedReview.spicyLevel = '불닭맵기';
            break;
        case 5:
            translatedReview.spicyLevel = '불닭보다매워요';
            break;
    }
    return translatedReview;
}

export const createAlternativePart = function(ingredients){
    if(!ingredients || ingredients.length === 0){
        return ''
    }
    const item = ingredients.map(i => `
            <div class="alternative-ing-item">
                <span class="alternative-ing-label">${i.originalIngredientName}</span>
                <span class="mx-2">→</span>
                <span class="alternative-ing-value">${i.alternativeIngredientName}</span>
            </div>
        `).join('');
    return `<div class="review-alternative-ing">
                    <div class="alternative-ing-title">대체한 재료</div>
                    ${item}
                </div>`
}