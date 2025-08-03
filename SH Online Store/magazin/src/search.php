<?php
include 'config.php';



$searchResults = [];
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['search'])) {
    $searchTerm = $_POST['searchTerm'];
    $sql = "SELECT * FROM haine WHERE tip LIKE ? OR material LIKE ? OR culoare LIKE ?";
    $stmt = $conn->prepare($sql);
    $likeTerm = '%' . $searchTerm . '%';
    $stmt->bind_param("sss", $likeTerm, $likeTerm, $likeTerm);
    $stmt->execute();
    $searchResults = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
}
?>

<!DOCTYPE html>
<html>
<head>
    <title>Căutare Articol </title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container">
    <h2 class="mt-5">Cauta Articol</h2>
    <form method="post" action="search.php">
        <div class="form-group">
            <label>Cuvânt cheie:</label>
            <input type="text" name="searchTerm" class="form-control" required>
        </div>
        <button type="submit" name="search" class="btn btn-primary">Caută</button>
    </form>

    <?php if (!empty($searchResults)) { ?>
        <h3 class="mt-5">Rezultate Căutare</h3>
        <table class="table table-bordered">
            <thead class="thead-dark">
                <tr>
                    <th>Tip</th>
                    <th>Material</th>
                    <th>Culoare</th>
                    <th>Mărime</th>
                    <th>Preț</th>
                    <th>Imagine</th>
                </tr>
            </thead>
            <tbody>
            <?php foreach ($searchResults as $row) { ?>
                <tr>
                    <td><?php echo $row['tip']; ?></td>
                    <td><?php echo $row['material']; ?></td>
                    <td><?php echo $row['culoare']; ?></td>
                    <td><?php echo $row['marime']; ?></td>
                    <td><?php echo $row['pret']; ?></td>
                    <td><img src="uploads/<?php echo $row['imagine']; ?>" alt="Imagine" width="50"></td>
                </tr>
            <?php } ?>
            </tbody>
        </table>
    <?php } else if ($_SERVER['REQUEST_METHOD'] == 'POST') { ?>
        <div class="alert alert-info mt-5">Nu s-au găsit rezultate pentru căutarea dvs.</div>
    <?php } ?>
    
    <a href="index.php" class="btn btn-secondary mt-3">Înapoi la pagina principală</a>
</body>
</html>
