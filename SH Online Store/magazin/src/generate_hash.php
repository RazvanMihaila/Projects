<?php
$parola = 'Razvy1234';
$hash = password_hash($parola, PASSWORD_DEFAULT);
echo $hash;
