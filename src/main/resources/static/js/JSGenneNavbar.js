const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.1.19:8080";
$(document).ready(function () {

    $.ajax({
        url: `${serverHost}/theloai/fill/AllGenre`,
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


        if (!genresList) return;

        genresList.innerHTML = '';

        listGenres.forEach(item => {
            const rawName = item.tenDanhMuc;
            if (item.trangThai === false) return;
            const slug = encodeURIComponent(rawName);
            const li = document.createElement('li');
            li.innerHTML = `<a href="${serverHost}/theloai/${slug}" style="text-transform: capitalize;">${rawName}</a>`;
            genresList.appendChild(li);
        });
    }
    const dropdownNavItem = document.querySelector('.nav-item.dropdown');

    if (dropdownNavItem) {
        const dropdownMenu = dropdownNavItem.querySelector('.genres-menu');
        const dropdownToggle = dropdownNavItem.querySelector('a.nav-link'); // Lấy cái nút "Thể loại"

        if (dropdownMenu && dropdownToggle) {

            // 1. Logic cho PC (Hover)
            // Chỉ kích hoạt khi màn hình lớn hơn 992px (Laptop/PC)
            const isDesktop = () => window.innerWidth > 992;
            let timeoutId;

            const showMenu = () => {
                if (!isDesktop()) return; // Bỏ qua nếu là mobile
                clearTimeout(timeoutId);
                dropdownMenu.classList.add('show');
            };

            const hideMenu = () => {
                if (!isDesktop()) return; // Bỏ qua nếu là mobile
                timeoutId = setTimeout(() => {
                    dropdownMenu.classList.remove('show');
                }, 200);
            };

            dropdownNavItem.addEventListener('mouseenter', showMenu);
            dropdownNavItem.addEventListener('mouseleave', hideMenu);

            // 2. Logic cho Mobile (Click / Tap)
            // Ngăn chặn hành vi mặc định khi click vào nút "Thể loại" trên mobile
            dropdownToggle.addEventListener('click', function(e) {
                if (!isDesktop()) {
                    e.preventDefault(); // Chặn việc load lại trang hoặc nhảy link
                    e.stopPropagation(); // Ngăn sự kiện nổi bọt

                    // Toggle class show
                    dropdownMenu.classList.toggle('show');
                }
            });

            // 3. Logic Click Outside (Bấm ra ngoài thì đóng menu - Cực quan trọng cho UX Mobile)
            document.addEventListener('click', function(e) {
                // Nếu menu đang mở và vị trí click KHÔNG nằm trong dropdownNavItem
                if (dropdownMenu.classList.contains('show') && !dropdownNavItem.contains(e.target)) {
                    dropdownMenu.classList.remove('show');
                }
            });

            // 4. Đóng menu khi click vào 1 thể loại bất kỳ (để chuyển trang)
            dropdownMenu.addEventListener('click', function() {
                dropdownMenu.classList.remove('show');
            });
        }
    }
});