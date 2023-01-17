(() => {
    'use strict'
    // forms to validate
    const forms = document.querySelectorAll('#formLogin')
    // execute validations
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
                if (!form.checkValidity()) event.preventDefault(); event.stopPropagation();
                form.classList.add('was-validated')
            },
            false)
    })
})()
