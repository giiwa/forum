package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Bean.V;
import org.giiwa.framework.bean.User;

import com.mongodb.BasicDBObject;

public class UserHelper {

  public static void count(long uid) {
    /**
     * count top topic
     */
    long count = Bean.count(new BasicDBObject("owner", uid).append("top", 1), Topic.class);
    V v = V.create("tops", count);

    /**
     * count topics
     */
    count = Bean.count(new BasicDBObject("owner", uid), Topic.class);
    v.set("topics", count);

    /**
     * count replies
     */
    long c1 = Bean.count(new BasicDBObject("owner", uid).append("parent", "root"), Topic.class);
    v.set("replies", count - c1);

    User.update(uid, v);
  }

}
