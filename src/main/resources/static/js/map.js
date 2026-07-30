(function () {
    const panel = document.getElementById('detail-panel');
    const attractionsPanel = document.getElementById('attractions-panel');

    const map = L.map('map').setView([20, 0], 2);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
        maxZoom: 8
    }).addTo(map);

    function showMessage(message) {
        panel.innerHTML = '<div class="' + UI.card + ' flex min-h-[200px] items-center justify-center text-center text-sm text-slate-500">' + message + '</div>';
        attractionsPanel.innerHTML = '';
    }

    function showError(message) {
        renderCardError(panel, message);
        attractionsPanel.innerHTML = '';
    }

    function showCountry(code) {
        showMessage('불러오는 중...');
        Promise.all([
            apiFetch('/api/countries/' + code),
            apiFetch('/api/timezones/' + code).catch(() => null)
        ])
            .then(([country, timezone]) => {
                panel.innerHTML = countryDetailHtml(country, timezone) +
                    '<div class="mt-6">' + infoGridHtml(country, timezone, 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3') + '</div>';
                attractionsPanel.innerHTML = '';
                document.getElementById('favorite-btn').addEventListener('click', () => {
                    toggleCountryFavorite(code, country.favorite, () => showCountry(code));
                });
            })
            .catch(err => showError(err.message));
    }

    function onCountryClick(e) {
        const code = e.target.feature.properties.cca2;
        if (!code) {
            showMessage('이 지역은 지원하지 않는 국가입니다.');
            return;
        }
        showCountry(code);
    }

    fetch('/data/world-countries.geojson')
        .then(res => res.json())
        .then(geojson => {
            L.geoJSON(geojson, {
                style: {
                    color: '#1f6feb',
                    weight: 1,
                    fillColor: '#a7c7f2',
                    fillOpacity: 0.4
                },
                onEachFeature: (feature, layer) => {
                    layer.on({
                        click: onCountryClick,
                        mouseover: e => e.target.setStyle({ fillOpacity: 0.7 }),
                        mouseout: e => e.target.setStyle({ fillOpacity: 0.4 })
                    });
                }
            }).addTo(map);
        })
        .catch(() => showError('지도 데이터를 불러오지 못했습니다.'));
})();
