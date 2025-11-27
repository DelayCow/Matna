document.addEventListener('DOMContentLoaded',function (){
    const joinYearSelect = document.getElementById('joinYear');
    const joinMonthSelect = document.getElementById('joinMonth');
    const joinDaySelect = document.getElementById('joinDay');

    createDateOptions();
    function createDateOptions() {
        const today = new Date();
        const currentYear = today.getFullYear();
        const currentMonth = today.getMonth() + 1;

        joinYearSelect.innerHTML = '';
        const futureYears = 1;

        for (let y = currentYear; y <= currentYear + futureYears; y++) {
            const option = document.createElement('option');
            option.value = y;
            option.textContent = y;
            if (y === currentYear) {
                option.selected = true;
            }
            joinYearSelect.appendChild(option);
        }

        joinMonthSelect.innerHTML = '';
        for (let m = 1; m <= 12; m++) {
            const isPastMonth = (joinYearSelect.value === String(currentYear)) && (m < currentMonth);
            if (isPastMonth) {
                continue;
            }

            const option = document.createElement('option');
            option.value = m;
            option.textContent = m;

            if (m === currentMonth) {
                option.selected = true;
            }
            joinMonthSelect.appendChild(option);
        }

        updateDayOptions();

        joinMonthSelect.addEventListener('change', updateDayOptions);

        joinYearSelect.addEventListener('change', function() {
            createMonthOptions();
            updateDayOptions();
        });
    }
    function updateDayOptions() {
        const today = new Date();
        const currentMonth = today.getMonth() + 1;
        const currentDay = today.getDate();

        const year = parseInt(joinYearSelect.value);
        const month = parseInt(joinMonthSelect.value);

        const isCurrentMonth = (month === currentMonth);

        const lastDay = new Date(year, month, 0).getDate();

        const selectedDay = parseInt(joinDaySelect.value) || 1;

        joinDaySelect.innerHTML = '';
        for (let d = 1; d <= lastDay; d++) {
            if (isCurrentMonth && d < currentDay) {
                continue;
            }

            const option = document.createElement('option');
            option.value = d;
            option.textContent = d;

            if (isCurrentMonth && d === currentDay) {
                option.selected = true;
            }
            else if (!isCurrentMonth && d === selectedDay) {
                option.selected = true;
            }

            joinDaySelect.appendChild(option);
        }
    }

    function createMonthOptions() {
        const today = new Date();
        const currentYear = today.getFullYear();
        const currentMonth = today.getMonth() + 1;
        const selectedYear = parseInt(joinYearSelect.value);

        joinMonthSelect.innerHTML = '';

        const startMonth = (selectedYear === currentYear) ? currentMonth : 1;

        for (let m = 1; m <= 12; m++) {
            if (m < startMonth) {
                continue;
            }

            const option = document.createElement('option');
            option.value = m;
            option.textContent = m;

            if (m === startMonth) {
                option.selected = true;
            }
            joinMonthSelect.appendChild(option);
        }
    }
})