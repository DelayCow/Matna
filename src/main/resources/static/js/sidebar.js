document.addEventListener("DOMContentLoaded", function () {
    const currentPage = window.location.pathname.split("/").pop();
    const links = document.querySelectorAll(".sidebar-link");

    links.forEach(link => {
        const linkPage = link.getAttribute("href").split("/").pop();
        console.log(currentPage)
        console.log(linkPage)
        if (linkPage === currentPage) {
            link.classList.add("active");
        }
    });

    document.getElementById("logoutBtn").addEventListener("click", () => {
        window.location.href = "/logout";
    });

    loadNickname();
});

async function loadNickname() {
    try {
        const res = await api.fetch("/api/manager/sidebar");

        if (!res.ok) {
            console.error("닉네임 요청 실패");
            return;
        }

        const nickname = await res.text();  // ← JSON 아니고 문자열이니까 text()

        const nickEl = document.getElementById("nickname");
        nickEl.textContent = nickname;

    } catch (err) {
        console.error("에러:", err);
    }
}