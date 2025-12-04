// ingredient-management.js (덮어쓰기)
const searchInput = document.getElementById("search");
const newIngredientInput = document.getElementById("newIngredient");
const addBtn = document.querySelector(".add-btn");
const ingredientTable = document.getElementById("ingredientTable"); // 기존 승인된 재료 테이블 tbody
const newIngredientTable = document.getElementById("newIngredientTable"); // 승인 대기 테이블 tbody

// row 렌더링 함수
function renderApprovedRow(item) {
    return `
        <tr data-id="${item.ingredientId}">
            <td>${item.ingredientId ?? "-"}</td>
            <td>${item.ingredientName ?? ""}</td>
            <td>${item.creatorName ?? ""}</td>
            <td>${item.inDate ?? ""}</td>
            <td><button class="btn btn-danger btn-delete">삭제</button></td>
        </tr>
    `;
}
function renderNotApprovedRow(item) {
    return `
        <tr data-id="${item.ingredientId}">
            <td>${item.ingredientId ?? "-"}</td>
            <td>${item.ingredientName ?? ""}</td>
            <td>${item.creatorName ?? ""}</td>
            <td>${item.inDate ?? ""}</td>
            <td>
                <button class="btn btn-primary btn-approve">승인</button>
                <button class="btn btn-danger btn-delete">삭제</button>
            </td>
        </tr>
    `;
}

// --- 필터 함수 (case-insensitive) ---
function filterTable(tbody, keyword) {
    tbody.querySelectorAll("tr").forEach(row => {
        const name = row.children[1]?.textContent;
        row.style.display = name.includes(keyword) ? "" : "none";
    });
}

// --- 이벤트 위임: 테이블 단위로 클릭 처리 ---
ingredientTable.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    const tr = btn.closest("tr");
    const id = tr?.dataset?.id;
    if (btn.classList.contains("btn-delete")) {
        if (confirm("정말 삭제하시겠습니까?")) {
            fetch(`/api/manager/ingredientManagement?ingredientId=${id}`, { method: "DELETE" })
                .then(res => {
                    if (!res.ok) throw new Error("삭제 실패");
                    tr.remove();
                })
                .catch(err => alert("삭제 실패: " + err.message));
        }
    }
});

newIngredientTable.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    const tr = btn.closest("tr");
    const id = tr?.dataset?.id;
    if (btn.classList.contains("btn-approve")) {
        if (confirm("승인하시겠습니까?")) {
            fetch(`/api/manager/ingredientManagement?ingredientId=${id}`, { method: "PUT" })
                .then(res => {
                    if (!res.ok) throw new Error("승인 실패");
                    // 승인되면 행을 승인 테이블로 이동 (간단 처리: 페이지에서 제거 후 재로딩하거나 행 이동)
                    tr.remove();
                    // 선택적으로 재로딩: loadApprovedIngredients();
                })
                .catch(err => alert("승인 실패: " + err.message));
        }
    } else if (btn.classList.contains("btn-delete")) {
        if (confirm("정말 삭제하시겠습니까?")) {
            fetch(`/api/manager/ingredientManagement?ingredientId=${id}`, { method: "DELETE" })
                .then(res => {
                    if (!res.ok) throw new Error("삭제 실패");
                    tr.remove();
                })
                .catch(err => alert("삭제 실패: " + err.message));
        }
    }
});

// --- 검색 input 이벤트 ---
searchInput.addEventListener("input", () => {
    const keyword = searchInput.value.trim();
    filterTable(ingredientTable, keyword);
    filterTable(newIngredientTable, keyword);
});

// --- 추가 버튼: 서버에 POST 요청 보내기 예시 ---
addBtn.addEventListener("click", () => {
    const value = newIngredientInput.value.trim();
    if (!value) { alert("재료명을 입력하세요."); return; }
    // 예: creatorId는 현재 로그인된 유저 id로 대체해야 함. 여기선 예시 1 사용.
    const creatorId = 1;
    fetch(`/api/manager/ingredientManagement?creatorId=${creatorId}&ingredientName=${encodeURIComponent(value)}`, {
        method: "POST"
    })
        .then(res => {
            if (!res.ok) throw new Error("추가 실패");
            return res.json();
        })
        .then(item => {
            // 서버가 바로 approveDate를 채우지 않는다면 새 행은 승인 대기 테이블로 올라가야 함.
            // 만약 서버에서 approveDate가 null이면 notApprovedTable에 넣기
            if (item.approveDate) {
                ingredientTable.insertAdjacentHTML('beforeend', renderApprovedRow(item));
            } else {
                newIngredientTable.insertAdjacentHTML('beforeend', renderNotApprovedRow(item));
            }
            newIngredientInput.value = "";
        })
        .catch(err => alert("추가 실패: " + err.message));
});

// --- 데이터 로드 함수들 (통합된 DOMContentLoaded 내부에서 실행) ---
async function loadApprovedIngredients() {
    try {
        const res = await fetch('/api/manager/ingredientManagement');
        if (!res.ok) throw new Error("서버 응답 에러");
        const data = await res.json();
        ingredientTable.innerHTML = "";
        data.forEach(item => ingredientTable.insertAdjacentHTML('beforeend', renderApprovedRow(item)));
    } catch (err) {
        console.error('데이터 로딩 실패 (approved):', err);
    }
}
async function loadNotApprovedIngredients() {
    try {
        const res = await fetch('/api/manager/ingredientManagement/notApproved');
        if (!res.ok) throw new Error("서버 응답 에러");
        const data = await res.json();
        newIngredientTable.innerHTML = "";
        data.forEach(item => newIngredientTable.insertAdjacentHTML('beforeend', renderNotApprovedRow(item)));
    } catch (err) {
        console.error('데이터 로딩 실패 (notApproved):', err);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    await Promise.all([loadApprovedIngredients(), loadNotApprovedIngredients()]);
});
