/**
调用http 接口的相关函数
**/
var mpHttpApi = {
	
	getConfig:function(callback){
			mpCommon.invoke({
				type:"GET",
				url : '/mp/config',
				data : {},
				async:false,
				success : function(result) {
					if (1 == result.resultCode) {
						callback(result.data);
					} 
				},
				error : function(result) {
					if(1030102==result.resultCode){
						window.location.href = "login.html";
					}
				}
			});
	},
	getCurrentTime:function(callback){
		mpCommon.invoke({
			url : mpCommon.apiAddr+'/getCurrentTime',
			data : {},
			success : function(result) {
				if (1 == result.resultCode) {
					if(callback)
						callback(result.data);
				} 				mpCommon.timeDelay=mpCommon.getMilliSeconds()-result.currentTime;
				console.log("timeDelay   ====> "+mpCommon.timeDelay);
			}
		});
	},
	getMessageForServer:function(msgId,chatType){
		
		mpCommon.invoke({
			    url : '/mp/getMessage',
				data:{
					messageId:msgId,
					type:chatType
				},
				success:function(result){
					if(1==result.resultCode){
						return result.data;
					}
				}			
		});	
	},
	updateHomeCount : function(){
		mpCommon.invoke({
			url : '/mp/getHomeCount',
			data : {},
			success : function(result) {
				if (result.resultCode == 1) {
					//$("#msgCount").html(result.data.msgCount);
					$("#fansCount").html(result.data.fansCount);
					//$("#userCount").html(result.data.userCount);
				} 
			},
			error : function(result) {
				console.log("update msgCount error ====> ");
			}
		});
	},
	deleteFile:function(url,callback){
		//删除文件服务器文件
		var data=WEBIM.createOpenApiSecret(); 
		data.paths=url;
		$.ajax({
			type:'POST',
			url: mpCommon.uploadAddr.substr(0,mpCommon.uploadAddr.lastIndexOf('/'))+"/deleteFileServlet",
			data:data,
			success:function(result){
				callback(result);
			},
			error : function(result) {
				//ownAlert(2,result);
			}
		});	
		
	},
	logout:function(){
		mpCommon.invoke({
			url : '/mp/logout',
			data : {},
			success : function(result) {
				if (1 == result.resultCode) {
					localStorage.clear();
					location.replace("/mp/login.html");
				}
			}
		});
	},



};