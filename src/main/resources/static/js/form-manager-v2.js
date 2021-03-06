function FormManager(csrf) {
    const rsvpStartPath = "/rsvp/start";
    let grades, meals, salutes, units;
    let checkoutForm;
    let gradesReady,mealsReady,salutesReady,unitsReady,allReady;

    let requiredFields = [
        "firstName",
        "lastName",
        "grade",
        "meal"
    ]

    let guestDtos = [];

    let guestIndex;

    let unitOptions = "";

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

    const formOutline = `
        <div class="form-body" id="rsvpFormContainer">
        <div class="form-box">
            <h3 class="safari-flex-fix">RSVP</h3>
            <button class="btn btn-form-close" id="getTicketsFormClose">
                <i class="fa-solid fa-times"></i>
            </button>
            <div id="formHolder">
                <form id="rsvpForm">
                    <div class="flex-row">
                        <div class="flex-col">
                            <label for="unit">Which unit are you with?</label>
                            <select id="unit">
                                <option value="" selected disabled hidden>Select a unit</option>
                            </select>
                            <span id="unitValidation" class="validation-spacer">You must select a unit</span>
                        </div>
                    </div>
                    <div class="flex-row">
                        <div class="flex-col">
                            <label for="guestEmail">What is your email address?</label>
                            <input type="email" id="guestEmail">
                            <span id="emailValidation" class="validation-spacer">You must enter an email address</span>
                        </div>
                    </div>
                    <div id="guestContainer"></div>
                    <div class="flex-row" style="justify-content: space-between; margin: .5rem 0;">
                        <button type="button" id="removeGuest" class="btn btn-rsvp btn-rsvp-add hidden">Remove a guest</button>
                        <button type="button" id="addGuest" class="btn btn-rsvp btn-rsvp-add">Add another guest</button>
                    </div>
                    <div style="border-bottom: 1px solid lightgray"></div>
                    <div class="flex-row" style="margin: .5rem 0;">
                        <button type="submit" id="rsvpSubmit" class="btn btn-rsvp btn-rsvp-submit">Submit</button>
                        <button type="reset" id="rsvpReset" class="btn btn-rsvp btn-rsvp-reset">Reset</button>
                    </div>
                </form>
            </div>
        </div>`;

    $(document).ready(function () {

        init().then(() => {
            $('#getTicketsButton').on('click', showForm);
            $('#bigBuyNow').on('click', showForm);
        });
    })

    this.showForm = async function() {
        await showForm();
    }
    async function init() {
        guestDtos.length = 0;
        guestIndex = 1;
        gradesReady = false;
        grades = await $.ajax({
            url: '/api/grade',
            type: 'get',
            success: function(data) {
                gradesReady = true;
                return data;
            }
        })
        mealsReady = false;
        meals = await $.ajax({
            url: '/api/meal',
            type: 'get',
            success: function(data) {
                mealsReady = true;
                return data;
            }
        })
        salutesReady = false;
        salutes = await $.ajax({
            url: '/api/salute',
            type: 'get',
            success: function(data) {
                salutesReady = true;
                return data;
            }
        })
        unitsReady = false;
        units = await $.ajax({
            url: '/api/unit',
            type: 'get',
            success: function(data) {
                unitsReady = true;
                return data;
            }
        })

        while(!unitsReady) {}
        units.forEach(function (value) {
            unitOptions = unitOptions +
                `<option value="${value['name']}">${value['name']}</option>`;
        })
    }

    function showForm() {
        guestDtos.length = 0;
        guestIndex = 1;
        $('#getTicketsFormHolder').html(formOutline);
        $('#getTicketsFormClose').on('click', closeForm);
        if(!gradesReady || !mealsReady || !salutesReady || !unitsReady) {
            $('.form-box').append(`
        <div class="form-loader">
            <div class="loader"></div>
        </div>
        `)
        }
        $('#unit').append(unitOptions);
        $('#guestContainer').append(createGuestBlock(guestIndex));
        $('#addGuest').on('click', addGuest);
        $('#rsvpForm').on('submit', doSubmit);
        $('#rsvpForm').on('reset', doReset);
        $(`#guest${guestIndex}grade`).on('change',{id: guestIndex}, gradeChanged)
    }

    function closeForm() {
        $('#getTicketsFormHolder').empty();
    }

    function doSubmit(e) {
        e.preventDefault();

        if (!validate()) {
            missingFieldsModal();
            return;
        }

        inputFieldsToDtos();

        $('.form-box').append(`
        <div class="form-loader">
            <div class="loader"></div>
        </div>
        `)

        $.ajax({
            url: rsvpStartPath,
            method: 'POST',
            data: JSON.stringify(guestDtos),
            contentType: 'application/json',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrf['header'],csrf['token']);
            },
            success: function(data) {
                checkoutForm = new CheckoutForm(data,csrf);
                guestIndex = 1;
                $('.form-loader').remove();
            }
        })

    }

    function doReset() {
        const $unitValidation = $('#unitValidation');
        const $emailValidation = $('#emailValidation');
        const $removeGuest = $('#removeGuest');
        for (let i = 2; i <= guestIndex; i++) {
            $(`#guest${i}`).remove();
        }

        $unitValidation.addClass('validation-spacer');
        $unitValidation.removeClass('validation-danger');
        $emailValidation.addClass('validation-spacer');
        $emailValidation.removeClass('validation-danger');
        requiredFields.forEach(function (value) {
            let $fieldValidation =$(`#guest1${value}Validation`);
            $fieldValidation.addClass('validation-spacer');
            $fieldValidation.removeClass('validation-danger');
        })
        $removeGuest.addClass('hidden');
        $removeGuest.off('click', removeGuest);
        guestIndex = 1;
    }

    function addGuest() {
        guestIndex++;
        $('#guestContainer').append(createGuestBlock(guestIndex));
        $(`#guest${guestIndex}grade`).on('change',{id: guestIndex}, gradeChanged)
        if (guestIndex === 2) {
            const $removeGuest = $('#removeGuest');
            $removeGuest.removeClass('hidden');
            $removeGuest.on('click', removeGuest);
        }
    }

    function removeGuest() {
        if (guestIndex === 1) {
            return;
        }
        $(`#guest${guestIndex}`).remove();
        guestIndex--;
        if (guestIndex === 1) {
            let $removeGuest =$('#removeGuest');
            $removeGuest.addClass('hidden');
            $removeGuest.off('click', removeGuest);
        }
    }

    function createGuestBlock() {
        let gradeOptions;
        let mealOptions;
        let saluteOptions;

        while(!gradesReady) {}
        grades.forEach(function (value) {
            gradeOptions = gradeOptions +
                `<option value"${value['name']}">${value['name']}</option>`;
        })

        while(!mealsReady) {}
        meals.forEach(function (value) {
            mealOptions = mealOptions +
                `<option value="${value['name']}">${value['name']}</option>`;
        })

        while(!salutesReady) {}
        salutes.forEach(function (value) {
            saluteOptions = saluteOptions +
                `<option value="${value['name']}">${value['name']}</option>`;
        })

        $('.form-loader').remove();

        return `
      <div id="guest${guestIndex}">
        <div class="half-spacer"></div>
        <div class="text-md" style="font-weight:bold;">Guest ${guestIndex}:</div>
        <div class="half-spacer"></div>
        <div class="flex-row mb-sm">
            <div class="flex-col">
                <label for="guest${guestIndex}grade">Grade or Title: <br/>
                    <span class="text-sm">(For civilian Family members please select "CIV" at the end of the list, DA Civilians please choose your WG or GS Grade)</span>
                </label>
                <select id="guest${guestIndex}grade">
                    <option value="" selected disabled hidden>Select a grade</option>
                    ${gradeOptions}
                </select>
                <span id="guest${guestIndex}gradeValidation"
                      class="validation-spacer">You must select a grade or title</span>
            </div>
            <div class="flex-col hidden" id="saluteBox${guestIndex}">
                <label for="guest${guestIndex}salute">Civilian title:</label>
                <select id="guest${guestIndex}salute">
                    <option value="" selected hidden>Select a civilian title:</option>
                    ${saluteOptions}
                </select>
            </div>
        </div>
        <div class="flex-row mb-sm">
            <div class="flex-col">
                <label for="guest${guestIndex}firstName">First Name:</label>
                <input type="text" id="guest${guestIndex}firstName">
                <span id="guest${guestIndex}firstNameValidation"
                      class="validation-spacer">You must enter a first name</span>
            </div>
            <div class="flex-col">
                <label for="guest${guestIndex}lastName">Last Name:</label>
                <input type="text" id="guest${guestIndex}lastName">
                <span id="guest${guestIndex}lastNameValidation" class="validation-spacer">You must enter a last name</span>
            </div>
        </div>
        <div class="flex-row">
            <div class="flex-col">
                <label for="guest${guestIndex}meal">Meal:</label>
                <select id="guest${guestIndex}meal">
                    <option value="" selected disabled hidden>Select a meal</option>
                    ${mealOptions}
                </select>
                <span id="guest${guestIndex}mealValidation" class="validation-spacer">You must select a meal</span>
            </div>
            <div class="flex-col">
                <label for="guest${guestIndex}request">Special requests:</label>
                <textarea id="guest${guestIndex}request" cols="20" rows="5"></textarea>
            </div>
        </div>
</div>`;
    }

    function gradeChanged(e) {
        let val = $(`#guest${e.data.id}grade`).val();
        let clear = false;
        for(let i=0; i < civGrades.length; i++) {
            if(civGrades[i] === val) {
                clear = true;
                break;
            }
        }
        if(clear) {
            $(`#saluteBox${e.data.id}`).removeClass('hidden');
        } else {
            $(`#saluteBox${e.data.id}`).addClass('hidden');
        }
    }

    function validate() {
        let valid = true;
        const $unit = $('#unit');
        const $guestEmail = $('#guestEmail');
        //check for a unit
        if ($unit.val() === null) {
            let $element = $('#unitValidation');
            $element.addClass('validation-danger');
            $element.removeClass('validation-spacer');
            $unit.on('change',{field: '#unit'},fieldChanged);
            valid = false;
        }

        if ($guestEmail.val() === "") {
            let $element = $('#emailValidation');
            $element.addClass('validation-danger');
            $element.removeClass('validation-spacer');
            $guestEmail.on('change',{field: '#email'},fieldChanged);
            valid=false;
        }
        for (let i = 1; i <= guestIndex; i++) {
            requiredFields.forEach(function (value) {
                let $element = $(`#guest${i}${value}`);
                let $elementValidation = $(`#guest${i}${value}Validation`);
                if ($element.val() === "" || $element.val() === null) {
                    $elementValidation.addClass('validation-danger');
                    $elementValidation.removeClass('validation-spacer');
                    $element.on('change', {field: `#guest${i}${value}`}, fieldChanged);
                    valid = false;
                }
            })
        }

        return valid;
    }

    function fieldChanged(event) {
        let $element = $(`${event.data.field}Validation`);
        $element.addClass('validation-spacer');
        $element.removeClass('validation-danger');
        $(`${event.data.field}`).off('change', fieldChanged);
    }

    function missingFieldsModal() {
        $('body').append(`
        <div id="modal" class="modal">
            <button id="modalClose" class="btn btn-form-close">
            <i class="fa-solid fa-times"></i>
            </button>
            <div style="margin:2.5rem; font-family: Semplicita,sans-serif; font-size:1.25rem">
                You missed some required input fields, they are highlighted in red.
            </div>
        </div>`);
        $('#modalClose').on('click', function () {
            $('#modal').remove();
        })
        setTimeout(() => {
            $('#modalClose').trigger('click');
        }, 2500);
    }

    function inputFieldsToDtos() {
        for (let i = 1; i <= guestIndex; i++) {
            let val = $(`#guests${i}salute`).val();
            let thisSalute = val === undefined ? '' : val;
            thisSalute = val === null ? '' : val;
            guestDtos.push(
                {
                    firstName: $(`#guest${i}firstName`).val(),
                    lastName: $(`#guest${i}lastName`).val(),
                    grade: $(`#guest${i}grade`).val(),
                    salute: thisSalute,
                    meal: $(`#guest${i}meal`).val(),
                    requestText: $(`#guest${i}request`).val(),
                    unit: $('#unit').val(),
                    email: $('#guestEmail').val(),
                    partnerId:""
                }
            )
        }
    }
}

