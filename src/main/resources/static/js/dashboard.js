document.addEventListener("DOMContentLoaded", function() {
    const userHasAccount = document.getElementById('userHasAccount').value === 'true';
    const serviceUser = document.getElementById('serviceUser').value === 'true';

    if (!serviceUser) {
        window.location.href = userHasAccount ? '/account/account-overview' : '/account/create-account';
    }
});
