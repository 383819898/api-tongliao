var page=0;
var sum=0;
var timeBigen='';
var timeEnd='';
layui.use(['form','layer','laydate','table','laytpl'],function(){
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        laydate = layui.laydate,
        laytpl = layui.laytpl,
        table = layui.table;


    // 用户提现列表
    var tableIns = table.render({

        elem: '#raiseCash_table'
        ,url:request("/console/systemRecharge")+"&type="+2

        ,id: 'raiseCash_table'
        ,page: true
        ,curr: 0
        ,limit:Common.limit
        ,limits:Common.limits
        ,toolbar: '#toolbarDemo'
        ,groups: 7
        ,cols: [[ //表头
        	  {type: 'checkbox', fixed: 'left'}
          /*  {field: 'id', title: '提现记录Id',sort: true,width:150}*/
          /*  {field: 'tradeNo', title: '交易单号',sort: true,width:180}*/
        	   ,{field: 'status', title: '交易状态',sort: true, width:120,templet : function (d) {
                   var statusMsg;
                   (d.status == 0 ? statusMsg = "<font color=blue>创建<font>" : (d.status == 1) ? statusMsg = "支付完成" : (d.status == 2) ? statusMsg = "交易完成" :(d.status == -1) ? statusMsg = "交易关闭" : statusMsg = "关闭")
                   return statusMsg;
               }}

           /* ,{field: 'userId', title: '用户Id',sort: true, width:120}*/
            ,{field: 'userName', title: '用户昵称',sort: true, width:120}
            ,{field: 'money', title: '提现金额',sort: true, width:120}
            ,{field: 'operationAmount', title: '应付金额',sort: true, width:120,templet:function(d){
            	return "<font color='blue'><B>"+d.operationAmount+"</B></font>";
            }}
            ,{field: 'serviceCharge', title: '手续费',sort: true, width:120}
            ,{field: 'serviceChargeInstruction', title: '手续费说明',sort: true, width:120}
           /* ,{field: 'serviceCharge', title: '手续费',sort: true, width:120}*/
           /* ,{field: 'operationAmount', title: '实际金额',sort: true, width:120}*/
            ,{field: 'currentBalance', title: '账户余额',sort: true, width:120}
          /*  ,{field: 'desc', title: '备注',sort: true, width:120}*/
            ,{field: 'payType', title: '支付方式',sort: true, width:120,templet : function (d) {
                    var payTypeMsg;
                    (d.payType == 1 ? payTypeMsg = "支付宝支付" : (d.payType == 2) ? payTypeMsg = "微信支付" : (d.payType == 3) ? payTypeMsg = "余额支付" :(d.payType == 4) ? payTypeMsg = "系统支付" : payTypeMsg = "手工转账支付")
                    return payTypeMsg;
                }},
         {field: 'type', title: '类型',sort: true, width:150, templet : function (d) {
					var statusMsg;
            		(d.type == 1 ? statusMsg = "APP充值" : (d.type == 3) ? statusMsg = "后台充值": (d.type == 2) ? statusMsg = "用户提现" : (d.type == 16) ? statusMsg = "后台手工提现" : statusMsg = "其他方式充值")
					return statusMsg;
                }}
            ,{field: 'time',title:'提现时间',width:195,templet: function(d){
                    return UI.getLocalTime(d.time);
                }},
                {fixed: 'right', width: 115,title:"操作", align:'left', toolbar: '#roomListBar',templet : function (d){ return d.userId}}
        ]]
        ,done:function(res, curr, count){
            checkRequst(res);
            // 初始化时间控件
            ///layui.form.render('select');
            //日期范围
            layui.laydate.render({
                elem: '#raiseCashMsgDate'
                ,range: "~"
                ,done: function(value, date, endDate){  // choose end
                    //console.log("date callBack====>>>"+value); //得到日期生成的值，如：2017-08-18
                    var startDate = value.split("~")[0];
                    var endDate = value.split("~")[1];
                    timeBigen = startDate.replace(' ','');
                    timeEnd = endDate.replace(' ','');

                    // Count.loadGroupMsgCount(roomJId,startDate,endDate,timeUnit);
                    table.reload("raiseCash_table",{
                        page: {
                            curr: 1 //重新从第 1 页开始
                        },
                        where: {
                            // userId : data.userId,  //搜索的关键字
                            startDate : startDate,
                            endDate : endDate
                        }
                    })
                }
                ,max: 0
            });
            $(".current_total").empty().text((0==res.total ? 0:res.total));
            if(localStorage.getItem("IS_ADMIN")==0){
                $(".btn_addLive").hide();
                $(".delete").hide();
                $(".chatMsg").hide();
                $(".member").hide();
            }
        }
    });

    // 列表操作
    table.on('tool(redEnvelope_table)', function(obj){
        var layEvent = obj.event,
            data = obj.data;
        console.log(data);
        if(layEvent === 'delete'){// 红包领取详情

        }
    });

    //首页搜索
    $(".search_live").on("click",function(){

    	var status =$("#statusSelect").val();
        // 关闭超出宽度的弹窗
        $(".layui-layer-content").remove();



        table.reload("raiseCash_table",{
            url:request("/console/systemRecharge")+"&type="+2+"&status="+status,
            where: {
                userId : $("#userId").val(), //搜索的关键字
            },
            page: {
                curr: 1 //重新从第 1 页开始
            }
        })
        $("#userId").val("");
        // $("#complaint_select").val(0);
    });

    // 导出数据
    $(".export_body").on("click",function(){
        var requestUrl = request('/console/exportDataWithdraw') + '&userId=' + $("#userId").val() + '&startDate=' + timeBigen + '&endDate=' + timeEnd+'&status=' + $("#statusSelect").val();
        layui.layer.open({
            title: '确定导出数据吗'
            ,type : 1
            ,offset: 'auto'
            ,area: ['370px','200px']
            ,btn: ['导出', '取消']
            ,content:  '<form class="layui-form" method="post" action='+requestUrl+'>'
                +  '<button id="exportData_submit"  class="layui-btn" type="submit" lay-submit="" style="display:none">确定导出</button>'
                +'</from>'
            ,success: function(index, layero){
                layui.form.render();
            }
            ,yes: function(index, layero){
                //确定按钮的回调
                $("#exportData_submit").click();
                layui.layer.close(index); //关闭弹框
            }
            ,btn2: function(index, layero){
                //按钮【取消】的回调

                //return false 开启该代码可禁止点击该按钮关闭
            }

        });

    });//layui END

});

