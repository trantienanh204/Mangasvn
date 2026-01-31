document.getElementById("login-form").addEventListener("submit", async function (event) {
    event.preventDefault();
    const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.1.32:8080";
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const errorElement = document.getElementById("error");


    errorElement.style.display = "none";
    errorElement.innerText = "";

    try {

        const response = await fetch(`${serverHost}/api/auth/login`, {

        method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                userName: username,
                password: password,
            }),
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem("token", data.token);
            window.location.href = "/view/trangchu.html";
        } else {

            errorElement.innerText = data.message || "Tài khoản hoặc mật khẩu không chính xác";
            errorElement.style.display = "block";


            setTimeout(() => {
                errorElement.style.display = "none";
                errorElement.innerText = "";
            }, 3000);
        }
    } catch (error) {

        errorElement.innerText = "Tài khoản hoặc mật khẩu không chính xác";
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