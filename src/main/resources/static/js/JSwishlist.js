$(document).ready(function() {
    const token = localStorage.getItem("token");
    if (token) {
        $.ajax({
            url: "http://localhost:8080/api/auth/user-info",
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(data) {
                $('#login-link').hide();
                $('#logout-link').show();
                $('#user-info').text("Xin chào, " + data.username);
                console.log("Đăng nhập thành công với user:", data.username); // Thêm log
            },
            error: function(xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    localStorage.removeItem("token");
                    $('#login-link').show();
                    $('#logout-link').hide();
                    $('#user-info').text("");
                }
            }
        });
    } else {
        $('#login-link').show();
        $('#logout-link').hide();
        $('#user-info').text("");
    }
});