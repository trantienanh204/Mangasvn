const serverHost = window.location.hostname === "localhost" ? "http://localhost:8080" : "http://192.168.1.19:8080";
$(document).ready(function() {

    $('#author-select').select2({
        placeholder: "Chọn tác giả",
        allowClear: true,
        width: '100%',
        tags: true, // Cho phép thêm tag mới
        createTag: function(params) {
            var term = $.trim(params.term);

            if (term === '') {
                return null;
            }

            return {
                id: term,
                text: term,
                newOption: true
            };
        }
    });

    $('#category-select').select2({
        placeholder: "Chọn danh mục",
        allowClear: true,
        width: '100%'
    });

    let authorsMap = {};
    let categoriesMap = {};

    function checkAdmin() {
        const token = localStorage.getItem('token');
        if (!token) {
            $('#admin-menu').hide();
            window.location.href = '/api/auth/login';
            return;
        }
        // Kiểm tra vai trò để hiển thị menu admin
        $.ajax({
            url: "${serverHost}/api/auth/user-info",
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token
            },
            success: function(data) {
                if (data.role === "ADMIN" || data.role === "CHUTUT") {
                    $('#admin-menu').show();
                } else {
                    $('#admin-menu').hide();
                }
            },
            error: function(xhr) {
                if (xhr.status === 401 || xhr.status === 403) {
                    $('#admin-menu').hide();
                }
            }
        });
    }

    function loadComics() {
        const token = localStorage.getItem('token');
        if (!token) {
            Toastify({ text: 'Vui lòng đăng nhập để tiếp tục!', duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
            window.location.href = '/api/auth/login';
            return;
        }

        $.ajax({
            url: `${serverHost}/api/truyen/listloc`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(comics) {
                const comicSelect = $('#comic-select');
                comicSelect.empty();
                comicSelect.append('<option value="0">Tạo truyện mới</option>');
                comics.forEach(comic => comicSelect.append(`<option value="${comic.id}">${comic.tenTruyen}</option>`));
                const comicList = $('#comic-list');
                comicList.empty();
                comics.forEach(comic => {
                    const categories = comic.categoryIds ? `Danh mục: ${comic.categoryIds.map(id => categoriesMap[id] || id).join(', ')}` : 'Chưa có danh mục';
                    const authors = comic.authorIds ? `Tác giả: ${comic.authorIds.map(id => authorsMap[id] || id).join(', ')}` : 'Chưa có tác giả';
                    comicList.append(`<li class="list-group-item">${comic.tenTruyen} (${categories} | ${authors})</li>`);
                });
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách truyện:', error);
                Toastify({ text: 'Lỗi tải danh sách truyện: ' + (xhr.responseText || error), duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
            }
        });
    }

    function loadAuthors() {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/truyen/authors`, // Cập nhật endpoint
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(authors) {
                const authorSelect = $('#author-select');
                authorSelect.empty();
                authors.forEach(author => {
                    authorsMap[author.id] = author.tenTacGia;
                    authorSelect.append(`<option value="${author.id}">${author.tenTacGia}</option>`);
                });
                authorSelect.trigger('change');
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách tác giả:', error, 'Status:', status, 'Response:', xhr.responseText);
                Toastify({ text: 'Lỗi tải danh sách tác giả: ' + (xhr.responseText || error), duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
            }
        });
    }

    function loadCategories() {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/categories`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(categories) {
                const categorySelect = $('#category-select');
                categorySelect.empty();
                categories.forEach(category => { categoriesMap[category.id] = category.tenDanhMuc; categorySelect.append(`<option value="${category.id}">${category.tenDanhMuc}</option>`); });
                categorySelect.trigger('change');
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải danh sách danh mục:', error);
            }
        });
    }

    $('#author-select').on('select2:select', function(e) {
        const data = e.params.data;
        if (data.newOption) {
            const newAuthorName = data.text;
            const token = localStorage.getItem('token');
            $.ajax({
                url: `${serverHost}/api/truyen/authors`,
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                data: JSON.stringify({ tenTacGia: newAuthorName }),
                success: function(response) {
                    authorsMap[response.id] = newAuthorName;
                    $('#author-select').append(`<option value="${response.id}" selected>${newAuthorName}</option>`).trigger('change');
                    Toastify({ text: 'Thêm tác giả mới thành công!', duration: 3000, gravity: 'top', position: 'right', style: { background: '#28a745' } }).showToast();
                },
                error: function(xhr, status, error) {
                    console.log('Lỗi thêm tác giả:', error);
                    Toastify({ text: 'Lỗi thêm tác giả: ' + (xhr.responseText || error), duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
                    $('#author-select').val(null).trigger('change'); // Xóa tag nếu thêm thất bại
                }
            });
        }
    });

    function loadComicDetails(comicId) {
        const token = localStorage.getItem('token');
        $.ajax({
            url: `${serverHost}/api/truyen/${comicId}`,
            method: 'GET',
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(comic) {
                $('#comic-title').val(comic.tenTruyen || '');
                $('#comic-description').val(comic.moTa || '');
                $('#comic-note').val(comic.ghiChu || '');
                $('#author-select').val(comic.authorIds[0] || null).trigger('change');
                $('#category-select').val(comic.categoryIds || []).trigger('change');
            },
            error: function(xhr, status, error) {
                console.log('Lỗi tải thông tin truyện:', error);
                Toastify({ text: 'Lỗi tải thông tin truyện: ' + (xhr.responseText || error), duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
            }
        });
    }

    window.addChapter = function() {
        const chapterItem = `<div class="chapter-item"><input type="text" class="form-control chapter-name" placeholder="Tên Chapter" required><input type="file" class="form-control chapter-zip" accept=".zip" required><button type="button" class="btn btn-danger btn-remove-chapter ms-2" onclick="removeChapter(this)">Xóa</button><div class="invalid-feedback">Vui lòng upload file ZIP với tên ảnh theo số thứ tự (ví dụ: 1.jpg, 2.png).</div></div>`;
        $('#chapter-list').append(chapterItem);
    };

    window.removeChapter = function(button) {
        const chapterItem = $(button).closest('.chapter-item');
        if ($('#chapter-list .chapter-item').length > 1) chapterItem.remove();
        else Toastify({ text: 'Phải có ít nhất một chapter!', duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
    };

    function validateZipOrder(zip) {
        return new Promise((resolve, reject) => {
            const files = Object.entries(zip.files).sort((a, b) => {
                const numA = parseInt(a[0].match(/^0*(\d+)/)[1]);
                const numB = parseInt(b[0].match(/^0*(\d+)/)[1]);
                return numA - numB;
            });

            let expectedIndex = 1;
            for (const [relativePath, file] of files) {
                if (!file.dir) {
                    const match = relativePath.match(/^0*(\d+)\.(jpg|png|jpeg)$/i);
                    if (!match) {
                        reject(new Error('Tên file không đúng định dạng (phải là số.jpg/png/jpeg): ' + relativePath));
                        return;
                    }
                    const pageNumber = parseInt(match[1]);
                    if (pageNumber !== expectedIndex) {
                        reject(new Error('Thứ tự file không liên tục, mong đợi ' + expectedIndex + ' nhưng nhận được ' + pageNumber));
                        return;
                    }
                    expectedIndex++;
                }
            }
            resolve();
        });
    }


    $('#upload-form').on('submit', async function(e) {
        e.preventDefault();

        const submitButton = $(this).find('button[type="submit"]');
        if (submitButton.prop('disabled')) return;
        submitButton.prop('disabled', true);

        const comicId = $('#comic-select').val();
        const isUpdate = comicId !== '0';
        let url = `${serverHost}/api/truyen`;
        let method = 'POST';

        if (isUpdate) {
            url = `${serverHost}/api/truyen/${comicId}`;
            method = 'PUT';
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').hide().find('input, textarea').prop('required', false);
            $('#create-btn').hide();
            $('#update-btn').show();
            $('.card-header').text('Cập nhật Truyện và Chapter');
        } else {
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').show().find('input, textarea').prop('required', true);
        }

        if (isUpdate) {
            const jsonData = {
                moTa: $('#comic-description').val() || null,
                ghiChu: $('#comic-note').val() || null
            };
            const authorIds = $('#author-select').val() ? [$('#author-select').val()] : [];
            const categoryIds = $('#category-select').val() || [];

            const token = localStorage.getItem('token');
            try {
                await $.ajax({
                    url: `${url}?authorIds=${authorIds.join(',')}&categoryIds=${categoryIds.join(',')}`,
                    type: 'PUT',
                    contentType: 'application/json',
                    data: JSON.stringify(jsonData),
                    headers: { 'Authorization': `Bearer ${token}` }
                });
            } catch (error) {
                Toastify({ text: 'Lỗi cập nhật thông tin truyện: ' + (error.responseText || error), duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
                submitButton.prop('disabled', false);
                return;
            }
        }

        const formData = new FormData();
        if (!isUpdate) {
            formData.append('title', $('#comic-title').val());
            formData.append('description', $('#comic-description').val());
            formData.append('note', $('#comic-note').val());
            const coverFile = $('#comic-cover')[0].files[0];
            if (!coverFile) {
                $('#comic-cover').next('.invalid-feedback').show();
                submitButton.prop('disabled', false);
                return;
            }
            formData.append('cover', coverFile);
            const authorId = $('#author-select').val();
            if (!authorId) {
                $('#author-select').next('.invalid-feedback').show();
                submitButton.prop('disabled', false);
                return;
            }
            formData.append('authorIds', authorId);
            const categoryIds = $('#category-select').val();
            if (!categoryIds || categoryIds.length === 0) {
                $('#category-select').next('.invalid-feedback').show();
                submitButton.prop('disabled', false);
                return;
            }
            categoryIds.forEach(id => formData.append('categoryIds', id));
        }

        const chapterPromises = [];
        let isValid = true;
        const chapters = [];

        $('#chapter-list .chapter-item').each(function(index) {
            const chapterName = $(this).find('.chapter-name').val();
            const zipFile = $(this).find('.chapter-zip')[0].files[0];
            if (!zipFile) {
                $(this).find('.invalid-feedback').show();
                isValid = false;
                return;
            }

            const promise = JSZip.loadAsync(zipFile)
                .then(function(zip) {
                    return validateZipOrder(zip).then(() => ({ chapterName, zipFile, valid: true }));
                })
                .catch(error => {
                    $(this).find('.invalid-feedback').text(error.message).show();
                    return { chapterName, zipFile, valid: false };
                });

            chapterPromises.push(promise);
        });

        const chapterResults = await Promise.all(chapterPromises);

        for (const result of chapterResults) {
            if (!result.valid) {
                isValid = false;
                continue;
            }
            formData.append('chapterZip', result.zipFile);
            formData.append('chapterName', result.chapterName);
            chapters.push({ name: result.chapterName, zip: result.zipFile });
        }

        if (!isValid) {
            submitButton.prop('disabled', false);
            return;
        }

        const progressBar = $('#upload-progress .progress-bar');
        $('#upload-progress').show();
        progressBar.css('width', '0%').attr('aria-valuenow', 0).text('0%');

        const token = localStorage.getItem('token');
        $.ajax({
            url: isUpdate ? `${serverHost}/api/truyen/${comicId}/chapters` : url,
            type: isUpdate ? 'PUT' : method,
            data: formData,
            contentType: false,
            processData: false,
            timeout: 30000,
            headers: { 'Authorization': `Bearer ${token}` },
            success: function(response) {
                progressBar.css('width', '100%').attr('aria-valuenow', 100).text('100%');
                Toastify({ text: isUpdate ? 'Cập nhật chapter thành công!' : 'Đăng truyện thành công!', duration: 3000, gravity: 'top', position: 'right', style: { background: '#28a745' } }).showToast();
                $('#upload-form')[0].reset();
                $('#chapter-list').empty().append(`<div class="chapter-item"><input type="text" class="form-control chapter-name" placeholder="Tên Chapter" required><input type="file" class="form-control chapter-zip" accept=".zip" required><button type="button" class="btn btn-danger btn-remove-chapter ms-2" onclick="removeChapter(this)">Xóa</button><div class="invalid-feedback">Vui lòng upload file ZIP với tên ảnh theo số thứ tự (ví dụ: 1.jpg, 2.png).</div></div>`);
                $('#author-select').val(null).trigger('change');
                $('#category-select').val(null).trigger('change');
                loadComics();
                setTimeout(() => $('#upload-progress').hide(), 1000);
            },
            error: function(xhr, status, error) {
                progressBar.css('width', '100%').attr('aria-valuenow', 100).text('Lỗi!');
                let errorMessage = isUpdate ? 'Cập nhật chapter thất bại' : 'Đăng truyện thất bại';
                if (status === 'timeout') errorMessage += ': Thời gian xử lý vượt quá 30 giây.';
                else if (xhr.responseText && xhr.responseText.includes('MaxUploadSizeExceededException')) errorMessage += ': Kích thước file vượt quá giới hạn (tối đa 50MB).';
                else errorMessage += ': ' + (xhr.responseText || error);
                Toastify({ text: errorMessage, duration: 3000, gravity: 'top', position: 'right', style: { background: '#dc3545' } }).showToast();
                setTimeout(() => $('#upload-progress').hide(), 1000);
            },
            complete: function() {
                submitButton.prop('disabled', false);
            },
            xhr: function() {
                const xhr = new XMLHttpRequest();
                xhr.upload.addEventListener('progress', function(e) {
                    if (e.lengthComputable) {
                        const percent = Math.round((e.loaded / e.total) * 100);
                        progressBar.css('width', percent + '%').attr('aria-valuenow', percent).text(percent + '%');
                    }
                });
                return xhr;
            }
        });
    });

    $('#comic-select').on('change', function() {
        const comicId = $(this).val();
        if (comicId === '0') {
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').show().find('input, textarea').prop('required', true);
            $('#create-btn').show();
            $('#update-btn').hide();
            $('.card-header').text('Đăng Truyện Mới');
            $('#author-select').prop('required', true);
            $('#category-select').prop('required', true);
        } else {
            $('#new-comic-fields, #new-comic-description, #new-comic-note, #new-comic-cover').hide().find('input, textarea').prop('required', false);
            $('#create-btn').hide();
            $('#update-btn').show();
            $('.card-header').text('Cập nhật Truyện và Chapter');
            loadComicDetails(comicId);
            $('#author-select').prop('required', true);
            $('#category-select').prop('required', true);
        }
    });

    checkAdmin();
    loadComics();
    loadAuthors();
    loadCategories();
});

const token = localStorage.getItem("token");
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
            console.log("Đăng nhập thành công với user:", data.username); // Thêm log
            // Hiển thị menu admin nếu là ADMIN hoặc CHUTUT
            if (data.role === "ADMIN" || data.role === "CHUTUT") {
                $('#admin-menu').show();
            }
        },
        error: function(xhr) {
            if (xhr.status === 401 || xhr.status === 403) {
                localStorage.removeItem("token");
                $('#login-link').show();
                $('#logout-link').hide();
                $('#user-info').text("");
                $('#admin-menu').hide();
            }
        }
    });
} else {
    $('#login-link').show();
    $('#logout-link').hide();
    $('#user-info').text("");
    $('#admin-menu').hide();
}
