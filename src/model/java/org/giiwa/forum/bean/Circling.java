package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.DBMapping;
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
@DBMapping(collection = "gi_circling")
public class Circling extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public long getId() {
    return this.getLong(X._ID);
  }

  public String getName() {
    return this.getString("name");
  }

  public String getMemo() {
    return this.getString("memo");
  }

  public String getState() {
    return this.getString("state");
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
      Bean.insert(v.set(X._ID, id), Circling.class);
      return id;
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return -1;
  }

  public static boolean exists(long id) {
    try {
      return Bean.exists(new BasicDBObject(X._ID, id), Circling.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static int update(long id, V v) {
    return Bean.updateCollection(id, v, Circling.class);
  }

  public static Beans<Circling> load(BasicDBObject q, BasicDBObject order, int s, int n) {
    return Bean.load(q, order, s, n, Circling.class);
  }

  public static Circling load(long id) {
    return Bean.load(new BasicDBObject(X._ID, id), Circling.class);
  }

  public static void delete(long id) {
    Bean.delete(new BasicDBObject(X._ID, id), Circling.class);
  }

  public static void repair(final long id) {
    new Task() {

      @Override
      public void onExecute() {

        /**
         * count the users
         */
        long total = Follower.count(new BasicDBObject("cid", id));
        V v = V.create("followers", total);
        long count = Follower.count(new BasicDBObject("cid", id).append("state", "pending"));
        v.set("followers_pending", count);
        count = Follower.count(new BasicDBObject("cid", id).append("state", "accepted"));
        v.set("followers_accepted", count);
        count = Follower.count(new BasicDBObject("cid", id).append("state", "rejected"));
        v.set("followers_rejected", count);
        count = Topic.count(new BasicDBObject("cid", id).append("parent", 0));
        v.set("topics", count);
        Circling.update(id, v);

        /**
         * repair all topics in the circling
         */
        int s = 0;
        BasicDBObject q = new BasicDBObject("cid", id);
        BasicDBObject order = new BasicDBObject(X._ID, 1);

        Beans<Topic> bs = Topic.load(q, order, s, 10);
        while (bs != null && bs.getList() != null && bs.getList().size() > 0) {
          for (Topic t : bs.getList()) {
            t.repair();
          }
          s += bs.getList().size();
          bs = Topic.load(q, order, s, 10);
        }
      }

    }.schedule(10);
  }

  public Follower getFollower(User u) {
    return Follower.load(new BasicDBObject("cid", this.getId()).append("uid", u.getId()));
  }

  public boolean isForbidden(User u) {
    return Log.exists(new BasicDBObject("data", "forbidden").append("cid", this.getId()).append("uid", u.getId())
        .append("expired", new BasicDBObject("$gt", System.currentTimeMillis())));
  }

  public static Beans<Circling> load(long uid, BasicDBObject order, int s, int n) {

    BasicDBList list = new BasicDBList();
    list.add(new BasicDBObject("owner", uid));

    int s1 = 0;
    BasicDBObject q = new BasicDBObject("uid", uid).append("data", "follower");
    BasicDBObject order1 = new BasicDBObject(X._ID, 1);
    Beans<Log> bs1 = Log.load(q, order1, s1, 100);
    while (bs1 != null && bs1.getList() != null && bs1.getList().size() > 0) {
      for (Log l : bs1.getList()) {
        list.add(new BasicDBObject(X._ID, l.getString("cid")));
      }
      s1 += bs1.getList().size();
      bs1 = Log.load(q, order1, s1, 100);
    }

    return Bean.load(new BasicDBObject("$or", list), order, s, n, Circling.class);

  }

  public static Circling load(BasicDBObject q, BasicDBObject order) {
    // TODO Auto-generated method stub
    return Bean.load(q, order, Circling.class);
  }

}
