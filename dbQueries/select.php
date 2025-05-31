<?php
if(!empty($_POST['email'])){
    $conn = mysqli_connect("localhost","root","","mobileapp");
    $email = $_POST['email'];
    $result = array();
    if($conn){
       $sql = "select * from table_users where user_email = '".$email."'";
       $res = mysqli_query($conn,$sql);
       if(mysqli_num_rows($res) != 0){
        $row = mysqli_fetch_assoc($res);
        $result = array("status"=>"success","message"=>"Got user information","email"=>$row['user_email'],"first_name"=>$row['user_first_name'],"last_name"=>$row['user_last_name'],"dob"=>$row['user_dob'],"role"=>$row['user_role']);
       }else{
        $result = array("status"=>"failed","message"=>"Couldnt get user data!");
       }
    }
    else{
        $result = array("status"=>"failed","message"=>"Database connection failed !");
    }
}else{
    $result = array("status"=>"failed","message"=>"Email field was empty!");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>