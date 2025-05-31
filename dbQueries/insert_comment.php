<?php
if (!empty($_POST['location_id']) and !empty($_POST['user_id']) and !empty($_POST['comment'])) {
    $conn = mysqli_connect("localhost", "root", "", "mobileapp");
    $location_id = $_POST['location_id'];
    $user_id = $_POST['user_id'];
    $comment = $_POST['comment'];
    $result = array();
    if ($conn) {
        $sql = "INSERT INTO table_comments (user_id,location_id,comment) VALUES ('" . $user_id . "','" . $location_id . "','" . $comment . "')";
        if(mysqli_query($conn,$sql)){
            $result = array("status"=>"success","message"=>"Commented successfully!");
        }else{
            $result = array("status"=>"failed","message"=>"Comment failed! Bad query or parameters!");
        }
    } else {
        $result = array("status" => "failed", "message" => "Database connection failed!");
    }
} else {
    $result = array("status" => "failed", "message" => "Parameters were not provided!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>