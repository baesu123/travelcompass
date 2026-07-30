(function () {
    const app = document.getElementById('app');
    const countryCode = app.dataset.countryCode;

    Promise.all([
        apiFetch('/api/countries/' + countryCode),
        apiFetch('/api/timezones/' + countryCode).catch(() => null)
    ])
        .then(([country, timezone]) => {
            app.innerHTML = countryDetailHtml(country, timezone) + attractionsHtml(country);
            document.getElementById('favorite-btn').addEventListener('click', () => {
                toggleCountryFavorite(countryCode, country.favorite, () => location.reload());
            });
        })
        .catch(err => renderCardError(app, err.message));
})();
