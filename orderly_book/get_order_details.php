<?php
header('Content-Type: application/json');
require_once('db.php');

if (!isset($_GET['order_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'order_id is required']);
    exit;
}

$order_id = intval($_GET['order_id']);

$sql = "SELECT 
            o.id AS order_id,
            o.customer_id,
            o.owner_id,
            o.product_id,
            p.name AS product_name,
            o.quantity,
            p.price AS product_price,
            (o.quantity * p.price) AS line_total,
            o.total_price,
            o.status,
            o.created_at AS order_date
        FROM orders o
        LEFT JOIN products p ON o.product_id = p.id
        WHERE o.id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $order_id);
$stmt->execute();
$result = $stmt->get_result();

$orderItems = [];
$totalPrice = 0; // store total price

while ($row = $result->fetch_assoc()) {
    $orderItems[] = [
        'product_name' => $row['product_name'],
        'quantity' => intval($row['quantity']),
        'product_price' => floatval($row['product_price']),
        'line_total' => floatval($row['line_total'])
    ];
    // assign total_price from row (all rows have same total_price)
    $totalPrice = floatval($row['total_price']);
}

if (count($orderItems) > 0) {
    echo json_encode([
        'status' => 'success',
        'order_id' => $order_id,
        'items' => $orderItems,
        'total_price' => $totalPrice
    ]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Order not found']);
}
