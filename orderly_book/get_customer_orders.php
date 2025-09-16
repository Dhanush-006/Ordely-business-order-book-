<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require 'db.php';

if (!isset($_GET['customer_id'])) {
    echo json_encode(["status" => "error", "message" => "customer_id is required"]);
    exit;
}

$customer_id = intval($_GET['customer_id']);

$sql = "SELECT 
            o.id AS order_id,
            u.name AS owner_name,
            o.total_price,
            o.status,
            DATE_FORMAT(o.created_at, '%d-%m-%Y %H:%i') AS order_date
        FROM orders o
        JOIN users u ON o.owner_id = u.id
        WHERE o.customer_id = ?
          AND u.role = 'owner'
        ORDER BY o.created_at DESC";

$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Query preparation failed"]);
    exit;
}

$stmt->bind_param("i", $customer_id);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];
while ($row = $result->fetch_assoc()) {
    $orders[] = $row;
}

echo json_encode(["status" => "success", "orders" => $orders]);

$stmt->close();
$conn->close();
?>
