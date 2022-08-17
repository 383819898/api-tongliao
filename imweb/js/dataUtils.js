/*
	页面数据的封装
	页面数据的处理
	页面数据的获取
*/

var DataMap={
	userMap:{},
	userSetting:{},
	friends:{},
	msgMap:{},
	msgRecordList:{},//好友聊天记录
	msgNum:{},
	myRooms:[],
	rooms:[],
	allFriendsUIds:{}, //存放所有的好友和单向关注用户的userId    key:userId  value :userId
	blackListUIds:{},  //存放已加入黑名单的userId    key:userId  value :userId
	msgStatus:{}, //存方发送消息的状态   key messageId  value 1:送达 2:已读  
	unReadMsg : {}, //存放未读消息    key : 发送方的userId  value: Array[] 存放该用户的所有未读消息(保证先后顺序)
	//msgEndTime : {}, //存放消息记录的结束时间   key: 发送方的userId   value: 结束时间
	loginData:null ,//用户登陆信息
	deleteRooms:{},//储存被踢出的群数据
	talkTime:{}, //储存我的禁言时间  key : 群的id   value: talkTime  我在该群禁言截止时间，为空则没有被禁言        
 	msgIds:[], //储存消息id ，只存最近10条
 	timeSendLogMap:{}, //消息发送时间保存
 	readDelMap:{},//好友阅后即焚 状态
 	deleteFriends:{}/*删除的好友 或群组*/

 }
var Constant={
	loginData:"loginData"
	
}

//临时变量
var Temp={
	user:null,
	friend:null,
	toJid:null,
	toUserId:null,//临时变量
	toNickname:null,
	msgId:null,
	message:null,
	copyMsg:null,//复制的消息
	minTimeSend:0,//当前聊天好友的 历史记录 最早时间
	file:null,
	//上传文件操作 标识  sendImg 发送图片 //  sendFile 发送文件  uploadFile 群文件上传 
	uploadType:"sendImg",
	//弹出好友列表 标识  sendCard 发送名片  @Member @群成员
	//  forward  转发消息
	friendListType:"sendCard", 
	//左边菜单栏标识 当前在哪个菜单
	////messages  聊天列表界面
	leftTitle:"messages",
	//当前列表页面  列表标识  当前在哪个列表
	//messageList  聊天列表界面
	nowList:"messageList",

	roomRole:3,//我在当前群组的 权限标识
	members:{},//当前聊天界面的 成员集合

	

	setJid:function(userId){
		this.toUserId=userId;
		this.toJid=userId;
	}
}

var groupMsgReadList = {};  //用于存放群组消息已读用户列表数据 key ：msgId, value : List<user>  msgId:消息id  user:封装已读用户数据userId,nickname,timeSend 
var groupMsgReadNum = {};   //用于存放群组消息已读数量  key :msgId   value:num 已读数量
var msgHistory = {};  //储存用于获取聊天历史记录的数据
var msgNumCount = 0;  //记录用户接收到的(未读)消息数量
var friendRelation = {}; //记录好友关系  key：userId  value： true/false  true:是好友 false:不是好友


