import {showAlertModal} from "./modal.js";
const bankList = [
    { name: "KB국민은행" }, { name: "신한은행" }, { name: "우리은행" }, { name: "하나은행" }, { name: "NH농협은행" }, { name: "IBK기업은행" }, { name: "SC제일은행" }, { name: "카카오뱅크" }, { name: "케이뱅크" }, { name: "토스뱅크" }, { name: "부산은행" }, { name: "대구은행" }, { name: "광주은행" }, { name: "전북은행" }, { name: "경남은행" }, { name: "제주은행" }, { name: "수협은행" }, { name: "한국씨티은행" }, { name: "KDB산업은행" }, { name: "우체국" },
];
document.addEventListener('DOMContentLoaded', function(){
    let checkId = false;
    let checkNickname = false;
    const bankSelect = document.getElementById('bank');
    bankList.forEach(bank => {
        const option = document.createElement('option');
        option.value = bank.name;
        option.textContent = bank.name;
        bankSelect.appendChild(option);
    })

    document.getElementById('register-btn').addEventListener('click', async function(){
        const memberId = document.getElementById('memberId').value
        const password = document.getElementById('password').value
        const confirmPassword = document.getElementById('confirmPassword').value
        const nickname = document.getElementById('nickname').value
        const bank = document.getElementById('bank').value
        const accountNumber = document.getElementById('accountNumber').value
        const accountName = document.getElementById('accountName').value
        const address = document.getElementById('address').value

        if(!checkId || !checkNickname) return;

        if(password != confirmPassword){
            const msg = document.getElementById('pwcheckmsg').classList
            msg.add('unavailable')
            msg.remove('notshow');
            return;
        };

        const formData = {
            memberId : memberId,
            password : password,
            nickname : nickname,
            bank : bank,
            accountName : accountName,
            accountNumber : accountNumber,
            address : address
        }
        try{
            const response = await api.fetch('/api/auth/member', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body : JSON.stringify(formData)
            })

            if(response.ok){
                showAlertModal(
                    '회원가입 완료',
                    '회원가입 되었습니다',
                    'success',
                    () => {
                        window.location.href = '/login';
                    }
                );
            }else{
                showAlertModal(
                    '회원가입 실패',
                    `회원가입에 실패하였습니다.<br>정보를 다시 입력해주세요`,
                    'error'
                );
            }
        }catch(error){
            console.error('네트워크 오류:', error);
            showAlertModal(
                '네트워크 오류',
                '서버와 통신할 수 없습니다.<br>잠시 후 다시 시도해주세요.',
                'error'
            );
        }
    })
    document.getElementById('login-btn').addEventListener('click', function(){
        location.href="/login";
    })
    document.getElementById('idCheck').addEventListener('click', async function(e){
        e.preventDefault();
        const msg = document.getElementById('idcheckmsg');
        msg.classList.remove('notshow', 'available', 'unavailable');

        const memberIdValue = document.querySelector('#memberId').value;

        if (!memberIdValue.trim()) {
            msg.innerText = "아이디를 입력해주세요.";
            msg.classList.add('unavailable');
            return;
        }
        try{
            const response = await api.fetch(`/api/auth/check/id?memberId=${memberIdValue}`)

            if(!response.ok){
                const errorData = await response.json();
                console.error(errorData.message);
            }
            const data = await response.json();
            const isDuplicated = data.result;

            if(!isDuplicated){
                msg.innerText = "사용할 수 있는 아이디입니다.";
                msg.classList.add('available');
                checkId = true;
            }else{
                msg.innerText = "사용할 수 없는 아이디입니다.";
                msg.classList.add('unavailable');
                checkId = false;
            }
        } catch (error) {
            console.error('아이디 중복 확인 중 오류 발생:', error);
            msg.innerText = "오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
            msg.classList.add('unavailable');
        }

    })
    document.getElementById('nicknameCheck').addEventListener('click', async function(e){
        e.preventDefault();
        const msg = document.getElementById('nicknamecheckmsg');
        msg.classList.remove('notshow', 'available', 'unavailable');

        const nicknameValue = document.querySelector('#nickname').value;

        if (!nicknameValue.trim()) {
            msg.innerText = "닉네임을 입력해주세요.";
            msg.classList.add('unavailable');
            return;
        }
        try{
            const response = await api.fetch(`/api/auth/check/nickname?nickname=${nicknameValue}`)

            if(!response.ok){
                const errorData = await response.json();
                console.error(errorData.message);
            }
            const data = await response.json();
            const isDuplicated = data.result;

            if(!isDuplicated){
                msg.innerText = "사용할 수 있는 닉네임입니다.";
                msg.classList.add('available');
                checkNickname = true;
            }else{
                msg.innerText = "사용할 수 없는 닉네임입니다.";
                msg.classList.add('unavailable');
                checkNickname = false;
            }
        } catch (error) {
            console.error('닉네임 중복 확인 중 오류 발생:', error);
            msg.innerText = "오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
            msg.classList.add('unavailable');
        }
    })

})

// 다음 우편번호 API 실행 함수
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            const addr = data.address; // 최종 주소 변수

            // 주소 정보를 해당 필드에 넣는다.
            document.getElementById("address").value = addr;

        }
    }).open();
}
// 주소 검색 버튼 클릭 이벤트
const searchBtn = document.getElementById('addressSearchBtn');
if (searchBtn) {
    searchBtn.addEventListener('click', execDaumPostcode);
}

// 주소 입력창 클릭 시에도 검색 창 열기
const addressInput = document.getElementById('address');
if (addressInput) {
    addressInput.addEventListener('click', execDaumPostcode);
}