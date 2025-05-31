<?php
if (!empty($_POST['location_id']) and !empty($_POST['user_id']) and !empty($_POST['rated'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $location_id = $_POST['location_id'];
    $user_id = $_POST['user_id'];
    $rated = $_POST['rated'];
    $result = array();
    if ($conn) {
        $sql = "INSERT INTO table_ratings (user_id,location_id,rated) VALUES ('" . $user_id . "','" . $location_id . "','" . $rated . "')";
        if (mysqli_query($conn, $sql)) {
            $result = array("status" => "success", "message" => "Location rated successfully!");
        } else {
            $result = array("status" => "failed", "message" => "Couldnt rate location!");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed!");
    }
} else {
    $result = array("status" => "failed", "message" => "Some of the fields are empty!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>