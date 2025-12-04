document.addEventListener("DOMContentLoaded", function () {
    const currentPage = window.location.pathname.split("/").pop();
    const links = document.querySelectorAll(".nav-link");

    links.forEach(link => {
        const linkPage = link.getAttribute("href").split("/").pop();
        console.log(linkPage)
        console.log(currentPage)
        if (linkPage === currentPage) {
            link.classList.add("active");
        }
    });
});