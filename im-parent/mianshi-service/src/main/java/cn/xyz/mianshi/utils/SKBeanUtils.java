package cn.xyz.mianshi.utils;

import cn.xyz.mianshi.service.impl.*;
import cn.xyz.repository.mongo.*;
import cn.xyz.service.TaskService;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import cn.xyz.commons.autoconfigure.KApplicationProperties.SmsConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import cn.xyz.commons.support.jedis.RedisCRUD;
import cn.xyz.mianshi.lable.LabelManagerImpl;
import cn.xyz.mianshi.lable.UserLabelManagerImpl;
import cn.xyz.mianshi.lable.UserLabelRepositoryImpl;
import cn.xyz.mianshi.opensdk.OpenAccountManageImpl;
import cn.xyz.mianshi.opensdk.OpenAppManageImpl;
import cn.xyz.mianshi.opensdk.OpenCheckLogManageImpl;
import cn.xyz.mianshi.opensdk.OpenWebManageImpl;
import cn.xyz.mianshi.service.LocalSpringBeanManager;
import cn.xyz.mianshi.vo.Config;
import cn.xyz.service.KSMSServiceImpl;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.RedisServiceImpl;

/**
* @Description: TODO(单例类获取   工具类)
* @author lidaye
* @date 2018年7月21日
*/

@Component
public class SKBeanUtils implements ApplicationContextAware {

    private static ApplicationContext ctx;

    private static LocalSpringBeanManager localSpringBeanManager;

  	public static LocalSpringBeanManager getLocalSpringBeanManager() {
  		return localSpringBeanManager;
  	}
    @Override
    public void setApplicationContext(ApplicationContext arg0)throws BeansException {
        ctx = arg0;
        localSpringBeanManager=ctx.getBean(LocalSpringBeanManager.class);

        System.out.println("SKBeanUtils ===> init end  ===> localSpringBeanManager > "+localSpringBeanManager.getClass().getSimpleName());
    }

    public static Object getBean(String beanName) {
        if(ctx == null){
            throw new NullPointerException();
        }
        return ctx.getBean(beanName);
    }



	@Autowired
	private RoomControlManagerImpl roomControlManager;
	public static Datastore getDatastore() {
		return getLocalSpringBeanManager().getDatastore();
	}

	public static Datastore getTigaseDatastore() {
		return getLocalSpringBeanManager().getTigaseDatastore();
	}


	public static Datastore getImRoomDatastore() {
		return getLocalSpringBeanManager().getImRoomDatastore();
	}

	public static Config getSystemConfig() {
		return getAdminManager().getConfig();
	}

	public static KXMPPServiceImpl getXmppService(){
		return getLocalSpringBeanManager().getXmppService();
	}

	public static XMPPConfig getXMPPConfig(){
		return getLocalSpringBeanManager().getXMPPConfig();
	}

	public static SmsConfig getSmsConfig(){
		return getLocalSpringBeanManager().getSmsConfig();
	}

	public static AdminManagerImpl getAdminManager() {
		return getLocalSpringBeanManager().getAdminManager();
	}

	public static UserManagerImpl getUserManager() {
		return getLocalSpringBeanManager().getUserManager();
	}

	public static AiNongUserManagerImpl getAiNongUserManager() {
		return getLocalSpringBeanManager().getAiNongUserManager();
	}


	public static FriendsManagerImpl getFriendsManager() {
		return getLocalSpringBeanManager().getFriendsManager();
	}
	public static  FriendGroupManagerImpl getFriendGroupManager() {
		return getLocalSpringBeanManager().getFriendGroupManager();
	}

	public static RoomManagerImplForIM getRoomManager() {
		return getLocalSpringBeanManager().getRoomManager();
	}

	public static RedisCRUD getRedisCRUD() {
		return getLocalSpringBeanManager().getRedisCRUD();
	}

	public static KSMSServiceImpl getSMSService() {
		return getLocalSpringBeanManager().getSMSService();
	}
	public static RedisServiceImpl getRedisService() {
		return getLocalSpringBeanManager().getRedisService();
	}

	public static TigaseManagerImpl getTigaseManager() {
		return getLocalSpringBeanManager().getTigaseManager();
	}

	public static AddressBookManagerImpl getAddressBookManger() {
		return getLocalSpringBeanManager().getAddressBookManger();
	}

	public static CompanyManagerImpl getCompanyManager() {
		return getLocalSpringBeanManager().getCompanyManager();
	}

	public static ConsumeRecordManagerImpl getConsumeRecordManager() {
		return getLocalSpringBeanManager().getConsumeRecordManager();
	}

	public static CustomerManagerImpl getCustomerManager() {
		return getLocalSpringBeanManager().getCustomerManager();
	}

