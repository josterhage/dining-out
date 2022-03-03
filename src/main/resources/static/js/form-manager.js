function FormManager(appHost) {
    const rsvpFormPath = appHost + "/form/rsvp-form";
    const rsvpStartPath = appHost + "/rsvp/start";
    let checkoutForm;

    let requiredFields =
        ["firstName",
            "lastName",
            "grade",
            "meal"]

    let csrf = {
        header: "",
        token: ""
    }

    this.guestDtos = function () {
        return guestDtos;
    }

    let guestDtos = [{
        firstName: "",
        lastName: "",
        grade: "",
        meal: "",
        requestText: "",
        unit: "",
        email: "",
        partnerId: "",
        confirmed: false
    }, {
        firstName: "",
        lastName: "",
        grade: "",
        meal: "",
        requestText: "",
        unit: "",
        email: "",
        partnerId: "",
        confirmed: false
    }];

    const civGrades = [
        'WS/WG5',
        'WS/WG6',
        'GS7',
        'GS8',
        'GS9',
        'GS11',
        'GS12',
        'GS13',
        'GS14',
        'CIV'
    ]

    $(document).ready(function () {
        $('#getTicketsButton').on('click', showRsvpForm);
    });

    this.showRsvpForm = function() {
        showRsvpForm();
    }

    function showRsvpForm() {
        $('.loader-frame').removeClass('hidden');
        $.ajax({
            url: rsvpFormPath,
            type: 'get',
            success: function (data) {
                let $div = $('<div></div>').html($(data)).find('#rsvpFormContainer');
                $('#getTicketsFormHolder').html($div);
                $('#getTicketsFormClose').on('click', hideRsvpForm);
                $('#rsvpForm').on('submit', doSubmit);
                $('#rsvpReset').on('click', doReset);
                $('#guestCount').on('change', guestCountChanged);
                $('#guest1grade').on('change', {guest: 1}, gradeChanged);
                $('#guest2grade').on('change', {guest: 2}, gradeChanged);
                $('.loader-frame').addClass('hidden');
            }
        });
    }

    function hideRsvpForm() {
        $('#getTicketsFormHolder').empty();
    }

    function doSubmit(event) {
        event.preventDefault();
        //validate data
        if (!validateData()) {
            alert('Please fill in the highlighted areas.');
            return;
        }

        //store data
        let count = $('#guestCount').val();

        let guestDtos = [];
        for (j = 1; j <= count; j++) {

            guestDtos.push({
                firstName: $(`#guest${j}firstName`).val(),
                lastName: $(`#guest${j}lastName`).val(),
                grade: $(`#guest${j}grade`).val(),
                salute: $(`#guest${j}salute`).val(),
                meal: $(`#guest${j}meal`).val(),
                requestText: $(`#guest${j}request`).val(),
                unit: $('#unit').val(),
                email: $('#guestEmail').val(),
                partnerId: "",
                confirmed: false
            });
        }

        csrf['header'] = $('#rsvpCsrfHeader').val();
        csrf['token'] = $('#rsvpCsrfToken').val();

        //get the confirmationToken
        $.ajax({
            url: rsvpStartPath,
            method: 'POST',
            data: JSON.stringify(guestDtos),
            contentType: 'application/json',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrf['header'], csrf['token']);
            },
            success: function (data) {

                checkoutForm = new CheckoutForm(data, appHost, csrf);
                checkoutForm.init();
            }
        });
    }

    function validateData() {
        let count = $('#guestCount').val();

        let valid = true;

        if ($('#unit').val() === null) {
            $('#unitValidation').addClass('validation-danger');
            $('#unitValidation').removeClass('validation-spacer');
            valid = false;
        }

        if ($('#guestEmail').val() === '') {
            $('#emailValidation').addClass('validation-danger');
            $('#emailValidation').removeClass('validation-spacer');
        }
        for (let i = 1; i <= count; i++) {
            requiredFields.forEach(function (value) {
                if ($(`#guest${i}${value}`).val() === "" || $(`#guest${i}${value}`).val() === null) {
                    $(`#guest${i}${value}Validation`).addClass('validation-danger');
                    $(`#guest${i}${value}Validation`).removeClass('validation-spacer');
                    valid = false;
                }
            })
        }
        return valid;
    }

    function doReset() {
        $('#secondGuestContainer').addClass('hidden');
        $('#secondGuest').val('no');
    }

    function guestCountChanged() {
        let count = $('#guestCount').val();
        if (count === '1') {
            $('#secondGuestContainer').addClass('hidden');
            $('#secondGuest').val('no');
        } else {
            $('#secondGuestContainer').removeClass('hidden');
            $('#secondGuest').val('yes');
        }
    }

    function gradeChanged(e) {
        if (isCivilian($(`#guest${e.data.guest}grade`).val())) {
            $(`#saluteBox${e.data.guest}`).removeClass('hidden');
        } else {
            $(`#saluteBox${e.data.guest}`).addClass('hidden');
        }
    }

    function isCivilian(grade) {
        let result = false;
        civGrades.forEach(function (value) {
            if (grade === value) {
                result = true;
            }
        })
        return result;
    }
}