var DataUtils={

	/*
	刷新 当前 当前会话 聊天记录最小时间  拉取漫游使用
	*/
	refreshUIMinTimeSend:function(timeSend,isDel){
		if(isDel){
			Temp.minTimeSend=0;
			return;
		}

		if(0==Temp.minTimeSend)
			Temp.minTimeSend=timeSend;
		else if(Temp.minTimeSend>timeSend){
			Temp.minTimeSend=timeSend;
		}
	},
	getStreamId:function(){
		return DBUtils.getStreamId();
	},
	setStreamId:function(streamId){
		DBUtils.setStreamId(streamId);
	},
	getIsEncrypt:function(){
		//shikuLog("===> getLogoutTime "+value);
		var isEncrypt=myData.isEncrypt;
		if(myFn.isNil(isEncrypt)){
			isEncrypt= DBUtils.getIsEncrypt();
			if(myFn.isNil(isEncrypt)){
				isEncrypt= 0;
				this.setIsEncrypt(isEncrypt);
			}
			myData.isEncrypt=isEncrypt;

		}
        WEBIM.setEncrypt(1 == isEncrypt);
		return isEncrypt;
	},
	setIsEncrypt:function(value){
		myData.isEncrypt=value;
		DBUtils.setIsEncrypt(value);
		shikuLog("===> setIsEncrypt "+value);
	},
	getGroupMsgFromUserName(msg,userId,defaultName){
		var member=Temp.members[userId];
		var friend =DataMap.friends[userId];
		if(!myFn.isNil(member)){
			if(!myFn.isNil(member.remarkName)){
				msg.fromUserName=member.remarkName;
			}else if(!myFn.isNil(friend)){
				if(!myFn.isNil(friend.remarkName)){
					msg.fromUserName=friend.remarkName;
				}else {
					msg.fromUserName=member.nickname;
				}
						
			}else{
				msg.fromUserName=member.nickname;
			}
		}else if(!myFn.isNil(friend)){
			if(!myFn.isNil(friend.remarkName))
				msg.fromUserName=friend.remarkName;
		}
		 if(myFn.isNil(msg.fromUserName)){
		 	if(!myFn.isNil(defaultName))
		 		msg.fromUserName=defaultName;
		 	else
				msg.fromUserName=userId;
		}
		
		return msg.fromUserName;
	},
	getLogoutTime:function(){
		//shikuLog("===> getLogoutTime "+value);
		return DBUtils.getLogoutTime();
	},
	setLogoutTime:function(time){
		DBUtils.setLogoutTime(time);
		shikuLog("===> setLogoutTime "+time);
	},
	/*获取好友聊天记录*/
	getMsgRecordListKey:function(userId){
		if(myData.userId==WEBIM.getUserIdFromJid(userId))
			return userId;
		else
			return WEBIM.getBareJid(userId);
	},
	getMsgRecordList:function(userId,isKey){
		if(1!=isKey)
			userId=this.getMsgRecordListKey(userId);
		var arrList=DataMap.msgRecordList[userId];
		if(myFn.isNil(arrList)){
			arrList=DBUtils.getMsgRecordList(userId);
			DataMap.msgRecordList[userId]=arrList;
		}
		return arrList;
	},
	setMsgRecordList:function(userId,arrList){
		userId=this.getMsgRecordListKey(userId);
		DataMap.msgRecordList[userId]=arrList;
		DBUtils.setMsgRecordList(userId,arrList);
	},
	putMsgRecordList:function(userId,msgId,inTop){
		userId=this.getMsgRecordListKey(userId);
		var arrList=DataUtils.getMsgRecordList(userId,1);
		var index=arrList.indexOf(msgId);
		if(index>-1)
			return;
		/*本地记录 最多只保存最新50条*/
		if(1==inTop){
			if(50>arrList.length){
				arrList.unshift(msgId);
				DataMap.msgRecordList[userId]=arrList;
				DBUtils.setMsgRecordList(userId,arrList);
			}
		}else{
			if(50>arrList.length){
				arrList.push(msgId);
			}else{
				arrList.splice(0, 1);
				arrList.push(msgId);
			}
			DataMap.msgRecordList[userId]=arrList;
			DBUtils.setMsgRecordList(userId,arrList);
		}
		
	},
	removeMsgRecordList:function(userId,msgId){
		userId=this.getMsgRecordListKey(userId);
		var arrList=DataMap.msgRecordList[userId];
		Utils.removeArrValue(userId,msgId);
		DataMap.msgRecordList[userId]=arrList;
		DBUtils.setMsgRecordList(userId,arrList);
	},
	/*清除 好友 或群组的 消息记录*/
	clearMsgRecordList:function(userId){
		userId=this.getMsgRecordListKey(userId);
		delete DataMap.msgRecordList[userId];
		DBUtils.clearMsgRecordList(userId);
	},
	//删除解散群组 或 删除好友 
	deleteFriend:function(jid){
		this.clearMsgRecordList(jid);
		this.removeUIMessageList(jid);
		DataMap.deleteFriends[jid]=jid;
	},
	getDeleteFirend:function(jid){
		return DataMap.deleteFriends[jid];
	},


	/*获取未读消息 总数*/
	getMsgNumCount:function(){
		var num=msgNumCount;
		if(myFn.isNil(num)||0==num){
			num=DBUtils.getMsgNumCount();
			msgNumCount=num;
		}
		return num;
	},
	/*更新消息总数*/
	setMsgMumCount:function(num){
		
		msgNumCount=num;
		DBUtils.setMsgMumCount(num);
		
	},
	getMsgNum:function(id){
		
		var num=DataMap.msgNum[id];
		if(myFn.isNil(num)){
			num=DBUtils.getMsgNum(id);
			DataMap.msgNum[id]=num;
		}
		return num;
	},
	setMsgNum:function(id,num){
		/*
		var num=DataMap.msgNum[id];

		if(myFn.isNil(num)){
			num=DBUtils.getItem(key);
			DataMap.msgNum[id]=num;
		}*/
		DataMap.msgNum[id]=num;
		DBUtils.setMsgNum(id,num);
		
	},
	/*同步服务器 最近聊天列表*/
	getLastChatList:function(callback){
		var startTime=myFn.isNil(DataUtils.getUIMessageList())?0:DataUtils.getLogoutTime();
		mySdk.getLastChatList(startTime,function(result){
			var msgObj=null;
			var name=null;
			/*if(myFn.isNil(result))
				callback();*/
			var jidList=[];
			var message=null;
			for (var i = 0; i < result.length; i++) {
				message=result[i];
				msgObj=DataUtils.getLastMsg(message.jid);
				/*
				本地最后一条聊天记录与服务器 记录不符
				*/
				/*if(message.messageId!=msgObj.messageId){
					DataUtils.clearMsgRecordList(msgObj.id);
				}*/
				msgObj.messageId=message.messageId;
				msgObj.type=message.type;
				
				msgObj.content=WEBIM.decryptMessage(message);
				if(1==message.isRoom){
					/*if(0==DataMap.myRooms.length)
						sleep(1000);*/
					var myRoom=DataMap.myRooms[msgObj.id];
					if(!myRoom)
						continue;
					var roomId=myRoom.id;
					 room=DataMap.rooms[roomId];
					 name=room.name;
					 var lastTime=0<msgObj.timeSend?msgObj.timeSend:DataUtils.getLogoutTime();
					 jidList.push(message.jid+","+lastTime);
				}else{
					var friend=DataMap.friends[msgObj.id];
					if(myFn.isNil(friend))
						continue;
					name=myFn.isNil(friend.remarkName)?friend.toNickname:friend.remarkName;
				}
				msgObj.lastTime=message.timeSend;
				msgObj.timeSend=message.timeSend;

				msgObj.name=name;
				DBUtils.putUIMessageList(msgObj);
				//DBUtils.removeMsgRecordList(myFn.getJid(msgObj.id));
			}
			callback();
		});
	},
	/*获取最近的消息列表记录*/
	getUIMessageList:function(){
		var messageList=DBUtils.getUIMessageList();
		return messageList;
	},
	putUIMessageList:function(msg,fromUserId,fromUserName){
		//var messageList=DBUtils.getUIMessageList();
		
		var obj=DataUtils.getLastMsg(fromUserId);
		if(fromUserId)
			obj.id=fromUserId;
		else
			obj.id=msg.fromUserId;
		if(fromUserName)
			obj.name=fromUserName;
		else
			obj.name=msg.fromUserName;
		if(msg.timeSend>obj.timeSend)
			obj.timeSend=msg.timeSend;
		obj.lastTime=0;
		obj.content=msg.content;
		obj.type=msg.type;
		obj.isReadDel=msg.isReadDel;
		DBUtils.putUIMessageList(obj);
	},
	putUIMessageObj:function(msgObj){
		DBUtils.putUIMessageList(msgObj);
	},
	removeUIMessageList:function(userId){
		DBUtils.removeUIMessageList(userId);
		
	},
	getLastMsg:function(userId){
		var msg=DBUtils.getLastMsg(userId);
		if(myFn.isNil(msg)){
			msg={};
			msg.id=userId;
			msg.timeSend=0;
		}else{
			msg.timeSend=Number(msg.timeSend);
		}
		return msg;
	},
	getMessage:function(messageId){
		var msg=DataMap.msgMap[messageId];
		if(myFn.isNil(msg)){
			msg=DBUtils.getMessage(messageId);
			DataMap.msgMap[messageId]=msg;
		}
		return msg;
	},
	saveMessage:function(msg,msgId){
		if(msgId){
			msg.messageId=msgId;
		}else if(myFn.isNil(msg.messageId))
			msg.messageId=msg.id;
		if(!msg.messageId)
			return;
		DataMap.msgMap[msg.messageId]=msg;
		DBUtils.saveMessage(msg);
	},
	deleteMessage:function(messageId){
		delete DataMap.msgMap[messageId];
		DBUtils.deleteMessage(messageId);
	},

	getMsgReadList:function(messageId){
		var readList=groupMsgReadList[messageId];
		if(myFn.isNil(readList)){
			readList=DBUtils.getMsgReadList(messageId);
			groupMsgReadList[messageId]=readList;
		}
		return readList;
	},
	/*添加已读列表 消息对象*/
	putMsgReadList:function(messageId,msg){
		
		var readList=groupMsgReadList[messageId];
		if(myFn.isNil(readList)){
			readList=DBUtils.getMsgReadList(messageId);
		}
		readList.push(msg);
		groupMsgReadList[messageId]=readList;
		DBUtils.putMsgReadList(messageId,msg);

	},
	/*获取消息已读数量*/
	getMsgReadNum:function (messageId) {
		var value= groupMsgReadNum[messageId];
		if(myFn.isNil(value)){
			value=DBUtils.getMsgReadNum(messageId);
			groupMsgReadNum[messageId]=value;
		}
		return value;
	},
	//更新消息已读数量
	setMsgReadNum:function (messageId,num) {
		//设置已读数量
		groupMsgReadNum[messageId]=num;

		DBUtils.setMsgReadNum(messageId,num);

	},
	/*获取消息过滤状态*/
	getMsgFilters:function(id){
		var status=GroupManager.filters[id];
		if(myFn.isNil(status)){
			status=DBUtils.getMsgFilters(id);
			GroupManager.filters[id]=status;
		}status="0"==status?false:true;
		return status;
	
	},
	setMsgFilters:function(id,status){
		status=false==status?0:1;
		GroupManager.filters[id]=status;
		DBUtils.setMsgFilters(id,status);
	},
	clearAll:function(){
		DBUtils.clearAll();
		ownAlert(3,"清除本地数据成功!");
	}
		
}



