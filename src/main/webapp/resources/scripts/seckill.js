/**
 * Created by williamjing on 2017/3/1.
 */
//存放主要交互逻辑代码
//JavaScript模块化
var seckill = {
    //封装ajax请求URL
    URL: {},
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(parseInt(phone))) {
            return true;
        } else {
            return false;
        }
    },
    //秒杀详情页逻辑
    detail: {
        init: function (params) {
            //手机验证和登录
            //从cookies中读取手机号
            var killPhone = $.cookie("killPhone");
            var startTime = params.startTime;
            var endTime = params.endTime;
            var seckillId = params.seckillId;
            if (!seckill.validatePhone(killPhone)) {
                //验证手机号
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var killPhoneKey = $('#killPhoneKey').val();
                    if (seckill.validatePhone(killPhoneKey)) {
                        //写入cookie
                        //7天  /seckill路径下有效
                        $.cookie("killPhone", killPhoneKey, {expires: 7, path: '/seckill'});
                        window.location.reload();
                    } else {
                        //先隐藏 再显示效果比较好
                        $('#killMessage').hide().html('<label class="label label-danger">手机号错误</label>').show(300);
                    }
                });
            }
        }
    }
};