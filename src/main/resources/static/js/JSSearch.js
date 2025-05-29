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

                            const correctedButtonHtml = isFavorited
                                ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`
                                : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`;
                            $("#favorite-button-container").html(correctedButtonHtml);

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

                const revertButtonHtml = isAdding
                    ? `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}">Thêm vào yêu thích</button>`
                    : `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}">Xóa khỏi yêu thích</button>`;
                $("#favorite-button-container").html(revertButtonHtml);

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
});