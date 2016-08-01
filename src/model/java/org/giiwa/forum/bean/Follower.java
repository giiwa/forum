package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.Table;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;
import org.giiwa.framework.bean.User;

@Table(name = "gi_follower")
public class Follower extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public String getId() {
    return this.getString(X._ID);
  }

  public long getUid() {
    return this.getLong("uid");
  }

  public User getUser_obj() {
    User u = (User) this.get("user_obj");
    if (u == null) {
      u = User.loadById(this.getUid());
      this.set("user_obj", u);
    }
    return u;
  }

  public long getCid() {
    return this.getLong("cid");
  }

  public Circling getCircling_obj() {
    Circling u = (Circling) this.get("circling_obj");
    if (u == null) {
      u = Circling.load(this.getCid());
      this.set("circling_obj", u);
    }
    return u;
  }

  public String getState() {
    return this.getString("state");
  }

  // -----------------
  public static boolean exists(W q) {
    try {
      return Helper.exists(q, Follower.class);
    } catch (Exception e) {
      log.error(q.toString(), e);
    }
    return false;
  }

  public static boolean create(long uid, long cid, V v) {
    String id = UID.id(uid, cid);
    try {
      if (!Helper.exists( id, Follower.class)) {
        return Helper.insert(v.set(X._ID, id).set("uid", uid).set("cid", cid), Follower.class) > 0;
      }
    } catch (Exception e) {
      log.error(v.toString(), e);
    }
    return false;
  }

  public static Beans<Follower> load(W q,  int s, int n) {
    return Helper.load(q, s, n, Follower.class);
  }

  public static Follower load(W q) {
    return Helper.load(q, Follower.class);
  }

  public static long count(W q) {
    return Helper.count(q, Follower.class);
  }

  public static void delete(W q) {
    // TODO Auto-generated method stub
    Helper.delete(q, Follower.class);
  }

  public static int update(W q, V v) {
    // TODO Auto-generated method stub
    return Helper.update(q, v, Follower.class);
  }

  public boolean getPost() {
    // TODO Auto-generated method stub
    String state = this.getState();
    return "owner".equals(state) || "accepted".equals(state);
  }
}
