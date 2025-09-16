<?php
require 'db.php';

$owner_id = $_GET['owner_id'];

$sql = "SELECT o.id, u.name as customer_name, u.contact, p.name as product_name, 
               o.quantity, o.total_price, o.status, o.created_at
        FROM orders o
        JOIN products p ON o.product_id = p.id
        JOIN users u ON o.customer_id = u.id
        WHERE p.owner_id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $owner_id);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];
while ($row = $result->fetch_assoc()) {
    $orders[] = $row;
}

echo json_encode(["status" => "success", "orders" => $orders]);
?>
