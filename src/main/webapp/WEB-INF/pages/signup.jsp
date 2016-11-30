<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Group 4: Swarna Viswanathan,Sneha Ilangovan</title>
<style>
.button {
    background-color: #008CBA; 
    border:none;
    color: white;
    padding: 5px 17px;
    text-align: center;
    font-size: 14px;
    cursor: pointer;
}
</style>
</head>
<body>
 <div style="margin-left:500px">
	<form method="post" action="signup">
	   <table>
	   		<tr>
       			<td colspan="2">
       				<label style="font-size:20px"><b>Create a New User</b></label><br><br>
       			</td>
        	<tr>
		    	<td><label>Name: </label></td>
		    	<td><input type="text" name="name" required/></td><br>
			</tr>
		 	<tr>
		    	<td><label>Email Id: </label></td>
		    	<td><input type="text" name="emailid" required/></td><br>
			</tr>
		 	<tr>
		    	<td><label>Password: </label></td>
		    	<td><input type="text" name="password" required/></td><br>
			</tr>
			<tr>
		    	<td><label>SJSU ID: </label></td>
		    	<td><input type="text" name="sjsuid" required/></td><br>
			</tr>
			<tr>
				<br>
				<td colspan="2">
					<input class="button" type="submit" name="signup" value="Sign Up"/>
				</td>
			</tr>
		</table>
	</form>
 </div>
</body>
</html>