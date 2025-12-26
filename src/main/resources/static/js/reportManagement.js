import {showAlertModal} from "./modal.js";
document.addEventListener("DOMContentLoaded", () => {

    const tableBody = document.querySelector("#reportManagement");

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
            const res = await api.fetch(`/api/manager/reportManagement`);
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
            const statusMap = {
                WIP: "진행중",
                rejection: "반려",
                complete: "완료"
            }
            const row = `
                <tr data-id="${item.managerReportId}">
                    <td>${item.managerReportId}</td>
                    <td><span class="badge ${statusColor(item.status)}">${statusMap[item.status]}</span></td>
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
            case "WIP": return "bg-danger text-white";
            case "rejection": return "bg-warning text-dark";
            case "complete": return "bg-success text-white";
            default: return "bg-secondary";
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

    const reporterImageUrl = document.getElementById("reporterImageUrl");
    const reporterName = document.getElementById("reporterName");
    const reporterId = document.getElementById("reporterId");

    const targetImageUrl = document.getElementById("targetImageUrl");
    const targetName = document.getElementById("targetName");
    const targetId = document.getElementById("targetId");

    const imageUrl = document.getElementById("imageUrl");
    const reportReason = document.getElementById("reportReason");


    function bindReportDetail(data) {

        reporterImageUrl.src = data.reporterImageUrl ?? "/img/default-profile.png";
        reporterName.textContent = data.reporterName ?? "-";
        reporterId.textContent = data.reporterId ?? "";

        if (data.type === "회원 신고") {
            targetImageUrl.src = data.targetImageUrl ?? "/img/default-profile.png";
            targetName.textContent = data.targetName ?? "-";
            targetId.textContent = data.targetId ?? "";
        } else {
            targetImageUrl.src = data.groupBuyImageUrl ?? "/img/default-profile.png";
            targetName.textContent = data.groupBuyTitle ?? "-";
            targetId.textContent = data.groupBuyNo ?? "";
        }

        imageUrl.src = data.imageUrl ?? "-";
        reportReason.value = data.reason ?? "";
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

    const reportDetailModal = new bootstrap.Modal(
        document.getElementById("reportDetailModal")
    );

    function openReportDetail(reportId) {
        const data = originalData.find(
            item => item.managerReportId === Number(reportId)
        );

        if (!data) {
            console.error("해당 신고 데이터 없음:", reportId);
            return;
        }

        bindReportDetail(data);
        reportDetailModal.show();
    }



    // ================================
    // 5) 상세보기 이벤트 위임
    // ================================
    tableBody.addEventListener("click", (e) => {
        const btn = e.target.closest(".btn-detail");
        if (!btn) return;

        const tr = btn.closest("tr");
        const reportId = tr.dataset.id;

        openReportDetail(reportId);
    });

    let currentReportId = null;

// 상세 열었을 때 id 저장
    function openReportDetail(reportId) {
        currentReportId = reportId;
        const data = originalData.find(item => item.managerReportId === Number(reportId));

        if (!data) return;

        bindReportDetail(data);
        reportDetailModal.show();
    }

    document.getElementById("completeBtn").addEventListener("click", async () => {
        if (!currentReportId) return;

        try {
            await api.fetch(`/api/manager/reportManagement/complete?reportNo=${currentReportId}`, {
                method: "PUT"
            });

            showAlertModal(
                '신고 처리',
                '처리 완료되었습니다.',
                'success',
            )
            reportDetailModal.hide();
            loadReportData(); // 테이블 최신화
        } catch (e) {
            console.error(e);
            showAlertModal(
                '신고 처리',
                '네트워크 오류가 발생하였습니다.',
                'error',
            )
        }
    });

    document.getElementById("rejectBtn").addEventListener("click", async () => {
        if (!currentReportId) return;

        try {
            await api.fetch(`/api/manager/reportManagement/rejection?reportNo=${currentReportId}`, {
                method: "PUT"
            });

            showAlertModal(
                '신고 처리',
                '반려되었습니다.',
                'success',
            )
            reportDetailModal.hide();
            loadReportData();
        } catch (e) {
            console.error(e);
            showAlertModal(
                '신고 처리',
                '네트워크 오류가 발생하였습니다.',
                'error',
            )
        }
    });


    // ================================
    // 6) 페이지 로드 시 실행
    // ================================
    loadReportData();
});

