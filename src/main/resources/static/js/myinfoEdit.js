const bankList = [
    { name: "KB국민은행" }, { name: "신한은행" }, { name: "우리은행" }, { name: "하나은행" }, { name: "NH농협은행" }, { name: "IBK기업은행" }, { name: "SC제일은행" }, { name: "카카오뱅크" }, { name: "케이뱅크" }, { name: "토스뱅크" }, { name: "부산은행" }, { name: "대구은행" }, { name: "광주은행" }, { name: "전북은행" }, { name: "경남은행" }, { name: "제주은행" }, { name: "수협은행" }, { name: "한국씨티은행" }, { name: "KDB산업은행" }, { name: "우체국" },
];
document.addEventListener('DOMContentLoaded', function(){
    const bankSelect = document.getElementById('bank');
    bankList.forEach(bank => {
        const option = document.createElement('option');
        option.value = bank.name;
        option.textContent = bank.name;
        bankSelect.appendChild(option);
    })
    bankSelect.value = "KB국민은행";

    document.getElementById('register-btn').addEventListener('click', function(){
        const password = document.getElementById('password').value
        const confirmPassword = document.getElementById('confirmPassword').value
        if(password != confirmPassword){
            msg = document.getElementById('pwcheckmsg').classList
            msg.add('unavailable')
            msg.remove('notshow');
            return;
        };
        document.querySelector('form').submit();
    })

    document.getElementById('nicknameCheck').addEventListener('click', function(){
        const nicknameInput = document.getElementById('nickname');
        const currentNickname = nicknameInput.value;
        const msg = document.getElementById('nicknamecheckmsg');

        const isDuplicated = (currentNickname === "기존닉네임" || currentNickname === "사용불가닉"); // 예시로 "기존닉네임"과 "사용불가닉"은 중복이라고 가정

        msg.classList.remove('notshow'); // 메시지 표시

        if(!isDuplicated){ // isDuplicated가 false일 때(중복이 아닐 때) 사용 가능
            msg.innerText = `"${currentNickname}"은(는) 사용할 수 있는 닉네임입니다.`;
            msg.classList.add('available');
            msg.classList.remove('unavailable');
        }else{
            msg.innerText = `"${currentNickname}"은(는) 이미 사용 중인 닉네임입니다.`;
            msg.classList.add('unavailable');
            msg.classList.remove('available');
        }
    });

    //이미지
    const profileImageElement = document.getElementById('profileImage');
    const editProfileBtn = document.getElementById('editProfileBtn');
    const deleteProfileBtn = document.getElementById('deleteProfileBtn');
    const profileFileUpload = document.getElementById('profileFileUpload');

    editProfileBtn.addEventListener('click', function(e) {
        e.preventDefault();
        profileFileUpload.click();
    });
    profileFileUpload.addEventListener('change', function(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                profileImageElement.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });
    deleteProfileBtn.addEventListener('click', function(e) {
        e.preventDefault();
        profileImageElement.src = '/img/user.png';
    });
})