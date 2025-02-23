
    $(document).ready(function(){
    $("#getUser").click(function(){
        $.ajax({
            url: "/test/1",
            type: "GET",
            dataType: "json",
            success: function(data) {
                $("#userInfo").html("Tên: " + data.username + "<br>Email: " + data.email);
            },
            error: function() {
                $("#userInfo").html("Không tìm thấy người dùng!");
            }
        });
    });
});
