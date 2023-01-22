(() => {
    'use strict'

    // forms to validate
    const forms = document.querySelectorAll('#formRegistration')

    // execute validations
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            // stop send POST via Web page to send via FETCH API
            event.preventDefault(); event.stopPropagation();

            if (form.checkValidity()) {
                // getting XSRF-TOKEN
                const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
                // sending request to /api/account/register
                const urlHost = window.location.origin
                const response = fetch(urlHost + "/api/account/register", {
                    method: 'POST',
                    mode: 'same-origin',
                    cache: 'no-cache',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-XSRF-TOKEN': form._csrf.value,
                        'Cookie': document.cookie
                    },
                    referrerPolicy: 'no-referrer',
                    body: JSON.stringify(
                        {
                            "email": form.email.value,
                            "password": form.password.value,
                            "confirmationPassword": form.confirmationPassword.value,
                            "firstName": form.firstName.value,
                            "lastName": form.lastName.value,
                        })
                }).then(async response => {
                    let body = await response.json();
                    console.log(body)
                    switch (body.status) {
                        case "SUCCESS":
                            // make auto login
                            const formLogin = document.getElementById('formLogin')
                            const usernameTag = document.getElementById('email-input');
                            const passwordTag = document.getElementById('password-input');

                            usernameTag.value = form.email.value;
                            passwordTag.value = form.password.value;
                            formLogin.submit();
                            break;
                        default:
                            // must show errors
                            if (body.errors && body.errors.length > 0) {
                                body.errors.forEach(err => createNotification("Registration error", err.defaultMessage, "Now"))
                            }

                            // if unique error
                            if (body.error && body.error != "") {
                                createNotification("Registration error", body.error, "Now")
                            }
                    }
                }, err => {
                    console.log(err)
                });
            }
            form.classList.add('was-validated')
        }, false)
    })

})()
