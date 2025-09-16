<?php
include "db.php";  // your DB connection

if (isset($_GET['id'])) {
    $id = intval($_GET['id']);
    $query = "SELECT id, name, email, contact FROM users WHERE id = $id AND role = 'customer' LIMIT 1";
    $result = mysqli_query($conn, $query);

    if ($row = mysqli_fetch_assoc($result)) {
        echo json_encode($row);
    } else {
        echo json_encode(["error" => "Customer not found"]);
    }
} else {
    echo json_encode(["error" => "Missing ID"]);
}
?>
