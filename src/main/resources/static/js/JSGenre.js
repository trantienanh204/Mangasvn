const serverHost = window.location.origin;
let currentGenre = '';
let currentSort = 'date-desc';
let currentStatus = '';
let currentPage = 1;

$(document).ready(function () {
    // 1. Phân tích thể loại từ URL pathname
    // URL có thể là /theloai, /theloai/, hoặc /theloai/{genre}
    const path = window.location.pathname;
    const parts = path.split('/').filter(p => p.length > 0);
    
    if (parts.length >= 2 && parts[0].toLowerCase() === 'theloai') {
        try {
            currentGenre = decodeURIComponent(parts[1]);
        } catch (e) {
            currentGenre = parts[1];
        }
    }

    // 2. Tải danh sách tất cả thể loại và hiển thị dạng Pills/Chips
    loadGenreChips();

    // 3. Xử lý sắp xếp và bộ lọc
    $('#sort-select').on('change', function () {
        currentSort = $(this).val();
        loadComics(currentGenre, currentStatus, currentSort, 1);
    });

    $('#status-select').on('change', function () {
        currentStatus = $(this).val();
        loadComics(currentGenre, currentStatus, currentSort, 1);
    });

    // 4. Click chuyển trang (Pagination)
    $(document).on('click', '.genre-page-btn', function (e) {
        e.preventDefault();
        if ($(this).hasClass('disabled') || $(this).hasClass('active')) return;
        const page = parseInt($(this).attr('data-page'));
        if (page && page > 0) {
            loadComics(currentGenre, currentStatus, currentSort, page);
            $('html, body').animate({
                scrollTop: $("#comic-grid").offset().top - 100
            }, 300);
        }
    });

    // 5. Click chọn thể loại từ chip
    $(document).on('click', '.genre-chip', function (e) {
        e.preventDefault();
        const selectedGenre = $(this).attr('data-genre');
        if (!selectedGenre || selectedGenre === currentGenre) return;

        currentGenre = selectedGenre;
        $('.genre-chip').removeClass('active');
        $(this).addClass('active');

        // Cập nhật URL trình duyệt không reload trang
        const newUrl = '/theloai/' + encodeURIComponent(selectedGenre);
        window.history.pushState({ genre: selectedGenre }, '', newUrl);

        loadComics(currentGenre, currentStatus, currentSort, 1);
    });

    // 6. Xử lý nút Back/Forward của trình duyệt
    window.addEventListener('popstate', function () {
        const p = window.location.pathname.split('/').filter(x => x.length > 0);
        if (p.length >= 2 && p[0].toLowerCase() === 'theloai') {
            currentGenre = decodeURIComponent(p[1]);
        } else {
            currentGenre = '';
        }
        highlightActiveChip(currentGenre);
        loadComics(currentGenre, currentStatus, currentSort, 1);
    });

    // 7. Khởi tạo trạng thái đăng nhập
    initAuthStatus();
});

function loadGenreChips() {
    $.ajax({
        url: `${serverHost}/theloai/fill/AllGenre`,
        method: "GET",
        success: function (data) {
            const $chipsList = $('#genre-chips-list');
            $chipsList.empty();

            if (Array.isArray(data) && data.length > 0) {
                // Nếu chưa có currentGenre, mặc định chọn thể loại đầu tiên
                if (!currentGenre) {
                    currentGenre = data[0].tenDanhMuc;
                }

                data.forEach(dm => {
                    const isActive = (dm.tenDanhMuc.toLowerCase() === currentGenre.toLowerCase());
                    const chip = $(`
                        <a href="/theloai/${encodeURIComponent(dm.tenDanhMuc)}" 
                           class="genre-chip ${isActive ? 'active' : ''}" 
                           data-genre="${dm.tenDanhMuc}">
                           ${dm.tenDanhMuc}
                        </a>
                    `);
                    $chipsList.append(chip);
                });

                // Tải truyện cho thể loại đã chọn
                loadComics(currentGenre, currentStatus, currentSort, 1);
            } else {
                $chipsList.html('<span class="text-muted small">Không tìm thấy thể loại nào.</span>');
            }
        },
        error: function (xhr) {
            console.error("Lỗi tải danh sách thể loại:", xhr);
            $('#genre-chips-list').html('<span class="text-danger small">Lỗi khi tải danh sách thể loại.</span>');
        }
    });
}

