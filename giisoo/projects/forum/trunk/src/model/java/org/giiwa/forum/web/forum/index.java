package org.giiwa.forum.web.forum;

import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

/**
 * web api: /demo
 * 
 * @author joe
 * 
 */
public class index extends Model {

  @Path(login = true)
  public void home() {
    long id = this.getLong("id");
    if (id > 0) {
      User u = User.loadById(id);
      this.set("user", u);
    } else {
      this.set("user", login);
    }

    this.show("/forum/user.home.html");
  }

}
