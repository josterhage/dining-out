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

        console.log(guestDtos);

        csrf['header'] = $('#rsvpCsrfHeader').val();
        csrf['token'] = $('#rsvpCsrfToken').val();

        //get the confirmationToken
        $.ajax({
            url: rsvpStartPath,
            type: 'POST',
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
            console.log('still true');
            $(`#saluteBox${e.data.guest}`).removeClass('hidden');
        } else {
            $(`#saluteBox${e.data.guest}`).addClass('hidden');
        }
    }

    function isCivilian(grade) {
        let result = false;
        civGrades.forEach(function (value) {
            console.log(`grade: ${grade}, value: ${value}, grade === value ${grade === value}`);
            if (grade === value) {
                console.log('true');
                result = true;
            }
        })
        return result;
    }
}

function CheckoutForm(checkout, appHost, csrf) {
    const pk = "pk_test_51KWNNWJnsiIlHanEhOtLOJSQJKtKqWx4mnXbMkmgrc2kEziYGVgfJlIa4esgsfrBaUhUbl8JQmOVVUFsTZ6Z2zii00asZ54jj1"
    const sendTokenMessagePath = appHost + '/confirm/send-message/';
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
    const price = (checkout['tier']['price'] * checkout['quantity']);
    const fee = (price * .029) + 30;
    const total = price + fee;

    const askAboutPurchase = `
    <div class="flex-row">
        <div class="text-med">Would you like to buy your ticket(s) now?</div>
    </div>
   <div class="flex-row">
           Your Cost:
    </div>
    <div class="flex-row">
        <div class="flex-col">
            <div class="flex-row">Quantity</div>
            <div class="flex-row" id="checkoutQty"></div>             
        </div>
        <div class="flex-col">
            <div class="flex-row">Description</div>
            <div class="flex-row" id="checkoutTierName"></div>
            <div class="flex-row">Online payment fee</div>
            <div class="flex-row">Total</div>        
        </div>
        <div class="flex-col">
            <div class="flex-row">Price</div>
            <div class="flex-row" id="checkoutPrice"></div>
            <div class="flex-row" id="serviceFee"></div>
            <div class="flex-row" id="checkoutSum"></div>
        </div>        
    </div>
    <div class="flex-row">
        <button id="purchaseButton" class="btn btn-rsvp btn-rsvp-submit">Yes</button>
        <button id="continueButton" class="btn btn-rsvp btn-rsvp-submit">No</button>
    </div>
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

    const messageSent = `
    <div class="text-med">
        Thank you <span id="guest-identity"></span>.<br/>
        We have emailed you at <span id="guest-email"></span> with a link to confirm your reservation.<br/>
        We look forward to seeing you!<br/>
        <div class="half-spacer"></div>
        <div class="text-sm">
            This dialog will close in <span id="close-countdown"></span> seconds.
        </div>
    </div>
    `;

    this.init = function () {
        console.log('hello there');
        $formDiv.html(askAboutPurchase);
        $('#checkoutQty').html(checkout['quantity']);
        $('#checkoutTierName').html(checkout['tier']['name']);
        $('#checkoutPrice').html(`\$${checkout['tier']['price'] / 100}`);
        $('#serviceFee').html(`\$${fee / 100}`);
        $('#checkoutSum').html(`\$${total / 100}`);
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

        const response = await Promise.resolve($.ajax({
            url: createIntentPath + checkout['token'],
            type: 'post',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrf['header'], csrf['token']);
            }
        }));

        elements = stripe.elements({
            appearance: {theme: 'stripe'},
            clientSecret: response
        });

        const paymentElement = elements.create("payment");
        paymentElement.mount("#payment-element");
    }

    function continueClicked(e) {
        e.preventDefault();
        $.ajax({
            url: sendTokenMessagePath + checkout['token'],
            method: 'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrf['header'], csrf['token']);
            },
            success: function (data) {
                console.log(data);
                $formHolder.html(messageSent);
                $('#guest-identity').html(`${data.grade['name']} ${data.firstName} ${data.lastName}`);
                $('#guest-email').html(`${data.email}`);
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