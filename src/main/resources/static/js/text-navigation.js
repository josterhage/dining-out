$(document).ready(function() {
    $('#main-home').css('display','flex');

    $('#nav-home').on('click',function() {
        if($('#main-home').css('display') !== 'flex') {
            $('.body-text').css('display','none');
            $('#main-home').css('display','flex');
            if(typeof(slideShow.resume === 'function')) {
                slideShow.resume();
            }
        }
    })
    $('#nav-sequence').on('click',function() {
        if($('#main-sequence').css('display') !== 'flex') {
            $('.body-text').css('display','none');
            $('#main-sequence').css('display','flex');
            if(typeof(slideShow.halt === 'function')) {
                slideShow.halt();
            }
        }
    })    
    $('#nav-menu').on('click',function() {
        if($('#main-menu').css('display') !== 'flex') {
            $('.body-text').css('display','none');
            $('#main-menu').css('display','flex');
            if(typeof(slideShow.halt === 'function')) {
                slideShow.halt();
            }
        }
    })
    $('#nav-location').on('click',function() {
        if($('#main-location').css('display') !== 'flex') {
            $('.body-text').css('display','none');
            $('#main-location').css('display','flex');
            if(typeof(slideShow.halt === 'function')) {
                slideShow.halt();
            }
        }
    })
    $('#nav-dress').on('click',function() {
        if($('#main-dress').css('display') !== 'flex') {
            $('.body-text').css('display','none');
            $('#main-dress').css('display','flex');
            if(typeof(slideShow.halt === 'function')) {
                slideShow.halt();
            }
        }
    })
})