(function () {
    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('search-country-code');
    const searchResults = document.getElementById('search-results');
    const reviewForm = document.getElementById('review-form');
    const reviewList = document.getElementById('review-list');
    const appData = document.getElementById('app-data');

    let currentCode = (appData.dataset.countryCode || '').toUpperCase();

    function loadReviews() {
        if (!currentCode) {
            reviewList.innerHTML = '<li class="rounded-xl bg-slate-50 px-4 py-6 text-center text-sm text-slate-400">국가 코드 또는 국가명을 입력해 검색해주세요.</li>';
            return;
        }
        apiFetch('/api/reviews?countryCode=' + currentCode)
            .then(reviews => {
                if (reviews.length === 0) {
                    reviewList.innerHTML = '<li class="rounded-xl bg-slate-50 px-4 py-6 text-center text-sm text-slate-400">등록된 후기가 없습니다.</li>';
                    return;
                }
                reviewList.innerHTML = reviews.map(r =>
                    '<li class="rounded-xl border border-slate-100 bg-white px-4 py-3 transition hover:border-primary-200">' +
                    '<a class="block text-sm" href="/reviews/' + r.id + '">' +
                    '<span class="text-amber-500">' + '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating) + '</span> ' +
                    '<span class="font-medium text-slate-700">[' + r.countryName + '] ' + r.authorNickname + '</span>'
                    + ' <span class="text-slate-500">- ' + r.content.substring(0, 40) + '</span>' +
                    '</a>' +
                    '</li>'
                ).join('');
            })
            .catch(err => renderListError(reviewList, err.message));
    }

    function selectCountry(code) {
        currentCode = code.toUpperCase();
        searchInput.value = currentCode;
        searchResults.innerHTML = '';
        history.replaceState(null, '', '/reviews?countryCode=' + currentCode);
        loadReviews();
    }

    searchForm.addEventListener('submit', async event => {
        event.preventDefault();
        const keyword = searchInput.value.trim();
        searchResults.innerHTML = '';
        if (!keyword) {
            return;
        }

        // 2글자 알파벳은 국가 코드로 간주해 바로 검색한다.
        if (/^[A-Za-z]{2}$/.test(keyword)) {
            selectCountry(keyword);
            return;
        }

        try {
            const results = await apiFetch('/api/countries/search?keyword=' + encodeURIComponent(keyword));
            if (results.length === 0) {
                renderListError(searchResults, '검색 결과가 없습니다.');
            } else if (results.length === 1) {
                selectCountry(results[0].code);
            } else {
                searchResults.innerHTML = results.map(r =>
                    '<li><a href="#" data-code="' + r.code + '" class="block px-4 py-3 text-sm text-slate-700 transition hover:bg-primary-50">'
                    + r.nameKo + ' (' + r.nameEn + ', ' + r.code + ')</a></li>'
                ).join('');
                searchResults.querySelectorAll('a[data-code]').forEach(a => {
                    a.addEventListener('click', event2 => {
                        event2.preventDefault();
                        selectCountry(a.dataset.code);
                    });
                });
            }
        } catch (err) {
            renderListError(searchResults, err.message);
        }
    });

    reviewForm.addEventListener('submit', event => {
        event.preventDefault();
        if (!currentCode) {
            alert('먼저 국가 코드 또는 국가명을 검색해주세요.');
            return;
        }
        const rating = Number(document.getElementById('rating').value);
        const content = document.getElementById('content').value;

        apiFetch('/api/reviews', {
            method: 'POST',
            body: JSON.stringify({ countryCode: currentCode, rating: rating, content: content })
        }).then(() => {
            document.getElementById('content').value = '';
            loadReviews();
        }).catch(err => alert(err.message));
    });

    if (currentCode) {
        loadReviews();
    } else {
        reviewList.innerHTML = '<li class="rounded-xl bg-slate-50 px-4 py-6 text-center text-sm text-slate-400">국가 코드 또는 국가명을 입력해 검색해주세요.</li>';
    }
})();
