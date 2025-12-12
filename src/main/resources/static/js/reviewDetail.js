import {translateReviewData, createAlternativePart} from "./reviewDetailCard.js";

const createReviewCard = function (r){
    const alternativeIngs = createAlternativePart(r.alternatives);
    const writerImageUrl = r.writerProfileImage || '/img/user.png';
    let editBox = '';
    if(r.writer){
        editBox = `<div class="position-relative" id="edit-box" data-no="${r.reviewNo}">
                    <i class="bi bi-three-dots-vertical text-dark"></i>
                    <div class="edit-box text-center bg-white">
                        <a href="/review/edit" class="mb-2">수정</a>
                        <a class="mt-2" id="removeReview">삭제</a>
                    </div>
                </div>`;
    }
    return `<div class="review-card">
            <img src="${r.reviewImage}" alt="리뷰 이미지" class="review-detail-image">
            <div class="d-flex justify-content-between align-items-start">
                <div class="review-info">
                    <div class="d-flex flex-column align-items-center">
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
        </div>`;
}
const fetchReviewData = async function(reviewNo){
    const reviewArea = document.getElementById('review-area');
    try{
        const response = await fetch(`/api/reviews/${reviewNo}`)
        const reviewData = await response.json();
        const translatedFormatReview = translateReviewData(reviewData);
        const cardHtml = createReviewCard(translatedFormatReview);
        reviewArea.insertAdjacentHTML('beforeend', cardHtml)

        bindEditBoxEvents();
    }catch(error){
        console.error('리뷰 데이터 가져오는 중 오류 발생: ', error);
    }
}

const bindEditBoxEvents = function() {
    const edit = document.querySelector('#edit-box');
    const editBox = document.querySelector('.edit-box');

    if (edit && editBox) {
        edit.addEventListener('click', function(e){
            e.stopPropagation();
            editBox.classList.toggle('show');
        });
        document.addEventListener('click', function() {
            if (editBox.classList.contains('show')) {
                editBox.classList.remove('show');
            }
        });
    }
};

document.addEventListener('DOMContentLoaded',function (){
    const reviewNo = window.location.pathname.split('/').at(-1);
    fetchReviewData(reviewNo);
})