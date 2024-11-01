layui.use(['form'], function() {
    var form = layui.form;

    // checkLogin();
    //提交
    form.on('submit(mpUser_login)', function(obj) {
        // obj.field.verkey = codeKey;

        obj.field.password = $.md5(obj.field.password);
        //console.log("登录密码："+obj.field.password);
      //  obj.field.valiCode = $.md5(obj.field.valiCode);
        layer.load(1);

        $.post("/mp/login",obj.field, function(result) {
            if (result.resultCode == 1) {
                layer.msg("登录成功",{icon: 1});
                result.data["password"] = obj.field.password;
                //console.log("Login data:"+JSON.stringify(result.data));
                localStorage.setItem('loginData',JSON.stringify(result.data));

                setTimeout(function() {
                    location.replace("/mp/index.html");
                }, 1000);

            } else {
                layer.closeAll('loading');

                layer.msg(result.resultMsg,{icon: 2});
            }

        }, "json");

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。

    });

    //提交
    form.on('verificationCode', function(obj) {
        // obj.field.verkey = codeKey;
        console.log("登录密码："+obj.field.password);
      //   obj.field.password = $.md5(obj.field.password);
      //   //console.log("登录密码："+obj.field.password);
      // //  obj.field.valiCode = $.md5(obj.field.valiCode);
      //   layer.load(1);
      //
      //   $.post("/mp/login",obj.field, function(result) {
      //       if (result.resultCode == 1) {
      //           layer.msg("登录成功",{icon: 1});
      //           result.data["password"] = obj.field.password;
      //           //console.log("Login data:"+JSON.stringify(result.data));
      //           localStorage.setItem('loginData',JSON.stringify(result.data));
      //
      //           setTimeout(function() {
      //               location.replace("/mp/index.html");
      //           }, 1000);
      //
      //       } else {
      //           layer.closeAll('loading');
      //
      //           layer.msg(result.resultMsg,{icon: 2});
      //       }
      //
      //   }, "json");
      //
      //   return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。

    });


    //表单输入效果
    $(".loginBody .input-item").click(function(e){
        e.stopPropagation();
        $(this).addClass("layui-input-focus").find(".layui-input").focus();
    })
    $(".loginBody .layui-form-item .layui-input").focus(function(){
        $(this).parent().addClass("layui-input-focus");
    })
    $(".loginBody .layui-form-item .layui-input").blur(function(){
        $(this).parent().removeClass("layui-input-focus");
        if($(this).val() != ''){
            $(this).parent().addClass("layui-input-active");
        }else{
            $(this).parent().removeClass("layui-input-active");
        }
    })


});

function sendSms(){
    $.ajax({
        url:"http://api.yiyiim.com/sendSms",
        dataType:"json",
        async:false,
        success:function(data){
            console.log(data)
            if(data.success == 0){
                localStorage.setItem("random",data.data)
                layer.alert("短信发送成功！");
                return ;
            }
            layer.alert("短信发送失败！");
        }
    })

}
