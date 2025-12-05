
document.getElementById('login-btn').addEventListener('click', function(){
    document.querySelector('form').submit();
})
document.getElementById('register-btn').addEventListener('click', function(){
    //회원가입 폼 주는 api
    location.href='/register'
})
