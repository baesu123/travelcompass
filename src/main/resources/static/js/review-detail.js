(function () {
    const app = document.getElementById('app');
    const reviewId = app.dataset.reviewId;

    function render(review) {
        const comments = (review.comments || []).map(c =>
            '<li class="flex items-start justify-between gap-3 rounded-xl border border-slate-100 bg-white px-4 py-3">' +
            '<div class="text-sm text-slate-700">' +
            '<p class="font-medium text-slate-800">' + c.authorNickname + '</p>' +
            '<p class="mt-1 text-slate-600">' + c.content + '</p>' +
            '</div>' +
            '<button class="' + UI.btnDanger + '" data-comment-id="' + c.id + '">삭제</button>' +
            '</li>'
        ).join('') || '<li class="rounded-xl bg-slate-50 px-4 py-6 text-center text-sm text-slate-400">등록된 댓글이 없습니다.</li>';

        app.innerHTML =
            '<div class="' + UI.card + ' sm:p-8">' +
            '<h2 class="text-xl font-bold text-slate-900">' + review.countryName + ' 여행 후기</h2>' +
            '<p class="mt-2 text-sm text-slate-500"><span class="text-amber-500">' + '★'.repeat(review.rating) + '☆'.repeat(5 - review.rating) + '</span> - '
            + review.authorNickname + '</p>' +
            '<p class="mt-4 text-sm leading-relaxed text-slate-700">' + review.content + '</p>' +
            '<button class="mt-6 ' + UI.btnDanger + '" id="delete-review">후기 삭제</button>' +
            '</div>' +
            '<div class="' + UI.card + ' mt-6 sm:p-8">' +
            '<h3 class="text-base font-semibold text-slate-800">댓글</h3>' +
            '<ul class="mt-4 space-y-2" id="comment-list">' + comments + '</ul>' +
            '<form id="comment-form" class="mt-4 flex gap-3">' +
            '<input type="text" id="comment-content" placeholder="댓글을 입력하세요" required class="' + UI.input + '">' +
            '<button type="submit" class="inline-flex shrink-0 items-center justify-center rounded-xl bg-primary-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-primary-700">등록</button>' +
            '</form>' +
            '</div>';

        document.getElementById('delete-review').addEventListener('click', () => {
            apiFetch('/api/reviews/' + reviewId, { method: 'DELETE' })
                .then(() => location.href = '/reviews')
                .catch(err => alert(err.message));
        });

        document.querySelectorAll('button[data-comment-id]').forEach(btn => {
            btn.addEventListener('click', () => {
                apiFetch('/api/reviews/' + reviewId + '/comments/' + btn.dataset.commentId, { method: 'DELETE' })
                    .then(load)
                    .catch(err => alert(err.message));
            });
        });

        document.getElementById('comment-form').addEventListener('submit', event => {
            event.preventDefault();
            const input = document.getElementById('comment-content');
            apiFetch('/api/reviews/' + reviewId + '/comments', {
                method: 'POST',
                body: JSON.stringify({ content: input.value })
            }).then(() => {
                input.value = '';
                load();
            }).catch(err => alert(err.message));
        });
    }

    function load() {
        apiFetch('/api/reviews/' + reviewId)
            .then(render)
            .catch(err => renderCardError(app, err.message));
    }

    load();
})();
