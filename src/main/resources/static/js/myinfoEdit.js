const bankList = [
    { name: "KB국민은행" }, { name: "신한은행" }, { name: "우리은행" }, { name: "하나은행" }, { name: "NH농협은행" }, { name: "IBK기업은행" }, { name: "SC제일은행" }, { name: "카카오뱅크" }, { name: "케이뱅크" }, { name: "토스뱅크" }, { name: "부산은행" }, { name: "대구은행" }, { name: "광주은행" }, { name: "전북은행" }, { name: "경남은행" }, { name: "제주은행" }, { name: "수협은행" }, { name: "한국씨티은행" }, { name: "KDB산업은행" }, { name: "우체국" },
];
document.addEventListener('DOMContentLoaded', function() {
    const bankSelect = document.getElementById('bank');
    bankList.forEach(bank => {
        const option = document.createElement('option');
        option.value = bank.name;
        option.textContent = bank.name;
        bankSelect.appendChild(option);
    })

    const pathParts = window.location.pathname.split('/');
    const urlMemberNo = pathParts[pathParts.length - 2] === 'mypage' ? null : parseInt(pathParts[pathParts.length - 2]);
    // let memberNo = null;

    fetchMemberInfo(urlMemberNo);

    const addressBtn = document.getElementById('addressSearchBtn');
    if (addressBtn) {
        addressBtn.addEventListener('click', findOtherAdress);
    }

    document.getElementById('register-btn').addEventListener('click', function (e) {
        e.preventDefault();

        const password = document.getElementById('password').value
        const confirmPassword = document.getElementById('confirmPassword').value
        const msg = document.getElementById('pwcheckmsg').classList;

        if (password != confirmPassword) {
            msg.add('unavailable')

            msg.remove('notshow');

            document.getElementById('password').focus();

            return;
        }
        const formData = new FormData();


        formData.append("nickname", document.getElementById('nickname').value);
        formData.append("bank", document.getElementById('bank').value);
        formData.append("accountNumber", document.getElementById('accountNumber').value);
        formData.append("accountName", document.getElementById('accountName').value);
        formData.append("address", document.getElementById('address').value);

        if (password) {
            formData.append("password", password);
        }

        const fileInput = document.getElementById('profileFileUpload');

        if (fileInput.files[0]) {
            formData.append("profileImage", fileInput.files[0]);
        }

        const memberNo = document.getElementById('memberNo').value;

        api.fetch(`/api/mypage/${memberNo}/infoEdit`, {
            method: 'PUT',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    alert("회원 정보가 수정되었습니다.");
                    window.location.reload();
                } else {
                    alert("수정에 실패했습니다.");
                    console.error(response);
                }
            })
            .catch(error => {
                console.error("오류 발생:", error);
                alert("서버 통신 중 오류가 발생했습니다.");
            });
    });


    document.getElementById('nicknameCheck').addEventListener('click', function () {
        const nicknameInput = document.getElementById('nickname');
        const currentNickname = nicknameInput.value;
        const msg = document.getElementById('nicknamecheckmsg');

        const isDuplicated = (currentNickname === "기존닉네임" || currentNickname === "사용불가닉");

        msg.classList.remove('notshow'); // 메시지 표시

        if (!isDuplicated) {
            msg.innerText = `"${currentNickname}"은(는) 사용할 수 있는 닉네임입니다.`;
            msg.classList.add('available');
            msg.classList.remove('unavailable');
        } else {
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

    editProfileBtn.addEventListener('click', function (e) {
        e.preventDefault();
        profileFileUpload.click();
    });
    profileFileUpload.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                profileImageElement.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });
    deleteProfileBtn.addEventListener('click', function (e) {
        e.preventDefault();
        profileImageElement.src = '/img/user.png';
        profileFileUpload.value = '';
    });
});

// controller 기능 빼면서 생김
function fetchMemberInfo(memberNo) {

    api.fetch(`/api/mypage/${memberNo}/infoEditFill`)
        .then(response => {
            if (!response.ok) throw new Error('정보 불러오기 실패');
            return response.json();
        }).then(member => {
            if (member){
                document.getElementById('memberNo').value = member.memberNo || '';
                document.getElementById('memberId').value = member.memberId || '';
                document.getElementById('nickname').value = member.nickname || '';
                document.getElementById('accountNumber').value = member.accountNumber || '';
                document.getElementById('accountName').value = member.accountName || '';
                document.getElementById('address').value = member.address || '';


                if(member.bank) {
                    document.getElementById('bank').value = member.bank;
                }

                const imgTag = document.getElementById('profileImage');
                if(member.imageUrl) {
                    imgTag.src = member.imageUrl;
                }
            }
    }).catch(error => console.error('내정보 로딩 실패', error));
}

function findOtherAdress() {
    new daum.Postcode({
        oncomplete: function (data){
        var addr = '';
        if (data.userSelectedType === 'R') {
            addr = data.roadAddress;
    } else {
            addr = data.jibunAddress;
    }
        document.getElementById('address').value = addr;
        }
    }).open();
}
