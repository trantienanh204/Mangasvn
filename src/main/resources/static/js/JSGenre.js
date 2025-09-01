$(document).ready(function () {
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.238.147:8080";

    // Lấy genre từ URL
    const pathParts = window.location.pathname.split('/');
    let currentGenre = pathParts[pathParts.length - 1] || $('#genre-select').val() || 'manga'; // Mặc định là 'manga'

    // Cập nhật select box
    $('#genre-select').val(currentGenre);

    function loadComics(genre, status = '', sort = '') {
        let url = `${serverHost}/theloai/${genre}`;
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
                    $('#theLoai').text(genre.charAt(0).toUpperCase() + genre.slice(1));
                    displayComics(data);
                } else {
                    $('#theLoai').text("Không tìm thấy truyện");
                    $('#comic-list').html('<p>Không có truyện nào trong danh mục này.</p>');
                }
            },
            error: function (xhr, status, error) {
                console.log("Lỗi khi lấy dữ liệu:", error);
                $('#theLoai').text("Lỗi tải dữ liệu");
                $('#comic-list').html('<p>Lỗi khi tải truyện, vui lòng thử lại!</p>');
                Toastify({
                    text: "Lỗi khi tải truyện, vui lòng thử lại!",
                    duration: 3000,
                    gravity: "top",
                    position: "right",
                    style: { background: "#dc3545" }
                }).showToast();
            }
        });
    }

    function displayComics(comics) {
        const comicList = $('#comic-list');
        comicList.empty();

        comics.forEach(comic => {
            const comicCard = `
 
                <div class="col-md-4 mb-3">
                    <div class="card">
                        <a href="/read/${comic.id}">
                                    <img src="${comic.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                         alt="${comic.tenTruyen || 'Không có tiêu đề'}" 
                                         style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                                </a>
                        <div class="card-body">
                            <h5 class="card-title">${comic.tenTruyen.substring(0,40)}${comic.tenTruyen.length > 40 ? '...':''}</h5>
                            <p class="card-text">${comic.moTa.substring(0, 100)}${comic.moTa.length > 100 ? '...' : ''}</p>
                            <p>Lượt xem: ${comic.luotXem}</p>
                            <p>Lượt thích: ${comic.luotThich}</p>
                        </div>
                    </div>
                </div>
           `
            ;
            comicList.append(comicCard);
        });
    }

    loadComics(currentGenre);

    $('#genre-select, #status-select, #sort-select').on('change', function () {
        const genre = $('#genre-select').val();
        const status = $('#status-select').val();
        const sort = $('#sort-select').val();

        loadComics(genre, status, sort);
    });
});