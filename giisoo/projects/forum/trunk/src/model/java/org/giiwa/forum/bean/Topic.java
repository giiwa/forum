package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.DBMapping;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;
import org.giiwa.framework.bean.User;

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

  public String getId() {
    return this.getString(X._ID);
  }

  public String getTitle() {
    return this.getString("title");
  }

  public String getContent() {
    return this.getString("content");
  }

  public Topic getRefer() {
    Topic t = (Topic) this.get("refer_obj");
    if (t == null && !X.isEmpty(this.get("refer"))) {
      t = Topic.load(this.getString("refer"));
      this.set("refer_obj", t);
    }
    return t;
  }

  public int getReads() {
    return this.getInt("reads");
  }

  public int getReplies() {
    return this.getInt("replies");
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

  public static Beans<Topic> load(BasicDBObject q, BasicDBObject order, int s, int n) {
    return Bean.load(q, order, s, n, Topic.class);
  }

  public static Topic load(String id) {
    return Bean.load(new BasicDBObject(X._ID, id), Topic.class);
  }

  public static void delete(String id) {
    Bean.delete(new BasicDBObject(X._ID, id), Topic.class);
  }

  public String getCid() {
    // TODO Auto-generated method stub
    return this.getString("cid");
  }

  public Topic getLast() {
    return Bean.load(new BasicDBObject("parent", this.getId()), new BasicDBObject("created", -1), Topic.class);
  }

  public void repair() {
    long c = Bean.count(new BasicDBObject("parent", this.getId()), Topic.class);
    update(this.getId(), V.create("replies", (int) c));
  }

  public void update(V v) {
    // TODO Auto-generated method stub
    update(this.getId(), v);
  }

  public Circling getCircling() {
    Circling c = (Circling) this.get("circling_obj");
    if (c == null) {
      c = Circling.load(this.getCid());
      this.set("circling_obj", c);
    }
    return c;
  }

  public int getDeleted() {
    return this.getInt("deleted");
  }

}
