document.addEventListener('DOMContentLoaded', function() {

    initializeSpicyIcons();

    const sortDropdownItems = document.querySelectorAll('.sort-dropdown .dropdown-item');
    const sortButton = document.querySelector('.sort-dropdown .btn');

    sortDropdownItems.forEach(item => {
    item.addEventListener('click', function(e) {
    e.preventDefault();
    sortDropdownItems.forEach(i => i.classList.remove('active'));
    this.classList.add('active');
    sortButton.textContent = this.textContent;
    const sort = sortButton.getAttribute('data-sort')
    // 실제 정렬 로직은 여기에 추가
});
});

    const recipeContainer = document.getElementById('recipeContainer');
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
    recipeContainer.insertAdjacentHTML('beforeend', `<div class="col d-flex justify-content-center" > ${cardHtml} </div>`)
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
