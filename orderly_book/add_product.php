<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require 'db.php'; // Make sure $conn is your MySQLi connection

// Initialize variables
$owner_id = 0;
$name = '';
$price = 0;

// 1️⃣ Check if form-data is sent
if (!empty($_POST)) {
    $owner_id = isset($_POST['owner_id']) ? intval($_POST['owner_id']) : 0;
    $name     = isset($_POST['name']) ? trim($_POST['name']) : '';
    $price    = isset($_POST['price']) ? floatval($_POST['price']) : 0;
} else {
    // 2️⃣ Check if JSON is sent
    $json = file_get_contents("php://input");
    $data = json_decode($json, true);

    if (!empty($data)) {
        $owner_id = isset($data['owner_id']) ? intval($data['owner_id']) : 0;
        $name     = isset($data['name']) ? trim($data['name']) : '';
        $price    = isset($data['price']) ? floatval($data['price']) : 0;
    }
}

// Validate required fields
if ($owner_id <= 0 || empty($name) || $price <= 0) {
    echo json_encode([
        "status" => "error",
        "message" => "Missing required fields: owner_id, name, price",
        "debug_post" => $_POST,
        "debug_json" => $data ?? null
    ]);
    exit;
}

// Insert into products table
$stmt = $conn->prepare("INSERT INTO products (owner_id, name, price) VALUES (?, ?, ?)");
$stmt->bind_param("isd", $owner_id, $name, $price);

if ($stmt->execute()) {
    echo json_encode([
        "status" => "success",
        "message" => "Product added successfully",
        "product_id" => $stmt->insert_id
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Database insert failed: " . $stmt->error
    ]);
}
