<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

require 'db.php';

$owner_id = isset($_GET['owner_id']) ? intval($_GET['owner_id']) : 0;

if($owner_id > 0){
    $stmt = $conn->prepare("SELECT id, owner_id, name, price FROM products WHERE owner_id = ?");
    $stmt->bind_param("i", $owner_id);
} else {
    $stmt = $conn->prepare("SELECT id, owner_id, name, price FROM products");
}
$stmt->execute();
$result = $stmt->get_result();

$products = [];
while($row = $result->fetch_assoc()){
    $products[] = $row;
}

echo json_encode([
    "status" => "success",
    "products" => $products
]);

