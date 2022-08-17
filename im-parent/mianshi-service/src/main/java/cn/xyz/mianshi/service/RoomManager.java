package cn.xyz.mianshi.service;

import java.util.List;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.Room.Member;
import cn.xyz.mianshi.vo.Room.Share;
import cn.xyz.mianshi.vo.User;
import org.springframework.web.bind.annotation.RequestParam;

public interface RoomManager {
	public static final String BEAN_ID = "RoomManagerImpl";

	Room add(User user, Room room, List<Integer> memberUserIdList, JSONObject userKeys);

	void delete(ObjectId roomId, Integer userId);


	Room get(ObjectId roomId, Integer pageIndex, Integer pageSize);
	 
	Room exisname(Object roomname, ObjectId roomId);

	List<Room> selectList(int pageIndex, int pageSize, String roomName);

	Object selectHistoryList(int userId, int type);

	Object selectHistoryList(int userId, int type, int pageIndex, int pageSize);

	void deleteMember(User user, ObjectId roomId, int userId);

	void updateMember(User user, ObjectId roomId, Member member);

	void updateMember(User user, ObjectId roomId, List<Integer> idList, JSONObject userKeys);
	
	void Memberset(Integer offlineNoPushMsg, ObjectId roomId, int userId, int type);

	Member getMember(ObjectId roomId, int userId);

	List<Member> getMemberList(ObjectId roomId, String keyword);

	void join(int userId, ObjectId roomId, int type);
	void joinInvite(String roomId, int userId, String text);

	void setAdmin(ObjectId roomId, int touserId, int type, int userId);

	Share Addshare(ObjectId roomId, long size, int type, int userId, String url, String name);
	
	List<Share> findShare(ObjectId roomId, long time, int userId, int pageIndex, int pageSize);
	
	Object getShare(ObjectId roomId, ObjectId shareId);
	
	void deleteShare(ObjectId roomId, ObjectId shareId, int userId);
	
	String getCall(ObjectId roomId);
	
	String getVideoMeetingNo(ObjectId roomId);

	Long countRoomNum();
}