function CheckoutForm(checkout, appHost, csrf) {
    const pk = "pk_test_51KWNNWJnsiIlHanEhOtLOJSQJKtKqWx4mnXbMkmgrc2kEziYGVgfJlIa4esgsfrBaUhUbl8JQmOVVUFsTZ6Z2zii00asZ54jj1"
    const abortPath = appHost + '/rsvp/abort/';
    const createIntentPath = appHost + '/checkout/create-payment-intent/';
    const $formDiv = $('#rsvpForm');
    const $formHolder = $('#formHolder');

    //stripe stuff
    let stripe;
    let elements;
    const appearance = {theme: 'stripe'};
    let clientSecret;

    let closeTime = 5;
    let intervalId;
    const price = (checkout['tierPrice'] * checkout['quantity']);
    const total = price + checkout['fee'];

    const priceStr = (checkout['tierPrice'] * checkout['quantity'] / 100).toFixed(2);
    const feeStr = (checkout['fee'] / 100).toFixed(2);
    const totalStr = (total/100).toFixed(2);

    const askAboutPurchase = `
    <div class="flex-row">
        <div class="text-med">Would you like to buy your ticket(s) now?</div>
    </div>
    <div class="flex-row">
           Your Cost:
    </div>
    <table>
    <tr>
        <th>Quantity</th>
        <th>Description</th>
        <th>Price</th>
    </tr>
    <tr>
        <td id="checkoutQty"></td>
        <td id="checkoutTierName"></td>  
        <td id="checkoutPrice"></td> 
    </tr>
    <tr>
        <td></td>
        <td>Online payment fee</td>
        <td id="serviceFee"></td>
    </tr>
    <tr>
        <td></td>
        <td>Total</td>
        <td id="checkoutSum"></td>
    </tr>
    
    </table>
    
    <div class="flex-row">
        <button id="purchaseButton" class="btn btn-rsvp btn-rsvp-submit">Pay now</button>
        <button id="continueButton" class="btn btn-rsvp btn-rsvp-reset">Cancel</button>
    </div>
    <a href="http://stripe.com"><img class="stripe-logo" src="/images/stripe/stripe-black.svg"></a>
    `;

    const checkoutForm = `
    <form id="payment-form">
        <div id="payment-element">
        </div>
        <button id="submit" class="btn-stripe">
            <div class="spinner hidden" id="spinner"></div>
            <span id="button-text">Pay now</span>
        </button>
        <div id="payment-message" class="hidden"></div>
    </form>
    `;

    const aborted = `
    <div class="text-med">
        Your RSVP has been cancelled.
        <div class="half-spacer"></div>
        <div class="text-sm">
            This dialog will close in <span id="close-countdown"></span> seconds.
        </div>
    </div>
    `;

    this.init = function () {
        $formDiv.html(askAboutPurchase);
        $('#checkoutQty').html(checkout['quantity']);
        $('#checkoutTierName').html(checkout['tierName']);
        $('#checkoutPrice').html(`\$${priceStr}`);
        $('#serviceFee').html(`\$${feeStr}`);
        $('#checkoutSum').html(`\$${totalStr}`);
        $('#purchaseButton').on('click', purchaseClicked);
        $('#continueButton').on('click', continueClicked);
    }

    async function purchaseClicked(e) {
        e.preventDefault();

        //show form <-- must be done before trying to instantiate stripe
        $formDiv.html(checkoutForm);

        //init stripe
        stripe = new Stripe(pk);
        $('#submit').on('click', handleSubmit);

        // const response = await Promise.resolve($.ajax({
        //     url: createIntentPath + checkout['token'],
        //     type: 'post',
        //     beforeSend: function (xhr) {
        //         xhr.setRequestHeader(csrf['header'], csrf['token']);
        //     }
        // }));

        elements = stripe.elements({
            appearance: {theme: 'stripe'},
            clientSecret: checkout['clientSecret']
        });

        const paymentElement = elements.create("payment");
        paymentElement.mount("#payment-element");
    }

    function continueClicked(e) {
        e.preventDefault();
        $.ajax({
            url: abortPath + checkout['clientSecret'],
            method: 'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrf['header'], csrf['token']);
            },
            success: function (data) {
                $formHolder.html(aborted);
                $('#close-countdown').html(closeTime);
                intervalId = setInterval(closeTimer, 1000);
                $('#getTicketsFormClose').on('click', function () {
                    clearInterval(intervalId);
                })
            }
        })
    }

    function closeTimer() {
        closeTime--;
        $('#close-countdown').html(closeTime);
        if (closeTime === 0) {
            $('#getTicketsFormClose').trigger('click');
        }
    }

    async function handleSubmit(e) {
        e.preventDefault()
        setLoading(true);

        const {error} = await stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: appHost + '/checkout/success'
            }
        });

        if (error.type === "card_error" || error.type === "validation_error") {
            showMessage(error.message);
        } else {
            showMessage("An unexpected error occurred.");
        }

        setLoading(false);
    }

    // ------- UI helpers -------

    function showMessage(messageText) {
        const messageContainer = document.querySelector("#payment-message");

        messageContainer.classList.remove("hidden");
        messageContainer.textContent = messageText;

        setTimeout(function () {
            messageContainer.classList.add("hidden");
            messageText.textContent = "";
        }, 4000);
    }

// Show a spinner on payment submission
    function setLoading(isLoading) {
        if (isLoading) {
            // Disable the button and show a spinner
            document.querySelector("#submit").disabled = true;
            document.querySelector("#spinner").classList.remove("hidden");
            document.querySelector("#button-text").classList.add("hidden");
        } else {
            document.querySelector("#submit").disabled = false;
            document.querySelector("#spinner").classList.add("hidden");
            document.querySelector("#button-text").classList.remove("hidden");
        }
    }
}