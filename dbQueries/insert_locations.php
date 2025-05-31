<?php
if (!empty($_POST['location_id'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $location_id = $_POST['location_id'];
    $result = array();
    if ($conn) {
        $sql = "select * from table_locations where location_id = '" . $location_id . "'";
        $res = mysqli_query($conn, $sql);
        if (mysqli_num_rows($res) == 0) {
            $sql = "INSERT INTO table_locations (location_id) VALUES ('" . $location_id . "')";
            if (mysqli_query($conn, $sql)) {
                $result = array("status" => "success", "message" => "Location added successfully!","generated_location_id"=>$row['id']);
            } else {
                $result = array("status" => "failed", "message" => "Couldnt add new location!");
            }
        } else {
            $result = array("status" => "existing", "message" => "This location id is already being saved!");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed!");
    }
} else {
    $result = array("status" => "failed", "message" => "Location id was not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>