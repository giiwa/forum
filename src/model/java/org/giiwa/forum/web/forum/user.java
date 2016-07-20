package org.giiwa.forum.web.forum;

import org.giiwa.core.bean.Bean.V;
import org.giiwa.framework.bean.User;
import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

public class user extends Model {

  @Path(path = "edit", login = true)
  public void edit() {
    if (method.isPost()) {

      V v = V.create();
      v.set("nickname", this.getString("nickname"));
      v.set("email", this.getString("email"));
      v.set("phone", this.getString("phone"));
      v.set("photo", this.getString("photo"));
      
      User.update(login.getId(), v);
      
      this.redirect("/forum/circling");
      return;
    }
    this.set(login.getJSON());
    this.show("/forum/user.edit.html");
  }
}
