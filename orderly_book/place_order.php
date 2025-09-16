<?php
header('Content-Type: application/json');
require 'db.php';

$data = json_decode(file_get_contents('php://input'), true);

if (!$data || !isset($data['customer_id'], $data['products'])) {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
    exit;
}

$customer_id = intval($data['customer_id']);
$products = $data['products'];

$total_price = 0;

foreach ($products as $item) {
    if (!isset($item['product_id'], $item['quantity'])) continue;

    $product_id = intval($item['product_id']);
    $quantity = intval($item['quantity']);

    // Get product price and owner_id
    $stmt = $conn->prepare("SELECT price, owner_id FROM products WHERE id = ?");
    $stmt->bind_param("i", $product_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $product = $result->fetch_assoc();
    if (!$product) continue;

    $price = floatval($product['price']);
    $owner_id = intval($product['owner_id']);
    $subtotal = $price * $quantity;
    $total_price += $subtotal;

    // Insert into orders table
    $insert = $conn->prepare("INSERT INTO orders (customer_id, owner_id, product_id, quantity, total_price, status) VALUES (?, ?, ?, ?, ?, 'pending')");
    $insert->bind_param("iiiid", $customer_id, $owner_id, $product_id, $quantity, $subtotal);
    $insert->execute();
    $insert->close();
}

echo json_encode([
    "status" => "success",
    "message" => "Order placed successfully",
    "total_price" => $total_price
]);

$conn->close();
?>
