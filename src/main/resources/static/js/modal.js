// 성공/에러 모달 표시
export function showAlertModal(title, message, type = 'success', onConfirm = null) {
    closeExistingModal();
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
    closeExistingModal();

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


function closeExistingModal() {
    const openModals = document.querySelectorAll('.modal.show');

    openModals.forEach(modalEl => {
        const modalInstance = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
        if (modalInstance) {
            modalInstance.hide();
        }
    });

    const backdrops = document.querySelectorAll('.modal-backdrop');
    backdrops.forEach(backdrop => backdrop.remove());
    document.body.classList.remove('modal-open');
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
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

    const reportBtn = document.getElementById('btnReportShare'); // 아까 ID 추가했죠?
    if (reportBtn) {
        reportBtn.onclick = () => {

            modal.hide();

            showReportModal('GROUPBUY', item.groupBuyNo);
        };
    }

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

// 결제 정보 모달
export function showPaymentInfoModal(item) {
    const modalEl = document.getElementById('paymentInfoModal');
    const modal = new bootstrap.Modal(modalEl);


    const imgEl = document.getElementById('modalPaymentImg');
    const noImgEl = document.getElementById('modalPaymentNoImg');
    const dateEl = document.getElementById('modalPaymentDate');
    const descEl = document.getElementById('modalPaymentDesc');


    if (item.receiptImageUrl) {
        imgEl.src = item.receiptImageUrl;
        imgEl.style.display = 'block';
        noImgEl.style.display = 'none';
    } else {
        imgEl.src = '';
        imgEl.style.display = 'none';
        noImgEl.style.display = 'block';
    }


    let displayDate = item.buyDate || "-";
    if (displayDate.includes('T')) {
        displayDate = displayDate.split('T')[0].replace(/-/g, '.'); // 2025-11-16 -> 2025.11.16
    }
    dateEl.value = displayDate;

    if (descEl) {

        descEl.value = item.paymentNote || "";
    }


    const reportBtn = document.getElementById('btnReportPayment');

    if (reportBtn) {
        reportBtn.onclick = () => {
            modal.hide();
            showReportModal('GROUPBUY', item.groupBuyNo);
        };
    }

    modal.show();
}

// 도착 정보 모달 띄우기
export function showArrivalInfoModal(item) {
    const modalEl = document.getElementById('arrivalInfoModal');

    if (!modalEl) {
        console.error("오류: 'arrivalInfoModal'이 HTML에 없습니다.");
        return;
    }

    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);

    const imgEl = document.getElementById('modalArrivalImg');
    const noImgEl = document.getElementById('modalArrivalNoImg');
    const dateEl = document.getElementById('modalArrivalDate');


    const reportBtn = document.getElementById('btnReportArrival');
    if (reportBtn) {
        reportBtn.onclick = () => {
            modal.hide();
            showReportModal('GROUPBUY', item.groupBuyNo);
        };
    }


    if (item.arrivalImageUrl) {
        if(imgEl) { imgEl.src = item.arrivalImageUrl; imgEl.style.display = 'block'; }
        if(noImgEl) noImgEl.style.display = 'none';
    } else {
        if(imgEl) { imgEl.src = ''; imgEl.style.display = 'none'; }
        if(noImgEl) noImgEl.style.display = 'block';
    }


    let displayDate = item.arrivalDate || "-";
    if (displayDate.includes('T')) {
        displayDate = displayDate.split('T')[0].replace(/-/g, '.');
    }
    if(dateEl) dateEl.value = displayDate;

    modal.show();
}

// 결제 정보 등록 모달 띄우기
export function showPaymentRegisterModal(item, onSuccess) {


    const modalEl = document.getElementById('paymentRegisterModal');
    if (!modalEl) return;

    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);

    const fileInput = document.getElementById('regPaymentFile');
    const imgArea = document.getElementById('paymentImgArea');
    const previewImg = document.getElementById('regPaymentPreview');
    const placeholder = document.getElementById('regPaymentPlaceholder');
    const dateInput = document.getElementById('regPaymentDate');
    const descInput = document.getElementById('regPaymentDesc');
    const registerBtn = document.getElementById('btnRegisterPayment');

    fileInput.value = '';
    previewImg.src = '';
    previewImg.style.display = 'none';
    placeholder.style.display = 'block';
    dateInput.value = '';
    descInput.value = '';


    imgArea.onclick = () => fileInput.click();


    fileInput.onchange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                previewImg.style.display = 'block';
                placeholder.style.display = 'none';
            };
            reader.readAsDataURL(file);
        }
    };


    registerBtn.onclick = () => {

        if (!fileInput.files[0]) {

            showAlertModal(
                '영수증 사진 필수',
                '영수증 사진을 등록 해 주세요',
                'error'
            );
            return;
        }


        if (!dateInput.value) {

            showAlertModal(
                        '구매 날짜 입력 요구',
                        '구매 날짜를 입력해주세요!',
                        'error'
                    );
            return;
        }

        const formData = new FormData();
        formData.append("groupBuyNo", item.groupBuyNo);
        formData.append("receiptImage", fileInput.files[0]);

        if (dateInput.value) {
            const fullDate = dateInput.value + " 00:00:00";
            formData.append("buyDate", fullDate);
        }

        formData.append("description", descInput.value);


        api.fetch('/api/mypage/payment/register', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {

                    showAlertModal(
                        '결제 정보 등록 완료',
                        '결제 정보가 등록되었습니다.',
                        'info'
                    );


                    if(onSuccess) onSuccess();
                } else {
                    showAlertModal(
                        '결제 정보 등록 실패',
                        '다시 등록되었습니다.',
                        'error'
                    );
                }
            })
            .catch(err => console.error("전송 오류:", err));
    };

    modal.show();
}

