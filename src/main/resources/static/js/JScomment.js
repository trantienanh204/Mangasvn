
const currentComicId = 1; // Thay bằng logic lấy ID thực tế của bạn
const currentUserId = 1; // Thay bằng ID người dùng đang đăng nhập

document.addEventListener('DOMContentLoaded', function() {
    loadComments();
});

function loadComments() {
    fetch(`/api/comments/comic/${currentComicId}`)
        .then(response => response.json())
        .then(data => {
            const commentListDiv = document.getElementById('comment-list');
            commentListDiv.innerHTML = '';

            if(data.length === 0) {
                commentListDiv.innerHTML = '<p class="text-muted">Chưa có bình luận nào. Hãy là người đầu tiên!</p>';
                return;
            }

            data.forEach(comment => {
                const userName = comment.users ? comment.users.name : 'Ẩn danh';
                const date = comment.ngayTao;

                const commentHtml = `
                    <div class="comment-item mb-2 border-bottom pb-2">
                        <strong>${userName}</strong> <small class="text-muted">(${date})</small>
                        <p class="mb-0">${comment.comment}</p>
                    </div>
                `;
                commentListDiv.insertAdjacentHTML('beforeend', commentHtml);
            });
        })
        .catch(error => console.error('Lỗi khi tải bình luận:', error));
}


function submitComment() {
    const inputField = document.getElementById('comment-input');
    const content = inputField.value.trim();

    if (!content) {
        alert("Vui lòng nhập nội dung bình luận!");
        return;
    }

    const payload = {
        userId: currentUserId,
        comicId: currentComicId,
        chapterId: null,
        content: content
    };

    fetch('/api/comments/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => {
            if(response.ok) {
                inputField.value = '';
                loadComments();
            } else {
                alert('Có lỗi xảy ra khi gửi bình luận.');
            }
        })
        .catch(error => console.error('Lỗi khi gửi bình luận:', error));
}