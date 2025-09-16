<?php
require 'db.php';

$owner_id = $_GET['owner_id'] ?? 0;

if($owner_id > 0){
    $stmt = $conn->prepare("SELECT COUNT(*) as order_count FROM orders WHERE owner_id=?");
    $stmt->bind_param("i", $owner_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    echo $row['order_count'];
} else {
    echo 0;
}
?>
