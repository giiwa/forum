package org.giiwa.forum.web.forum;

import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

/**
 * web api: /demo
 * 
 * @author joe
 * 
 */
public class topic extends Model {

  @Path(login = true)
  public void onGet() {

    this.show("/forum/topic.index.html");
  }

  @Path(path = "create", login = true)
  public void create() {
    this.show("/forum/topic.create.html");
  }

  @Path(path = "detail", login = true)
  public void detail() {
    this.show("/forum/topic.detail.html");
  }

}
