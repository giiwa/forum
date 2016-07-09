package org.giiwa.forum.web;

import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

/**
 * web api: /demo
 * 
 * @author joe
 * 
 */
public class user extends Model {

  @Path(login = true)
  public void home() {
    this.show("/user/user.home.html");
  }

}