function highlightActiveChip(genreName) {
    if (!genreName) return;
    $('.genre-chip').each(function () {
        const chipGenre = $(this).attr('data-genre');
        if (chipGenre && chipGenre.toLowerCase() === genreName.toLowerCase()) {
            $(this).addClass('active');
        } else {
            $(this).removeClass('active');
        }
    });
}

function loadComics(genre, status = '', sort = 'date-desc', page = 1) {
    currentPage = page;
    let url = `${serverHost}/theloai/translate/${encodeURIComponent(genre)}?page=${page}`;
    if (sort) url += `&sort=${sort}`;
    if (status) url += `&status=${status}`;

    // Hiển thị loading skeleton / text
    $('#comic-grid').html(`
        <div class="col-12 text-center py-5">
            <div class="spinner-border text-primary" role="status"></div>
            <p class="text-muted small mt-2">Đang tải danh sách truyện...</p>
        </div>
    `);

    $.ajax({
        url: url,
        method: "GET",
        success: function (data) {
            // Cập nhật thông tin tiêu đề và mô tả thể loại
            if (data.genreInfo) {
                const genreName = data.genreInfo.tenDanhMuc;
                $('#theLoai').text(genreName);
                $('#breadcrumb-genre').text(genreName);
                $('#summary_genre').text(data.genreInfo.noiDung || "Thể loại tổng hợp truyện tranh chất lượng tại MangaSVN.");
                highlightActiveChip(genreName);
            } else {
                $('#theLoai').text(genre);
                $('#breadcrumb-genre').text(genre);
            }

            const total = data.totalElements !== undefined ? data.totalElements : (data.content ? data.content.length : 0);
            $('#comic-count-display').html(`<i class="fa-solid fa-book-open me-1"></i> Có <strong>${total}</strong> truyện`);

            if (data && data.content && data.content.length > 0) {
                displayComics(data.content);
                renderPagination(data.currentPage, data.totalPages);
            } else {
                $('#comic-grid').html(`
                    <div class="col-12 text-center py-5 w-100" style="grid-column: 1 / -1;">
                        <i class="fa-solid fa-folder-open text-muted mb-3" style="font-size: 3.5rem;"></i>
                        <h5 class="text-light fw-bold">Chưa có truyện nào trong thể loại này</h5>
                        <p class="text-muted small">Hãy chọn một thể loại khác hoặc quay lại trang chủ.</p>
                        <a href="/" class="btn btn-outline-primary btn-sm mt-2"><i class="fa-solid fa-house me-1"></i>Về trang chủ</a>
                    </div>
                `);
                $('#pagination-container').empty();
            }
        },
        error: function (xhr) {
            console.error("Lỗi khi tải truyện:", xhr);
            $('#theLoai').text("Không tìm thấy thể loại");
            $('#comic-grid').html(`
                <div class="col-12 text-center py-5 w-100" style="grid-column: 1 / -1;">
                    <i class="fa-solid fa-triangle-exclamation text-danger mb-3" style="font-size: 3.5rem;"></i>
                    <h5 class="text-light">Lỗi khi tải danh sách truyện</h5>
                    <p class="text-muted small">Vui lòng thử lại sau giây lát.</p>
                </div>
            `);
            $('#pagination-container').empty();
            if (typeof Toastify !== 'undefined') {
                Toastify({
                    text: "Không thể tải truyện của thể loại này!",
                    duration: 3000,
                    gravity: "top",
                    position: "right",
                    style: { background: "#dc3545" }
                }).showToast();
            }
        }
    });
}

