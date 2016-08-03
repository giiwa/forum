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

/**
 * Demo bean
 * 
 * @author joe
 * 
 */
@Table(name = "gi_expose")
public class Expose extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public String getId() {
    return this.getString(X.ID);
  }

  public User getReporter_obj() {
    User u = (User) this.get("reporter_obj");
    if (u == null) {
      u = User.loadById(this.getLong("reporter"));
      this.set("reporter_obj", u);
    }
    return u;
  }

  public Circling getCircling_obj() {
    Circling u = (Circling) this.get("circling_obj");
    if (u == null) {
      u = Circling.load(this.getLong("cid"));
      this.set("circling_obj", u);
    }
    return u;
  }

  public Topic getTopic_obj() {
    Topic u = (Topic) this.get("topic_obj");
    if (u == null) {
      u = Topic.load(this.getLong("tid"));
      this.set("topic_obj", u);
    }
    return u;
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

  public static String create(V v) {
    /**
     * generate a unique id in distribute system
     */
    String id = UID.id(v.toString());
    try {
      if (!exists(id)) {
        Helper.insert(v.set(X.ID, id), Expose.class);
        return id;
      }
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return null;
  }

  public static boolean exists(String id) {
    try {
      return Helper.exists(id, Expose.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static Beans<Expose> load(W q, int s, int n) {
    return Helper.load(q, s, n, Expose.class);
  }

  public static Expose load(String id) {
    return Helper.load(id, Expose.class);
  }

  public static void delete(String id) {
    Helper.delete(id, Expose.class);
  }

}
