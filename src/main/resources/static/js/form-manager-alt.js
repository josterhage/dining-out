$(document).ready(function () {
    let formManager = new FormManager();
});

function FormManager() {
    let formButtons = [
        '#getTicketsButton',
        '#donateButton'
    ]

    let forms = [
        '#getTicketsForm',
        '#donateForm'
    ]

    let registration = [
        registerTicketFormElements,
        registerDonateFormElements
    ]

    let lists = {
        title: ['Make a Selection', 'Mr.', 'Mrs.', 'Ms.', 'CPT'],
        unit: ['HHC BDE', 'SHC', '304th', '305th', '309th', '344th']
    }

    function showForm(event) {
        let $formDiv = $(forms[event.data]);
        let $closeButton = $(forms[event.data] + 'Close');

        $formDiv.removeClass('hidden');
        $('body').css('overflow','hidden');

        $closeButton.on('click',event.data, closeForm);
    }

    function closeForm(event) {
        let $formDiv = $(forms[event.data]);

        //reset?

        $formDiv.addClass('hidden');
        $('body').css('overflow','');


    }

    function registerTicketFormElements() {
        
    }

    function registerDonateFormElements() {

    }
}