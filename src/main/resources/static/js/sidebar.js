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
});