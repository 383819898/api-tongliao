//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.xyz.mianshi.scheduleds;

import com.google.common.primitives.Ints;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Task implements Delayed, Runnable {
    private String id = "";
    private long start = 0L;

    public Task(String id, long delayInMilliseconds) {
        this.id = id;
        this.start = System.currentTimeMillis() + delayInMilliseconds;
    }

    public String getId() {
        return this.id;
    }

    public long getDelay(TimeUnit unit) {
        long diff = this.start - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed o) {
        return Ints.saturatedCast(this.start - ((Task)o).start);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (!(o instanceof Task)) {
            return false;
        } else {
            Task t = (Task)o;
            return this.id.equals(t.getId());
        }
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}
