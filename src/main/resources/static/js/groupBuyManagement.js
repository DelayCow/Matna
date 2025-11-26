// 테이블 데이터 수집
const tableBody = document.querySelector("table tbody");
let originalRows = Array.from(tableBody.querySelectorAll("tr")); // 원본 보관

// 상태 필터 버튼 클릭 이벤트
document.querySelectorAll(".search-box .btn-outline-secondary, .search-box .btn-outline-danger")
    .forEach(btn => {
        btn.addEventListener("click", () => {
            const filterText = btn.textContent.trim();
            filterByStatus(filterText);
        });
    });

function filterByStatus(status) {
    const filtered = originalRows.filter(row => {
        const badge = row.querySelector(".badge-status");
        return badge && badge.textContent.trim() === status;
    });

    renderRows(filtered);
}

// 제목 검색 (검색명 입력 후 버튼 클릭)
document.querySelector(".search-box .btn-primary").addEventListener("click", () => {
    const keyword = document.querySelector('.search-box input[type="text"]').value.trim();
    const filtered = originalRows.filter(row => {
        const title = row.children[4].textContent.trim();
        return title.includes(keyword);
    });

    renderRows(filtered);
});

// 날짜 검색 (between date1, date2)
const dateInputs = document.querySelectorAll('.search-box input[type="date"]');

dateInputs.forEach(input => {
    input.addEventListener("change", filterByDate);
});

function filterByDate() {
    const [start, end] = Array.from(dateInputs).map(i => i.value ? new Date(i.value) : null);

    let filtered = originalRows;

    if (start) {
        filtered = filtered.filter(row => {
            const dateText = row.children[2].textContent.replace(/\./g, "-");
            const rowDate = new Date(dateText);
            return rowDate >= start;
        });
    }

    if (end) {
        filtered = filtered.filter(row => {
            const dateText = row.children[2].textContent.replace(/\./g, "-");
            const rowDate = new Date(dateText);
            return rowDate <= end;
        });
    }

    renderRows(filtered);
}

// 상세보기 버튼 이벤트
function attachDetailEvent(btn) {
    btn.addEventListener("click", () => {
        alert("상세보기 페이지로 이동합니다.");
        // 필요 시 location.href = "/detail?id=xxx";
    });
}

// 중단하기 버튼 이벤트
function attachStopEvent(btn) {
    btn.addEventListener("click", () => {
        if (confirm("정말 중단하시겠습니까?")) {
            const row = btn.closest("tr");
            row.querySelector(".badge-status").textContent = "중단";
            row.querySelector(".badge-status").classList.remove("bg-secondary");
            row.querySelector(".badge-status").classList.add("bg-danger");
            alert("중단되었습니다.");
        }
    });
}

// 렌더링 함수 (공통)
function renderRows(rows) {
    tableBody.innerHTML = ""; // 초기화
    rows.forEach(row => {
        tableBody.appendChild(row);
    });
    rebindButtons(); // 버튼 다시 바인딩
}

// 테이블 버튼 다시 바인딩
function rebindButtons() {
    document.querySelectorAll(".btn-primary.btn-sm").forEach(attachDetailEvent);
    document.querySelectorAll(".btn-danger.btn-sm").forEach(attachStopEvent);
}

// 초기 바인딩
rebindButtons();
