(function () {
    const list = document.getElementById('favorite-list');

    function removeFavorite(id) {
        apiFetch('/api/favorites/' + id, { method: 'DELETE' })
            .then(loadFavorites)
            .catch(err => alert(err.message));
    }

    function loadFavorites() {
        apiFetch('/api/favorites')
            .then(favorites => {
                if (favorites.length === 0) {
                    list.innerHTML = '<li>즐겨찾기한 국가가 없습니다.</li>';
                    return;
                }
                list.innerHTML = favorites.map(f =>
                    '<li>' +
                    '<a href="/country/' + f.countryCode + '">' + f.countryCode + '</a>' +
                    '<button class="danger" data-id="' + f.id + '">삭제</button>' +
                    '</li>'
                ).join('');

                list.querySelectorAll('button[data-id]').forEach(btn => {
                    btn.addEventListener('click', () => removeFavorite(btn.dataset.id));
                });
            })
            .catch(err => renderListError(list, err.message));
    }

    loadFavorites();
})();
