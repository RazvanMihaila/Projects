<?php
include 'config.php';

$recaptcha_secret = getenv('RECAPTCHA_SECRET_KEY');


if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (session_status() == PHP_SESSION_NONE) {
        session_start();
    }

    $username = $_POST['username'];
    $password = $_POST['password'];
    $remember = isset($_POST['remember']);
    $recaptcha_response = $_POST['g-recaptcha-response'];

    // Verificare reCAPTCHA
    $response = file_get_contents("https://www.google.com/recaptcha/api/siteverify?secret=$recaptcha_secret&response=$recaptcha_response");
    $response_keys = json_decode($response, true);

    if (intval($response_keys["success"]) !== 1) {
        $error = "Verificarea reCAPTCHA a eșuat. Încearcă din nou.";
    } else {
        // Autentificare utilizator 
        $userDetails = $user->authenticate($username, $password);
        if ($userDetails) {
            $_SESSION['user_id'] = $userDetails['id'];
            $_SESSION['username'] = $userDetails['username'];
            
            if ($remember) {
                // Creare token unic pentru utilizator
                $token = bin2hex(random_bytes(16));
                setcookie("rememberme", $token, time() + (86400 * 30), "/"); // 86400 = 1 zi
                
                // Salvare token în baza de date 
                $user->setRememberToken($userDetails['id'], $token);
            }

            // Redirecționare către pagina securizată
            header('Location: secure.php');
            exit();
        } else {
            $error = "Nume utilizator sau parolă incorectă!";
        }
    }
}
?>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>
<body class="container">
    <h2 class="mt-5">Login</h2>
    <?php if (isset($error)) { echo "<div class='alert alert-danger'>$error</div>"; } ?>
    <form method="post" action="login.php">
        <div class="form-group">
            <label>Username:</label>
            <input type="text" name="username" class="form-control" required>
        </div>
        <div class="form-group">
            <label>Password:</label>
            <input type="password" name="password" class="form-control" required>
        </div>
        <div class="form-group form-check">
            <input type="checkbox" name="remember" class="form-check-input">
            <label class="form-check-label">Remember Me</label>
        </div>
        <div class="g-recaptcha" data-sitekey="<?= getenv('RECAPTCHA_SITE_KEY') ?>"></div>
 
        <button type="submit" class="btn btn-primary">Login</button>
    </form>



    <br>
    <br>
    <a href="index.php" class="btn btn-primary mb-3">Vizualizare Articole</a>
</body>
</html>



