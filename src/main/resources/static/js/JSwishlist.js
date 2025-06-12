$(document).ready(function() {
    const token = localStorage.getItem("token");
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.238.147:8080";
    if (token) {
        $.ajax({
            url: `${serverHost}/api/auth/user-info`,
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(data) {
                $('#login-link').hide();
                $('#logout-link').show();
                $('#user-info').text("Xin chào, " + data.username);
                console.log("Đăng nhập thành công với user:", data.username);
                wishlist();
            },
            error: function(xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    localStorage.removeItem("token");
                    $('#login-link').show();
                    $('#logout-link').hide();
                    $('#user-info').text("");
                    $('#favourite-list').html('<p style="color: #ff4444; padding: 10px;">Vui lòng đăng nhập để xem danh sách yêu thích.</p>');
                    Toastify({
                        text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        style: { background: "#ff4444" }
                    }).showToast();
                    setTimeout(() => {
                        window.location.href = "/login.html";
                    }, 3000);
                }
            }
        });
    } else {
        $('#login-link').show();
        $('#logout-link').hide();
        $('#user-info').text("");
        $('#favourite-list').html('<p style="color: #ff4444; padding: 10px;">Vui lòng đăng nhập để xem danh sách yêu thích.</p>');
    }

    function wishlist() {
        if (!token) {
            $('#favourite-list').html('<p style="color: #ff4444; padding: 10px;">Vui lòng đăng nhập để xem danh sách yêu thích.</p>');
            return;
        }

        $.ajax({
            url: `${serverHost}/api/truyen/favorite/list`,
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(data) {
                const favouriteList = $('#favourite-list');
                favouriteList.empty();
                if (Array.isArray(data) && data.length > 0) {
                    data.forEach(truyen => {
                        displayTruyen(favouriteList, truyen, token, false, 'favourite-');
                    });
                } else {
                    favouriteList.html('<p style="color: #ff4444; padding: 10px;">Bạn chưa có truyện yêu thích nào.</p>');
                }
            },
            error: function(xhr, status, error) {
                console.log("Lỗi khi tải danh sách yêu thích:", status, error);
                $('#favourite-list').html('<p style="color: #ff4444; padding: 10px;">Lỗi khi tải danh sách yêu thích. Vui lòng thử lại sau.</p>');
                if (xhr.status === 401 || xhr.status === 403) {
                    Toastify({
                        text: "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!",
                        duration: 3000,
                        gravity: "top",
                        position: "right",
                        style: { background: "#ff4444" }
                    }).showToast();
                    localStorage.removeItem("token");
                    setTimeout(() => {
                        window.location.href = "/login.html";
                    }, 3000);
                }
            }
        });
    }
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

        const url = `${serverHost}/api/truyen/favorite/${truyenId}`;
        const method = isAdding ? "POST" : "DELETE";

        const cardId = `favourite-truyen-${truyenId}`; // Giả định prefix là 'favourite-'

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

                if (!isAdding) {
                    // Xóa card khỏi DOM khi xóa thành công
                    $(`#${cardId}`).remove();
                    // Kiểm tra nếu danh sách rỗng, hiển thị thông báo
                    if ($('#favourite-list .custom-card').length === 0) {
                        $('#favourite-list').html('<p style="color: #ff4444; padding: 10px;">Bạn chưa có truyện yêu thích nào.</p>');
                    }
                }
            },
            error: function(xhr) {
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


    window.logout = function() {
        localStorage.removeItem("token");
        window.location.href = "/view/trangchu.html";
    };
});