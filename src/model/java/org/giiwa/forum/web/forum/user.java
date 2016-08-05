package org.giiwa.forum.web.forum;

import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.Helper;
import org.giiwa.core.bean.Helper.V;
import org.giiwa.core.bean.Helper.W;
import org.giiwa.forum.bean.MyAccount;
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

  @Path(path = "account", login = true)
  public void accout() {
    int s = this.getInt("s");
    int n = this.getInt("n", 20, "number.per.page");

    W w = W.create("uid", login.getId());
    Beans<MyAccount> bs = Helper.load(w, s, n, MyAccount.class);
    bs.setTotal((int) Helper.count(w, MyAccount.class));

    this.set(bs, s, n);
    this.show("/forum/myaccount.index.html");
  }
}
