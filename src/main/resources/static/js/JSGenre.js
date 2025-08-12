$(document).ready(function () {
    const token = localStorage.getItem("token")
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.238.147:8080";

    if (token){
        $.ajax({
            url:`{serverHost}/theloai`
        })
    }
})