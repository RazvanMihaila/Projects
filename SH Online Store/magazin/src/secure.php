<?php
ob_start();
include 'config.php';
if (session_status() == PHP_SESSION_NONE) {
    session_start();
}

if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit();
}


function validate_and_upload_file($file) {
    $target_dir = "uploads/";
    $file_name = pathinfo($file["name"], PATHINFO_FILENAME);
    $file_extension = strtolower(pathinfo($file["name"], PATHINFO_EXTENSION));
    $unique_file_name = $file_name . "_" . uniqid() . "." . $file_extension;
    $target_file = $target_dir . $unique_file_name;
    $check = getimagesize($file["tmp_name"]);

    if ($check === false) {
        return "Fisierul nu este o imagine.";
    }

    if ($file["size"] > 500000) {
        return "Fisierul este prea mare.";
    }

    $valid_extensions = array("jpg", "png", "jpeg", "gif");
    if (!in_array($file_extension, $valid_extensions)) {
        return "Doar fisiere JPG, JPEG, PNG si GIF sunt permise.";
    }

    if (move_uploaded_file($file["tmp_name"], $target_file)) {
        return $unique_file_name;
    } else {
        return "Eroare la incarcarea fisierului.";
    }
}

// Manipularea formularului pentru adăugare/editare
$notification = "";
$notification_type = "info";
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['save'])) {
    $tip = $_POST['tip'];
    $material = $_POST['material'];
    $culoare = $_POST['culoare'];
    $marime = $_POST['marime'];
    $pret = $_POST['pret'];
    $imagine = "";

    if (isset($_FILES['imagine']) && $_FILES['imagine']['error'] == 0) {
        $upload_result = validate_and_upload_file($_FILES['imagine']);
        if (in_array($upload_result, ["Fisierul nu este o imagine.", "Fisierul este prea mare.", "Doar fisiere JPG, JPEG, PNG si GIF sunt permise.", "Eroare la incarcarea fisierului."])) {
            $error = $upload_result;
            $notification = $upload_result;
            $notification_type = "error";
        } else {
            $imagine = $upload_result;
        }
    }

    if (!isset($error)) {
        if (isset($_POST['id']) && $_POST['id'] != '') {
            // Editare haină existentă
            $id = $_POST['id'];
            $haine->update($id, $tip, $material, $culoare, $marime, $pret, $imagine);
            $notification = "Arrticolul a fost editat cu succes.";
            $notification_type = "success";
        } else {
            // Adăugare haină nouă
            $haine->create($tip, $material, $culoare, $marime, $pret, $imagine);
            $notification = "Articolul a fost adăugat cu succes.";
            $notification_type = "success";
        }
    }
}

// Ștergere haină
if (isset($_GET['delete'])) {
    $id = $_GET['delete'];
    $haine->delete($id);
    $notification = "Articolul a fost șters cu succes.";
    $notification_type = "success";
}

// Editare haină
if (isset($_GET['edit'])) {
    $id = $_GET['edit'];
    $editHaina = $haine->getById($id);
}

// Listare haine
$haineList = $haine->getAll();
?>

<!DOCTYPE html>
<html>
<head>
    <title>Gestionare Articole</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
    #notification {
        padding: 10px;
        border-radius: 5px;
        color: white;
        margin: 10px 0;
        display: none;
    }
    .success {
        background-color: #4CAF50;
    }
    .error {
        background-color: #f44336;
    }
    .info {
        background-color: #2196F3;
    }
    .warning {
        background-color: #ff9800;
    }
    </style>