function displayComics(comics) {
    const $grid = $('#comic-grid');
    $grid.empty();

    comics.forEach(comic => {
        const tenTruyen = comic.tenTruyen || 'Không có tiêu đề';
        const moTa = comic.moTa || 'Chưa có mô tả chi tiết cho truyện này.';
        const imgSrc = comic.imageComic || '/img/placeholder.jpg';
        const views = (comic.luotXem || 0).toLocaleString();
        const likes = (comic.luotThich || 0).toLocaleString();

        const card = `
            <div class="comic-poster-card">
                <div class="comic-thumb-wrapper">
                    <a href="/truyen/${comic.id}">
                        <img class="comic-thumb" src="${imgSrc}" alt="${tenTruyen}" loading="lazy" onerror="this.src='https://i.postimg.cc/zBZ7k81R/cass.jpg'">
                    </a>
                    <div class="comic-badge-top">HOT</div>
                    <div class="comic-badge-bottom">
                        <span><i class="fa-solid fa-eye me-1"></i>${views}</span>
                        <span><i class="fa-solid fa-heart me-1"></i>${likes}</span>
                    </div>
                </div>
                <div class="comic-card-info">
                    <a href="/truyen/${comic.id}" class="comic-card-title" title="${tenTruyen}">${tenTruyen}</a>
                    <div class="comic-card-desc">${moTa}</div>
                </div>
            </div>
        `;
        $grid.append(card);
    });
}

function renderPagination(currentPage, totalPages) {
    const $container = $('#pagination-container');
    $container.empty();

    if (!totalPages || totalPages <= 1) return;

    let html = '';

    // Nút Trang trước
    html += `<a href="#" class="genre-page-btn ${currentPage === 1 ? 'disabled' : ''}" data-page="${currentPage - 1}">
        <i class="fa-solid fa-chevron-left"></i>
    </a>`;

    const startPage = Math.max(1, currentPage - 2);
    const endPage = Math.min(totalPages, currentPage + 2);

    if (startPage > 1) {
        html += `<a href="#" class="genre-page-btn" data-page="1">1</a>`;
        if (startPage > 2) {
            html += `<span class="genre-page-btn disabled" style="border:none;background:transparent;">...</span>`;
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        html += `<a href="#" class="genre-page-btn ${currentPage === i ? 'active' : ''}" data-page="${i}">${i}</a>`;
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            html += `<span class="genre-page-btn disabled" style="border:none;background:transparent;">...</span>`;
        }
        html += `<a href="#" class="genre-page-btn" data-page="${totalPages}">${totalPages}</a>`;
    }

    // Nút Trang sau
    html += `<a href="#" class="genre-page-btn ${currentPage === totalPages ? 'disabled' : ''}" data-page="${currentPage + 1}">
        <i class="fa-solid fa-chevron-right"></i>
    </a>`;

    $container.html(html);
}

function initAuthStatus() {
    const token = localStorage.getItem("token");
    if (token) {
        $.ajax({
            url: `${serverHost}/api/auth/user-info`,
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function (data) {
                $('#login-link').hide();
                $('#logout-link').show();
                $('#user-info').text("Xin chào, " + (data.username || "Thành viên"));
                if (data.role === "ADMIN" || data.role === "CHUTUT") {
                    $('#nav-admin-link').show();
                }
            },
            error: function (xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    localStorage.removeItem("token");
                    $('#login-link').show();
                    $('#logout-link').hide();
                    $('#user-info').text("");
                    $('#nav-admin-link').hide();
                }
            }
        });
    } else {
        $('#login-link').show();
        $('#logout-link').hide();
        $('#user-info').text("");
        $('#nav-admin-link').hide();
    }
}

function logout() {
    localStorage.removeItem('token');
    $('#login-link').show();
    $('#logout-link').hide();
    $('#user-info').text('');
    if (typeof Toastify !== 'undefined') {
        Toastify({
            text: "Đã đăng xuất!",
            duration: 3000,
            gravity: "top",
            position: "right",
            style: { background: "#28a745" }
        }).showToast();
    }
    setTimeout(() => {
        window.location.href = "/";
    }, 500);
}
