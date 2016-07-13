package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.DBMapping;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;

import com.mongodb.BasicDBObject;

@DBMapping(collection = "gi_log")
public class Log extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static boolean exists(BasicDBObject q) {
    try {
      return Bean.exists(q, Log.class);
    } catch (Exception e) {
      log.error(q.toString(), e);
    }
    return false;
  }

  public static boolean create(V v) {
    String id = UID.id(v.toString());
    try {
      if (!Bean.exists(new BasicDBObject(X._ID, id), Log.class)) {
        return Bean.insertCollection(v.set(X._ID, id), Log.class) > 0;
      }
    } catch (Exception e) {
      log.error(v.toString(), e);
    }
    return false;
  }

  public static Beans<Log> load(BasicDBObject q, BasicDBObject order, int s, int n) {
    return Bean.load(q, order, s, n, Log.class);
  }

}
