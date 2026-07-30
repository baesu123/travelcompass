const UI = {
    card: 'rounded-2xl bg-white p-6 shadow-card',
    btnPrimary: 'inline-flex items-center justify-center rounded-xl bg-primary-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-primary-700 disabled:opacity-50',
    btnOutline: 'inline-flex items-center justify-center rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm font-semibold text-slate-600 transition hover:border-primary-300 hover:text-primary-700',
    btnAccent: 'inline-flex items-center justify-center rounded-xl bg-emerald-500 px-4 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-emerald-600',
    btnDanger: 'inline-flex items-center justify-center rounded-lg border border-rose-200 px-3 py-1.5 text-xs font-semibold text-rose-500 transition hover:bg-rose-50 hover:border-rose-300',
    input: 'w-full rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm text-slate-800 shadow-sm outline-none transition focus:border-primary-400 focus:ring-2 focus:ring-primary-100',
    label: 'mb-1.5 block text-sm font-semibold text-slate-700',
};

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
    container.innerHTML = '<li class="rounded-xl bg-rose-50 px-4 py-3 text-sm text-rose-600">' + message + '</li>';
}

function renderCardError(container, message) {
    container.innerHTML = '<div class="' + UI.card + '"><p class="text-sm text-rose-600">' + message + '</p></div>';
}
