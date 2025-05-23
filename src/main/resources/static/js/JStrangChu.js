$(document).ready(function() {
    const token = localStorage.getItem("token");
    const userInfo = $("#user-info");
    const loginLink = $("#login-link");
    const logoutLink = $("#logout-link");

    // Kiểm tra token để tự động đăng nhập
    if (token) {
        $.ajax({
            url: "http://localhost:8080/api/auth/user-info",
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(data) {
                userInfo.text("Xin chào, " + data.role);
                loginLink.hide();
                logoutLink.show();
            },
            error: function(xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    localStorage.removeItem("token");
                    userInfo.text("");
                    loginLink.show();
                    logoutLink.hide();
                }
            }
        });
    } else {
        userInfo.text("");
        loginLink.show();
        logoutLink.hide();
    }

    function displayTruyen(container, truyen, token, isScrollable = false, prefix = '') {
        const timeAgo = Math.floor(Math.random() * 10) + 1 + " Phút Trước";
        const isHot = Math.random() > 0.5 ? "Hot" : "";
        const cardId = `${prefix}truyen-${truyen.id}`;

        console.log(`Displaying truyen: ID=${truyen.id}, TenTruyen=${truyen.tenTruyen}, Prefix=${prefix}`);

        if (token) {
            $.ajax({
                url: `http://localhost:8080/api/truyen/favorite/status/${truyen.id}`,
                method: "GET",
                headers: {
                    "Authorization": "Bearer " + token
                },
                success: function(isFavorited) {
                    const buttonHtml = isFavorited
                        ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyen.id}">Xóa khỏi yêu thích</button>`
                        : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyen.id}">Thêm vào yêu thích</button>`;
                    container.append(`
                        <div class="card" id="${cardId}" style="${isScrollable ? 'display: inline-block; vertical-align: top; margin: 10px;' : ''}">
                            <div style="position: relative;">
                                <a href="/read/${truyen.id}">
                                    <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                         alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                         style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                                </a>
                                <span style="position: absolute; top: 5px; left: 5px; background-color: ${isHot ? '#ff4444' : 'transparent'}; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${isHot}</span>
                                <span style="position: absolute; top: 5px; right: 5px; background-color: #00bcd4; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${timeAgo}</span>
                            </div>
                            <div style="padding: 10px; color: #fff;">
                                <h6 style="margin: 0; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${truyen.tenTruyen || 'Không có tiêu đề'}</h6>
                                ${buttonHtml}
                            </div>
                        </div>
                    `);
                },
                error: function(xhr, status, error) {
                    console.log(`Lỗi khi kiểm tra trạng thái yêu thích cho truyen ${truyen.id}: ${status} - ${error}`);
                    const buttonHtml = `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyen.id}">Thêm vào yêu thích</button>`;
                    container.append(`
                        <div class="card" id="${cardId}" style="${isScrollable ? 'display: inline-block; vertical-align: top; margin: 10px;' : ''}">
                            <div style="position: relative;">
                                <a href="/read/${truyen.id}">
                                    <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                         alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                         style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                                </a>
                                <span style="position: absolute; top: 5px; left: 5px; background-color: ${isHot ? '#ff4444' : 'transparent'}; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${isHot}</span>
                                <span style="position: absolute; top: 5px; right: 5px; background-color: #00bcd4; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${timeAgo}</span>
                            </div>
                            <div style="padding: 10px; color: #fff;">
                                <h6 style="margin: 0; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${truyen.tenTruyen || 'Không có tiêu đề'}</h6>
                                ${buttonHtml}
                            </div>
                        </div>
                    `);
                }
            });
        } else {
            container.append(`
                <div class="card" id="${cardId}" style="${isScrollable ? 'display: inline-block; vertical-align: top; margin: 10px;' : ''}">
                    <div style="position: relative;">
                        <a href="/read/${truyen.id}">
                            <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                 alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                 style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                        </a>
                        <span style="position: absolute; top: 5px; left: 5px; background-color: ${isHot ? '#ff4444' : 'transparent'}; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${isHot}</span>
                        <span style="position: absolute; top: 5px; right: 5px; background-color: #00bcd4; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${timeAgo}</span>
                    </div>
                    <div style="padding: 10px; color: #fff;">
                        <h6 style="margin: 0; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${truyen.tenTruyen || 'Không có tiêu đề'}</h6>
                    </div>
                </div>
            `);
        }
    }

    // Gọi API để lấy danh sách truyện hot (carousel chính)
    $.ajax({
        url: "http://localhost:8080/api/truyen/hot",
        method: "GET",
        success: function(data) {
            const carouselInner = $("#hot-truyen-carousel");
            if (Array.isArray(data) && data.length > 0) {
                data.forEach((truyen, index) => {
                    const isActive = index === 0 ? "active" : "";
                    carouselInner.append(`
                        <div class="carousel-item ${isActive}">
                            <div class="d-flex align-items-center" style="width: 100%; height: 300px; overflow: hidden;">
                                <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}"
                                     class="img-shadow img-fluid img-carousel" alt="${truyen.tenTruyen}">
                                <div class="text-container" style="width: 85%; padding: 1rem; background-color: rgba(248, 249, 250, 0.7);">
                                    <h5 class="text-title">${truyen.tenTruyen}</h5>
                                    <p class="text-content">${truyen.moTa || "Không có mô tả"}</p>
                                    <a href="/read/${truyen.id}" class="btn btn-primary">Đọc ngay</a>
                                </div>
                            </div>
                        </div>
                    `);
                });
            } else {
                carouselInner.append('<div class="carousel-item active"><p>Không có truyện hot hoặc lỗi dữ liệu</p></div>');
            }
        },
        error: function(xhr, status, error) {
            $("#hot-truyen-carousel").append('<div class="carousel-item active"><p>Lỗi khi tải truyện hot: ' + error + '</p></div>');
            console.log("Lỗi API /api/truyen/hot: ", status, error);
        }
    });

    // Gọi API để lấy danh sách truyện nổi bật (cuộn ngang)
    $.ajax({
        url: "http://localhost:8080/api/truyen/hot",
        method: "GET",
        success: function(data) {
            const truyenListHot = $("#truyen-list-hot");
            if (Array.isArray(data)) {
                truyenListHot.empty();
                data.forEach(truyen => {
                    displayTruyen(truyenListHot, truyen, token, true, 'hot-');
                });
            } else {
                truyenListHot.text("Lỗi dữ liệu truyện nổi bật");
            }
        },
        error: function(xhr, status, error) {
            $("#truyen-list-hot").text("Lỗi khi tải danh sách truyen nổi bật: " + error);
            console.log("Lỗi API /api/truyen/hot: ", status, error);
        }
    });

    // Gọi API để lấy danh sách truyện (grid, 5 card mỗi hàng)
    $.ajax({
        url: "http://localhost:8080/api/truyen/list",
        method: "GET",
        success: function(data) {
            const truyenList = $("#truyen-list");
            if (Array.isArray(data)) {
                truyenList.empty();
                data.forEach(truyen => {
                    displayTruyen(truyenList, truyen, token, false, 'list-');
                });
            } else {
                truyenList.text("Lỗi dữ liệu danh sách truyện");
            }
        },
        error: function(xhr, status, error) {
            $("#truyen-list").text("Lỗi khi tải danh sách truyện: " + error);
            console.log("Lỗi API /api/truyen/list: ", status, error);
        }
    });

    // Gọi API để lấy danh sách truyện mới (grid, 5 card mỗi hàng)
    $.ajax({
        url: "http://localhost:8080/api/truyen/moi",
        method: "GET",
        success: function(data) {
            const truyenListNew = $("#truyen-list-new");
            if (Array.isArray(data)) {
                truyenListNew.empty();
                data.forEach(truyen => {
                    displayTruyen(truyenListNew, truyen, token, false, 'new-');
                });
            } else {
                $.ajax({
                    url: "http://localhost:8080/api/truyen/list",
                    method: "GET",
                    success: function(data) {
                        if (Array.isArray(data)) {
                            const shuffled = data.sort(() => 0.5 - Math.random());
                            const newTruyen = shuffled.slice(0, 5);
                            truyenListNew.empty();
                            newTruyen.forEach(truyen => {
                                displayTruyen(truyenListNew, truyen, token, false, 'new-');
                            });
                        } else {
                            truyenListNew.text("Lỗi dữ liệu danh sách truyện mới");
                        }
                    },
                    error: function(xhr, status, error) {
                        truyenListNew.text("Lỗi khi tải danh sách truyện mới: " + error);
                        console.log("Lỗi API /api/truyen/list: ", status, error);
                    }
                });
            }
        },
        error: function(xhr, status, error) {
            $("#truyen-list-new").text("Lỗi khi tải danh sách truyện mới: " + error);
            console.log("Lỗi API /api/truyen/moi: ", status, error);
        }
    });

    // Hàm cuộn danh sách truyện (cho truyện nổi bật)
    window.scrollTruyenList = function(direction) {
        const list = document.getElementById("truyen-list-hot");
        const scrollAmount = 300;
        if (direction === "left") {
            list.scrollBy({ left: -scrollAmount, behavior: "smooth" });
        } else {
            list.scrollBy({ left: scrollAmount, behavior: "smooth" });
        }
    };

    // Hàm chuyển đổi trạng thái yêu thích
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

        $(`[id$="truyen-${truyenId}"]`).each(function() {
            const newButtonHtml = isAdding
                ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`
                : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`;
            $(this).find(".favorite-btn").replaceWith(newButtonHtml);
        });

        Toastify({
            text: isAdding ? "Đã thêm vào danh sách yêu thích!" : "Đã xóa khỏi danh sách yêu thích!",
            duration: 3000,
            gravity: "top",
            position: "right",
            backgroundColor: isAdding ? "#00b09b" : "#ff4444"
        }).showToast();

        $.ajax({
            url: url,
            method: method,
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function() {
                $.ajax({
                    url: `http://localhost:8080/api/truyen/favorite/status/${truyenId}`,
                    method: "GET",
                    headers: {
                        "Authorization": "Bearer " + token
                    },
                    success: function(isFavorited) {
                        const expectedState = isAdding;
                        if (isFavorited !== expectedState) {
                            $(`[id$="truyen-${truyenId}"]`).each(function() {
                                const correctedButtonHtml = isFavorited
                                    ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`
                                    : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`;
                                $(this).find(".favorite-btn").replaceWith(correctedButtonHtml);
                            });
                            Toastify({
                                text: "Trạng thái yêu thích đã được cập nhật từ server!",
                                duration: 3000,
                                gravity: "top",
                                position: "right",
                                backgroundColor: "#ff9800"
                            }).showToast();
                        }
                    },
                    error: function() {
                        Toastify({
                            text: "Lỗi khi kiểm tra trạng thái yêu thích!",
                            duration: 3000,
                            gravity: "top",
                            position: "right",
                            backgroundColor: "#ff4444"
                        }).showToast();
                    }
                });
            },
            error: function(xhr) {
                $(`[id$="truyen-${truyenId}"]`).each(function() {
                    const revertButtonHtml = isAdding
                        ? `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`
                        : `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`;
                    $(this).find(".favorite-btn").replaceWith(revertButtonHtml);
                });

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

    // Gắn sự kiện cho nút yêu thích
    $(document).on('click', '.favorite-btn', function() {
        const truyenId = $(this).data('truyen-id');
        const isAdding = $(this).hasClass('btn-outline-primary');
        toggleFavorite(truyenId, isAdding);
    });

    // Hàm viết tắt tên truyện
    function truncateText(text, maxLength = 20) {
        if (text.length > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }

    // Tìm kiếm gợi ý
    $("#search-input").on("input", function() {
        const query = $(this).val().trim();
        const suggestions = $("#search-suggestions");

        console.log("Searching with query:", query); // Debug

        if (query.length < 2) {
            suggestions.hide().empty();
            return;
        }

        $.ajax({
            url: `http://localhost:8080/api/truyen/search?query=${encodeURIComponent(query)}`,
            method: "GET",
            success: function(data) {
                console.log("API response:", data); // Debug
                suggestions.empty();
                if (Array.isArray(data) && data.length > 0) {
                    data.slice(0, 5).forEach(truyen => {
                        suggestions.append(`
                            <div class="suggestion-item" data-truyen-id="${truyen.id}" style="display: flex; align-items: center; padding: 8px; border-bottom: 1px solid #eee;">
                                <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
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
                console.log("Error in search API:", status, error); // Debug
                suggestions.hide();
            }
        });
    });

    // Chọn truyện từ danh sách gợi ý
    $(document).on("click", ".suggestion-item", function() {
        const truyenId = $(this).data("truyen-id");
        if (truyenId) {
            window.location.href = `/read/${truyenId}`;
        }
    });

    // Ẩn danh sách gợi ý khi click ra ngoài
    $(document).on("click", function(e) {
        if (!$(e.target).closest(".search-container").length) {
            $("#search-suggestions").hide();
        }
    });

    // Xử lý khi nhấn nút tìm kiếm
    $("#search-button").on("click", function(e) {
        e.preventDefault(); // Ngăn form submit mặc định
        const query = $("#search-input").val().trim();
        console.log("Search button clicked with query:", query); // Debug
        if (query) {
            window.location.href = `/view/search.html?query=${encodeURIComponent(query)}`;
        }
    });

    // Xử lý khi nhấn Enter trong ô tìm kiếm
    $("#search-input").on("keypress", function(e) {
        if (e.which === 13) { // Phím Enter
            e.preventDefault(); // Ngăn form submit mặc định
            const query = $(this).val().trim();
            console.log("Enter pressed with query:", query); // Debug
            if (query) {
                window.location.href = `/view/search.html?query=${encodeURIComponent(query)}`;
            }
        }
    });

    // Xử lý trang kết quả tìm kiếm (chỉ chạy trên search.html)
    if (window.location.pathname.includes("search.html")) {
        const urlParams = new URLSearchParams(window.location.search);
        const query = urlParams.get("query") || "";
        $("#search-input").val(decodeURIComponent(query));

        if (query) {
            $.ajax({
                url: `http://localhost:8080/api/truyen/search?query=${encodeURIComponent(query)}`,
                method: "GET",
                success: function(data) {
                    console.log("Search results:", data); // Debug
                    const searchResults = $("#search-results");
                    searchResults.empty();
                    if (Array.isArray(data) && data.length > 0) {
                        data.forEach(truyen => {
                            const timeAgo = Math.floor(Math.random() * 10) + 1 + " Phút Trước";
                            const isHot = Math.random() > 0.5 ? "Hot" : "";
                            const cardId = `search-truyen-${truyen.id}`;

                            if (token) {
                                $.ajax({
                                    url: `http://localhost:8080/api/truyen/favorite/status/${truyen.id}`,
                                    method: "GET",
                                    headers: {
                                        "Authorization": "Bearer " + token
                                    },
                                    success: function(isFavorited) {
                                        const buttonHtml = isFavorited
                                            ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyen.id}">Xóa khỏi yêu thích</button>`
                                            : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyen.id}">Thêm vào yêu thích</button>`;
                                        searchResults.append(`
                                            <div class="card" id="${cardId}">
                                                <div style="position: relative;">
                                                    <a href="/read/${truyen.id}">
                                                        <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                                             alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                                             style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                                                    </a>
                                                    <span style="position: absolute; top: 5px; left: 5px; background-color: ${isHot ? '#ff4444' : 'transparent'}; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${isHot}</span>
                                                    <span style="position: absolute; top: 5px; right: 5px; background-color: #00bcd4; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${timeAgo}</span>
                                                </div>
                                                <div style="padding: 10px; color: #fff;">
                                                    <h6 style="margin: 0; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${truyen.tenTruyen || 'Không có tiêu đề'}</h6>
                                                    ${buttonHtml}
                                                </div>
                                            </div>
                                        `);
                                    },
                                    error: function() {
                                        const buttonHtml = `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyen.id}">Thêm vào yêu thích</button>`;
                                        searchResults.append(`
                                            <div class="card" id="${cardId}">
                                                <div style="position: relative;">
                                                    <a href="/read/${truyen.id}">
                                                        <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                                             alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                                             style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                                                    </a>
                                                    <span style="position: absolute; top: 5px; left: 5px; background-color: ${isHot ? '#ff4444' : 'transparent'}; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${isHot}</span>
                                                    <span style="position: absolute; top: 5px; right: 5px; background-color: #00bcd4; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${timeAgo}</span>
                                                </div>
                                                <div style="padding: 10px; color: #fff;">
                                                    <h6 style="margin: 0; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${truyen.tenTruyen || 'Không có tiêu đề'}</h6>
                                                    ${buttonHtml}
                                                </div>
                                            </div>
                                        `);
                                    }
                                });
                            } else {
                                searchResults.append(`
                                    <div class="card" id="${cardId}">
                                        <div style="position: relative;">
                                            <a href="/read/${truyen.id}">
                                                <img src="${truyen.image_comic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                                     alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                                     style="width: 100%; height: 260px; object-fit: cover; border-bottom: 2px solid #fff;">
                                            </a>
                                            <span style="position: absolute; top: 5px; left: 5px; background-color: ${isHot ? '#ff4444' : 'transparent'}; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${isHot}</span>
                                            <span style="position: absolute; top: 5px; right: 5px; background-color: #00bcd4; color: #fff; padding: 2px 8px; border-radius: 5px; font-size: 12px;">${timeAgo}</span>
                                        </div>
                                        <div style="padding: 10px; color: #fff;">
                                            <h6 style="margin: 0; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${truyen.tenTruyen || 'Không có tiêu đề'}</h6>
                                        </div>
                                    </div>
                                `);
                            }
                        });
                    } else {
                        searchResults.html('<p style="color: #ff4444; padding: 20px;">Không tìm thấy kết quả nào.</p>');
                    }
                },
                error: function(xhr, status, error) {
                    console.log("Error in search results API:", status, error); // Debug
                    $("#search-results").html('<p style="color: #ff4444; padding: 20px;">Lỗi khi tải kết quả tìm kiếm: ' + error + '</p>');
                }
            });
        }
    }

    // Xử lý trang đọc truyện (chỉ chạy trên read.html)
    if (window.location.pathname.startsWith("/read")) {
        const truyenId = window.location.pathname.split("/").pop();
        let currentChapterId = null;

        // Lấy thông tin truyện
        $.ajax({
            url: `http://localhost:8080/api/truyen/${truyenId}`,
            method: "GET",
            success: function(truyen) {
                $("#comic-title").text(truyen.tenTruyen || "Không có tiêu đề");
                $("#image-container").prepend(`
                <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" alt="${truyen.tenTruyen || 'Bìa truyện'}" style="max-width: 100%; height: auto;">
                <p>Mô tả: ${truyen.moTa || 'Không có mô tả'}</p>
                <p>Lượt thích: ${truyen.luotThich || 0}</p>
                <p>Lượt xem: ${truyen.luotXem || 0}</p>
                <p>Ghi chú: ${truyen.ghiChu || 'Không có ghi chú'}</p>
                <p>Người dịch: ${truyen.translator?.username || 'Không có thông tin'}</p>
            `);
            },
            error: function(xhr, status, error) {
                $("#comic-title").text("Lỗi khi tải thông tin truyện: " + error);
                console.log("Lỗi API /api/truyen/{id}: ", status, error);
            }
        });

        // Lấy danh sách chapter
        $.ajax({
            url: `http://localhost:8080/api/chapters?id_comic=${truyenId}`,
            method: "GET",
            success: function(chapters) {
                const chapterList = $("#chapter-list");
                chapterList.empty();
                if (Array.isArray(chapters) && chapters.length > 0) {
                    chapters.forEach(chapter => {
                        chapterList.append(`
                        <button class="btn btn-outline-primary" data-chapter-id="${chapter.id}">
                            ${chapter.tenChap}
                        </button>
                    `);
                    });
                    // Chọn chapter đầu tiên mặc định
                    currentChapterId = chapters[0].id;
                    loadChapterImages(currentChapterId);
                } else {
                    chapterList.append('<p>Không có chapter nào.</p>');
                }
            },
            error: function(xhr, status, error) {
                $("#chapter-list").html('<p>Lỗi khi tải danh sách chapter: ' + error + '</p>');
                console.log("Lỗi API /api/chapters: ", status, error);
            }
        });

        // Gắn sự kiện cho nút chapter
        $(document).on("click", "#chapter-list button", function() {
            currentChapterId = $(this).data("chapter-id");
            $("#chapter-title").text($(this).text());
            loadChapterImages(currentChapterId);
        });

        // Hàm tải ảnh của chapter
        function loadChapterImages(chapterId) {
            const imageContainer = $("#chapter-images"); // Cập nhật ID
            imageContainer.empty();
            $.ajax({
                url: `http://localhost:8080/api/images?id_chapter=${chapterId}`,
                method: "GET",
                success: function(images) {
                    if (Array.isArray(images) && images.length > 0) {
                        images.sort((a, b) => a.pageNumber - b.pageNumber);
                        images.forEach(image => {
                            imageContainer.append(`
                        <img src="${image.imageUrl || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" alt="Ảnh trang ${image.pageNumber}">
                    `);
                        });
                    } else {
                        imageContainer.append('<p>Không có ảnh cho chapter này.</p>');
                    }
                },
                error: function(xhr, status, error) {
                    imageContainer.html('<p>Lỗi khi tải ảnh: ' + error + '</p>');
                    console.log("Lỗi API /api/images: ", status, error);
                }
            });
        }
    }
    // Hàm đăng xuất
    window.logout = function() {
        localStorage.removeItem("token");
        window.location.href = "/view/trangchu.html";
    };
});