<?php
if(!empty($_POST['email']) and !empty($_POST['password']) and !empty($_POST['dob']) and !empty($_POST['first_name']) and !empty($_POST['last_name'])){
    $conn = mysqli_connect("localhost","root","","mobileapp");
    $email = $_POST['email'];
    $password = password_hash($_POST['password'], PASSWORD_DEFAULT);
    $dob = $_POST['dob'];
    $first_name = $_POST['first_name'];
    $last_name = $_POST['last_name'];
    $isDeleted = $_POST['isDeleted'];
    $isConfirmed = $_POST['isConfirmed'];
    $role = $_POST['role'];
    $user_picture = $_POST['user_picture'];
    $result = array();
    if($conn){
        $sql = "select * from table_users where user_email = '".$email."'";
        $res = mysqli_query($conn,$sql);
        if(mysqli_num_rows($res) == 0){
            $sql = "INSERT INTO table_users (user_email,user_first_name,user_last_name,user_password,user_isdeleted,user_isconfirmed,user_role,user_dob,user_picture) 
            VALUES ('".$email."','".$first_name."','".$last_name."','".$password."','".$isDeleted."','".$isConfirmed."','".$role."','".$dob."','".$user_picture."')";
            if(mysqli_query($conn,$sql)){
                $result = array("status"=>"success","message"=>"Registered successfully!");
            }else{
                $result = array("status"=>"failed","message"=>"Registration failed! Bad query or parameters!");
            }
        }else{
            $result = array("status"=>"failed","message"=>"There is account with this email address!");
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