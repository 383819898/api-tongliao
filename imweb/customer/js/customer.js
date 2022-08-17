var customerData = {
	customerId:null,
	serviceId:null,
	nickname:"", //我的nickname
	isJoinUp:0,  //是否接入人工客服  1: 表示接入
}


$(function() {
	loadUploadUrl();
	init();
	NetWork.networkListener(onNet,offNet);
});

function loadUploadUrl(){   //将上传的url赋到页面
	//我的资料-头像上传
	$("#prop #avatarForm").attr("action",""+AppConfig.uploadAvatarUrl); 
	$("#uploadFileModal #uploadFileFrom").attr("action",""+AppConfig.uploadUrl); 
}

function over(){
	var s=Document.getElementById("img1");
	s.src="img/378FMI5VN@}]1D{H5(NOP8D.png"
}
function out(){
	var s=Document.getElementById();
}


$(document).ready(function(){   

	$(".nano").nanoScroller();
	
});


window.onload=function(){
	//进入页面，打开10000号系统聊天框，并显示欢迎语。
	var welcomeContent ="欢迎使用客服系统。";
	

	// var welcomeHtml = "<p class='chat_content'>欢迎使用客服系统。</p>";
					// +"<a href=''>常见问题</a>";
					// <a  id="Snapchat" href="#" class="Cbtn Cbtn-outline" onclick="Customer.register();">人工客服</a>
	
	
	// welcome(welcomeHtml);
	
	var welcomeHtml = "<div id=msg_1000>"
			            +		"<div class='clearfix'>"
						+			"<div style='overflow: hidden;' >"
						+	    		"<div  class='message you'>"
						+			        "<div  class='message_system'>"
						+			        	"<div  class='content'>"+getTimeText(Date.parse(new Date())/1000)+"</div>"
						+			        "</div>"
						+	        		"<img  class='avatar' onerror='this.src=\"img/ic_avatar.png\"' src='../img/logo.png'>"
						+	        		"<div class='content'>"
			            +	            	"<div class='bubble js_message_bubble bubble_default left'>"
						+	               		 "<div class='bubble_cont'>"
						+							'<div class="plain">'
						+			                     "<pre class='js_message_plain'>"+welcomeContent+"</pre>"
						+			                "</div>"
						+	               		 "</div>"
						+	            	"</div>"
						+	        	"</div>"
						+	    	"</div>"
						+		"</div>"
						+	"</div>"
			       		+"</div>";


		// 追加消息
		$("#messageContainer").append(welcomeHtml);

		// 滚动到底部
		UI.scrollToEnd();
	


};

function init() { //进入主页面后执行


	WEBIM.initConfig();
	// 发送
	$("#btnSend").click(function() {
		var content = $("#messageBody").val();
		if (myFn.isNil(content)) {
			ownAlert(2,"请输入要发送的内容");
			return;
		}
		var msg=Customer.createMessage(1, content,customerData.serviceId);
		var toJid = customerData.serviceId;
		UI.sendMsg(msg,toJid);
		
	});	
	 //加载表情
    $.getJSON("../json/emoji.json",function(data){  
            var emojiHtml = "";  //emoji 的Html
            //var emojiNum = 0;
            $.each(data,function(infoIndex,info){  
                  emojiHtml +="<img src='../emojl/"+info['filename']+".png' alt='"+info['chinese']+"' title='"+info['chinese']+"' onclick='Customer.choiceEmojl(\"" +"["+info['english']+"]"+ "\")' />"
                  //emoji[emojiNum] = emojiHtml;
            }) 
            $("#emojl-panel #emojiList").append(emojiHtml);
    });
	// 表情
	$("#btnEmojl").click(function(event) {
		$("#gif-panel").hide();
		//$("#gif-panel #gifList").nanoScroller();
		var e = window.event || event;
		if (e.stopPropagation) {
			e.stopPropagation();
			
		} else {
			e.cancelBubble = true;
		}
		$('#emojl-panel').toggle();
		

	});
	// 表情框
	$("#emojl-panel").click(function(event) {
		var e = window.event || event;
		if (e.stopPropagation) {
			e.stopPropagation();
		} else {
			e.cancelBubble = true;
		}
	});
	//监听图片上传按钮
	UI.uploadImg();
		
	//监听文件上传按钮
	UI.uploadFile();

	// 空白点击事件
	document.onclick = function() {
		
		$("#emojl-panel").hide();
		$("#gif-panel").hide();
	};

	$("#messageBody").empty();

}

