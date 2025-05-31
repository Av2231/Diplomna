<?php
if (!empty($_POST['user_id'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $user_id = $_POST['user_id'];
    $result = array();
    if ($conn) {
        $sql = "select * from table_comments where user_id = '" . $user_id . "'";
        $res = mysqli_query($conn, $sql);
        if (mysqli_num_rows($res) != 0) {
            $rowCount = $res->num_rows;
            $result = array("status" => "success", "message" => "Returned average rate and user rating score!", "comment_score" => $rowCount);
        } else {
            $result = array("status" => "success", "message" => "User never rated!", "comment_score" => "0");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed !");
    }
} else {
    $result = array("status" => "failed", "message" => "Location id was not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>