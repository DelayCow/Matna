document.addEventListener('DOMContentLoaded',function (){
    const quantityGroupBuyContainer = document.getElementById('quantityBuyContainer');
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
    quantityGroupBuyContainer.insertAdjacentHTML('beforeend', `<div class="col d-flex justify-content-center" > ${cardHtml} </div>`)
})

    const periodGroupBuyContainer = document.getElementById('periodBuyContainer');
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
    periodGroupBuyContainer.insertAdjacentHTML('beforeend', `<div class="col d-flex justify-content-center" > ${cardHtml} </div>`)
})

    const timerElements = document.querySelectorAll('.countdown-timer');
    timerElements.forEach(timerElement => {
    startCountdownTimer(timerElement);
});

    const addPeriodBuy = document.getElementById('addPeriodBuy');
    const addQuantityBuy = document.getElementById('addQuantityBuy');
    addPeriodBuy.addEventListener('click', () => location.href='addPeriodGroupBuy.html');//나중에 api로 바꾸기
    addQuantityBuy.addEventListener('click', () => location.href='addQuantityGroupBuy.html');//나중에 api로 바꾸기

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
