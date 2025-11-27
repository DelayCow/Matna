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
    document.getElementById('login-btn').addEventListener('click', function(){
        location.href="login.html";
    })
    document.getElementById('idCheck').addEventListener('click', function(){
        msg = document.getElementById('idcheckmsg')
        msg.classList.remove('notshow');
        const isDuplicated = true;//id중복확인 api로 true/false받기
        if(isDuplicated){
            msg.innerText = "사용할 수 있는 아이디입니다.";
            msg.classList.add('available');
        }else{
            msg.innerText = "사용할 수 없는 아이디입니다.";
            msg.classList.add('unavailable');
        }
    })
    document.getElementById('nicknameCheck').addEventListener('click', function(){
        msg = document.getElementById('nicknamecheckmsg')
        msg.classList.remove('notshow');
        const isDuplicated = false; //닉네임중복확인api booelan값
        if(isDuplicated){
            msg.innerText = "사용할 수 있는 닉네임입니다.";
            msg.classList.add('available');
        }else{
            msg.innerText = "사용할 수 없는 닉네임입니다.";
            msg.classList.add('unavailable');
        }
    })

})