function FormManager(appHost) {
    const rsvpFormPath = appHost + "/form/rsvp-form";

    $.ajax({
        url: rsvpFormPath,
        type: 'get',
        success: function (data) {
            $('#getTicketsFormHolder').append($(data));
            $('#getTicketsButton').on('click',showRsvpForm);
        }
    })

    function showRsvpForm() {
        $('#rsvpFormContainer').removeClass('hidden');
        $('#getTicketsFormClose').on('click',hideRsvpForm);
        $('#rsvpSubmit').on('click',doSubmit);
        $('#rsvpReset').on('click',doReset);
        $('#guestCount').on('change',guestCountChanged);
    }

    function hideRsvpForm() {
        $('#rsvpForm').trigger('reset');
        $('#rsvpFormContainer').addClass('hidden');
    }

    function doSubmit(event) {
        event.preventDefault();
        //validate data

        //submit guests

        //prompt for purchase
    }

    function doReset() {
        $('#secondGuestContainer').addClass('hidden');
        $('#secondGuest').val('no');
    }

    function guestCountChanged() {
        let count = $('#guestCount').val();
        if(count == 1) {
            $('#secondGuestContainer').addClass('hidden');
            $('#secondGuest').val('no');
        } else {
            $('#secondGuestContainer').removeClass('hidden');
            $('#secondGuest').val('yes');
        }
    }
}