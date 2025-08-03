 function showNotification(message, type) {
    const notification = document.querySelector("#notification");
    if (notification) {
        notification.textContent = message;
        notification.className = ''; // Reseteaza 
        notification.classList.add(type);
        notification.style.display = "block";
        setTimeout(function() {
            notification.style.display = "none";
        }, 5000); // Ascunde notificarea după 5 secunde
    }
}

document.addEventListener("DOMContentLoaded", function() {
     
     showNotification('Acțiunea a fost completată cu succes.', 'success');
     showNotification('A apărut o eroare la completarea acțiunii.', 'error');
});
