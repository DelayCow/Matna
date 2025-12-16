document.getElementById('login-btn').addEventListener('click', function(){
    const id = document.querySelector('#memberId');
    const pw = document.querySelector('#password');
    let msg = '아이디/비밀번호를 다시 입력해주세요';
    const loginData = {
        memberId: id.value,
        password: pw.value
    };
    fetch('/api/auth/login',{
        method : "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginData)
    }).then(response => {
        sessionStorage.au = response.headers.authorization;
        return response.json();
    }).then(data => {
        console.log(data)
        location.href = data.redirectUrl;
        msg = data.message;
    }).catch(e => {
        console.error("로그인 실패", e.message)
    }).finally(()=>{
        id.value = '';
        pw.value = '';
        document.querySelector('#message').innerHTML = msg;
    });
})
document.getElementById('register-btn').addEventListener('click', function(){
    location.href='/register'
})
