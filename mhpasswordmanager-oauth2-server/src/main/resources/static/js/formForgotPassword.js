(() => {
    'use strict'
    // forms to validate
    const formsStep1 = document.querySelectorAll('#formForgotPasswordStep1')
    const formsStep2 = document.querySelectorAll('#formForgotPasswordStep2')
    const btnBlockSkipStep2 = document.getElementById('block-skip-step2');
    const baseHref = (document.getElementsByTagName('base')[0] || {href: window.location.origin + '/'}).href;
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    const jsonHeaders = () => {
        const headers = {'Content-Type': 'application/json'};
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        return headers;
    };
    const showBoxForStep2 = () => {
        formsStep2.forEach(form => {
            btnBlockSkipStep2.classList.add('d-none')
            form.classList.remove('d-none')
        })
    }

    // enable step 2 on click in button
    btnBlockSkipStep2.addEventListener('click', showBoxForStep2)
    // call backend api when send email to start recovery account
    Array.from(formsStep1).forEach(form => {
        form.addEventListener('submit', event => {
            // stop send POST via Web page to send via FETCH API
            event.preventDefault(); event.stopPropagation();
            // validate
            if (form.identification.value.trim() === '') {
                createNotification("Registration error", "Preencha corretamente com seu e-mail e/ou usuario.", "Now")
                return
            }

            if (form.checkValidity()) {
                // sending request to /api/account/forgot
                fetch(baseHref + "api/account/forgot/step1", {
                    method: 'POST',
                    mode: 'same-origin',
                    credentials: 'same-origin',
                    cache: 'no-cache',
                    headers: jsonHeaders(),
                    referrerPolicy: 'no-referrer',
                    body: JSON.stringify(
                        {
                            "identification": form.identification.value,
                        })
                }).then(result => {
                    showBoxForStep2()
                })
            }
            form.classList.add('was-validated')
        }, false)
    })

    Array.from(formsStep2).forEach(form => {
        form.addEventListener('submit', event => {
            // stop send POST via Web page to send via FETCH API
            event.preventDefault(); event.stopPropagation();
            form.classList.add('was-validated')

            if (form.password.value !== form.passwordConfirm.value) {
                createNotification("Registration error", "Sua senha deve ser identica a confirmação.", "Now")
                return
            }

            if (form.checkValidity()) {
                // sending request to /api/account/forgot
                fetch(baseHref + "api/account/forgot/step2", {
                    method: 'POST',
                    mode: 'same-origin',
                    credentials: 'same-origin',
                    cache: 'no-cache',
                    headers: jsonHeaders(),
                    referrerPolicy: 'no-referrer',
                    body: JSON.stringify(
                        {
                            "code": form.code.value,
                            "password": form.password.value
                        })
                }).then(async response => {
                    let body = await response.json();
                    switch (body.status) {
                        case "SUCCESS":
                            createNotification('Resetado com sucesso!', "Sua senha foi redefinida com sucesso. Faça o login com sua nova credencial", "Now")
                            break;
                        default:
                            if (body.errors && body.errors.length > 0) body.errors.forEach(err => createNotification("Registration error", err.defaultMessage, "Now"))
                            if (body.error && body.error !== "") createNotification("Registration error", body.error, "Now")
                    }
                }, err => console.log(err));
            }
        }, false)
    })
})();
