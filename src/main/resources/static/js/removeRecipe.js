import {showAlertModal, showValidationModal} from "./modal.js";

document.addEventListener('DOMContentLoaded', function(){
    const remove = document.querySelector('#removeRecipe');
    if(remove){
        const recipeNo = remove.getAttribute('data-id')
        remove.addEventListener('click', function(){
            showAlertModal(
                '레시피 삭제',
                '레시피를 삭제하시겠습니까?',
                'error',
                () => removeRecipe(recipeNo)
            )
        })
    }

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

    document.getElementById('writer-profile').addEventListener('click', function (){
        const writerNo = this.getAttribute('data-no');
        location.href=`/mypage/${writerNo}`;
    })
})