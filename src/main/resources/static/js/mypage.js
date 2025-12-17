
import { showShareConfirmModal, showPaymentInfoModal, showArrivalInfoModal, showPaymentRegisterModal,showPasswordCheckModal } from "./modal.js";

document.addEventListener('DOMContentLoaded', function() {



    const isOwnerText = document.getElementById('isOwner').textContent.trim().toLowerCase();
    const isOwner = isOwnerText === 'true';

    const memberNo = document.getElementById('memberNo').textContent;



    let currentGroupTab = 'participate';
    let currentFilterStatus = 'ALL';

    const getStatusStep = (status) => {
        const cleanStatus = String(status).trim().toUpperCase();
        switch (cleanStatus) {
            case 'OPEN': case 'RECRUITING': return 1;
            case 'CLOSED': case 'PAYMENT_WAIT': return 2;
            case 'PAID': case 'DELIVERED': return 3;
            case 'SHARED': case 'COMPLETED': return 4;
            case 'CANCELED': return 0;
            default: return 1;
        }
    };

    const getButtonConfig = (status) => {

        const s = String(status).trim().toUpperCase();
        if (s === 'OPEN' || s === 'RECRUITING') {
            return {
                text: "참여 취소",
                cls: "btn-outline-danger",
                type: "modal",
                target: "#cancelModal" };
        }
        if (s === 'CLOSED') {
            return {
                text: "결제정보 등록",
                cls: "btn-danger",
                type: "custom",
                action: "registerPayment",
                target: "#paymentRegisterModal"
            };
        }

        // 3. 입금 대기
        if (s === 'PAYMENT_WAIT') {
            return null;
        }

        if (s === 'PAID') {
            return {
                text: "결제정보 확인",
                cls: "btn-outline-primary",
                type: "custom",
                action: "checkPayment",
                target: "#paymentInfoModal" };
        }
        if (s === 'DELIVERED') {
            return {
                text: "도착정보 확인",
                cls: "btn-success",
                type: "custom",
                action: "checkArrival",
                target: "#arrivalInfoModal"
            };
        }
        if (s === 'SHARED' || s === 'COMPLETED') {
            return {
                text: "나눔 받았어요!",
                cls: "btn-success",
                type: "custom",
                action: "share",
                target: "#shareConfirmModal" };
        }
        return

    };

    const renderCommonArea = (data) => {


        const headerArea = document.getElementById('header-right-area');
        const profileArea = document.getElementById('profile-main-area');
        if (!data) data = {};
        const nickname = data.nickname || "맛도리 회원님";
        const image = data.imageUrl || "/img/user.png";
        const money = data.points || 0;

        if (isOwner && headerArea) {
            headerArea.innerHTML = `<div class="position-relative"> <button class="btn p-0 border-0" id="headerMenuBtn">
                <i class="bi bi-three-dots-vertical fs-4 text-dark"></i>
            </button>
            <ul class="custom-dropdown" id="headerDropdown">
                <li><a href="#" id="btnEditInfo">정보 수정</a></li>
                <li><a href="/logout">로그아웃</a></li>
                <li><a href="#" class="text-danger">탈퇴</a></li>
            </ul>
        </div>`;

            const btn = document.getElementById('headerMenuBtn');
            const dropdown = document.getElementById('headerDropdown');
            const editBtn = document.getElementById('btnEditInfo');

            if (btn && dropdown) {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    dropdown.classList.toggle('show'); });

                if (editBtn) {
                    editBtn.addEventListener('click', (e) => {
                        e.preventDefault();
                        dropdown.classList.remove('show');
                        showPasswordCheckModal(memberNo); // import한 함수 실행
                    });
                }
            }

            document.addEventListener('click', (e) => {
                if (dropdown && dropdown.classList.contains('show')) {
                    if (!dropdown.contains(e.target) && !btn.contains(e.target)) {
                        dropdown.classList.remove('show');
                    }
                }
            });

        } else if (headerArea) { headerArea.innerHTML = ''; }

        let subInfo = isOwner
            ? `<small class="text-muted" 
              style="cursor: pointer; text-decoration: underline;" 
              onclick="location.href='/mypage/point/charge'">
         내 맛나머니 : ${money.toLocaleString()} 원
       </small>`
            : `<button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1">
         <i class="bi bi-exclamation-circle me-1"></i>신고하기
       </button>`;

        if(profileArea) {
            profileArea.innerHTML = `<img src="${image}" class="rounded-circle border me-3" width="60" height="60"><div><h5 class="fw-bold mb-1">${nickname}</h5><div>${subInfo}</div></div>`;
        }
    };

    const createRecipeCard = (item) => {

        const imgUrl = item.image ? item.image : '/img/default_food.jpg';


        let difficultyKor = item.difficulty;
        if (item.difficulty === 'easy' || item.difficulty === '쉬움') difficultyKor = '쉬움';
        else if (item.difficulty === 'normal' || item.difficulty === '보통') difficultyKor = '보통';
        else if (item.difficulty === 'hard' || item.difficulty === '어려움') difficultyKor = '어려움';


        let spicyText = '';

        switch(item.spicy){
            case 0: spicyText = '안매워요'; break;
            case 1: spicyText = '약간매워요'; break;
            case 2: spicyText = '신라면맵기'; break;
            case 3: spicyText = '열라면맵기'; break;
            case 4: spicyText = '불닭맵기'; break;
            case 5: spicyText = '불닭보다매워요'; break;
            default: spicyText = '';
        }


        const editUrl = `/recipe/edit/${item.id}`;

        const detailUrl = `/recipe/detail/${item.id}`;

        const kebabMenuHtml = (typeof isOwner !== 'undefined' && isOwner) ? `
        <div class="dropdown ms-auto">
            <button class="btn btn-link text-secondary p-0 border-0" type="button" data-bs-toggle="dropdown"><i class="bi bi-three-dots-vertical"></i></button>
            <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                <li><a class="dropdown-item small" href="${editUrl}">수정</a></li>
                <li><hr class="dropdown-divider my-1"></li>
                <li><button id="removeRecipe" class="dropdown-item small text-danger btn-delete" data-id="${item.id}">삭제</button></li>
            </ul>
        </div>` : '';

        return `
    <div class="recipe-card mb-4 col-12" data-id="${item.id}">
        <div class="card-img-wrap" onclick="location.href='${detailUrl}'">
            <img src="${imgUrl}" alt="${item.title}" onerror="this.src='/img/default_food.jpg'">
        </div>
        <div class="card-info mt-2 p-2">
            <h5 class="card-title">${item.title}</h5>
            <div class="d-flex align-items-center mb-2">
                <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                <span class="fw-bold me-1">${item.rating}</span>
                <span class="text-muted small">(${item.reviewCount || 0})</span>
                ${kebabMenuHtml}
            </div>
            <div class="d-flex flex-wrap gap-2 text-secondary" style="font-size: 0.8rem;">
                <span class="bg-light px-2 py-1 rounded-pill border"><i class="bi bi-clock me-1"></i>${item.time}</span>
                <span class="bg-light px-2 py-1 rounded-pill border"><i class="bi bi-bar-chart me-1"></i>${difficultyKor}</span>
                ${ spicyText ? `<span class="bg-danger-subtle text-danger px-2 py-1 rounded-pill border border-danger-subtle"><i class="bi bi-fire me-1"></i>${spicyText}</span>` : '' }
            </div>
        </div>
    </div>`
    };

    const removeRecipe = async function(recipeNo){
        try{
            const response = await fetch(`/api/recipes/${recipeNo}`,{
                method: 'DELETE'
            });

            if(response.ok) {
                showAlertModal(
                    '삭제 완료',
                    '레시피가 성공적으로 삭제되었습니다!',
                    'success',
                    () => {
                        window.location.href = '/recipe';
                    }
                );
            }else{
                const errorData = await response.json();
                const errorMessage = errorData.message || '서버 오류가 발생했습니다.';

                showAlertModal(
                    '삭제 실패',
                    `레시피 삭제에 실패했습니다.<br><br><small class="text-muted">${errorMessage}</small>`,
                    'error'
                );
            }

        }catch(error){
            console.error('네트워크 오류:', error);
            showAlertModal(
                '네트워크 오류',
                '서버와 통신할 수 없습니다.<br>잠시 후 다시 시도해주세요.',
                'error'
            );
        }
    }
    const createReviewCard = (item) => {
        const imgUrl = item.imageUrl ? item.imageUrl : '/img/default_profile.jpg';
        return `<div class="review-card mb-4 col-12"><div class="card-img-wrap"><img src="${imgUrl}"></div><div class="card-info mt-2 p-2"><h5 class="card-title">${item.title}</h5></div></div>`;
    };

    const createGroupCard = (item) => {

        const unit = item.unit || '';
        const currentStep = getStatusStep(item.status);


        const btnConfig = getButtonConfig(item.status);


        const steps = ["모집", "상품결제", "상품도착", "나눔진행"];

        let timelineHtml = '<div class="timeline-steps">';
        steps.forEach((stepName, index) => {
            const stepNum = index + 1;
            let activeClass = (stepNum < currentStep) ? "active" : (stepNum === currentStep ? "current" : "");
            timelineHtml += `<div class="step-item ${activeClass}"><div class="step-circle"></div><span class="step-text">${stepName}</span></div>`;
        });
        timelineHtml += '</div>';
        if (!isOwner) timelineHtml = '';

        let buttonHtml = '';
        if (isOwner && btnConfig) {
            if (btnConfig.type === 'link') {
                buttonHtml = `<button class="btn ${btnConfig.cls} btn-sm" onclick="event.stopPropagation(); location.href='${btnConfig.target}'">${btnConfig.text}</button>`;
            } else if (btnConfig.type === 'modal') {
                buttonHtml = `<button class="btn ${btnConfig.cls} btn-sm" data-bs-toggle="modal" data-bs-target="${btnConfig.target}" onclick="event.stopPropagation()">${btnConfig.text}</button>`;
            } else if (btnConfig.type === 'custom') {
                if (btnConfig.action === 'share') {
                    const dataToSend = { title: item.title, price: item.finalPaymentPoint || 0, amount: item.myQuantity, unit: item.unit || '', groupBuyNo: item.groupBuyNo, groupParticipantNo: item.groupParticipantNo };
                    const itemData = encodeURIComponent(JSON.stringify(dataToSend));
                    buttonHtml = `<button class="btn ${btnConfig.cls} btn-sm btn-share-confirm" data-item="${itemData}">${btnConfig.text}</button>`;
                } else if (btnConfig.action === 'checkPayment') {
                    const dataToSend = {
                        groupBuyNo: item.groupBuyNo,
                        receiptImageUrl: item.receiptImageUrl,
                        buyDate: item.buyDate,
                        paymentNote: item.paymentNote};
                    const itemData = encodeURIComponent(JSON.stringify(dataToSend));
                    buttonHtml = `<button class="btn ${btnConfig.cls} btn-sm btn-payment-info" data-item="${itemData}">${btnConfig.text}</button>`;
                }else if (btnConfig.action === 'checkArrival') {
                    const dataToSend = {
                        groupBuyNo: item.groupBuyNo,
                        arrivalImageUrl: item.arrivalImageUrl || item.deliveryImageUrl,
                        arrivalDate: item.arrivalDate || item.deliveryDate
                    };

                    const itemData = encodeURIComponent(JSON.stringify(dataToSend));

                    buttonHtml = `
                <button class="btn ${btnConfig.cls} btn-sm text-nowrap z-index-front btn-arrival-info" 
                        style="font-size: 0.75rem;"
                        data-item="${itemData}">
                    ${btnConfig.text}
                </button>`;
                }


                else if (btnConfig.action === 'registerPayment') {
                    // 등록할 때는 글 번호(PK)만 있으면 됨
                    const dataToSend = { groupBuyNo: item.groupBuyNo };
                    const itemData = encodeURIComponent(JSON.stringify(dataToSend));

                    buttonHtml = `
            <button class="btn ${btnConfig.cls} btn-sm text-nowrap z-index-front btn-payment-register" 
                    style="font-size: 0.75rem;"
                    data-item="${itemData}">
                ${btnConfig.text}
            </button>`;
                }

            }
        }
        if (item.receiveDate) buttonHtml = `<button class="btn btn-secondary btn-sm" disabled>수령 완료</button>`;

        let detailLink = `/periodGroupBuy/detail/${item.periodGroupBuyNo}`;

        if (item.periodGroupBuyNo == null) {
            detailLink = `/quantityGroupBuy/detail/${item.quantityGroupBuyNo}`;
        }


        return `<div class="group-card mb-3 p-3 border rounded bg-white shadow-sm">
            <div class="d-flex justify-content-between align-items-start mb-2"><div class="flex-grow-1 me-3">${timelineHtml}</div>${buttonHtml}</div>
            <div class="d-flex align-items-center gap-3">
                <div class="rounded overflow-hidden border" style="width: 80px; height: 80px;" onclick="location.href='${detailLink}'"><img src="${item.imageUrl || '/img/default_food.jpg'}" class="w-100 h-100 object-fit-cover"></div>
                <div class="group-info flex-grow-1"><h5 class="fw-bold mb-1">${item.title}</h5></div>
            </div>
        </div>`;
    };


    function fetchProfileData(memberNo) {
        fetch(`/api/mypage/${memberNo}/profile`).then(res => res.json()).then(renderCommonArea).catch(console.error);
    }
    const fetchRecipeData = (memberNo) => fetch(`/api/mypage/${memberNo}/recipe`).then(res => res.json());
    const fetchReviewData = (memberNo) => fetch(`/api/mypage/${memberNo}/reviewList`).then(res => res.json());

    const fetchGroupData = async () => {
        const listEl = document.getElementById('group-list');
        const countEl = document.getElementById('statGroupCount');



        if (!listEl) {
            console.error("데이터가 없습니다.");
            return;
        }

        listEl.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

        try {
            const baseUrl = (currentGroupTab === 'participate') ? `/api/mypage/${memberNo}/groupBuy/participation` : `/api/mypage/${memberNo}/groupBuy/host`;
            const url = `${baseUrl}?filter=${currentFilterStatus}`;


            const response = await fetch(url);
            if (!response.ok) throw new Error("Network Error");
            const dataList = await response.json();



            if (!dataList || dataList.length === 0) {
                listEl.innerHTML = '<div class="text-center py-5 text-muted">내역이 없습니다.</div>';
                if(countEl) countEl.innerText = '0';
            } else {
                listEl.innerHTML = dataList.map(createGroupCard).join('');
                if(countEl) countEl.innerText = dataList.length;
            }
        } catch (error) {
            console.error(error);
            listEl.innerHTML = '<div class="text-center py-5 text-danger">데이터 로드 실패</div>';
        }
    };


    const groupListEl = document.getElementById('group-list');


    if (groupListEl) {
        groupListEl.addEventListener('click', function (e) {
            const shareBtn = e.target.closest('.btn-share-confirm');
            if (shareBtn) {
                e.preventDefault(); e.stopPropagation();
                const item = JSON.parse(decodeURIComponent(shareBtn.getAttribute('data-item')));
                showShareConfirmModal(item, (selectedDate) => {
                    fetch(`/api/mypage/groupbuy/shared`, {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({ groupParticipantNo: item.groupParticipantNo, receiveDate: selectedDate + "T00:00:00" })
                    }).then(() => { alert("확정 완료!"); window.location.reload(); });
                });
            }

            const paymentBtn = e.target.closest('.btn-payment-info');
            if (paymentBtn) {
                e.preventDefault(); e.stopPropagation();
                const item = JSON.parse(decodeURIComponent(paymentBtn.getAttribute('data-item')));



                showPaymentInfoModal(item);
            }

            const arrivalBtn = e.target.closest('.btn-arrival-info');
            if (arrivalBtn) {
                e.preventDefault(); e.stopPropagation();
                const itemDataString = arrivalBtn.getAttribute('data-item');
                if (itemDataString) {
                    try {
                        const item = JSON.parse(decodeURIComponent(itemDataString));
                        showArrivalInfoModal(item);
                    } catch (err) {
                        console.error("데이터 파싱 오류:", err);
                    }
                }
            }

            const regPaymentBtn = e.target.closest('.btn-payment-register');
            if (regPaymentBtn) {
                e.preventDefault(); e.stopPropagation();
                const itemDataString = regPaymentBtn.getAttribute('data-item');
                if (itemDataString) {
                    const item = JSON.parse(decodeURIComponent(itemDataString));

                    // 모달 띄우기 (성공 시 새로고침)
                    showPaymentRegisterModal(item, () => {
                        window.location.reload();
                    });
                }
            }

        });
    }

    // 나머지 탭/필터 이벤트들...
    const statTabRecipe = document.getElementById('statTabRecipe');
    const statTabGroup = document.getElementById('statTabGroup');
    const wrapRecipe = document.getElementById('recipe-section-wrapper');
    const wrapGroup = document.getElementById('group-section-wrapper');

    if (statTabRecipe && statTabGroup) {
        statTabRecipe.addEventListener('click', () => {
            statTabRecipe.classList.add('active'); statTabGroup.classList.remove('active');
            wrapRecipe.style.display = 'block'; wrapGroup.style.display = 'none';
        });
        statTabGroup.addEventListener('click', () => {
            statTabGroup.classList.add('active'); statTabRecipe.classList.remove('active');
            wrapGroup.style.display = 'block'; wrapRecipe.style.display = 'none';
        });
    }

    const btnParticipate = document.getElementById('btnParticipate');
    const btnOpen = document.getElementById('btnOpen');
    if (btnParticipate) btnParticipate.addEventListener('change', () => { if (btnParticipate.checked) { currentGroupTab = 'participate'; fetchGroupData(); } });
    if (btnOpen) btnOpen.addEventListener('change', () => { if (btnOpen.checked) { currentGroupTab = 'host'; fetchGroupData(); } });

    const statusFilterEl = document.getElementById('groupStatusFilter');
    if (statusFilterEl) statusFilterEl.addEventListener('change', (e) => { currentFilterStatus = e.target.value; fetchGroupData(); });


    renderCommonArea();
    fetchProfileData(memberNo);

    fetchRecipeData(memberNo).then(list => {
        const listEl = document.getElementById('recipe-list');
        const countEl = document.getElementById('statRecipeCount');
        if (list && listEl) {
            listEl.innerHTML = list.map(createRecipeCard).join('');
            if(countEl) countEl.innerText = list.length;
        }
    });

    fetchReviewData(memberNo).then(list => {
        const listEl = document.getElementById('review-list');
        if (listEl && list) {
            listEl.innerHTML = list.map(createReviewCard).join('');
        }
    });


    fetchGroupData();

});