var emojiList = [
	{"filename":"e-01","english":"smile","chinese":"微笑"},
	{"filename":"e-02","english":"joy","chinese":"快乐"},
	{"filename":"e-03","english":"heart-eyes","chinese":"色咪咪"},
	{"filename":"e-04","english":"sweat_smile","chinese":"汗"},
	{"filename":"e-05","english":"laughing","chinese":"大笑"},
	{"filename":"e-06","english":"wink","chinese":"眨眼"},
	{"filename":"e-07","english":"yum","chinese":"百胜"},
	{"filename":"e-08","english":"relieved","chinese":"放松"},
	{"filename":"e-09","english":"fearful","chinese":"可怕"},
	{"filename":"e-10","english":"ohYeah","chinese":"欧耶"},
	{"filename":"e-11","english":"cold-sweat","chinese":"冷汗"},
	{"filename":"e-12","english":"scream","chinese":"尖叫"},
	{"filename":"e-13","english":"kissing_heart","chinese":"亲亲"},
	{"filename":"e-14","english":"smirk","chinese":"得意"},
	{"filename":"e-15","english":"angry","chinese":"害怕"},
	{"filename":"e-16","english":"sweat","chinese":"沮丧"},
	{"filename":"e-17","english":"stuck","chinese":"卡住"},
	{"filename":"e-18","english":"rage","chinese":"愤怒"},
	{"filename":"e-19","english":"etriumph","chinese":"生气"},
	{"filename":"e-20","english":"mask","chinese":"面具"},
	{"filename":"e-21","english":"confounded","chinese":"羞愧"},
	{"filename":"e-22","english":"sunglasses","chinese":"太阳镜"},
	{"filename":"e-23","english":"sob","chinese":"在"},
	{"filename":"e-24","english":"blush","chinese":"脸红"},
	{"filename":"e-26","english":"doubt","chinese":"疑惑"},
	{"filename":"e-27","english":"flushed","chinese":"激动"},
	{"filename":"e-28","english":"sleepy","chinese":"休息"},
	{"filename":"e-29","english":"sleeping","chinese":"睡着"},
	{"filename":"e-30","english":"disappointed_relieved","chinese":"失望"},
	{"filename":"e-31","english":"tire","chinese":"累"},
	{"filename":"e-32","english":"astonished","chinese":"惊讶"},
	{"filename":"e-33","english":"buttonNose","chinese":"抠鼻"},
	{"filename":"e-34","english":"frowning","chinese":"皱眉头"},
	{"filename":"e-35","english":"shutUp","chinese":"闭嘴"},
	{"filename":"e-36","english":"expressionless","chinese":"面无表情"},
	{"filename":"e-37","english":"confused","chinese":"困惑"},
	{"filename":"e-38","english":"tired_face","chinese":"厌倦"},
	{"filename":"e-39","english":"grin","chinese":"露齿而笑"},
	{"filename":"e-40","english":"unamused","chinese":"非娱乐"},
	{"filename":"e-41","english":"persevere","chinese":"坚持下去"},
	{"filename":"e-42","english":"relaxed","chinese":"傻笑"},
	{"filename":"e-43","english":"pensive","chinese":"沉思"},
	{"filename":"e-44","english":"no_mouth","chinese":"无嘴"},
	{"filename":"e-45","english":"worried","chinese":"担心"},
	{"filename":"e-46","english":"cry","chinese":"哭"},
	{"filename":"e-47","english":"pill","chinese":"药"},
	{"filename":"e-48","english":"celebrate","chinese":"庆祝"},
	{"filename":"e-49","english":"gift","chinese":"礼物"},
	{"filename":"e-50","english":"birthday","chinese":"生日 "},
	{"filename":"e-51","english":"paray","chinese":"祈祷"},
	{"filename":"e-52","english":"ok_hand","chinese":"好"},
	{"filename":"e-53","english":"first","chinese":"冠军"},
	{"filename":"e-54","english":"v","chinese":"耶"},
	{"filename":"e-55","english":"punch","chinese":"拳头"},
	{"filename":"e-56","english":"thumbsup","chinese":"赞"},
	{"filename":"e-57","english":"thumbsdown","chinese":"垃圾"},
	{"filename":"e-58","english":"muscle","chinese":"肌肉"},
	{"filename":"e-59","english":"maleficeent","chinese":"鼓励"},
	{"filename":"e-60","english":"broken_heart","chinese":"心碎"},
	{"filename":"e-61","english":"heart","chinese":"心 "},
	{"filename":"e-62","english":"taxi","chinese":"出租车"},
	{"filename":"e-63","english":"eyes","chinese":"眼睛"},
	{"filename":"e-64","english":"rose","chinese":"玫瑰"},
	{"filename":"e-65","english":"ghost","chinese":"鬼"},
	{"filename":"e-66","english":"lip","chinese":"嘴唇"},
	{"filename":"e-67","english":"fireworks","chinese":"烟花"},
	{"filename":"e-68","english":"balloon","chinese":"气球"},
	{"filename":"e-69","english":"clasphands","chinese":"握手"},
	{"filename":"e-70","english":"bye","chinese":"抱拳"}
];


