<?php
include 'config.php';
if (session_status() == PHP_SESSION_NONE) {
    session_start();
}


$notification = "";
$haineList = [];

$haineList = $haine->getAll();
?>

<!DOCTYPE html>
<html>
<head>
    <title>Vizualizare Articole</title>
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="style.css" rel="stylesheet">
    <script async defer crossorigin="anonymous" src="https://connect.facebook.net/en_US/sdk.js#xfbml=1&version=v11.0" nonce="6lgx9XZC"></script>
    


    <script async defer src="https://maps.googleapis.com/maps/api/js?key=<?= getenv('GOOGLE_API_KEY') ?>"></script>
    <script type="text/javascript">
        function initMap() {
            var uaicIasi = {lat: 47.173534, lng: 27.574685};
            var map = new google.maps.Map(document.getElementById('map'), {
                zoom: 15,
                center: uaicIasi
            });
            var marker = new google.maps.Marker({
                position: uaicIasi,
                map: map
            });
        }

        function drawCanvas() {
            var canvas = document.getElementById('myCanvas');
            if (canvas.getContext) {
                var ctx = canvas.getContext('2d');
                ctx.clearRect(0, 0, canvas.width, canvas.height);
                ctx.fillStyle = 'lightgrey';
                ctx.fillRect(0, 0, canvas.width, canvas.height);
                ctx.fillStyle = 'blue';
                ctx.fillRect(50, 50, 100, 100);
                ctx.fillStyle = 'red';
                ctx.beginPath();
                ctx.arc(150, 150, 50, 0, Math.PI * 2, true);
                ctx.fill();
                ctx.fillStyle = 'green';
                ctx.beginPath();
                ctx.moveTo(50, 150);
                ctx.lineTo(100, 200);
                ctx.lineTo(0, 200);
                ctx.closePath();
                ctx.fill();
                ctx.fillStyle = 'black';
                ctx.font = '20px Arial';
                ctx.fillText('Canvas Drawing', 50, 30);
            }
        }
        document.addEventListener('DOMContentLoaded', drawCanvas);
    </script>
</head>
<body class="container">
    <div class="welcome-section card shadow-sm p-4 mb-4">
        <h1 class="display-4 mb-1">👕 Vizualizare Articole</h1>
        <p class="lead text-muted">Bun venit! Alege o opțiune mai jos pentru a începe.</p>

        <div class="d-flex flex-wrap align-items-center gap-2 mt-3">
            <a href="search.php" class="btn btn-primary me-2">
                🔍 Caută articol
            </a>
            <a href="secure.php" class="btn btn-secondary me-2">
                🛠️ Gestionare articole
            </a>
            <div class="me-2">
                <div class="fb-like" data-href="http://yourwebsite.com" data-layout="button_count" data-action="like" data-size="small" data-share="true"></div>
            </div>
            <a href="https://twitter.com/share" class="twitter-share-button" data-text="Check out this awesome site!" data-url="http://yourwebsite.com" data-hashtags="example,website">Tweet</a>
            <script async src="https://platform.twitter.com/widgets.js" charset="utf-8"></script>
        </div>
    </div>


    

    <?php if ($notification): ?>
        <div id="notification" class="alert alert-info">
            <?php echo $notification; ?>
        </div>
    <?php endif; ?>
    <div class="row">
        <?php foreach ($haineList as $row): ?>
        <div class="col-md-4 mb-4">
            <div class="card h-100 shadow-sm">
                <img src="uploads/<?php echo htmlspecialchars($row['imagine']); ?>" class="card-img-top" alt="Imagine articol">
                <div class="card-body">
                    <h5 class="card-title">Tip: <?php echo htmlspecialchars($row['tip']); ?></h5>
                    <p class="card-text">Material: <?php echo htmlspecialchars($row['material']); ?></p>
                    <p class="card-text">Culoare: <?php echo htmlspecialchars($row['culoare']); ?></p>
                    <p class="card-text">Mărime: <?php echo htmlspecialchars($row['marime']); ?> | Preț: <?php echo htmlspecialchars($row['pret']); ?> lei</p>
                </div>
            </div>
        </div>
        <?php endforeach; ?>
    </div>
    
    <div id="map" style="height: 400px; width: 100%; margin-top: 20px;"></div>

    <div class="container mt-4">
        <h2>Clip YouTube</h2>
        <div class="embed-responsive embed-responsive-16by9">
            <iframe class="embed-responsive-item" src="https://www.youtube.com/embed/EEZcumLH7SY" allowfullscreen></iframe>
        </div>
    </div>

    <div class="container mt-4">
        <h2>Clip MP4 Local</h2>
        <video width="100%" height="auto" controls>
            <source src="videos/local_video.mp4" type="video/mp4">
            Browserul tău nu suportă elementul video.
        </video>
        <a href="videos/local_video.mp4" download class="btn btn-primary mt-2">Descarcă Clipul MP4</a>
    </div>

    <div class="container mt-4">
        <h2>Fișier MP3 Local</h2>
        <audio controls>
            <source src="audio/local_audio.mp3" type="audio/mpeg">
            Browserul tău nu suportă elementul audio.
        </audio>
        <a href="audio/local_audio.mp3" download class="btn btn-primary mt-2">Descarcă Fișierul MP3</a>
    </div>

  
    <div class="canvas-container">
        
        <canvas id="myCanvas" width="400" height="400"></canvas>
    </div>

    
    <div class="svg-container">
        
        <svg width="400" height="400">
            <rect x="50" y="50" width="100" height="100" style="fill:rgb(0,0,255);stroke-width:3;stroke:rgb(0,0,0)" />
            <circle cx="250" cy="250" r="50" stroke="black" stroke-width="3" fill="red" />
            <text x="10" y="390" fill="black" font-size="20">SVG Text</text>
        </svg>
    </div>

    <script src="js/validation.js"></script>
    <script src="js/confirmDelete.js"></script>
    <script src="js/notifications.js"></script>
</body>
</html>