	public static UserLabelManagerImpl getUserLabelManager() {
		return getLocalSpringBeanManager().getUserLabelManager();
	}

	public static LabelManagerImpl getLabelManager() {
		return getLocalSpringBeanManager().getLabelManager();
	}

	public static LiveRoomManagerImpl getLiveRoomManager() {
		return getLocalSpringBeanManager().getLiveRoomManager();
	}

	public static MsgCommentRepositoryImpl getMsgCommentRepository() {
		return getLocalSpringBeanManager().getMsgCommentRepository();
	}

	public static MsgRepositoryImpl getMsgRepository() {
		return getLocalSpringBeanManager().getMsgRepository();
	}

	public static MsgGiftRepositoryImpl getMsgGiftRepository() {
		return getLocalSpringBeanManager().getMsgGiftRepository();
	}

	public static MsgPraiseRepositoryImpl getMsgPraiseRepository() {
		return getLocalSpringBeanManager().getMsgPraiseRepository();
	}

	public static MsgForwardAmountRepositoryImpl getMsgForwardAmountRepository(){
		return getLocalSpringBeanManager().getMsgForwardAmountRepository();
	}

	public static MsgPlayAmountRepositoryImpl getMsgPlayAmountRepository(){
		return getLocalSpringBeanManager().getMsgPlayAmountRepository();
	}

	public static MsgListRepositoryImpl getMsgListRepository() {
		return getLocalSpringBeanManager().getMsgListRepository();
	}

	public static RedPacketManagerImpl getRedPacketManager() {
		return getLocalSpringBeanManager().getRedPacketManager();
	}

	public static SkTransferManagerImpl getSkTransferManager(){
		return getLocalSpringBeanManager().getSkTransferManagerImpl();
	}

	public static RoomManagerImplForIM getRoomManagerImplForIM() {
		return getLocalSpringBeanManager().getRoomManager();
	}

	public static FriendsRepositoryImpl getFriendsRepository() {
		return getLocalSpringBeanManager().getFriendsRepository();
	}

	public static UserLabelRepositoryImpl getUserLabelRepository() {
		return getLocalSpringBeanManager().getUserLabelRepository();
	}


	public static AdminRepositoryImpl getAdminRepository() {
		return getLocalSpringBeanManager().getAdminRepository();
	}

	public static UserRepositoryImpl getUserRepository() {
		return getLocalSpringBeanManager().getUserRepository();
	}

	public static AiNongUserRepositoryImpl getAiNongUserRepository() {
		return getLocalSpringBeanManager().getAiNongUserRepository();
	}

	public static EmployeeRepositoryImpl getEmployeeRepository() {
		return getLocalSpringBeanManager().getEmployeeRepository();
	}


	public static DepartmentRepositoryImpl getDepartmentRepository() {
		return getLocalSpringBeanManager().getDepartmentRepository();
	}

	public static CompanyRepositoryImpl getCompanyRepository() {
		return getLocalSpringBeanManager().getCompanyRepository();
	}

	public static CustomerRepositoryImpl getCustomerRepository() {
		return getLocalSpringBeanManager().getCustomerRepository();
	}

	public static ErrorMessageManageImpl getErrorMessageManage() {
		return getLocalSpringBeanManager().getErrorMessageManage();
	}



	public static ReportManagerImpl getReportManager() {
		return getLocalSpringBeanManager().getReportManager();
	}



	public static OpenAccountManageImpl getOpenAccountManage(){
		return getLocalSpringBeanManager().getOpenAccountManage();
	}

	public static OpenAppManageImpl getOpenAppManage(){
		return getLocalSpringBeanManager().getOpenAppManage();
	}

	public static OpenCheckLogManageImpl getOpenCheckLogManage(){
		return getLocalSpringBeanManager().getOpenCheckLogManage();
	}

	public static OpenWebManageImpl getOpenWebAppManage(){
		return getLocalSpringBeanManager().getOpenWebAppManage();
	}

	public static RoleManagerImpl getRoleManager(){
		return getLocalSpringBeanManager().getRoleManager();
	}

	public static PressureTestManagerImpl getPressureTestManager(){
		return getLocalSpringBeanManager().getPressureTestManager();
	}

	public static MsgInferceptManagerImpl getMsgInferceptManager(){
		return getLocalSpringBeanManager().getMsgInferceptManager();
	}

	public static RoomControlManagerImpl getRoomControlManager() {
		return getLocalSpringBeanManager().getRoomControlManager();
	}
	/*public static PressureTest getPressureTestManager(){
		return getLocalSpringBeanManager().getPressureTestManager();
	}*/

	public static TaskService getTaskManager() {
		return getLocalSpringBeanManager().getTaskManager();
	}

}

