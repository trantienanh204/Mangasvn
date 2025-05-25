$(document).ready(function() {
    // Hàm phóng to ảnh khi click
    $(document).on('click', '.image-container img', function() {
        const src = $(this).attr('src');
        const img = `<img src="${src}" style="max-width: 90%; height: auto; display: block; margin: 20px auto;" onclick="this.style.display='none';">`;
        $('body').append(`<div style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 10000;" onclick="this.remove()">${img}</div>`);
    });

    // Hàm cuộn danh sách chapter
    window.scrollChapterList = function(direction) {
        const container = $('#chapter-list');
        const scrollAmount = 200;
        if (direction === 'left') {
            container.animate({ scrollLeft: container.scrollLeft() - scrollAmount }, 300);
        } else {
            container.animate({ scrollLeft: container.scrollLeft() + scrollAmount }, 300);
        }
    };

    // Xử lý nút Lên đầu trang
    $(window).scroll(function() {
        if ($(this).scrollTop() > 100) {
            $('#back-to-top').addClass('show');
        } else {
            $('#back-to-top').removeClass('show');
        }
    });

    $('#back-to-top').click(function() {
        $('html, body').animate({ scrollTop: 0 }, 300);
        return false;
    });

    // Chế độ đọc sách
    let currentImageIndex = 0;
    let images = [];

    window.loadChapterImages = function(chapterId) {
        const imageContainer = $("#chapter-images");
        imageContainer.empty();
        $.ajax({
            url: `http://localhost:8080/api/images?id_chapter=${chapterId}`,
            method: "GET",
            success: function(imagesData) {
                if (Array.isArray(imagesData) && imagesData.length > 0) {
                    imagesData.sort((a, b) => a.pageNumber - b.pageNumber);
                    images = imagesData.map(img => img.imageUrl || 'https://via.placeholder.com/800x600.png?text=Fallback+Image');
                    let displayedImages = images.slice(0, 15);
                    displayedImages.forEach((src, index) => {
                        imageContainer.append(`
                        <img src="${src}" alt="Ảnh trang ${index + 1}" onerror="this.src='https://via.placeholder.com/800x600.png?text=Error+Loading+Image';">
                    `);
                    });
                    if (images.length > 15) {
                        $('#load-all').show();
                        $('#load-15').show();
                    } else {
                        $('#load-all').hide();
                        $('#load-15').hide();
                    }
                } else {
                    imageContainer.append('<p>Không có ảnh cho chapter này.</p>');
                }
            },
            error: function(xhr, status, error) {
                imageContainer.html('<p>Lỗi khi tải ảnh: ' + error + '</p>');
                console.log("Lỗi API /api/images: ", status, error);
            }
        });
    };

    $('#toggle-reading-mode').click(function(e) {
        e.stopPropagation(); // Ngăn sự kiện click lan truyền lên document
        console.log("Nút Chuyển sang chế độ đọc sách đã được nhấn!");
        if ($('#chapter-images img').length === 0) {
            console.log("Không có ảnh trong #chapter-images!");
            alert('Vui lòng tải chapter trước khi chuyển sang chế độ đọc sách!');
            return;
        }
        images = $('#chapter-images img').map(function() {
            return $(this).attr('src');
        }).get();
        console.log("Danh sách ảnh:", images);
        if (images.length > 0) {
            currentImageIndex = 0;
            $('#reading-image')
                .attr('src', images[currentImageIndex])
                .on('error', function() {
                    console.log("Lỗi tải ảnh:", images[currentImageIndex]);
                    $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Loading+Image');
                })
                .on('load', function() {
                    console.log("Ảnh tải thành công:", images[currentImageIndex]);
                    const width = $('#reading-image').width();
                    const height = $('#reading-image').height();
                    console.log("Kích thước ảnh:", width + "x" + height);
                    if (width <= 0 || height <= 0) {
                        console.log("Ảnh không hiển thị do kích thước không hợp lệ, thay bằng placeholder");
                        $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Rendering+Image');
                    }
                });
            $('#reading-mode').addClass('active');
            console.log("Chuyển sang chế độ đọc sách, ảnh đầu tiên:", images[currentImageIndex]);
            console.log("Kiểm tra hiển thị #reading-mode:", $('#reading-mode').is(':visible'));
            console.log("Kiểm tra hiển thị #reading-image:", $('#reading-image').is(':visible'));
            console.log("Kiểm tra src của #reading-image:", $('#reading-image').attr('src'));
            updatePagination();
        } else {
            console.log("Không có ảnh nào được tải vào mảng images!");
        }
    });

    window.changePage = function(delta) {
        console.log("Chuyển trang, delta:", delta);
        currentImageIndex += delta;
        if (currentImageIndex < 0) currentImageIndex = 0;
        if (currentImageIndex >= images.length) currentImageIndex = images.length - 1;
        $('#reading-image')
            .attr('src', images[currentImageIndex])
            .on('error', function() {
                console.log("Lỗi tải ảnh:", images[currentImageIndex]);
                $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Loading+Image');
            })
            .on('load', function() {
                console.log("Ảnh tải thành công:", images[currentImageIndex]);
                const width = $('#reading-image').width();
                const height = $('#reading-image').height();
                console.log("Kích thước ảnh:", width + "x" + height);
                if (width <= 0 || height <= 0) {
                    console.log("Ảnh không hiển thị do kích thước không hợp lệ, thay bằng placeholder");
                    $(this).attr('src', 'https://via.placeholder.com/800x600.png?text=Error+Rendering+Image');
                }
            });
        console.log("Trang hiện tại:", currentImageIndex + 1, "Ảnh:", images[currentImageIndex]);
        updatePagination();
    };

    function updatePagination() {
        const pagination = $('#pagination');
        pagination.empty();
        console.log("Tạo thanh phân trang, số trang:", images.length);
        for (let i = 0; i < images.length; i++) {
            pagination.append(`<button ${i === currentImageIndex ? 'style="background-color: #0056b3;"' : ''} onclick="currentImageIndex=${i}; $('#reading-image').attr('src', images[${i}]).on('error', function(){this.src='https://via.placeholder.com/800x600.png?text=Error+Loading+Image';}).on('load', function(){console.log('Ảnh tải thành công:', images[${i}]); const w=$('#reading-image').width(); const h=$('#reading-image').height(); console.log('Kích thước:', w+'x'+h); if(w<=0||h<=0){this.src='https://via.placeholder.com/800x600.png?text=Error+Rendering+Image';}}); updatePagination();">${i + 1}</button>`);
        }
        console.log("Kiểm tra hiển thị #pagination:", $('#pagination').is(':visible'));
    }

    // Sửa sự kiện đóng chế độ đọc sách
    $(document).on('click', function(e) {
        // Chỉ chạy nếu không nhấp vào #toggle-reading-mode
        if ($(e.target).closest('#toggle-reading-mode').length) {
            return; // Bỏ qua nếu nhấp vào nút chuyển chế độ
        }
        if ($('#reading-mode').hasClass('active') && !$(e.target).closest('#reading-mode').length) {
            $('#reading-mode').removeClass('active');
        }
    });

    $('#load-all').click(function() {
        const imageContainer = $('#chapter-images');
        imageContainer.empty();
        images.forEach((src, index) => {
            imageContainer.append(`
                <img src="${src}" alt="Ảnh trang ${index + 1}" onerror="this.src='https://via.placeholder.com/800x600.png?text=Error+Loading+Image';">
            `);
        });
        $('#load-all').hide();
        $('#load-15').hide();
    });

    $('#close-reading-mode').click(function(e) {
        e.stopPropagation();
        $('#reading-mode').removeClass('active');
    });

    $('#load-15').click(function() {
        const imageContainer = $('#chapter-images');
        const currentLength = imageContainer.find('img').length;
        const nextImages = images.slice(currentLength, currentLength + 15);
        nextImages.forEach((src, index) => {
            imageContainer.append(`
                <img src="${src}" alt="Ảnh trang ${currentLength + index + 1}" onerror="this.src='https://via.placeholder.com/800x600.png?text=Error+Loading+Image';">
            `);
        });
        if (currentLength + 15 >= images.length) {
            $('#load-15').hide();
            $('#load-all').hide();
        }
    });

    $(document).on("click", "#chapter-list button", function() {
        const chapterId = $(this).data("chapter-id");
        $("#chapter-title").text($(this).text());
        window.loadChapterImages(chapterId);
    });
});