<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *"); // allow requests from any domain
header("Access-Control-Allow-Methods: GET");

// Include your database connection
require 'db.php'; // make sure db.php sets up $conn (MySQLi)

try {
    // Prepare statement to get all owners
    $stmt = $conn->prepare("SELECT id, name, role FROM users WHERE role = ?");
$role = 'owner';
$stmt->bind_param("s", $role);
$stmt->execute();
$result = $stmt->get_result();

$owners = [];
while ($row = $result->fetch_assoc()) {
    $owners[] = $row;
}

echo json_encode(["status" => "success", "owners" => $owners]);


} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "Something went wrong: " . $e->getMessage()
    ]);
}
