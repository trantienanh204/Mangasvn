// JStrangChu.js
$(document).ready(function() {
    const token = localStorage.getItem("token");
    const userInfo = $("#user-info");
    const loginLink = $("#login-link");
    const logoutLink = $("#logout-link");
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.156.147:8080";
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

        console.log(`Displaying truyen: ID=${truyen.id}, TenTruyen=${truyen.tenTruyen}, Prefix=${prefix}`);

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
            console.log("Lỗi API /api/truyen/hot: ", status, error);
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
            console.log("Lỗi API /api/truyen/hot: ", status, error);
        }
    });

    $.ajax({
        url: `${serverHost}/api/truyen/list`,
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
                console.log("Phản hồi API /api/chapters:", chapters);
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
                }
            },
            error: function(xhr, status, error) {
                $("#chapter-list").html('<p>Lỗi khi tải danh sách chapter: ' + (xhr.responseText || error) + '</p>');
                console.log("Lỗi API /api/chapters: ", status, error, xhr.responseText);
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

    async function checkAdminAccess() {
        const token = localStorage.getItem("token");
        const adminButtonContainer = document.getElementById("admin-button-container");

        if (!token) {
            adminButtonContainer.innerHTML = "";
            return;
        }

        try {
            const userInfoResponse = await fetch(`${serverHost}/api/auth/user-info`, {
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
                        const response = await fetch(`${serverHost}/api/admin`, {
                            method: "GET",
                            headers: { "Authorization": `Bearer ${token}` }
                        });

                        if (!response.ok) {
                            throw new Error("❌ Bạn không có quyền truy cập.");
                        }

                        window.location.href = `${serverHost}/api/admin`;
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
    document.addEventListener("DOMContentLoaded", checkAdminAccess);

    window.logout = function() {
        localStorage.removeItem("token");
        window.location.href = "/view/trangchu.html";
    };
});