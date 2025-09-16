<?php
header('Content-Type: application/json');
include 'db.php'; // your DB connection

if (!isset($_GET['owner_id'])) {
    echo json_encode(['status'=>'error', 'message'=>'owner_id required']);
    exit;
}

$owner_id = intval($_GET['owner_id']);

// Fetch customers who have placed orders with this owner
$query = $conn->prepare("
    SELECT DISTINCT u.id, u.name 
    FROM users u
    JOIN orders o ON u.id = o.customer_id
    WHERE o.owner_id = ? AND u.role = 'customer'
");
$query->bind_param("i", $owner_id);
$query->execute();
$result = $query->get_result();

$customers = [];
while($row = $result->fetch_assoc()) {
    $customers[] = ['id' => $row['id'], 'name' => $row['name']];
}

echo json_encode(['status'=>'success', 'customers'=>$customers]);
?>
