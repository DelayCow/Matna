document.addEventListener("DOMContentLoaded", () => {

    const tableBody = document.querySelector(".table-content tbody");
    const searchInput = document.querySelector(".search-section input[type='text']");
    const categorySelect = document.querySelector(".search-section select");
    const statusButtons = document.querySelectorAll(".search-section .btn");
    const searchBtn = document.querySelector(".search-section button.btn-primary");

    let reportList = [];   // 전체 데이터
    let filteredList = []; // 필터 적용된 데이터


    // 🔥 초기 데이터 로딩
    loadReportData();


    // =====================================================================
    // 1) API에서 신고 리스트 호출
    // =====================================================================
    async function loadReportData() {
        try {
            // 실제 엔드포인트로 변경해야 함
            const res = await fetch("/api/reports");
            reportList = await res.json();
            filteredList = reportList;

            renderTable(filteredList);

        } catch (error) {
            console.error("신고 데이터 불러오기 실패:", error);
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
                    <td>${item.reportDate}</td>
                    <td>${item.reporter}</td>
                    <td>${shorten(item.content)}</td>
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


    // =====================================================================
    // 3) 검색 버튼 클릭
    // =====================================================================
    searchBtn.addEventListener("click", () => {
        applyFilters();
    });


    // =====================================================================
    // 4) 상태 버튼 클릭 필터
    // =====================================================================
    statusButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            const status = btn.textContent.trim();
            applyFilters(status);
        });
    });


    // =====================================================================
    // 5) 전체 필터링 함수
    // =====================================================================
    function applyFilters(statusFilter = null) {
        const keyword = searchInput.value.trim();
        const category = categorySelect.value;

        filteredList = reportList.filter(item => {
            let ok = true;

            // 상태 필터
            if (statusFilter && statusFilter !== "전체") {
                ok = ok && item.status === statusFilter;
            }

            // 카테고리 필터
            if (category !== "전체") {
                ok = ok && item.type.includes(category);
            }

            // 검색 필터 (닉네임 + 아이디)
            if (keyword) {
                ok = ok && item.reporter.includes(keyword);
            }

            return ok;
        });

        renderTable(filteredList);
    }

});
