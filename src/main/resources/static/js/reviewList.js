import {translateReviewData, createAlternativePart} from "./reviewDetailCard.js";
import {removeReview} from "./reviewUtil.js";
import {showAlertModal} from "./modal.js";
const recipeNo = window.location.pathname.split('/').at(-1);
const createReviewCard = function (r, recipeNo){
    const alternativeIngs = createAlternativePart(r.alternatives);
    const writerImageUrl = r.writerProfileImage || '/img/user.png';
    let editBox = '';
    if(r.writer){
        editBox = `<div class="position-relative edit-box-container" data-no="${r.reviewNo}">
                    <i class="bi bi-three-dots-vertical text-dark"></i>
                    <div class="edit-box text-center bg-white">
                        <a href="/review/edit/${r.reviewNo}/${recipeNo}" class="mb-2">수정</a>
                        <a class="mt-2 removeReview" data-reviewNo="${r.reviewNo}">삭제</a>
                    </div>
                </div>`;
    }
    return `<div class="review-card">
            <div class="d-flex justify-content-between align-items-start">
                <div class="review-info">
                    <div class="d-flex flex-column align-items-center writer-profile" data-no="${r.writerNo}">
                        <img src="${writerImageUrl}" alt="프로필사진" class="review-avatar">
                        <div class="review-author">${r.writerNickname}</div>
                    </div>
                    <div>
                        <div class="review-title mb-2">${r.title}</div>
                        <div class="review-meta">
                            <span class="review-rating"><span class="text-warning"><i class="bi bi-star-fill"></i></span> ${r.rating}</span>
                            <span>${r.inDate}</span>
                            <span class="review-badge"><img src="/img/spicy.png"> ${r.spicyLevel}</span>
                        </div>
                    </div>
                </div>
                ${editBox}
            </div>

            <div class="review-content">${r.content}</div>
            ${alternativeIngs}
            <img src="${r.reviewImage}" alt="리뷰 이미지" class="review-image">
        </div>`;
}
const fetchReviewData = async function(recipeNo){
    const reviewList = document.getElementById('review-list');
    try{
        const response = await fetch(`/api/reviews/recipe/${recipeNo}`)
        const reviewData = await response.json();
        reviewData.forEach(review => {
            const translatedFormatReview = translateReviewData(review);
            const cardHtml = createReviewCard(translatedFormatReview, recipeNo);
            reviewList.insertAdjacentHTML('beforeend', cardHtml)
        })
        bindRemoveEvents();
        bindMoveWriterPage();
    }catch(error){
        console.error('리뷰 데이터 가져오는 중 오류 발생: ', error);
    }
}
const fetchRecipeData = async function(recipeNo){
    const recipeInfo = document.querySelector('.rating-summary');
    try{
        const response = await fetch(`/api/recipes/detail/${recipeNo}`)
        const recipeData = await response.json();
        const infoHtml = `<span class="text-warning"><i class="bi bi-star-fill"></i></span>
                                <span class="fw-semibold">${recipeData.rating}</span>
                                <span style="color: #6c757d;">(${recipeData.reviewCount})</span>`
        recipeInfo.insertAdjacentHTML('afterbegin', infoHtml)
    }catch(error){
        console.error('레시피 데이터 가져오는 중 오류 발생: ', error);
    }
}
const closeAllEditBoxes = function() {
    document.querySelectorAll('.edit-box.show').forEach(box => {
        box.classList.remove('show');
    });
};
const bindRemoveEvents = function() {
    const removeButtons = document.querySelectorAll('.removeReview');
    removeButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            const reviewNo = this.getAttribute('data-reviewNo');

            showAlertModal(
                '리뷰 삭제',
                '리뷰를 삭제하시겠습니까?',
                'error',
                () => removeReview(reviewNo, recipeNo)
            );
        });
    });
};
const bindMoveWriterPage = function (){
    const writerInfos = document.querySelectorAll('.writer-profile');
    writerInfos.forEach(button => {
        button.addEventListener('click', function (){
            const writerNo = this.getAttribute('data-no');
            location.href=`/mypage/${writerNo}`;
        })
    })
}
document.addEventListener('DOMContentLoaded',function (){
    fetchReviewData(recipeNo);
    fetchRecipeData(recipeNo);
    const reviewList = document.getElementById('review-list');
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.edit-box-container')) {
            closeAllEditBoxes();
        }
    });
    reviewList.addEventListener('click', function(e) {
        const container = e.target.closest('.edit-box-container');
        if (container) {
            e.stopPropagation();
            const currentEditBox = container.querySelector('.edit-box');

            if (currentEditBox) {
                closeAllEditBoxes();
                currentEditBox.classList.toggle('show');
            }
        }
    });

    document.getElementById('addReviewBtn').addEventListener('click', function (){
        location.href = `/review/add/${recipeNo}`
    })
})