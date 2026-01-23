$(document).ready(function () {
    $.ajax({
        url: "http://localhost:8080/theloai/fill/AllGenre",
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        success: function (data) {
            renderGenres(data);
        },
        error: function (err) {
            console.error("Lỗi tải danh mục:", err);
        }
    });

    function renderGenres(listGenres) {
        const genresList = document.getElementById('genres-list');

        // Kiểm tra nếu tìm thấy element
        if (!genresList) return;

        genresList.innerHTML = ''; // Xóa nội dung cũ

        listGenres.forEach(item => {
            const rawName = item.tenDanhMuc;
            if (item.trangThai === false) return;
            const slug = encodeURIComponent(rawName);
            const li = document.createElement('li');
            li.innerHTML = `<a href="/theloai/${slug}" style="text-transform: capitalize;">${rawName}</a>`;
            genresList.appendChild(li);
        });
    }
    const dropdownNavItem = document.querySelector('.nav-item.dropdown');

    if (dropdownNavItem) {
        const dropdownMenu = dropdownNavItem.querySelector('.genres-menu');

        if (dropdownMenu) {
            let timeoutId;

            const showMenu = () => {
                clearTimeout(timeoutId);
                dropdownMenu.classList.add('show');
            };

            const hideMenu = () => {
                timeoutId = setTimeout(() => {
                    dropdownMenu.classList.remove('show');
                }, 200);
            };

            dropdownNavItem.addEventListener('mouseenter', showMenu);
            dropdownNavItem.addEventListener('mouseleave', hideMenu);

            dropdownMenu.addEventListener('mouseenter', showMenu);
            dropdownMenu.addEventListener('mouseleave', hideMenu);
        }
    }
});