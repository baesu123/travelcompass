function timezoneHtml(timezone) {
    if (!timezone) {
        return '<div class="card"><h3>현지 시각</h3><p>시차 정보 없음</p></div>';
    }

    const diff = timezone.timeDifferenceHours;
    const diffLabel = diff === 0 ? '한국과 시차 없음' : '한국보다 ' + Math.abs(diff) + '시간 ' + (diff > 0 ? '빠름' : '느림');

    return (
        '<div class="card">' +
        '<h3>현지 시각</h3>' +
        '<p><strong>현지 시각:</strong> ' + timezone.localTime + ' (' + timezone.timezone + ')</p>' +
        '<p><strong>한국 시각:</strong> ' + timezone.koreaTime + '</p>' +
        '<p><strong>시차:</strong> ' + diffLabel + '</p>' +
        '</div>'
    );
}

function countryDetailHtml(country, timezone) {
    const currencyEntry = country.currencies ? Object.entries(country.currencies)[0] : null;
    const currencyLabel = currencyEntry ? currencyEntry[1].name + ' (' + currencyEntry[0] + ')' : '정보 없음';
    const rateLabel = country.exchangeRateToKrw
        ? '100 ' + (country.currencyCode || '') + ' = ' + Math.round(country.exchangeRateToKrw * 100).toLocaleString() + '원'
        : '정보 없음';

    const attractions = (country.attractions || []).map(a =>
        '<li>' + a.title + ' (약 ' + Math.round(a.distanceMeters) + 'm)</li>'
    ).join('') || '<li>주변 관광지 정보 없음</li>';

    const forecast = (country.forecastDates || []).map((date, i) =>
        '<li>' + date + ': ' + country.temperatureMin[i] + '℃ ~ ' + country.temperatureMax[i] + '℃'
        + ' (강수확률 ' + country.precipitationProbability[i] + '%)</li>'
    ).join('') || '<li>날씨 예보 정보 없음</li>';

    return (
        '<div class="card">' +
        '<h2>' + country.commonName + ' (' + country.countryCode + ')</h2>' +
        (country.flags ? '<img src="' + country.flags.png + '" alt="flag" style="height:60px;">' : '') +
        '<p><strong>공식 명칭:</strong> ' + country.officialName + '</p>' +
        '<p><strong>수도:</strong> ' + ((country.capital || []).join(', ') || '정보 없음') + '</p>' +
        '<p><strong>지역:</strong> ' + country.region + ' / ' + country.subregion + '</p>' +
        '<p><strong>통화:</strong> ' + currencyLabel + '</p>' +
        '<p><strong>환율:</strong> ' + rateLabel + '</p>' +
        '<button id="favorite-btn">' + (country.favorite ? '즐겨찾기 해제' : '즐겨찾기 추가') + '</button>' +
        ' <a class="btn" href="/reviews?countryCode=' + country.countryCode + '">이 나라 후기 보기</a>' +
        '</div>' +
        '<div class="card">' +
        '<h3>주변 관광지</h3><ul class="plain-list">' + attractions + '</ul>' +
        '</div>' +
        timezoneHtml(timezone) +
        '<div class="card">' +
        '<h3>2주간 날씨 예보</h3><ul class="plain-list forecast-list">' + forecast + '</ul>' +
        '</div>'
    );
}

function toggleCountryFavorite(countryCode, isFavorite, onDone) {
    const request = isFavorite
        ? apiFetch('/api/favorites').then(favorites => {
            const match = favorites.find(f => f.countryCode === countryCode);
            if (!match) {
                throw new Error('즐겨찾기 정보를 찾을 수 없습니다.');
            }
            return apiFetch('/api/favorites/' + match.id, { method: 'DELETE' });
        })
        : apiFetch('/api/favorites', { method: 'POST', body: JSON.stringify({ countryCode: countryCode }) });

    request.then(onDone).catch(err => alert(err.message));
}
