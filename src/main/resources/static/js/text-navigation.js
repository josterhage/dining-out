function TextNavigator() {
    const viewUrl = '/view/';

    let lastClicked;
    let sequenceNavigation;

    $(document).ready(async function () {
        $('#nav-home').on('click',onHomeClick);
        $('#nav-sequence').on('click',onSequenceClick);
        $('#nav-menu').on('click',onMenuClick);
        $('#nav-location').on('click',onLocationClick);
        $('#nav-dress').on('click',onDressClick);

        $('main').html(await getView('home'));
        if(Date.now() < 1648105199000) {
            $('#bigBuyNow').on('click', formManager.showForm);
        }
        lastClicked = 'home';
    });

    async function onHomeClick(){
        $('main').html(await getView('home'));
        updateClicks();
        $('nav-home').off('click',onHomeClick);
        if(Date.now() < 1648105199000) {
            $('#bigBuyNow').on('click', formManager.showForm);
        } else {
            $('#bigBuyNow').remove();
            $('#slide-show').text("Ticket sales have ended.")
        }
        lastClicked = 'home';
    }

    async function onSequenceClick() {
        $('main').html(await getView('sequence'));
        updateClicks();
        $('nav-home').off('click',onSequenceClick);
        lastClicked = 'home';
        sequenceNavigation = new SequenceNavigator();
    }

    async function onMenuClick() {
        $('main').html(await getView('menu'));
        updateClicks();
        $('nav-menu').off('click',onMenuClick);
        lastClicked = 'home';
    }

    async function onLocationClick() {
        $('main').html(await getView('location'));
        updateClicks();
        $('nav-location').off('click',onLocationClick);
        lastClicked = 'home';
    }

    async function onDressClick() {
        $('main').html(await getView('dress'));
        updateClicks();
        $('nav-dress').off('click',onDressClick);
        lastClicked = 'home';
    }

    function updateClicks() {
        switch(lastClicked){
            case 'home':
                $('#nav-home').on('click',onHomeClick);
                break;
            case 'sequence':
                $('#nav-sequence').on('click',onSequenceClick);
                break;
            case 'menu':
                $('#nav-menu').on('click',onMenuClick);
                break;
            case 'location':
                $('#nav-location').on('click',onLocationClick);
                break;
            case 'dress':
                $('#nav-dress').on('click',onDressClick);
                break;
        }
    }

    async function getView(viewName) {
        const requestUrl = viewUrl + viewName;

        let data = await $.ajax({
            url: requestUrl,
            type: 'get'
        });

        return $('<div></div>').html($(data)).find(`#main-${viewName}`);
    }
}