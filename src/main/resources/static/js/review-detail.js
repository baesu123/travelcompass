(function () {
    const app = document.getElementById('app');
    const reviewId = app.dataset.reviewId;

    function render(review) {
        const comments = (review.comments || []).map(c =>
            '<li>' +
            '<span>' + c.authorNickname + ': ' + c.content + '</span>' +
            '<button class="danger" data-comment-id="' + c.id + '">삭제</button>' +
            '</li>'
        ).join('') || '<li>등록된 댓글이 없습니다.</li>';

        app.innerHTML =
            '<div class="card">' +
            '<h2>' + review.countryCode + ' 여행 후기</h2>' +
            '<p><span class="rating-stars">' + '★'.repeat(review.rating) + '☆'.repeat(5 - review.rating) + '</span> - '
            + review.authorNickname + '</p>' +
            '<p>' + review.content + '</p>' +
            '<button class="danger" id="delete-review">후기 삭제</button>' +
            '</div>' +
            '<div class="card">' +
            '<h3>댓글</h3>' +
            '<ul class="plain-list" id="comment-list">' + comments + '</ul>' +
            '<form id="comment-form" class="search-form">' +
            '<input type="text" id="comment-content" placeholder="댓글을 입력하세요" required>' +
            '<button type="submit">등록</button>' +
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
