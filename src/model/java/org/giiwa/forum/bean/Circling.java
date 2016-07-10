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
@DBMapping(collection = "gi_circling")
public class Circling extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public String getId() {
    return this.getString(X._ID);
  }

  public String getName() {
    return this.getString("name");
  }

  public String getMemo() {
    return this.getString("memo");
  }
  
  public long getOwner() {
    return this.getLong("owner");
  }

  // ------------

  public static String create(V v) {
    /**
     * generate a unique id in distribute system
     */
    String id = "c" + UID.next("circling.id");
    try {
      while (exists(id)) {
        id = "c" + UID.next("circling.id");
      }
      Bean.insert(v.set(X._ID, id), Circling.class);
      return id;
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return null;
  }

  public static boolean exists(String id) {
    try {
      return Bean.exists(new BasicDBObject(X._ID, id), Circling.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static int update(String id, V v) {
    return Bean.updateCollection(id, v, Circling.class);
  }

  public static Beans<Circling> load(BasicDBObject q, BasicDBObject order, int s, int n) {
    return Bean.load(q, order, s, n, Circling.class);
  }

  public static Circling load(String id) {
    return Bean.load(new BasicDBObject(X._ID, id), Circling.class);
  }

  public static void delete(String id) {
    Bean.delete(new BasicDBObject(X._ID, id), Circling.class);
  }

}
