package org.giiwa.forum.web.forum;

import org.giiwa.core.bean.Bean.V;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.X;
import org.giiwa.forum.bean.Circling;
import org.giiwa.forum.bean.Log;
import org.giiwa.forum.bean.Topic;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

import com.mongodb.BasicDBObject;

/**
 * web api: /demo
 * 
 * @author joe
 * 
 */
public class topic extends Model {

  @Path(login = true)
  public void onGet() {
    String cid = this.getString("cid");
    this.set("cid", cid);
    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");
    Beans<Topic> bs = Topic.load(new BasicDBObject("cid", cid).append("parent", "root"),
        new BasicDBObject("updated", -1), s, n);
    this.set(bs, s, n);
    this.query.path("/forum/topic");
    this.show("/forum/topic.index.html");
  }

  @Path(path = "create", login = true)
  public void create() {
    String cid = this.getString("cid");
    this.set("cid", cid);
    if (method.isPost()) {
      V v = V.create("cid", cid);
      v.set("title", this.getString("title"));

      /**
       * remove the "FFF" background, that may blink eyes
       */
      String content = this.getHtml("content").replaceAll("background-color:#FFFFFF;", "");
      v.set("content", content);
      v.set("owner", login.getId());
      v.set("parent", "root");
      Topic.create(v);
      this.set(X.MESSAGE, lang.get("create.success"));
      onGet();
      return;
    }
    this.show("/forum/topic.create.html");
  }

  @Path(path = "update", login = true)
  public void update() {
    String cid = this.getString("cid");
    Circling c = Circling.load(cid);
    String id = this.getString("id");
    if (c.getOwner() == login.getId()) {
      V v = V.create();
      if (this.getString("deleted") != null) {
        v.set("deleted", this.getInt("deleted"));
      }

      Topic.update(id, v);
    }

    Topic t = Topic.load(id);
    this.set("f", t);
    this.set("c", c);
    this.show("/forum/topic.reply.content.html");
  }

  @Path(path = "reply", login = true)
  public void reply() {
    String id = this.getString("id");
    String refer = this.getString("refer");

    Topic t = Topic.load(id);
    Topic last = t.getLast();

    /**
     * remove the "FFF" background, that may blink eyes
     */
    String content = this.getHtml("content").replaceAll("background-color:#FFFFFF;", "");
    if (last == null || last.getOwner() != login.getId() || !X.isSame(content, last.getContent())) {
      V v = V.create("parent", id);
      v.set("content", content);
      v.set("owner", login.getId());
      if (!X.isEmpty(refer)) {
        v.set("refer", refer);
      }
      Topic.create(v);
      Topic.update(id, V.create("replies", t.getReplies() + 1));
    }

  }

  @Path(path = "edit", login = true)
  public void edit() {
    String id = this.getString("id");
    Topic t = Topic.load(id);
    if (method.isPost()) {
      V v = V.create();
      v.set("title", this.getString("title"));
      String content = this.getHtml("content").replaceAll("background-color:#FFFFFF;", "");
      v.set("content", content);
      Topic.update(id, v);
      this.set(X.MESSAGE, lang.get("save.success"));
      detail();
      return;
    }
    this.set("t", t);
    this.set("c", t.getCircling());
    this.set("id", t.getId());
    this.set("cid", t.getCid());

    this.show("/forum/topic.edit.html");
  }

  @Path(path = "detail", login = true)
  public void detail() {
    String id = this.getString("id");
    Topic t = Topic.load(id);
    this.set("t", t);
    this.set("c", t.getCircling());
    this.set("id", t.getId());
    this.set("cid", t.getCid());

    if (Log.create(V.create("topic_id", id).set("sid", sid()))) {
      t.update(V.create("reads", t.getReads() + 1));
    }

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");
    Beans<Topic> bs = Topic.load(new BasicDBObject("parent", t.getId()), new BasicDBObject("created", 1), s, n);

    this.set(bs, s, n);
    this.query.path("/forum/topic/detail");

    /**
     * get recommends
     */
    Beans<Topic> bs1 = Topic.load(new BasicDBObject("cid", t.getCid()), new BasicDBObject("updated", -1), 0, 20);
    this.set("recommends", bs1 == null ? null : bs1.getList());
    this.show("/forum/topic.detail.html");
  }

  public String toHtml(Topic r) {
    StringBuilder sb = new StringBuilder("<div class='refer'>");
    Topic r1 = r.getRefer();
    if (r1 != null) {
      sb.append(toHtml(r1));
    }
    sb.append("<div>").append(r.getOwner_obj().getNickname()).append(":</div>");
    sb.append("<div class='content'>");
    if (r.getDeleted() == 1) {
      sb.append(lang.get("topic.was.deleted"));
    } else {
      sb.append(r.getContent());
    }

    sb.append("</div>");
    sb.append("</div>");
    return sb.toString();
  }
}
