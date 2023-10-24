<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>login</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
</head>
<body>
    <!-- <div style="width: 100%; margin: 0%; text-align: center;"> -->
    <div class="w-100 text-center">
        <div class="mt-5">
<%--            <img src="resources/static/img/yonginLogo.png">--%>
            <img src="/img/yonginLogo.png">
        </div>
        <div>
            <h2 class="mt-3">용인시 청소차 관제 시스템</h2>
            <input type="text" class="mt-5" placeholder="ID를 입력하세요">
            <BR><input type="text" class="mt-2" placeholder="PW를 입력하세요">
        </div>
        <div class="w-25 mx-auto mt-5 d-flex justify-content-center">
            <button type="submit" class="btn btn-danger border w-25 mx-2">로그인</button>
            <button type="submit" class="btn btn-primary border w-25 mx-2">회원가입</button>
        </div>
    </div>
        
</body>
</html>