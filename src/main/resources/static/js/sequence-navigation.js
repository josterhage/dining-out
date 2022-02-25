$(document).ready(function () {
    sequenceNavigator = new SequenceNavigator();
});

function SequenceNavigator() {
    let sequenceIcons = [
        '#cocktailIcon',
        '#receivingIcon',
        '#dinnerIcon',
        '#awardIcon',
        '#speakerIcon',
        '#danceIcon'
    ]

    let sequenceItems = [
        '#cocktailBox',
        '#receivingBox',
        '#dinnerBox',
        '#awardBox',
        '#speakerBox',
        '#danceBox'
    ];

    function onMouseOver(event) {
        let $icon = $(sequenceIcons[event.data]);
        let $item = $(sequenceItems[event.data]);
        $icon.off('mouseover', onMouseOver);
        $icon.on('mouseleave', event.data, onMouseOut);

        $item.removeClass('hidden');
        let position = $item.offset();
        let width = $item.width();
        let marginLeft = $item.css('margin-left');

        console.log('position: ', position);
        console.log('width: ', width);

        $item.css('position','fixed');
        $item.css('left',position.left);
        $item.css('top',position.top);
        $item.css('width',width);
    }

    function onMouseOut(event) {
        let $icon = $(sequenceIcons[event.data]);
        let $item = $(sequenceItems[event.data]);

        $icon.off('mouseleave', onMouseOut);
        $icon.on('mouseover', event.data, onMouseOver);
        $item.addClass('hidden');
        $item.css('position','');
        $item.css('left','');
        $item.css('top','');
        $item.css('width','');
    }

    function onClick(event) {
        $('#iconsBox').addClass('hidden');
        $('#textBox').addClass('hidden');
        $(sequenceItems[event.data]).removeClass('hidden');
        $(sequenceItems[event.data]+'Close').removeClass('hidden');
        $(sequenceItems[event.data]+'Close').on('click',event.data,onClose);
        $('.body-text .mobile-block').addClass('hidden');
    }

    function onClose(event) {
        $(sequenceItems[event.data]).addClass('hidden');
        $(sequenceItems[event.data]+'Close').addClass('hidden');
        $(sequenceItems[event.data]+'Close').off('click',onClose);
        $('.body-text .mobile-block').removeClass('hidden');
        $('#iconsBox').removeClass('hidden');
        $('#textBox').removeClass('hidden');
    }

    if ($(window).width() > 767) {
        sequenceIcons.forEach(function (sequenceIcon, index) {
            $(sequenceIcon).on('mouseover', index, onMouseOver);
        });
    } else {
        sequenceIcons.forEach(function (sequenceIcon, index) {
            $(sequenceIcon).on('click',index,onClick);
        })
    }

    $(window).resize(function () {
        if ($(window).width() < 768) {
            sequenceIcons.forEach(function (sequenceIcon, index) {
                $(sequenceIcon).off('mouseover', onMouseOver);
                $(sequenceIcon).on('click',index,onClick);
            });
        } else {
            sequenceIcons.forEach(function (sequenceIcon, index) {
                $(sequenceIcon).off('mouseover',onMouseOver);
                $(sequenceIcon).on('mouseover', index, onMouseOver);
            });
        }
    })
}