function CheckoutForm(data,csrf) {
    //TODO: is there a better way to stor this?
    const pk = 
    const abortPath = '/rsvp/abort/';
    const $formHolder = $('#formHolder');
    const $getTicketsFormClose = $('#getTicketsFormClose');

    let stripe;
    let elements;
    let closeTime=5;
    let intervalId;

    const paymentForm = `
    <form id="payment-form" style="margin:auto;">
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

    //starting
    $formHolder.html(buildCheckoutForm());
    $('#purchaseButton').on('click',purchaseClicked);
    $('#cancelButton').on('click',cancelClicked);
    $getTicketsFormClose.on('click',cancelClicked);

    async function purchaseClicked(e) {
        e.preventDefault();
        $formHolder.html(paymentForm);

        $('.form-box').append(`
        <div class="form-loader">
            <div class="loader"></div>
        </div>
        `)

        stripe = new Stripe(pk);
        $('#submit').on('click',handleSubmit);

        elements = stripe.elements({
            appearance: {theme: 'stripe'},
            clientSecret: data['clientSecret']
        });

        const paymentElement = elements.create("payment");
        paymentElement.mount("#payment-element");
        paymentElement.on('ready',() => {$('.form-loader').remove();});
        //if the paymentelement doesn't get mounted in 15 seconds, try again
        let elementMountTimerId = setInterval(() => {
            if(!$.trim($("#payment-element").html())) {
                showMessage("It's taking longer than usual to load the payment form");
                paymentElement.unmount();
                paymentElement.mount('#payment-element');
            } else {
                clearInterval(elementMountTimerId);
            }
        },15000);
    }

    function cancelClicked(e) {
        e.preventDefault();
        $.ajax({
            url: abortPath + data['clientSecret'],
            method:'POST',
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrf['header'],csrf['token']);
            },
            success: function () {
                $formHolder.html(aborted);
                $('#close-countdown').html(closeTime);
                intervalId=setInterval(closeTimer,1000);
                $getTicketsFormClose.on('click', function() {
                    clearInterval(intervalId);
                })
            }
        })
    }

    function closeTimer() {
        closeTime--;
        $('#close-countdown').html(closeTime);
        if(closeTime === 0) {
            $getTicketsFormClose.trigger('click');
        }
    }

    function buildCheckoutForm() {
        const fee = (data['fee'] / 100).toFixed(2);
        const total = (data['total'] /100).toFixed(2);
        const tiers = [
            {
                name: 'Soldier',
                price: 1500,
                quantity: 0
            },
            {
                name: 'Junior Leader',
                price: 2000,
                quantity: 0
            },
            {
                name: 'Company-Grade Leader 1',
                price: 3000,
                quantity: 0
            },
            {
                name: 'Company-Grade Leader 2',
                price: 4000,
                quantity: 0
            },
            {
                name: 'Senior Leader',
                price: 5000,
                quantity: 0
            }
        ]

        let rows = "";

        tiers.forEach((value) => {
            value['quantity'] = data['ticketTiers'].filter(e => e.name === value['name']).length;
        })

        tiers.forEach((value) => {
            if(value['quantity'] > 0) {
                rows += createCheckoutRow(value);
            }
        })

        return `
        <div class="flex-row">
            <div class="text-med">Would you like to buy your ticket(s) now?</div>
        </div>
        <div class="flex-row">Your cost:</div>
        <table>
            <tr>
                <th>Quantity</th>
                <th>Description</th>
                <th>Price</th>
            </tr>
            ${rows}
            <tr>
                <td></td>
                <td>Online payment fee</td>
                <td>${fee}</td>
            </tr>
            <tr>
                <td></td>
                <td>Total</td>
                <td>${total}</td>
            </tr>
        
        </table>
        <div class="flex-row">
            <button id="purchaseButton" class="btn btn-rsvp btn-rsvp-submit">Pay now</button>
            <button id="cancelButton" class="btn btn-rsvp btn-rsvp-reset">Cancel</button>        
        </div>
        <a href="https://stripe.com"><img alt="Powered by Stripe" class="stripe-logo" src="/images/stripe/stripe-black.svg"></a>
        `;
    }

    function createCheckoutRow(data) {
        return `
        <tr>
            <td>${data['quantity']}</td>
            <td>${data['name']}</td>
            <td>${(data['price']/100).toFixed(2)}</td>
        </tr>`
    }

    async function handleSubmit(e) {
        e.preventDefault()
        setLoading(true);

        const {error} = await stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: window.location.origin + '/checkout/success'
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