// 물품 도착 등록 모달
export function showArrivalRegisterModal(item, onSuccess) {
    const modalEl = document.getElementById('arrivalRegisterModal');
    if (!modalEl) return;

    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);

    // 요소 찾기
    const fileInput = document.getElementById('regArrivalFile');
    const imgArea = document.getElementById('arrivalImgArea');
    const previewImg = document.getElementById('regArrivalPreview');
    const placeholder = document.getElementById('regArrivalPlaceholder');
    const dateInput = document.getElementById('regArrivalDate');
    const registerBtn = document.getElementById('btnRegisterArrival');

    // 초기화
    fileInput.value = '';
    previewImg.src = '';
    previewImg.style.display = 'none';
    placeholder.style.display = 'block';
    dateInput.value = '';

    // 이미지 영역 클릭 시 파일 선택창 열기
    imgArea.onclick = () => fileInput.click();

    // 파일 선택 시 미리보기
    fileInput.onchange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                previewImg.style.display = 'block';
                placeholder.style.display = 'none';
            };
            reader.readAsDataURL(file);
        }
    };

    // 등록 버튼 클릭
    registerBtn.onclick = () => {
        if (!fileInput.files[0]) {
            alert("도착 인증 사진을 등록해주세요!");
            return;
        }
        if (!dateInput.value) {
            alert("도착 날짜를 입력해주세요!");
            return;
        }

        const formData = new FormData();
        formData.append("groupBuyNo", item.groupBuyNo);
        formData.append("arrivalImage", fileInput.files[0]); // 이름: arrivalImage


        const fullDate = dateInput.value + " 00:00:00";
        formData.append("arrivalDate", fullDate);

        // 전송
        api.fetch('/api/mypage/arrival/register', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {


                    showAlertModal(
                        '도착 정보 등록 완료',
                        '도착 정보가 등록되었습니다.',
                        'info'
                    );
                    if(onSuccess) onSuccess();
                } else {
                    response.text().then(msg => alert("등록 실패: " + msg));
                }
            })
            .catch(err => console.error("전송 오류:", err));
    };

    modal.show();
}

export function showPasswordCheckModal(memberNo) {

    const existingModal = document.getElementById('passwordCheckModal');
    if (existingModal) {
        existingModal.remove();
    }


    const modalHtml = `
    <div class="modal fade" id="passwordCheckModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title fw-bold">비밀번호 확인</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p class="text-secondary small mb-3">정보 수정을 위해 비밀번호를 입력해주세요.</p>
                    <input type="password" class="form-control" id="modalPasswordInput" placeholder="비밀번호 입력">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">취소</button>
                    <button type="button" class="btn btn-outline-primary btn-main-action" id="btnCheckPassword">확인</button>
                </div>
            </div>
        </div>
    </div>`;


    document.body.insertAdjacentHTML('beforeend', modalHtml);


    const modalEl = document.getElementById('passwordCheckModal');
    const inputEl = document.getElementById('modalPasswordInput');
    const btnCheck = document.getElementById('btnCheckPassword');
    const bsModal = new bootstrap.Modal(modalEl);


    const handleCheck = () => {
        const password = inputEl.value;
        if (!password) {
            // alert("비밀번호를 입력해주세요.");
            showAlertModal(
                '비밀번호 입력',
                '비밀번호를 입력해주세요.',
                'error'
            );
            return;
        }


        api.fetch('/api/mypage/checkModal/checkPassword', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                memberNo: parseInt(memberNo),
                password: password
            })
        })
            .then(res => {
                if (res.ok) return res.json();
                else throw new Error("서버 오류");
            })
            .then(isCorrect => {
                if (isCorrect) {
                    bsModal.hide();
                    location.href = `/mypage/${memberNo}/myinfoEdit`;
                } else {
                    alert("비밀번호가 일치하지 않습니다.");
                    inputEl.value = '';
                    inputEl.focus();
                }
            })
            .catch(err => {
                // alert("오류 발생: " + err.message);
            });
    };


    btnCheck.onclick = handleCheck;
    inputEl.onkeyup = (e) => {
        if (e.key === 'Enter') handleCheck();
    };


    modalEl.addEventListener('hidden.bs.modal', () => {
        modalEl.remove();
    });

    bsModal.show();

    modalEl.addEventListener('shown.bs.modal', () => {
        inputEl.focus();
    });
}

