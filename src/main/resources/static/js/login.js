document.getElementById('login-btn').addEventListener('click', function(){
    const id = document.querySelector('#memberId');
    const pw = document.querySelector('#password');
    const messageElement = document.querySelector('#message');

    let msg = '로그인 중...';

    const loginData = {
        memberId: id.value,
        password: pw.value
    };

    fetch('/api/auth/login', {
        method : "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    })
        .then(async response => {
            if (response.ok) {
                sessionStorage.au = response.headers.get('Authorization');
                return response.json();
            }
            const errorData = await response.json();
            throw new Error(errorData.error || '아이디/비밀번호를 다시 입력해주세요');
        })
        .then(data => {
            location.href = data.redirectUrl;
        })
        .catch(e => {
            console.error("로그인 실패:", e.message);
            msg = e.message;
        })
        .finally(() => {
            id.value = '';
            pw.value = '';
            messageElement.innerHTML = msg;
        });
});
document.getElementById('register-btn').addEventListener('click', function(){
    location.href='/register'
})
