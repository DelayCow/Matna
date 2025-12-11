
const tableBody = document.getElementById("groupBuyManagement");
const statusButtons = document.querySelectorAll(".search-box .btn-outline-secondary, .search-box .btn-outline-danger");
const titleInput = document.querySelector('.search-box input[type="text"]');
const dateInputs = document.querySelectorAll('.search-box input[type="date"]');
const caseInput = document.querySelector(".form-select")

let originalData = [];  // 원본 데이터 저장


// ==========================
// 1) Row 렌더링 (HTML 문자열)
// ==========================
function renderRow(item) {
    const badgeClass = item.status === "canceled" ? "bg-danger" : "bg-secondary";

    return `
        <tr data-id="${item.groupBuyNo}" data-qty="${item.quantityGroupBuyNo}" data-period="${item.periodGroupBuyNo}">
            <td>${item.groupBuyNo}</td>
            <td><span class="badge ${badgeClass}">${item.status}</span></td>
            <td>${item.inDate ?? ""}</td>
            <td>${item.creatorName ?? ""}</td>
            <td>${item.title ?? ""}</td>
            <td>${item.groupBuyCase ?? ""}</td>
            <td>
                <button class="btn btn-primary btn-detail btn-sm">상세보기</button>
                <button class="btn btn-danger btn-stop btn-sm">중단</button>
            </td>
        </tr>
    `;
}

// 2) HTML → DOM 변환
function createRow(item) {
    const template = document.createElement("template");
    template.innerHTML = renderRow(item).trim();
    return template.content.firstElementChild;
}

// 3) 렌더링 함수
function renderRows(list) {
    tableBody.innerHTML = "";
    list.forEach(item => tableBody.appendChild(createRow(item)));
}

// 4) 데이터 로드
async function loadGroupBuyList() {
    try {
        const params = new URLSearchParams({
            startDate: dateInputs[0].value || "",
            endDate: dateInputs[1].value || "",
            status: "",
            title: titleInput.value || ""
        });

        const res = await fetch(`/api/manager/groupBuyManagement`);
        if (!res.ok) throw new Error("데이터 조회 실패");

        const data = await res.json();
        originalData = data;
        renderRows(data);
    } catch (err) {
        console.error("loadGroupBuyList 오류:", err);
    }
}

// 5) 필터 기능
//유형 필터
function filterByCase(caseValue){
    const filtered = originalData.filter(item => item.groupBuyCase === caseValue)
    renderRows(filtered);
}

// 상태 필터
function filterByStatus(status) {
    const filtered = originalData.filter(item => item.status === status);
    renderRows(filtered);
}

// 제목 필터
function filterByTitle(keyword) {
    const filtered = originalData.filter(item =>
        item.title?.includes(keyword)
    );
    renderRows(filtered);
}

// 날짜 필터
function filterByDate() {
    const [start, end] = Array.from(dateInputs).map(i => i.value ? new Date(i.value) : null);

    let filtered = originalData;

    if (start) {
        filtered = filtered.filter(item => new Date(item.inDate) >= start);
    }
    if (end) {
        filtered = filtered.filter(item => new Date(item.inDate) <= end);
    }

    renderRows(filtered);
}

// 6) 이벤트 위임 (상세보기 / 중단)
tableBody.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;

    const tr = btn.closest("tr");
    const qtyNo = tr.dataset.qty;
    const periodNo = tr.dataset.period;

    // 상세보기 이동
    if (btn.classList.contains("btn-detail")) {

        if (periodNo && periodNo !== "0" && periodNo !== "" && periodNo !== "null") {
            // 기간 공구
            location.href = `/periodGroupBuy/detail/${periodNo}`;
        }
        else if (qtyNo && qtyNo !== "0" && qtyNo !== "" && qtyNo !== "null") {
            // 수량 공구
            location.href = `/quantityGroupBuy/detail/${qtyNo}`;
        }
        else {
            alert("상세 정보를 찾을 수 없습니다.");
        }

        return;
    }

    // 공구 중단
    if (btn.classList.contains("btn-stop")) {

        // 공통적으로 groupBuyNo 필요함
        const groupBuyNo = periodNo || qtyNo;

        if (!confirm("정말 중단하시겠습니까?")) return;

        fetch(`/api/manager/groupBuyManagement?groupBuyNo=${groupBuyNo}`, { method: "PUT" })
            .then(res => {
                if (!res.ok) throw new Error("중단 실패");

                tr.querySelector(".badge").textContent = "중단";
                tr.querySelector(".badge").classList.remove("bg-secondary");
                tr.querySelector(".badge").classList.add("bg-danger");

                // 원본 데이터 업데이트
                const target = originalData.find(it =>
                    it.periodGroupBuyNo == periodNo || it.quantityGroupBuyNo == qtyNo
                );
                if (target) target.status = "중단";
            })
            .catch(err => alert("중단 실패: " + err.message));
    }
});



// 7) 이벤트 바인딩
//유형
caseInput.addEventListener("change", () => {
    const value = caseInput.value.trim();
    if (value === "전체") {
        renderRows(originalData);
    } else {
        filterByCase(value);
    }
});

// 상태 버튼
statusButtons.forEach(btn => {
    btn.addEventListener("click", () => {
        filterByStatus(btn.textContent.trim());
    });
});

// 제목 검색
titleInput.addEventListener("input", () => {
    filterByTitle(titleInput.value.trim());
});

// 날짜 검색
dateInputs.forEach(input => input.addEventListener("change", filterByDate));


// ==========================
// 8) 페이지 로드 후 실행
// ==========================
document.addEventListener("DOMContentLoaded", loadGroupBuyList);
