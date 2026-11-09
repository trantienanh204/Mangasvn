const serverHost = window.location.origin;

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    })[c]);
}

let authorsMap = {};
let categoriesMap = {};
let currentUserRole = "";

window.logout = function(e) {
    if (e && e.preventDefault) e.preventDefault();
    localStorage.removeItem('token');
    if (typeof Toastify !== 'undefined') {
        Toastify({ text: "Đã đăng xuất thành công!", duration: 1500, style: { background: "#28a745" } }).showToast();
    }
    setTimeout(() => {
        window.location.replace('/login');
    }, 200);
};

$(document).ready(function() {
    // Khởi tạo Select2
    $('#author-select').select2({
        placeholder: "Chọn tác giả",
        allowClear: true,
        width: '100%',
        tags: true,
        createTag: function(params) {
            var term = $.trim(params.term);
            if (term === '') return null;
            return { id: term, text: term, newOption: true };
        }
    });

    $('#category-select').select2({
        placeholder: "Chọn danh mục",
        allowClear: true,
        width: '100%'
    });

    function checkAdmin() {
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.replace('/login');
            return Promise.resolve(false);
        }

        return $.ajax({
            url: `${serverHost}/api/auth/user-info`,
            method: "GET",
            headers: { "Authorization": "Bearer " + token },
            success: function(data) {
                currentUserRole = data.role;
                $('#user-info').text("Xin chào, " + (data.username || "Admin"));
                $('#logout-link').show();
                $('#login-link').hide();
                if (data.role === "ADMIN") {
                    $('#role-badge').text("QUẢN TRỊ VIÊN (ADMIN)").removeClass('bg-secondary bg-warning').addClass('bg-danger');
                    $('.admin-only').show();
                } else if (data.role === "CHUTUT") {
                    $('#role-badge').text("CHỦ THỚT (CHUTUT)").removeClass('bg-secondary bg-danger').addClass('bg-warning text-dark');
                    $('.admin-only').hide();
                } else {
                    window.location.replace('/');
                }
            },
            error: function(xhr) {
                localStorage.removeItem('token');
                window.location.replace('/login');
            }
        }).then(function(data) {
            return (data.role === "ADMIN" || data.role === "CHUTUT");
        }, function() {
            return false;
        });
    }

    // ==========================================
    // 1. DASHBOARD STATS
    // ==========================================
    window.loadDashboardStats = function() {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/stats`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(stats) {
                $('#stat-comics').text(stats.totalComics.toLocaleString());
                $('#stat-chapters').text(stats.totalChapters.toLocaleString());
                $('#stat-users').text(stats.totalUsers.toLocaleString());
                $('#stat-views').text(stats.totalViews.toLocaleString());
                $('#stat-comments').text(stats.totalComments.toLocaleString());
                $('#stat-reports').text(stats.totalReports.toLocaleString());
            },
            error: function(err) {
                console.log("Lỗi tải thống kê:", err);
            }
        });
    };

    // ==========================================
    // 2. QUẢN LÝ TRUYỆN (COMICS LIST & ACTIONS)
    // ==========================================
    window.loadAdminComics = function(page = 0) {
        const token = localStorage.getItem('token');
        const keyword = $('#comic-search-input').val().trim();
        const trangThai = $('#comic-status-filter').val();
        const categoryId = $('#comic-category-filter').val();
        const authorId = $('#comic-author-filter').val();

        let url = `${serverHost}/api/admin/comics?page=${page}&size=10`;
        if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
        if (trangThai !== '') url += `&trangThai=${trangThai}`;
        if (categoryId) url += `&categoryId=${categoryId}`;
        if (authorId) url += `&authorId=${authorId}`;

        if (currentUserRole === "CHUTUT") {
            $('#comics-table-title').html('<i class="fa-solid fa-book me-1"></i> Danh Sách Truyện Của Bạn');
        } else {
            $('#comics-table-title').html('<i class="fa-solid fa-list me-1"></i> Danh Sách Toàn Bộ Truyện Hệ Thống');
        }

        const tbody = $('#admin-comics-tbody');
        tbody.html('<tr><td colspan="9" class="text-center py-4"><div class="spinner-border text-primary" role="status"></div></td></tr>');

        $.ajax({
            url: url,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(response) {
                tbody.empty();
                $('#comics-count-badge').text(`Tổng: ${response.totalElements || 0} truyện`);
                const comics = response.content || [];
                if (comics.length === 0) {
                    tbody.html('<tr><td colspan="9" class="text-center py-4 text-muted"><i class="fa-solid fa-magnifying-glass me-1"></i> Không tìm thấy truyện nào phù hợp với bộ lọc.</td></tr>');
                    $('#comic-pagination').empty();
                    return;
                }

                comics.forEach(c => {
                    let statusBadge = '';
                    if (c.trangThai === 1) {
                        statusBadge = '<span class="badge bg-success"><i class="fa-solid fa-eye me-1"></i>Hiển thị</span>';
                    } else if (c.trangThai === 0) {
                        statusBadge = '<span class="badge bg-secondary"><i class="fa-solid fa-eye-slash me-1"></i>Đang ẩn</span>';
                    } else if (c.trangThai === -1) {
                        statusBadge = '<span class="badge bg-dark"><i class="fa-solid fa-trash me-1"></i>Đã xóa mềm</span>';
                    } else {
                        statusBadge = `<span class="badge bg-info">${c.trangThai}</span>`;
                    }

                    const coverImg = c.imageComic ? `<img src="${c.imageComic}" alt="cover" style="width: 45px; height: 60px; object-fit: cover; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.2);">` : '<span class="text-muted small">Không có</span>';

                    // Categories badges
                    let catBadges = (c.categories && c.categories.length > 0)
                        ? c.categories.map(cat => `<span class="badge bg-dark text-info border border-info border-opacity-25 me-1" style="font-size: 0.72rem;">${cat}</span>`).join('')
                        : '<span class="text-muted small">Chưa có</span>';

                    // Authors list
                    let authorNames = (c.authors && c.authors.length > 0)
                        ? c.authors.join(', ')
                        : 'Chưa rõ';

                    // Translator badge
                    let translatorBadge = `<span class="badge bg-dark text-light border border-secondary"><i class="fa-solid fa-user-pen me-1 text-info"></i>${c.translatorUsername || 'Chưa rõ'}</span>`;

                    let actionsHtml = '';
                    if (c.canEdit) {
                        actionsHtml += `
                            <div class="d-inline-flex align-items-center gap-1 text-nowrap">
                                <button class="btn btn-sm btn-outline-primary" onclick="editComicInForm(${c.id})" title="Chỉnh sửa / Thêm chapter">
                                    <i class="fa-solid fa-pen-to-square"></i> Sửa
                                </button>
                                <button class="btn btn-sm ${c.trangThai === 1 ? 'btn-outline-warning' : 'btn-outline-success'}" onclick="toggleComicStatus(${c.id})" title="Ẩn hoặc hiện">
                                    <i class="fa-solid ${c.trangThai === 1 ? 'fa-eye-slash' : 'fa-eye'}"></i> ${c.trangThai === 1 ? 'Ẩn' : 'Hiện'}
                                </button>
                                <button class="btn btn-sm btn-outline-danger" onclick="softDeleteComic(${c.id})" title="Xóa mềm">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                            </div>
                        `;
                    } else {
                        actionsHtml = '<span class="text-muted small italic">Không có quyền can thiệp</span>';
                    }

                    const row = `
                        <tr>
                            <td><strong class="text-light">#${c.id}</strong></td>
                            <td>${coverImg}</td>
                            <td>
                                <a href="/truyen/${c.id}" target="_blank" class="text-decoration-none fw-bold text-light d-block mb-1 hover-cyan" style="font-size: 0.95rem;">${c.tenTruyen}</a>
                            </td>
                            <td>
                                <div class="small fw-semibold text-info mb-1"><i class="fa-solid fa-pen-nib me-1"></i>${authorNames}</div>
                                <div>${catBadges}</div>
                            </td>
                            <td>${translatorBadge}</td>
                            <td>
                                <div class="small text-light"><strong>${c.chapterCount}</strong> chap</div>
                                <div class="small text-muted"><i class="fa-solid fa-eye me-1"></i>${c.luotXem.toLocaleString()} | <i class="fa-solid fa-heart text-danger me-1"></i>${c.luotThich.toLocaleString()}</div>
                            </td>
                            <td>${statusBadge}</td>
                            <td class="small text-muted">${c.ngayTao || ''}</td>
                            <td class="text-center">${actionsHtml}</td>
                        </tr>
                    `;
                    tbody.append(row);
                });

                renderPagination('#comic-pagination', response, 'loadAdminComics');
            },
            error: function(xhr) {
                tbody.html(`<tr><td colspan="9" class="text-center text-danger py-4">Lỗi tải dữ liệu: ${xhr.responseText || 'Không xác định'}</td></tr>`);
            }
        });
    };

    window.resetComicFilters = function() {
        $('#comic-search-input').val('');
        $('#comic-category-filter').val('');
        $('#comic-author-filter').val('');
        $('#comic-status-filter').val('');
        loadAdminComics(0);
    };

    // Lắng nghe sự kiện gõ Enter và thay đổi bộ lọc
    $('#comic-search-input').on('keypress', function(e) {
        if (e.which === 13) {
            e.preventDefault();
            loadAdminComics(0);
        }
    });

    $('#comic-category-filter, #comic-author-filter, #comic-status-filter').on('change', function() {
        loadAdminComics(0);
    });

    window.toggleComicStatus = function(comicId) {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/comics/${comicId}/toggle-status`,
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function() {
                Toastify({ text: "Đã thay đổi trạng thái truyện!", duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminComics();
                loadComics();
            },
            error: function(xhr) {
                let err = "Không thể đổi trạng thái!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: "Lỗi: " + err, duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.softDeleteComic = function(comicId) {
        if (!confirm("Bạn có chắc chắn muốn xóa mềm truyện này? Truyện sẽ bị ẩn khỏi toàn bộ trang web!")) return;
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/comics/${comicId}`,
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function() {
                Toastify({ text: "Đã xóa mềm truyện thành công!", duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminComics();
                loadComics();
            },
            error: function(xhr) {
                let err = "Không thể xóa truyện!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: "Lỗi: " + err, duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.editComicInForm = function(comicId) {
        // Chuyển sang tab Đăng / Cập nhật
        const triggerEl = document.querySelector('#tab-upload-btn');
        bootstrap.Tab.getInstance(triggerEl) ? bootstrap.Tab.getInstance(triggerEl).show() : new bootstrap.Tab(triggerEl).show();
        $('#comic-select').val(comicId).trigger('change');
    };

    // ==========================================
    // 3. QUẢN LÝ THÀNH VIÊN (ADMIN ONLY)
    // ==========================================
    window.loadAdminUsers = function(page = 0) {
        if (currentUserRole !== "ADMIN") return;
        const token = localStorage.getItem('token');
        const keyword = $('#user-search-input').val().trim();
        const roleId = $('#user-role-filter').val();
        const trangThai = $('#user-status-filter').val();

        let url = `${serverHost}/api/admin/users?page=${page}&size=10`;
        if (keyword) url += `&keyword=${encodeURIComponent(keyword)}`;
        if (roleId) url += `&roleId=${roleId}`;
        if (trangThai !== '') url += `&trangThai=${trangThai}`;

        const tbody = $('#admin-users-tbody');
        tbody.html('<tr><td colspan="7" class="text-center py-4"><div class="spinner-border text-primary" role="status"></div></td></tr>');

        $.ajax({
            url: url,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(response) {
                tbody.empty();
                const users = response.content || [];
                if (users.length === 0) {
                    tbody.html('<tr><td colspan="7" class="text-center py-4 text-muted">Không tìm thấy thành viên nào.</td></tr>');
                    $('#user-pagination').empty();
                    return;
                }

                users.forEach(u => {
                    let roleBadgeClass = 'badge-role-docgia';
                    if (u.roleName === 'ADMIN') roleBadgeClass = 'badge-role-admin';
                    else if (u.roleName === 'CHUTUT') roleBadgeClass = 'badge-role-chutut';

                    const roleBadge = `<span class="badge ${roleBadgeClass}">${u.roleName || 'CHƯA CÓ'}</span>`;
                    const statusBadge = u.trangThai
                        ? '<span class="badge bg-success">Hoạt động</span>'
                        : '<span class="badge bg-danger">Đã bị khóa</span>';

                    const banBtn = u.trangThai
                        ? `<button class="btn btn-sm btn-outline-danger me-1" onclick="toggleUserBan(${u.id}, '${u.username}')" title="Khóa tài khoản"><i class="fa-solid fa-ban"></i> Khóa</button>`
                        : `<button class="btn btn-sm btn-outline-success me-1" onclick="toggleUserBan(${u.id}, '${u.username}')" title="Mở khóa tài khoản"><i class="fa-solid fa-unlock"></i> Mở</button>`;

                    const roleBtn = `<button class="btn btn-sm btn-outline-primary" onclick="openRoleModal(${u.id}, '${u.username}', ${u.roleId})"><i class="fa-solid fa-user-shield"></i> Quyền</button>`;

                    const row = `
                        <tr>
                            <td>#${u.id}</td>
                            <td><strong>${u.username}</strong></td>
                            <td>${u.email || '<span class="text-muted">Chưa có</span>'}</td>
                            <td>${roleBadge}</td>
                            <td>${statusBadge}</td>
                            <td>${u.ngayTao || ''}</td>
                            <td class="text-center">${banBtn} ${roleBtn}</td>
                        </tr>
                    `;
                    tbody.append(row);
                });

                renderPagination('#user-pagination', response, 'loadAdminUsers');
            },
            error: function(xhr) {
                tbody.html(`<tr><td colspan="7" class="text-center text-danger py-4">Lỗi: ${xhr.responseText || 'Không xác định'}</td></tr>`);
            }
        });
    };

    window.toggleUserBan = function(userId, username) {
        if (!confirm(`Bạn có chắc chắn muốn thay đổi trạng thái khóa/mở cho người dùng "${username}"?`)) return;
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/users/${userId}/toggle-ban`,
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(updated) {
                const msg = updated.trangThai ? "Đã mở khóa tài khoản!" : "Đã khóa tài khoản thành công!";
                Toastify({ text: msg, duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminUsers();
            },
            error: function(xhr) {
                let err = "Không thể thực hiện!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: "Lỗi: " + err, duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.openRoleModal = function(userId, username, currentRoleId) {
        $('#modal-user-id').val(userId);
        $('#modal-username').text(username);
        $('#modal-role-select').val(currentRoleId || 3);
        const modal = new bootstrap.Modal(document.getElementById('roleModal'));
        modal.show();
    };

    window.submitChangeRole = function() {
        const userId = $('#modal-user-id').val();
        const roleId = parseInt($('#modal-role-select').val());
        const token = localStorage.getItem('token');

        $.ajax({
            url: `${serverHost}/api/admin/users/${userId}/role`,
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            data: JSON.stringify({ roleId: roleId }),
            success: function() {
                Toastify({ text: "Đã cập nhật vai trò thành công!", duration: 2500, style: { background: "#28a745" } }).showToast();
                bootstrap.Modal.getInstance(document.getElementById('roleModal')).hide();
                loadAdminUsers();
            },
            error: function(xhr) {
                let err = "Không thể cập nhật!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: "Lỗi: " + err, duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    // ==========================================
    // 4. QUẢN LÝ BÁO CÁO LỖI (ADMIN ONLY)
    // ==========================================
    window.loadAdminReports = function(page = 0) {
        if (currentUserRole !== "ADMIN") return;
        const token = localStorage.getItem('token');
        const tbody = $('#admin-reports-tbody');
        tbody.html('<tr><td colspan="7" class="text-center py-4"><div class="spinner-border text-primary" role="status"></div></td></tr>');

        $.ajax({
            url: `${serverHost}/api/admin/reports?page=${page}&size=10`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(response) {
                tbody.empty();
                const reports = response.content || [];
                if (reports.length === 0) {
                    tbody.html('<tr><td colspan="7" class="text-center py-4 text-muted">Không có báo cáo lỗi nào.</td></tr>');
                    $('#report-pagination').empty();
                    return;
                }

                reports.forEach(r => {
                    let statusBadge = '<span class="badge bg-warning text-dark">Chưa xử lý</span>';
                    if (r.trangThai === 'DA_XU_LY') statusBadge = '<span class="badge bg-success">Đã xử lý</span>';
                    else if (r.trangThai === 'DA_HUY') statusBadge = '<span class="badge bg-secondary">Đã hủy</span>';

                    const actions = `
                        <button class="btn btn-sm btn-outline-success me-1" onclick="updateReportStatus(${r.id}, 'DA_XU_LY')" title="Đánh dấu đã sửa xong"><i class="fa-solid fa-check"></i> Xong</button>
                        <button class="btn btn-sm btn-outline-secondary me-1" onclick="updateReportStatus(${r.id}, 'DA_HUY')" title="Hủy báo cáo"><i class="fa-solid fa-xmark"></i> Hủy</button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteReportAction(${r.id})" title="Xóa báo cáo"><i class="fa-solid fa-trash"></i></button>
                    `;

                    const row = `
                        <tr>
                            <td>#${r.id}</td>
                            <td>${r.comicTitle ? `<a href="/truyen/${r.comicId}" target="_blank" class="fw-bold">${r.comicTitle}</a>` : 'Không xác định'}</td>
                            <td>${r.username || '<span class="text-muted">Ẩn danh</span>'}</td>
                            <td class="text-break" style="max-width: 300px;">${r.lyDo || ''}</td>
                            <td>${r.ngayTao || ''}</td>
                            <td>${statusBadge}</td>
                            <td class="text-center">${actions}</td>
                        </tr>
                    `;
                    tbody.append(row);
                });

                renderPagination('#report-pagination', response, 'loadAdminReports');
            },
            error: function(xhr) {
                tbody.html(`<tr><td colspan="7" class="text-center text-danger py-4">Lỗi: ${xhr.responseText || 'Không xác định'}</td></tr>`);
            }
        });
    };

    window.updateReportStatus = function(reportId, status) {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/reports/${reportId}/status`,
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            data: JSON.stringify({ status: status }),
            success: function() {
                Toastify({ text: "Đã cập nhật trạng thái báo cáo!", duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminReports();
                loadDashboardStats();
            },
            error: function(xhr) {
                Toastify({ text: "Lỗi cập nhật: " + (xhr.responseText || ''), duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.deleteReportAction = function(reportId) {
        if (!confirm("Bạn có chắc muốn xóa báo cáo này?")) return;
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/reports/${reportId}`,
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function() {
                Toastify({ text: "Đã xóa báo cáo!", duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminReports();
                loadDashboardStats();
            },
            error: function(xhr) {
                Toastify({ text: "Lỗi xóa báo cáo: " + (xhr.responseText || ''), duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    // ==========================================
    // 5. XÓA BÌNH LUẬN (ADMIN ONLY)
    // ==========================================
    window.deleteCommentAction = function() {
        const commentId = $('#delete-comment-id').val().trim();
        if (!commentId) {
            Toastify({ text: "Vui lòng nhập ID bình luận cần xóa!", duration: 3000, style: { background: "#dc3545" } }).showToast();
            return;
        }

        if (!confirm(`Bạn có chắc chắn muốn xóa bình luận ID #${commentId}?`)) return;

        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/comments/${commentId}`,
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function() {
                Toastify({ text: `Đã xóa bình luận #${commentId} thành công!`, duration: 3000, style: { background: "#28a745" } }).showToast();
                $('#delete-comment-id').val('');
                loadDashboardStats();
            },
            error: function(xhr) {
                Toastify({ text: "Lỗi: " + (xhr.responseText || "Không tìm thấy hoặc không thể xóa"), duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    // ==========================================
    // 6. QUẢN LÝ THỂ LOẠI (CATEGORIES - ADMIN ONLY)
    // ==========================================
    let adminCategoriesList = [];

    window.loadAdminCategories = function() {
        if (currentUserRole !== "ADMIN") return;
        const token = localStorage.getItem('token');
        const tbody = $('#admin-categories-tbody');
        tbody.html('<tr><td colspan="5" class="text-center py-4"><div class="spinner-border text-primary" role="status"></div></td></tr>');

        $.ajax({
            url: `${serverHost}/api/admin/categories`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(categories) {
                adminCategoriesList = categories || [];
                renderAdminCategoriesTable(adminCategoriesList);
            },
            error: function(xhr) {
                tbody.html(`<tr><td colspan="5" class="text-center text-danger py-4">Lỗi: ${xhr.responseText || 'Không thể tải danh sách thể loại'}</td></tr>`);
            }
        });
    };

    function renderAdminCategoriesTable(categories) {
        const tbody = $('#admin-categories-tbody');
        tbody.empty();
        $('#categories-count-badge').text(`Tổng: ${categories.length} thể loại`);

        if (categories.length === 0) {
            tbody.html('<tr><td colspan="5" class="text-center py-4 text-muted"><i class="fa-solid fa-tags me-1"></i> Không có thể loại nào phù hợp.</td></tr>');
            return;
        }

        categories.forEach(cat => {
            const isAct = (cat.trangThai !== false);
            const statusBadge = isAct
                ? '<span class="badge bg-success"><i class="fa-solid fa-check me-1"></i>Đang hiển thị</span>'
                : '<span class="badge bg-secondary"><i class="fa-solid fa-ban me-1"></i>Đang ẩn</span>';

            const toggleBtnText = isAct ? 'Ẩn' : 'Hiện';
            const toggleBtnClass = isAct ? 'btn-outline-warning' : 'btn-outline-success';
            const toggleIcon = isAct ? 'fa-eye-slash' : 'fa-eye';

            const row = `
                <tr>
                    <td><strong>#${cat.id}</strong></td>
                    <td>
                        <span class="fw-bold text-primary">${escapeHtml(cat.tenDanhMuc)}</span>
                    </td>
                    <td>
                        <span class="text-muted small">${escapeHtml(cat.noiDung || 'Chưa có mô tả')}</span>
                    </td>
                    <td>${statusBadge}</td>
                    <td class="text-center">
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="openCategoryModal(${cat.id})" title="Chỉnh sửa">
                            <i class="fa-solid fa-pen-to-square"></i> Sửa
                        </button>
                        <button class="btn btn-sm ${toggleBtnClass} me-1" onclick="toggleCategoryStatusAction(${cat.id})" title="${toggleBtnText} thể loại">
                            <i class="fa-solid ${toggleIcon}"></i> ${toggleBtnText}
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="deleteCategoryAction(${cat.id}, '${escapeHtml(cat.tenDanhMuc)}')" title="Xóa thể loại">
                            <i class="fa-solid fa-trash"></i>
                        </button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    }

    // Lọc thể loại theo từ khóa tìm kiếm
    $('#category-search-input').on('input', function() {
        const kw = $(this).val().toLowerCase().trim();
        if (!kw) {
            renderAdminCategoriesTable(adminCategoriesList);
            return;
        }
        const filtered = adminCategoriesList.filter(c => 
            (c.tenDanhMuc && c.tenDanhMuc.toLowerCase().includes(kw)) ||
            (c.noiDung && c.noiDung.toLowerCase().includes(kw))
        );
        renderAdminCategoriesTable(filtered);
    });

    window.openCategoryModal = function(catId = null) {
        if (catId) {
            const cat = adminCategoriesList.find(c => c.id === catId);
            if (cat) {
                $('#categoryModalTitle').text('Chỉnh Sửa Thể Loại');
                $('#modal-cat-id').val(cat.id);
                $('#modal-cat-name').val(cat.tenDanhMuc);
                $('#modal-cat-desc').val(cat.noiDung || '');
            }
        } else {
            $('#categoryModalTitle').text('Thêm Thể Loại Mới');
            $('#modal-cat-id').val('');
            $('#modal-cat-name').val('');
            $('#modal-cat-desc').val('');
        }
        const modal = new bootstrap.Modal(document.getElementById('categoryModal'));
        modal.show();
    };

    window.submitSaveCategory = function() {
        const catId = $('#modal-cat-id').val();
        const tenDanhMuc = $('#modal-cat-name').val().trim();
        const noiDung = $('#modal-cat-desc').val().trim();

        if (!tenDanhMuc) {
            Toastify({ text: "Vui lòng nhập tên thể loại!", duration: 3000, style: { background: "#dc3545" } }).showToast();
            return;
        }

        const token = localStorage.getItem('token');
        const isUpdate = Boolean(catId);
        const url = isUpdate ? `${serverHost}/api/admin/categories/${catId}` : `${serverHost}/api/admin/categories`;
        const method = isUpdate ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            method: method,
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            data: JSON.stringify({ tenDanhMuc, noiDung }),
            success: function() {
                Toastify({ text: isUpdate ? "Cập nhật thể loại thành công!" : "Tạo thể loại mới thành công!", duration: 2500, style: { background: "#28a745" } }).showToast();
                const modalEl = document.getElementById('categoryModal');
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
                loadAdminCategories();
                loadCategories();
            },
            error: function(xhr) {
                let err = "Không thể lưu thể loại!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: "Lỗi: " + err, duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.toggleCategoryStatusAction = function(catId) {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/categories/${catId}/toggle-status`,
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function() {
                Toastify({ text: "Đã thay đổi trạng thái thể loại!", duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminCategories();
                loadCategories();
            },
            error: function(xhr) {
                Toastify({ text: "Lỗi: " + (xhr.responseText || 'Không thể đổi trạng thái'), duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.deleteCategoryAction = function(catId, catName) {
        if (!confirm(`Bạn có chắc chắn muốn xóa thể loại "${catName}"?`)) return;
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/categories/${catId}`,
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function() {
                Toastify({ text: "Đã xóa thể loại thành công!", duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminCategories();
                loadCategories();
            },
            error: function(xhr) {
                let err = "Không thể xóa thể loại!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: "Lỗi: " + err, duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    // ==========================================
    // QUẢN LÝ TÁC GIẢ (AUTHORS MANAGEMENT)
    // ==========================================
    let adminAuthorsList = [];

    window.loadAdminAuthors = function() {
        const token = localStorage.getItem('token');
        const tbody = $('#admin-authors-tbody');
        tbody.html('<tr><td colspan="5" class="text-center py-4"><div class="spinner-border text-primary" role="status"></div></td></tr>');

        $.ajax({
            url: `${serverHost}/api/admin/authors`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(authors) {
                adminAuthorsList = authors || [];
                renderAdminAuthorsTable(adminAuthorsList);
            },
            error: function(xhr) {
                tbody.html(`<tr><td colspan="5" class="text-center text-danger py-4">Lỗi: ${xhr.responseText || 'Không thể tải danh sách tác giả'}</td></tr>`);
            }
        });
    };

    function renderAdminAuthorsTable(authors) {
        const tbody = $('#admin-authors-tbody');
        tbody.empty();
        $('#authors-count-badge').text(`Tổng: ${authors.length} tác giả`);

        if (authors.length === 0) {
            tbody.html('<tr><td colspan="5" class="text-center py-4 text-muted"><i class="fa-solid fa-feather-pointed me-1"></i> Không có tác giả nào phù hợp.</td></tr>');
            return;
        }

        authors.forEach(author => {
            const isAct = (author.trangThai !== false);
            const statusBadge = isAct
                ? '<span class="badge bg-success"><i class="fa-solid fa-check me-1"></i>Đang hoạt động</span>'
                : '<span class="badge bg-secondary"><i class="fa-solid fa-ban me-1"></i>Ngừng hoạt động</span>';

            const toggleBtnText = isAct ? 'Ẩn' : 'Hiện';
            const toggleBtnClass = isAct ? 'btn-outline-warning' : 'btn-outline-success';
            const toggleIcon = isAct ? 'fa-eye-slash' : 'fa-eye';

            const actions = (currentUserRole === "ADMIN") ? `
                <button class="btn btn-sm btn-outline-primary me-1" onclick="openAuthorModal(${author.id})" title="Chỉnh sửa">
                    <i class="fa-solid fa-pen-to-square"></i> Sửa
                </button>
                <button class="btn btn-sm ${toggleBtnClass} me-1" onclick="toggleAuthorStatusAction(${author.id})" title="${toggleBtnText} tác giả">
                    <i class="fa-solid ${toggleIcon}"></i> ${toggleBtnText}
                </button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteAuthorAction(${author.id}, '${escapeHtml(author.tenTacGia)}')" title="Xóa tác giả">
                    <i class="fa-solid fa-trash"></i>
                </button>
            ` : `
                <button class="btn btn-sm btn-outline-secondary" disabled title="Chỉ Admin mới có quyền sửa/xóa tác giả">
                    <i class="fa-solid fa-lock"></i>
                </button>
            `;

            const row = `
                <tr>
                    <td><strong>#${author.id}</strong></td>
                    <td>
                        <span class="fw-bold text-primary">${escapeHtml(author.tenTacGia)}</span>
                    </td>
                    <td>
                        <span class="text-muted small">${author.ngayTao || '---'}</span>
                    </td>
                    <td>${statusBadge}</td>
                    <td class="text-center">
                        ${actions}
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    }

    // Lọc tác giả theo từ khóa
    $('#author-search-input').on('input', function() {
        const kw = $(this).val().toLowerCase().trim();
        if (!kw) {
            renderAdminAuthorsTable(adminAuthorsList);
            return;
        }
        const filtered = adminAuthorsList.filter(a => 
            a.tenTacGia && a.tenTacGia.toLowerCase().includes(kw)
        );
        renderAdminAuthorsTable(filtered);
    });

    window.openAuthorModal = function(authorId = null) {
        if (authorId) {
            const author = adminAuthorsList.find(a => a.id === authorId);
            if (author) {
                $('#authorModalTitle').text('Chỉnh Sửa Tác Giả');
                $('#modal-author-id').val(author.id);
                $('#modal-author-name').val(author.tenTacGia);
                $('#modal-author-status').prop('checked', author.trangThai !== false);
            }
        } else {
            $('#authorModalTitle').text('Thêm Tác Giả Mới');
            $('#modal-author-id').val('');
            $('#modal-author-name').val('');
            $('#modal-author-status').prop('checked', true);
        }
        const modal = new bootstrap.Modal(document.getElementById('authorModal'));
        modal.show();
    };

    window.submitSaveAuthor = function() {
        const authorId = $('#modal-author-id').val();
        const tenTacGia = $('#modal-author-name').val().trim();
        const trangThai = $('#modal-author-status').is(':checked');

        if (!tenTacGia) {
            Toastify({ text: "Vui lòng nhập tên tác giả!", duration: 3000, style: { background: "#dc3545" } }).showToast();
            return;
        }

        const token = localStorage.getItem('token');
        const isUpdate = Boolean(authorId);
        const url = isUpdate ? `${serverHost}/api/admin/authors/${authorId}` : `${serverHost}/api/admin/authors`;
        const method = isUpdate ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            method: method,
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            data: JSON.stringify({ tenTacGia, trangThai }),
            success: function() {
                Toastify({ text: isUpdate ? "Cập nhật tác giả thành công!" : "Tạo tác giả mới thành công!", duration: 2500, style: { background: "#28a745" } }).showToast();
                const modalEl = document.getElementById('authorModal');
                const modal = bootstrap.Modal.getInstance(modalEl);
                if (modal) modal.hide();
                loadAdminAuthors();
                loadAuthors(); // Refresh dropdown in upload form
            },
            error: function(xhr) {
                let err = "Không thể lưu tác giả!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: "Lỗi: " + err, duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.toggleAuthorStatusAction = function(authorId) {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/authors/${authorId}/toggle-status`,
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function() {
                Toastify({ text: "Đã thay đổi trạng thái tác giả!", duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminAuthors();
                loadAuthors();
            },
            error: function(xhr) {
                Toastify({ text: "Lỗi: " + (xhr.responseText || 'Không thể đổi trạng thái'), duration: 3000, style: { background: "#dc3545" } }).showToast();
            }
        });
    };

    window.deleteAuthorAction = function(authorId, authorName) {
        if (!confirm(`Bạn có chắc chắn muốn xóa tác giả "${authorName}"?`)) return;
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/admin/authors/${authorId}`,
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(res) {
                const msg = (res && res.message) ? res.message : "Đã xóa tác giả thành công!";
                Toastify({ text: msg, duration: 2500, style: { background: "#28a745" } }).showToast();
                loadAdminAuthors();
                loadAuthors();
            },
            error: function(xhr) {
                let err = "Không thể xóa tác giả!";
                try {
                    const parsed = JSON.parse(xhr.responseText);
                    if (parsed.message) err = parsed.message;
                } catch(e) { err = xhr.responseText || err; }
                Toastify({ text: err, duration: 3500, style: { background: "#ffc107", color: "#000" } }).showToast();
                loadAdminAuthors();
            }
        });
    };

    // Helper: Render Pagination UI
    function renderPagination(containerId, pageData, callbackFnName) {
        const container = $(containerId);
        container.empty();
        if (!pageData || pageData.totalPages <= 1) return;

        const currentPage = pageData.number;
        const totalPages = pageData.totalPages;

        let html = `<span class="small text-muted">Trang ${currentPage + 1} / ${totalPages} (Tổng ${pageData.totalElements} mục)</span>`;
        html += '<ul class="pagination pagination-sm mb-0">';

        if (currentPage > 0) {
            html += `<li class="page-item"><button class="page-link" onclick="${callbackFnName}(${currentPage - 1})">Trước</button></li>`;
        }

        for (let i = 0; i < totalPages; i++) {
            if (i === currentPage) {
                html += `<li class="page-item active"><span class="page-link">${i + 1}</span></li>`;
            } else if (i === 0 || i === totalPages - 1 || Math.abs(i - currentPage) <= 2) {
                html += `<li class="page-item"><button class="page-link" onclick="${callbackFnName}(${i})">${i + 1}</button></li>`;
            }
        }

        if (currentPage < totalPages - 1) {
            html += `<li class="page-item"><button class="page-link" onclick="${callbackFnName}(${currentPage + 1})">Sau</button></li>`;
        }
        html += '</ul>';

        container.html(html);
    }

    // ==========================================
    // 6. GIỮ NGUYÊN CODE TẢI DỮ LIỆU CŨ CHO FORM
    // ==========================================
    function loadComics() {
        const token = localStorage.getItem('token');
        if (!token) return;

        $.ajax({
            url: `${serverHost}/api/truyen/listloc`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(comics) {
                const comicSelect = $('#comic-select');
                comicSelect.empty();
                comicSelect.append('<option value="0">Tạo truyện mới</option>');
                comics.forEach(comic => comicSelect.append(`<option value="${comic.id}">${comic.tenTruyen}</option>`));
                const comicList = $('#comic-list');
                comicList.empty();
                comics.forEach(comic => {
                    const categories = comic.categoryIds ? `Danh mục: ${comic.categoryIds.map(id => categoriesMap[id] || id).join(', ')}` : 'Chưa có danh mục';
                    const authors = comic.authorIds ? `Tác giả: ${comic.authorIds.map(id => authorsMap[id] || id).join(', ')}` : 'Chưa có tác giả';
                    comicList.append(`<li class="list-group-item d-flex justify-content-between align-items-center"><span>${comic.tenTruyen} <small class="text-muted">(${categories} | ${authors})</small></span><button class="btn btn-sm btn-outline-primary" onclick="editComicInForm(${comic.id})">Sửa</button></li>`);
                });
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách truyện:', error);
            }
        });
    }

    function loadAuthors() {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/truyen/authors`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(authors) {
                const authorSelect = $('#author-select');
                const authorFilter = $('#comic-author-filter');
                authorSelect.empty();
                authorFilter.empty().append('<option value="">Tất cả tác giả</option>');
                authors.forEach(author => {
                    authorsMap[author.id] = author.tenTacGia;
                    authorSelect.append(`<option value="${author.id}">${author.tenTacGia}</option>`);
                    authorFilter.append(`<option value="${author.id}">${author.tenTacGia}</option>`);
                });
                authorSelect.trigger('change');
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách tác giả:', error);
            }
        });
    }

    function loadCategories() {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/categories`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(categories) {
                const categorySelect = $('#category-select');
                const catFilter = $('#comic-category-filter');
                categorySelect.empty();
                catFilter.empty().append('<option value="">Tất cả thể loại (Danh mục)</option>');
                categories.forEach(category => {
                    categoriesMap[category.id] = category.tenDanhMuc;
                    categorySelect.append(`<option value="${category.id}">${category.tenDanhMuc}</option>`);
                    catFilter.append(`<option value="${category.id}">${category.tenDanhMuc}</option>`);
                });
                categorySelect.trigger('change');
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách danh mục:', error);
            }
        });
    }

    $('#author-select').on('select2:select', function(e) {
        const data = e.params.data;
        if (data.newOption) {
            const newAuthorName = data.text;
            const token = localStorage.getItem('token');
            $.ajax({
                url: `${serverHost}/api/truyen/authors`,
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                data: JSON.stringify({ tenTacGia: newAuthorName }),
                success: function(response) {
                    authorsMap[response.id] = newAuthorName;
                    $('#author-select').append(`<option value="${response.id}" selected>${newAuthorName}</option>`).trigger('change');
                    Toastify({ text: 'Thêm tác giả mới thành công!', duration: 3000, style: { background: '#28a745' } }).showToast();
                },
                error: function(xhr, status, error) {
                    Toastify({ text: 'Lỗi thêm tác giả: ' + (xhr.responseText || error), duration: 3000, style: { background: '#dc3545' } }).showToast();
                    $('#author-select').val(null).trigger('change');
                }
            });
        }
    });

    function loadComicDetails(comicId) {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/truyen/${comicId}`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(comic) {
                $('#comic-title').val(comic.tenTruyen || '');
                $('#comic-description').val(comic.moTa || '');
                $('#comic-note').val(comic.ghiChu || '');
                $('#author-select').val(comic.authorIds[0] || null).trigger('change');
                $('#category-select').val(comic.categoryIds || []).trigger('change');
            },
            error: function(xhr, status, error) {
                Toastify({ text: 'Lỗi tải thông tin truyện: ' + (xhr.responseText || error), duration: 3000, style: { background: '#dc3545' } }).showToast();
            }
        });
    }

    window.addChapter = function() {
        const chapterItem = `<div class="chapter-item"><input type="text" class="form-control chapter-name" placeholder="Tên Chapter" required><input type="file" class="form-control chapter-zip" accept=".zip" required><button type="button" class="btn btn-danger btn-remove-chapter ms-2" onclick="removeChapter(this)">Xóa</button><div class="invalid-feedback">Vui lòng upload file ZIP với tên ảnh theo số thứ tự (ví dụ: 1.jpg, 2.png).</div></div>`;
        $('#chapter-list').append(chapterItem);
    };

    window.removeChapter = function(button) {
        const chapterItem = $(button).closest('.chapter-item');
        if ($('#chapter-list .chapter-item').length > 1) chapterItem.remove();
        else Toastify({ text: 'Phải có ít nhất một chapter!', duration: 3000, style: { background: '#dc3545' } }).showToast();
    };

        function validateZip(zip) {
        return new Promise((resolve, reject) => {
            let validImagesCount = 0;
            for (const [relativePath, file] of Object.entries(zip.files)) {
                if (file.dir) continue;
                if (relativePath.includes('__MACOSX') || relativePath.endsWith('.DS_Store') || relativePath.endsWith('Thumbs.db')) continue;

                const fileName = relativePath.split('/').pop().split('\\').pop();
                if (fileName.startsWith('._') || !fileName.trim()) continue;

                const lower = fileName.toLowerCase();
                if (lower.endsWith('.jpg') || lower.endsWith('.jpeg') || lower.endsWith('.png') || lower.endsWith('.webp') || lower.endsWith('.gif')) {
                    validImagesCount++;
                }
            }

            if (validImagesCount === 0) {
                reject(new Error('File ZIP không chứa file ảnh hợp lệ nào (.jpg, .jpeg, .png, .webp, .gif)!'));
                return;
            }

            resolve(validImagesCount);
        });
    }

    $(document).on('change', '.chapter-zip', function() {
        $(this).closest('.chapter-item').find('.invalid-feedback').hide().text('');
    });

    $('#upload-form').on('submit', async function(e) {
        e.preventDefault();

        const submitButton = $(this).find('button[type="submit"]');
        if (submitButton.prop('disabled')) return;

        const comicId = $('#comic-select').val();
        const isUpdate = comicId !== '0';
        let url = `${serverHost}/api/truyen`;
        let method = 'POST';

        if (isUpdate) {
            url = `${serverHost}/api/truyen/${comicId}`;
            method = 'PUT';
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').hide().find('input, textarea').prop('required', false);
            $('#create-btn').hide();
            $('#update-btn').show();
            $('#form-header-title').text('Cập nhật Truyện và Chapter');
        } else {
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').show().find('input, textarea').prop('required', true);
        }

        if (isUpdate) {
            const jsonData = {
                moTa: $('#comic-description').val() || null,
                ghiChu: $('#comic-note').val() || null
            };

            $.ajax({
                url: `${serverHost}/api/truyen/${comicId}`,
                type: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify(jsonData),
                headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` },
                success: function() {
                    console.log('Cập nhật thông tin truyện thành công');
                },
                error: function(xhr, status, error) {
                    Toastify({ text: 'Lỗi cập nhật truyện: ' + (xhr.responseText || error), duration: 3000, style: { background: '#dc3545' } }).showToast();
                }
            });
        }

        const formData = new FormData();
        if (!isUpdate) {
            const titleVal = $('#comic-title').val().trim();
            const descVal = $('#comic-description').val().trim();
            const noteVal = $('#comic-note').val().trim();
            const coverInput = $('#comic-cover')[0];
            const coverFile = coverInput && coverInput.files ? coverInput.files[0] : null;

            formData.append('title', titleVal);
            formData.append('tenTruyen', titleVal);
            formData.append('description', descVal);
            formData.append('moTa', descVal);
            formData.append('note', noteVal);
            formData.append('ghiChu', noteVal);
            if (coverFile) {
                formData.append('cover', coverFile);
                formData.append('image', coverFile);
            }

            const authorIds = $('#author-select').val();
            if (!authorIds || (Array.isArray(authorIds) && authorIds.length === 0)) {
                Toastify({ text: 'Vui lòng chọn tác giả cho truyện!', duration: 3000, style: { background: '#dc3545' } }).showToast();
                return;
            }
            if (Array.isArray(authorIds)) {
                authorIds.forEach(id => formData.append('authorIds', id));
            } else {
                formData.append('authorIds', authorIds);
            }

            const categoryIds = $('#category-select').val();
            if (!categoryIds || categoryIds.length === 0) {
                $('#category-select').next('.invalid-feedback').show();
                Toastify({ text: 'Vui lòng chọn ít nhất một thể loại!', duration: 3000, style: { background: '#dc3545' } }).showToast();
                return;
            }
            categoryIds.forEach(id => formData.append('categoryIds', id));
        }

        const chapterPromises = [];
        let isValid = true;
        const validationErrors = [];
        const chapters = [];

        $('#chapter-list .chapter-item').each(function(index) {
            const item = $(this);
            const chapterName = item.find('.chapter-name').val().trim();
            const zipInput = item.find('.chapter-zip')[0];
            const zipFile = zipInput ? zipInput.files[0] : null;

            item.find('.invalid-feedback').hide().text('');

            if (!chapterName) {
                item.find('.invalid-feedback').text('Vui lòng nhập tên Chapter!').show();
                isValid = false;
                validationErrors.push('Chapter ' + (index + 1) + ' chưa có tên');
                return;
            }

            if (!zipFile) {
                item.find('.invalid-feedback').text('Vui lòng chọn file ZIP cho Chapter!').show();
                isValid = false;
                validationErrors.push('Chapter "' + chapterName + '" chưa chọn file ZIP');
                return;
            }

            const promise = JSZip.loadAsync(zipFile)
                .then(function(zip) {
                    return validateZip(zip).then(imgCount => ({ chapterName, zipFile, imgCount, valid: true }));
                })
                .catch(error => {
                    item.find('.invalid-feedback').text(error.message).show();
                    validationErrors.push(chapterName + ': ' + error.message);
                    return { chapterName, zipFile, valid: false };
                });

            chapterPromises.push(promise);
        });

        submitButton.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2" role="status"></span> Đang kiểm tra file...');

        const chapterResults = await Promise.all(chapterPromises);

        for (const result of chapterResults) {
            if (!result.valid) {
                isValid = false;
                continue;
            }
            formData.append('chapterZip', result.zipFile);
            formData.append('chapterName', result.chapterName);
            chapters.push({ name: result.chapterName, zip: result.zipFile });
        }

        if (!isValid) {
            const defaultBtnText = isUpdate ? '<i class="fa-solid fa-floppy-disk me-1"></i> Cập nhật Chapter' : '<i class="fa-solid fa-upload me-1"></i> Đăng Truyện';
            submitButton.prop('disabled', false).html(defaultBtnText);
            Toastify({
                text: validationErrors.length ? validationErrors[0] : 'Vui lòng kiểm tra lại file ZIP của các chapter!',
                duration: 4000,
                style: { background: '#dc3545' }
            }).showToast();
            return;
        }

        // Bắt đầu quá trình tải lên thực sự
        const progressBar = $('#upload-progress .progress-bar');
        const statusText = $('#upload-status-text');
        $('#upload-progress').show();
        statusText.show().html('<i class="fa-solid fa-spinner fa-spin me-1"></i> Đang chuẩn bị truyền dữ liệu lên máy chủ...');

        submitButton.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2" role="status"></span> Đang tải lên...');

        progressBar.removeClass('bg-danger bg-success').addClass('bg-primary progress-bar-striped progress-bar-animated')
            .css('width', '5%').attr('aria-valuenow', 5).text('5%');

        const token = localStorage.getItem('token');
        $.ajax({
            url: isUpdate ? `${serverHost}/api/truyen/${comicId}/chapters` : url,
            type: isUpdate ? 'PUT' : method,
            data: formData,
            contentType: false,
            processData: false,
            timeout: 300000, // 5 phút
            headers: { 'Authorization': `Bearer ${token}` },
            xhr: function() {
                const xhr = new window.XMLHttpRequest();
                if (xhr.upload) {
                    xhr.upload.addEventListener("progress", function(evt) {
                        if (evt.lengthComputable) {
                            const percent = Math.min(90, Math.round((evt.loaded / evt.total) * 90));
                            progressBar.css('width', percent + '%').attr('aria-valuenow', percent).text(percent + '%');
                            const mbLoaded = (evt.loaded / (1024 * 1024)).toFixed(1);
                            const mbTotal = (evt.total / (1024 * 1024)).toFixed(1);
                            if (percent < 90) {
                                statusText.html(`<i class="fa-solid fa-arrow-up-from-bracket me-1 text-primary"></i> Đang tải file lên máy chủ: <strong>${mbLoaded} MB / ${mbTotal} MB (${percent}%)</strong>...`);
                            } else {
                                statusText.html('<i class="fa-solid fa-cloud-arrow-up text-primary me-1"></i> Đã tải xong file lên máy chủ! Đang giải nén & đồng bộ ảnh lên Cloudflare R2, vui lòng đợi...');
                            }
                        }
                    }, false);
                }
                return xhr;
            },
            success: function(response) {
                progressBar.removeClass('bg-primary').addClass('bg-success').css('width', '100%').attr('aria-valuenow', 100).text('100% - Hoàn tất!');
                statusText.html('<span class="text-success fw-bold"><i class="fa-solid fa-circle-check me-1"></i> Thành công! Truyện và các chapter đã được lưu trữ an toàn.</span>');
                Toastify({ text: isUpdate ? 'Cập nhật chapter thành công!' : 'Đăng truyện thành công!', duration: 3500, style: { background: '#28a745' } }).showToast();

                $('#upload-form')[0].reset();
                $('#chapter-list').empty().append(`<div class="chapter-item"><input type="text" class="form-control chapter-name" placeholder="Tên Chapter" required><input type="file" class="form-control chapter-zip" accept=".zip" required><button type="button" class="btn btn-danger btn-remove-chapter ms-2" onclick="removeChapter(this)">Xóa</button><div class="invalid-feedback"></div></div>`);
                $('#author-select').val(null).trigger('change');
                $('#category-select').val(null).trigger('change');
                loadComics();
                loadAdminComics();
                loadDashboardStats();
                setTimeout(() => {
                    $('#upload-progress').fadeOut();
                    statusText.fadeOut();
                }, 3500);
            },
            error: function(xhr, status, error) {
                progressBar.removeClass('bg-primary').addClass('bg-danger').text('Lỗi tải lên!');
                let errorMessage = isUpdate ? 'Cập nhật chapter thất bại' : 'Đăng truyện thất bại';
                if (status === 'timeout') errorMessage += ': Quá thời gian chờ (5 phút). File của bạn có thể quá lớn hoặc mạng yếu.';
                else if (xhr.responseText && xhr.responseText.includes('MaxUploadSizeExceededException')) errorMessage += ': Kích thước file ZIP vượt quá giới hạn tải lên.';
                else errorMessage += ': ' + (xhr.responseText || error);

                statusText.html(`<span class="text-danger fw-bold"><i class="fa-solid fa-circle-exclamation me-1"></i> ${errorMessage}</span>`);
                Toastify({ text: errorMessage, duration: 5000, style: { background: '#dc3545' } }).showToast();
            },
            complete: function() {
                const defaultBtnText = isUpdate ? '<i class="fa-solid fa-floppy-disk me-1"></i> Cập nhật Chapter' : '<i class="fa-solid fa-upload me-1"></i> Đăng Truyện';
                submitButton.prop('disabled', false).html(defaultBtnText);
            }
        });
    });
    $('#comic-select').on('change', function() {
        const comicId = $(this).val();
        if (comicId === '0') {
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').show().find('input, textarea').prop('required', true);
            $('#create-btn').show();
            $('#update-btn').hide();
            $('#form-header-title').text('Đăng Truyện Mới');
            $('#author-select').prop('required', true);
            $('#category-select').prop('required', true);
        } else {
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').hide().find('input, textarea').prop('required', false);
            $('#create-btn').hide();
            $('#update-btn').show();
            $('#form-header-title').text('Cập nhật Truyện và Chapter');
            loadComicDetails(comicId);
            $('#author-select').prop('required', true);
            $('#category-select').prop('required', true);
        }
    });

    // Khởi chạy khi load trang
    checkAdmin().then(function(isAdmin) {
        if (!isAdmin) return;
        loadAuthors();
        loadCategories();
        loadDashboardStats();
        loadAdminComics();
        loadComics();

        if (currentUserRole === "ADMIN") {
            loadAdminCategories();
            loadAdminAuthors();
            loadAdminUsers();
            loadAdminReports();
        }
    });

    $('#tab-categories-btn').on('click', function() {
        loadAdminCategories();
    });

    $('#tab-authors-btn').on('click', function() {
        loadAdminAuthors();
    });
});
