<?php
if (!empty($_POST['user_id']) and !empty($_POST['location_id'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $user_id = $_POST['user_id'];
    $location_id = $_POST['location_id'];
    $result = array();
    if ($conn) {
        $sql = "select * from table_ratings where user_id = '".$user_id."' and location_id = '".$location_id."'";
        $res = mysqli_query($conn, $sql);
        if (mysqli_num_rows($res) != 0) {
            $result = array("status" => "success", "message" => "User already rated this location!", "rated" => "true");
        } else {
            $result = array("status" => "success", "message" => "User already rated this location!", "rated" => "false");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed!");
    }
} else {
    $result = array("status" => "failed", "message" => "User ID or location ID was not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>
