$(document).ready(function() {
    let authorsMap = {};
    let categoriesMap = {};
    let images = [];
    let displayedCount = 0;
    let currentChapterId = null;

    const serverHost = window.location.origin;

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
                url: `${serverHost}/api/history`,
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
            let localHistory = JSON.parse(localStorage.getItem('localHistory') || '[]');
            const existingIndex = localHistory.findIndex(item => item.comicId === comicId);
            if (existingIndex !== -1) {
                localHistory[existingIndex] = { comicId, tenTruyen, imageComic, lastRead: new Date() };
            } else {
                localHistory.push({ comicId, tenTruyen, imageComic, lastRead: new Date() });
            }
            localStorage.setItem('localHistory', JSON.stringify(localHistory));

        }
    }
    // Hàm tải lịch sử
    function loadHistory(callback) {
        const token = localStorage.getItem("token");
        const isLoggedIn = !!token;

        if (isLoggedIn) {
            $.ajax({
                url: `${serverHost}/api/history`,
                method: 'GET',
                headers: { "Authorization": "Bearer " + token },
                success: function(history) {
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
            const localHistory = JSON.parse(localStorage.getItem('localHistory') || '[]');
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
                            <a href="${serverHost}/truyen/${item.comicId}">
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

                }
            }
        });
        window.loadComments(chapterId);
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
        if (chapterId !== window.ACTIVE_CHAPTER_ID) {
            $("#chapter-title").text(chapterTitle);
            window.ACTIVE_CHAPTER_ID = chapterId;

            window.loadChapterImages(chapterId);
            window.loadComments(chapterId);
        } else {
            console.log("Chapter này đã được tải.");
        }
    });

    if (window.location.pathname.startsWith("/truyen")) {
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
                        categoriesList.forEach(category => $('#comic-categories').append(`<span class="category-item"><a href="${serverHost}/theloai/${category}">${category}</a></span>`));

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
                        
                        loadHistory(function(history) {
                            displayHistory(history, "#guest-history");
                        });
                        window.loadComments(truyenId);
                        window.loadComments(null);
                    },
                    error: function(xhr, status, error) {
                        $("#comic-title").text("Lỗi khi tải thông tin truyện: " + error);
                        if (xhr.status === 401 || xhr.status === 403 || xhr.status === 404 ) {

                            window.location.href="/"
                         //    console.log("Token không hợp lệ khi tải truyện, không chuyển hướng ngay check point");

                         //
                         //    Toastify({ text: "Phiên đăng nhập có vấn đề. Vui lòng thử lại hoặc đăng nhập lại!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
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
        // Ngăn chặn hành vi mặc định
        e.preventDefault();
        e.stopPropagation();

        const hasValidImages = images && images.length > 0 && images.some(img => img && img.trim() !== '');

        if (!hasValidImages) {
            alert('Vui lòng tải chapter trước khi chuyển sang chế độ đọc sách!');

            $('#reading-mode').removeClass('active').hide();

            return;
        }


        currentImageIndex = 0;

        $('#reading-mode').show();

        $('#reading-image')
            .attr('src', images[currentImageIndex])
            .css({
                'max-width': '100%',
                'max-height': '100vh',
                'object-fit': 'contain',
                'display': 'block',
                'margin': '0 auto'
            })
            .off('error').on('error', function() {
            console.log("Lỗi tải ảnh:", images[currentImageIndex]);
            $(this).attr('src', 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
        })
            .off('load').on('load', function() {
            const width = this.naturalWidth;
            const height = this.naturalHeight;
            if (width <= 0 || height <= 0) {
                $(this).attr('src', 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
            }
        });

        $('#reading-mode').addClass('active').css({
            'position': 'fixed',
            'top': 0,
            'left': 0,
            'width': '100vw',
            'height': '100vh',
            'overflow': 'hidden',
            'background-color': 'rgba(0,0,0,0.9)',
            'z-index': 9999
        });
        if (typeof updatePagination === "function") {
            updatePagination();
        }
    });

    window.changePage = function(delta) {


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
                const width = $('#reading-image').width();
                const height = $('#reading-image').height();
                if (width <= 0 || height <= 0) {
                    $(this).attr('src', 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                }
            });

        updatePagination();
    };

    function updatePagination() {

        if (!images || images.length === 0) return;
        const progressPercentage = ((currentImageIndex + 1) / images.length) * 100;

        $('#reading-progress-bar').css('width', progressPercentage + '%');
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


    window.logout = function() {
        localStorage.removeItem("token");
        window.location.href = "/";
    }
//

    window.loadComments = function(chapterId) {
        const comicId = window.location.pathname.split("/").pop();
        const apiUrl = chapterId
            ? `${serverHost}/api/comments/chapter/${chapterId}`
            : `${serverHost}/api/comments/comic/${comicId}`;

        $.ajax({
            url: apiUrl,
            method: 'GET',
            success: function(data) {
                const commentListDiv = $('#comment-list');
                commentListDiv.empty();

                if (data.length === 0) {
                    const msg = chapterId ? 'Chưa có bình luận nào cho chapter này.' : 'Chưa có bình luận chung cho truyện này.';
                    commentListDiv.append(`<p class="text-muted">${msg}</p>`);
                    return;
                }


                function buildCommentHtml(cmt, isReply = false) {
                    const userName = (cmt.users && cmt.users.username) ? cmt.users.username : 'Ẩn danh';
                    const displayLike = (cmt.like && cmt.like > 0) ? cmt.like : '';
                    const marginClass = isReply ? 'ms-5 border-start ps-3 mt-2' : 'mb-3 border-bottom pb-2';

                    return `
                        <div class="comment-item ${marginClass}">
                            <div class="d-flex justify-content-between align-items-center">
                                <strong>${userName}</strong> 
                                <small class="text-muted" style="font-size: 0.8rem;">${cmt.ngayTao}</small>
                            </div>
                            <p class="mb-1 mt-1">${cmt.comment}</p>
                            <div class="comment-actions mt-1" style="font-size: 0.85rem; user-select: none;">
                                <span class="btn-like-comment text-muted" data-comment-id="${cmt.id}" style="cursor: pointer; font-weight: 600;">
                                    Thích <span class="like-count ms-1">${displayLike}</span>
                                </span>
                               
                                ${!isReply ? `<span class="btn-reply-comment text-muted ms-3" data-comment-id="${cmt.id}" style="cursor: pointer; font-weight: 600;">Phản hồi</span>` : ''}
                            </div>
                        </div>
                    `;
                }


                data.forEach(comment => {
                    commentListDiv.append(buildCommentHtml(comment, false));

                    if (comment.replies && comment.replies.length > 0) {
                        comment.replies.forEach(reply => {
                            commentListDiv.append(buildCommentHtml(reply, true));
                        });
                    }
                });
            },
            error: function(xhr) {
                $('#comment-list').html('<p class="text-danger">Không thể tải bình luận lúc này.</p>');
            }
        });
    };


    $(document).on('click', '.btn-like-comment', function() {
        const token = localStorage.getItem("token");

        if (!token) {
            Toastify({ text: "Bạn cần đăng nhập để thích bình luận!", duration: 3000, gravity: "top", position: "right", style: { background: "#ffc107" } }).showToast();
            window.location.href = serverHost + "/api/auth/login";
            return;
        }

        const btn = $(this);
        const commentId = btn.data('comment-id');
        const countSpan = btn.find('.like-count');
        let currentCount = parseInt(countSpan.text()) || 0;

        $.ajax({
            url: `${serverHost}/api/comments/${commentId}/like`,
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(response) {
                if (response === "LIKED") {
                    countSpan.text(currentCount + 1);
                    btn.removeClass('text-muted').addClass('text-primary');
                } else {
                    const newCount = currentCount - 1;
                    countSpan.text(newCount > 0 ? newCount : '');
                    btn.removeClass('text-primary').addClass('text-muted');
                }
            },
            error: function(xhr) {
                console.log('Lỗi gửi like:', xhr);
                Toastify({ text: "Có lỗi xảy ra khi xử lý like!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
            }
        });
    });

    $(document).on('click', '.btn-reply-comment', function() {
        $('.reply-box').remove();

        const commentId = $(this).data('comment-id');
        const replyHtml = `
            <div class="reply-box mt-2 ms-4">
                <div class="input-group input-group-sm w-75">
                    <input type="text" class="form-control reply-input" placeholder="Viết phản hồi...">
                    <button class="btn btn-secondary btn-submit-reply" data-parent-id="${commentId}">Gửi</button>
                    <button class="btn btn-outline-danger btn-cancel-reply">Hủy</button>
                </div>
            </div>
        `;
        $(this).closest('.comment-item').append(replyHtml);
    });


    $(document).on('click', '.btn-cancel-reply', function() {
        $(this).closest('.reply-box').remove();
    });

    $(document).on('click', '.btn-submit-reply', function() {
        const token = localStorage.getItem("token");
        if (!token) {
            Toastify({ text: "Bạn cần đăng nhập để phản hồi!", duration: 3000, style: { background: "#ff4444" } }).showToast();
            return;
        }

        const btn = $(this);
        const parentId = btn.data('parent-id');
        const inputField = btn.siblings('.reply-input');
        const content = inputField.val().trim();
        const comicId = window.location.pathname.split("/").pop();

        if (!content) return;

        const payload = {
            comicId: parseInt(comicId),
            chapterId: window.ACTIVE_CHAPTER_ID || null,
            parentId: parentId,
            content: content
        };

        $.ajax({
            url: `${serverHost}/api/comments/add`,
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            data: JSON.stringify(payload),
            success: function() {
                window.loadComments(window.ACTIVE_CHAPTER_ID || null);
                Toastify({ text: "Phản hồi thành công!", duration: 3000, style: { background: "#4caf50" } }).showToast();
            },
            error: function(xhr) {
                Toastify({ text: "Lỗi gửi phản hồi!", duration: 3000, style: { background: "#ff4444" } }).showToast();
            }
        });
    });


    $(document).on('click', '#btn-submit-comment', function() {
        const token = localStorage.getItem("token");

        if (!token) {
            Toastify({ text: "Bạn cần đăng nhập để bình luận!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
            window.location.href=serverHost+"/api/auth/login";
            return;
        }

        const inputField = $('#comment-input');
        const content = inputField.val().trim();
        const comicId = window.location.pathname.split("/").pop();

        if (!content) {
            Toastify({ text: "Vui lòng nhập nội dung!", duration: 3000, gravity: "top", position: "right", style: { background: "#ffc107" } }).showToast();
            return;
        }

        const payload = {
            comicId: parseInt(comicId),
            chapterId: window.ACTIVE_CHAPTER_ID || null,
            content: content
        };

        $.ajax({
            url: `${serverHost}/api/comments/add`,
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            data: JSON.stringify(payload),
            success: function() {
                inputField.val('');
                window.loadComments(window.ACTIVE_CHAPTER_ID || null);
                Toastify({ text: "Gửi bình luận thành công!", duration: 3000, gravity: "top", position: "right", style: { background: "#4caf50" } }).showToast();
            },
            error: function(xhr) {
                Toastify({ text: "Lỗi khi gửi bình luận!", duration: 3000, gravity: "top", position: "right", style: { background: "#ff4444" } }).showToast();
            }
        });
    });

    $(document).on("click", "#clear-history", function() {
        if (!localStorage.getItem("token")) {
            window.clearLocalHistory();
        }
    });
});


const toggleReadingModeBtn = document.getElementById('toggle-reading-mode');
const readingModeDiv = document.getElementById('reading-mode');
const closeReadingModeBtn = document.getElementById('close-reading-mode');

    toggleReadingModeBtn.addEventListener('click', () => {
    readingModeDiv.classList.add('active');
});

     closeReadingModeBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    readingModeDiv.classList.remove('active');
});
