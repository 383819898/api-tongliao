package cn.xyz.repository;

import cn.xyz.mianshi.vo.InviteCode;
import cn.xyz.mianshi.vo.Room;
import org.bson.types.ObjectId;

public interface RoomMemberRepository extends IMongoDAO<Room.Member,ObjectId> {




}
