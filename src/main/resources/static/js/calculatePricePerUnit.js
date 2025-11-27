document.addEventListener('DOMContentLoaded',function (){
    const calculateButton = document.getElementById('calculateButton') || null;
    const priceInput = document.getElementById('price');
    const feeInput = document.getElementById('fee_rate');
    const totalQuantityInput = document.getElementById('quantity');
    const sharingUnitInput = document.getElementById('share_amount');
    const calculatedPriceSpan = document.getElementById('price_per_unit');

    calculateButton.addEventListener('click', calculatePrice);

    function calculatePrice() {
        const price = parseFloat(priceInput.value) || 0;
        const fee = parseFloat(feeInput.value) || 0;
        const totalQuantity = parseFloat(totalQuantityInput.value) || 0;
        const sharingUnit = parseFloat(sharingUnitInput.value) || 0;

        if (totalQuantity <= 0 || sharingUnit <= 0) {
            calculatedPriceSpan.textContent = '0';
            return;
        }

        const totalCostWithFee = price * (1 + fee / 100);
        const numberOfShares = totalQuantity / sharingUnit;
        const pricePerUnit = totalCostWithFee / numberOfShares;

        const formattedPrice = Math.round(pricePerUnit).toLocaleString('ko-KR');

        calculatedPriceSpan.textContent = formattedPrice;
    }
})