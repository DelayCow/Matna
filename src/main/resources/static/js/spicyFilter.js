function initializeSpicyIcons() {
    const spicyIcons = document.querySelectorAll('.spicy-level-icon');

    spicyIcons.forEach(icon => {
        const level = parseInt(icon.getAttribute('data-level'));
        const iconContainer = icon.querySelector('.icon-container');
        const spicyImagePath = '/img/spicy.png';

        if (iconContainer) {
            iconContainer.innerHTML = '';

            if (level > 0) {
                for (let i = 0; i < level; i++) {
                    const img = document.createElement('img');
                    img.src = spicyImagePath;
                    img.alt = `${level}단계 고추 아이콘`;
                    iconContainer.appendChild(img);
                }
            }
        }

        icon.addEventListener('click', function(e) {
            if(this.classList.contains('active')){
                this.classList.remove('active');
                currentSpicyLevel = null;
            }else{
                spicyIcons.forEach(i => i.classList.remove('active'));
                this.classList.add('active');
                currentSpicyLevel = level;
            }
            fetchRecipeData(true);
        });
    });
}