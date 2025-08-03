document.addEventListener("DOMContentLoaded", function() {
    const deleteLinks = document.querySelectorAll("a.delete-link");
    deleteLinks.forEach(function(link) {
        link.addEventListener("click", function(event) {
            console.log("Delete link clicked");
            if (confirm("Ești sigur că vrei să ștergi această haină?")) {
                console.log("Deletion confirmed");
                showNotification('Haina a fost ștearsă cu succes.', 'success');
            } else {
                console.log("Deletion cancelled");
                event.preventDefault();
                showNotification('Acțiunea de ștergere a fost anulată.', 'info');
            }
        });
    });
});
