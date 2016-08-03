package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.core.bean.Table;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;

@Table(name = "gi_log")
public class Log extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static boolean exists(W q) {
    try {
      return Helper.exists(q, Log.class);
    } catch (Exception e) {
      log.error(q.toString(), e);
    }
    return false;
  }

  public static boolean create(V v) {
    String id = UID.id(v.toString());
    try {
      if (!Helper.exists(id, Log.class)) {
        return Helper.insert(v.set(X.ID, id), Log.class) > 0;
      }
    } catch (Exception e) {
      log.error(v.toString(), e);
    }
    return false;
  }

  public static Beans<Log> load(W q, int s, int n) {
    return Helper.load(q, s, n, Log.class);
  }

}
