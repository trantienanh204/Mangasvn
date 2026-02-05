
$(document).ready(function() {
    const token = localStorage.getItem("token");
    const userInfo = $("#user-info");
    const loginLink = $("#login-link");
    const logoutLink = $("#logout-link");

    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.1.19:8080";
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

    function displayTruyen(container, truyen, token, isScrollable = false, prefix = '') {
        const timeAgo = Math.floor(Math.random() * 10) + 1 + " Phút Trước";
        const isHot = Math.random() > 0.5 ? "Hot" : "";
        const cardId = `${prefix}truyen-${truyen.id}`;

        if (token) {
            $.ajax({
                url: `${serverHost}/api/truyen/favorite/status/${truyen.id}`,
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
                                    <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
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
                                    <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
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
                            <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}" 
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

    $.ajax({
        url: `${serverHost}/api/truyen/hot`,
        method: "GET",
        success: function(data) {
            const carouselInner = $("#hot-truyen-carousel");
            if (Array.isArray(data) && data.length > 0) {
                data.forEach((truyen, index) => {
                    const isActive = index === 0 ? "active" : "";
                    carouselInner.append(`
                        <div class="carousel-item ${isActive}">
                            <div class="d-flex align-items-center" style="width: 100%; height: 300px; overflow: hidden;">
                                <img src="${truyen.imageComic || 'https://i.postimg.cc/zBZ7k81R/cass.jpg'}"
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

    if (window.location.pathname.startsWith("/read")) {
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
                    window.location.href = "/login.html";
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
        const userInfoResponse = await fetch("http://localhost:8080/api/auth/user-info", {
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
            document.getElementById("goToAdmin").addEventListener("click", async function() {
                try {
                    const response = await fetch("http://localhost:8080/api/admin", {
                        method: "GET",
                        headers: { "Authorization": `Bearer ${token}` }
                    });

                    if (!response.ok) {
                        throw new Error("❌ Bạn không có quyền truy cập.");
                    }

                    window.location.href = "http://localhost:8080/api/admin";
                } catch (error) {
                    alert(error.message);
                }
            });
        } else {
            adminButtonContainer.innerHTML = "";
        }
    } catch (error) {
        console.error("Lỗi khi kiểm tra quyền truy cập:", error);
        adminButtonContainer.innerHTML = "";
    }
}