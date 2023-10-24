<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <!-- openlayers -->
    <script src="https://cdn.jsdelivr.net/npm/ol@v8.1.0/dist/ol.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/ol@v8.1.0/ol.css">

    <!-- JQuery -->
    <script  src="http://code.jquery.com/jquery-latest.min.js"></script>
    
    <!-- bootstrap 5.3.1 -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

    <!-- bootstrap checkBox api -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap5-toggle@5.0.4/css/bootstrap5-toggle.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap5-toggle@5.0.4/js/bootstrap5-toggle.jquery.min.js"></script>
    

    <title>Document</title>
</head>
<body>
    
<h1>fileInput.jsp</h1>

<div class="mb-3">
  <label for="gpsCsvInput" class="form-label">GPS 데이터</label>
  <input class="form-control" type="file" accept=".csv" id="gpsCsvInput" name="gpsCsvInput">
  <label for="noiseCsvInput" class="form-label">소음 데이터</label>
  <input class="form-control" type="file" accept=".csv" id="noiseCsvInput" name="noiseCsvInput">
  <label for="frequencyCsvInput" class="form-label">진동 데이터</label>
  <input class="form-control" type="file" accept=".csv" id="frequencyCsvInput" name="frequencyCsvInput">
  <input type="submit" onclick="submitCsvBtn()">
</div>

<script type="text/javascript">

/////////////////////////////////////////////////////////////////////////////////////////////
/*
 * (시작) CSV파일 업로드 구현
 * 
 * << jsp에 추가해야 할 것 >>
 * - <input type="file" ... 에 id 채우기
 * - 버튼에 onclick="submitCsvBtn()"
 */
/////////////////////////////////////////////////////////////////////////////////////////////
function submitCsvBtn(){
	const formData = new FormData();
	const gpsCsvInput = document.getElementById('gpsCsvInput');
	const noiseCsvInput = document.getElementById('noiseCsvInput');
	const frequencyCsvInput = document.getElementById('frequencyCsvInput');
	
	// 파일을 모두 첨부하지 않으면 실행되지 않음
	if(!gpsCsvInput.files[0] || !noiseCsvInput.files[0] || !frequencyCsvInput.files[0] ){
		alert('파일을 모두 첨부해주세요');
		return;
	}
	
	formData.append('gpsFile', gpsCsvInput.files[0]);
	formData.append('noiseFile', noiseCsvInput.files[0]);
	formData.append('frequencyFile', frequencyCsvInput.files[0]);

	fetch('/file',{
		method: 'POST',
		body: formData,
	})
	.then((response)=>	{
		if(!response.ok) {
			// 오류 발생시 처리
			alert('오류가 발생했습니다\n파일을 다시 확인해주세요');
		    throw new Error('오류 발생');
		}})
	.then((data) =>	{
		alert('등록이 완료되었습니다.');
	})
}

/////////////////////////////////////////////////////////////////////////////////////////////
/*
 * (끝) CSV파일 업로드 구현
 */
/////////////////////////////////////////////////////////////////////////////////////////////
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>