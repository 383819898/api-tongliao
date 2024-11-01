package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.utils.SKBeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * 音乐模块接口
 *
 * @author Administrator
 *
 */

@Api(value="MusicController",tags="音乐模块接口")
@RestController
@RequestMapping(value="/music",method={RequestMethod.GET,RequestMethod.POST})
public class MusicController extends AbstractController {

	@ApiOperation("查询音乐列表")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="pageSize" , value="数据数量，默认10",dataType="int",defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="pageIndex" , value="当前页",dataType="int",defaultValue = "20"),
		@ApiImplicitParam(paramType="query" , name="keyword" , value="关键字",dataType="String",defaultValue = "")
	})
	@PostMapping(value = "/list")
	public JSONMessage queryMusicList(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "20") Integer pageSize,@RequestParam(defaultValue = "") String keyword) {
		Object data = SKBeanUtils.getLocalSpringBeanManager().getMusicManager()
				.queryMusicInfo(pageIndex, pageSize, keyword);
		return JSONMessage.success(data);
	}

}
