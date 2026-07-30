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
                    list.innerHTML = '<li class="rounded-xl bg-slate-50 px-4 py-6 text-center text-sm text-slate-400">즐겨찾기한 국가가 없습니다.</li>';
                    return;
                }
                list.innerHTML = favorites.map(f =>
                    '<li class="flex items-center justify-between gap-3 rounded-xl border border-slate-100 bg-white px-4 py-3 transition hover:border-primary-200">' +
                    '<a class="text-sm font-medium text-slate-700 hover:text-primary-700" href="/country/' + f.countryCode + '">' + f.countryName + ' (' + f.countryCode + ')</a>' +
                    '<button class="' + UI.btnDanger + '" data-id="' + f.id + '">삭제</button>' +
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
