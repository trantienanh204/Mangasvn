const serverHost = window.location.origin;
function escapeHistoryHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[character]);
}
    function logout() {

        localStorage.removeItem('token');
        $('#login-link').show();
        $('#logout-link').hide();
        $('#user-info').text('');
        loadHistory();
        console.log("Đăng xuất thành công, localHistory:", localStorage.getItem('localHistory'));
        Toastify({ text: "Đã đăng xuất!", duration: 3000, gravity: "top", position: "right", style: { background: "#28a745" } }).showToast();
        window.location.href = "/";
    }


    function loadHistory() {
        const historyList = $('#history-list');
        historyList.empty();
        const token = localStorage.getItem("token");
        const isLoggedIn = !!token;

        if (isLoggedIn) {
            $.ajax({
                url: `${serverHost}/api/history`,
                method: 'GET',
                headers: { "Authorization": "Bearer " + token },
                success: function(history) {
                    console.log("Lịch sử từ server cho token:", token, "Dữ liệu:", history);
                    if (Array.isArray(history) && history.length > 0) {
                        history.forEach(item => {
                            if (item.comicId) {
                                const imageUrl = escapeHistoryHtml(item.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                                const tenTruyen = escapeHistoryHtml(item.tenTruyen || 'Không có tiêu đề');
                                const comicId = Number(item.comicId);
                                const lastRead = escapeHistoryHtml(new Date(item.ngayTao).toLocaleString());
                                historyList.append(`
                                <article class="library-item">
                                    <img src="${imageUrl}" alt="${tenTruyen}" onerror="this.src='https://i.postimg.cc/zBZ7k81R/cass.jpg';">
                                    <div class="library-copy"><a class="library-title" href="/truyen/${comicId}">${tenTruyen}</a><span class="library-meta">Đọc gần đây · ${lastRead}</span></div>
                                    <button class="library-remove" aria-label="Xóa ${tenTruyen} khỏi lịch sử" title="Xóa khỏi lịch sử" onclick="deleteHistoryItem(${comicId}, true)">×</button>
                                </article>
                            `);
                            }
                        });
                    } else {
                        historyList.append('<p>Chưa có lịch sử đọc.</p>');
                    }
                },
                error: function(xhr, status, error) {
                    console.log('Lỗi tải lịch sử từ server:', { status, error, response: xhr.responseText });
                    if (xhr.status === 401 || xhr.status === 403) {
                        Toastify({ text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                        // localStorage.removeItem("token");
                        // window.location.href = "/api/auth/login";
                    } else {
                        historyList.append('<p>Lỗi tải lịch sử từ server. Vui lòng thử lại sau.</p>');
                    }
                }
            });
        } else {
            loadLocalHistory();
        }
    }

    function loadLocalHistory() {
        const historyList = $('#history-list');
        historyList.empty();
        const localHistory = JSON.parse(localStorage.getItem('localHistory') || '[]');

        console.log("Lịch sử local:", localHistory);
        if (localHistory.length > 0) {
            localHistory.forEach((item, index) => {
                if (item.comicId) {
                    const imageUrl = escapeHistoryHtml(item.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                    const tenTruyen = escapeHistoryHtml(item.tenTruyen || 'Không có tiêu đề');
                    const comicId = Number(item.comicId);
                    const lastRead = escapeHistoryHtml(new Date(item.lastRead).toLocaleString());
                    historyList.append(`
                    <article class="library-item">
                        <img src="${imageUrl}" alt="${tenTruyen}" onerror="this.src='https://i.postimg.cc/zBZ7k81R/cass.jpg';">
                        <div class="library-copy"><a class="library-title" href="/truyen/${comicId}">${tenTruyen}</a><span class="library-meta">Đọc gần đây · ${lastRead}</span></div>
                        <button class="library-remove" aria-label="Xóa ${tenTruyen} khỏi lịch sử" title="Xóa khỏi lịch sử" onclick="deleteHistoryItem(${index}, false)">×</button>
                    </article>
                `);
                }
            });
        } else {
            historyList.append('<p>Chưa có lịch sử đọc.</p>');
        }
    }

    window.loadComicFromHistory = function(comicId) {
    window.location.href = `/truyen/${comicId}`;
};

    window.deleteHistoryItem = function(id, isLoggedIn) {
        if (isLoggedIn) {
            const token = localStorage.getItem("token");
            $.ajax({
                url: `${serverHost}/api/history/${id}`,
                method: 'DELETE',
                headers: { "Authorization": "Bearer " + token },
                success: function() {
                    console.log(`Xóa lịch sử truyện ${id} trên server thành công`);
                    Toastify({ text: "Đã xóa truyện khỏi lịch sử!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                    loadHistory();
                },
                error: function(xhr, status, error) {
                    console.log('Lỗi xóa lịch sử trên server:', error);
                    console.log(`id = ${id}`);
                    if (xhr.status === 401 || xhr.status === 403) {
                        Toastify({ text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                        localStorage.removeItem("token");
                        window.location.href = "/login";
                    }
                }
            });
        } else {
            let localHistory = JSON.parse(localStorage.getItem('localHistory') || '[]');
            if (localHistory.length > id) {
                localHistory.splice(id, 1);
                localStorage.setItem('localHistory', JSON.stringify(localHistory));
                Toastify({ text: "Đã xóa truyện khỏi lịch sử!", duration: 3000, gravity: "top", position: "right", style: { background: "#28a745" } }).showToast();
                loadLocalHistory();
            }
        }
    };

    $(document).ready(function() {
        loadHistory();
        const token = localStorage.getItem("token");
        if (token) {
            $.ajax({
                url: `${serverHost}/api/auth/user-info`,
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                },
                success: function(data) {
                    $('#login-link').hide();
                    $('#logout-link').show();
                    $('#user-info').text("Xin chào, " + data.username);
                    console.log("Đăng nhập thành công với user:", data.username);
                },
                error: function(xhr) {
                    if (xhr.status === 401 || xhr.status === 403) {
                        localStorage.removeItem("token");
                        $('#login-link').show();
                        $('#logout-link').hide();
                        $('#user-info').text("");
                    }
                }
            });
        } else {
            $('#login-link').show();
            $('#logout-link').hide();
            $('#user-info').text("");
        }
    });


