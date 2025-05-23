
$(document).on('click', '.image-container img', function() {
    const src = $(this).attr('src');
    const img = `<img src="${src}" style="max-width: 90%; height: auto; display: block; margin: 20px auto;" onclick="this.style.display='none';">`;
    $('body').append(`<div style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); z-index: 10000;" onclick="this.remove()">${img}</div>`);
});


function scrollChapterList(direction) {
    const container = $('#chapter-list');
    const scrollAmount = 200;
    if (direction === 'left') {
        container.scrollLeft(container.scrollLeft() - scrollAmount);
    } else {
        container.scrollLeft(container.scrollLeft() + scrollAmount);
    }
}


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