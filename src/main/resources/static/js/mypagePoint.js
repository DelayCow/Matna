
const inputEl = document.getElementById('chargeAmountInput');
let currentAmount = 0;

// 숫자 콤마 포맷팅
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 금액 추가 버튼 (+1천, +5천 등)
window.addAmount = function(amount) { // window. 붙여서 전역으로 명시
    currentAmount += amount;
    if(inputEl) inputEl.value = formatNumber(currentAmount);
}

// 초기화 버튼
window.resetAmount = function() {
    currentAmount = 0;
    if(inputEl) inputEl.value = "";
}


document.addEventListener('DOMContentLoaded', function() {

    // 직접 입력 처리
    if(inputEl) {
        inputEl.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^0-9]/g, '');
            if(value === '') {
                currentAmount = 0;
                e.target.value = '';
                return;
            }
            currentAmount = parseInt(value);
            e.target.value = formatNumber(currentAmount);
        });
    }



    let currentMode = 'CHARGE'; // 기본값: 충전

    // DOM 요소 가져오기
    const tabCharge = document.getElementById('tabCharge');
    const tabRefund = document.getElementById('tabRefund');
    const pageTitle = document.getElementById('pageTitle');
    const minAmountText = document.getElementById('minAmountText');
    const btnAction = document.getElementById('btnAction');

    // 1. 모드 변경 함수 (화면 꾸미기)
    function setMode(mode) {
        currentMode = mode;
        resetAmount(); // 모드 바뀔 때 금액 0원으로 초기화

        // 스타일 정의
        const activeClass = "active flex-fill text-center py-2 fw-bold border-bottom border-3 border-secondary".split(" ");
        const inactiveClass = "flex-fill text-center py-2 text-secondary bg-white".split(" ");


        if (mode === 'CHARGE') {
            // [충전 모드]
            // 탭 스타일
            tabCharge.className = "tab-item flex-fill text-center py-2 fw-bold border-bottom border-3 border-secondary";
            tabCharge.style.backgroundColor = "#f8f9fa";
            tabCharge.style.color = "black";

            tabRefund.className = "tab-item flex-fill text-center py-2 text-secondary bg-white";
            tabRefund.style.backgroundColor = "white";
            tabRefund.style.color = "#6c757d";

            // 텍스트 변경
            if(pageTitle) pageTitle.innerText = "충전 예정 맛나머니";
            if(minAmountText) minAmountText.innerText = "* 최소 충전금액은 1,000원입니다.";

            // 버튼 변경
            if(btnAction) {
                btnAction.innerText = "충전하기";
                btnAction.classList.remove('btn-secondary');
                btnAction.classList.add('btn-main-action'); // 파란색
            }

        } else {
            // [환급 모드]
            // 탭 스타일
            tabRefund.className = "tab-item flex-fill text-center py-2 fw-bold border-bottom border-3 border-secondary";
            tabRefund.style.backgroundColor = "#f8f9fa";
            tabRefund.style.color = "black";

            tabCharge.className = "tab-item flex-fill text-center py-2 text-secondary bg-white";
            tabCharge.style.backgroundColor = "white";
            tabCharge.style.color = "#6c757d";

            // 텍스트 변경
            if(pageTitle) pageTitle.innerText = "환급 신청 맛나머니";
            if(minAmountText) minAmountText.innerText = "* 최대 환급금액은 보유금액을 넘길 수 없습니다.";

            // 버튼 변경
            if(btnAction) {
                btnAction.innerText = "환급받기";
                btnAction.classList.remove('btn-main-action');
                btnAction.classList.add('btn-secondary'); // 회색
            }
        }
    }

    // 2. 탭 클릭 이벤트 연결
    if(tabCharge) tabCharge.addEventListener('click', () => setMode('CHARGE'));
    if(tabRefund) tabRefund.addEventListener('click', () => setMode('REFUND'));

    // 3. 버튼 클릭 이벤트
    if(btnAction) {
        btnAction.addEventListener('click', () => {
            const memberNo = document.getElementById('memberNo').value;

            if (currentAmount < 1000) {
                alert("최소 금액은 1,000원입니다.");
                return;
            }

            const isCharge = (currentMode === 'CHARGE');
            const actionName = isCharge ? "충전" : "환급";

            // confirm 창
            if (!confirm(`${formatNumber(currentAmount)}원을 ${actionName}하시겠습니까?`)) {
                return;
            }

            // 전송 데이터
            const requestData = {
                memberNo: parseInt(memberNo),
                amount: currentAmount
            };

            // URL 결정
            const url = isCharge ? '/api/mypage/point/charge' : '/api/mypage/point/refund';

            fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestData)
            })
                .then(response => {
                    if (response.ok) return response.json();
                    else return response.text().then(text => { throw new Error(text) });
                })
                .then(result => {
                    alert(`${actionName}이(가) 완료되었습니다!`);
                    window.location.reload();
                })
                .catch(error => {
                    alert(`${actionName} 실패: ` + error.message);
                });
        });
    }
});