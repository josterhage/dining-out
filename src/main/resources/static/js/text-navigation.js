function TextNavigator(baseUrl) {
    const viewUrl = baseUrl + '/view/';

    let lastClicked;
    let sequenceNavigation;

    $(document).ready(async function () {
        $('#nav-home').on('click',onHomeClick);
        $('#nav-sequence').on('click',onSequenceClick);
        $('#nav-menu').on('click',onMenuClick);
        $('#nav-location').on('click',onLocationClick);
        $('#nav-dress').on('click',onDressClick);

        $('main').html(await getView('home'));
        lastClicked = 'home';
    });

    async function onHomeClick(e){
        $('main').html(await getView('home'));
        updateClicks();
        $('nav-home').off('click',onHomeClick);
        lastClicked = 'home';
    }

    async function onSequenceClick(e) {
        $('main').html(await getView('sequence'));
        updateClicks();
        $('nav-home').off('click',onSequenceClick);
        lastClicked = 'home';
        sequenceNavigation = new SequenceNavigator();
    }

    async function onMenuClick(e) {
        $('main').html(await getView('menu'));
        updateClicks();
        $('nav-menu').off('click',onMenuClick);
        lastClicked = 'home';
    }

    async function onLocationClick(e) {
        $('main').html(await getView('location'));
        updateClicks();
        $('nav-location').off('click',onLocationClick);
        lastClicked = 'home';
    }

    async function onDressClick(e) {
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

    function onBuyClick(e) {
        // ???
    }

    this.getView = async function(viewName) {
        return getView(viewName);
    }

    async function getView(viewName) {
        const requestUrl = viewUrl + viewName;

        let data = await Promise.resolve($.ajax({
            url: requestUrl,
            type: 'get'
        }));

        return $('<div></div>').html($(data)).find(`#main-${viewName}`);
    }
}