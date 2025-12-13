// 성공/에러 모달 표시
export function showAlertModal(title, message, type = 'success', onConfirm = null) {
    const modal = new bootstrap.Modal(document.getElementById('alertModal'));
    const modalTitle = document.getElementById('modalTitle');
    const modalBody = document.getElementById('modalBody');
    const modalIcon = document.getElementById('modalIcon');
    const confirmBtn = document.getElementById('modalConfirmBtn');

    if (type === 'success') {
        modalIcon.className = 'bi bi-check-circle-fill text-success me-2';
        confirmBtn.className = 'btn btn-success';
    } else if (type === 'error') {
        modalIcon.className = 'bi bi-x-circle-fill text-danger me-2';
        confirmBtn.className = 'btn btn-danger';
    } else if (type === 'info') {
        modalIcon.className = 'bi bi-info-circle-fill text-primary me-2';
        confirmBtn.className = 'btn btn-primary';
    }

    modalTitle.textContent = title;
    modalBody.innerHTML = message;

    if (onConfirm) {
        confirmBtn.onclick = () => {
            modal.hide();
            onConfirm();
        };
    } else {
        confirmBtn.onclick = null;
    }

    modal.show();
}

export function showValidationModal(errors) {
    const modal = new bootstrap.Modal(document.getElementById('validationModal'));
    const validationList = document.getElementById('validationList');

    // 에러 목록 생성
    validationList.innerHTML = errors.map((error, index) => `
        <li class="list-group-item d-flex align-items-start">
            <span class="badge bg-warning text-dark me-2">${index + 1}</span>
            <span>${error}</span>
        </li>
    `).join('');

    modal.show();
}

// 나눔 확정 모달
export function showShareConfirmModal(item, onConfirm) {
    const modalElement = document.getElementById('shareConfirmModal');
    const modal = new bootstrap.Modal(modalElement);


    const titleInput = document.getElementById('shareModalTitle');
    const amountInput = document.getElementById('shareModalAmount');
    const priceInput = document.getElementById('shareModalPrice');
    const dateInput = document.getElementById('shareModalDate');
    const confirmBtn = document.getElementById('shareConfirmBtn');

    // 제목
    if (titleInput) {
        titleInput.value = item.title || "";
    }

    // 수량
    if (amountInput) {
        const quantity = item.amount || item.myQuantity || 0;
        const unit = item.unit || '';
        amountInput.value = `${quantity}${unit}`;
    }


    if (priceInput) {

        const price = (item.price !== undefined && item.price !== null) ? item.price : 0;


        priceInput.value = typeof price === 'number' ? price.toLocaleString() + "원" : price + "원";
    }

    // 2. 날짜 기본값 설정 (오늘)
    const today = new Date().toISOString().split('T')[0];
    if (dateInput) dateInput.value = today;


    const errorMsg = document.getElementById('shareDateError');
    if(errorMsg) errorMsg.classList.add('d-none');

    // 3. 확정 버튼 이벤트 연결
    if (confirmBtn) {
        const newConfirmBtn = confirmBtn.cloneNode(true);
        confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);

        newConfirmBtn.addEventListener('click', () => {
            const selectedDate = dateInput.value;
            if (!selectedDate) {
                if(errorMsg) errorMsg.classList.remove('d-none');
                return;
            }
            modal.hide();
            if (onConfirm) onConfirm(selectedDate);
        });
    }

    // 4. 모달 띄우기
    modal.show();
}




