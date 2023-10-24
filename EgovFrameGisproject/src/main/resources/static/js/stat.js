let button = document.querySelector('.checkButton');
let cleanRatio;
let timeInMinutes;
let totalDistance = [];
let carSelect = document.querySelector('.form-select');
let noCleanDistance = [];


// 년도와 월 드롭다운 엘리먼트를 가져옵니다.
const yearDropdown = document.querySelector('.YearDropBox');
const monthDropdown = document.querySelector('.MonthDropBox');




let carNum;
const selectedYear = yearDropdown.value; // 선택한 연도 값
const selectedMonth = monthDropdown.value.toString().padStart(2, '0');; // 선택한 월 값			을 한자릿수라면 앞에 0을 추가해서 두자릿수로 변경 


let selectedCarNum = carSelect.value;

let carSelectReq = document.querySelector('.carSelectReq');


	carSelectReq.disabled = true; // 첫 번째 옵션을 비활성화합니다.
	carSelectReq.selected = true; // 기본 선택값으로 설정합니다.
	
// 현재 날짜 정보를 얻기 위한 함수
function getCurrentDate() {
	const today = new Date();
	return {
		year: today.getFullYear(),
		month: today.getMonth() + 1, // getMonth()는 0부터 시작하므로 1을 더합니다.
	};
}

