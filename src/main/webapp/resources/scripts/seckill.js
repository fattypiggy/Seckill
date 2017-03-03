/**
 * Created by williamjing on 2017/3/1.
 */
//存放主要交互逻辑代码
//JavaScript模块化
var seckill = {
    //封装ajax请求URL
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        killexecution: function (seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    SeckillExecute: function (seckillId, node) {
        node.hide()
            .html('<button class="btn btn-primary btn-lg " id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.killexecution(seckillId, md5);
                    $('#killBtn').one('click', function () {
                        $(this).addClass('disabled');
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];

                                node.html('<span class="btn btn-success">' + stateInfo + '</span>')
                            } else {
                                console.log(result);
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀  由于本机时间和系统时间的偏差导致
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];

                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log(result);
            }
        })
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');

        if (nowTime < startTime) {
            //秒杀未开启
            var killTime = new Date(startTime + 1000);
            //倒计时开始
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime("秒杀倒计时：%D天 %H时 %M分 %S秒");
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                seckill.SeckillExecute(seckillId, seckillBox);
            })
        } else if (nowTime > endTime) {
            //秒杀已结束
            seckillBox.html("秒杀结束");
        } else {
            seckill.SeckillExecute(seckillId, seckillBox);
        }
    },
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
            //已经登录
            var startTime = params.startTime;
            var endTime = params.endTime;
            var seckillId = params.seckillId;
            $.get(seckill.URL.now(), function (result) {
                if (result && result['success']) {
                    //计时交互
                    var nowTime = result['data'];
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log("result" + result);
                }
            });

        }
    }
}