var gifList = [
	{"filename":"eight.gif","english":"eight"},
	{"filename":"eighteen.gif","english":"eighteen"},
	{"filename":"eleven.gif","english":"eleven"},
	{"filename":"fifity.gif","english":"fifity"},
	{"filename":"fifity_four.gif","english":"fifity_four"},
	{"filename":"fifity_one.gif","english":"fifity_one"},
	{"filename":"fifity_three.gif","english":"fifity_three"},
	{"filename":"fifity_two.gif","english":"fifity_two"},
	{"filename":"fifteen.gif","english":"fifteen"},
	{"filename":"five.gif","english":"five"},
	{"filename":"forty.gif","english":"forty"},
	{"filename":"forty_eight.gif","english":"forty_eight"},
	{"filename":"forty_five.gif","english":"forty_five"},
	{"filename":"forty_four.gif","english":"forty_four"},
	{"filename":"forty_nine.gif","english":"forty_nine"},
	{"filename":"forty_one.gif","english":"forty_one"},
	{"filename":"forty_seven.gif","english":"forty_seven"},
	{"filename":"forty_three.gif","english":"forty_three"},
	{"filename":"forty_two.gif","english":"forty_two"},
	{"filename":"fourteen.gif","english":"fourteen"},
	{"filename":"nine.gif","english":"nine"},
	{"filename":"nineteen.gif","english":"nineteen"},
	{"filename":"one.gif","english":"one"},
	{"filename":"seven.gif","english":"seven"},
	{"filename":"seventeen.gif","english":"seventeen"},
	{"filename":"sixteen.gif","english":"sixteen"},
	{"filename":"ten.gif","english":"ten"},
	{"filename":"thirteen.gif","english":"thirteen"},
	{"filename":"thirty.gif","english":"thirty"},
	{"filename":"thirty_eight.gif","english":"thirty_eight"},
	{"filename":"thirty_five@.gif","english":"thirty_five@"},
	{"filename":"thirty_four.gif","english":"thirty_four"},
	{"filename":"thirty_nine.gif","english":"thirty_nine"},
	{"filename":"thirty_seven.gif","english":"thirty_seven"},
	{"filename":"thirty_six.gif","english":"thirty_six"},
	{"filename":"thirty_three.gif","english":"thirty_three"},
	{"filename":"thirty_two.gif","english":"thirty_two"},
	{"filename":"thirty-one.gif","english":"thirty-one"},
	{"filename":"three.gif","english":"three"},
	{"filename":"twelve.gif","english":"twelve"},
	{"filename":"twenty.gif","english":"twenty"},
	{"filename":"twenty_eight.gif","english":"twenty_eight"},
	{"filename":"twenty_five.gif","english":"twenty_five"},
	{"filename":"twenty_four.gif","english":"twenty_four"},
	{"filename":"twenty_nine.gif","english":"twenty_nine"},
	{"filename":"twenty_one.gif","english":"twenty_one"},
	{"filename":"twenty_seven.gif","english":"twenty_seven"},
	{"filename":"twenty_six.gif","english":"twenty_six"},
	{"filename":"twenty_three.gif","english":"twenty_three"},
	{"filename":"twenty_two.gif","english":"twenty_two"}
];