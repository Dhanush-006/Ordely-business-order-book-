<?php
header('Content-Type: application/json');
require_once('db.php');

if (!isset($_GET['owner_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'owner_id is required']);
    exit;
}

$owner_id = intval($_GET['owner_id']);

$sql = "SELECT 
            o.id AS order_id,
            o.customer_id,
            o.total_price,
            o.status,
            o.created_at AS order_date,  -- ✅ use created_at as order_date
            u.name AS customer_name
        FROM orders o
        LEFT JOIN users u 
            ON o.customer_id = u.id AND u.role = 'customer'
        WHERE o.owner_id = ?
        ORDER BY o.id DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $owner_id);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];
while ($row = $result->fetch_assoc()) {
    if (!$row['customer_name']) $row['customer_name'] = "Unknown";
    $orders[] = [
        'order_id' => intval($row['order_id']),
        'customer_id' => intval($row['customer_id']),
        'customer_name' => $row['customer_name'],
        'total_price' => $row['total_price'],
        'status' => $row['status'],
        'order_date' => $row['order_date']   // ✅ now coming from created_at
    ];
}

echo json_encode(['status' => 'success', 'orders' => $orders]);
