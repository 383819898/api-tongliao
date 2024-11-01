package cn.xyz.mianshi.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("地址")
public class BasePoi {

	@ApiModelProperty("地址")
	private String address;// 地址
	@ApiModelProperty("距离")
	private int distance;// 距离
	@JSONField(serialize = false)
	@ApiModelProperty("纬度")
	private double latitude;// 纬度
	@JSONField(serialize = false)
	@ApiModelProperty("经度")
	private double longitude;// 经度
	@JSONField(serialize = false)
	@ApiModelProperty("当前页")
	private int pageIndex=0;
	@ApiModelProperty("当前页大小")
	@JSONField(serialize = false)
	private int pageSize=10;
	@ApiModelProperty("索引")
	@JSONField(serialize = false)
	private int poiId;// 索引
	@ApiModelProperty("标签")
	private String tags;// 标签
	@ApiModelProperty("名称")
	private String title;// 名称

	public String getAddress() {
		return address;
	}

	public int getDistance() {
		return distance;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPoiId() {
		return poiId;
	}

	public String getTags() {
		return tags;
	}

	public String getTitle() {
		return title;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPoiId(int poiId) {
		this.poiId = poiId;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
