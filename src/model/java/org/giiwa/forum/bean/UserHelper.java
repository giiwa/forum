package org.giiwa.forum.bean;

import org.giiwa.core.bean.Helper;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.framework.bean.User;

public class UserHelper {

  public static void count(long uid) {
    /**
     * count top topic
     */
    long count = Helper.count(W.create("owner", uid).and("top", 1), Topic.class);
    V v = V.create("tops", count);

    /**
     * count topics
     */
    count = Helper.count(W.create("owner", uid), Topic.class);
    v.set("topics", count);

    /**
     * count replies
     */
    long c1 = Helper.count(W.create("owner", uid).and("parent", 0), Topic.class);
    v.set("replies", count - c1);

    User.update(uid, v);
  }

}
