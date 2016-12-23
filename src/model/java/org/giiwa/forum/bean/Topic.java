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

/**
 * Demo bean
 * 
 * @author joe
 * 
 */
@Table(name = "gi_topic")
public class Topic extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public long getId() {
    return this.getLong(X.ID);
  }

  public String getTitle() {
    return this.getString("title");
  }

  public int getUp() {
    return this.getInt("up");
  }

  public int getDown() {
    return this.getInt("down");
  }

  public int getTop() {
    return this.getInt("top");
  }

  public int getFloor() {
    return this.getInt("floor");
  }

  public String getContent() {
    return this.getString("content");
  }

  private Topic refer_obj;

  public Topic getRefer() {
    if (refer_obj == null && !X.isEmpty(this.get("refer"))) {
      refer_obj = Topic.load(this.getLong("refer"));
    }
    return refer_obj;
  }

  public int getReads() {
    return this.getInt("reads");
  }

  public int getReplies() {
    return this.getInt("replies");
  }

  public long replysInDays(int days) {
    return Helper.count(W.create("cid", this.getCid()).and("parent", this.getId()).and("created",
        System.currentTimeMillis() - days * X.ADAY, W.OP.gt), Topic.class);
  }

  public long getOwner() {
    return this.getLong("owner");
  }

  private User owner_obj;

  public User getOwner_obj() {
    if (owner_obj == null) {
      owner_obj = User.loadById(this.getOwner());
    }
    return owner_obj;
  }

  // ------------

  public static long create(V v) {
    /**
     * generate a unique id in distribute system
     */
    long id = UID.next("topic.id");
    try {
      while (exists(id)) {
        id = UID.next("topic.id");
      }
      Helper.insert(v.set(X.ID, id), Topic.class);
      return id;
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return -1;
  }

  public static boolean exists(long id) {
    try {
      return Helper.exists(id, Topic.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static int update(long id, V v) {
    return Helper.update(id, v, Topic.class);
  }

  public static Beans<Topic> load(W q, int s, int n) {
    return Helper.load(q, s, n, Topic.class);
  }

  public static Topic load(long id) {
    return Helper.load(id, Topic.class);
  }

  public static void delete(long id) {
    Helper.delete(id, Topic.class);
  }

  public long getCid() {
    // TODO Auto-generated method stub
    return this.getLong("cid");
  }

  public Topic getLast() {
    return Helper.load(W.create("parent", this.getId()).and("deleted", 1, W.OP.neq).sort("created", -1), Topic.class);
  }

  public void repair() {
    long c = Helper.count(W.create("parent", this.getId()), Topic.class);
    update(this.getId(), V.create("replies", (int) c).ignore("updated"));

    new Task() {

      @Override
      public void onExecute() {
        long id = getId();
        int s = 0;
        W q = W.create("parent", id).sort("created", 1);
        Beans<Topic> bs = Topic.load(q, s, 100);
        int f = 1;
        while (bs != null && bs.getList() != null && bs.getList().size() > 0) {

          for (Topic t : bs.getList()) {
            if (t.getFloor() != f) {
              t.update(V.create("floor", f).ignore("updated"));
            }
            f++;
          }
          s += bs.getList().size();
          bs = Topic.load(q, s, 100);

        }
      }

    }.schedule(10);
  }

  public void update(V v) {
    // TODO Auto-generated method stub
    update(this.getId(), v);
  }

  private Circling circling_obj;

  public Circling getCircling() {
    if (circling_obj == null) {
      circling_obj = Circling.load(this.getCid());
    }
    return circling_obj;
  }

  public int getDeleted() {
    return this.getInt("deleted");
  }

  public static long count(W q) {
    return Helper.count(q, Topic.class);
  }

  private Topic parent_obj;

  public Topic getParent_obj() {
    if (parent_obj == null) {
      long parent = this.getLong("parent");
      if (!"root".equals(parent)) {
        parent_obj = Topic.load(parent);
      }
    }
    return parent_obj;
  }

  public static Topic load(W q) {
    return Helper.load(q, Topic.class);
  }

  public static Beans<Topic> load(long uid, int s, int n) {
    // load circling first
    W q = W.create("uid", uid).and(W.create("state", "owner").or("state", "accepted")).sort("cid", 1);
    int s1 = 0;
    W w1 = W.create();
    Beans<Follower> bs = Follower.load(q, s1, 100);
    while (bs != null && bs.getList() != null && bs.getList().size() > 0) {
      for (Follower f : bs.getList()) {
        w1.or("cid", f.getCid());
      }
      s1 += bs.getList().size();
      bs = Follower.load(q, s1, 100);
    }

    q = W.create();
    q.and(w1).and("parent", 0).and("deleted", 1, W.OP.neq).sort("updated", -1);
    return Topic.load(q, s, n);
  }
}
