<?php
$hash = '$2y$10$MzBQHFw7dI.6XtHkIxmUlu7xS2WwDn8BhSE1mFhOe8YPi2PQiJu3C'; // din phpMyAdmin
$parola = 'Razvy1234';

if (password_verify($parola, $hash)) {
    echo "✅ parola e corectă!";
} else {
    echo "❌ parola NU e validă!";
}
