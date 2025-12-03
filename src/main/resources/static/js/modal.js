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