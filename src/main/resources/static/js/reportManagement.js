document.addEventListener("DOMContentLoaded", () => {

    const tableBody = document.querySelector(".table-content tbody");

    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");
    const statusButtons = document.querySelectorAll(".status-buttons button");
    const categorySelect = document.getElementById("reportCase");
    const keywordInput = document.getElementById("keyword");
    const searchBtn = document.querySelector(".search-btn");

    let originalData = [];  // 원본 데이터 저장


    // ================================
    // 1) 최초 데이터 로딩
    // ================================
    async function loadReportData() {
        try {
            const res = await fetch(`/api/manager/reportManagement`);
            const data = await res.json();

            originalData = data;
            renderTable(data);

        } catch (e) {
            console.error("데이터 로드 실패:", e);
        }
    }


    // ================================
    // 2) 테이블 렌더링
    // ================================
    function renderTable(list) {
        tableBody.innerHTML = "";

        if (!list.length) {
            tableBody.innerHTML = `
                <tr><td colspan="7" class="text-center text-muted">검색 결과가 없습니다.</td></tr>
            `;
            return;
        }

        list.forEach((item) => {
            const row = `
                <tr data-id="${item.reportId}">
                    <td>${item.managerReportId}</td>
                    <td><span class="btn btn-sm ${statusColor(item.status)}">${item.status}</span></td>
                    <td>${formatDate(item.reportedDate)}</td>
                    <td>${item.reporterName ?? "-"}</td>
                    <td>${shorten(item.reason)}</td>
                    <td>${item.type}</td>
                    <td><button class="btn btn-primary btn-sm btn-detail">상세보기</button></td>
                </tr>
            `;
            tableBody.insertAdjacentHTML("beforeend", row);
        });
    }


    // 상태 색상
    function statusColor(status) {
        switch (status) {
            case "진행중": return "btn-danger";
            case "반려": return "btn-warning";
            case "완료": return "btn-success";
            default: return "btn-secondary";
        }
    }

    // 날짜 포맷
    function formatDate(dateStr) {
        if (!dateStr) return "-";
        return dateStr.replace("T", ".");
    }

    // 내용 축약
    function shorten(text) {
        return text?.length > 20 ? text.substring(0, 20) + "..." : text;
    }


    // ================================
    // 3) 필터링 (날짜, 상태, 유형, 키워드)
    // ================================
    function applyFilters() {
        let filtered = [...originalData];

        const start = startDateInput.value ? new Date(startDateInput.value) : null;
        const end = endDateInput.value ? new Date(endDateInput.value) : null;
        const status = document.getElementById("status").value;
        const reportCase = categorySelect.value;
        const typeMap = {
            "members": "회원 신고",
            "group_buys": "공동구매 신고"
        };
        const keyword = keywordInput.value.trim();



        // 날짜 필터
        if (start) {
            filtered = filtered.filter(item => new Date(item.reportedDate.split("T")[0]) >= start);
        }
        if (end) {
            filtered = filtered.filter(item => new Date(item.reportedDate.split("T")[0]) <= end);
        }

        // 상태 필터
        if (status) {
            filtered = filtered.filter(item => item.status === status);
        }

        // 유형 필터
        if (reportCase && reportCase !== "전체") {
            filtered = filtered.filter(item => item.type === typeMap[reportCase]);
        }

        // 제목/내용 키워드 필터
        if (keyword) {
            filtered = filtered.filter(item =>
                item.reason?.includes(keyword) ||
                item.reporterName?.includes(keyword)
            );
        }

        renderTable(filtered);
    }


    // ================================
    // 4) 이벤트 바인딩
    // ================================
    searchBtn.addEventListener("click", applyFilters);

    keywordInput.addEventListener("input", applyFilters);

    [startDateInput, endDateInput].forEach(input =>
        input.addEventListener("change", applyFilters)
    );

    categorySelect.addEventListener("change", applyFilters);

    statusButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            document.getElementById("status").value = btn.dataset.status;
            applyFilters();
        });
    });


    // ================================
    // 5) 상세보기 이벤트 위임
    // ================================
    tableBody.addEventListener("click", (e) => {
        const btn = e.target.closest(".btn-detail");
        if (!btn) return;

        const tr = btn.closest("tr");
        const reportId = tr.dataset.id;

        location.href = `/manager/reportManagement/detail/${reportId}`;
    });


    // ================================
    // 6) 페이지 로드 시 실행
    // ================================
    loadReportData();
});

