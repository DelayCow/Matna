import { showAlertModal, showShareConfirmModal } from "./modal.js";

document.addEventListener('DOMContentLoaded', function() {

    const isOwnerText = document.getElementById('isOwner').textContent.trim().toLowerCase();
    const isOwner = isOwnerText === 'true';

    const memberNo = document.getElementById('memberNo').textContent;
    let currentGroupTab = 'participate';
    let currentFilterStatus = 'ALL';


    const getStatusStep = (status) => {

        const cleanStatus = String(status).trim().toUpperCase();


        switch (cleanStatus) {
            case 'OPEN':
            case 'RECRUITING':
                return 1;

            case 'CLOSED':
            case 'PAYMENT_WAIT':
                return 2;

            case 'PAID':
            case 'DELIVERED':
                return 3;

            case 'SHARED':
            case 'COMPLETED':
                return 4;

            case 'CANCELED':
                return 0;

            default:
                return 1;
        }
    };

    const getButtonConfig = (status, groupBuyId) => {
        const s = String(status).trim().toUpperCase();

        // 1. 모집중: 참여 취소 (모달)
        if (s === 'OPEN' || s === 'RECRUITING') {
            return {
                text: "참여 취소",
                cls: "btn-outline-danger",
                type: "modal",
                target: "#cancelModal"
            };
        }


        if (s === 'CLOSED' || s === 'PAYMENT_WAIT') {
            return null;
        }


        if (s === 'PAID') {
            return {
                text: "결제정보 확인",
                cls: "btn-outline-primary",
                type: "link",                        // ★ 추가됨
                target: `/payment/info?no=${groupBuyId}` // ★ 추가됨
            };
        }


        if (s === 'DELIVERED') {
            return {
                text: "도착정보 확인",
                cls: "btn-success",
                type: "link",
                target: `/delivery/info?no=${groupBuyId}`
            };
        }


        if (s === 'SHARED' || s === 'COMPLETED') {
            return {
                text: "나눔 받았어요!",
                cls: "btn-success",
                type: "custom",
                action: "share",
                target: "#shareConfirmModal"
            };
        }


        return {
            text: "상세 보기",
            cls: "btn-outline-secondary",
            type: "link",
            target: `/groupBuy/detail?no=${groupBuyId}`
        };
    };


    const renderCommonArea = (data) => {
        const headerArea = document.getElementById('header-right-area');
        const profileArea = document.getElementById('profile-main-area');
        // const btnsArea = document.getElementById('profile-action-btns');

        if (!data) data = {};

        const nickname = data.nickname || "맛도리 회원님";
        const image = data.imageUrl || "/img/user.png";
        const money = data.points || 0;
        const profileMemberNo = data.memberNo;

        // const isOwner = false; // [테스트용] 일단 내 페이지라고 가정

        if (isOwner && headerArea) {
            headerArea.innerHTML = `<button class="btn p-0 border-0" id="headerMenuBtn"><i class="bi bi-three-dots-vertical fs-4 text-dark"></i></button>
            <ul class="custom-dropdown" id="headerDropdown"><li><a href="#">정보 수정</a></li><li><a href="/logout">로그아웃</a></li><li><a href="#" class="text-danger">탈퇴</a></li></ul>`;
        } else if (headerArea) { headerArea.innerHTML = ''; }

        let subInfo = isOwner
            ? `<small class="text-muted">내 맛나머니 : ${money.toLocaleString()} 원</small>`
            : `<button class="btn btn-outline-secondary btn-sm rounded-pill px-2 py-0 mt-1"><i class="bi bi-exclamation-circle me-1"></i>신고하기</button>`;

        if(profileArea) {
            profileArea.innerHTML =
                `<img src="${image}" class="rounded-circle border me-3" width="60" height="60"><div><h5 class="fw-bold mb-1">${nickname}</h5><div>${subInfo}</div></div>`;
        }

        // if(btnsArea) {
        //     btnsArea.innerHTML = isOwner ? '' : `
        //         <div class="d-flex gap-2">
        //             <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">채팅</button>
        //             <button class="btn btn-success flex-grow-1 text-white shadow-sm py-2" style="background-color:#6CC537;border:none;">팔로우</button>
        //         </div>`;
        // }
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

        return `
    <div class="review-card mb-4 col-12" data-id="${item.reviewNo}">
        <div class="card-img-wrap">
            <img src="${imgUrl}" alt="${item.title}" onerror="this.src='/img/default_profile.jpg'">
        </div>
        <div class="card-info mt-2 p-2">
            <h5 class="card-title">${item.title}</h5>
            <div class="d-flex align-items-center mb-2">
                <span class="text-warning me-1"><i class="bi bi-star-fill"></i></span>
                <span class="fw-bold me-1">${item.rating}</span>
            </div>
            </div>
    </div>`;
    };

    const startCountdown = (timerElement) => {
        const dueDateString = timerElement.dataset.dueDate;
        if (!dueDateString) {
            timerElement.innerHTML = '마감일 정보 없음';
            return;
        }

        const targetDate = new Date(dueDateString);

        const updateCountdown = () => {
            const now = new Date().getTime();
            const distance = targetDate.getTime() - now;

            if (distance <= 0) {
                clearInterval(timerInterval);
                timerElement.innerHTML = '<span class="badge bg-secondary">모집마감</span>';
                timerElement.classList.remove('text-danger');
                timerElement.classList.add('text-muted');
                return;
            }

            const D_IN_MS = 1000 * 60 * 60 * 24;
            const H_IN_MS = 1000 * 60 * 60;
            const M_IN_MS = 1000 * 60;

            const days = Math.floor(distance / D_IN_MS);
            const hours = Math.floor((distance % D_IN_MS) / H_IN_MS);
            const minutes = Math.floor((distance % H_IN_MS) / M_IN_MS);
            const seconds = Math.floor((distance % M_IN_MS) / 1000);

            // 화면 표시 형식
            timerElement.innerHTML = `
                <i class="bi bi-clock"></i> 남은 시간 : 
                ${days}일 ${hours}시간 ${minutes}분 ${seconds}초
            `;
        };

        const timerInterval = setInterval(updateCountdown, 1000);
        updateCountdown(); // 즉시 실행
    };


    const createGroupCard = (item) => {


        const unit = item.unit || '';

        const currentStep = getStatusStep(item.status);

        const btnConfig = getButtonConfig(item.status, item.groupBuyNo);

        console.log("제목:", item.title, " / 수령일:", item.receiveDate);

        const steps = ["모집", "상품결제", "상품도착", "나눔진행"];
        let timelineHtml = '<div class="timeline-steps">';
        // 계산 식 다시 해야 함 아오
        steps.forEach((stepName, index) => {
            const stepNum = index + 1;
            let activeClass = "";
            if (stepNum < currentStep) activeClass = "active";
            else if (stepNum === currentStep) activeClass = "current";
            timelineHtml += `<div class="step-item ${activeClass}"><div class="step-circle"></div><span class="step-text">${stepName}</span></div>`;
        });
        timelineHtml += '</div>';


        if(!isOwner){timelineHtml = '';}

        let buttonHtml = '';

        if (isOwner && btnConfig) {

            // 1. 링크 이동 버튼 (type: link)
            if (btnConfig.type === 'link') {
                buttonHtml = `
                <button class="btn ${btnConfig.cls} btn-sm text-nowrap z-index-front" 
                        style="font-size: 0.75rem;" 
                        onclick="event.stopPropagation(); location.href='${btnConfig.target}'">
                    ${btnConfig.text}
                </button>`;
            }
            // 2. 일반 모달 버튼 (type: modal) - 참여 취소 등
            else if (btnConfig.type === 'modal') {
                buttonHtml = `
                <button class="btn ${btnConfig.cls} btn-sm text-nowrap z-index-front" 
                        style="font-size: 0.75rem;" 
                        data-bs-toggle="modal" 
                        data-bs-target="${btnConfig.target}" 
                        onclick="event.stopPropagation()">
                    ${btnConfig.text}
                </button>`;
            }


            // 3. 커스텀 액션 버튼 (type: custom) - 나눔 완료
            else if (btnConfig.type === 'custom' && btnConfig.action === 'share') {


                const payAmount = item.finalPaymentPoint || 0;

                const dataToSend = {
                    title: item.title,
                    price: payAmount,
                    amount: item.myQuantity,
                    unit: item.unit || '',
                    groupBuyNo: item.groupBuyNo,
                    groupParticipantNo: item.groupParticipantNo
                };

                const itemData = encodeURIComponent(JSON.stringify(dataToSend));

                buttonHtml = `
                <button class="btn ${btnConfig.cls} btn-sm text-nowrap z-index-front btn-share-confirm" 
                        style="font-size: 0.75rem;"
                        data-item="${itemData}">
                    ${btnConfig.text}
                </button>`;
            }
        }

        if (item.receiveDate) {
            buttonHtml = `<button class="btn btn-secondary btn-sm" disabled>수령 완료</button>`;
        }

        // (B) 상세 정보(수량, 가격 등): 주인이 아니면 안 보여줌
        const detailsHtml = isOwner
            ? `
            <div class="group-details">
                <span>신청 수량 <strong>${item.myQuantity}${unit}</strong></span>
           
                <div class="text-muted" style="font-size: 0.8rem;">나 외에 ${item.participantExMe}명 참여 중</div>
                ${ item.remainingQuantity > 0
                ? `<div class="text-primary fw-bold mt-1" style="font-size: 0.8rem;">남은 수량: ${item.remainingQuantity}${unit}</div>`
                : `<div class="text-secondary fw-bold mt-1" style="font-size: 0.8rem;">모집 완료</div>`
            }
            </div>`
            : '';

        let timerHtml = '';
        if (item.dueDate) {
            timerHtml = `
            <div class="mt-2">
                <small class="text-danger fw-bold countdown-timer" data-due-date="${item.dueDate}">
                    <i class="bi bi-clock"></i> 남은 시간 계산 중...
                </small>
            </div>
        `;
        }

        const detailLinkToThumnail = `/groupBuy/detail?no=${item.groupBuyNo}`;
        return `
        <div class="group-card mb-3 p-3 border rounded bg-white shadow-sm" style="cursor:default;">
        <div class="d-flex justify-content-between align-items-start mb-2">
            <div class="flex-grow-1 me-3">${timelineHtml}</div>
            
            ${buttonHtml} 
            
        </div>
        <div class="d-flex align-items-center gap-3">
            <div class="rounded overflow-hidden border" style="width: 80px; height: 80px; flex-shrink: 0; cursor: pointer;" onclick="location.href='${detailLinkToThumnail}'">
                <img src="${item.imageUrl || '/img/default_food.jpg'}" alt="${item.title}" class="w-100 h-100 object-fit-cover">
            </div>
            <div class="group-info flex-grow-1">
                <h5 class="fw-bold mb-1" style="font-size: 1rem;">${item.title}</h5>
                
                ${detailsHtml}
                
            </div>
        </div>
    </div>`;
    };


    function fetchProfileData(memberNo) {
        fetch(`/api/mypage/${memberNo}/profile`)
            .then(res => res.json())
            .then(data => {
                renderCommonArea(data);

            })
            .catch(err => console.error("프로필 로드 실패", err));
    }

    const fetchRecipeData = function(memberNo){
        return fetch(`/api/mypage/${memberNo}/recipe`,{ method: 'GET' })
            .then(response => response.json());
    };

    const fetchReviewData = function(memberNo) {
        return fetch(`/api/mypage/${memberNo}/reviewList`, { method: 'GET' })
            .then(response => response.json());
    };


    const fetchGroupData = async () => {
        const listEl = document.getElementById('group-list');
        const countEl = document.getElementById('statGroupCount');


        listEl.innerHTML = '<div class="text-center py-5"><div class="spinner-border text-success" role="status"></div></div>';

        try {

            const baseUrl = (currentGroupTab === 'participate')
                ? `/api/mypage/${memberNo}/groupBuy/participation`
                : `/api/mypage/${memberNo}/groupBuy/host`;

            const url = `${baseUrl}?filter=${currentFilterStatus}`;

            const response = await fetch(url);
            if (!response.ok) throw new Error("Network Error");
            // {
            //     const errorMessage = await response.text();
            //     throw new Error(`서버 에러 (${response.status}): ${errorMessage}`);
            // }
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
            listEl.innerHTML = '<div class="text-center py-5 text-danger">데이터를 불러오지 못했습니다.</div>';
        }
    };

    const groupListEl = document.getElementById('group-list');

    if (groupListEl) {
        groupListEl.addEventListener('click', function(e) {
            // 1. 클릭된 요소가 '나눔 버튼'인지 확인
            const shareBtn = e.target.closest('.btn-share-confirm');

            if (shareBtn) {
                // 2. 카드 클릭(상세페이지 이동) 방지
                e.preventDefault();
                e.stopPropagation();

                // 3. 버튼에 숨겨둔 데이터 꺼내기
                const itemDataString = shareBtn.getAttribute('data-item');

                if (itemDataString) {
                    try {
                        // ★★★ [핵심 수정] decodeURIComponent로 포장을 뜯어줍니다! ★★★
                        const item = JSON.parse(decodeURIComponent(itemDataString));

                        // 4. 모달 띄우기 (import한 함수 사용)
                        showShareConfirmModal(item, (selectedDate) => {

                            const requestBody = {
                                groupParticipantNo: item.groupParticipantNo,
                                receiveDate: selectedDate + "T00:00:00"
                            };

                            fetch(`/api/mypage/groupbuy/shared`, {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(requestBody)
                            })
                                .then(response => {
                                    if (response.ok) {
                                        alert("수령이 확정되었습니다!");
                                        window.location.reload(); // 새로고침
                                    } else {
                                        alert("오류가 발생했습니다.");
                                    }
                                })
                                .catch(err => {
                                    console.error(err);
                                    alert("서버 통신 실패");
                                });
                        });
                    } catch (err) {
                        console.error("데이터 오류:", err);
                    }
                }
            }
        });
    }



    const statTabRecipe = document.getElementById('statTabRecipe');
    const statTabGroup = document.getElementById('statTabGroup');
    const wrapRecipe = document.getElementById('recipe-section-wrapper');
    const wrapGroup = document.getElementById('group-section-wrapper');

    if(statTabRecipe) statTabRecipe.addEventListener('click', () => {
        statTabRecipe.classList.add('active'); statTabGroup.classList.remove('active');
        wrapRecipe.style.display = 'block'; wrapGroup.style.display = 'none';
    });
    if(statTabGroup) statTabGroup.addEventListener('click', () => {
        statTabGroup.classList.add('active'); statTabRecipe.classList.remove('active');
        wrapGroup.style.display = 'block'; wrapRecipe.style.display = 'none';
    });


    const btnParticipate = document.getElementById('btnParticipate');
    const btnOpen = document.getElementById('btnOpen');

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


    const statusFilterEl = document.getElementById('groupStatusFilter');
    if (statusFilterEl) {
        statusFilterEl.addEventListener('change', function(e) {
            currentFilterStatus = e.target.value; // ALL, OPEN, PAID ...
            fetchGroupData(); // 데이터 다시 로드
        });
    }


    document.addEventListener('click', (e) => {
        const btn = e.target.closest('#headerMenuBtn');
        const menu = document.getElementById('headerDropdown');
        if(btn && menu) { e.stopPropagation(); menu.classList.toggle('show'); }
        else if(menu) { menu.classList.remove('show'); }
    });

    const filterRecipe = document.getElementById('filterRecipe');
    const filterReview = document.getElementById('filterReview');
    const listRecipe = document.getElementById('recipe-list');
    const listReview = document.getElementById('review-list');

    // 레시피 목록 보여주는 함수
    const showRecipeList = () => {
        if(listRecipe) listRecipe.style.display = 'grid'; // 또는 'block' (CSS에 맞게)
        if(listReview) listReview.style.display = 'none';

        // 버튼 스타일 활성화 (선택사항: CSS에 .active가 있다면)
        if(filterRecipe) filterRecipe.classList.add('active');
        if(filterReview) filterReview.classList.remove('active');
    };

    // 후기 목록 보여주는 함수
    const showReviewList = () => {
        if(listReview) listReview.style.display = 'grid'; // 또는 'block'
        if(listRecipe) listRecipe.style.display = 'none';

        // 버튼 스타일 활성화
        if(filterReview) filterReview.classList.add('active');
        if(filterRecipe) filterRecipe.classList.remove('active');
    };

    // 이벤트 리스너 연결 (클릭 시 실행)
    if (filterRecipe) {
        filterRecipe.addEventListener('click', showRecipeList);
        // 만약 라디오 버튼(<input type="radio">)이라면 'change' 이벤트도 추가
        filterRecipe.addEventListener('change', () => { if(filterRecipe.checked) showRecipeList(); });
    }

    if (filterReview) {
        filterReview.addEventListener('click', showReviewList);
        // 만약 라디오 버튼이라면 'change' 이벤트도 추가
        filterReview.addEventListener('change', () => { if(filterReview.checked) showReviewList(); });
    }

    if (listRecipe) {
        listRecipe.addEventListener('click', function(e) {
            const deleteButton = e.target.closest('.btn-delete');

            if (deleteButton) {
                const recipeNo = deleteButton.getAttribute('data-id');

                showAlertModal(
                    '레시피 삭제',
                    '레시피를 삭제하시겠습니까?',
                    'error',
                    () => removeRecipe(recipeNo)
                );
            }
        });
    }

    renderCommonArea();
    fetchProfileData(memberNo);

    // 레시피 로드
    fetchRecipeData(memberNo).then(recipeData => {
        const listEl = document.getElementById('recipe-list');
        const countEl = document.getElementById('statRecipeCount');
        if(recipeData && listEl) {
            listEl.innerHTML = recipeData.map(createRecipeCard).join('');
            if(countEl) countEl.innerText = recipeData.length;
        }
    }).catch(err => console.error(err));

    // 후기 로드
    fetchReviewData(memberNo).then(reviewList => {
        const listContainer = document.getElementById('review-list');
        if(listContainer) {
            if (!reviewList || reviewList.length === 0) {
                listContainer.innerHTML = '<div class="text-center w-100 py-5 text-muted">작성한 후기가 없습니다.</div>';
            } else {
                listContainer.innerHTML = reviewList.map(item => createReviewCard(item)).join('');
            }
        }
    }).catch(err => console.error(err));

    // 공동구매 리스트 초기 로드
    fetchGroupData();
});