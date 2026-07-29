(function () {
    const app = document.getElementById('app');
    const countryCode = app.dataset.countryCode;

    apiFetch('/api/countries/' + countryCode)
        .then(country => {
            app.innerHTML = countryDetailHtml(country);
            document.getElementById('favorite-btn').addEventListener('click', () => {
                toggleCountryFavorite(countryCode, country.favorite, () => location.reload());
            });
        })
        .catch(err => renderCardError(app, err.message));
})();
