function csrfHeaders(extra) {
    const token = document.querySelector('meta[name="_csrf"]');
    const header = document.querySelector('meta[name="_csrf_header"]');
    const headers = Object.assign({}, extra);
    if (token && header) {
        headers[header.content] = token.content;
    }
    return headers;
}

async function apiFetch(url, options) {
    options = options || {};
    const method = (options.method || 'GET').toUpperCase();
    const needsCsrf = method !== 'GET' && method !== 'HEAD';

    let headers = Object.assign({}, options.headers);
    if (options.body) {
        headers['Content-Type'] = 'application/json';
    }
    if (needsCsrf) {
        headers = csrfHeaders(headers);
    }

    const response = await fetch(url, Object.assign({}, options, { headers }));
    const data = await response.json();

    if (!response.ok || data.success === false) {
        throw new Error(data.message || '요청 처리 중 오류가 발생했습니다.');
    }
    return data.data;
}

function renderListError(container, message) {
    container.innerHTML = '<li class="error-message">' + message + '</li>';
}

function renderCardError(container, message) {
    container.innerHTML = '<div class="card"><p class="error-message">' + message + '</p></div>';
}
