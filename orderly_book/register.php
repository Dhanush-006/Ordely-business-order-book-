<?php
require 'db.php';

header('Content-Type: application/json');

$data = json_decode(file_get_contents("php://input"), true);
if (!$data) {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
    exit;
}

$name = $data['name'];
$email = $data['email'];
$password = $data['password'];
$role = $data['role'];
$contact = $data['contact'];

// 🔐 Hash the password before storing
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

$sql = "INSERT INTO users (name, email, password, role, contact) VALUES (?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sssss", $name, $email, $hashedPassword, $role, $contact);

try {
    if ($stmt->execute()) {
        echo json_encode([
            "status" => "success",
            "message" => "User registered successfully"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Registration failed"
        ]);
    }
} catch (mysqli_sql_exception $e) {
    echo json_encode([
        "status" => "error",
        "message" => "Email already exists"
    ]);
}
?>
