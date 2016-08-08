package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.Table;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;
import org.giiwa.core.task.Task;
import org.giiwa.framework.bean.User;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

/**
 * Demo bean
 * 
 * @author joe
 * 
 */
@Table(name = "gi_circling")
public class Circling extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public long getId() {
    return this.getLong(X.ID);
  }

  public String getName() {
    return this.getString("name");
  }

  public String getMemo() {
    return this.getString("memo");
  }

  public String getUser_state() {
    return this.getString("user_state");
  }

  public String getAccess() {
    return this.getString("access");
  }

  public long getOwner() {
    return this.getLong("owner");
  }

  public User getOwner_obj() {
    User u = (User) this.get("owner_obj");
    if (u == null) {
      u = User.loadById(this.getOwner());
      this.set("user_obj", u);
    }
    return u;
  }

  // ------------

  public static long create(V v) {
    /**
     * generate a unique id in distribute system
     */
    long id = UID.next("circling.id");
    try {
      while (exists(id)) {
        id = UID.next("circling.id");
      }
      Helper.insert(v.set(X.ID, id), Circling.class);
      return id;
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return -1;
  }

  public static boolean exists(long id) {
    try {
      return Helper.exists(id, Circling.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static int update(long id, V v) {
    return Helper.update(id, v, Circling.class);
  }

  public static Beans<Circling> load(W q, int s, int n) {
    return Helper.load(q, s, n, Circling.class);
  }

  public static Circling load(long id) {
    return Helper.load(id, Circling.class);
  }

  public static void delete(long id) {
    Helper.delete(id, Circling.class);
  }

  public static void repair(final long id) {
    new Task() {

      @Override
      public void onExecute() {

        /**
         * count the users
         */
        long total = Follower.count(W.create("cid", id));
        V v = V.create("followers", total);
        long count = Follower.count(W.create("cid", id).and("state", "pending"));
        v.set("followers_pending", count);
        count = Follower.count(W.create("cid", id).and("state", "accepted"));
        v.set("followers_accepted", count);
        count = Follower.count(W.create("cid", id).and("state", "rejected"));
        v.set("followers_rejected", count);
        count = Topic.count(W.create("cid", id).and("parent", 0));
        v.set("topics", count);
        Circling.update(id, v);

        /**
         * repair all topics in the circling
         */
        int s = 0;
        W q = W.create("cid", id).sort(X.ID, 1);

        Beans<Topic> bs = Topic.load(q, s, 10);
        while (bs != null && bs.getList() != null && bs.getList().size() > 0) {
          for (Topic t : bs.getList()) {
            t.repair();
          }
          s += bs.getList().size();
          bs = Topic.load(q, s, 10);
        }
      }

    }.schedule(10);
  }

  public Follower getFollower(User u) {
    if (u == null) {
      return null;
    }
    return Follower.load(W.create("cid", this.getId()).and("uid", u.getId()));
  }

  public boolean isForbidden(User u) {
    return Log.exists(W.create("data", "forbidden").and("cid", this.getId()).and("uid", u.getId()).and("expired",
        System.currentTimeMillis(), W.OP_GT));
  }

  public static Beans<Circling> load(long uid, W q, int s, int n) {

    BasicDBList list = new BasicDBList();
    list.add(new BasicDBObject("owner", uid));

    int s1 = 0;
    W w = W.create();
    W q1 = W.create("uid", uid).and("data", "follower").sort(X.ID, 1);
    Beans<Log> bs1 = Log.load(q1, s1, 100);
    while (bs1 != null && bs1.getList() != null && bs1.getList().size() > 0) {
      for (Log l : bs1.getList()) {
        w.or(X.ID, l.getString("cid"));
      }
      s1 += bs1.getList().size();
      bs1 = Log.load(q1, s1, 100);
    }

    return Helper.load(q.and(w), s, n, Circling.class);

  }

  public static Circling load(W q) {
    // TODO Auto-generated method stub
    return Helper.load(q, Circling.class);
  }

  public boolean isPrivate() {
    return X.isSame("private", this.getAccess());
  }

}
