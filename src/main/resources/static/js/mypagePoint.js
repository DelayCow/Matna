document.addEventListener('DOMContentLoaded', function() {
    const isMyPage = true;

    const userData = {
        name: "베베는오리",
        image: "/img/user1.png",
        money: 5600,
        isOwner: isMyPage
    };

    // 1. 헤더 & 프로필
    const renderCommonArea = () => {
        const headerArea = document.getElementById('header-right-area');
        const profileArea = document.getElementById('profile-main-area');
        const btnsArea = document.getElementById('profile-action-btns');

        if (userData.isOwner) {
            headerArea.innerHTML = `<button class="btn p-0 border-0" id="headerMenuBtn"><i class="bi bi-three-dots-vertical fs-4 text-dark"></i></button>
            <ul class="custom-dropdown" id="headerDropdown"><li><a href="#">정보 수정</a></li><li><a href="#">로그아웃</a></li><li><a href="#" class="text-danger">탈퇴</a></li></ul>`;
        } else { headerArea.innerHTML = ''; }

        let subInfo = userData.isOwner
            ? `<small class="text-muted">내 맛나머니 : ${userData.money.toLocaleString()} 원</small>`
            : `<button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1"><i class="bi bi-exclamation-circle me-1"></i>신고하기</button>`;

        profileArea.innerHTML = `<img src="${userData.image}" class="rounded-circle border me-3" width="60" height="60"><div><h5 class="fw-bold mb-1">${userData.name}</h5><div>${subInfo}</div></div>`;

        btnsArea.innerHTML = userData.isOwner ? '' : `
            <div class="d-flex gap-2">
                <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">채팅</button>
                <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">팔로우</button>
            </div>`;
    };
    renderCommonArea();
});