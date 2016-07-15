package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.DBMapping;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;
import org.giiwa.core.task.Task;
import org.giiwa.framework.bean.User;

import com.mongodb.BasicDBObject;

/**
 * Demo bean
 * 
 * @author joe
 * 
 */
@DBMapping(collection = "gi_expose")
public class Expose extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public String getId() {
    return this.getString(X._ID);
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
      u = Circling.load(this.getString("cid"));
      this.set("circling_obj", u);
    }
    return u;
  }

  public Topic getTopic_obj() {
    Topic u = (Topic) this.get("topic_obj");
    if (u == null) {
      u = Topic.load(this.getString("tid"));
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
        Bean.insert(v.set(X._ID, id), Expose.class);
        return id;
      }
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return null;
  }

  public static boolean exists(String id) {
    try {
      return Bean.exists(new BasicDBObject(X._ID, id), Expose.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static Beans<Expose> load(BasicDBObject q, BasicDBObject order, int s, int n) {
    return Bean.load(q, order, s, n, Expose.class);
  }

  public static Expose load(String id) {
    return Bean.load(new BasicDBObject(X._ID, id), Expose.class);
  }

  public static void delete(String id) {
    Bean.delete(new BasicDBObject(X._ID, id), Expose.class);
  }

}
