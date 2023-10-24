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

    <!-- custom -->
    <link rel="stylesheet" href="css/common.css">
    <link rel="stylesheet" href="css/map.css">
    <script src="js/map.js"></script>
    <script src="js/yonginLayer.js"></script>
    <script src="js/live.js"></script>

    <title>Document</title>
</head>
<body>
    <div class="offcanvas offcanvas-start show" data-bs-scroll="true" data-bs-backdrop="false" tabindex="-1" id="offcanvasScrolling" aria-labelledby="offcanvasScrollingLabel">
        <input type="checkbox" data-toggle="toggle" data-onlabel="실시간 활성" data-offlabel="실시간 비활성" data-onstyle="success" data-offstyle="secondary" id="toggle-live" data-style="slow">
        <button class="btn btn-primary tab-btn" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasScrolling" aria-controls="offcanvasScrolling">
            <i class="bi bi-chevron-compact-right custom-arrow"></i>
        </button>

        <div class="offcanvas-header">
            <h3 class="offcanvas-title" id="offcanvasScrollingLabel">
                <img class="offcanvas-logo" src="/img/yonginLogo.svg">
                청소차 관제 시스템
            </h3>

        </div>
        <div class="offcanvas-body">
            <div class="d-flex text-center">
                <button class="w-25 border border-1 pe-none">지도유형</button>
                <button class="w-25 border border-1 border-start-0 bg-primary text-white map-type" id="base">기본</button>
                <button class="w-25 border border-1 border-start-0 bg-white map-type" id="satellite">위성</button>
                <button class="w-25 border border-1 border-start-0 bg-white map-type" id="hybrid">하이브리드</button>
            </div>
            <div class="d-flex text-center mt-2">
                <button class="w-25 border border-1 pe-none">권역(구)</button>
                <button class="w-25 border border-1 border-start-0 bg-white region-type" id="giheung">기흥구</button>
                <button class="w-25 border border-1 border-start-0 bg-white region-type" id="cheoin">처인구</button>
                <button class="w-25 border border-1 border-start-0 bg-white region-type" id="suji">수지구</button>
            </div>
            <div class="input-group mt-2">
                <label class="input-group-text rounded-0" for="carNumGroup">차량</label>
                <select class="form-select rounded-0" id="carNumGroup">
                  <option selected>차량을 선택하세요.</option>
                    <c:forEach var="list" items="${carNumList}" varStatus="st">
                        <option value=${st.count}>${list}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="text-center mt-3">
                <table class="sec-cal border" id="calendar">
                    <tr>
                        <td><label class="next-prev" onclick="prevCalendar()">&lt;</label></td>
                        <td id="tbCalendarYM" colspan="5">yyyy년 m월</td>
                        <td><label class="next-prev" onclick="nextCalendar()">&gt;</label></td>
                    </tr>
                    <tr>
                        <td class="text-danger">일</td>
                        <td>월</td>
                        <td>화</td>
                        <td>수</td>
                        <td>목</td>
                        <td>금</td>
                        <td class="text-primary">토</td>
                    </tr> 
                </table>
            </div>
            <div class="mt-3">
                <div class="d-flex">
                    <div class="w-25 text-center border bg-light">선택날짜</div>
                    <div class="w-75 text-center border selected-cal" id="clickDate"></div>
                </div>
                <div class="d-flex mt-3">
                    <div class="w-25 text-center border bg-light">운행시간</div>
                    <div class="w-75 text-center border" id="driveTime"></div>
                </div>
                <div class="d-flex border-top-0">
                    <div class="w-25 text-center border border-top-0 bg-light">청소비율</div>
                    <div class="w-75 text-center border border-top-0" id="cleanRatio"></div>
                </div>
                <div class="d-flex border-top-0">
                    <div class="w-25 text-center border border-top-0 bg-light">총 운행거리</div>
                    <div class="w-75 text-center border border-top-0" id="driveDistance"></div>
                </div>
                <div class="d-flex border-top-0">
                    <div class="w-25 text-center border border-top-0 bg-light">유효 운행거리</div>
                    <div class="w-75 text-center border border-top-0" id="cleanDistance"></div>
                </div>
            </div>

            <div class="mt-3 text-center">
                <button class="btn btn-info w-100 text-white rounded-0"><a href="/stat" class="nav-link">기간별 통계</a></button>
            </div>

            <!-- Button trigger modal -->
            <div class="mt-1">
                <button type="button" class="btn btn-primary w-100 rounded-0 add-csv" data-bs-toggle="modal" data-bs-target="#exampleModal">
                    CSV 데이터 추가
                </button>
            </div>
        </div>
    </div>
    <!-- Modal -->
    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">CSV 데이터 추가</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="gpsCsvInput" class="form-label mb-0">GPS 데이터</label>
                        <input class="form-control" type="file" accept=".csv" id="gpsCsvInput" name="gpsCsvInput">
                        <label for="noiseCsvInput" class="form-label mb-0 mt-2">소음 데이터</label>
                        <input class="form-control" type="file" accept=".csv" id="noiseCsvInput" name="noiseCsvInput">
                        <label for="frequencyCsvInput" class="form-label mb-0 mt-2">진동 데이터</label>
                        <input class="form-control" type="file" accept=".csv" id="frequencyCsvInput" name="frequencyCsvInput">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                    <button type="submit" class="btn btn-primary" onclick="submitCsvBtn()">추가</button>
                </div>
            </div>
        </div>
    </div>
    
    <div id="map" class="map map-active">
        <div class="timer border border-1 border-primary rounded-3 bg-body-tertiary px-2 py-2">
            <div class="text-center mb-2">데이터 저장 주기 설정</div>
            <div class="d-flex justify-content-center">
                <div class="btn btn-primary mx-1 set-minute" id="1">1분</div>
                <div class="btn btn-primary mx-1 set-minute" id="2">2분</div>
                <div class="btn btn-primary mx-1 set-minute" id="3">3분</div>
            </div>
        </div>
<%--        <div>
            <button class="download-btn btn btn-success">CSV 다운로드</button>
        </div>--%>

    </div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>