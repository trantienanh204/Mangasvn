$(document).ready(function() {
    const serverHost = window.location.origin;

    function updateFavoriteUI(truyenId, isFavorited) {
        // 1. Update round heart buttons on cards (homepage, category, search, etc.)
        $(`.favorite-btn[data-truyen-id="${truyenId}"]`).each(function() {
            const $btn = $(this);
            if (!$btn.hasClass('btn')) {
                $btn.toggleClass('is-favorite', Boolean(isFavorited));
                $btn.attr('aria-label', isFavorited ? 'Đã thêm vào yêu thích' : 'Thêm truyện vào yêu thích');
            }
        });

        // 2. Update detail page action button if present
        const $detailContainer = $("#favorite-button-container");
        if ($detailContainer.length > 0) {
            const detailHtml = isFavorited
                ? `<button class="btn btn-sm btn-outline-danger favorite-btn" data-truyen-id="${truyenId}"><i class="fa-solid fa-heart me-1"></i> Xóa khỏi yêu thích</button>`
                : `<button class="btn btn-sm btn-outline-primary favorite-btn" data-truyen-id="${truyenId}"><i class="fa-regular fa-heart me-1"></i> Thêm vào yêu thích</button>`;
            $detailContainer.html(detailHtml);
        }
    }

    function toggleFavorite(truyenId, isAdding) {
        const token = localStorage.getItem("token");
        if (!token) {
            Toastify({
                text: "Vui lòng đăng nhập để quản lý danh sách yêu thích!",
                duration: 2500,
                gravity: "top",
                position: "right",
                style: { background: "#ef4444" }
            }).showToast();
            setTimeout(() => {
                window.location.href = "/login";
            }, 1200);
            return;
        }

        const url = `${serverHost}/api/truyen/favorite/${truyenId}`;
        const method = isAdding ? "POST" : "DELETE";
        const cardId = `favourite-truyen-${truyenId}`;
        const isFavouritePage = window.location.pathname.includes('/view/favourite') || window.location.pathname.includes('/wishlist');

        // Optimistically update UI
        updateFavoriteUI(truyenId, isAdding);

        $.ajax({
            url: url,
            method: method,
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(response) {
                Toastify({
                    text: isAdding ? "Đã thêm vào danh sách yêu thích!" : "Đã xóa khỏi danh sách yêu thích!",
                    duration: 2500,
                    gravity: "top",
                    position: "right",
                    style: { background: isAdding ? "#06b6d4" : "#ef4444" }
                }).showToast();

                if (!isAdding && isFavouritePage) {
                    $(`#${cardId}`).fadeOut(250, function() {
                        $(this).remove();
                        if ($('#favourite-list .library-item').length === 0) {
                            $('#favourite-list').html('<p style="color: #94a3b8; padding: 20px; text-align: center;">Bạn chưa có truyện yêu thích nào.</p>');
                        }
                    });
                }
            },
            error: function(xhr) {
                // Revert UI on failure
                updateFavoriteUI(truyenId, !isAdding);

                if (xhr.status === 401) {
                    Toastify({
                        text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                        duration: 2500,
                        gravity: "top",
                        position: "right",
                        style: { background: "#ef4444" }
                    }).showToast();
                    localStorage.removeItem("token");
                    setTimeout(() => {
                        window.location.href = "/login";
                    }, 1200);
                } else {
                    const errorMsg = xhr.responseText || (isAdding ? "Lỗi khi thêm vào danh sách yêu thích!" : "Lỗi khi xóa khỏi danh sách yêu thích!");
                    Toastify({
                        text: errorMsg,
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        style: { background: "#ef4444" }
                    }).showToast();
                }
            }
        });
    }

    // Expose toggleFavorite globally for inline onclick handlers
    window.toggleFavorite = toggleFavorite;

    $(document).on('click', '.favorite-btn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        const $btn = $(this);
        const truyenId = $btn.data('truyen-id');
        if (!truyenId) return;

        let isAdding;
        if ($btn.hasClass('btn-outline-primary')) {
            isAdding = true;
        } else if ($btn.hasClass('btn-outline-danger')) {
            isAdding = false;
        } else {
            // Heart button on comic card: if currently favorite, clicking will remove; else add
            isAdding = !$btn.hasClass('is-favorite');
        }

        toggleFavorite(truyenId, isAdding);
    });

    function truncateText(text, maxLength = 26) {
        if (!text) return "Không có tiêu đề";
        if (text.length > maxLength) {
            return text.substring(0, maxLength) + "...";
        }
        return text;
    }

    $("#search-form").on("submit", function(e) {
        e.preventDefault();
        const query = $("#search-input").val().trim();
        if (query) {
            const firstItem = $("#search-suggestions .suggestion-item[data-truyen-id]").first();
            if (firstItem.length && firstItem.data("truyen-id")) {
                window.location.href = `/truyen/${firstItem.data("truyen-id")}`;
            }
        }
    });

    $("#search-input").on("input", function() {
        const query = $(this).val().trim();
        const suggestions = $("#search-suggestions");

        if (query.length < 2) {
            suggestions.hide().empty();
            return;
        }

        $.ajax({
            url: `${serverHost}/api/truyen/search?query=${encodeURIComponent(query)}`,
            method: "GET",
            success: function(data) {
                suggestions.empty();
                if (Array.isArray(data) && data.length > 0) {
                    data.slice(0, 6).forEach(truyen => {
                        suggestions.append(`
                            <div class="suggestion-item d-flex align-items-center gap-3 p-2" data-truyen-id="${truyen.id}" style="cursor: pointer; border-bottom: 1px solid rgba(255,255,255,0.06); transition: background 0.15s ease;">
                                <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
                                     alt="${truyen.tenTruyen || 'Không có tiêu đề'}" 
                                     style="width: 42px; height: 58px; object-fit: cover; border-radius: 6px; flex-shrink: 0; box-shadow: 0 2px 8px rgba(0,0,0,0.5);">
                                <div style="min-width: 0; flex: 1;">
                                    <div class="fw-bold text-truncate text-light" style="font-size: 0.875rem;">
                                        ${truncateText(truyen.tenTruyen || 'Không có tiêu đề', 36)}
                                    </div>
                                    <div class="small text-muted text-truncate" style="font-size: 0.75rem;">MangaSVN · Đọc ngay</div>
                                </div>
                            </div>
                        `);
                    });
                    suggestions.show();
                } else {
                    suggestions.append('<div class="suggestion-item p-3 text-center text-muted small"><i class="fa-solid fa-circle-exclamation me-1 text-warning"></i> Không tìm thấy kết quả nào.</div>');
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
            window.location.href = `/truyen/${truyenId}`;
        }
    });

    $(document).on("click", function(e) {
        if (!$(e.target).closest(".search-container").length) {
            $("#search-suggestions").hide();
        }
    });
});
