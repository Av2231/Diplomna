<?php
if (!empty($_POST['location_id'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $location_id = $_POST['location_id'];
    $result = array();
    if ($conn) {
        $sql = "SELECT table_comments.*, table_users.user_first_name, table_users.user_last_name,table_users.user_picture
        FROM table_comments
        INNER JOIN table_users ON table_comments.user_id = table_users.id
        WHERE table_comments.location_id = '".$location_id."'";
        $res = mysqli_query($conn, $sql);
        if (mysqli_num_rows($res) != 0) {
            while ($row = mysqli_fetch_assoc($res)) {
                $result[] = $row;
            }
            $result = array("status" => "success", "message" => "Returned comments", "comments" => $result);
        } else {
            $result = array("status" => "no_comments", "message" => "No comments found for this location");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed!");
    }
} else {
    $result = array("status" => "failed", "message" => "Location ID was not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>