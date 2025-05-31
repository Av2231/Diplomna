<?php
if (!empty($_POST['location_id'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $location_id = $_POST['location_id'];
    $result = array();
    if ($conn) {
        $sql = "select * from table_locations where location_id = '" . $location_id . "'";
        $res = mysqli_query($conn, $sql);
        if (mysqli_num_rows($res) != 0) {
            $row = mysqli_fetch_assoc($res);
            $result = array("status" => "success", "message" => "Location is already existing!", "base_id" => $row['id']);
        } else {
            $result = array("status" => "success", "message" => "Location is not existing!", "base_id" => "0");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed !");
    }
} else {
    $result = array("status" => "failed", "message" => "Location id was not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>