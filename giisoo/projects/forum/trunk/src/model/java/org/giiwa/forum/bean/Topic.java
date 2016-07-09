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
@DBMapping(collection = "gi_topic")
public class Topic extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static enum Type {
    ticket, reply
  };

  public String getId() {
    return this.getString(X._ID);
  }

  public String getTitle() {
    return this.getString("title");
  }

  public String getContent() {
    return this.getString("content");
  }

  public String getReplyto() {
    return this.getString("replyto");
  }

  public Topic getReplyto_obj() {
    Topic t = (Topic) this.get("replyto_obj");
    if (t == null) {
      String replyto = this.getReplyto();
      t = Topic.load(replyto);
      this.set("replyto_obj", t);
    }
    return t;
  }

  public int getReaded() {
    return this.getInt("readed");
  }

  public int getReplied() {
    return this.getInt("replied");
  }

  public String getCategoryid() {
    return this.getString("categoryid");
  }

  public String[] getTogroup() {
    return (String[]) this.get("togroup");
  }

  // ------------

  public static String create(V v) {
    /**
     * generate a unique id in distribute system
     */
    String id = "t" + UID.next("topic.id");
    try {
      while (exists(id)) {
        id = "t" + UID.next("topic.id");
      }
      Bean.insert(v.set(X._ID, id), Topic.class);
      return id;
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return null;
  }

  public static boolean exists(String id) {
    try {
      return Bean.exists(new BasicDBObject(X._ID, id), Topic.class);
    } catch (Exception e1) {
      log.error(e1.getMessage(), e1);
    }
    return false;
  }

  public static int update(String id, V v) {
    return Bean.updateCollection(id, v, Topic.class);
  }

  public static Beans<Topic> load(BasicDBObject q, int s, int n) {
    return Bean.load(q, new BasicDBObject(X._ID, 1), s, n, Topic.class);
  }

  public static Topic load(String id) {
    return Bean.load(new BasicDBObject(X._ID, id), Topic.class);
  }

  public static void delete(String id) {
    Bean.delete(new BasicDBObject(X._ID, id), Topic.class);
  }

}
