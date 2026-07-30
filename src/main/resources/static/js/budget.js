(function () {
    document.getElementById('totalBudget').addEventListener('change', function () {
        document.getElementById('otherBudget').style.display = this.value === 'other' ? 'block' : 'none';
    });

    function spendBreakdownHtml(totalBudget, days, accommodation, transport, food, shopping) {
        const totalSpent = (accommodation * days) + (food * days) + transport + shopping;

        const accommodationPercent = (((accommodation * days) / totalSpent) * 100).toFixed(2);
        const foodPercent = (((food * days) / totalSpent) * 100).toFixed(2);
        const transportPercent = ((transport / totalSpent) * 100).toFixed(2);
        const shoppingPercent = ((shopping / totalSpent) * 100).toFixed(2);

        const budgetComparison = totalSpent <= totalBudget ? '예산 내에 있습니다.' : '예산을 초과하였습니다.';

        return (
            '<p><strong>총 소비액:</strong> ' + totalSpent.toLocaleString() + '원 (' + budgetComparison + ')</p>' +
            '<p>숙박비 비율: ' + accommodationPercent + '%</p>' +
            '<p>식비 비율: ' + foodPercent + '%</p>' +
            '<p>교통비 비율: ' + transportPercent + '%</p>' +
            '<p>쇼핑 및 기타 비용 비율: ' + shoppingPercent + '%</p>'
        );
    }

    function exchangeHtml(result) {
        return (
            '<p><strong>환산 숙박비 총액:</strong> ' + result.convertedAmount.toFixed(2) + ' ' + result.toCurrency + '</p>' +
            '<p>적용 환율: 1 ' + result.fromCurrency + ' = ' + result.rate.toFixed(4) + ' ' + result.toCurrency + '</p>'
        );
    }

    document.getElementById('budgetCalculator').addEventListener('submit', function (event) {
        event.preventDefault();

        let totalBudget = document.getElementById('totalBudget').value;
        if (totalBudget === 'other') {
            totalBudget = document.getElementById('otherBudget').value;
        }
        totalBudget = Number(totalBudget);

        const days = Number(document.getElementById('days').value);
        const accommodation = Number(document.getElementById('accommodation').value);
        const transport = Number(document.getElementById('transport').value);
        const food = Number(document.getElementById('food').value);
        const shopping = Number(document.getElementById('shopping').value);
        const targetCurrency = document.getElementById('targetCurrency').value.trim();

        const resultEl = document.getElementById('result');
        resultEl.innerHTML = spendBreakdownHtml(totalBudget, days, accommodation, transport, food, shopping);

        if (!targetCurrency) {
            return;
        }

        apiFetch('/api/exchange/budget', {
            method: 'POST',
            body: JSON.stringify({ days: days, costPerNightKrw: accommodation, targetCurrency: targetCurrency })
        })
            .then(result => { resultEl.innerHTML += exchangeHtml(result); })
            .catch(err => { resultEl.innerHTML += '<p class="error-message">' + err.message + '</p>'; });
    });
})();
