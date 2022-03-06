const $hamburger = $('<a href="#" class="btn btn-hamburger"><i class="fa-solid fa-bars"></i></a>');


$(document).ready(function () {
    $hamburger.insertBefore('.navbar.collapsable');

    $('.btn-hamburger').on('click', function () {
        let $navbar = $(this).parent().find('.navbar.collapsable');

        if ($navbar.css('display') === 'none') {
            $navbar.css('display', 'block');
            $navbar.on('click',{element: $navbar},clearMenu);
        } else {
            $navbar.css('display', 'none');
        }
    })
})

function clearMenu(event) {
    event.data.element.css('display','none');
    event.data.element.off('click',clearMenu);
    $(window).on('resize',{element: event.data.element},restoreNavbar);
}

function restoreNavbar(event) {
    if($(window).width() > 767) {
        
        event.data.element.css('display','');
        $(window).off('resize',restoreNavbar);
    }
}