(function () {
    const list = document.getElementById('checklist');
    const addForm = document.getElementById('add-form');

    function loadChecklist() {
        apiFetch('/api/checklists')
            .then(items => {
                if (items.length === 0) {
                    list.innerHTML = '<li>등록된 체크리스트 항목이 없습니다.</li>';
                    return;
                }
                list.innerHTML = items.map(item =>
                    '<li>' +
                    '<label>' +
                    '<input type="checkbox" data-id="' + item.id + '" ' + (item.checked ? 'checked' : '') + '> ' +
                    item.itemName +
                    '</label>' +
                    '<button class="danger" data-delete-id="' + item.id + '">삭제</button>' +
                    '</li>'
                ).join('');

                list.querySelectorAll('input[type=checkbox]').forEach(checkbox => {
                    checkbox.addEventListener('change', () => {
                        apiFetch('/api/checklists/' + checkbox.dataset.id, {
                            method: 'PATCH',
                            body: JSON.stringify({ checked: checkbox.checked })
                        }).catch(err => alert(err.message));
                    });
                });

                list.querySelectorAll('button[data-delete-id]').forEach(btn => {
                    btn.addEventListener('click', () => {
                        apiFetch('/api/checklists/' + btn.dataset.deleteId, { method: 'DELETE' })
                            .then(loadChecklist)
                            .catch(err => alert(err.message));
                    });
                });
            })
            .catch(err => renderListError(list, err.message));
    }

    addForm.addEventListener('submit', event => {
        event.preventDefault();
        const input = document.getElementById('item-name');
        apiFetch('/api/checklists', {
            method: 'POST',
            body: JSON.stringify({ itemName: input.value })
        }).then(() => {
            input.value = '';
            loadChecklist();
        }).catch(err => alert(err.message));
    });

    loadChecklist();
})();
