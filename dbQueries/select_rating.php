<?php
if (!empty($_POST['location_id'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $location_id = $_POST['location_id'];
    $result = array();
    if ($conn) {
        $sqlGet = "SELECT * FROM table_ratings WHERE location_id = '".$location_id."'";
        $res = mysqli_query($conn, $sqlGet);
        if (mysqli_num_rows($res) != 0) {
            $sql = "SELECT AVG(rated) AS average_rating FROM table_ratings WHERE location_id = '".$location_id."'";
            $res_avg = mysqli_query($conn, $sql);
            $row = mysqli_fetch_assoc($res_avg);
            $average_rating = $row['average_rating'];

            $result = array("status" => "success", "message" => "Returned average rate!", "average_rating" => $average_rating);
        } else {
            $result = array("status" => "not rated", "message" => "Location has never been rated!");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed!");
    }
} else {
    $result = array("status" => "failed", "message" => "Location ID was not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>
