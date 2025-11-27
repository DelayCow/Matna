document.addEventListener('DOMContentLoaded', function() {
    const edit = document.querySelector('#edit-box');
    const editBox = document.querySelector('.edit-box');
    edit.addEventListener('click', function(e){
        e.stopPropagation();
        editBox.classList.toggle('show');
    })
    document.addEventListener('click', function() {
        if (editBox.classList.contains('show')) {
            editBox.classList.remove('show');
        }
    });
});