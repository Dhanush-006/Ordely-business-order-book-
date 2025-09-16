<?php
header('Content-Type: application/json');
include 'db.php';

$response = ["status" => "error", "message" => "Missing parameters"];

// Try form-data
$order_id = $_POST['order_id'] ?? null;
$status   = $_POST['status'] ?? null;

// If not found, try JSON body
if (!$order_id || !$status) {
    $input = json_decode(file_get_contents("php://input"), true);
    if ($input) {
        $order_id = $input['order_id'] ?? null;
        $status   = $input['status'] ?? null;
    }
}

if ($order_id && $status) {
    $order_id = intval($order_id);
    if (in_array($status, ["pending", "accepted", "rejected"])) {
        $stmt = $conn->prepare("UPDATE orders SET status=? WHERE id=?");
        $stmt->bind_param("si", $status, $order_id);
        if ($stmt->execute()) {
            $response = ["status" => "success", "message" => "Order updated"];
        } else {
            $response = ["status" => "error", "message" => "Database update failed"];
        }
        $stmt->close();
    } else {
        $response["message"] = "Invalid order_id or status";
    }
}

echo json_encode($response);
$conn->close();
?>
