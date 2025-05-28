$(document).ready(function() {
    // Biến toàn cục để lưu trữ ánh xạ ID-to-name
    let authorsMap = {};
    let categoriesMap = {};
    let images = []; // Mảng toàn cục để lưu trữ tất cả các URL ảnh từ API
    let displayedCount = 0; // Biến để theo dõi số lượng ảnh đã hiển thị

    // Hàm tải danh sách tác giả
    function loadAuthors(resolve) {
        const token = localStorage.getItem("token");
        $.ajax({
            url: 'http://localhost:8080/api/authors',
            method: 'GET',
            headers: { "Authorization": "Bearer " + token },
            success: function(authors) {
                authors.forEach(author => { authorsMap[author.id] = author.tenTacGia; });
                if (resolve) resolve();
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách tác giả:', error);
                if (xhr.status === 401 || xhr.status === 403) {
                    Toastify({ text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                    localStorage.removeItem("token");
                    window.location.href = "/login.html";
                }
                if (resolve) resolve();
            }
        });
    }

    // Hàm tải danh sách danh mục
    function loadCategories(resolve) {
        const token = localStorage.getItem("token");
        $.ajax({
            url: 'http://localhost:8080/api/categories',
            method: 'GET',
            headers: { "Authorization": "Bearer " + token },
            success: function(categories) {
                categories.forEach(category => { categoriesMap[category.id] = category.tenDanhMuc; });
                if (resolve) resolve();
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách danh mục:', error);
                if (xhr.status === 401 || xhr.status === 403) {
                    Toastify({ text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                    localStorage.removeItem("token");
                    window.location.href = "/login.html";
                }
                if (resolve) resolve();
            }
        });
    }

    // Hàm phóng to ảnh khi click
    $(document).on('click', '.image-container img', function() {
        const src = $(this).attr('src');
        const img = `<img src="${src}" style="max-width: 90%; height: auto; display: block; margin: 20px auto;" onclick="this.style.display='none';">`;
        $('body').append(`<div style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 10000;" onclick="this.remove()">${img}</div>`);
    });

    // Hàm cuộn danh sách chapter
    window.scrollChapterList = function(direction) {
        const container = $('#chapter-list');
        const scrollAmount = 200;
        if (direction === 'left') {
            container.animate({ scrollLeft: container.scrollLeft() - scrollAmount }, 300);
        } else {
            container.animate({ scrollLeft: container.scrollLeft() + scrollAmount }, 300);
        }
    };

    // Xử lý nút Lên đầu trang
    $(window).scroll(function() {
        if ($(this).scrollTop() > 100) {
            $('#back-to-top').addClass('show');
        } else {
            $('#back-to-top').removeClass('show');
        }
    });

    $('#back-to-top').click(function() {
        $('html, body').animate({ scrollTop: 0 }, 300);
        return false;
    });

    // Chế độ đọc sách
    let currentImageIndex = 0;

    window.loadChapterImages = function(chapterId) {
        const imageContainer = $("#chapter-images");
        imageContainer.empty();
        displayedCount = 0; // Reset số lượng ảnh đã hiển thị
        images = []; // Reset mảng images khi tải chapter mới
        const token = localStorage.getItem("token");
        $.ajax({
            url: `http://localhost:8080/api/images?id_chapter=${chapterId}`,
            method: "GET",
            headers: { "Authorization": "Bearer " + token },
            success: function(imagesData) {
                console.log("Phản hồi API /api/images:", imagesData);
                if (Array.isArray(imagesData) && imagesData.length > 0) {
                    imagesData.sort((a, b) => a.pageNumber - b.pageNumber);
                    images = imagesData.map(img => {
                        let url = img.imageUrl || 'https://via.placeholder.com/800x600.png?text=Fallback+Image';
                        if (!url.startsWith('http')) url = `http://localhost:8080${url}`;
                        return url;
                    });
                    console.log("Danh sách URL ảnh:", images);
                    let displayedImages = images.slice(0, 15);
                    displayedCount = displayedImages.length;
                    if (displayedImages.length === 0) {
                        imageContainer.append('<p>Không có ảnh nào được tải từ API.</p>');
                        return;
                    }
                    displayedImages.forEach((src, index) => {
                        imageContainer.append(`
                            <div class="image-container">
                                <img src="${src}" alt="Ảnh trang ${index + 1}" style="max-width: 100%; height: auto;" onerror="this.onerror=null; this.src='https://via.placeholder.com/800x600.png?text=Error+Loading+Image'; console.log('Lỗi tải ảnh:', '${src}');">
                            </div>
                        `);
                    });
                    if (displayedCount < images.length) {
                        $('#load-all').show();
                        $('#load-15').show();
                    } else {
                        $('#load-all').hide();
                        $('#load-15').hide();
                    }
                } else {
                    imageContainer.append('<p>Không có ảnh cho chapter này.</p>');
                    $('#load-all').hide();
                    $('#load-15').hide();
                }
            },
            error: function(xhr, status, error) {
                imageContainer.html('<p>Lỗi khi tải ảnh: ' + (xhr.responseText || error) + '</p>');
                console.log("Lỗi API /api/images: ", status, error, xhr.responseText);
                if (xhr.status === 401 || xhr.status === 403) {
                    Toastify({ text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                    localStorage.removeItem("token");
                    window.location.href = "/login.html";
                }
            }
        });
    };

    $('#toggle-reading-mode').click(function(e) {
        e.stopPropagation();
        console.log("Nút Chuyển sang chế độ đọc sách đã được nhấn!");
        if (images.length === 0) {
            console.log("Không có ảnh trong mảng images!");
            alert('Vui lòng tải chapter trước khi chuyển sang chế độ đọc sách!');
            return;
        }
        if (images.length > 0) {
            currentImageIndex = 0;
            $('#reading-image')
                .attr('src', images[currentImageIndex])
                .on('error', function() {
                    console.log("Lỗi tải ảnh:", images[currentImageIndex]);
                    $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Loading+Image');
                })
                .on('load', function() {
                    console.log("Ảnh tải thành công:", images[currentImageIndex]);
                    const width = $('#reading-image').width();
                    const height = $('#reading-image').height();
                    console.log("Kích thước ảnh:", width + "x" + height);
                    if (width <= 0 || height <= 0) {
                        console.log("Ảnh không hiển thị do kích thước không hợp lệ, thay bằng placeholder");
                        $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Rendering+Image');
                    }
                });
            $('#reading-mode').addClass('active');
            console.log("Chuyển sang chế độ đọc sách, ảnh đầu tiên:", images[currentImageIndex]);
            updatePagination();
        } else {
            console.log("Không có ảnh nào được tải vào mảng images!");
        }
    });

    window.changePage = function(delta) {
        console.log("Chuyển trang, delta:", delta);
        currentImageIndex += delta;
        if (currentImageIndex < 0) currentImageIndex = 0;
        if (currentImageIndex >= images.length) currentImageIndex = images.length - 1;
        $('#reading-image')
            .attr('src', images[currentImageIndex])
            .on('error', function() {
                console.log("Lỗi tải ảnh:", images[currentImageIndex]);
                $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Loading+Image');
            })
            .on('load', function() {
                console.log("Ảnh tải thành công:", images[currentImageIndex]);
                const width = $('#reading-image').width();
                const height = $('#reading-image').height();
                console.log("Kích thước ảnh:", width + "x" + height);
                if (width <= 0 || height <= 0) {
                    console.log("Ảnh không hiển thị do kích thước không hợp lệ, thay bằng placeholder");
                    $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Rendering+Image');
                }
            });
        console.log("Trang hiện tại:", currentImageIndex + 1, "Ảnh:", images[currentImageIndex]);
        updatePagination();
    };

    $(document).on('click', '#reading-image', function(e) {
        const imgOffset = $(this).offset();
        const clickX = e.pageX - imgOffset.left;
        const imgWidth = $(this).width();
        if (clickX < imgWidth / 2) {
            window.changePage(-1);
        } else {
            window.changePage(1);
        }
    });

    function updatePagination() {
        const pagination = $('#pagination');
        pagination.empty();
        for (let i = 0; i < images.length; i++) {
            pagination.append(`<button class="page-btn${i === currentImageIndex ? ' active' : ''}" data-page="${i}">${i + 1}</button>`);
        }
        const activeBtn = pagination.find('.active');
        if (activeBtn.length) {
            pagination.scrollLeft(activeBtn.position().left - pagination.width() / 2 + activeBtn.width() / 2);
        }
    }

    $(document).off('click', '.page-btn').on('click', '.page-btn', function() {
        currentImageIndex = parseInt($(this).data('page'));
        $('#reading-image').attr('src', images[currentImageIndex]);
        updatePagination();
    });

    $('#close-reading-mode').click(function(e) {
        e.stopPropagation();
        $('#reading-mode').removeClass('active');
    });

    $('#load-all').click(function() {
        const imageContainer = $('#chapter-images');
        imageContainer.empty();
        displayedCount = 0; // Reset displayedCount
        loadChapterImagesFromArray(images); // Hiển thị tất cả ảnh
        displayedCount = images.length; // Cập nhật số lượng ảnh đã hiển thị
        $('#load-all').hide();
        $('#load-15').hide();
    });

    $('#load-15').click(function() {
        const imageContainer = $('#chapter-images');
        const remainingImages = images.slice(displayedCount, images.length); // Chỉ lấy số ảnh còn lại
        if (remainingImages.length > 0) {
            loadChapterImagesFromArray(remainingImages);
            displayedCount += remainingImages.length; // Cập nhật số lượng ảnh đã hiển thị
            if (displayedCount >= images.length) {
                $('#load-15').hide();
                $('#load-all').hide();
            }
        } else {
            $('#load-15').hide();
            $('#load-all').hide();
        }
    });

    // Hàm phụ để hiển thị ảnh từ mảng
    function loadChapterImagesFromArray(imageUrls) {
        const imageContainer = $('#chapter-images');
        imageUrls.forEach((src, index) => {
            imageContainer.append(`
                <div class="image-container">
                    <img src="${src}" alt="Ảnh trang ${displayedCount + index + 1}" style="max-width: 100%; height: auto;" onerror="this.onerror=null; this.src='https://via.placeholder.com/800x600.png?text=Error+Loading+Image'; console.log('Lỗi tải ảnh:', '${src}');">
                </div>
            `);
        });
    }

    $(document).on("click", "#chapter-list button", function() {
        const chapterId = $(this).data("chapter-id");
        $("#chapter-title").text($(this).text());
        window.loadChapterImages(chapterId); // Làm mới mảng images từ API
    });

    // Logic hiển thị thông tin truyện
    if (window.location.pathname.startsWith("/read")) {
        const truyenId = window.location.pathname.split("/").pop();
        const token = localStorage.getItem("token");

        Promise.all([new Promise(resolve => loadAuthors(resolve)), new Promise(resolve => loadCategories(resolve))])
            .then(() => {
                $.ajax({
                    url: `http://localhost:8080/api/truyen/${truyenId}`,
                    method: "GET",
                    headers: { "Authorization": "Bearer " + token },
                    success: function(truyen) {
                        console.log("Phản hồi API /api/truyen/", truyen);
                        $("#comic-title").text(truyen.tenTruyen || "Không có tiêu đề");
                        $("#comic-cover").attr("src", truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                        $("#comic-cover").attr("alt", truyen.tenTruyen || 'Bìa truyện');
                        $("#comic-description").text(truyen.moTa || "Không có mô tả");
                        $("#comic-likes").text(truyen.luotThich || 0);
                        $("#comic-views").text(truyen.luotXem || 0);
                        $("#comic-note").text(truyen.ghiChu || "Không có ghi chú");

                        const translator = truyen.user?.username || "Chưa có thông tin người dịch";
                        console.log("Tên người dịch:", translator);
                        $("#comic-translator").empty().append(`<span class="info-item">${translator}</span>`);

                        const authorsList = truyen.authorIds && Array.isArray(truyen.authorIds) && truyen.authorIds.length > 0
                            ? truyen.authorIds.map(id => authorsMap[id] || `ID ${id} không xác định`)
                            : ['Chưa cập nhật'];
                        console.log("Danh sách tác giả:", authorsList);
                        $('#comic-authors').empty();
                        authorsList.forEach(author => $('#comic-authors').append(`<span class="info-item">${author}</span>`));

                        const categoriesList = truyen.categoryIds && Array.isArray(truyen.categoryIds) && truyen.categoryIds.length > 0
                            ? truyen.categoryIds.map(id => categoriesMap[id] || `ID ${id} không xác định`)
                            : ['Chưa cập nhật'];
                        console.log("Danh sách danh mục:", categoriesList);
                        $('#comic-categories').empty();
                        categoriesList.forEach(category => $('#comic-categories').append(`<span class="category-item">${category}</span>`));

                        if (token) {
                            $.ajax({
                                url: `http://localhost:8080/api/truyen/favorite/status/${truyenId}`,
                                method: "GET",
                                headers: { "Authorization": "Bearer " + token },
                                success: function(isFavorited) {
                                    const buttonHtml = isFavorited
                                        ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`
                                        : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`;
                                    $("#favorite-button-container").html(buttonHtml);
                                },
                                error: function(xhr, status, error) {
                                    console.log(`Lỗi khi kiểm tra trạng thái yêu thích cho truyen ${truyenId}: ${status} - ${error}`);
                                    const buttonHtml = `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`;
                                    $("#favorite-button-container").html(buttonHtml);
                                }
                            });
                        }
                    },
                    error: function(xhr, status, error) {
                        $("#comic-title").text("Lỗi khi tải thông tin truyện: " + error);
                        console.log("Lỗi API /api/truyen/{id}: ", status, error, xhr.responseText);
                        if (xhr.status === 401 || xhr.status === 403) {
                            Toastify({ text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                            localStorage.removeItem("token");
                            window.location.href = "/login.html";
                        }
                    }
                });
            })
            .catch(error => {
                console.log('Lỗi khi tải danh sách tác giả hoặc danh mục:', error);
                Toastify({ text: 'Lỗi tải dữ liệu ban đầu: ' + error, duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
            });
    }
});