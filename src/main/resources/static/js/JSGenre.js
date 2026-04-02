const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.1.14:8080";

$(document).ready(function () {

    const token = localStorage.getItem("token");
    const $carouselInner = $('#genre-select');
    const pathParts = window.location.pathname.split('/');

    let currentGenre = pathParts[pathParts.length - 1] || $('#genre-select').val() || 'romcom';
    $('#genre-select').val(currentGenre);


    $.ajax({
        url: `${serverHost}/theloai/fill/AllGenre`,
        method: "GET",
        success: function(data) {
            if ($carouselInner.length) $carouselInner.empty();
            if (Array.isArray(data)) {
                data.forEach((truyen) => {
                    $carouselInner.append(
                        `<option value="${truyen.tenDanhMuc}">${truyen.tenDanhMuc}</option>`
                    );
                });
                $carouselInner.val(decodeURIComponent(currentGenre));
            } else {
                console.warn("Expected array from server, got:", data);
            }
        },
        error: function(xhr) {
            console.error("Lỗi tải danh sách thể loại", xhr);
        }
    });

    function loadComics(genre, status = '', sort = '', page = 1) {
        let url = `${serverHost}/theloai/translate/${genre}?page=${page}`;
        if (status) url += `&status=${status}`;
        if (sort) url += `&sort=${sort}`;

        $.ajax({
            url: url,
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
            success: function (data) {
                if (data && data.content && data.content.length > 0) {
                    $.ajax({
                        url : `${serverHost}/theloai/search/${genre}`,
                        method: "GET",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        success : function (dataInfo){
                            if(dataInfo && dataInfo.noiDung){
                                $('#summary_genre').text(dataInfo.noiDung);
                                $('#theLoai').text(dataInfo.tenDanhMuc.toUpperCase());
                            }else {
                                $('#theLoai').text("Không tìm thấy truyện");
                                $('#summary_genre').html('<p>lỗi không có danh mục</p>');
                            }
                        }
                    });

                    displayComics(data.content);
                    renderPagination(data.currentPage, data.totalPages);
                } else {
                    $('#theLoai').text("Không tìm thấy truyện");
                    $('#comic-list').html('<p>Không có truyện nào trong danh mục này.</p>');
                    $('#pagination-container').empty();
                }
            },
            error: function (xhr, status, error) {
                $('#theLoai').text("Lỗi tải dữ liệu");
                $('#comic-list').html('<p>Lỗi khi tải truyện, vui lòng thử lại!</p>');
                $('#pagination-container').empty();
                if (typeof Toastify !== 'undefined') {
                    Toastify({
                        text: "Lỗi khi tải truyện, vui lòng thử lại! Chi tiết: " + error,
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
        const comicList = $('#comic-list');
        comicList.empty();
        if (!comicList.length) {
            return;
        }
        comics.forEach(comic => {
            const tenTruyen = comic.tenTruyen || 'Không có tiêu đề';
            const moTa = comic.moTa || 'Không có mô tả';

            const comicCard = `
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <a href="/truyen/${comic.id}">
                            <img src="${comic.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                 alt="${tenTruyen}" 
                                 style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                        </a>
                        <div class="card-body">
                            <h5 class="card-title">${tenTruyen.substring(0, 40)}${tenTruyen.length > 40 ? '...' : ''}</h5>
                            <p class="card-text">${moTa.substring(0, 100)}${moTa.length > 60 ? '...' : ''}</p>
                            <p>Lượt xem: ${comic.luotXem || 0}</p>
                            <p>Lượt thích: ${comic.luotThich || 0}</p>
                        </div>
                    </div>
                </div>
            `;
            comicList.append(comicCard);
        });
    }

    function renderPagination(currentPage, totalPages) {
        if (totalPages <= 1) {
            $('#pagination-container').empty();
            return;
        }
        let paginationHtml = '<nav aria-label="Page navigation" class="w-100 d-flex justify-content-center"><ul class="pagination mb-0" style="gap: 5px;">';
        paginationHtml += `<li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link shadow-none" href="#" data-page="${currentPage - 1}">Trước</a>
        </li>`;

        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            paginationHtml += `<li class="page-item"><a class="page-link shadow-none" href="#" data-page="1">1</a></li>`;
            if (startPage > 2) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link shadow-none">...</span></li>`;
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `<li class="page-item ${currentPage === i ? 'active' : ''}">
                <a class="page-link shadow-none" href="#" data-page="${i}">${i}</a>
            </li>`;
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link shadow-none">...</span></li>`;
            }
            paginationHtml += `<li class="page-item"><a class="page-link shadow-none" href="#" data-page="${totalPages}">${totalPages}</a></li>`;
        }

        paginationHtml += `<li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link shadow-none" href="#" data-page="${currentPage + 1}">Sau</a>
        </li>`;

        paginationHtml += '</ul></nav>';

        if ($('#pagination-container').length === 0) {
            $('#comic-list').after(`
                <div id="pagination-container" 
                     class="w-100 d-flex justify-content-center py-3 mt-4" 
                     style="position: sticky; bottom: 0; background-color: #212529; z-index: 999; box-shadow: 0 -5px 10px rgba(0,0,0,0.2);">
                </div>
            `);
        }
        $('#pagination-container').html(paginationHtml);
    }

    $(document).on('click', '.page-link', function(e) {
        e.preventDefault();

        if ($(this).parent().hasClass('disabled') || $(this).parent().hasClass('active')) {
            return;
        }

        const selectedPage = parseInt($(this).attr('data-page'));
        const status = $('#status-select').val();
        const sort = $('#sort-select').val();
        loadComics(currentGenre, status, sort, selectedPage);

        $('html, body').animate({
            scrollTop: $("#comic-list").offset().top - 50
        }, 300);
    });

    loadComics(currentGenre);

    $('#genre-select, #status-select, #sort-select').on('change', function () {
        const genre = $('#genre-select').val();
        const status = $('#status-select').val();
        const sort = $('#sort-select').val();

        if (genre !== currentGenre) {
            window.location.href = `/theloai/${genre}`;
            currentGenre = genre;
        }

        loadComics(genre, status, sort, 1);
    });
});

function logout() {
    localStorage.removeItem('token');
    $('#login-link').show();
    $('#logout-link').hide();
    $('#user-info').text('');
    if (typeof loadHistory === 'function') loadHistory();
    console.log("Đăng xuất thành công, localHistory:", localStorage.getItem('localHistory'));
    Toastify({ text: "Đã đăng xuất!", duration: 3000, gravity: "top", position: "right", style: { background: "#28a745" } }).showToast();
    window.location.href = "/view/trangchu.html";
}

$(document).ready(function() {
    if (typeof loadHistory === 'function') loadHistory();

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