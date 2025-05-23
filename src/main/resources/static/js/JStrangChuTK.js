// Thêm vào cuối file search.js
$(document).ready(function() {
    const token = localStorage.getItem("token");

    // Lấy từ khóa từ URL
    const urlParams = new URLSearchParams(window.location.search);
    const query = urlParams.get("query") || "";
    $("#search-input").val(decodeURIComponent(query));

    // Hiển thị kết quả tìm kiếm
    if (query) {
        $.ajax({
            url: `http://localhost:8080/api/truyen/search?query=${encodeURIComponent(query)}`,
            method: "GET",
            success: function(data) {
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
                $("#search-results").html('<p style="color: #ff4444; padding: 20px;">Lỗi khi tải kết quả tìm kiếm: ' + error + '</p>');
                console.log("Lỗi API /api/truyen/search: ", status, error);
            }
        });
    }

    // Gắn sự kiện cho nút yêu thích (nếu cần)
    $(document).on('click', '.favorite-btn', function() {
        const truyenId = $(this).data('truyen-id');
        const isAdding = $(this).hasClass('btn-outline-primary');
        toggleFavorite(truyenId, isAdding);
    });

    function toggleFavorite(truyenId, isAdding) {
        const token = localStorage.getItem("token");
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
});