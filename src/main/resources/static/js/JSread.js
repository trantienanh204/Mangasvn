$(document).ready(function() {
    let authorsMap = {};
    let categoriesMap = {};
    let images = [];
    let displayedCount = 0;
    let currentChapterId = null;
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.156.147:8080";
    // Hàm tải danh sách tác giả
    function loadAuthors(resolve) {
        const token = localStorage.getItem("token");
        $.ajax({
            url: `${serverHost}/api/authors`,
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
            url: `${serverHost}/api/categories`,
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

// Hàm lưu lịch sử
    function saveToHistory(comicId, tenTruyen, imageComic) {
        const token = localStorage.getItem("token");
        const isLoggedIn = !!token;

        console.log("Lưu lịch sử - Token:", token, "ComicId:", comicId); // Thêm log
        if (isLoggedIn) {
            // Khi đã đăng nhập: lưu lên server
            $.ajax({
                url: 'http://localhost:8080/api/history',
                method: 'POST',
                headers: { "Authorization": "Bearer " + token },
                data: JSON.stringify({ comicId }),
                contentType: 'application/json',
                success: function(response) {
                    console.log("Lưu lịch sử lên server thành công:", response);
                },
                error: function(xhr, status, error) {
                    console.log('Lỗi lưu lịch sử lên server:', {
                        status: xhr.status,
                        responseText: xhr.responseText,
                        error: error
                    });
                    if (xhr.status === 401 || xhr.status === 403) {
                        console.log("Token không hợp lệ, không chuyển hướng ngay lập tức");
                        // Không xóa token ngay, chỉ báo lỗi
                        Toastify({
                            text: "Phiên đăng nhập có vấn đề. Vui lòng thử lại hoặc đăng nhập lại!",
                            duration: 3000,
                            gravity: "top",
                            position: "right",
                            style: { background: "#ff4444" }
                        }).showToast();
                    }
                }
            });
        } else {
            // Khi chưa đăng nhập: lưu vào localStorage
            let localHistory = JSON.parse(localStorage.getItem('localHistory') || '[]');
            const existingIndex = localHistory.findIndex(item => item.comicId === comicId);
            if (existingIndex !== -1) {
                localHistory[existingIndex] = { comicId, tenTruyen, imageComic, lastRead: new Date() };
            } else {
                localHistory.push({ comicId, tenTruyen, imageComic, lastRead: new Date() });
            }
            localStorage.setItem('localHistory', JSON.stringify(localHistory));
            console.log("Lưu lịch sử vào localStorage thành công:", localHistory);
        }
    }
    // Hàm tải lịch sử
    function loadHistory(callback) {
        const token = localStorage.getItem("token");
        const isLoggedIn = !!token;

        if (isLoggedIn) {
            // Khi đã đăng nhập: lấy từ server
            $.ajax({
                url: `${serverHost}/api/history`,
                method: 'GET',
                headers: { "Authorization": "Bearer " + token },
                success: function(history) {
                    console.log("Tải lịch sử user từ server thành công:", history);
                    callback(history);
                },
                error: function(xhr, status, error) {
                    console.log('Lỗi tải lịch sử user từ server:', {
                        status: xhr.status,
                        responseText: xhr.responseText,
                        error: error
                    });
                    callback([]);
                    if (xhr.status === 401 || xhr.status === 403) {
                        Toastify({
                            text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                            duration: 3000,
                            gravity: "top",
                            position: "right",
                            style: { background: "#ff4444" }
                        }).showToast();
                        localStorage.removeItem("token");
                        window.location.href = "/login.html";
                    }
                }
            });
        } else {
            // Khi chưa đăng nhập: lấy từ localStorage
            const localHistory = JSON.parse(localStorage.getItem('localHistory') || '[]');
            console.log("Tải lịch sử từ localStorage:", localHistory);
            callback(localHistory);
        }
    }

    // Hàm hiển thị lịch sử
    function displayHistory(history, containerId) {
        const token = localStorage.getItem("token");
        const historyContainer = $(containerId);
        if (historyContainer.length) {
            historyContainer.empty();
            if (history && history.length > 0) {
                historyContainer.append(`
                    <div class="history-header">
                        <h3>Lịch sử đọc</h3>
                        <button id="clear-history" class="btn btn-danger btn-sm" style="display: ${token ? 'none' : 'inline-block'}">Xóa lịch sử</button>
                    </div>
                `);
                history.forEach(item => {
                    historyContainer.append(`
                        <div class="history-item">
                            <a href="/read/${item.comicId}">
                                <img src="${item.imageComic}" alt="${item.tenTruyen}" style="width: 50px; height: 70px; object-fit: cover; margin-right: 10px;">
                            </a>
                            <span>${item.tenTruyen}</span>
                            <span> - Last read: ${new Date(item.lastRead).toLocaleString()}</span>
                        </div>
                    `);
                });
            } else {
                historyContainer.append('<p>Không có lịch sử đọc.</p>');
            }
        }
    }

    // Hàm xóa lịch sử trong localStorage
    window.clearLocalHistory = function() {
        localStorage.removeItem('localHistory');
        const historyContainer = $("#guest-history");
        const historyListContainer = $("#history-list");
        if (historyContainer.length) {
            historyContainer.empty();
            historyContainer.append('<p>Không có lịch sử đọc.</p>');
        }
        if (historyListContainer.length) {
            historyListContainer.empty();
            historyListContainer.append('<p>Không có lịch sử đọc.</p>');
        }
        Toastify({
            text: "Đã xóa lịch sử!",
            duration: 3000,
            gravity: "top",
            position: "right",
            style: { background: "#4caf50" }
        }).showToast();
    };


    window.loadChapterImages = function(chapterId) {
        const imageContainer = $("#chapter-images");
        imageContainer.empty();
        displayedCount = 0;
        images = [];
        const token = localStorage.getItem("token");
        console.log("Tải ảnh chapter - Token:", token, "ChapterId:", chapterId); // Thêm log

        $.ajax({
            url: `${serverHost}/api/images?id_chapter=${chapterId}`,
            method: "GET",
            headers: { "Authorization": "Bearer " + token },
            success: function(imagesData) {
                console.log("Phản hồi API /api/images:", imagesData);
                if (Array.isArray(imagesData) && imagesData.length > 0) {
                    imagesData.sort((a, b) => a.pageNumber - b.pageNumber);
                    images = imagesData.map(img => {
                        let url = img.imageUrl || 'https://i.postimg.cc/zBZ7k81R/cass.jpg';
                        if (!url.startsWith('http')) url = `http://localhost:8080${url}`;
                        return url;
                    });

                    let displayedImages = images.slice(0, Math.min(15, images.length));
                    displayedCount = displayedImages.length;

                    if (displayedImages.length === 0) {
                        imageContainer.append('<p>Không có ảnh nào được tải từ API.</p>');
                        $('#load-all').hide();
                        $('#load-15').hide();
                        return;
                    }

                    loadChapterImagesFromArray(displayedImages, 0);

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
                console.log("Lỗi API /api/images: ", { status, error, response: xhr.responseText });
                if (xhr.status === 401 || xhr.status === 403) {
                    console.log("Token không hợp lệ khi tải ảnh, không chuyển hướng ngay");
                    Toastify({ text: "Phiên đăng nhập có vấn đề. Vui lòng thử lại hoặc đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                    // Không xóa token ngay, chỉ báo lỗi
                }
            }
        });
    };

    $('#load-all').click(function() {
        const imageContainer = $('#chapter-images');
        imageContainer.empty();
        displayedCount = 0;
        loadChapterImagesFromArray(images);
        displayedCount = images.length;
        $('#load-all').hide();
        $('#load-15').hide();
    });

    $('#load-15').click(function() {
        const imageContainer = $('#chapter-images');
        const remainingImages = images.slice(displayedCount, displayedCount + 15);

        if (remainingImages.length > 0) {
            imageContainer.empty();
            loadChapterImagesFromArray(images.slice(0, displayedCount + remainingImages.length), 0);
            displayedCount += remainingImages.length;
            if (displayedCount >= images.length) {
                $('#load-15').hide();
                $('#load-all').hide();
            }
        } else {
            console.log("Không còn ảnh nào để hiển thị thêm.");
            $('#load-15').hide();
            $('#load-all').hide();
        }
    });

    function loadChapterImagesFromArray(imageUrls, startIndex = 0) {
        const imageContainer = $('#chapter-images');
        imageUrls.forEach((src, index) => {
            const absoluteIndex = startIndex + index;
            imageContainer.append(`
                <div class="image-container">
                    <img src="${src}" alt="Ảnh trang ${absoluteIndex + 1}" style="max-width: 100%; height: auto;" onerror="this.onerror=null; this.src='https://via.placeholder.com/800x600.png?text=Error+Loading+Image'; console.log('Lỗi tải ảnh:', '${src}');">
                </div>
            `);
        });
    }

    $(document).on("click", "#chapter-list button", function(e) {
        e.stopPropagation();
        const chapterId = $(this).data("chapter-id");
        const chapterTitle = $(this).text();

        if (chapterId !== currentChapterId) {
            $("#chapter-title").text(chapterTitle).data("current-chapter-id", chapterId);
            currentChapterId = chapterId;
            window.loadChapterImages(chapterId);
        } else {
            console.log("Chapter này đã được tải, không gọi lại loadChapterImages.");
        }
    });

    if (window.location.pathname.startsWith("/read")) {
        const truyenId = window.location.pathname.split("/").pop();
        const token = localStorage.getItem("token");

        Promise.all([new Promise(resolve => loadAuthors(resolve)), new Promise(resolve => loadCategories(resolve))])
            .then(() => {
                $.ajax({
                    url: `${serverHost}/api/truyen/${truyenId}`,
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
                        $("#comic-translator").empty().append(`<span class="info-item">${translator}</span>`);

                        const authorsList = truyen.authorIds && Array.isArray(truyen.authorIds) && truyen.authorIds.length > 0
                            ? truyen.authorIds.map(id => authorsMap[id] || `ID ${id} không xác định`)
                            : ['Chưa cập nhật'];
                        $('#comic-authors').empty();
                        authorsList.forEach(author => $('#comic-authors').append(`<span class="info-item">${author}</span>`));

                        const categoriesList = truyen.categoryIds && Array.isArray(truyen.categoryIds) && truyen.categoryIds.length > 0
                            ? truyen.categoryIds.map(id => categoriesMap[id] || `ID ${id} không xác định`)
                            : ['Chưa cập nhật'];
                        $('#comic-categories').empty();
                        categoriesList.forEach(category => $('#comic-categories').append(`<span class="category-item">${category}</span>`));

                        const tenTruyen = truyen.tenTruyen || "Không có tiêu đề";
                        const imageComic = truyen.imageComic || 'https://via.placeholder.com/50x70.png?text=No+Image';
                        saveToHistory(truyenId, tenTruyen, imageComic);

                        if (token) {
                            $.ajax({
                                url: `${serverHost}/api/truyen/favorite/status/${truyenId}`,
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

                        // Hiển thị lịch sử trên trang đọc
                        loadHistory(function(history) {
                            displayHistory(history, "#guest-history");
                        });
                    },
                    error: function(xhr, status, error) {
                        $("#comic-title").text("Lỗi khi tải thông tin truyện: " + error);
                        console.log("Lỗi API /api/truyen/{id}: ", { status, error, response: xhr.responseText });
                        if (xhr.status === 401 || xhr.status === 403) {
                            console.log("Token không hợp lệ khi tải truyện, không chuyển hướng ngay");
                            Toastify({ text: "Phiên đăng nhập có vấn đề. Vui lòng thử lại hoặc đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
                            // Không xóa token ngay, chỉ báo lỗi
                        }
                        saveToHistory(truyenId, "Không có tiêu đề", 'https://via.placeholder.com/50x70.png?text=No+Image');
                    }
                });
            })
            .catch(error => {
                console.log('Lỗi khi tải danh sách tác giả hoặc danh mục:', error);
                Toastify({ text: 'Lỗi tải dữ liệu ban đầu: ' + error, duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
                saveToHistory(truyenId, "Không có tiêu đề", 'https://via.placeholder.com/50x70.png?text=No+Image');
            });
    }
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
                    $(this).attr('src', 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                })
                .on('load', function() {
                    console.log("Ảnh tải thành công:", images[currentImageIndex]);
                    const width = $('#reading-image').width();
                    const height = $('#reading-image').height();
                    console.log("Kích thước ảnh:", width + "x" + height);
                    if (width <= 0 || height <= 0) {
                        console.log("Ảnh không hiển thị do kích thước không hợp lệ, thay bằng placeholder");
                        $(this).attr('src', 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                    }
                });
            $('#reading-mode').addClass('active');
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
                $(this).attr('src', 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
            })
            .on('load', function() {
                console.log("Ảnh tải thành công:", images[currentImageIndex]);
                const width = $('#reading-image').width();
                const height = $('#reading-image').height();
                console.log("Kích thước ảnh:", width + "x" + height);
                if (width <= 0 || height <= 0) {
                    console.log("Ảnh không hiển thị do kích thước không hợp lệ, thay bằng placeholder");
                    $(this).attr('src', 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                }
            });

        updatePagination();
    };

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

    $(document).on('click', '.page-btn', function() {
        currentImageIndex = parseInt($(this).data('page'));
        $('#reading-image').attr('src', images[currentImageIndex]);
        updatePagination();
    });

    $('#close-reading-mode').click(function(e) {
        e.stopPropagation();
        $('#reading-mode').removeClass('active');
    });

    $(document).on('click', '.image-container img', function() {
        const src = $(this).attr('src');
        const img = `<img src="${src}" style="max-width: 90%; height: auto; display: block; margin: 20px auto;" onclick="this.style.display='none';">`;
        $('body').append(`<div style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 10000;" onclick="this.remove()">${img}</div>`);
    });

    window.scrollChapterList = function(direction) {
        const container = $('#chapter-list');
        const scrollAmount = 200;
        if (direction === 'left') {
            container.animate({ scrollLeft: container.scrollLeft() - scrollAmount }, 300);
        } else {
            container.animate({ scrollLeft: container.scrollLeft() + scrollAmount }, 300);
        }
    };

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

    $('#reading-image').on('click', function(e) {
        e.stopPropagation();
        const imageWidth = $(this).width();
        const clickX = e.offsetX;

        if (clickX < imageWidth / 2) {
            window.changePage(-1);
        } else {
            window.changePage(1);
        }
    });

    // Hàm đăng xuất
    window.logout = function() {
        localStorage.removeItem("token");
        console.log("Đăng xuất thành công, token đã xóa");
        window.location.href = "/view/trangchu.html";
    }

    // Gắn sự kiện xóa lịch sử (dùng chung cho cả trang đọc và trang lịch sử)
    $(document).on("click", "#clear-history", function() {
        if (!localStorage.getItem("token")) {
            window.clearLocalHistory();
        }
    });
});