// 드롭다운에 옵션을 추가하는 함수
function addDropdownOptions() {
	yearDropdown.innerHTML = ""; // 년도 드롭다운을 초기화합니다.
	monthDropdown.innerHTML = ""; // 월 드롭다운을 초기화합니다.
	const currentDate = getCurrentDate();

	// 현재 날짜와 다음 달을 계산합니다.
	let currentMonth = currentDate.month;
	let currentYear = currentDate.year;

	// 1달을 더합니다.
	currentMonth += 1;



	// 년도 드롭다운에 옵션을 추가합니다.
	for (let i = 0; i < 5; i++) {
		const option = document.createElement("option");
		option.text = currentYear;
		option.value = currentYear;
		yearDropdown.appendChild(option);
		currentYear -= 1;
	}
	
	
	
	const defaultYearDropDown = document.createElement("option");
	defaultYearDropDown.text = "년도를 선택하세요"; // 첫 번째 텍스트를 설정합니다.
	defaultYearDropDown.disabled = true; // 첫 번째 옵션을 비활성화합니다.
	defaultYearDropDown.selected = true; // 기본 선택값으로 설정합니다.
	yearDropdown.insertBefore(defaultYearDropDown, yearDropdown.firstChild);
	
	


	const monthNames = ['월을 선택하세요','1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
	// 월 드롭다운에 옵션을 추가합니다.
	for (let i = 0; i < 13; i++) {
		const option = document.createElement("option");
		option.text = monthNames[i];
		option.value = i; // 실제 값을 숫자로 설정하려면 +1을 해야 합니다.
		monthDropdown.appendChild(option);


	}
	

/*	yearDropdown.value = currentDate.year;
	monthDropdown.value = currentDate.month;*/

}


// 드롭다운 값이 변경될 때 호출할 함수
function handleDropdownSelected() {
	const selectedYear = yearDropdown.value; // 선택한 연도 값
	const selectedMonth = monthDropdown.value.toString().padStart(2, '0');; // 선택한 월 값			을 한자릿수라면 앞에 0을 추가해서 두자릿수로 변경 
	let selectedCarNum = carSelect.value;

	console.log("selectedCarNum = ", selectedCarNum);



	// 이제 선택한 연도와 월을 사용하여 원하는 작업을 수행할 수 있습니다.
	/*console.log(`선택한 연도: ${selectedYear}`);
	console.log(`선택한 월: ${selectedMonth}`);*/


	//////////// 조회버튼 클릭 이벤트
	button.onclick = function() {


		console.log(`선택한 연도: ${selectedYear}`);
		console.log(`선택한 월: ${selectedMonth}`);


		let YearMonthDate = { selectedYear, selectedMonth, selectedCarNum };
		console.log("yearmonthdate = ", YearMonthDate);



		$.ajax({
			type: 'post',
			url: '/stat/data',
			dataType: 'json',
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(YearMonthDate),
			success: function(result) {
				/*console.log("ajax에서의 콘솔" + result);*/
				let cleanRatio = result.cleanRatio
				let cleanTime = result.cleanTime
				
				

				cleanTime = [];
				
				// cleanTime 배열의 각 요소의 driveTime 값 사용
				result.cleanTime.forEach((item, index) => {
					// 삼항 연산자를 사용하여 null인 경우 0을 넣어줌
					const driveTimeValue = item && item.driveTime ? item.driveTime : "00:00:00";
					cleanTime.push(driveTimeValue); // driveTime 값을 cleanTime 배열에 추가
				});


				let cleanDistance = result.cleanDistance
//				console.log('js에서의 클린타임 값 = ', result);


				//////////////////	청소 비율 차트
				pieChart(cleanRatio);





				totalDistance = [];
				
				// cleanTime 배열의 각 요소의 driveTime 값 사용
				result.totalDistance.forEach((item, index) => {
					// 삼항 연산자를 사용하여 null인 경우 0을 넣어줌
					const totalDistanceValue = item && item.totalDistance ? item.totalDistance : 0;
					/*console.log(`Index: ${index + 1}, Drive Time: ${driveTimeValue}`);*/
					totalDistance.push(totalDistanceValue/1000);
				});

				noCleanDistance = [];
				

				result.noCleanDistance.forEach((item, index) => {

					const noCleanDistanceValue = item && item.noCleanDistance ? item.noCleanDistance : 0;

					noCleanDistance.push(noCleanDistanceValue/1000);
				});

				


console.log("총운행거리 = " + totalDistance);
								console.log("노클린distance = ", noCleanDistance);
								
								
				////////////	운행거리 차트
				distanceChart(totalDistance, noCleanDistance);



				// 주어진 시간 문자열을 분 단위로 변환하는 함수
				function convertTimeToMinutes(timeString) {
					const timeParts = timeString.split(':').map(part => parseInt(part, 10));
					const hours = timeParts[0];
					const minutes = timeParts[1];
					const seconds = timeParts[2];
					const totalMinutes = hours * 60 + minutes + seconds / 60;
					return totalMinutes;
				}

				timeInMinutes = cleanTime.map(timeString => convertTimeToMinutes(timeString));

				console.log("123123 = ",timeInMinutes); // 분 단위로 변환된 값 출력



				////////////////	운행 시간 차트
				cleanTimeChart(timeInMinutes);
				
				

				///////////////


			},
			error: function(error) {
				console.log(error);
			},



		});





	}
}

// 드롭다운 값이 변경될 때 이벤트 리스너 등록
yearDropdown.addEventListener("change", handleDropdownSelected);
monthDropdown.addEventListener("change", handleDropdownSelected);
carSelect.addEventListener("change", handleDropdownSelected);

// 초기 옵션을 설정합니다.
addDropdownOptions();








function pieChart(cleanRatio) {
	$("#cleanRatioCanvas").remove();
	$("#cleanRatioDiv").append('<canvas id="cleanRatioCanvas"></canvas>');


	var context = document.getElementById('cleanRatioCanvas').getContext('2d');
	pieChartDraw = new Chart(context, {
		type: 'pie', // 차트의 형태
		data: { // 차트에 들어갈 데이터
			labels: [
				//x 축
				'청소 미완료', '청소 완료'
			],
			datasets: [
				{ //데이터
					label: '청소 비율', //차트 제목
					fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
					data: [
						100 - cleanRatio, cleanRatio //x축 label에 대응되는 데이터 값
					],
					backgroundColor: [
						//색상
						'rgba(255, 99, 132, 0.2)',
						'rgba(54, 162, 235, 0.2)',
					],

					borderWidth: 1 //경계선 굵기
				}
			]
		},
		options: {
			plugins: {
				legend: {
					display: false, // 범례 표시 여부
					labels: {
						boxWidth: 20, // 범례 아이콘 너비
						fontSize: 14 // 범례 텍스트 크기
					}
				}

			}

		}

	});
}



function distanceChart() {
	$("#cleanDistanceCanvas").remove();
	$("#cleanDistanceDiv").append('<canvas id="cleanDistanceCanvas"></canvas>');


	var context = document.getElementById('cleanDistanceCanvas').getContext('2d');
	distanceChartDraw = new Chart(context, {
	type: 'line', // 차트의 형태
	data: { // 차트에 들어갈 데이터
		labels: [
			//x 축
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'
		],
		datasets: [
			{ //데이터
				label: '운행 거리', //차트 제목
				fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
				data: 
					totalDistance
				,
				backgroundColor: 
					//색상
					'rgba(255, 99, 132, 0.2)'

				,
				borderColor: 
					//경계선 색상
					'rgba(255, 99, 132, 1)'

				,
				borderWidth: 1 //경계선 굵기
			},
			{ //데이터
				label: '유효 운행 거리', //차트 제목
				fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
				data: 
					noCleanDistance
				,
				backgroundColor: 
					//색상
					'rgba(1, 1, 1, 0.2)'

				,
				borderColor: 
					//경계선 색상
					'rgba(1, 1, 1, 1)'

				,
				borderWidth: 1 //경계선 굵기
			}
		]
	},
	options: {

		scales: {
			yAxes: [
				{
					ticks: {
						beginAtZero: true
					}
				}
			]
		}
	}
});
}






function cleanTimeChart(cleanTime) {
	$("#cleanTimeCanvas").remove();
	$("#cleanTimeDiv").append('<canvas id="cleanTimeCanvas"></canvas>');


	var context = document.getElementById('cleanTimeCanvas').getContext('2d');
	cleanTimeChartDraw = new Chart(context, {
		type: 'line', // 차트의 형태
		data: { // 차트에 들어갈 데이터
			labels: [
				//x 축
				'1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'
			],
			datasets: [
				{ //데이터
					label: '청소 시간', //차트 제목
					fill: false, // line 형태일 때, 선 안쪽을 채우는지 안채우는지
					data:
						timeInMinutes
					,
					backgroundColor: 
						//색상
						'rgba(255, 99, 132, 0.2)'

					,
					borderColor: 
						//경계선 색상
						'rgba(255, 99, 132, 1)'

					,
					borderWidth: 1 //경계선 굵기
				}
			]
		},
		options: {
			scales: {
				yAxes: [
					{
						ticks: {
							beginAtZero: true
						}
					}
				]
			}
		}

	});
}

