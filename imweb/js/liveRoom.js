var LiveManager={
	showLiveRoomList:function(pageIndex){
		$("#orgShow").hide();
		$("#o").show();
		$("#tabCon_0").show();
		$("#tab").hide();
		$("#prop").hide();
		$("#setPassword").hide();
		$("#tabCon_0").hide();
		$("#tabCon_1").hide();
		$("#tabCon_2").hide();
		$("#tab").hide();
		$("#companyTab").hide();
		$("#roomTab").hide();
		$("#liveRoomTab").show();
		if(!pageIndex)
			pageIndex=0;
		$("#liveRoomTab").addClass("active");
		$("#liveRoomTab").addClass("in");
		$("#liveRoomTab").siblings().removeClass("active");
		UI.hideChatBodyAndDetails();
		mySdk.getLiveRoomList(pageIndex,10,function(result){
			var html = "";
			var roomId=null;
			for (var i = 0; i < result.length; i++) {
				if(result[i].status==1){// 只显示正在直播的直播间
					var obj = result[i];
					html += LiveManager.createMyItem(obj);
				}
				
			}
			if(html==""){
				html+="<div style='text-align:center;margin-top:60px;'><img src='img/noData.png' style='width:20px;margin-left:10px;margin-top:-2.5px'><span style='margin-left:5px; color:#716C6C;; font-size:16px;'>暂无数据</span></div>";
			}else{
				html += LiveManager.createPager(pageIndex, length, 'LiveManager.showLiveRoomList');
			}

			$("#liveRoomList").empty();
			$("#liveRoomList").append(html);
			$("#liveRoomList").show();
		});
	},
	joinRoom:function(liveUrl){
 		window.open("liveDemo/test.html?liveUrl="+liveUrl);
	},

	isChoose : function(groupId){ //群组列表选中状态切换
      $("#groups_"+groupId+"").addClass("fActive");
      $("#groups_"+groupId+"").siblings().removeClass("fActive");
	},

	createMyItem : function(obj) {
		var itemHtml = "<div id='groups_"+obj.jid+"' class='groupListChild'  onclick='LiveManager.isChoose(\"" +obj.jid + "\");'>"
				+          "<a href='liveDemo/test.html?liveUrl="+obj.url+"' target='_blank' style='cursor: pointer; margin-right: 10px;' class='pull-left'>"
				+               "<img onerror='this.src=\"img/ic_avatar.png\"' width='40' height='40' alt='' src='"+ (myFn.getAvatarUrl(obj.userId))+ "' class='roundAvatar'>"
				+          "</a> "
				+          "<div onclick='LiveManager.joinRoom(\""+ (obj.url)+ "\");' style='cursor: pointer;height:45px;display:inline' class='media-body'>"
				+               "<h5 class='media-heading groupName' style='text-overflow: ellipsis;white-space: nowrap;width: 200px;margin-top:10px'>"+ (myFn.isNil(obj.name) ? "&nbsp;" : obj.name)+"</h5><p style='float:right;margin-top:-18px;color:red'>"+((obj.status==0)?"已停止":"正在直播")+"<p>"
						
				+          "</div>"
				+      "</div>";

		return itemHtml;
	},
	createItem : function(obj) {
		var itemHtml = "<div id='groups_"+obj.jid+"' class='groupListChild' style='border-bottom:1px solid #eeeeee;' onclick='GroupManager.isChoose(\"" + obj.jid + "\");'>"
				+          "<a href='liveDemo/test.html?liveUrl="+obj.url+"' target='_blank' style='cursor: pointer; margin-right: 10px;' class='pull-left'>"
				+               "<img onerror='this.src=\"img/ic_avatar.png\"' width='40' height='40' alt='' src='"+ (myFn.getAvatarUrl(obj.userId))+ "' class='roundAvatar'>"
				+          "</a> ";
				
					itemHtml=itemHtml+"<div onclick='LiveManager.joinRoom(\""+ (obj.url)+ "\");' style='cursor: pointer;' class='media-body'>";
				

				itemHtml=itemHtml+ "<h5 class='media-heading' style='overflow: hidden;text-overflow: ellipsis;white-space: nowrap;max-width: 200px'>"+ (myFn.isNil(obj.name) ? "&nbsp;" : obj.name)+"</h5>"
				+               "<div style='color:#b0acac;'>"+ (myFn.isNil(obj.notice) ? "无" : obj.notice)+"</div>";

			
				itemHtml=itemHtml+"<a href='javascript:LiveManager.joinRoom(\"" + obj.url +"\");' style='float:right;z-index:5;margin-top:-25px;'>查看直播</a>";
				
				itemHtml=itemHtml+          "</div>"
				+      "</div>";

		return itemHtml;
	},
	createPager : function(pageIndex, totalPage, fnName) {

		var pagerHtml = "<div class='pageTurning'>";  
		if (pageIndex == 0) {
			pagerHtml += "<img style='width:21px;'  src='img/on1.png'>"
			            
		} else {
			pagerHtml += "<a href='javascript:" + fnName + "(" + (pageIndex - 1) + ")" + "'>"
			             +"<img style='width:21px;'  src='img/on.png'>"
			             +"</a>";
		}
		pagerHtml += "<div class='pageIndex'>" + (pageIndex + 1) + "</div>";
		if ((pageIndex+1) >= totalPage) {
			pagerHtml += "<img style='width:21px;'  src='img/next1.png'>";
		} else {
			pagerHtml += "<a href='javascript:" + fnName + "(" + (pageIndex + 1) + ")" + "'> <img style='width:21px;' src='img/next.png'> </a>";
		}
		return pagerHtml;
	}
};