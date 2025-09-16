<?php
header('Content-Type: application/json');

$host = "localhost";
$db = "your_database";
$user = "your_db_user";
$pass = "your_db_pass";

// Connect to database
$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed"]));
}

// Get query parameters
$owner_id = isset($_GET['owner_id']) ? intval($_GET['owner_id']) : 0;
$customer_id = isset($_GET['customer_id']) ? intval($_GET['customer_id']) : 0;

if ($owner_id <= 0 || $customer_id <= 0) {
    echo json_encode(["status" => "error", "message" => "Invalid parameters"]);
    exit;
}

// Fetch customer orders for this owner
$sqlOrders = "SELECT id, product_name, quantity, total_price, order_date 
              FROM orders 
              WHERE owner_id = ? AND customer_id = ? 
              ORDER BY order_date ASC";

$stmt = $conn->prepare($sqlOrders);
$stmt->bind_param("ii", $owner_id, $customer_id);
$stmt->execute();
$result = $stmt->get_result();

$orders = [];
$monthlyTotals = array_fill(1, 12, 0); // Initialize 12 months

while ($row = $result->fetch_assoc()) {
    $orders[] = [
        "id" => intval($row['id']),
        "product_name" => $row['product_name'],
        "quantity" => intval($row['quantity']),
        "total_price" => floatval($row['total_price']),
        "order_date" => $row['order_date']
    ];

    // Add to monthly totals
    $month = intval(date("n", strtotime($row['order_date'])));
    $monthlyTotals[$month] += floatval($row['total_price']);
}

$stmt->close();
$conn->close();

echo json_encode([
    "status" => "success",
    "orders" => $orders,
    "monthly_totals" => array_values($monthlyTotals)
]);
