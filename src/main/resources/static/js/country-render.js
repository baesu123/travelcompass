function infoCard(icon, title, bodyHtml) {
    return (
        '<div class="flex h-full flex-col rounded-2xl bg-white p-6 shadow-card transition hover:shadow-card-hover">' +
        '<div class="mb-3 flex items-center gap-2">' +
        '<span class="flex h-9 w-9 items-center justify-center rounded-lg bg-primary-50 text-lg">' + icon + '</span>' +
        '<h3 class="text-base font-semibold text-slate-800">' + title + '</h3>' +
        '</div>' +
        '<div class="flex-1 space-y-1.5 text-sm text-slate-600">' + bodyHtml + '</div>' +
        '</div>'
    );
}

function timezoneHtml(timezone) {
    if (!timezone) {
        return infoCard('🕒', '현지 시각', '<p class="text-slate-400">시차 정보 없음</p>');
    }

    const diff = timezone.timeDifferenceHours;
    const diffLabel = diff === 0 ? '한국과 시차 없음' : '한국보다 ' + Math.abs(diff) + '시간 ' + (diff > 0 ? '빠름' : '느림');

    return infoCard('🕒', '현지 시각',
        '<p><span class="font-medium text-slate-800">현지 시각</span> ' + timezone.localTime + ' (' + timezone.timezone + ')</p>' +
        '<p><span class="font-medium text-slate-800">한국 시각</span> ' + timezone.koreaTime + '</p>' +
        '<p><span class="font-medium text-slate-800">시차</span> ' + diffLabel + '</p>'
    );
}

function attractionsHtml(country) {
    const attractions = (country.attractions || []).map(a =>
        '<li class="flex items-center justify-between border-b border-slate-100 py-2 last:border-0">' +
        '<span>' + a.title + '</span>' +
        '<span class="text-xs text-slate-400">약 ' + Math.round(a.distanceMeters) + 'm</span>' +
        '</li>'
    ).join('') || '<li class="text-slate-400">주변 관광지 정보 없음</li>';

    return infoCard('📍', '주변 관광지', '<ul class="max-h-[240px] overflow-y-auto">' + attractions + '</ul>');
}

function forecastHtml(country) {
    const forecast = (country.forecastDates || []).map((date, i) =>
        '<li class="flex items-center justify-between border-b border-slate-100 py-2 last:border-0">' +
        '<span>' + date + '</span>' +
        '<span class="text-slate-500">' + country.temperatureMin[i] + '℃ ~ ' + country.temperatureMax[i] + '℃'
        + ' <span class="text-xs text-primary-500">(강수 ' + country.precipitationProbability[i] + '%)</span></span>' +
        '</li>'
    ).join('') || '<li class="text-slate-400">날씨 예보 정보 없음</li>';

    return infoCard('⛅', '2주간 날씨 예보', '<ul class="max-h-[240px] overflow-y-auto">' + forecast + '</ul>');
}

function climateHtml(country) {
    return infoCard('🧳', '평균 기후 및 여행 준비물',
        '<p><span class="font-medium text-slate-800">평균 기온</span> ' + country.averageTemperature + '℃</p>' +
        '<p><span class="font-medium text-slate-800">평균 강수량</span> ' + country.averagePrecipitation + 'mm</p>' +
        '<p><span class="font-medium text-slate-800">추천 복장</span> ' + country.recommendedClothing + '</p>' +
        '<p><span class="font-medium text-slate-800">여행 팁</span> ' + country.travelTip + '</p>'
    );
}

function infoGridHtml(country, timezone, gridClass) {
    return (
        '<div class="grid gap-6 ' + gridClass + '">' +
        timezoneHtml(timezone) + forecastHtml(country) + climateHtml(country) + attractionsHtml(country) +
        '</div>'
    );
}

function countryDetailHtml(country, timezone) {
    const currencyEntry = country.currencies ? Object.entries(country.currencies)[0] : null;
    const currencyLabel = currencyEntry ? currencyEntry[1].name + ' (' + currencyEntry[0] + ')' : '정보 없음';
    const rateLabel = country.exchangeRateToKrw
        ? '100 ' + (country.currencyCode || '') + ' = ' + Math.round(country.exchangeRateToKrw * 100).toLocaleString() + '원'
        : '정보 없음';

    return (
        '<div class="rounded-2xl bg-white p-6 shadow-card sm:p-8">' +
        '<div class="flex flex-col gap-5 sm:flex-row sm:items-start sm:justify-between">' +
        '<div class="flex items-start gap-4">' +
        (country.flags ? '<img src="' + country.flags.png + '" alt="flag" class="h-12 w-16 rounded-md object-cover shadow-sm">' : '') +
        '<div>' +
        '<h2 class="text-xl font-bold text-slate-900 sm:text-2xl">' + country.commonName +
        ' <span class="text-sm font-medium text-slate-400">(' + country.countryCode + ')</span></h2>' +
        '<p class="mt-1 text-sm text-slate-500">' + country.officialName + '</p>' +
        '</div>' +
        '</div>' +
        '<div class="flex flex-wrap gap-2">' +
        '<button id="favorite-btn" class="' + (country.favorite ? UI.btnOutline : UI.btnAccent) + '">'
        + (country.favorite ? '★ 즐겨찾기 해제' : '☆ 즐겨찾기 추가') + '</button>' +
        '<a class="' + UI.btnPrimary + '" href="/country/' + country.countryCode + '">상세보기</a>' +
        '<a class="' + UI.btnOutline + '" href="/reviews?countryCode=' + country.countryCode + '">이 나라 후기 보기</a>' +
        '</div>' +
        '</div>' +
        '<dl class="mt-6 grid grid-cols-2 gap-4 border-t border-slate-100 pt-6 sm:grid-cols-4">' +
        '<div><dt class="text-xs font-medium uppercase tracking-wide text-slate-400">수도</dt>' +
        '<dd class="mt-1 text-sm text-slate-700">' + ((country.capital || []).join(', ') || '정보 없음') + '</dd></div>' +
        '<div><dt class="text-xs font-medium uppercase tracking-wide text-slate-400">지역</dt>' +
        '<dd class="mt-1 text-sm text-slate-700">' + country.region + ' / ' + country.subregion + '</dd></div>' +
        '<div><dt class="text-xs font-medium uppercase tracking-wide text-slate-400">통화</dt>' +
        '<dd class="mt-1 text-sm text-slate-700">' + currencyLabel + '</dd></div>' +
        '<div><dt class="text-xs font-medium uppercase tracking-wide text-slate-400">환율</dt>' +
        '<dd class="mt-1 text-sm text-slate-700">' + rateLabel + '</dd></div>' +
        '</dl>' +
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
