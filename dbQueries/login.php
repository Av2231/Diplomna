<?php
if(!empty($_POST['email']) and !empty($_POST['password'])){
    $conn = mysqli_connect("localhost","root","","mobileapp");
    $email = $_POST['email'];
    $password = $_POST['password'];
    $result = array();
    if($conn){
       $sql = "select * from table_users where user_email = '".$email."'";
       $res = mysqli_query($conn,$sql);
       if(mysqli_num_rows($res) != 0){
        $row = mysqli_fetch_assoc($res);
        if($email == $row['user_email'] and password_verify($password, $row['user_password'])){
            $result = array("status"=>"success","message"=>"Logged successfully !","id"=>$row['id'],"email"=>$row['user_email'],"first_name"=>$row['user_first_name'],"last_name"=>$row['user_last_name'],"dob"=>$row['user_dob'],"role"=>$row['user_role'],"picture"=>$row['user_picture']);
        }else{
            $result = array("status"=>"failed","message"=>"Incorrect password !");
        }
       }else{
        $result = array("status"=>"failed","message"=>"Invalid username !");
       }
    }
    else{
        $result = array("status"=>"failed","message"=>"Database connection failed !");
    }
}else{
    $result = array("status"=>"failed","message"=>"All fields are required !");
}
echo json_encode($result, JSON_PRETTY_PRINT);
?>