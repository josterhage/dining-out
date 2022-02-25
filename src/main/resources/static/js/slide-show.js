function SlideShow(slides = [], transition = 5) {
    let $slideShow;
    let $image;
    let slideshowTimeout;

    let index = 0;
    let paused = false;

    //fixed ui elements
    const $buttonBar = $(`
        <span class="ss-buttons">
            <button class="btn btn-ss-disabled" id="singleLeft" style="margin-left:auto;">
                <i class="fa-solid fa-chevron-left fa-1x"></i>
            </button>
            <button class="btn btn-ss" id="playPause">
                <i class="fa-solid fa-pause" id="playPauseIcon"></i>
            </button>
            <button class="btn btn-ss-disabled" id="singleRight" style="margin-right:auto;">
                <i class="fa-solid fa-chevron-right"></i>
            </button>
        </span>
    `);

    this.halt = function() {
        pause();
    }

    this.resume = function() {
        play();
    }

    function nextSlide() {
        index++;
        // if (!$('#singleLeft').hasClass('btn-ss')) {
        //     $('#singleLeft').removeClass('btn-ss-disabled');
        //     $('#singleLeft').addClass('btn-ss');
        //     $('#singleLeft').on('click', prevSlide);
        // }
        
        if (index < slides.length) {
            $image.attr('src', slides[index]);
            if ((index + 1) === slides.length) {
                $('#singleRight').removeClass('btn-ss');
                $('#singleRight').addClass('btn-ss-disabled');
                $('#singleRight').off('click', nextSlide);
            }
        } else {
            index = 0;
            $image.attr('src', slides[index]);
            $('#singleLeft').removeClass('btn-ss');
            $('#singleLeft').addClass('btn-ss-disabled');
            $('#singleLeft').off('click', prevSlide);
            $('#singleRight').removeClass('btn-ss-disabled');
            $('#singleRight').addClass('btn-ss');
            $('#singleRight').on('click', nextSlide);            
        }
        if (index > 0) {
            $('#singleLeft').removeClass('btn-ss-disabled');
            $('#singleLeft').addClass('btn-ss');
            $('#singleLeft').on('click', prevSlide);
        }
        if (!paused) {
            slideshowTimeout = window.setTimeout(nextSlide, transition * 1000);
        }
    }

    function prevSlide() {
        index--;
        // if (!$('#singleRight').hasClass('btn-ss')) {
        //     $('#singleRight').removeClass('btn-ss-disabled');
        //     $('#singleRight').addClass('btn-ss');
        //     $('#singleRight').on('click', nextSlide);
        // }
        if (index >= 0) {
            $image.attr('src', slides[index]);
            if (index === 0) {
                $('#singleLeft').removeClass('btn-ss');
                $('#singleLeft').addClass('btn-ss-disabled');
                $('#singleLeft').off('click', prevSlide);
            }
        } else {
            index = slides.length - 1;
            $image.attr('src', slides[index]);
            $('#singleRight').removeClass('btn-ss');
            $('#singleRight').addClass('btn-ss-disabled');
            $('#singleRight').off('click', nextSlide);
        }
        if ((index + 1) < slides.length) {
            $('#singleRight').removeClass('btn-ss-disabled');
            $('#singleRight').addClass('btn-ss');
            $('#singleRight').on('click', nextSlide);
        }
        if (!paused) {
            slideshowTimeout = window.setTimeout(nextSlide, transition * 1000);
        }
    }

    function pause() {
        paused = true;
        clearTimeout(slideshowTimeout);
        $('#playPause').off('click', pause);
        $('#playPause').on('click', play);
        $('#playPauseIcon').removeClass('fa-pause');
        $('#playPauseIcon').addClass('fa-play');
    }

    function play() {
        paused = false;
        slideshowTimeout = window.setTimeout(nextSlide, transition * 1000);
        $('#playPause').off('click', play);
        $('#playPause').on('click', pause);
        $('#playPauseIcon').removeClass('fa-play');
        $('#playPauseIcon').addClass('fa-pause');
    }

    //if slides is empty add a dummy slide
    if (slides.length === 0) {
        slides.push('images/263303036_265475378946822_6199204000457916659_n.jpg');
    }

    //page init callback
    $(document).ready(function () {
        //load stylesheet
        $('head').append('<link href="css/slide-show.css" rel="stylesheet">');

        //hook #slide-show
        $slideShow = $('#slide-show');

        $slideShow.addClass('ss-container');
        $slideShow.append('<img class="ss-img" id="ss-frame">');
        $slideShow.append($buttonBar);

        $image = $('#ss-frame');
        $image.attr('src', slides[index]);

        if (slides.length > 1) {
            $('#singleRight').removeClass('btn-ss-disabled');
            $('#singleRight').addClass('btn-ss');
            $('#singleRight').on('click', nextSlide);
            $('#playPause').on('click', pause);

            slideshowTimeout = window.setTimeout(nextSlide, transition * 1000);
        } else {
            $('#playPause').removeClass('btn-ss');
            $('#playPause').addClass('btn-ss-disabled');
        }
    })
} 