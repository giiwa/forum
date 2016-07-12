package org.giiwa.forum.web.forum;

import org.giiwa.core.bean.Bean.V;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.X;
import org.giiwa.forum.bean.Topic;
import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

import com.mongodb.BasicDBObject;

/**
 * web api: /demo
 * 
 * @author joe
 * 
 */
public class user extends Model {

  @Path(login = true)
  public void onGet() {
    long id = this.getLong("id");

    User u = User.loadById(id);
    this.set("u", u);
    this.set("id", id);

    this.query.path("/forum/user");
    this.show("/forum/user.home.html");
  }

  @Path(path = "create", login = true)
  public void create() {
    String cid = this.getString("cid");
    this.set("cid", cid);
    if (method.isPost()) {
      V v = V.create("cid", cid);
      v.set("title", this.getString("title"));
      v.set("content", this.getHtml("content"));
      v.set("owner", login.getId());
      v.set("parent", "root");
      Topic.create(v);
      this.set(X.MESSAGE, lang.get("create.success"));
      onGet();
      return;
    }
    this.show("/forum/topic.create.html");
  }

  @Path(path = "reply", login = true)
  public void reply() {
    String id = this.getString("id");
    this.set("id", id);
    if (method.isPost()) {

      Topic t = Topic.load(id);
      Topic last = t.getLast();

      String content = this.getHtml("content");
      if (last == null || last.getOwner() != login.getId() || !X.isSame(content, last.getContent())) {
        V v = V.create("parent", id);
        v.set("content", this.getHtml("content"));
        v.set("owner", login.getId());
        Topic.create(v);
        Topic.update(id, V.create("replies", t.getReplies() + 1));
      }

      this.set(X.MESSAGE, lang.get("create.success"));
      detail();
      return;
    }
    Topic t = Topic.load(id);
    this.set("cid", t.getCid());
    this.show("/forum/topic.reply.html");
  }

  @Path(path = "detail", login = true)
  public void detail() {
    String id = this.getString("id");
    Topic t = Topic.load(id);
    this.set("t", t);
    this.set("id", t.getId());
    this.set("cid", t.getCid());

    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");
    Beans<Topic> bs = Topic.load(new BasicDBObject("parent", t.getId()), new BasicDBObject("created", 1), s, n);

    this.set(bs, s, n);

    this.show("/forum/topic.detail.html");
  }

}
