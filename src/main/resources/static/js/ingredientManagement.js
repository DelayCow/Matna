// 요소 선택
const searchInput = document.getElementById("search");
const newIngredientInput = document.getElementById("newIngredient");
const addBtn = document.querySelector(".add-btn");
const ingredientTable = document.getElementById("ingredientTable");
const newIngredientTable = document.getElementById("newIngredientTable");

// 검색 기능
searchInput.addEventListener("input", () => {
    const keyword = searchInput.value.trim();

    filterTable(ingredientTable, keyword);
    filterTable(newIngredientTable, keyword);
});

function filterTable(tbody, keyword) {
    const rows = tbody.querySelectorAll("tr");

    rows.forEach(row => {
        const ingredientName = row.children[1].textContent;

        if (ingredientName.includes(keyword)) {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });
}

// 재료 추가 기능
addBtn.addEventListener("click", () => {
    const value = newIngredientInput.value.trim();
    if (value === "") {
        alert("재료명을 입력하세요.");
        return;
    }

    const newRow = document.createElement("tr");
    newRow.innerHTML = `
        <td>-</td>
        <td>${value}</td>
        <td>홍길동</td>
        <td>${getNowDate()}</td>
        <td><button class="btn btn-danger btn-delete">삭제</button></td>
    `;

    ingredientTable.appendChild(newRow);
    newIngredientInput.value = "";

    attachDeleteEvent(newRow.querySelector(".btn-delete"));
});

// 삭제 버튼 기능
function attachDeleteEvent(button) {
    button.addEventListener("click", () => {
        if (confirm("정말 삭제하시겠습니까?")) {
            button.closest("tr").remove();
        }
    });
}

// 기존 삭제 버튼 모두 연결
document.querySelectorAll(".btn-danger").forEach(btn => {
    attachDeleteEvent(btn);
});

// 테이블 채우기
document.addEventListener('DOMContentLoaded', async () => {
    const tbody = document.getElementById('ingredientTable');

    try {
        const res = await fetch('/api/ingredients');
        const data = await res.json();

        tbody.innerHTML = ""; // 초기화

        data.forEach(item => {
            const row = `
                <tr>
                    <td>${item.ingredientNo}</td>
                    <td>${item.ingredientName}</td>
                    <td>${item.creatorNo}</td>
                    <td>${item.inDate}</td>
                    <td><button class="btn btn-danger">삭제</button></td>
                </tr>
            `;
            tbody.insertAdjacentHTML('beforeend', row);
        });

    } catch (err) {
        console.error('데이터 로딩 실패:', err);
    }
});

document.addEventListener('DOMContentLoaded', async () => {
    const tbody = document.getElementById('newIngredientTable');

    try {
        const res = await fetch('/api/ingredients');
        const data = await res.json();

        tbody.innerHTML = ""; // 초기화

        data.forEach(item => {
            const row = `
                <tr>
                    <td>${item.ingredientNo}</td>
                    <td>${item.ingredientName}</td>
                    <td>${item.creatorNo}</td>
                    <td>${item.inDate}</td>
                    <td><button class="btn btn-danger">삭제</button></td>
                </tr>
            `;
            tbody.insertAdjacentHTML('beforeend', row);
        });

    } catch (err) {
        console.error('데이터 로딩 실패:', err);
    }
});

// --------------------------------------------------------------------
// 승인 / 업데이트 버튼 기능
// --------------------------------------------------------------------
// 이유: 페이지 버튼들이 아무 동작도 하지 않으면 UI 테스트가 불가.
// 장점: 나중에 fetch로 서버 연동할 때 쉽게 교체할 수 있음.
// 변경 사항: alert만 띄움 (실제 서버 작업 시 fetch로 변경)
newIngredientTable.querySelectorAll("tr").forEach(row => {
    const approveBtn = row.querySelectorAll(".btn-primary")[0];
    const updateBtn = row.querySelectorAll(".btn-primary")[1];

    approveBtn.addEventListener("click", () => {
        alert(row.children[1].textContent + " 승인 처리");
    });

    updateBtn.addEventListener("click", () => {
        alert(row.children[1].textContent + " 업데이트 처리");
    });
});


// 날짜 생성 함수
function getNowDate() {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, "0");
    const dd = String(now.getDate()).padStart(2, "0");
    const hh = String(now.getHours()).padStart(2, "0");
    const mi = String(now.getMinutes()).padStart(2, "0");

    return `${yyyy}.${mm}.${dd} ${hh}:${mi}`;
}