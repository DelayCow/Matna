// normal, participant, creator
const status = 'normal';

const normalBtn = document.querySelector('.normal-btn');
const participantBtn = document.querySelector('.participant-btn');
const creatorStoppedBtn = document.querySelector('.creator-stopped-btn');
const creatorRunningBtn = document.querySelector('.creator-running-btn');

function showBtnByStatus(status){
    const allBtns = [normalBtn, participantBtn, creatorRunningBtn, creatorStoppedBtn];
    allBtns.forEach(btn => btn.classList.remove('show'));

    switch(status){
        case 'normal':
            normalBtn.classList.add('show');
            break;
        case 'participant':
            participantBtn.classList.add('show');
            break;
        case 'creator':
            creatorStoppedBtn.classList.add('show');
            creatorRunningBtn.classList.add('show');
            break;
    }
}
showBtnByStatus(status);


const normalOptions = [
    { value: "100g", text: "100g", selected: true },
    { value: "200g", text: "200g" },
    { value: "300g", text: "300g" }
];

const participantOptions = [
    { value: "100g", text: "100g", selected: true },
    { value: "300g", text: "300g" },
    { value: "400g", text: "400g" },
    { value: "500g", text: "500g" }
];

document.addEventListener('DOMContentLoaded', function () {
    const participantQuantityModal = document.getElementById('participantQuantityModal');
    if (participantQuantityModal) {
        participantQuantityModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const modalStatus = button.getAttribute('data-bs-status');

            const participantOnlyRow = participantQuantityModal.querySelector('.participant-only');
            const quantityLabel = document.getElementById('quantityLabel');
            const amountLabel = document.getElementById('amountLabel');
            const normalBtnContainer = document.getElementById('normalButtonContainer');
            const participantBtnContainer = document.getElementById('participantButtonContainer');
            const quantitySelect = document.getElementById('quantitySelect');

            let optionsToUse = [];
            if (modalStatus === 'participant') {
                // Participant
                participantOnlyRow.classList.remove('d-none');
                quantityLabel.textContent = '수량 수정';
                amountLabel.textContent = '추가 결제 / 환불 금액';
                normalBtnContainer.classList.add('d-none');
                participantBtnContainer.classList.remove('d-none');
                optionsToUse = participantOptions;
            } else { // normal
                participantOnlyRow.classList.add('d-none');
                quantityLabel.textContent = '수량 선택';
                amountLabel.textContent = '결제 금액';
                normalBtnContainer.classList.remove('d-none');
                participantBtnContainer.classList.add('d-none');
                optionsToUse = normalOptions;
            }

            quantitySelect.innerHTML = '';
            optionsToUse.forEach(optionData => {
                const option = document.createElement('option');
                option.value = optionData.value;
                option.textContent = optionData.text;
                if (optionData.selected) {
                    option.selected = true;
                }
                quantitySelect.appendChild(option);
            });
        });
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const creatorActionModal = document.getElementById('creatorActionModal');

    if (creatorActionModal) {
        creatorActionModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const action = button.getAttribute('data-bs-action'); // 'run' 또는 'stop'

            // 동적 요소들
            const titleElement = document.getElementById('creatorActionTitle');
            const messageElement = document.getElementById('creatorActionMessage');
            const reasonContainer = document.getElementById('reasonInputContainer');
            const actionButton = document.getElementById('creatorActionButton');

            if (action === 'run') {
                // Run
                reasonContainer.classList.add('d-none');
                titleElement.innerHTML = `공동구매를 <span class="text-danger">지금</span> 진행하시겠습니까?`;
                messageElement.innerHTML = `남은 수량인 <span class="fw-bold text-decoration-underline">300g</span>은 개설자님께서 부담하게 됩니다.`;
                actionButton.textContent = '진행하기';
            } else if (action === 'stop') {
                // Stop
                reasonContainer.classList.remove('d-none');
                titleElement.innerHTML = `공동구매를 정말 <span class="text-danger">중단</span>하시겠습니까?`;
                messageElement.innerHTML = `300g만 더 참여하면 마감할 수 있습니다.`;
                actionButton.textContent = '중단하기';
            }
        });
    }
});