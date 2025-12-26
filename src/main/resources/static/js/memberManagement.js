import {showAlertModal} from './modal.js';

const tableBody = document.getElementById("memberManagement");
const titleInput = document.querySelector('.search-box input[type="text"]');
const dateInputs = document.querySelectorAll('.search-box input[type="date"]');
const searchButton = document.querySelector('.search-box button[type="button"], .search-box button.btn-primary');

let originalData = [];  // 서버에서 받은 원본 데이터 저장 (캐시 용)

// --------------------------
// 1) Row 렌더링 (회원 DTO 기준)
// --------------------------
function renderRow(member) {
    // banDate 표시용 포맷 (있을 때만)
    const banInfo = member.banDate ? new Date(member.banDate).toLocaleString() : "-";
    const statusText = member.accountStatus === "ban" ? "정지" : "활성";
    const statusClass = member.accountStatus === "ban" ? "text-danger fw-bold" : "text-success";
    return `
        <tr data-id="${member.memberNo}">
            <td>${escapeHtml(member.nickname ?? "")}</td>
            <td>${escapeHtml(member.memberId ?? "")}</td>
            <td class="${statusClass}">${escapeHtml(statusText)}</td>
            <td>
                <div class="d-flex gap-2 align-items-center">
                    <select class="form-select ban-select" id="select-${member.memberNo}">
                        <option value="">정지 선택</option>
                        <option value="1">1일 정지</option>
                        <option value="3">3일 정지</option>
                        <option value="7">7일 정지</option>
                        <option value="30">30일 정지</option>
                        <option value="9999">영구정지</option>
                    </select>
                    <button class="btn btn-danger btn-sm ban-btn" data-member-no="${member.memberNo}">확인</button>
                </div>
                <div class="small text-muted mt-1">최근 정지일: ${escapeHtml(banInfo)}</div>
            </td>
        </tr>
    `;
}

// 간단한 HTML 이스케이프 (XSS 방지)
function escapeHtml(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// 2) HTML 문자열 → DOM 노드
function createRow(member) {
    const template = document.createElement("template");
    template.innerHTML = renderRow(member).trim();
    return template.content.firstElementChild;
}

// 3) 테이블 렌더링
function renderRows(list) {
    tableBody.innerHTML = "";
    if (!Array.isArray(list) || list.length === 0) {
        const tr = document.createElement("tr");
        tr.innerHTML = `<td colspan="5" class="text-center text-muted">조회된 회원이 없습니다.</td>`;
        tableBody.appendChild(tr);
        return;
    }
    list.forEach(member => tableBody.appendChild(createRow(member)));
}

// --------------------------
// 4) 서버 조회 (필터 포함)
// --------------------------
async function loadMemberList() {
    try {
        const params = new URLSearchParams({
            startDate: (dateInputs[0] && dateInputs[0].value) || "",
            endDate: (dateInputs[1] && dateInputs[1].value) || "",
            keyword: titleInput.value || ""
        });

        // 쿼리 스트링 붙여서 요청
        const res = await api.fetch(`/api/manager/memberManagement?${params}`);
        if (!res.ok) {
            const text = await res.text().catch(() => null);
            throw new Error(text || "데이터 조회 실패");
        }

        const data = await res.json();
        originalData = data || [];
        renderRows(originalData);

    } catch (err) {
        console.error("loadMemberList 오류:", err);
    }
}

// --------------------------
// 5) 정지 적용 (applyBan) - async/await 버전
// --------------------------
async function applyBan(memberNo) {
    try {
        const select = document.querySelector(`#select-${memberNo}`);
        if (!select) {
            console.log("정지 기간 선택 요소를 찾을 수 없습니다.");
            return;
        }

        const days = Number(select.value);
        if (!days) {
            showAlertModal(
                '회원 정지',
                '정지 기간을 선택하세요.',
                'error',
            );
            return;
        }

        // banDate 계산 (String 형태)
        let banDate;
        if (days === 9999) {
            banDate = "9999-12-31T23:59:59";
        } else {
            const now = new Date();
            now.setDate(now.getDate() + days);
            banDate = now.toISOString().slice(0, 19); // "YYYY-MM-DDTHH:mm:ss"
        }

        const url = `/api/manager/memberManagement/ban?memberNo=${memberNo}&days=${days}`;

        const res = await api.fetch(url, {
            method: "PUT"
        });

        if (!res.ok) {
            const text = await res.text().catch(() => null);
            throw new Error(text || "정지 적용 실패");
        }

        // UI 업데이트
        const tr = tableBody.querySelector(`tr[data-id="${memberNo}"]`);
        if (tr) {
            const statusTd = tr.children[2];
            statusTd.textContent = "정지";

            const smallEl = tr.querySelector(".small.text-muted");
            if (smallEl) smallEl.textContent = `정지일: ${new Date(banDate).toLocaleString()}`;
        }

        // 데이터 반영
        const target = originalData.find(m => m.memberNo == memberNo);
        if (target) {
            target.accountStatus = "정지";
            target.banDate = banDate;
        }

        showAlertModal(
            '회원 정지',
            '정지 처리되었습니다.',
            'success',
        );

    } catch (err) {
        console.error("applyBan 오류:", err);
        showAlertModal(
            '회원 정지',
            '정지 적용 중 오류가 발생했습니다.',
            'error',
        );
    }
}


tableBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("ban-btn")) {
        const memberNo = Number(e.target.dataset.memberNo);
        applyBan(memberNo);
    }
});
// --------------------------
// 7) 검색/날짜 변경 이벤트 (서버 필터링 사용)
// --------------------------
if (searchButton) {
    searchButton.addEventListener("click", () => loadMemberList());
}
if (titleInput) {
    titleInput.addEventListener("input", () => {
        // 즉시 검색하거나 debounce를 넣을 수 있음
        loadMemberList();
    });
}
dateInputs.forEach(input => input.addEventListener("change", () => loadMemberList()));

// --------------------------
// 8) 페이지 로드 시 최초 실행
// --------------------------
document.addEventListener("DOMContentLoaded", loadMemberList);
