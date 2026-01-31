document.getElementById("login-form").addEventListener("submit", async function (event) {
    event.preventDefault();
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.1.32:8080";
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value;
    const password1 = document.getElementById("password1").value;
    const email = document.getElementById("email").value.trim();
    const errorElement = document.getElementById("error");

    errorElement.style.display = "none";
    errorElement.innerText = "";

    if (!username || !password || !password1 || !email) {
        errorElement.innerText = "Vui lòng điền đầy đủ thông tin!";
        errorElement.style.display = "block";
        setTimeout(() => {
            errorElement.style.display = "none";
            errorElement.innerText = "";
        }, 3000);
        return;
    }

    const usernameRegex = /^[a-zA-Z0-9_]+$/;
    if (!usernameRegex.test(username)) {
        errorElement.innerText = "Tên người dùng chỉ được chứa chữ cái, số và dấu gạch dưới!";
        errorElement.style.display = "block";
        setTimeout(() => {
            errorElement.style.display = "none";
            errorElement.innerText = "";
        }, 3000);
        return;
    }

    const passwordRegex = /^[a-zA-Z0-9@!_-]+$/;
    if (!passwordRegex.test(password) || !passwordRegex.test(password1)) {
        errorElement.innerText = "Mật khẩu chỉ được chứa chữ cái, số và ký tự @, !, -, _!";
        errorElement.style.display = "block";
        setTimeout(() => {
            errorElement.style.display = "none";
            errorElement.innerText = "";
        }, 3000);
        return;
    }

    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(email)) {
        errorElement.innerText = "Email không hợp lệ!";
        errorElement.style.display = "block";
        setTimeout(() => {
            errorElement.style.display = "none";
            errorElement.innerText = "";
        }, 3000);
        return;
    }

    if (password !== password1) {
        errorElement.innerText = "Mật khẩu xác nhận không khớp!";
        errorElement.style.display = "block";
        setTimeout(() => {
            errorElement.style.display = "none";
            errorElement.innerText = "";
        }, 3000);
        return;
    }

    try {
        const response = await fetch(`${serverHost}/api/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                userName: username,
                password: password,
                email: email
            }),
        });

        let data;
        try {
            data = await response.json();
        } catch (jsonError) {
            data = { message: await response.text() };
        }

        if (response.ok) {
            errorElement.innerText = data.message || "Đăng ký thành công!";
            errorElement.style.display = "block";
            setTimeout(() => {
                errorElement.style.display = "none";
                errorElement.innerText = "";
                window.location.href = "/api/auth/login";
            }, 3000);
        } else {
            errorElement.innerText = data.message || "Đăng ký thất bại, vui lòng thử lại!";
            errorElement.style.display = "block";
            setTimeout(() => {
                errorElement.style.display = "none";
                errorElement.innerText = "";
            }, 3000);
        }
    } catch (error) {
        errorElement.innerText = "Có lỗi xảy ra, vui lòng kiểm tra kết nối và thử lại!";
        errorElement.style.display = "block";
        setTimeout(() => {
            errorElement.style.display = "none";
            errorElement.innerText = "";
        }, 3000);
    }
});

 document.getElementById("username").addEventListener("input", function () {
    const errorElement = document.getElementById("error");
    errorElement.style.display = "none";
    errorElement.innerText = "";
 });

 document.getElementById("password").addEventListener("input", function () {
    const errorElement = document.getElementById("error");
    errorElement.style.display = "none";
    errorElement.innerText = "";
 });

 document.getElementById("email").addEventListener("input", function () {
    const errorElement = document.getElementById("error");
    errorElement.style.display = "none";
    errorElement.innerText = "";
 });

 document.getElementById("password1").addEventListener("input", function () {
    const errorElement = document.getElementById("error");
    errorElement.style.display = "none";
    errorElement.innerText = "";
 });