var Customer = {
	msgsList : {},
	user:null,
	notice:null,// 消息通知标识
	isGroup:0,//1是群聊 0是单聊
	curViewUnreadNum:0, //当前界面的消息未读数量
	isShowNewMsgCut:true,//是否显示新消息分割线,初始为需要显示
	isShowUnreadCount:true,//是否需要显示新消息未读统计，初始需要显示
	
	/** 打开会话*/
	open : function(from, name) {

		this.nickName = name;		
	
		
	 
		$("#messageContainer   #bottomUnreadCount").remove();
		$("#messageContainer .message_system").remove();

	
		Customer.isOpen = true;
		Customer.to = from;
		Customer.toUserId = WEBIM.getUserIdFromJid(from);
		Customer.resource = WEBIM.getResource(from);
		var type = WEBIM.isGroup(Customer.fromUserId)?0 :1;
		//type 1 单聊  0 群组
	
	
		

		//初始化当前界面的新消息未读数据
		Customer.curViewUnreadNum=0;
		Customer.isShowNewMsgCut=true;
		Customer.isShowUnreadCount=true;
		
		DataMap.timeSendLogMap[Customer.fromUserId]=0;	
		Temp.minTimeSend=0;
		
	
		var chatType=1==type?WEBIM.CHAT:WEBIM.GROUPCHAT;	
	
	},
	register : function(){

		myFn.invoke({
			url : '/CustomerService/register',
			data : {
				companyId:AppConfig.companyId,
				departmentId:AppConfig.departmentId
			},
			success : function(result) {
				if (1 == result.resultCode) {
					if(result.data.serviceId==0){
						ownAlert(3,"抱歉,当前没有人工客服在线，请稍后再试！");
						return;
					}
					customerData.customerId = result.data.customerId;
					customerData.nickname = "客户"+result.data.customerId;
					customerData.serviceId = result.data.serviceId;
					myData.access_token = result.data.access_token;
					myData.password=$.md5(customerData.customerId+"");
					WEBIM.setUserIdAndToken(customerData.customerId,myData.access_token);
					WEBIM.initWebIM(AppConfig.boshUrl,customerData.customerId,myData.resource,
					myData.password,myData.keepalive,myData.nickname,AppConfig.boshDomain);
		        
			        //xmpp登录
				    WEBIM.loginIM(function(){
				            console.log("ShikuWebIM loginSuccess =========>");
				        	$("#Snapchat").html("已接入人工客服");
							$("#Snapchat").removeAttr("onclick");
							//建立对话
							Customer.createChat();
							customerData.isJoinUp = 1;
					});		
				
					
				} else {
					ownAlert(2,"连接人工客服失败，请稍后再试！");
				}
			},
			error : function(result) {
				ownAlert(2,"连接人工客服失败，请稍后再试！");
			}
		});
	},
	createChat:function(){ // 建立对话

		//-----------UI 处理--------------
		
		//1.客服头像显示到左上角
		
		//模拟打开聊天框
		var toUserJid = customerData.serviceId;
		//Customer.open(toUserJid,customerData,customerData.nickname);
		//发送建立对话xmpp协议
		Customer.sendCreateSession();
		//2.您好,我是客服 XXXX ，很高兴为您服务，请问有什么可以帮到您的
		//同时将右上角的人工客服，改为已接入人工客服

		$("#chatPanel Snapchat").innerHTML = "已接入人工客服";
		ConversationManager.showAvatar(customerData.serviceId,0);//显示聊天窗口顶部头像(单聊)
		$("#messagePanel #messageContainer").empty();
	},
	//发送建立会话的xmpp消息
	sendCreateSession:function(){
		var type = 'chat';
		var to = customerData.serviceId ;
		var from = customerData.customerId ;

		console.log("from :  "+ from +"    to   :   "+to);

		//var msg=WEBIM.createMessage(320,customerData.customerId);
		var msg=Customer.createMessage(320,customerData.customerId,customerData.serviceId);

		// 存储消息
		//DataMap.msgMap[messageId]=msg;
		WEBIM.sendMessage(msg);
		shikuLog("发送建立会话xmpp："+msg.messageId);
	},
	choiceEmojl : function(key) {
		// var emojiHtml = "<img data-alias='hehe' src='" + _emojl[key] + "' width='25' height='25' title='"+key+"'/>";
		$("#messageBody").val($("#messageBody").val() + key);
		$("#emojl-panel").hide();
	},
	//结束会话
	endChat : function(fromUserId){
		//结束会话，调用接口，将客服的会话人数减1
		myFn.invoke({
				url : '/org/employee/updateEmployee',
				data : {
					userId : fromUserId,
					operationType : 2,
					companyId : AppConfig.companyId,
					departmentId : AppConfig.departmentId,
				},
				success : function(result) {
					if (1 == result.resultCode) {
						window.opener=null;
						window.open('','_self');
						window.close();
					}
				},
				error : function(result) {
				}
		});
		
	},
	/*构建一条消息*/
	createMessage :function(type,content,toUserId){
		var timeSend = WEBIM.getServerTime();
		var messageId = WEBIM.randomUUID();
			var msg = {
				messageId:messageId,
				fromUserId : customerData.customerId,
				fromUserName : customerData.nickname,
				toUserId:toUserId+"",
				content : content,
				timeSend : timeSend,
				type : type
			};
			if(true==WEBIM.encrypt)
				msg.isEncrypt=1;
			if(4>type&&6!=type&&1==myData.isReadDel)
				msg.isReadDel=myData.isReadDel;
			
			msg.to=msg.toUserId;
			msg.chatType = (SKIMSDK.isGroup(msg.to)?"groupchat":"chat");
			
			return msg;
			
	},







}





