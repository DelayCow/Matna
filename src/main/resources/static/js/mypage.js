
import { showAlertModal, showShareConfirmModal, showPaymentInfoModal, showArrivalInfoModal, showPaymentRegisterModal,showPasswordCheckModal, showReportModal, showRemoveMemberModal } from "./modal.js";

document.addEventListener('DOMContentLoaded', async function() {
    // URL에서 memberNo 추출
    const pathParts = window.location.pathname.split('/');
    const urlMemberNo = pathParts[pathParts.length - 1] === 'mypage' ? null : parseInt(pathParts[pathParts.length - 1]);


    // 현재 로그인한 사용자 정보 가져오기


    let currentUser = null;
    let memberNo = null;
    let isOwner = false;

    try {
        const authResponse = await api.fetch('/api/auth/currentUser');
        if (!authResponse.ok) throw new Error('인증 정보를 가져올 수 없습니다.');

        currentUser = await authResponse.json();

        memberNo = urlMemberNo || currentUser.memberNo;
        
        isOwner = currentUser.memberNo === memberNo;

    } catch (error) {
        console.error('인증 확인 오류:', error);
    }


    let currentGroupTab = 'host';
    let currentFilterStatus = 'ALL';


    const getStatusStep = (status) => {
        const s = status;
        switch (s) {
            case 'open': case 'recruiting': return 1;
            case 'closed': return 2;
            case 'paid': return 3;
            case 'delivered': return 4;
            case 'shared': return 5;
            case 'canceled': return 0;
            default: return 1;
        }
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
                <li><a href="#" id="btnLogout">로그아웃</a></li>
                <li><a href="#" id="removeMember" class="text-danger">탈퇴</a></li>
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
            : `<button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1 btn-report-member"
         data-member-no= "${memberNo}">
         <i class="bi bi-exclamation-circle me-1"></i>신고하기
       </button>`;

        if(profileArea) {
            profileArea.innerHTML = `<img src="${image}" class="rounded-circle border me-3" width="60" height="60"><div><h5 class="fw-bold mb-1">${nickname}</h5><div>${subInfo}</div></div>`;
        }
    };

    // [추가] 전체(참여 + 개설) 카운트 구하기
    const updateTotalGroupCount = async () => {
        const countEl = document.getElementById('statGroupCount');
        if (!countEl) return;

        try {
            // 1. 참여 내역 가져오기 (필터 ALL)
            const participateRes = await api.fetch(`/api/mypage/${memberNo}/groupBuy/participation?filter=ALL`);
            const participateData = await participateRes.json();

            const partList = (participateData || []).filter(item => item.status !== 'canceled');



            // 2. 개설 내역 가져오기 (필터 ALL)
            const hostRes = await api.fetch(`/api/mypage/${memberNo}/groupBuy/host?filter=ALL`);
            const hostData = await hostRes.json();

            const hostList = (hostData || []).filter(item => item.status !== 'canceled');


            const combinedList = [...partList, ...hostList];
            const uniqueList = [];
            const seenIds = new Set();

            combinedList.forEach(item => {

                if (!seenIds.has(item.groupBuyNo)) {
                    seenIds.add(item.groupBuyNo);
                    uniqueList.push(item);
                }
            });



            // 3. 합산하여 표시

            countEl.innerText = uniqueList.length;



        } catch (error) {
            console.error("카운트 집계 실패:", error);
            countEl.innerText = "0";
        }
    };

    //  참여 취소 API 호출 함수 (기간/수량 구분)
    const cancelParticipation = async (participantNo, type) => {

        let apiUrl = '';

        if (type === 'PERIOD') {
            apiUrl = `/api/periodGroupBuy/cancelParticipant/${participantNo}`;
        } else {
            // 기본값은 수량형 (QUANTITY)
            apiUrl = `/api/quantityGroupBuy/cancelParticipant/${participantNo}`;
        }

        try {
            const response = await api.fetch(apiUrl, {
                method: 'PUT', // 상세페이지 로직에 맞춰 PUT 사용
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();

            if (response.ok) {
                alert("참여가 정상적으로 취소되었습니다.");
                window.location.reload(); // 새로고침해서 목록 갱신
            } else {
                alert("취소 실패: " + (result.message || "오류가 발생했습니다."));
            }
        } catch (error) {
            console.error("취소 요청 중 에러:", error);
            alert("서버 통신 중 오류가 발생했습니다.");
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
                <span class="fw-bold me-1">${Number(item.rating).toFixed(1)}</span>
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
            const response = await api.fetch(`/api/recipes/${recipeNo}`,{
                method: 'DELETE'
            });

            if(response.ok) {
                showAlertModal(
                    '삭제 완료',
                    '레시피가 성공적으로 삭제되었습니다!',
                    'success',
                    () => {
                        window.location.href = '/mypage';
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

        let spicyText = '';
        switch(item.spicyLevel){
            case 0: spicyText = '안매워요'; break;
            case 1: spicyText = '약간매워요'; break;
            case 2: spicyText = '신라면맵기'; break;
            case 3: spicyText = '열라면맵기'; break;
            case 4: spicyText = '불닭맵기'; break;
            case 5: spicyText = '불닭보다매워요'; break;
            default: spicyText = '';
        }

        const reviewNo = item.reviewNo || item.id;

        const detailUrl = `/review/detail/${reviewNo}`;


        return `
        <div class="review-card mb-4 col-12">
            <div class="card-img-wrap" onclick="location.href='${detailUrl}'" style="cursor: pointer;">
                <img src="${imgUrl}" class="w-100 h-100 object-fit-cover" alt="후기 이미지">
            </div>
            <div class="card-info mt-2 p-2">
                <h5 class="card-title fw-bold" onclick="location.href='${detailUrl}'" style="cursor: pointer;">${item.title}</h5>
                
                <div class="d-flex align-items-center mb-2">
                    <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                    <span class="fw-bold me-2">${Number(item.rating).toFixed(1)}</span>
                    
                    </div>

                <div class="d-flex flex-wrap gap-2 text-secondary" style="font-size: 0.8rem;">
                    ${ spicyText ? `
                    <span class="bg-danger-subtle text-danger px-2 py-1 rounded-pill border border-danger-subtle">
                        <i class="bi bi-fire me-1"></i>${spicyText}
                    </span>` : '' }
                </div>
                
                <p class="text-muted small mt-2 text-truncate">${item.content || ''}</p>
            </div>
        </div>`;
    };



    const createGroupCard = (item) => {
        if (item.status === 'canceled') return '';

        const isHostTab = (currentGroupTab === 'host');
        const groupBuyType = (item.periodGroupBuyNo !== null && item.periodGroupBuyNo !== undefined) ? 'PERIOD' : 'QUANTITY';
        const currentStep = getStatusStep(item.status);

        // 1. 타임라인 생성
        const steps = ["모집", "상품결제", "상품도착", "나눔진행"];
        let timelineHtml = '<div class="timeline-steps mb-3">';
        steps.forEach((stepName, index) => {
            const stepNum = index + 1;
            let activeClass = (stepNum < currentStep) ? "active" : (stepNum === currentStep ? "current" : "");
            timelineHtml += `<div class="step-item ${activeClass}"><div class="step-circle"></div><span class="step-text">${stepName}</span></div>`;
        });
        timelineHtml += '</div>';

        // 2. 버튼 데이터 세팅
        const paymentData = encodeURIComponent(JSON.stringify({
            groupBuyNo: item.groupBuyNo, receiptImageUrl: item.receiptImageUrl,
            buyDate: item.buyDate, paymentNote: item.paymentNote
        }));
        const arrivalData = encodeURIComponent(JSON.stringify({
            groupBuyNo: item.groupBuyNo,
            arrivalImageUrl: item.arrivalImageUrl || item.deliveryImageUrl,
            arrivalDate: item.arrivalDate || item.deliveryDate
        }));
        const sharedata = encodeURIComponent(JSON.stringify({
            title: item.title, groupBuyNo: item.groupBuyNo, groupParticipantNo: item.groupParticipantNo
        }));
        const cancelData = encodeURIComponent(JSON.stringify({
            groupParticipantNo: item.groupParticipantNo, type: groupBuyType
        }));

        // 3. 버튼 세트 (수직 배치) 로직 수정
        let buttonsHtml = `<div class="d-flex flex-column gap-1 ms-3" style="min-width: 120px;">`;

        if (isHostTab) {
            // --- [개설자 전용 버튼 세트] ---
            buttonsHtml += `<button class="btn btn-danger btn-sm btn-payment-register" data-item="${paymentData}" 
                        ${item.status !== 'closed' ? 'disabled' : ''}>결제정보 등록</button>`;

            buttonsHtml += `<button class="btn btn-success btn-sm btn-arrival-register" data-item="${arrivalData}" 
                        ${item.status !== 'paid' ? 'disabled' : ''}>도착정보 등록</button>`;
        } else {
            // --- [참여자 전용 버튼 세트] ---
            if (item.status === 'open' || item.status === 'recruiting') {
                // 모집 중일 때는 '참여 취소' 버튼만 노출
                buttonsHtml += `<button class="btn btn-outline-danger btn-sm btn-cancel-participation" data-item="${cancelData}">참여 취소</button>`;
            } else {
                // 모집 완료 후에는 나머지 버튼들만 노출
                // 결제정보 확인 (PAID 이상일 때 활성화)
                buttonsHtml += `<button class="btn btn-outline-primary btn-sm btn-payment-info" data-item="${paymentData}" 
                            ${!['paid', 'delivered', 'shared'].includes(item.status) ? 'disabled' : ''}>결제정보 확인</button>`;

                // 도착정보 확인 (DELIVERED 이상일 때 활성화)
                buttonsHtml += `<button class="btn btn-outline-success btn-sm btn-arrival-info" data-item="${arrivalData}" 
                            ${!['delivered', 'shared'].includes(item.status) ? 'disabled' : ''}>도착정보 확인</button>`;

                // 나눔 받았어요! (DELIVERED 일 때만 활성화, 수령 전일 때만 노출)
                if (!item.receiveDate) {
                    buttonsHtml += `<button class="btn btn-success btn-sm btn-share-confirm" data-item="${sharedata}" 
                                ${item.status !== 'delivered' ? 'disabled' : ''}>나눔 받았어요!</button>`;
                }
            }
        }
        buttonsHtml += `</div>`;

        // 4. 타이틀 밑 상태 메시지 처리
        let statusMessageHtml = '';
        if (isHostTab) {
            if (item.status === 'shared') {
                statusMessageHtml = '<div class="text-success small fw-bold mt-1"><i class="bi bi-people-fill me-1"></i>모든 참여자 수령 완료</div>';
            }
        } else {
            if (item.receiveDate) {
                statusMessageHtml = '<div class="text-success small fw-bold mt-1"><i class="bi bi-check-circle-fill me-1"></i>수령 완료</div>';
            }
        }

        const baseDetailUrl = (groupBuyType === 'PERIOD') ? `/periodGroupBuy/detail/${item.periodGroupBuyNo}` : `/quantityGroupBuy/detail/${item.quantityGroupBuyNo}`;

        return `
    <div class="group-card mb-3 p-3 border rounded bg-white shadow-sm">
        ${isOwner ? timelineHtml : ''}
        <div class="d-flex align-items-center justify-content-between">
            <div class="d-flex align-items-center gap-3 flex-grow-1" onclick="location.href='${baseDetailUrl}'" style="cursor: pointer;">
                <div class="rounded overflow-hidden border" style="width: 70px; height: 70px; flex-shrink: 0;">
                    <img src="${item.imageUrl || '/img/default_food.jpg'}" class="w-100 h-100 object-fit-cover">
                </div>
                <div class="group-info">
                    <h6 class="fw-bold mb-0 text-truncate" style="max-width: 220px;">${item.title}</h6>
                    ${statusMessageHtml}
                </div>
            </div>
            ${isOwner ? buttonsHtml : ''}
        </div>
    </div>`;
    };


    function fetchProfileData(memberNo) {
        api.fetch(`/api/mypage/${memberNo}/profile`).then(res => res.json()).then(renderCommonArea).catch(console.error);
    }
    const fetchRecipeData = (memberNo) => api.fetch(`/api/mypage/${memberNo}/recipe`).then(res => res.json());
    const fetchReviewData = (memberNo) => api.fetch(`/api/mypage/${memberNo}/reviewList`).then(res => res.json());

    const fetchGroupData = async () => {
        const listEl = document.getElementById('group-list');
        // const countEl = document.getElementById('statGroupCount');



        if (!listEl) {
            console.error("데이터가 없습니다.");
            return;
        }

        listEl.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success"></div></div>';

        try {
            const baseUrl = (currentGroupTab === 'participate') ? `/api/mypage/${memberNo}/groupBuy/participation` : `/api/mypage/${memberNo}/groupBuy/host`;
            const url = `${baseUrl}?filter=${currentFilterStatus}`;


            const response = await api.fetch(url);
            if (!response.ok) throw new Error("Network Error");
            const dataList = await response.json();



            if (!dataList || dataList.length === 0) {
                listEl.innerHTML = '<div class="text-center py-5 text-muted">내역이 없습니다.</div>';
                // if(countEl) countEl.innerText = '0';
            } else {
                listEl.innerHTML = dataList.map(createGroupCard).join('');
                // if(countEl) countEl.innerText = dataList.length;
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
                    api.fetch(`/api/mypage/groupbuy/shared`, {
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

    document.body.addEventListener('click', function(e) {
        const reportBtn = e.target.closest('.btn-report-member');

        if (reportBtn) {
            e.preventDefault();
            const targetMemberNo = reportBtn.getAttribute('data-member-no');

            showReportModal('MEMBER', targetMemberNo);
        }
    });

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

    const filterRecipeBtn = document.getElementById('filterRecipe');
    const filterReviewBtn = document.getElementById('filterReview');
    const recipeListSection = document.getElementById('recipe-list');
    const reviewListSection = document.getElementById('review-list');

    if (filterRecipeBtn && filterReviewBtn && recipeListSection && reviewListSection) {

        // 레시피 버튼 클릭 시
        filterRecipeBtn.addEventListener('change', () => {
            if (filterRecipeBtn.checked) {
                recipeListSection.style.display = 'grid'; // 또는 'block' (CSS에 맞게)
                reviewListSection.style.display = 'none';
            }
        });

        // 후기 버튼 클릭 시
        filterReviewBtn.addEventListener('change', () => {
            if (filterReviewBtn.checked) {
                recipeListSection.style.display = 'none';
                reviewListSection.style.display = 'grid'; // 또는 'block'
            }
        });
    }

    //공동구매 참여/개설탭 & 상태필터링 (isOwner일 때만)
    const renderGroupFilters = () => {
        const container = document.getElementById('group-filters-container');

        if (!container) return;

        if(isOwner) {
            // isOwner일 때만 필터 표시
            container.style.display = 'flex';

            const btnParticipate = document.getElementById('btnParticipate');
            const btnOpen = document.getElementById('btnOpen');
            const statusFilterEl = document.getElementById('groupStatusFilter');

            if(btnParticipate) {
                btnParticipate.addEventListener('change', () => {
                    if(btnParticipate.checked) {
                        currentGroupTab = 'participate';
                        fetchGroupData(); // 데이터 다시 로드
                    }
                });
            }
            if(btnOpen) {
                btnOpen.addEventListener('change', () => {
                    if(btnOpen.checked) {
                        currentGroupTab = 'host';
                        fetchGroupData(); // 데이터 다시 로드
                    }
                });
            }

            if (statusFilterEl) {
                statusFilterEl.addEventListener('change', function(e) {
                    currentFilterStatus = e.target.value; // ALL, Open, PAID ...
                    fetchGroupData(); // 데이터 다시 로드
                });
            }
        }else {
            // isOwner가 아니면 필터 숨김
            container.style.display = 'none';
        }
    }


    renderGroupFilters();
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

    updateTotalGroupCount();

    document.addEventListener('click', (e) => {
        const btn = e.target.closest('#headerMenuBtn');
        const menu = document.getElementById('headerDropdown');
        const logoutBtn = e.target.closest('#btnLogout')
        const removebtn = e.target.closest('#removeMember');
        const recipeDeleteBtn = e.target.closest('.btn-delete');

        const cancelPartBtn = e.target.closest('.btn-cancel-participation');

        if(btn && menu) {
            e.stopPropagation();
            menu.classList.toggle('show');
        }else if(logoutBtn){
            sessionStorage.removeItem("au");
            location.href="/login";
        }

        else if(removebtn){
            showRemoveMemberModal(memberNo);
            menu.classList.remove('show');
        }else if(recipeDeleteBtn) {
            e.preventDefault();
            e.stopPropagation();
            const recipeNo = recipeDeleteBtn.getAttribute('data-id');
            showAlertModal(
                '레시피 삭제',
                '레시피를 삭제하시겠습니까?',
                'error',
                () => removeRecipe(recipeNo)
            )
        } else if (cancelPartBtn){
            e.preventDefault();
            e.stopPropagation();
            try {
                // 버튼에 심어둔 데이터 꺼내기
                const item = JSON.parse(decodeURIComponent(cancelPartBtn.getAttribute('data-item')));

                // 경고창(모달) 띄우기 -> 확인 누르면 cancelParticipation 실행
                showAlertModal(
                    '참여 취소',
                    '정말 공동구매 참여를 취소하시겠습니까?',
                    'warning',
                    () => cancelParticipation(item.groupParticipantNo, item.type)
                );
            } catch (err) {
                console.error("데이터 파싱 오류:", err);
            }
        } else if(logoutBtn){
            e.preventDefault();
            location.href = "/logout";
        }
        else if(menu) {
            menu.classList.remove('show');
        }
    });
});