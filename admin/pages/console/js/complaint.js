/*** 举报管理js  **/

var page=0;
var sum=0;
var consoleAdmin = localStorage.getItem("account");
$(function(){
	Com.complaint(0);
	Com.limit();
});


function seeDetail(detail){
	
	if(detail != '' && detail != null && detail != "undefined"){
	    layer.open({
	        type: 1
	        ,offset: 1 //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
	        ,id: 'layerDemo'+1 //防止重复弹出
	        ,content: detail
	        ,btn: '关闭全部'
	        ,	  area: ['500px', '300px']
	        ,btnAlign: 'c' //按钮居中
	        ,shade: 0 //不显示遮罩
	        ,yes: function(){
	          layer.closeAll();
	        }
	      });
	}else{
		layer.alert("用户没有上传详情！");
	}

}

function seePics(imgs){
	
	
	
	if(imgs != '' && imgs != null && imgs != "undefined"){
		var imgArr = imgs.split(",");
		
		var pics = "";

		for(var i = 0;i<imgArr.length;i++){
			pics += '<div style="float: left;width: 200px;height:200px;text-align: center;line-height: 100px;"><img src='+imgArr[i]+' style="width: 95%;height:95%;object-fit: cover;"/></div>';
		}
		
		
	    layer.open({
	        type: 1
	        ,offset: 1 //具体配置参考：http://www.layui.com/doc/modules/layer.html#offset
	        ,id: 'layerDemo'+1 //防止重复弹出
	        ,content:pics
	        ,btn: '关闭全部'
	        ,	  area: ['640px', '500px']
	        ,btnAlign: 'c' //按钮居中
	        ,shade: 0 //不显示遮罩
	        ,yes: function(){
	          layer.closeAll();
	        }
	      });
	}else{
		layer.alert("用户没有上传图片！");
	}
	
	  
}
var Com={
	// 举报列表
	complaint:function(e,pageSize){
		html="";
		if(e==undefined){
			e=0;
		}else if(pageSize==undefined){
			pageSize=Common.limit;
		}
		$.ajax({
			url:request('/console/beReport'),
			data:{
				sender:($("#sender").val()==""?"":$("#sender").val()),
				receiver:($("#receiver").val()==""?"":$("#receiver").val()),
				type:$("#complaint_select").val(),
				pageIndex:(e==0?"0":e-1),
				pageSize:pageSize
			},
			dataType:'json',
			async:false,
			success:function(result){
                checkRequst(result);
				if(!Common.isNil(result.data.pageData)){
					$("#pageCount").val(result.data.allPageCount);
					var lockingMsg;
					var lockingRoomMsg;
					for(var i=0;i<result.data.pageData.length;i++){
						if($("#complaint_select").val()==0){
							(result.data.pageData[i].toUserStatus == -1 ? lockingMsg = "解封" : lockingMsg = "封号" )
							html+="<tr align='center'><td>"+result.data.pageData[i].userId+"</td><td>"+result.data.pageData[i].userName+"</td><td>"+result.data.pageData[i].toUserId
						+"</td><td>"+result.data.pageData[i].toUserName+"</td><td>"+result.data.pageData[i].info+"</td><td>"+UI.getLocalTime(result.data.pageData[i].time)
						+"</td><td><button onclick='Com.deleteComplaint(\""+result.data.pageData[i].id
						+"\")' class='layui-btn layui-btn-danger layui-btn-xs delete' style='width: 50px'>删除</button>"
						+"<button onclick='Com.lockIng(\""+result.data.pageData[i].toUserId+"\",\""+(result.data.pageData[i].toUserStatus == -1 ? 1 : -1 )+"\")' class='layui-btn layui-btn-primary layui-btn-xs locking'>"+lockingMsg+"</button>";
						}else if($("#complaint_select").val()==1){
                            (result.data.pageData[i].roomStatus == -1 ? lockingRoomMsg = "解封" : lockingRoomMsg = "封群" )
                            html+="<tr align='center'><td>"+result.data.pageData[i].userId+"</td><td>"+result.data.pageData[i].userName+"</td><td>"+result.data.pageData[i].roomId
						+"</td><td>"+result.data.pageData[i].roomName+"</td><td>"+result.data.pageData[i].info+"</td><td>"+UI.getLocalTime(result.data.pageData[i].time)
						+"</td><td><button onclick='Com.deleteComplaint(\""+result.data.pageData[i].id
						+"\")' class='layui-btn layui-btn-danger layui-btn-xs delete'>删除</button>"
                                +"<button onclick='Com.roomlockIng(\""+consoleAdmin+"\",\""+result.data.pageData[i].roomId+"\",\""+(result.data.pageData[i].roomStatus == -1 ? 1 : -1 )+"\")' class='layui-btn layui-btn-primary layui-btn-xs roomLocking'>"+lockingRoomMsg+"</button>";
						}else if($("#complaint_select").val()==2){
                            (result.data.pageData[i].webStatus == -1 ? lockingRoomMsg = "解禁被举报网页" : lockingRoomMsg = "禁用被举报网页" )
                            html+="<tr align='center'><td>"+result.data.pageData[i].userId+"</td><td>"+result.data.pageData[i].userName+"</td>"
                        +"<td>"+result.data.pageData[i].webUrl+"</td><td>"+result.data.pageData[i].info+"</td><td>"+UI.getLocalTime(result.data.pageData[i].time)
                        +"</td><td><button onclick='Com.deleteComplaint(\""+result.data.pageData[i].id
                        +"\")' class='layui-btn layui-btn-danger layui-btn-xs delete'>删除</button>"
                                +"<button onclick='Com.webLockIng(\""+result.data.pageData[i].id+"\",\""+(result.data.pageData[i].webStatus == -1 ? 1 : -1)+"\")' class='layui-btn layui-btn-primary layui-btn-xs roomLocking'>"+lockingRoomMsg+"</button>";
                        }
						
						  html+= "<button onclick='seeDetail(\""+result.data.pageData[i].remark
						+"\")' class='layui-btn layui-btn-primary layui-btn-xs locking' style='width: 50px'>详情</button><button onclick='seePics(\""+result.data.pageData[i].imgs
						+"\")' class='layui-btn layui-btn-primary layui-btn-xs locking' style='width: 60px'>查看图片</button></td></tr>";
					}
					if($("#complaint_select").val()==0){
                        $("#td_value").show();
						$("#td_value").empty();
						$("#td_value").append("被举报人Id");
                        $('#receiver').attr('placeholder','被举报人Id');

                    }else if($("#complaint_select").val()==1){
                        $("#td_value").show();
					    $("#td_value").empty();
						$("#td_value").append("被举报群组roomId");
                        $('#receiver').attr('placeholder','被举报群组roomId');
					}else if($("#complaint_select").val()==2){
                        $("#td_value").hide();
                        $('#receiver').attr('placeholder','被举报的网页地址');
                    }

                    if($("#complaint_select").val()==0){
					    $("#td_nameValue").empty();
                        $("#td_nameValue").append("被举报人昵称");
                    }else if($("#complaint_select").val()==1){
                        $("#td_nameValue").empty();
                        $("#td_nameValue").append("被举报群组昵称");
                    }else if($("#complaint_select").val()==2){
                        $("#td_nameValue").empty();
                        $("#td_nameValue").append("网页地址");
                    }
                    if(($("#sender").val()==""||$("#sender").val()==undefined)||
					($("#receiver").val()==""||$("#receiver").val()==undefined)){
                        $("#complaint_table").empty();
                        $("#complaint_table").append(html);
					}
					if(localStorage.getItem("role")==1){
						$(".delete").hide();
						$(".locking").hide();
						$(".roomLocking").hide();

					}
					$("#sender").val("");
					$("#receiver").val("");
				}else{
                    layer.msg("暂无数据",{"icon":2});
                }

			}
		})
	},

    // 举报列表
    findComplaint:function(){
        var a = $("#sender").val();
        var b = $("#receiver").val();
        html="";
        $.ajax({
            url:request('/console/beReport'),
            data:{
                sender:($("#sender").val()==""?"":$("#sender").val()),
                receiver:($("#receiver").val()==""?"":$("#receiver").val()),
                type:$("#complaint_select").val(),

            },
            dataType:'json',
            async:false,
            success:function(result){
                checkRequst(result);
                if(!Common.isNil(result.data.pageData)){
                    $("#pageCount").val(result.data.allPageCount);
                    var lockingMsg;
                    var lockingRoomMsg;
                    for(var i=0;i<result.data.pageData.length;i++){
                        if($("#complaint_select").val()==0){
                            (result.data.pageData[i].toUserStatus == -1 ? lockingMsg = "解封" : lockingMsg = "封号" )
                            html+="<tr align='center'><td>"+result.data.pageData[i].userId+"</td><td>"+result.data.pageData[i].toUserId
                                +"</td><td>"+result.data.pageData[i].info+"</td><td>"+UI.getLocalTime(result.data.pageData[i].time)
                                +"</td><td><button onclick='Com.deleteComplaint(\""+result.data.pageData[i].id
                                +"\")' class='layui-btn layui-btn-danger layui-btn-xs delete' style='width: 50px'>删除</button>"
                                +"<button onclick='Com.lockIng(\""+result.data.pageData[i].toUserId+"\",\""+(result.data.pageData[i].toUserStatus == -1 ? 1 : -1 )+"\")' class='layui-btn layui-btn-primary layui-btn-xs locking'>"+lockingMsg+"</button></td></tr>";
                        }else{
                            (result.data.pageData[i].roomStatus == -1 ? lockingRoomMsg = "解封" : lockingRoomMsg = "封群" )
                            html+="<tr align='center'><td>"+result.data.pageData[i].userId+"</td><td>"+result.data.pageData[i].roomId
                                +"</td><td>"+result.data.pageData[i].info+"</td><td>"+UI.getLocalTime(result.data.pageData[i].time)
                                +"</td><td><button onclick='Com.deleteComplaint(\""+result.data.pageData[i].id
                                +"\")' class='layui-btn layui-btn-danger layui-btn-xs delete'>删除</button>"
                                +"<button onclick='Com.roomlockIng(\""+consoleAdmin+"\",\""+result.data.pageData[i].roomId+"\",\""+(result.data.pageData[i].roomStatus == -1 ? 1 : -1 )+"\")' class='layui-btn layui-btn-primary layui-btn-xs roomLocking'>"+lockingRoomMsg+"</button></td></tr>";
                        }
                    }
                    if($("#complaint_select").val()==0){
                        $("#td_value").empty();
                        $("#td_value").append("被举报的人");
                    }else if($("#complaint_select").val()==1){
                        $("#td_value").empty();
                        $("#td_value").append("被举报的群组");
                    }
                    $("#complaint_table").empty();
                    $("#complaint_table").append(html);
                    if(localStorage.getItem("role")==1){
                        $(".delete").hide();
                        $(".locking").hide();
                    }
                    Com.limit(1);
                }else{
                    layer.msg("暂无数据",{"icon":2});
                }

            }
        })
    },


	// 删除
	deleteComplaint:function(id){
		layer.confirm('确定删除该举报内容？',{icon:3, title:'提示信息'},function(index){
			Common.invoke({
				url:request('/console/deleteReport'),
				data:{
					id:id
				},
				success:function(result){
					if(result.resultCode==1){
                        layer.msg("删除成功",{"icon":1});
						Com.complaint(0);
						Com.limit();
					}
				}
			})
		})
		
	},
    // 用户锁定解锁
    lockIng:function(userId,status){
        var confMsg,successMsg="";
        (status == -1 ? confMsg = '确定对该用户做封号处理？':confMsg = '确定解封该用户的账号？');
        (status == -1 ? successMsg = "封号成功":successMsg ="解封成功");
        layer.confirm(confMsg,{icon:3, title:'提示信息'},function(index){

            Common.invoke({
                url : request('/console/changeStatus'),
                data : {
                    userId:userId,
                    status:status
                },
                successMsg : successMsg,
                errorMsg :  "加载数据失败，请稍后重试",
                success : function(result) {
                    /*layui.table.reload("user_list",{

                    })*/
                    Com.complaint(0);
                    Com.limit();
                },
                error : function(result) {
                }
            });
        })
    },

    // 群组锁定解锁
    roomlockIng:function(userId,roomId,status){
	    console.log(" ======》 "+userId+"   =====  "+roomId+"  ====="+status)
        var confMsg,successMsg="";
        (status == -1 ? confMsg = '确定封锁该群组？':confMsg = '确定解封该群组？');
        (status == -1 ? successMsg = "封群成功":successMsg ="解封成功");
        layer.confirm(confMsg,{icon:3, title:'提示信息'},function(index){

            Common.invoke({
                url : request('/console/updateRoom'),
                data : {
                    userId:userId,
                    roomId:roomId,
                    s:status
                },
                successMsg : successMsg,
                errorMsg :  "加载数据失败，请稍后重试",
                success : function(result) {
                    // layui.table.reload("room_table")
                    Com.complaint(0);
                    Com.limit();
                },
                error : function(result) {
                }
            });
        })
    },
    // 网页锁定
    webLockIng:function(webUrlId,webUrlStatus){
	    console.log(" ===== webUrlId ==== : "+webUrlId +" ==== webUrlStatus ==== : "+webUrlStatus);
        // layer.alert("锁定成功");
        var confMsg,successMsg="";
        (webUrlStatus == -1 ? confMsg = '确定禁用该网页地址？':confMsg = '确定解禁该网页地址？');
        (webUrlStatus == -1 ? successMsg = "禁用成功":successMsg ="解禁成功");
        layer.confirm(confMsg,{icon:3, title:'提示信息'},function(index){

            Common.invoke({
                url : request('/console/isLockWebUrl'),
                data : {
                    webUrlId:webUrlId,
                    webStatus:webUrlStatus,
                },
                successMsg : successMsg,
                errorMsg :  "加载数据失败，请稍后重试",
                success : function(result) {
                    // layui.table.reload("room_table")
                    Com.complaint(0);
                    Com.limit();
                },
                error : function(result) {
                }
            });
        })
    },

	limit:function (index) {
        layui.use('laypage', function(){
            var laypage = layui.laypage;
            console.log($("#pageCount").val());
            //执行一个laypage实例
            laypage.render({
                elem: 'laypage'
                ,count: $("#pageCount").val()
                ,limit:Common.limit
                ,limits:Common.limits
                ,layout: ['count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip']
                ,jump: function(obj){
                    console.log(obj)
					if(index == 1){
                        Com.complaint(index,obj.limit)
						index = 0;
                    }else{
                        Com.complaint(obj.curr,obj.limit)
					}

                }
            })
        });
    }
	
}