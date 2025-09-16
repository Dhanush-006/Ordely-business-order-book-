<?php
$host = "localhost";
$user = "root";    // change if needed
$pass = "";        // change if needed
$dbname = "orderly_book";

$conn = new mysqli($host, $user, $pass, $dbname);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}
?>