var WithDraw = {
		getData:function(){

			 var checkStatus = layui.table.checkStatus('raiseCash_table');

			   console.log(checkStatus.data)

			   if(0 == checkStatus.data.length){
		            layer.msg("请勾选要转账的行");
		            return;
		        }

			   for (var i = 0; i < checkStatus.data.length; i++) {
				  if( checkStatus.data[i].status != 0){
					  layer.msg("请不要勾选已经转过账的提现记录！！");
			            return;
				  }
			   }

			layer.confirm('您确定要设置用户的提现状态吗？不可恢复！！！', {
				  btn: ['确定','取消'] //按钮
				}, function(){

					   var checkStatus = layui.table.checkStatus('raiseCash_table');

					   console.log(checkStatus.data)



					   if(0 == checkStatus.data.length){
				            layer.msg("请勾选要删除的行");
				            return;
				        }

					   var arr = new Array();
					   for (var i = 0; i < checkStatus.data.length; i++) {
						   arr.push( checkStatus.data[i].id);
					   }
					   $.ajax({
						   url:request("/updateWithdraw")+"&ids="+arr,
						   dataType:"json",
						   success:function(data){
							   if(data.success == 0){
								   layer.msg(data.msg);
								   layui.table.reload("raiseCash_table");

								   return;
							   }
							   layer.msg(data.msg);
						   }

					   })
					   console.log(arr);

				});


		},


    getPersonalInfo:function(userId,cardNO){
			console.log(userId)


            var obj = null;
            $.ajax({
            	url:request("/getRealNameCertifyByUserId")+"&userId="+userId,

            	dataType:'json',
            	async:false,
            	success:function(data){
            		console.log(data)
            		console.log(data.data)
            		console.log(data.data[0])
            		if(data.success == 0){
            			obj = data.data;
            		}else{
            			 layer.alert(data.msg);
            		}
            	},error:function(){
            		 layer.alert("请求失败！！！！");
            	}
            })

          var content = '<div class="layui-form-item"><label class="layui-form-label">用户真名：</label><div class="layui-input-block"><input type="text" name="realname" id="realname" autocomplete="off" placeholder="" class="layui-input" value='+obj.realname+'></div></div>'+
          '<div class="layui-form-item"><label class="layui-form-label">身份证号：</label><div class="layui-input-block"><input type="text" name="idCard" id="idCard" autocomplete="off" placeholder="" class="layui-input" value='+obj.idCard+'></div></div>'+
          '<div class="layui-form-item"><label class="layui-form-label">银行卡号：</label><div class="layui-input-block"><input type="text" name="cardNO" id="cardNO" autocomplete="off" placeholder="" class="layui-input" value='+obj.cardNO+'></div></div>'+
          '<div class="layui-form-item"><label class="layui-form-label">银行名称：</label><div class="layui-input-block"><input type="text" name="bankName" id="bankName" autocomplete="off" placeholder="" class="layui-input" value='+obj.bankName+'></div></div>'+
          '<div class="layui-form-item"><label class="layui-form-label">&nbsp;&nbsp;卡类型：</label><div class="layui-input-block"><input type="text" name="cardType" id="cardType" autocomplete="off" placeholder="" class="layui-input" value='+obj.cardType+'></div></div>'
               layui.layer.open({
               title:"人工设置实名",
               type: 1,
               btn:["确定","取消"],
               area: ['400px','400px'],
               content: '<form id="policy" class="layui-form" action="">'+content+' '+"</form>"

               ,yes: function(index, layero){ //确定按钮的回调

               	 layui.layer.close(index); //关闭弹框
               }

           });
		}
}



var appRecharge={

    // 删除账单记录


    btn_back:function(){
        $("#redEnvelope").show();
        $("#receiveWater").hide();

    }

}
