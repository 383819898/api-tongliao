//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.xyz.mianshi.scheduleds;

import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CancelControlTask extends Task {
    private final Logger logger = LoggerFactory.getLogger(CancelControlTask.class);
    private int userId = -1;
    private static long defaultTime = 300000L;

    public CancelControlTask(Integer userId, long delayInMilliseconds) {
        super("CancelControlTask-" + userId, delayInMilliseconds);
        this.userId = userId;
    }

    public CancelControlTask(Integer userId) {
        super("CancelControlTask-" + userId, defaultTime);
        this.userId = userId;
    }

    public void run() {
        this.logger.info("系统恢复用户:{}", this.userId);
        User user = (User)SKBeanUtils.getUserManager().get(this.userId);
        if (user != null) {
            SKBeanUtils.getUserManager().resetControl(this.userId);
        }
    }
}