</head>
<body>
    <div id="notification" class="<?php echo $notification_type; ?>">
        <?php echo $notification; ?>
    </div>

    <div class="container">
        <h1 class="mt-4">Gestionare Articole</h1>
        <p>Bun venit, <?php echo htmlspecialchars($_SESSION['username'], ENT_QUOTES, 'UTF-8'); ?>! <a href="logout.php">Logout</a></p>
        <a href="index.php" class="btn btn-primary mb-3">Vizualizare Articole</a>

        <table class="table table-bordered">
            <thead class="thead-dark">
                <tr>
                    <th>Tip</th>
                    <th>Material</th>
                    <th>Culoare</th>
                    <th>Mărime</th>
                    <th>Preț</th>
                    <th>Imagine</th>
                    <th>Acțiuni</th>
                </tr>
            </thead>
            <tbody>
            <?php if (count($haineList) > 0): ?>
                <?php foreach ($haineList as $row): ?>
                    <tr>
                        <td><?php echo htmlspecialchars($row['tip'], ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($row['material'], ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($row['culoare'], ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($row['marime'], ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><?php echo htmlspecialchars($row['pret'], ENT_QUOTES, 'UTF-8'); ?></td>
                        <td><img src="uploads/<?php echo htmlspecialchars($row['imagine'], ENT_QUOTES, 'UTF-8'); ?>" alt="Imagine" width="50"></td>
                        <td>
                            <a href="secure.php?edit=<?php echo $row['id']; ?>" class="btn btn-warning btn-sm">Edit</a>
                            <a href="secure.php?delete=<?php echo $row['id']; ?>" class="btn btn-danger btn-sm delete-link">Delete</a>
                        </td>
                    </tr>
                <?php endforeach; ?>
            <?php else: ?>
                <tr><td colspan="7">Nu există articole</td></tr>
            <?php endif; ?>
            </tbody>
        </table>

        <h2 class="mt-4">Adaugă/Editează Articole</h2>
        <?php if (isset($error)) { echo "<div class='alert alert-danger'>$error</div>"; } ?>
        <form method="post" action="secure.php" enctype="multipart/form-data">
            <input type="hidden" name="id" value="<?php echo isset($editHaina) ? $editHaina['id'] : ''; ?>">
            <div class="form-group">
                <label>Tip:</label>
                <input type="text" name="tip" class="form-control" value="<?php echo isset($editHaina) ? htmlspecialchars($editHaina['tip'], ENT_QUOTES, 'UTF-8') : ''; ?>" required>
            </div>
            <div class="form-group">
                <label>Material:</label>
                <input type="text" name="material" class="form-control" value="<?php echo isset($editHaina) ? htmlspecialchars($editHaina['material'], ENT_QUOTES, 'UTF-8') : ''; ?>" required>
            </div>
            <div class="form-group">
                <label>Culoare:</label>
                <input type="text" name="culoare" class="form-control" value="<?php echo isset($editHaina) ? htmlspecialchars($editHaina['culoare'], ENT_QUOTES, 'UTF-8') : ''; ?>" required>
            </div>
            <div class="form-group">
                <label>Mărime:</label>
                <input type="text" name="marime" class="form-control" value="<?php echo isset($editHaina) ? htmlspecialchars($editHaina['marime'], ENT_QUOTES, 'UTF-8') : ''; ?>" required>
            </div>
            <div class="form-group">
                <label>Preț:</label>
                <input type="text" name="pret" class="form-control" value="<?php echo isset($editHaina) ? htmlspecialchars($editHaina['pret'], ENT_QUOTES, 'UTF-8') : ''; ?>" required>
            </div>
            <div class="form-group">
                <label>Imagine:</label>
                <input type="file" name="imagine" class="form-control" <?php echo isset($editHaina) ? '' : 'required'; ?>>
            </div>
            <input type="submit" name="save" class="btn btn-success" value="Salvează">
        </form>
    </div>

    <script>
        function showNotification(message, type) {
            const notification = document.querySelector("#notification");
            if (notification) {
                notification.textContent = message;
                notification.className = '';
                notification.classList.add(type);
                notification.style.display = "block";
                setTimeout(function() {
                    notification.style.display = "none";
                }, 5000); 
            }
        }

        document.addEventListener("DOMContentLoaded", function() {
            // Verifică dacă există o notificare și setează tipul acesteia
            const notification = document.querySelector("#notification");
            if (notification && notification.textContent.trim() !== "") {
                notification.style.display = "block";
                
                if (!notification.classList.contains('success') && 
                    !notification.classList.contains('error') &&
                    !notification.classList.contains('info') &&
                    !notification.classList.contains('warning')) {
                    notification.classList.add('info');
                }
                setTimeout(function() {
                    notification.style.display = "none";
                }, 5000);
            }
        });
    </script>
    <script src="js/validation.js"></script>
    <script src="js/confirmDelete.js"></script>
</body>
</html>