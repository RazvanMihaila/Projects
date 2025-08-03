<?php
$servername = "mysql_db";
$username = "root";
$password = "toor";
$dbname = "magazin";

// Creare conexiune
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificare conexiune
if ($conn->connect_error) {
    die("Conexiunea a eșuat: " . $conn->connect_error);
}

session_start();

require_once __DIR__ . '/vendor/autoload.php';
$dotenv = Dotenv\Dotenv::createImmutable(__DIR__);
$dotenv->load();

require_once 'User.php';
require_once 'Haine.php';

$user = new User($conn);
$haine = new Haine($conn);

if (!isset($_SESSION['user_id']) && isset($_COOKIE['rememberme'])) {
    $token = $_COOKIE['rememberme'];
    $userDetails = $user->getUserByRememberToken($token);
    if ($userDetails) {
        $_SESSION['user_id'] = $userDetails['id'];
        $_SESSION['username'] = $userDetails['username'];
    }
}

