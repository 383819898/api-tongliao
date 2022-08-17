package com.shiku.xmpppush.vo;

import org.jivesoftware.smack.packet.Message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageVo {
	private long createTime;
	private Message message;
}
