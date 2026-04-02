$(document).ready(function () {
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.1.14:8080";


    const token = localStorage.getItem("token");
    const $carouselInner = $('#genre-select');
    const userInfo = $('#user-info');
    const loginLink = $('#login-link');
    const logoutLink = $('#logout-link');

    const pathParts = window.location.pathname.split('/');

    let currentGenre = pathParts[pathParts.length - 1] || $('#genre-select').val() || 'romcom';
    $('#genre-select').val(currentGenre);

    if (token) {
        $.ajax({
            url: `${serverHost}/theloai/fill/AllGenre`,
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
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
                if (xhr.status === 401 || xhr.status === 403) {
                    localStorage.removeItem("token");
                    if (userInfo.length) userInfo.text("");
                    if (loginLink.length) loginLink.show();
                    if (logoutLink.length) logoutLink.hide();
                }
            }
        });
    } else {
        if (userInfo.length) userInfo.text("");
        if (loginLink.length) loginLink.show();
        if (logoutLink.length) logoutLink.hide();
    }

    function loadComics(genre, status = '', sort = '') {
        let url = `${serverHost}/theloai/translate/${genre}`;
        if (status || sort) {
            url += `?status=${status}&sort=${sort}`;
        }
        $.ajax({
            url: url,
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
            success: function (data) {
                if (data && data.length > 0) {
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

                    displayComics(data);
                } else {
                    $('#theLoai').text("Không tìm thấy truyện");
                    $('#comic-list').html('<p>Không có truyện nào trong danh mục này.</p>');
                }
            },
            error: function (xhr, status, error) {
                $('#theLoai').text("Lỗi tải dữ liệu");
                $('#comic-list').html('<p>Lỗi khi tải truyện, vui lòng thử lại!</p>');
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

    loadComics(currentGenre);

    $('#genre-select, #status-select, #sort-select').on('change', function () {
        const genre = $('#genre-select').val();
        const status = $('#status-select').val();
        const sort = $('#sort-select').val();

        if (genre !== currentGenre) {
            window.location.href = `/theloai/${genre}`;
            currentGenre = genre;
        }
        loadComics(genre, status, sort);
    });
});