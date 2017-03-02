<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <script src="/resources/js/jquery.min.js"></script>
    <script src="/resources/js/bootstrap.min.js"></script>
    <script src="/resources/js/jquery.countdown.min.js"></script>
    <script src="/resources/js/jquery.cookie.js"></script>

    <%@include file="common/head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-dafault text-center">
        <div class="panel-heading">
            <h2>${seckill.name}</h2>
        </div>
    </div>
    <div class="panel-body">
        <h2 class="text-danger">
            <span class="glyphicon glyphicon-time"></span>
            <span class="glyphicon" id="seckill-box"></span>
        </h2>
    </div>
</div>

<!-- 弹出框 -->
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey"
                               placeholder="请输入手机号码" class="form-control">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <span id="killMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone">Submit</span>
                </button>
            </div>
        </div>
    </div>
</div>


</body>


<script src="/resources/scripts/seckill.js"></script>
<script type="text/javascript">
    $(function () {
        seckill.detail.init({
            seckillId:${seckill.seckillId},
            startTime:${seckill.startTime.time},
            endTime:${seckill.endTime.time}
        });
    });
</script>
</html>
