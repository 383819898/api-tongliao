var mpCommon = {

	/*imServerAddr:"192.168.0.227:5260",
	fileAddr:"http://47.91.232.3:8089/",
	uploadAddr:"http://47.91.232.3:8080/upload/UploadifyServlet",*/
	apiAddr:"",
	imServerAddr:"",
	imDomian:"",
	fileAddr:"",
	uploadAddr:"",
	apiKey:getLoginData().apiKey,
	keepalive:15,
	timeDelay:0,

	invoke : function(obj) {

		jQuery.support.cors = true;
		mpCommon.createCommApiSecret(obj.data);
		var params = {
			type : (mpCommon.isNil(obj.type)?"POST":"GET"),
			url : obj.url,
			data : obj.data,
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			dataType : 'JSON',
			async:false,
			success : function(result) {

				if(1030101==result.resultCode){//缺少访问令牌
					location.replace("/mp/login.html");
					return;
				}else if(1030102==result.resultCode){ //访问令牌无效或过期
					location.replace("/mp/login.html");
					return;
			 	}

			 	obj.success(result);

			},
			error : function(result) {
				if(obj.error)
					obj.error(result);
			},
			complete : function() {
			}
		};
		
		params.data["access_token"] = getLoginData().access_Token;
		$.ajax(params);
	},
	//这个方法是为了数组到服务器----zhm
	invokeByArray : function(obj) {

		jQuery.support.cors = true;
		mpCommon.createCommApiSecret(obj.data);
		var params = {
			type : "POST",
			url : obj.url,
			data : obj.data,
			contentType : 'application/x-www-form-urlencoded; charset=UTF-8',
			dataType : 'JSON',
			async:false,
			traditional: true,
			success : function(result) {

				if(1030101==result.resultCode){//缺少访问令牌
					location.replace("/mp/login.html");
					return;
				}else if(1030102==result.resultCode){ //访问令牌无效或过期
					location.replace("/mp/login.html");
					return;
				}

				obj.success(result);

			},
			error : function(result) {
				if(obj.error)
					obj.error(result);
			},
			complete : function() {
			}
		};

		params.data["access_token"] = getLoginData().access_Token;
		$.ajax(params);
	},
	// 公众号获取头像
	getAvatarUrl : function(userId,update) {
		if(10000==userId)
			return "./images/im_10000.png";
		var imgUrl = this.fileAddr+"avatar/o/"+ (parseInt(userId) % 10000) + "/" + userId + ".jpg";
		if(1==update)
			imgUrl+="?x="+Math.random()*10;
		return imgUrl;
	},
	//创建 密钥
	createCommApiSecret : function (obj){
		obj.time=mpCommon.getServerTimeSecond();
		var key = getLoginData().apiKey+obj.time+getLoginData().userId
			+getLoginData().access_Token;
		obj.secret=$.md5(key);
		return obj;
	},
	getServerTimeSecond:function(){
    	return Math.round((this.getMilliSeconds()-mpCommon.timeDelay)/1000);
    },
	getMilliSeconds:function(){
		return Math.round(new Date().getTime());
    },
   	parseFileSize : function(value){
	    if(null==value||value==''){
	        return "0 B";
	    }
	    var unitArr = new Array("B","KB","MB","GB");
	    var index=0;
	    var srcsize = parseFloat(value);
	    index=Math.floor(Math.log(srcsize)/Math.log(1024));
	    var size =srcsize/Math.pow(1024,index);
	    size=size.toFixed(2);//保留的小数位数
	    return size+unitArr[index];
	},
	isContains: function(str, substr) {
    	return str.indexOf(substr) >= 0;
	},
	isNil : function(s) {
		return undefined == s || null == s || $.trim(s) == "" || $.trim(s) == "null" || JSON.stringify(s) == JSON.stringify({});
	},
	notNull : function(s) {
		return undefined != s && null != s && $.trim(s) != "" && $.trim(s) != "null";
	},
	strToJson : function(str) {
		return eval("(" + str + ")");
	},
	setCookie:function(key,value){
		$.cookie(key,JSON.stringify(value));
	},
	getCookie:function(key){
		var value=$.cookie(key);
		return this.strToJson(value);
	},
	removeCookie:function(key){
		return $.removeCookie(key);
	},
	randomUUID : function() {
		var chars = myData.charArray, uuid = new Array(36), rnd = 0, r;
		for (var i = 0; i < 36; i++) {
			if (i == 8 || i == 13 || i == 18 || i == 23) {
				uuid[i] = '-';
			} else if (i == 14) {
				uuid[i] = '4';
			} else {
				if (rnd <= 0x02)
					rnd = 0x2000000 + (Math.random() * 0x1000000) | 0;
				r = rnd & 0xf;
				rnd = rnd >> 4;
				uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
			}
		}
		return uuid.join('').replace(/-/gm, '').toLowerCase();
	},
	regText :"(\\s|\\n|<br>|^)(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?(&|&amp;)//=]*)",

	hrefEncode:function  (e) {
      var a = e.match(/&lt;a href=(?:'|").*?(?:'|").*?&gt;.*?&lt;\/a&gt;/g);
      if (a) {
          for (var n, i, o = 0, r = a.length; o < r; ++o)
             n = /&lt;a href=(?:'|")(.*?)(?:'|").*?&gt;.*?&lt;\/a&gt;/.exec(a[o]),
              n && n[1] && (i = n[1],this.isUrl(i) && (e = e.replace(n[0], this.htmlDecode(n[0])).replace(n[1], n[1])));
          return e
      }
      return e.replace(new RegExp(this.regText, "ig"), function () {
          return '<a target="_blank" href="' + arguments[0].replace(/^(\s|\n)/, "") + '">' + arguments[0] + "</a> "
      })
   },
   isUrl:function(e) {
      return new RegExp(this.regText, "i").test(e)
   },
   htmlDecode:function (e){
     return e && 0 != e.length ? e.replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&#39;/g, "'").replace(/&quot;/g, '"').replace(/&amp;/g, "&") : ""
   },
   /* 初始化配置 */
   initConfig:function(){
		mpHttpApi.getConfig(function(result){
			console.log("====> initConfig > "+JSON.stringify(result));
			if(mpCommon.isNil(result))
				return;
			if(result.apiAddr.endWith("/")){
				result.apiAddr+="#";
				result.apiAddr=result.apiAddr.replace("/#","");
			}
			mpCommon.imServerAddr = "http://"+result.imServerAddr+":5280";
			mpCommon.imDomian = result.imDomian;
			mpCommon.apiAddr = result.apiAddr;
			mpCommon.fileAddr = result.fileAddr;
			mpCommon.uploadAddr = result.uploadAddr+"upload/UploadifyServlet";
			
		});

		if(mpCommon.isNil(mpCommon.imServerAddr)){
			
			setTimeout(function(){
				if(mpCommon.isNil(mpCommon.imServerAddr)){
					mpCommon.initConfig();
				}else{
					return;
				}
					
			},2000);
		}

	},
	
};





function getLoginData() {
		var loginData = JSON.parse(localStorage.getItem('loginData'));
		if(loginData==undefined || loginData == null){
			location.replace("/mp/login.html");
			return;
		}
		return loginData;
};


/**
 * 
 * @param type //type : 1 成功 2:失败 3：提示 4:询问
 * @param infoText
 * @param okCallback
 * @returns
 */
function ownAlert(type,infoText,okCallback){  //自定义的弹框  

	if(type==1)
		layer.msg(infoText, {icon: 1});

	if(type==2)
		layer.msg(infoText, {icon: 5});
	if(type==3)
		layui.layer.open({
		  title: false,
		  closeBtn: 0,
		  btnAlign: 'c',
		  skin: 'my-skin',
		  content: '<div style="text-align:center;">'+infoText+'</div>' 
		});
	if(type==4)

		layui.layer.confirm(
				'<div style="text-align:center;">'+infoText+'</div>', 
				{icon: 3, title:false, closeBtn: 0,btnAlign: 'c',skin: 'my-skin'}, 
				function(index){
				   if(okCallback)	
						 okCallback();
                   layui.layer.close(index);

			    }
		);

};





String.prototype.replaceAll  = function(s1,s2){   

    return this.replace(new RegExp(s1,"gm"),s2);   
};

String.prototype.endWith = function(str){
   if(str==null || str=="" || this.length == 0 ||str.length > this.length){ 
       return false;
   }
  if(str==this.substring(this.length - str.length)){
     return true;
   }else{
     return false;
   }
};

//时间转换
Date.prototype.format = function(fmt) { 
     var o = { 
        "M+" : this.getMonth()+1,                 //月份 
        "d+" : this.getDate(),                    //日 
        "h+" : this.getHours(),                   //小时 
        "m+" : this.getMinutes(),                 //分 
        "s+" : this.getSeconds(),                 //秒 
        "q+" : Math.floor((this.getMonth()+3)/3), //季度 
        "S"  : this.getMilliseconds()             //毫秒 
    }; 
    if(/(y+)/.test(fmt)) {
            fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
    }
     for(var k in o) {
        if(new RegExp("("+ k +")").test(fmt)){
             fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
         }
     }
    return fmt; 
};
