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

  private User reporter_obj;

  public User getReporter_obj() {
    if (reporter_obj == null) {
      reporter_obj = User.loadById(this.getLong("reporter"));
    }
    return reporter_obj;
  }

  private Circling circling_obj;

  public Circling getCircling_obj() {
    if (circling_obj == null) {
      circling_obj = Circling.load(this.getLong("cid"));
    }
    return circling_obj;
  }

  private Topic topic_obj;

  public Topic getTopic_obj() {
    if (topic_obj == null) {
      topic_obj = Topic.load(this.getLong("tid"));
    }
    return topic_obj;
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
