document.addEventListener('DOMContentLoaded', function() {
    const countRadio = document.getElementById('count');
    const weightRadio = document.getElementById('weight');
    const unitLabels = document.querySelectorAll('.unit-label');
    const daySelects = document.querySelectorAll('.day-select')
    const hourSelects = document.querySelectorAll('.time-select');

    // const fetchPeriodHomeData = function(){
    //     return fetch(`/api/period-group-buy/home`,{
    //         method: 'GET'
    //     }).then(response => {
    //         return response.json();
    //     })
    // }

    createDayOptions(daySelects, 7, 1);
    createTimeOptions(hourSelects);
    updateUnits();

    function updateUnits() {
        let unitText = '';

        if (weightRadio.checked) {
            unitText = 'g';
        } else if (countRadio.checked) {
            unitText = '개';
        }
        unitLabels.forEach(span => span.textContent = unitText);
    }
    countRadio.addEventListener('change', updateUnits);
    weightRadio.addEventListener('change', updateUnits);

    function createDayOptions(selectElements, maxDays, selectedDay = 1) {
        selectElements.forEach(selectElement => {
            selectElement.innerHTML = '';
            for (let i = 1; i <= maxDays; i++) {
                const option = document.createElement('option');
                option.value = i;
                option.textContent = i;
                if (i === selectedDay) {
                    option.selected = true;
                }
                selectElement.appendChild(option);
            }
        });
    }

    function createTimeOptions(selectElements) {
        selectElements.forEach(selectElement => {
            selectElement.innerHTML = '';
            let defaultSelected = false;

            for (let h = 0; h < 24; h++) {
                for (let m = 0; m < 60; m += 30) {
                    const hour = String(h).padStart(2, '0');
                    const minute = String(m).padStart(2, '0');
                    const timeValue = `${hour}:${minute}`;

                    const option = document.createElement('option');
                    option.value = timeValue;
                    option.textContent = timeValue;

                    if (timeValue === '00:00' && !defaultSelected) {
                        option.selected = true;
                        defaultSelected = true;
                    }

                    selectElement.appendChild(option);
                }
            }
        })
    }
});