<?php
require 'db.php';

header('Content-Type: application/json');

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
    exit;
}

$email = $data['email'];
$password = $data['password'];

$sql = "SELECT * FROM users WHERE email=?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    // if you stored plain text, use === instead of password_verify
    if (password_verify($password, $row['password'])) {
        echo json_encode([
            "status" => "success",
            "user" => [
                "id" => $row['id'],
                "name" => $row['name'],
                "email" => $row['email'],
                "role" => $row['role'],
                "contact" => $row['contact']
            ]
        ]);
    } else {
        echo json_encode(["status" => "error", "message" => "Invalid password"]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "User not found"]);
}
?>
