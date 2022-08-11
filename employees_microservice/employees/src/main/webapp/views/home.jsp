<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>HomePage</title>
</head>
<body>
    Welcome to Employees World.<br>

    <br>This microservice provides a RESTful API with all requests in JSON form:<br>
    <br>To retrieve the list of employees send a GET request to:
    <br>/employees<br>
    <br>To retrieve a specific employee send a GET request to:
    <br>/employee/'ID'<br>
    <br>To add an employee send a POST request to:
    <br>/employee
    <br>with a form of:
    <br>{
    <br>"name":'name_value'
    <br>"country":'country_value'
    <br>"city":'city_value'
    <br>"salary":'salary_in_NIS'
    <br>"email":'email_address'
    <br>}<br>
    <br>To delete a certain employee send DELETE request to:
    <br>/employee/remove/'ID'

</body>
</html>
