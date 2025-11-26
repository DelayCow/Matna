document.addEventListener('DOMContentLoaded',function (){
    const attachPhotoButton = document.getElementById('attachPhotoButton');
    const fileInput = document.getElementById('fileInput');
    const previewContainer = document.getElementById('previewContainer');
    function removeFileAndReset() {
        fileInput.value = '';
        previewContainer.innerHTML = '';

        attachPhotoButton.textContent = '사진 첨부하기';
        attachPhotoButton.classList.remove('btn-success');
        attachPhotoButton.classList.add('btn-light');

        attachPhotoButton.disabled = false;
    }

    attachPhotoButton.addEventListener('click', function() {
        if (!attachPhotoButton.disabled) {
            fileInput.click();
        }
    });

    fileInput.addEventListener('change', function(e) {
        const files = e.target.files;
        previewContainer.innerHTML = '';

        if (files.length > 0) {
            const file = files[0];

            attachPhotoButton.textContent = `사진 1개 첨부됨`;
            attachPhotoButton.classList.remove('btn-light');
            attachPhotoButton.classList.add('btn-success');

            attachPhotoButton.disabled = true;

            if (file.type.startsWith('image/')) {
                const reader = new FileReader();

                reader.onload = function(e) {
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    img.alt = file.name;

                    const wrapper = document.createElement('div');
                    wrapper.classList.add('preview-wrapper');

                    const deleteBtn = document.createElement('span');
                    deleteBtn.classList.add('delete-btn');
                    deleteBtn.textContent = 'X';

                    deleteBtn.addEventListener('click', removeFileAndReset);

                    wrapper.appendChild(img);
                    wrapper.appendChild(deleteBtn);

                    previewContainer.appendChild(wrapper);
                };

                reader.readAsDataURL(file);
            }

        } else {
            removeFileAndReset();
        }
    });
})