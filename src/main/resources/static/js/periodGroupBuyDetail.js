// normal, participant, creator
const status = 'normal'
const normalBtn = document.querySelector('.normal-btn');
const participantBtn = document.querySelector('.participant-btn');
const creatorRunningBtn = document.querySelector('.creator-running-btn');

function showBtnByStatus(status){
    const allBtns = [normalBtn, participantBtn, creatorRunningBtn];
    allBtns.forEach(btn => btn.classList.remove('show'));

    switch (status){
        case 'normal':
            normalBtn.classList.add('show');
            break;
        case 'participant':
            participantBtn.classList.add('show');
            break;
        case 'creator':
            creatorRunningBtn.classList.add('show');
            break;
    }
}
showBtnByStatus(status);


document.addEventListener('DOMContentLoaded', function () {
    const dynamicMainModal = document.getElementById('dynamicMainModal');
    if (dynamicMainModal) {
        dynamicMainModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const modalStatus = button.getAttribute('data-bs-status'); // normal, participant, creator

            const titleElement = document.getElementById('dynamicTitle');
            const messageElement = document.getElementById('dynamicMessage');
            const personArea = document.getElementById('personArea');
            const personHeader = document.getElementById('personHeader');
            const personCount = document.getElementById('personCount');
            const reasonContainer = document.getElementById('reasonInputContainer');
            const mainActionButton = document.getElementById('mainActionButton');
            const amountLabel = document.getElementById('amountLabel');
            const amountValue = document.getElementById('amountValue');
            const refundNotice = document.getElementById('refundNotice');

            // 초기화: 모든 동적 컨텐츠 숨김 및 기본 스타일 설정
            titleElement.textContent = '';
            messageElement.textContent = '';
            personArea.classList.add('d-none');
            reasonContainer.classList.add('d-none');
            mainActionButton.classList.remove('btn-success');
            mainActionButton.style.backgroundColor = '#dc3545';
            mainActionButton.style.borderColor = '#dc3545';


            if (modalStatus === 'normal') {
                personArea.classList.remove('d-none');

                personHeader.textContent = '현재 참여인원';
                personCount.textContent = '2명';
                amountLabel.textContent = '결제 예정 금액';
                amountValue.textContent = '6,000원';
                refundNotice.textContent = '최종 참여인원에 따른 차액은 공동구매 마감기간 이후 3일 이내 환불됩니다.';
                refundNotice.classList.remove('d-none');

                mainActionButton.textContent = '참여하기';

            } else if (modalStatus === 'participant') {
                personArea.classList.remove('d-none');

                personHeader.textContent = '본인 포함 현재 참여인원';
                personCount.textContent = '3명';
                amountLabel.textContent = '환불 예정 금액';
                amountValue.textContent = '6,000원';
                refundNotice.textContent = '공동구매 참여를 취소하시겠습니까?';
                refundNotice.classList.remove('d-none');

                mainActionButton.textContent = '참여 취소하기';

            } else if (modalStatus === 'creator') {
                reasonContainer.classList.remove('d-none');
                titleElement.innerHTML = `공동구매를 정말 <span class="text-danger">중단</span>하시겠습니까?`;

                mainActionButton.textContent = '중단하기';
            }
        });
    }
});