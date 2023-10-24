<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<link>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>기간별 통계</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <title>Bootstrap datepicket demo</title>
  <script src="https://code.jquery.com/jquery-3.2.1.js"></script>
</head>
<body>

<div class="">

    <div class="statHome">
        <a href="/map" style="text-decoration-line: none; color: black;"><h3 class="d-flex justify-content-center" style="line-height: 100px;"><img src="/img/yonginLogo.svg" alt="용인시 로고" style="width: 100px;">청소차 관제 시스템</h3></a>
    </div>

  <div class="col-9 mx-auto mt-3">
        <div class="row gy-3 mt-4">
            <div class="col-12 col-xl-6">
                <div class="card h-100">
                    <div class="container d-flex justify-content-center">
        
                        <select class="form-select fs-6 w-25 mx-3 mt-5" id="searchType" name="searchType" title="검색 유형 선택" aria-label="Example select with button addon">
	                        	<option class = "carSelectReq">차량을 선택하세요</option>
	                        <c:forEach items="${localdb}" var="localDto">
	                            <option value="${localDto.carNum}" class="carSelect">${localDto.carNum}</option>
	                        </c:forEach>
                        </select>

                        <select class="YearDropBox form-select fs-6 w-25 mt-5 ms-2" title="기간 선택" aria-label="Example select with button addon">
                        	<!-- <option>년도를 선택하세요</option> -->
                            <option class="YearDropBox"></option>
                        </select>
                
                        <select class="MonthDropBox form-select fs-6 w-25 mt-5 ms-2" title="기간 선택" aria-label="Example select with button addon">

                            <option class="MonthDropBox"></option>

                        </select>
                
                
                        <button class="checkButton border ms-3 lh-lg mt-5" style="width: 10%;">조회</button>
                    </div>
                </div>
            </div>
            <div class="col-12 col-xl-6">
                <div class="card h-100">
                <div class="card-header">
                    운행거리(Km)
                </div>
                <div class="card-body" id="cleanDistanceDiv">
                    <!--차트가 그려질 부분-->
                    <canvas id="cleanDistanceCanvas"></canvas>
                </div>
                </div>
            </div>
        </div>       
        <div class="row gy-3 mt-2">
            <div class="col-12 col-xl-6">
                <div class="card h-100">
                    <div class="card-header">
                        청소 비율
                    </div>
                    <div class="card-body w-50 mx-auto" id="cleanRatioDiv">
                        <!--차트가 그려질 부분-->
                        <canvas id="cleanRatioCanvas"></canvas>
                    </div>
                    
                </div>
            </div>
            <div class="col-12 col-xl-6">
                <div class="card h-100">
                <div class="card-header">
                    운행시간(분)
                </div>
	                <div class="card-body" id="cleanTimeDiv">
	                    <!--차트가 그려질 부분-->
	                    <canvas id="cleanTimeCanvas"></canvas>
	                </div>    
                </div>
            </div>
        </div>
    </div>
</div>
<script src="/js/stat.js"></script>
</body>
</html>