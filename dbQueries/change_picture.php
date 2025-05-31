<?php
if(!empty($_POST['user_id']) and !empty( $_POST['user_picture'])) {
    $conn = mysqli_connect("localhost","root","","mobileapp");
    $user_id = $_POST['user_id'];
    $user_picture = $_POST['user_picture'];
    $result = array();
    if($conn){
        $sql = "select * from table_users where id = '".$user_id."'";
        $res = mysqli_query($conn,$sql);
        if(mysqli_num_rows($res) != 0){
            $sql = "UPDATE table_users SET user_picture = '".$user_picture."' where id = '".$user_id."'";
            if(mysqli_query($conn,$sql)){
                $result = array("status"=>"success","message"=>"Picture saved!");
            }else{
                $result = array("status"=>"failed","message"=>"Something went wrong!");
            }
        }else{
            $result = array("status"=>"failed","message"=>"User is not existing!");
        }
    }
    else{
        $result = array("status"=>"failed","message"=>"Database connection failed!");
    }
}else{
    $result = array("status"=>"failed","message"=>"All fields are required!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>