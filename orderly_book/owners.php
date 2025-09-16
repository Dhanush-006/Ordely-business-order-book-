<?php
header('Content-Type: application/json'); // return JSON
include 'db.php'; // your database connection file

// Check if 'id' parameter is provided
if (isset($_GET['id'])) {
    $id = intval($_GET['id']); // sanitize input

    // Prepare query to fetch owner by id
    $stmt = $conn->prepare("SELECT id, name, email, contact FROM users WHERE id = ? AND role = 'owner'");
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($owner = $result->fetch_assoc()) {
        // Return owner data as JSON
        echo json_encode($owner);
    } else {
        // Owner not found
        http_response_code(404);
        echo json_encode(["error" => "Owner not found"]);
    }

    $stmt->close();
} else {
    // Missing id parameter
    http_response_code(400);
    echo json_encode(["error" => "Missing id parameter"]);
}

$conn->close();
?>
