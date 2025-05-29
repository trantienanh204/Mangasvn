// JSSearch.js
$(document).ready(function() {
    const token = localStorage.getItem("token");

    function toggleFavorite(truyenId, isAdding) {
        if (!token) {
            Toastify({
                text: "Vui lòng đăng nhập để quản lý danh sách yêu thích!",
                duration: 3000,
                gravity: "top",
                position: "right",
                backgroundColor: "#ff4444"
            }).showToast();
            window.location.href = "/login.html";
            return;
        }

        const url = `http://localhost:8080/api/truyen/favorite/${truyenId}`;
        const method = isAdding ? "POST" : "DELETE";
        const cardId = `favourite-truyen-${truyenId}`; // Dùng prefix 'favourite-' cho trang yêu thích
        const isFavouritePage = window.location.pathname.includes('/view/favourite'); // Kiểm tra trang hiện tại

        if (isAdding || !isFavouritePage) {
            // Logic cho các trang khác (trang chủ, read) hoặc khi thêm yêu thích
            $(`[id$="truyen-${truyenId}"]`).each(function() {
                const newButtonHtml = isAdding
                    ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`
                    : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`;
                $(this).find(".favorite-btn").replaceWith(newButtonHtml);
            });

            const favoriteButtonContainer = $("#favorite-button-container");
            const newButtonHtml = isAdding
                ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`
                : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`;
            favoriteButtonContainer.html(newButtonHtml);
        }

        $.ajax({
            url: url,
            method: method,
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(response) {
                Toastify({
                    text: isAdding ? "Đã thêm vào danh sách yêu thích!" : "Đã xóa khỏi danh sách yêu thích!",
                    duration: 3000,
                    gravity: "top",
                    position: "right",
                    backgroundColor: isAdding ? "#00b09b" : "#ff4444"
                }).showToast();

                if (!isAdding && isFavouritePage) {
                    // Chỉ xóa card trong trang yêu thích
                    $(`#${cardId}`).remove();
                    // Kiểm tra nếu danh sách rỗng
                    if ($('#favourite-list .custom-card').length === 0) {
                        $('#favourite-list').html('<p style="color: #ff4444; padding: 10px;">Bạn chưa có truyện yêu thích nào.</p>');
                    }
                }
            },
            error: function(xhr) {
                if (isAdding || !isFavouritePage) {
                    // Hoàn nguyên nút nếu lỗi (cho các trang khác)
                    $(`[id$="truyen-${truyenId}"]`).each(function() {
                        const revertButtonHtml = isAdding
                            ? `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`
                            : `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`;
                        $(this).find(".favorite-btn").replaceWith(revertButtonHtml);
                    });

                    const revertButtonHtml = isAdding
                        ? `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`
                        : `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`;
                    $("#favorite-button-container").html(revertButtonHtml);
                }

                if (xhr.status === 401 || xhr.status === 403) {
                    Toastify({
                        text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        backgroundColor: "#ff4444"
                    }).showToast();
                    localStorage.removeItem("token");
                    window.location.href = "/login.html";
                } else {
                    Toastify({
                        text: isAdding ? "Lỗi khi thêm vào danh sách yêu thích!" : "Lỗi khi xóa khỏi danh sách yêu thích!",
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        backgroundColor: "#ff4444"
                    }).showToast();
                }
            }
        });
    }

    $(document).on('click', '.favorite-btn', function() {
        const truyenId = $(this).data('truyen-id');
        const isAdding = $(this).hasClass('btn-outline-primary');
        toggleFavorite(truyenId, isAdding);
    });

    function truncateText(text, maxLength = 20) {
        if (text.length > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }

    // Logic tìm kiếm
    $("#search-form").on("submit", function(e) {
        e.preventDefault();
        const query = $("#search-input").val().trim();
        if (query) {
            window.location.href = `/view/search.html?query=${encodeURIComponent(query)}`;
        }
    });

    $("#search-input").on("input", function() {
        const query = $(this).val().trim();
        const suggestions = $("#search-suggestions");

        console.log("Searching with query:", query);

        if (query.length < 2) {
            suggestions.hide().empty();
            return;
        }

        $.ajax({
            url: `http://localhost:8080/api/truyen/search?query=${encodeURIComponent(query)}`,
            method: "GET",
            success: function(data) {
                console.log("API response:", data);
                suggestions.empty();
                if (Array.isArray(data) && data.length > 0) {
                    data.slice(0, 5).forEach(truyen => {
                        suggestions.append(`
                            <div class="suggestion-item" data-truyen-id="${truyen.id}" style="display: flex; align-items: center; padding: 8px; border-bottom: 1px solid #eee;">
                                <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                     alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                     style="width: 40px; height: 60px; object-fit: cover; margin-right: 10px;">
                                <span style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                    ${truncateText(truyen.tenTruyen || 'Không có tiêu đề')}
                                </span>
                            </div>
                        `);
                    });
                    suggestions.show();
                } else {
                    suggestions.append('<div class="suggestion-item" style="padding: 8px; color: #ff4444;">Không tìm thấy kết quả nào.</div>');
                    suggestions.show();
                }
            },
            error: function(xhr, status, error) {
                console.log("Error in search API:", status, error);
                suggestions.hide();
            }
        });
    });

    $(document).on("click", ".suggestion-item", function() {
        const truyenId = $(this).data("truyen-id");
        if (truyenId) {
            window.location.href = `/read/${truyenId}`;
        }
    });

    $(document).on("click", function(e) {
        if (!$(e.target).closest(".search-container").length) {
            $("#search-suggestions").hide();
        }
    });

    // lấy tạm của trang chủ dùng haha
    window.displayTruyen = function(container, truyen, token, isScrollable = false, prefix = '') {
        const timeAgo = Math.floor(Math.random() * 10) + 1 + " Phút Trước";
        const isHot = Math.random() > 0.5 ? "Hot" : "";
        const cardId = `${prefix}truyen-${truyen.id || truyen.comicId}`;

        console.log(`Displaying truyen: ID=${truyen.id || truyen.comicId}, TenTruyen=${truyen.tenTruyen}, Prefix=${prefix}`);

        if (token) {
            $.ajax({
                url: `http://localhost:8080/api/truyen/favorite/status/${truyen.id || truyen.comicId}`,
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                },
                success: function(isFavorited) {
                    const buttonHtml = isFavorited
                        ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyen.id || truyen.comicId}">Xóa khỏi yêu thích</button>`
                        : ''; // Chỉ hiển thị nút khi đã yêu thích
                    container.append(`
                    <div class="card custom-card" id="${cardId}" style="${isScrollable ? 'display: inline-block; vertical-align: top; margin: 10px;' : 'margin-bottom: 15px;'}">
                        <div class="card-content">
                            <div class="image-title-container">
                                <a href="/read/${truyen.id || truyen.comicId}">
                                    <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                         alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                         class="card-img">
                                </a>
                                <h5 class="card-title">${truyen.tenTruyen || 'Không có tiêu đề'}</h5> <!-- Tăng kích thước h5 -->
                            </div>
                            ${buttonHtml ? `<div class="card-actions">${buttonHtml}</div>` : ''} <!-- Tách nút ra -->
                        </div>
                        ${isHot ? '<span class="hot-label">Hot</span>' : ''}
                        <span class="time-label">${timeAgo}</span>
                    </div>
                `);
                },
                error: function(xhr, status, error) {
                    console.log(`Lỗi khi kiểm tra trạng thái yêu thích cho truyen ${truyen.id || truyen.comicId}: ${status} - ${error}`);
                    container.append(`
                    <div class="card custom-card" id="${cardId}" style="${isScrollable ? 'display: inline-block; vertical-align: top; margin: 10px;' : 'margin-bottom: 15px;'}">
                        <div class="card-content">
                            <div class="image-title-container">
                                <a href="/read/${truyen.id || truyen.comicId}">
                                    <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                         alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                         class="card-img">
                                </a>
                                <h5 class="card-title">${truyen.tenTruyen || 'Không có tiêu đề'}</h5> <!-- Tăng kích thước h5 -->
                            </div>
                        </div>
                        ${isHot ? '<span class="hot-label">Hot</span>' : ''}
                        <span class="time-label">${timeAgo}</span>
                    </div>
                `);
                }
            });
        } else {
            container.append(`
            <div class="card custom-card" id="${cardId}" style="${isScrollable ? 'display: inline-block; vertical-align: top; margin: 10px;' : 'margin-bottom: 15px;'}">
                <div class="card-content">
                    <div class="image-title-container">
                        <a href="/read/${truyen.id || truyen.comicId}">
                            <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                 alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                 class="card-img">
                        </a>
                        <h5 class="card-title">${truyen.tenTruyen || 'Không có tiêu đề'}</h5> <!-- Tăng kích thước h5 -->
                    </div>
                </div>
                ${isHot ? '<span class="hot-label">Hot</span>' : ''}
                <span class="time-label">${timeAgo}</span>
            </div>
        `);
        }
    };

});