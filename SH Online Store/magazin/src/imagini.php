<?php
include 'config.php';
include 'Imagine.php';

if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit();
}

$imagine = new Imagine($conn);

// Funcție de validare și încărcare fișier
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

    if ($file["size"] > 5000000) { // 5MB max
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
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['save'])) {
    $nume = $_POST['nume'];
    $cale = "";

    if (isset($_FILES['imagine']) && $_FILES['imagine']['error'] == 0) {
        $upload_result = validate_and_upload_file($_FILES['imagine']);
        if (in_array($upload_result, ["Fisierul nu este o imagine.", "Fisierul exista deja.", "Fisierul este prea mare.", "Doar fisiere JPG, JPEG, PNG si GIF sunt permise.", "Eroare la incarcarea fisierului."])) {
            $error = $upload_result;
        } else {
            $cale = $upload_result;
        }
    }

    if (!isset($error)) {
        if (isset($_POST['id']) && $_POST['id'] != '') {
            // Editare imagine existentă
            $id = $_POST['id'];
            if ($imagine->update($id, $nume, $cale)) {
                $notification = "Imaginea a fost actualizată cu succes!";
            } else {
                $notification = "Eroare la actualizarea imaginii!";
            }
        } else {
            // Adăugare imagine nouă
            if ($imagine->create($nume, $cale)) {
                $notification = "Imaginea a fost adăugată cu succes!";
            } else {
                $notification = "Eroare la adăugarea imaginii!";
            }
        }
    }
}

// Ștergere imagine
if (isset($_GET['delete'])) {
    $id = $_GET['delete'];
    if ($imagine->delete($id)) {
        $notification = "Imaginea a fost ștearsă cu succes!";
    } else {
        $notification = "Eroare la ștergerea imaginii!";
    }
}

// Editare imagine
if (isset($_GET['edit'])) {
    $id = $_GET['edit'];
    $editImagine = $imagine->getById($id);
}

// Listare imagini
$imaginiList = $imagine->getAll();
?>

<!DOCTYPE html>
<html>
<head>
    <title>Gestionare Imagini</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script src="js/validation.js"></script>
    <script src="js/confirmDelete.js"></script>
    <script src="js/notifications.js"></script>
</head>
<body class="container">

    <h1 class="mt-4">Gestionare Imagini</h1>
    <p>Bun venit, <?php echo $_SESSION['username']; ?>! <a href="logout.php">Logout</a></p>
    <a href="secure.php" class="btn btn-primary mb-3">Înapoi la zona securizată</a>

    <?php if ($notification): ?>
        <div id="notification" class="alert alert-info">
            <?php echo $notification; ?>
        </div>
    <?php endif; ?>

    <table class="table table-bordered">
        <thead class="thead-dark">
            <tr>
                <th>Nume</th>
                <th>Imagine</th>
                <th>Acțiuni</th>
            </tr>
        </thead>
        <tbody>
        <?php
        if ($imaginiList->num_rows > 0) {
            while($row = $imaginiList->fetch_assoc()) {
                echo "<tr>
                        <td>{$row['nume']}</td>
                        <td><img src='uploads/{$row['cale']}' alt='Imagine' width='50'></td>
                        <td>
                            <a href='imagini.php?edit={$row['id']}' class='btn btn-warning btn-sm'>Edit</a>
                            <a href='imagini.php?delete={$row['id']}' class='btn btn-danger btn-sm delete-link'>Delete</a>
                        </td>
                      </tr>";
            }
        } else {
            echo "<tr><td colspan='3'>Nu există imagini</td></tr>";
        }
        ?>
        </tbody>
    </table>

    <h2 class="mt-4">Adaugă/Editează Imagine</h2>
    <?php if (isset($error)) { echo "<div class='alert alert-danger'>$error</div>"; } ?>
    <form method="post" action="imagini.php" enctype="multipart/form-data">
        <input type="hidden" name="id" value="<?php echo isset($editImagine) ? $editImagine['id'] : ''; ?>">
        <div class="form-group">
            <label>Nume:</label>
            <input type="text" name="nume" class="form-control" value="<?php echo isset($editImagine) ? $editImagine['nume'] : ''; ?>" required>
        </div>
        <div class="form-group">
            <label>Imagine:</label>
            <input type="file" name="imagine" class="form-control" <?php echo isset($editImagine) ? '' : 'required'; ?>>
        </div>
        <input type="submit" name="save" class="btn btn-success" value="Salvează">
    </form>
</body>
</html>
