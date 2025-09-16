<?php
include 'db_connection.php'; // your DB connection

$order_id = isset($_GET['order_id']) ? intval($_GET['order_id']) : 0;

if ($order_id <= 0) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid order ID']);
    exit;
}

// Fetch products for the order
$sql = "SELECT p.id, p.name, p.price, op.quantity 
        FROM order_products op 
        JOIN products p ON op.product_id = p.id 
        WHERE op.order_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $order_id);
$stmt->execute();
$result = $stmt->get_result();

$products = [];
while($row = $result->fetch_assoc()) {
    $products[] = [
        'id' => $row['id'],
        'name' => $row['name'],
        'price' => $row['price'],
        'quantity' => intval($row['quantity'])
    ];
}

echo json_encode([
    'status' => 'success',
    'products' => $products
]);
?>