// 신고 모달
export function showReportModal(type, targetId, onSuccess) {
    const modalEl = document.getElementById('reportModal');


    if (!modalEl) {
        console.error("오류: 'reportModal'이 HTML에 없습니다.");
        return;
    }

    const modal = bootstrap.Modal.getOrCreateInstance(modalEl);

    // 1. 요소 가져오기
    const modalTitle = document.getElementById('reportModalLabel');
    const imgArea = document.getElementById('uploadTrigger');
    const fileInput = document.getElementById('reportFile');
    const previewImg = document.getElementById('previewImage');
    const plusIcon = document.getElementById('plusIcon');
    const reasonInput = document.getElementById('reportReason');
    const form = document.getElementById('reportForm');

    // 2. 초기화
    form.reset();
    fileInput.value = '';
    previewImg.src = '';
    previewImg.style.display = 'none';
    plusIcon.style.display = 'block';

    // 3. 타입 설정 (MEMBER / GROUPBUY)
    let apiUrl = '';
    let idFieldName = '';

    if (type === 'MEMBER') {
        modalTitle.textContent = "회원 신고";
        apiUrl = '/api/mypage/report/member';
        idFieldName = 'targetMemberNo';
    } else if (type === 'GROUPBUY') {
        modalTitle.textContent = "공구 신고";
        apiUrl = '/api/mypage/report/groupbuy';
        idFieldName = 'groupBuyNo';
    } else {
        console.error("잘못된 신고 유형");
        return;
    }

    // 4. 이미지 미리보기 (핸들러 덮어쓰기)
    imgArea.onclick = () => fileInput.click();

    fileInput.onchange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                previewImg.style.display = 'block';
                plusIcon.style.display = 'none';
            };
            reader.readAsDataURL(file);
        } else {
            previewImg.src = '';
            previewImg.style.display = 'none';
            plusIcon.style.display = 'block';
        }
    };

    // 5. 전송 (핸들러 덮어쓰기)
    form.onsubmit = (e) => {
        e.preventDefault();

        const reason = reasonInput.value.trim();
        if (!reason) {
            alert("신고 사유를 입력해주세요.");
            return;
        }

        const formData = new FormData();
        formData.append(idFieldName, targetId);
        formData.append("reason", reason);
        if (fileInput.files[0]) {
            formData.append("imageFile", fileInput.files[0]);
        }

        api.fetch(apiUrl, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {

                    showAlertModal(
                        '신고가 접수 되었습니다.',
                        '신고가 접수되었습니다.',
                        'info'
                    );

                    if (onSuccess) onSuccess();
                } else {
                    response.text().then(msg => alert("신고 실패: " + msg));
                }
            })
            .catch(err => {
                console.error(err);
                alert("오류 발생");
            });
    };

    // 6. 모달 표시
    modal.show();
}

export function showRemoveMemberModal(memberNo) {
    const modal = new bootstrap.Modal(document.getElementById('removeMemberModal'));
    const msg = document.getElementById('remove-message');
    document.getElementById('removeMemberBtn').addEventListener('click',function (){
        const password = document.getElementById('password').value;
        api.fetch('/api/mypage/checkModal/checkPassword',{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json' // 필수: 서버가 JSON임을 알게 함
            },
            body: JSON.stringify({
                memberNo : parseInt(memberNo),
                password : password
            })
        }).then(response => {
            return response.json()
        }).then(result => {
            if(!result){
                msg.innerText = '비밀번호가 틀렸습니다. 확인 후 올바른 비밀번호를 입력해주세요.'
                return
            }
            api.fetch(`/api/mypage/remove/${memberNo}`, {
                method: 'DELETE'
            }).then(response => {
                msg.innerText = '탈퇴 되었습니다.'
                setTimeout(()=> {
                    location.href='/logout'
                }, 2000)
            })

        })
    })
    modal.show();
}




