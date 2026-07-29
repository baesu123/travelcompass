(function () {
    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('search-country-code');
    const reviewForm = document.getElementById('review-form');
    const reviewList = document.getElementById('review-list');
    const appData = document.getElementById('app-data');

    function currentCode() {
        return searchInput.value.trim().toUpperCase();
    }

    function loadReviews() {
        const code = currentCode();
        if (!code) {
            reviewList.innerHTML = '<li>국가 코드를 입력해 검색해주세요.</li>';
            return;
        }
        apiFetch('/api/reviews?countryCode=' + code)
            .then(reviews => {
                if (reviews.length === 0) {
                    reviewList.innerHTML = '<li>등록된 후기가 없습니다.</li>';
                    return;
                }
                reviewList.innerHTML = reviews.map(r =>
                    '<li>' +
                    '<a href="/reviews/' + r.id + '">' +
                    '<span class="rating-stars">' + '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating) + '</span> ' +
                    r.authorNickname + ' - ' + r.content.substring(0, 40) +
                    '</a>' +
                    '</li>'
                ).join('');
            })
            .catch(err => renderListError(reviewList, err.message));
    }

    searchForm.addEventListener('submit', event => {
        event.preventDefault();
        history.replaceState(null, '', '/reviews?countryCode=' + currentCode());
        loadReviews();
    });

    reviewForm.addEventListener('submit', event => {
        event.preventDefault();
        const code = currentCode();
        if (!code) {
            alert('먼저 국가 코드를 입력해주세요.');
            return;
        }
        const rating = Number(document.getElementById('rating').value);
        const content = document.getElementById('content').value;

        apiFetch('/api/reviews', {
            method: 'POST',
            body: JSON.stringify({ countryCode: code, rating: rating, content: content })
        }).then(() => {
            document.getElementById('content').value = '';
            loadReviews();
        }).catch(err => alert(err.message));
    });

    if (appData.dataset.countryCode) {
        loadReviews();
    } else {
        reviewList.innerHTML = '<li>국가 코드를 입력해 검색해주세요.</li>';
    }
})();
