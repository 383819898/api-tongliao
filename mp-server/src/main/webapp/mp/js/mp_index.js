var i=1;
var sum=0;
var pageIndex=0;
var UI={
	getText:function(text,length){
		if(mpCommon.isNil(text))
			return  " ";
		text = text.replace(/<br\/>/g, '');
		if(!length)
			length=15;
		if (text.length<=length)
			return text;
		text = text.substring(0,length)+"...";
		return text;

	},
	// 首页
	index:function(){
		$("#li_one").css("background-color","#4E5465");
		$("#li_two").css("background-color","#393D49");
		$("#li_three").css("background-color","#393D49");
		$("#li_four").css("background-color","#393D49");
		$("#li_five").css("background-color","#393D49");
		$("#li_six").css("background-color","#393D49");
		$("#li_seven").css("background-color","#393D49");
		$("#index").show();
		$("#index_one").show();
		$("#update_menu").hide();
		$("#pushText").hide();
		$("#pushOneText").hide();
		$("#pushManyText").hide();
		$("#menu").hide();
		$("#msg_manager").hide();
		$("#fan").hide();
		$("#index_newMsg").hide();
		$("#index_add").hide();
		
		$("#pushMsg").hide();
		$("#pageIndex").hide();
		$("#newMsg_item").hide();
		$("#msg_item").hide();
		mpHttpApi.updateHomeCount();
	},
	// 群发消息
	pushText:function(){
		$("#li_one").css("background-color","#393D49");
		$("#li_two").css("background-color","#4E5465");
		$("#li_three").css("background-color","#393D49");
		$("#li_four").css("background-color","#393D49");
		$("#li_five").css("background-color","#393D49");
		$("#li_six").css("background-color","#393D49");
		$("#li_seven").css("background-color","#393D49");
		$("#index_one").hide();
		$("#update_menu").hide();
		$("#pushText").show();
		$("#pushOneText").hide();
		$("#pushManyText").hide();
		$("#menu").hide();
		$("#msg_manager").hide();
		$("#fan").hide();
		$("#index").hide();
		$("#pushMsg").hide();
		$("#msg_item").hide();
	},
	// 群发单条图文消息
	pushOneText:function(){
		$("#li_one").css("background-color","#393D49");
		$("#li_two").css("background-color","#393D49");
		$("#li_three").css("background-color","#4E5465");
		$("#li_four").css("background-color","#393D49");
		$("#li_five").css("background-color","#393D49");
		$("#li_six").css("background-color","#393D49");
		$("#li_seven").css("background-color","#393D49");
		
		$("#pushOneText").show();
		$("#update_menu").hide();
		$("#index_one").hide();
		$("#pushText").hide();
		$("#pushManyText").hide();
		$("#menu").hide();
		$("#msg_manager").hide();
		$("#fan").hide();
		$("#index").hide();
		$("#pushMsg").hide();
		$("#msg_item").hide();
	},
	// 群发多条图文消息
	pushManyText:function(){
		$("#li_one").css("background-color","#393D49");
		$("#li_two").css("background-color","#393D49");
		$("#li_three").css("background-color","#393D49");
		$("#li_four").css("background-color","#4E5465");
		$("#li_five").css("background-color","#393D49");
		$("#li_six").css("background-color","#393D49");
		$("#li_seven").css("background-color","#393D49");
		
		$("#pushManyText").show();
		$("#update_menu").hide();
		$("#index_one").hide();
		$("#pushText").hide();
		$("#pushOneText").hide();
		$("#menu").hide();
		$("#msg_manager").hide();
		$("#fan").hide();
		$("#index").hide();
		$("#pushMsg").hide();
		$("#msg_item").hide();
	},
	// 自定义菜单
	menu:function(num){
		var html="";
		var body="";
		$("#li_one").css("background-color","#393D49");
		$("#li_two").css("background-color","#393D49");
		$("#li_three").css("background-color","#393D49");
		$("#li_four").css("background-color","#393D49");
		$("#li_five").css("background-color","#4E5465");
		$("#li_six").css("background-color","#393D49");
		$("#li_seven").css("background-color","#393D49");
		$("#menu").show();
		$("#menu_menuId").hide();
		$("#update_menu").hide();
		$("#index_one").hide();
		$("#pushText").hide();
		$("#pushOneText").hide();
		$("#pushManyText").hide();
		$("#msg_manager").hide();
		$("#fan").hide();
		$("#index").hide();
		$("#pushMsg").hide();
		$("#menuPage").show();
		$("#msg_item").hide();
		
		if($("#parentId").val()==0){
			$("#menu_url").hide();
		}else{
			$("#menu_url").show();
		}
		
		


		mpCommon.invoke({
			url : '/mp/menuList',
			data : {},
			success : function(result) {
				if(result.data.length==0){
					layui.layer.msg("暂无数据");
				}else{

					for(var i=0;i<result.data.length;i++){
                        var url="";
						if(result.data[i].url!=undefined){
							url=result.data[i].url;
						}

						html+="<tr><td>"+result.data[i].id+"</td><td>0</td></td><td>一级菜单</td><td>"+result.data[i].name+"</td><td>"+result.data[i].index
						+"</td><td></td><td>"+url
						+"</td><td><button onclick='UI.deleteMenu(\""+result.data[i].id+"\")' class='layui-btn' style='height:30px;line-height:30px;border-radius:3px'>删除</button><button onclick='UI.updateMenu(\""+result.data[i].id+"\",null,\""+result.data[i].name+"\",\""+result.data[i].index+"\",\""
						+result.data[i].url+"\",\""+result.data[i].desc+"\",\""+result.data[i].menuId+"\")' class='layui-btn' style='height:30px;line-height:30px;border-radius:3px'>修改</button></td><tr>";
						
						body+="<option value='"+result.data[i].id+"'>"+"二级菜单 - "+result.data[i].name+"</option>";
						
						
						
					}
					if(result.data[0].menuList.length>0){
						for(var j=0;j<result.data[0].menuList.length;j++){
							//console.log(result.data[0].menuList[j]);
							html+="<tr><td>"+result.data[0].menuList[j].id+"</td><td>"+result.data[0].menuList[j].parentId+"</td><td>二级菜单</td><td>"+"</td><td>"+result.data[0].menuList[j].index
							+"</td><td>"+result.data[0].menuList[j].name+"</td><td>"+result.data[0].menuList[j].url
							+"</td><td><button onclick='UI.deleteMenu(\""+result.data[0].menuList[j].id+"\")' class='layui-btn' style='height:30px;line-height:30px;border-radius:3px'>删除</button><button onclick='UI.updateMenu(\""+result.data[0].menuList[j].id+"\",\""+result.data[0].menuList[j].parentId+"\",\""+result.data[0].menuList[j].name+"\",\""+result.data[0].menuList[j].index+"\",\""
							+result.data[0].menuList[j].url+"\",\""+result.data[0].menuList[j].desc+"\",\""+result.data[0].menuList[j].menuId+"\")' class='layui-btn' style='height:30px;line-height:30px;border-radius:3px'>修改</button></td><tr>";
						}
					}
					
					
					
				}
				$("#menu_td").empty();
				$("#menu_td").append(html);
				
				$("#parentId").empty();
				$("#parentId").append("<option value='0'>一级菜单</option>");
				$("#parentId").append(body);
				$("#update_parentId").empty();
				$("#update_parentId").append("<option value='0'>一级菜单</option>");
				$("#update_parentId").append(body);
				$("#menu_name").val("");
				$("#menu_num").val("");
				$("#menu_mark").val("");
				$("#menu_url").val("");
			},
			error : function(result) {
				layui.layer.msg("加载数据失败");
			}
		});



	},
	// 添加菜单
	addMenu:function(){
		var reg = new RegExp("^[0-9]*$");  
	    var obj = $("#menu_num").val();
		if($("#menu_name").val()==""){
			layui.layer.alert("请输入菜单名");
			return;
		}else if($("#menu_num").val()==""){
			layui.layer.alert("请输入排序");
			return;
		}else if(!reg.test(obj)){
			 layui.layer.alert("排序必须为数字!");  
			 return;
		}
		var url="";
		if($("#menu_url").val()!=""){
			url=$("#menu_url").val();
		}

		
		mpCommon.invoke({
			url : '/mp/menu/save',
			data : {
				parentId:$("#parentId").val(),
				menuId:$("#menu_menuId").val(),
				name:$("#menu_name").val(),
				index:$("#menu_num").val(),
				desc:$("#menu_mark").val(),
				urls:url
			},
			success : function(result) {
				UI.menu();
				$("#menu_url").hide();// 访问地址
				$("#menu_name").val("");
				$("#menu_num").val("");
				$("#menu_mark").val("");
				$("#menu_url").val("");
			},
			error : function(result) {
				layui.layer.msg("添加菜单失败");
			}
		});


	},
	// 修改菜单
	updateMenu:function(id,parentId,name,index,url,desc,menuId){
		$("#index").hide();
		$("#index_one").hide();
		$("#pushText").hide();
		$("#pushOneText").hide();
		$("#pushManyText").hide();
		$("#menu").hide();
		$("#msg_manager").hide();
		$("#fan").hide();
		
		
		
		$("#pushMsg").hide();
		$("#update_menu").show();
		$("#update_id").val(id);
		$("#update_name").val(name);
		$("#update_index").val(index);
		if(url=="undefined"){
			$("#update_urls").val("");
		}else{
			$("#update_urls").val(url);
		}
		if(menuId=="undefined"){
			$("#update_menu_id").val("");
		}else{
			$("#update_menu_id").val(menuId);
		}
		
		if(desc=="undefined"){
			$("#update_desc").val("");
		}else{
			$("#update_desc").val(desc);
		}

		if(parentId==null){
			// $("#update_parentId").attr("disabled","disabled");
			$("#update_parentId").val(0);
		}else{
			$("#update_parentId").val(parentId);
		}
		
	},
	// 提交修改菜单
	submit_update:function(){
		var reg = new RegExp("^[0-9]*$");  
	    var obj = $("#update_index").val();
		if($("#update_name").val()==""){
			layui.layer.alert("请输入菜单名");
			return;
		}else if($("#update_index").val()==""){
			layui.layer.alert("请输入排序");
			return;
		}else if(!reg.test(obj)){
			 layui.layer.alert("排序必须为数字!");  
			 return;
		}
		

		mpCommon.invoke({
			url : '/mp/menu/saveupdate',
			data : {
				id:$("#update_id").val(),
				parentId:$("#update_parentId").val(),
				menuId:$("#update_menu_id").val(),
				name:$("#update_name").val(),
				url:$("#update_urls").val(),
				index:$("#update_index").val(),
				desc:$("#update_desc").val()
			},
			success : function(result) {
				layui.layer.alert("修改成功");
				UI.menu(0);
			},
			error : function(result) {
				layui.layer.msg("修改失败");
			}
		});
	},
	// 消息管理
	load_messageList:function(){
		
		
		var html="";
		$("#li_one").css("background-color","#393D49");
		$("#li_two").css("background-color","#393D49");
		$("#li_three").css("background-color","#393D49");
		$("#li_four").css("background-color","#393D49");
		$("#li_five").css("background-color","#393D49");
		$("#li_six").css("background-color","#4E5465");
		$("#li_seven").css("background-color","#393D49");
		
		$("#msg_manager").show();
		$("#update_menu").hide();
		$("#index_one").hide();
		$("#pushText").hide();
		$("#pushOneText").hide();
		$("#pushManyText").hide();
		$("#menu").hide();
		$("#fan").hide();
		$("#index").hide();
		$("#pushMsg").hide();
		$("#msg_item").hide();

		/*mpCommon.invoke({
			url : '/mp/msgs',
			data : {
				pageIndex:pageIndex,
				pageSize:10
			},
			success : function(result) {
				if(result.data==null){
					html+="<tr><td>暂无数据</td><td></tr>";
				}else{
					sum=result.data.length;
					for(var i=0;i<result.data.length;i++){
						var msg = result.data[i];
						var content = msg.body;
						if(msg.isEncrypt == 1){
							content = msgCommon.decryptMsg(msg.body,msg.messageId,msg.timeSend);
						}
						content=UI.getText(content,20);
						var sender = result.data[i].receiver;
						html+=UI.createMessageItem(sender,msg.nickname,msg.count,getTimeText(new Date(),0),content);

					}
					$("#msg_manager #message_list").empty();
					$("#msg_manager #message_list").append(html);
				}
			},
			error : function(result) {
				layui.layer.msg("加载数据失败");
			}
		});*/


		var MsgListOrder =  mpDataUtils.getMsgListOrder();
		
		if (JSON.stringify(MsgListOrder) != JSON.stringify([])){
			//for(var userId in MsgNumMap){
			MsgListOrder.forEach(function(userId){
				var lastMsg = mpDataUtils.getLastMsgFromUnReadMsgList(userId);
				if(!mpCommon.isNil(lastMsg)){
					var nickName = (lastMsg.fromUserId==getLoginData().userId) ?
					 (mpCommon.isNil(lastMsg.toUserName) ? SKIMSDK.getUserIdFromJid(lastMsg.toJid) : lastMsg.toUserName)  : lastMsg.fromUserName;
		  			html+=UI.createMessageItem(userId, nickName,mpDataUtils.getMsgNum(userId),getTimeText(lastMsg.timeSend,0,1), WEBIM.parseShowMsgTitle(lastMsg));
				}
			});
			//}

		}else{
			html+="<p class='empty_tips'>暂无消息</p>";
		}

		$("#msg_manager #message_list").empty();
		$("#msg_manager #message_list").append(html);

		
	},
	createMessageItem : function(userId,nickName,msgNum,timeStr,lastMsgStr){


		var messageItem ='<li class="message_item " id="msgListItem_'+userId+'">'
						+      '<div class="message_opr">'
						+          '<button onclick="MpChat.openChatPanel('+userId+',\''+nickName+'\')" class="layui-btn" style="height:30px;line-height:30px;border-radius:3px">回复消息</button>'
						+       '</div>'
						+       '<div class="message_info">'
					    +          '<div id="message_time" class="message_time">'+timeStr+'</div>'
						+            '<div class="user_info">'
						+                '<p class="remark_name">'+nickName+'</p>'
						+                '<a target="_blank"  class="avatar">'
						+                    '<img class="roundAvatar" onerror="this.src=\'./images/ic_avatar.png\'" src="'+mpCommon.getAvatarUrl(userId)+'">'
						+          			 '<i id="messageNumw_count" class="message_num '+(msgNum>0 ? 'msgNumShow':'msgNumHide')+'">'+(msgNum>0 ? msgNum : "")+'</i>'
						+                '</a>'
						+            '</div>'
						+        '</div>'
						+        '<div class="message_content text">'
						+          '<div id="lastMsgContent">'+lastMsgStr+'</div>'
						+      '</div>'
						+ '</li>';

		return messageItem;

	},
	loadFansList:function(num){
		
		var keyWord = $("#fansList .nickName").val();
		var html="";
		
		mpCommon.invoke({
			url : '/mp/fans',
			data : {
				pageIndex:num,
				pageSize:10,
				keyWord : (mpCommon.isNil(keyWord))?"":keyWord
			},
			success : function(result) {
				if(result.data.pageData==null){
					html+="<tr><td>暂无数据</td><td></tr>";
				}else{
					$("#pageCount").val(result.data.total);
                    // console.log("粉丝数据："+JSON.stringify(result.data));
					$("#fanTotal").empty();
					$("#fanTotal").append("共"+result.data.total+"条");
					sum=result.data.pageData.length;
					var toUserId,nickname;
					for(var i=0;i<result.data.pageData.length;i++){
						toUserId = result.data.pageData[i].toUserId;
						nickname = result.data.pageData[i].toNickname;

						html+="<tr>"
							+	"<td>"
							+		"<img width='40px' onerror='this.src=\"./images/ic_avatar.png\"'  src='"+mpCommon.getAvatarUrl(toUserId)+"'>"
							+	"</td>"
							+	"<td>"+toUserId+"</td>"
							+	"<td>"+nickname+"</td>"
							+	"<td>"
							+		"<button onclick='UI.deleteUser(\""+toUserId+"\")' class='layui-btn layui-btn-sm layui-btn-danger'>删除</button>"
							+		"<button onclick='MpChat.openChatPanel("+toUserId+",\""+nickname+"\");' class='layui-btn layui-btn-sm' style='margin:0 20px;'>发消息</button>"
							+	"</td>"      
							"</tr>";
					}
					$("#fan_td").empty();
					$("#fan_td").append(html);
				}
			},
			error : function(result) {
				layui.layer.msg("加载数据失败");
			}
		});

	},
	// 粉丝管理
	fan:function(num,type){
		
		
		$("#li_one").css("background-color","#393D49");
		$("#li_two").css("background-color","#393D49");
		$("#li_three").css("background-color","#393D49");
		$("#li_four").css("background-color","#393D49");
		$("#li_five").css("background-color","#393D49");
		$("#li_six").css("background-color","#393D49");
		$("#li_seven").css("background-color","#4E5465");
		$("#pushMsg").hide();
		$("#pageIndex").show();

		$("#fan").show();
		$("#update_menu").hide();
		$("#index_one").hide();
		$("#pushText").hide();
		$("#pushOneText").hide();
		$("#pushManyText").hide();
		$("#menu").hide();
		$("#index").hide();
		$("#msg_manager").hide();
		$("#fanPage").show();
		$("#msg_item").hide();


		this.loadFansList(num);

		if(type==0){
			UI.limit(num,"fan")
		}

	},
	// 发送群发消息
	pushTextToAll:function() {
		var body=$("#textbody").val();
		if(null == body || "" == body || undefined == body){
			layui.layer.alert("请输入群发内容");
			return;
		}
		
		mpCommon.invoke({
			url : '/mp/textToAll',
			data : {
				title : body
			},
			success : function(result) {
				if (result.resultCode == 1) {
					layui.layer.alert("群发成功");
					$("#textbody").val("");
				} else {
					layui.layer.alert("群发失败");
				}
			},
			error : function(result) {
				layui.layer.alert("群发失败");
			}
		});
	},
	// 发送单条图文消息
	pushOneToAll:function() {
		var title=$("#pushbody").val()
		if(null == title  || "" == title || undefined == title){
            layui.layer.alert("请输入标题");
            return;
		}
		var sub=$("#pushbodyTitle").val();
        if(null == sub  || "" == sub || undefined == sub){
            layui.layer.alert("请输入小标题");
            return;
        }
		var img=$("#pushbodyImgUrl").val();
        if(null == img  || "" == img || undefined == img){
            layui.layer.alert("请输入图片Url");
            return;
        }
		var url=$("#pushbodyHtmlUrl").val();
        if(null == url  || "" == url || undefined == url){
            layui.layer.alert("请输入网页Url");
            return;
        }
		

		mpCommon.invoke({
			url : '/mp/pushToAll',
			data : {
				title:title,
				sub:sub,
				img:img,
				url:url
			},
			success : function(result) {
				if (result.resultCode == 1) {
					layui.layer.alert("群发成功");
					$("#pushbody").val("");
					$("#pushbodyTitle").val("");
					$("#pushbodyImgUrl").val("");
					$("#pushbodyHtmlUrl").val("");
				} else {
					layui.layer.alert("群发失败");
				}
			},
			error : function(result) {
				layui.layer.alert("群发失败");
			}
		});
	},
	// 发送多条图文消息
	pushManyToAll:function () {

        var bodyVal = $("#body").val();
        var bodyHtmlUrlVal = $("#bodyHtmlUrl").val();
        var bodyImgUrlVal = $("#bodyImgUrl").val();

		if(null == bodyVal || "" == bodyVal || undefined == bodyVal){
            layui.layer.alert("请输入标题");
            return;
		}
        if(null == bodyImgUrlVal || "" == bodyImgUrlVal || undefined == bodyImgUrlVal){
            layui.layer.alert("请输入图片Url");
            return;
        }
        if(null == bodyHtmlUrlVal || "" == bodyHtmlUrlVal || undefined == bodyHtmlUrlVal){
            layui.layer.alert("请输入网页Url");
            return;
        }
        var title=new Array();
		title.push(bodyVal);
		var url=new Array();
		url.push(bodyHtmlUrlVal);
		var img=new Array();
		img.push(bodyImgUrlVal);
		
		for(var j=2;j<=i;j++){
            var fVal = $("#f"+j).val();
            var dVal = $("#d"+j).val();
            var cVal = $("#c"+j).val();
			if( undefined == fVal){
				continue;
			}
			if(null == fVal || "" == fVal || undefined == fVal){
				layui.layer.alert("请输入标题");
                return;
			}
            if(null == cVal || "" == cVal || undefined == cVal){
                layui.layer.alert("请输入图片Url");
                return;
            }
            if(null == dVal || "" == dVal || undefined == dVal){
            	layui.layer.alert("请输入网页Url");
            	return;
			}

			title.push($("#f"+j).val());
			url.push($("#d"+j).val());
			img.push($("#c"+j).val());
		}

		mpCommon.invokeByArray({
			url : '/mp/manyToAll',
			data : {
				title:title,
				url:url,
				img:img
			},
			success : function(result) {
				if (result.resultCode == 1) {

					$("#body").val("");
					$("#bodyImgUrl").val("");
					$("#bodyHtmlUrl").val("");
                    for(var j=2;j<=i;j++){
                        
						$("#f"+j).remove();
						$("#d"+j).remove();
						$("#c"+j).remove();
						$("#delete"+j).remove();
                    }
                    title=[];
                    url=[];
                    img=[];
                    i = 1;// 重置
                    layui.layer.alert("群发成功");
				} else {
					alert("群发失败");
				}
			},
			error : function(result) {
				layui.layer.alert("群发失败");
			}
		});
	},
	// 新增
	add:function(){
		i++;
		var divId="manyDiv"+i;
		var table="<div id='"+divId+"' style='margin-top: 1%'><input id='f"+i+"' name='title' class='layui-input' style='width: 20%;display: inline;margin-right: 1.3%' placeholder='请输入标题'>"
		+"<input id='c"+i+"' name='img' class='layui-input' style='width: 20%;display: inline;margin-right: 1.3%' placeholder='请输入图片url'>"
		+"<input id='d"+i+"' name='url' class='layui-input' style='width: 20%;display: inline;margin-right: 1.3%' placeholder='请输入网页url'>"
		+"<button id='delete"+i+"' onclick='UI.removeManyUI("+divId+")' class='layui-btn' type='button' style=''>删除</button></div>";

		$("#tb").append(table);

	},
	removeManyUI:function(id){
		id.remove();
		// i--;
	},
	// 删除粉丝
	deleteUser:function(userId){
		
		ownAlert(4,"此操作会将该粉丝删除,是否继续？",function(){

			mpCommon.invoke({
				url : '/mp/fans/delete',
				data : {
					toUserId:userId
				},
				success : function(result) {
					if (result.resultCode == 1) {
						ownAlert(1,"删除成功");
						UI.fan(0);
					} else {
						ownAlert(2,"删除失败");
					}
				},
				error : function(result) {
					ownAlert(2,"删除失败");
				}
			});

		});

		
	},
	findMsgList:function(toUserId){
		$("#newMsg_item").show();
		$("#msg_item").show();
		$("#index_newMsg").hide();
		$("#msg_manager").hide();
		var html="";
		
		mpCommon.invoke({
			url : '/mp/msg/list',
			data : {
				toUserId:toUserId
			},
			success : function(result) {
				if(result.data==null){
					html+="<tr><td>暂无数据</td><td></tr>";
				}else{
					for(var i=0;i<result.data.length;i++){
					    var msg = result.data[i];
						var sender = msg.sender;
						var plaintext = msg.content;
						// 消息解密处理
                        if(1 == msg.isEncrypt){
                            // 明文
                            plaintext = msgCommon.decryptMsg(msg.content,msg.messageId,msg.timeSend);
                            console.log(" msg : "+msg.content+"      role:   "+msg.isEncrypt+"       msgId:  "+msg.messageId+"    timeSend:  "+msg.timeSend +" plaintext:   "+plaintext);
                        }
						plaintext=UI.getText(plaintext,20);
						html+="<tr>"
						    +	"<td>"
						    +		"<img width='40px' onerror='this.src=\"./images/ic_avatar.png\"' src='"+mpCommon.getAvatarUrl(msg.receiver)+"'>"
						    +	"</td>"
						    +    "<td>"+msg.receiver+"</td>"
						    +	 "<td>"+result.data[i].nickname+"</td>"
						    +	 "<td>"+plaintext+"</td>"
						    +	 "<td>"
						    +		"<button onclick='MpChat.openChatPanel(" + msg.receiver + "," + result.data[i].nickname + ");'  class='layui-btn' >发消息</button>"
						    +	 "</td>"
						    + "</tr>";
					}
					$("#newMsg_body").empty();							
					$("#newMsg_body").append(html);
					$("#Msg_body").empty();
					$("#Msg_body").append(html);
				}
			},
			error : function(result) {
				layui.layer.alert("加载数据失败");
			}
		});
	},
	// select框变化
	change:function(){
		if($("#parentId").val()==0){
			$("#menu_url").hide();
			$("#menu_menuId").hide();
		}else{
			$("#menu_url").show();
			$("#menu_menuId").show();
		}
	},
	// 删除菜单
	deleteMenu:function(id){

		mpCommon.invoke({
			url : '/mp/menu/delete',
			data : {
				id:id
			},
			success : function(result) {
				layui.layer.confirm("删除成功",{btn:["确定"],closeBtn:0,icon:1,title:"提示"},function (index) {
					layui.layer.close();
					UI.menu(0);
				});
			},
			error : function(result) {
				layui.layer.alert("删除失败");
			}
		});


	},
	limit:function(index,functionName){
		layui.use('laypage', function(){
        var laypage = layui.laypage;
        //console.log($("#pageCount").val());
        var count=$("#pageCount").val();
        //执行一个laypage实例
        laypage.render({
            elem: functionName+"_laypage"
            ,count: count
            ,layout: [ 'prev', 'page', 'next', 'refresh', 'skip']
            ,jump: function(obj){
            
				if(functionName == "fan"){

					if(index==1){
						UI.fan(1,1)
						index=0;
					}else{
						UI.fan(obj.curr-1,1)
					}


				}else if(functionName == "msg"){

					if(index==1){
						UI.msg(1,1)
						index=0;
					}else{
						UI.msg(obj.curr-1,1)
					}

				}else if(functionName == "newAddUserNum"){

					if(index==1){
						UI.newAddUser(1,1)
						index=0;
					}else{
						UI.newAddUser(obj.curr-1,1)
					}

				}else if(functionName == "newMsg"){

					if(index==1){
						UI.newMsg(1,1)
						index=0;
					}else{
						UI.newMsg(obj.curr-1,1)
					}

				}

            	
            }
   		 })
 	   })
	},
	// 返回
	return_btn:function(){
		$("#menu").show();
		$("#update_menu").hide();
	},
	online:function(){
		// 用户在线
		$("#userInfo #avatar").removeClass("headChang");
		$("#userInfo #status").removeClass("user-back");
		$("#userInfo #status").addClass("user-online");
	},
	offline:function(){
		// 用户离线
		$("#userInfo #avatar").addClass("headChang");
		$("#userInfo #status").removeClass("user-online");
		$("#userInfo #status").addClass("user-back");
	},

}
