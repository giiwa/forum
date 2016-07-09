package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.DBMapping;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;

import com.mongodb.BasicDBObject;

/**
 * Demo bean
 * 
 * @author joe
 * 
 */
@DBMapping(collection = "gi_follower")
public class Follower extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public String getId() {
    return this.getString(X._ID);
  }

  public String getCid() {
    return this.getString("cid");
  }

  public long getOwner() {
    return this.getLong("owner");
  }

  // ------------

  public static String create(V v) {
    /**
     * generate a unique id in distribute system
     */
    String id = "f" + UID.next("follower.id");
    try {
      while (exists(id)) {
        id = "f" + UID.next("follower.id");
      }
      Bean.insert(v.set(X._ID, id), Follower.class);
      return id;
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return null;
  }

  public static boolean exists(String id) {
    try {
      return Bean.exists(new BasicDBObject(X._ID, id), Follower.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static int update(String id, V v) {
    return Bean.updateCollection(id, v, Follower.class);
  }

  public static Beans<Follower> load(BasicDBObject q, BasicDBObject order, int s, int n) {
    return Bean.load(q, order, s, n, Follower.class);
  }

  public static Follower load(String id) {
    return Bean.load(new BasicDBObject(X._ID, id), Follower.class);
  }

  public static void delete(String id) {
    Bean.delete(new BasicDBObject(X._ID, id), Follower.class);
  }

}
