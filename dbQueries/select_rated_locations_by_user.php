<?php
if (!empty($_POST['user_id'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $user_id = $_POST['user_id'];
    $result = array();
    if ($conn) {
        $sql = "select * from table_ratings where user_id = '" . $user_id . "'";
        $res = mysqli_query($conn, $sql);
        if (mysqli_num_rows($res) != 0) {
            $sql = "SELECT AVG(rated) AS average_rating FROM table_ratings WHERE user_id = '" . $user_id . "'";
            $res_avg = mysqli_query($conn, $sql);
            $row = mysqli_fetch_assoc($res_avg);
            $average_rating = $row['average_rating'];
            $rowCount = $res->num_rows;
            $result = array("status" => "success", "message" => "Returned average rate and user rating score!", "average_rating" => $average_rating, "rate_score" => $rowCount);
        } else {
            $result = array("status" => "success", "message" => "User never rated!", "average_rating" => "0", "rate_score" => "0");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed !");
    }
} else {
    $result = array("status" => "failed", "message" => "Location id was not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>