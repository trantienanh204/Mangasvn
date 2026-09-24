
$(document).ready(function() {
    const token = localStorage.getItem("token");
    const userInfo = $("#user-info");
    const loginLink = $("#login-link");
    const logoutLink = $("#logout-link");

    const serverHost = window.location.origin;

    if (token) {
        $.ajax({
            url: `${serverHost}/api/auth/user-info`,
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(data) {
                userInfo.text("Xin chào, " + data.username);
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

    function escapeHtml(value) {
        return String(value ?? "").replace(/[&<>"']/g, character => ({
            "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
        })[character]);
    }

    function displayTruyen(container, truyen, token, isScrollable = false, prefix = '') {
        const cardId = `${prefix}truyen-${truyen.id}`;
        const title = escapeHtml(truyen.tenTruyen || "Chưa có tên truyện");
        const cover = escapeHtml(truyen.imageComic || "https://i.postimg.cc/zBZ7k81R/cass.jpg");
        const badge = isScrollable ? '<span class="comic-badge">ĐANG ĐƯỢC QUAN TÂM</span>' : '';
        const favoriteButton = token
            ? `<button class="favorite-btn" type="button" data-truyen-id="${Number(truyen.id)}" aria-label="Thêm truyện vào yêu thích">♡</button>`
            : '';

        container.append(`
            <article class="comic-card ${isScrollable ? 'comic-card-featured' : ''}" id="${cardId}">
                <a class="comic-cover-link" href="/truyen/${Number(truyen.id)}" aria-label="Đọc ${title}">
                    <img class="comic-cover" src="${cover}" alt="Bìa truyện ${title}" loading="lazy">
                    ${badge}
                </a>
                <div class="comic-card-info">
                    <a class="comic-title" href="/truyen/${Number(truyen.id)}">${title}</a>
                    <span class="comic-meta">MangaSVN · Truyện tranh</span>
                    ${favoriteButton}
                </div>
            </article>
        `);

        if (!token) return;
        $.ajax({
            url: `${serverHost}/api/truyen/favorite/status/${Number(truyen.id)}`,
            method: "GET",
            headers: { "Authorization": "Bearer " + token },
            success: function(isFavorited) {
                const button = $(`#${cardId} .favorite-btn`);
                button.toggleClass("is-favorite", Boolean(isFavorited));
                button.attr("aria-label", isFavorited ? "Đã thêm vào yêu thích" : "Thêm truyện vào yêu thích");
            }
        });
    }

    $.ajax({
        url: `${serverHost}/api/truyen/hot`,
        method: "GET",
        success: function(data) {
            const carouselInner = $("#hot-truyen-carousel");
            if (Array.isArray(data) && data.length > 0) {
                data.forEach((truyen, index) => {
                    const isActive = index === 0 ? "active" : "";
                    const title = escapeHtml(truyen.tenTruyen || "Truyện nổi bật");
                    const description = escapeHtml(truyen.moTa || "Khám phá chương mới và tiếp tục hành trình của bạn.");
                    const cover = escapeHtml(truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg');
                    const comicId = Number(truyen.id);
                    carouselInner.append(`
                        <div class="carousel-item ${isActive}">
                            <div class="hot-slide">
                                <img src="${cover}"
                                     class="hot-cover" alt="Bìa truyện ${title}">
                                <div class="hot-copy">
                                    <span class="eyebrow">MANGASVN · TRUYỆN NỔI BẬT</span>
                                    <h2 class="text-title">${title}</h2>
                                    <p class="text-content">${description}</p>
                                    <a href="/truyen/${comicId}" class="btn btn-primary">Đọc ngay <span aria-hidden="true">→</span></a>
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
            console.log("Lỗi API truyen hot: ", status, error);
        }
    });

    $.ajax({
        url: `${serverHost}/api/truyen/hot`,
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
            console.log("Lỗi API truyen hot: ", status, error);
        }
    });

    let currentPage = 0;
    let globalTotalPages = 0;

    function loadTruyen(page = 0) {
        currentPage = page;
        const truyenList = $("#truyen-list");
        truyenList.html('<div class="loading">Đang tải...</div>'); // Nếu có spinner

        $.ajax({
            url: `${serverHost}/api/truyen/list?page=${page}`,
            method: "GET",
            success: function(response) {
                truyenList.empty();
                if (response.content && Array.isArray(response.content)) {
                    response.content.forEach(truyen => {
                        displayTruyen(truyenList, truyen, token, false, 'list-');
                    });
                    globalTotalPages = response.totalPages; // Lưu totalPages global
                    renderPagination(response.totalPages, response.number);
                    window.scrollTo(0, 0);
                }
            },
            error: function() {
                truyenList.html('<p>Lỗi tải dữ liệu</p>');
            }
        });
    }

    function jumpToPage() {
        const jumpInput = $("#jump-page").val();
        if (!jumpInput || isNaN(jumpInput)) {
            alert("Vui lòng nhập số trang hợp lệ!");
            return;
        }

        const page = parseInt(jumpInput) - 1;
        if (page >= 0 && page < globalTotalPages) {
            loadTruyen(page);
            $("#jump-page").val('');
        } else {
            alert(`Trang không hợp lệ! (Phải từ 1 đến ${globalTotalPages})`);
        }
    }

    window.jumpToPage = jumpToPage;
    loadTruyen(0);
    function renderPagination(totalPages, activePage) {
        const container = $("#pagination-container");
        const pageInfo = $("#page-info");
        container.empty();
        pageInfo.empty();

        if (totalPages <= 1) return;

        pageInfo.text(`Trang ${activePage + 1} / ${totalPages}`);
        container.append(`<button ${activePage === 0 ? 'disabled' : ''} onclick="loadTruyen(0)">Đầu</button>`);
        const prevDisabled = activePage === 0 ? "disabled" : "";
        container.append(`<button ${prevDisabled} onclick="loadTruyen(${activePage - 1})">Trước</button>`);
        let startPage = Math.max(0, activePage - 2);
        let endPage = Math.min(totalPages, activePage + 3);
        if (startPage > 1) {
            container.append(`<button onclick="loadTruyen(0)">1</button>`);
            if (startPage > 2) container.append(`<span>...</span>`); // Ellipsis
        }
        for (let i = startPage; i < endPage; i++) {
            const activeClass = i === activePage ? "active" : "";
            container.append(`<button class="${activeClass}" onclick="loadTruyen(${i})">${i + 1}</button>`);
        }
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) container.append(`<span>...</span>`); // Ellipsis
            container.append(`<button onclick="loadTruyen(${totalPages - 1})">${totalPages}</button>`);
        }
        const nextDisabled = activePage === totalPages - 1 ? "disabled" : "";
        container.append(`<button ${nextDisabled} onclick="loadTruyen(${activePage + 1})">Sau</button>`);
        container.append(`<button ${activePage === totalPages - 1 ? 'disabled' : ''} onclick="loadTruyen(${totalPages - 1})">Cuối</button>`);
    }


    window.loadTruyen = loadTruyen;
    $.ajax({
        url: `${serverHost}/api/truyen/moi`,
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
                    url: `${serverHost}/api/truyen/list`,
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

    window.scrollTruyenList = function(direction) {
        const list = document.getElementById("truyen-list-hot");
        const scrollAmount = 300;
        if (direction === "left") {
            list.scrollBy({ left: -scrollAmount, behavior: "smooth" });
        } else {
            list.scrollBy({ left: scrollAmount, behavior: "smooth" });
        }
    };

    if (window.location.pathname.startsWith("/truyen")) {
        const truyenId = window.location.pathname.split("/").pop();

        $.ajax({
            url: `${serverHost}/api/chapters?id_comic=${truyenId}`,
            method: "GET",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token")
            },
            success: function(chapters) {
                const chapterList = $("#chapter-list");
                chapterList.empty();

                if (Array.isArray(chapters) && chapters.length > 0) {
                    chapters.forEach(chapter => {
                        chapterList.append(`
                            <button class="btn btn-outline-primary" data-chapter-id="${chapter.id}">
                                ${chapter.tenChap || `Chapter ${chapter.id}`}
                            </button>
                        `);
                    });
                    $("#chapter-title").text(chapters[0].tenChap || `Chapter ${chapters[0].id}`);
                } else {
                    chapterList.append('<p>Không có chapter nào.</p>');
                    const imageSection = document.getElementById('image-section');
                    if (imageSection) {
                        imageSection.hidden = true;
                    }
                }
            },
            error: function(xhr, status, error) {
                $("#chapter-list").html('<p>Lỗi khi tải danh sách chapter: ' + (xhr.responseText || error) + '</p>');
                if (xhr.status === 401 || xhr.status === 403) {
                    Toastify({
                        text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        style: { background: "#ff4444" }
                    }).showToast();
                    localStorage.removeItem("token");
                    window.location.href = "/login_dangnhap.html";
                }
            }
        });
    }

    window.logout = function() {
        localStorage.removeItem("token");
        window.location.href = "/view/trangchu.html";
    };
});
document.addEventListener("DOMContentLoaded", checkAdminAccess);
async function checkAdminAccess() {
    const token = localStorage.getItem("token");
    const adminButtonContainer = document.getElementById("admin-button-container");

    if (!token) {
        adminButtonContainer.innerHTML = "";
        return;
    }

    try {
        const userInfoResponse = await fetch(`${window.location.origin}/api/auth/user-info`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!userInfoResponse.ok) {
            throw new Error("Phiên đăng nhập hết hạn hoặc không hợp lệ.");
        }

        const userInfo = await userInfoResponse.json();
        const userRole = userInfo.role;

        if (userRole === "ADMIN" || userRole === "CHUTUT") {
            adminButtonContainer.innerHTML = `
                    <button id="goToAdmin">Truy cập Admin</button>
                `;
            document.getElementById("goToAdmin").addEventListener("click", function() {
                window.location.href = "/view/admin";
            });
        } else {
            adminButtonContainer.innerHTML = "";
        }
    } catch (error) {
        console.error("Lỗi khi kiểm tra quyền truy cập:", error);
        adminButtonContainer.innerHTML = "";
    }
}
