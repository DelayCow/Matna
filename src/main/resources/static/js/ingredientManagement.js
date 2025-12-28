import {showAlertModal} from './modal.js';

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
            <td>${formatDate(item.inDate) ?? ""}</td>
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
            <td>${formatDate(item.inDate) ?? ""}</td>
            <td>
                <button class="btn btn-primary btn-approve">승인</button>
                <button class="btn btn-danger btn-delete">삭제</button>
                <button class="btn btn-warning btn-update">업데이트</button>

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

// 날짜 포맷
function formatDate(dateStr) {
    if (!dateStr) return "-";
    return dateStr.replace("T", ".");
}

// --- 이벤트 위임: 테이블 단위로 클릭 처리 ---
ingredientTable.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    const tr = btn.closest("tr");
    const id = tr?.dataset?.id;
    if (btn.classList.contains("btn-delete")) {
        showAlertModal(
            '재료 삭제',
            '정말 삭제하시겠습니까?',
            'error',
            ()=>removeIngredient(tr, id)
        );

    }
});
const removeIngredient = function (tr, id){
    api.fetch(`/api/manager/ingredientManagement?ingredientId=${id}`, { method: "DELETE" })
        .then(res => {
            if (!res.ok) throw new Error("삭제 실패");
            tr.remove();
        })
        .catch(err =>
            showAlertModal(
                '삭제 실패',
                "삭제 실패: " + err.message,
                'error',
            )
        );
}
const confirmIngredient = function (tr, id){
    api.fetch(`/api/manager/ingredientManagement?ingredientId=${id}`, { method: "PUT" })
        .then(res => {
            if (!res.ok) throw new Error("승인 실패");
            // 승인되면 행을 승인 테이블로 이동 (간단 처리: 페이지에서 제거 후 재로딩하거나 행 이동)
            tr.remove();
            loadApprovedIngredients();
        })
        .catch(err => {
            showAlertModal(
                '재료 승인',
                '승인 실패:' + err.message,
                'info',
                ()=>confirmIngredient(tr, id)
            );
        }

        );
}
newIngredientTable.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;
    const tr = btn.closest("tr");
    const id = tr?.dataset?.id;
    if (btn.classList.contains("btn-approve")) {
        showAlertModal(
            '재료 승인',
            '승인하시겠습니까?',
            'info',
            ()=>confirmIngredient(tr, id)
        );
    } else if (btn.classList.contains("btn-delete")) {
        showAlertModal(
            '재료 삭제',
            '정말 삭제하시겠습니까?',
            'error',
            ()=>removeIngredient(tr, id)
        );
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
    if (!value) { showAlertModal(
        '재료 등록',
        '재료명을 입력하세요',
        'info'
    ); return;}
    api.fetch(`/api/manager/ingredientManagement?ingredientName=${encodeURIComponent(value)}`, {
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
                ingredientTable.insertAdjacentHTML('afterbegin', renderApprovedRow(item));
            } else {
                newIngredientTable.insertAdjacentHTML('afterbegin', renderNotApprovedRow(item));
            }
            newIngredientInput.value = "";
        })
        .catch(err => {
            showAlertModal(
                '재료 등록',
                "등록 실패: " + err.message,
                'info'
            )
        });
});

// --- 데이터 로드 함수들 (통합된 DOMContentLoaded 내부에서 실행) ---
async function loadApprovedIngredients() {
    try {
        const res = await api.fetch('/api/manager/ingredientManagement');
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
        const res = await api.fetch('/api/manager/ingredientManagement/notApproved');
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

//모달
let selectedIngredientId = null;
let selectedIngredientName = null;
let changeIngredientId = null;
let changeIngredientName = null;

const modal = new bootstrap.Modal(document.getElementById("ingredientChangeModal"));
const modalSearchInput = document.getElementById("modalSearchInput");
const modalIngredientList = document.getElementById("modalIngredientList");

newIngredientTable.addEventListener("click", (e) => {
    const btn = e.target.closest("button");
    if (!btn) return;

    const tr = btn.closest("tr");

    if (btn.classList.contains("btn-update")) {
        selectedIngredientId = tr.dataset.id;
        selectedIngredientName = tr.children[1].textContent;

        document.getElementById("selectedIngredientText").textContent =
            `선택된 재료: ${selectedIngredientName}`;

        document.getElementById("changeIngredientText").textContent = "-";
        document.getElementById("approvedMark").classList.add("d-none");

        modalIngredientList.innerHTML = "";
        modalSearchInput.value = "";

        modal.show();
    }
});

modalSearchInput.addEventListener("input", () => {
    const keyword = modalSearchInput.value.trim();
    modalIngredientList.innerHTML = "";

    if (!keyword) return;

    ingredientTable.querySelectorAll("tr").forEach(tr => {
        const name = tr.children[1].textContent;
        if (name.includes(keyword)) {
            const id = tr.dataset.id;

            const li = document.createElement("li");
            li.className = "list-group-item list-group-item-action";
            li.textContent = name;

            li.addEventListener("click", () => {
                changeIngredientId = id;
                changeIngredientName = name;

                document.getElementById("changeIngredientText").textContent = name;
                document.getElementById("approvedMark").classList.remove("d-none");
            });

            modalIngredientList.prepend(li);
        }
    });
});
const editIngredient = async function (){
    try {
        const res = await api.fetch(
            `/api/manager/ingredientManagement/change?ingredientNo=${selectedIngredientId}&newIngredientNo=${changeIngredientId}`,
            { method: "PUT" }
        );

        if (!res.ok) throw new Error("변경 실패");
        showAlertModal(
            '재료 변경',
            '재료가 변경되었습니다.',
            'success',
        );

        await Promise.all([
            loadApprovedIngredients(),
            loadNotApprovedIngredients()
        ]);

    } catch (err) {
        showAlertModal(
            '재료 변경',
            '재료 변경 중 오류:' + err.message,
            'error',
        );
    }
}
document.getElementById("confirmChangeBtn").addEventListener("click", async () => {
    if (!selectedIngredientId || !changeIngredientId) {
        modal.hide();
        showAlertModal(
            '재료 변경',
            '변경할 재료를 선택해주세요.',
            'error',
        );
        return;
    }
    modal.hide();
    showAlertModal(
        '재료 변경',
        '정말 재료를 변경하시겠습니까?',
        'error',
        () => editIngredient()
    );

});
