$(document).ready(function () {
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.238.147:8080";

    // Lấy genre từ URL
    const pathParts = window.location.pathname.split('/');
    let currentGenre = pathParts[pathParts.length - 1] || $('#genre-select').val() || 'romcom'; // Mặc định là 'romcom'
    console.log("Genre từ URL: " + currentGenre);

    // Cập nhật và chọn genre-select
    $('#genre-select').val(currentGenre);
    console.log("Genre-select được đặt thành: " + $('#genre-select').val());

    function loadComics(genre, status = '', sort = '') {
        let url = `${serverHost}/theloai/translate/${genre}`;
        if (status || sort) {
            url += `?status=${status}&sort=${sort}`;
        }
        console.log("Gọi API: " + url);

        $.ajax({
            url: url,
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            },
            success: function (data) {
                console.log("Dữ liệu nhận được:", data);
                if (data && data.length > 0) {
                    $('#theLoai').text(genre.charAt(0).toUpperCase() + genre.slice(1));
                    displayComics(data);
                    console.log("Danh sách truyện đã được hiển thị.");
                } else {
                    $('#theLoai').text("Không tìm thấy truyện");
                    $('#comic-list').html('<p>Không có truyện nào trong danh mục này.</p>');
                    console.log("Không có dữ liệu để hiển thị.");
                }
            },
            error: function (xhr, status, error) {
                console.log("Lỗi API - Status:", status, "Error:", error, "Response:", xhr.responseText);
                $('#theLoai').text("Lỗi tải dữ liệu");
                $('#comic-list').html('<p>Lỗi khi tải truyện, vui lòng thử lại!</p>');
                Toastify({
                    text: "Lỗi khi tải truyện, vui lòng thử lại! Chi tiết: " + error,
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
        console.log("Bắt đầu hiển thị danh sách, comicList tồn tại:", comicList.length > 0);
        comicList.empty();

        if (!comicList.length) {
            console.error("Phần tử #comic-list không được tìm thấy trong DOM!");
            return;
        }

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
                            <h5 class="card-title">${comic.tenTruyen.substring(0, 40)}${comic.tenTruyen.length > 40 ? '...' : ''}</h5>
                            <p class="card-text">${comic.moTa.substring(0, 100)}${comic.moTa.length > 100 ? '...' : ''}</p>
                            <p>Lượt xem: ${comic.luotXem}</p>
                            <p>Lượt thích: ${comic.luotThich}</p>
                        </div>
                    </div>
                </div>
            `;
            comicList.append(comicCard);
        });
        console.log("Hoàn thành hiển thị danh sách.");
    }

    // Tải dữ liệu ngay khi trang load
    loadComics(currentGenre);

    // Xử lý sự kiện thay đổi bộ lọc
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