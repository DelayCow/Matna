document.addEventListener("DOMContentLoaded", () => {

    const tableBody = document.querySelector(".table-content tbody");
    const searchInput = document.querySelector("#keyword");
    const categorySelect = document.querySelector("#reportCase");
    const statusButtons = document.querySelectorAll(".status-buttons button");
    const searchBtn = document.querySelector(".search-btn");

    // 초기 데이터 로딩
    loadReportData();


    // =====================================================================
    // 1) API에서 신고 리스트 호출
    // =====================================================================
    async function loadReportData() {
        const params = new URLSearchParams({
            startDate: document.getElementById("startDate").value || "",
            endDate: document.getElementById("endDate").value || "",
            status: document.getElementById("status").value || "",
            reportCase: document.getElementById("reportCase").value || "",
            keyword: document.getElementById("keyword").value || "",
        });

        try {
            const res = await fetch(`/api/manager/reportManagement?${params}`);
            const data = await res.json();
            renderTable(data);

        } catch (e) {
            console.error("데이터 로드 실패:", e);
        }
    }

    // =====================================================================
    // 2) 테이블 렌더링 함수
    // =====================================================================
    function renderTable(list) {
        tableBody.innerHTML = "";

        if (!list.length) {
            tableBody.innerHTML = `
                <tr><td colspan="7" class="text-center text-muted">검색 결과가 없습니다.</td></tr>
            `;
            return;
        }

        list.forEach((item, index) => {
            const row = `
                <tr>
                    <td>${index + 1}</td>
                    <td><span class="btn btn-sm ${statusColor(item.status)}">${item.status}</span></td>
                    <td>${formatDate(item.reportedDate)}</td>
                    <td>${item.reporterName}</td>
                    <td>${shorten(item.reason)}</td>
                    <td>${item.type}</td>
                    <td><button class="btn btn-sm btn-primary">상세보기 🔍</button></td>
                </tr>
            `;
            tableBody.insertAdjacentHTML("beforeend", row);
        });
    }


    // 상태에 따라 색 적용
    function statusColor(status) {
        switch (status) {
            case "진행중": return "btn-danger";
            case "반려": return "btn-warning";
            case "완료": return "btn-success";
            default: return "btn-secondary";
        }
    }

    // 내용 길이 축약
    function shorten(text) {
        return text.length > 20 ? text.substring(0, 20) + "..." : text;
    }

    function formatDate(dateStr) {
        if (!dateStr) return "-";
        return dateStr.replace("T", ".");
    }

    // =====================================================================
    // 3) 검색 버튼 클릭
    // =====================================================================
    searchBtn.addEventListener("click", () => {
        loadReportData();
    });


    // =====================================================================
    // 4) 상태 버튼 클릭 필터
    // =====================================================================
    statusButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            document.getElementById("status").value = btn.dataset.status;
            loadReportData();
        });
    });


    // =====================================================================
    // 5) 전체 필터링 함수
    // =====================================================================
    categorySelect.addEventListener("change", () => {
        loadReportData();
    });

});
