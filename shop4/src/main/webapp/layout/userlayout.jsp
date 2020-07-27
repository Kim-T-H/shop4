<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="decorator" 
           uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ include file="/WEB-INF/view/jspHeader.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<title><decorator:title /></title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<style>
body,h1 {font-family: "Raleway", Arial, sans-serif}
h1 {letter-spacing: 6px}
.w3-row-padding img {margin-bottom: 12px}
</style>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script type="text/javascript" 
src="https://www.chartjs.org/dist/2.9.3/Chart.min.js"></script>
<style>
*  { margin: 0px; padding: 0px; }
html,body,h1,h2,h3,h4,h5 {font-family: "Raleway", sans-serif}

</style>
<decorator:head />
<body>

<!-- !PAGE CONTENT! -->


<!-- Header -->
<header class="w3-panel w3-center w3-opacity" style="padding:128px 16px">
  <h1 class="w3-xlarge">PHOTOGRAPHER</h1>
  <h1>John Doe</h1>
  
  <div class="w3-padding-32">
    <div class="w3-bar w3-border">
      <a href="${path }/user/main.shop" class="w3-bar-item w3-button">회원관리</a>
      <a href="${path }/item/list.shop" class="w3-bar-item w3-button w3-light-grey">상품관리</a>
      <a href="${path }/board/list.shop" class="w3-bar-item w3-button">게시판</a>
      <a href="${path }/chat/chat.shop" class="w3-bar-item w3-button w3-hide-small">채팅</a>
    </div>
  </div>
 	<div class="w3-row-padding w3-margin-bottom">
    <div class="w3-half">
      <div class="w3-container w3-padding-16">
        <div class="w3-left"><i class="fa fa-comment w3-xxxlarge"></i></div>
       <div id="container" style="width: 75%;">
		<canvas id="canvas1"></canvas>
	   </div>
      </div>
    </div>
    <div class="w3-half">
      <div class="w3-container  w3-padding-16">
        <div class="w3-left"><i class="fa fa-eye w3-xxxlarge"></i></div>
       <div id="barcontainer" style="width: 75%;">
		<canvas id="canvas2"></canvas>
	   </div>
    </div>
  </div>
</div>
  <div align="center">
  <decorator:body />
  </div>
  <br>
 	<div class="w3-container">
	<%--ajax을 통해 얻은 환율 정보 내용 출력 --%>
	<div id="exchange"></div>    
  </div>
  <br>
  	<div class="w3-container">
	<%--ajax을 통해 얻은 환율 정보 내용 출력 --%>
	<div id="exchange2"></div>    
  </div>
</header>


<!-- Footer -->
<footer class="w3-container w3-padding-64 w3-light-grey w3-center w3-large"> 
  <i class="fa fa-facebook-official w3-hover-opacity"></i>
  <i class="fa fa-instagram w3-hover-opacity"></i>
  <i class="fa fa-snapchat w3-hover-opacity"></i>
  <i class="fa fa-pinterest-p w3-hover-opacity"></i>
  <i class="fa fa-twitter w3-hover-opacity"></i>
  <i class="fa fa-linkedin w3-hover-opacity"></i>
  <p>Powered by <a href="https://www.w3schools.com/w3css/default.asp" target="_blank" class="w3-hover-text-green">w3.css</a></p>
</footer>

<script type="text/javascript">
var randomColorFactor = function() {
	return Math.round(Math.random()*255);
}
var randomColor = function(opacity) {	//opacity : 투명도
	return "rgba(" + randomColorFactor() + ","
			+ randomColorFactor() + ","
			+ randomColorFactor() + ","
			+ (opacity || '.3') + ")";
};



$(function() {
	piegraph();
	bargraph();
	exchangeRate(); //환율 정보를 ajax 를 통해 크롤링하기
	exchangeRate2();
})

function exchangeRate(){
		$.ajax("${path}/ajax/exchange1.shop", {
			success : function(data){
				$("#exchange").html(data);
				console.log("체크");
			},
			error:function(e){
				alert("환율 조회시 서버 오류:"+e.status);
				}
		})
	}
	
function exchangeRate2(){
	$.ajax("${path}/ajax/exchange2.shop",{
		success : function(data){
			$("#exchange2").html(data);
		},
		error:function(e){ 
			alert("조회시 서버 오류:"+e.status);
		}
	})
}

function piegraph() {
		console.log("ajax 시작")
		$.ajax("${path}/ajax/graph1.shop", {
			success : function(data) {	//data :json 형태의 문자열로 수신
				pieGraphPrint(data);
				
			},
			error : function(e) {
				alert("서버 오류 : "+ e.status);
			}
		})
	}
	
function bargraph() {
	console.log("ajax 시작")
	$.ajax("${path}/ajax/graph2.shop", {
		success : function(data) {
			barGraphPrint(data);
			
		},
		error : function(e) {
			alert("서버 오류 : "+ e.status);
		}
	})
}
function pieGraphPrint(data) {
	console.log(data)
	var rows = JSON.parse(data)
	var names = []
	var datas = []
	var colors = []
	$.each(rows, function(index, item) {
		names[index] = item.name;
		datas[index] = item.cnt;
		colors[index] = randomColor(1);
	})
	
	var config = {
		type : 'pie',
		data : {
			datasets : [{
				data : datas,
				backgroundColor : colors
			}],
			labels : names
		},
		options : {
			responsive : true,
			legend : {position : 'top'},
			title : {
				display : true,
				text : '글쓴이별 게시판 등록 건수',
				position : 'bottom'
			}
		}
	}
	var ctx = document.getElementById("canvas1").getContext("2d");
	new Chart(ctx, config);
}


function barGraphPrint(data) {
	console.log(data)
	var rows = JSON.parse(data)
	var regdates = []
	var datas = []
	var colors = []
	$.each(rows, function(index, item) {
		regdates[index] = item.regdate;
		datas[index] = item.cnt;
		colors[index] = randomColor(1);
	})
	
	
	var chartData= {
		labels: regdates,
		datasets:[{
			type:'line',
			borderWidth:2,
			borderColor:colors,
			label: '건수',
			fill:false,
			data:datas,
		},{
			type:'bar',
			label:'건수',
			backgroundColor:colors,
			data:datas
			
		}]
	}
	
	var config = {
		type : 'bar',
		data : chartData,
		options: {
			responsive: true,
			title: {display:true,
				text:'최근 7일 동안  게시판 등록 건수',
				position:'bottom'
			},
			legend :{display:false},
			scales:{
				xAxes:[{display:true, stacked:true}],
				yAxes:[{display:true, stacked:true}]
			}
		}
	}
			
			
	var ctx = document.getElementById("canvas2").getContext("2d");
	new Chart(ctx, config);
	
}
</script>

</body>
</html>
