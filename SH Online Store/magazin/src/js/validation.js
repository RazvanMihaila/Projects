document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");
    form.addEventListener("submit", function(event) {
        const tip = document.querySelector("input[name='tip']").value.trim();
        const material = document.querySelector("input[name='material']").value.trim();
        const culoare = document.querySelector("input[name='culoare']").value.trim();
        const marime = document.querySelector("input[name='marime']").value.trim();
        const pret = document.querySelector("input[name='pret']").value.trim();

        if (!tip || !material || !culoare || !marime || !pret) {
            alert("Toate câmpurile sunt obligatorii!");
            event.preventDefault();
            showNotification('Toate câmpurile sunt obligatorii!', 'error');
        } else {
            showNotification('Formularul a fost trimis cu succes.', 'success');
        }
    });
});
