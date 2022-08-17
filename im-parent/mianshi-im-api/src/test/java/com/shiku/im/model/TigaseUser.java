package com.shiku.im.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value="tig_users",noClassnameStored=true)
public class TigaseUser {

	@Id
	private byte[] _id;
	private String user_id;
	private String domain;
	private String password;
	private String type;
	public byte[] get_id() {
		return _id;
	}
	public void set_id(byte[] _id) {
		this._id = _id;
	}

	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}



}
