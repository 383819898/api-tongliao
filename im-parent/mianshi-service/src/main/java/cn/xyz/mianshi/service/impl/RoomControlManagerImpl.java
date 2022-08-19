//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.xyz.mianshi.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.mianshi.model.RoomControlNewVO;
import cn.xyz.mianshi.utils.ConvertUtils;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.RoomControl;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.mongo.MongoRepository;
import cn.xyz.service.RedisServiceImpl;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

@Service
public class RoomControlManagerImpl extends MongoRepository<RoomControl, ObjectId> {
    public RoomControlManagerImpl() {
    }

    public Datastore getDatastore() {
        return SKBeanUtils.getDatastore();
    }

    public Class<RoomControl> getEntityClass() {
        return RoomControl.class;
    }

    private static RedisServiceImpl getRedisServiceImpl() {
        return SKBeanUtils.getRedisService();
    }

    public RoomControlNewVO getRoomControlByRoomId(ObjectId roomId) {
        if (ObjectUtil.isNull(roomId)) {
            return new RoomControlNewVO();
        } else {
            RoomControl roomControlRedis = getRedisServiceImpl().queryRoomControlSet(Convert.toStr(roomId));
            RoomControl roomControl;
            if (ObjectUtil.isNull(roomControlRedis)) {
                Query<RoomControl> query = this.getDatastore().createQuery(RoomControl.class);
                query.field("roomId").equal(roomId);
                roomControl = (RoomControl)query.get();
                if (ObjectUtil.isNull(roomControl)) {
                    return new RoomControlNewVO();
                }

                getRedisServiceImpl().saveRoomControlSet(roomControl);
            } else {
                roomControl = roomControlRedis;
            }

            RoomControlNewVO roomControlNewVO = (RoomControlNewVO)ConvertUtils.sourceToTarget(roomControl, RoomControlNewVO.class);
            roomControlNewVO.setId(Convert.toStr(roomControl.getId()));
            roomControlNewVO.setRoomId(Convert.toStr(roomControl.getRoomId()));
            return roomControlNewVO;
        }
    }

    public void saveOrUpdate(RoomControlNewVO roomControlVO) throws ServiceException {
        RoomControl roomControl = (RoomControl)ConvertUtils.sourceToTarget(roomControlVO, RoomControl.class);
        ObjectId roomId = new ObjectId(roomControlVO.getRoomId());
        if (!ObjectUtil.isNull(roomId) && !StrUtil.isBlank(Convert.toStr(roomId))) {
            User user = SKBeanUtils.getUserManager().getUser(ReqUtil.getUserId());
            long currentTime = DateUtil.currentTimeSeconds();
            roomControl.setRoomId(roomId);
            if (StrUtil.isBlank(roomControlVO.getId())) {
                roomControl.setCreateTime(currentTime);
                roomControl.setUserId(user.getUserId());
                roomControl.setNickname(user.getNickname());
                this.save(roomControl);
            } else {
                ObjectId roomControlId = new ObjectId(roomControlVO.getId());
                roomControl.setId(roomControlId);
                roomControl.setModifier(user.getUserId());
                roomControl.setNickname(user.getNickname());
                roomControl.setModifyTime(currentTime);
                this.update(roomControlId, roomControl);
            }

            this.modifyOrSaveRoomControllerSetRedis(roomControl);
        } else {
            throw new ServiceException("群id不能为空");
        }
    }

    private void modifyOrSaveRoomControllerSetRedis(RoomControl roomControl) {
        if (ObjectUtil.isNotNull(roomControl)) {
            getRedisServiceImpl().deleteRoomControlSet(Convert.toStr(roomControl.getRoomId()));
            getRedisServiceImpl().saveRoomControlSet(roomControl);
        }

    }
}
