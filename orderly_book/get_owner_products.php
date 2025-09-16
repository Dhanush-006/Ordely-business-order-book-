<?php
header('Content-Type: application/json');
require 'db.php';

// Check owner_id
if(!isset($_GET['owner_id'])){
    echo json_encode(["status"=>"error","message"=>"owner_id is required"]);
    exit;
}

$owner_id = intval($_GET['owner_id']);

// Fetch products for the owner
$sql = "SELECT id AS product_id, owner_id, name, price, created_at
        FROM products
        WHERE owner_id = ?
        ORDER BY created_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $owner_id);
$stmt->execute();
$result = $stmt->get_result();

$products = [];
while($row = $result->fetch_assoc()){
    $products[] = [
        "id" => $row['product_id'],
        "owner_id" => $row['owner_id'],
        "name" => $row['name'],
        "price" => $row['price'],
        "created_at" => $row['created_at']
    ];
}

echo json_encode([
    "status" => "success",
    "products" => $products
]);
?>
